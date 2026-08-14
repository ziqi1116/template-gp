package com.gp.business.gen.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.gp.business.gen.domain.GenConfig;
import com.gp.business.gen.mapper.GenMapper;
import com.gp.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/**
 * 简版代码生成器
 *
 * 原理：读取 information_schema 表字段元数据 → 按业务模块规范渲染模板
 * （domain/mapper/service/controller + 前端 api/页面 + 菜单 SQL），打包 ZIP。
 * 模板文件位于 resources/templates/gen/，用 ${token} 占位。
 */
@Service
public class GenService {

    /** 与 BaseEntity 重复的基础字段：生成实体时跳过 */
    private static final Set<String> BASE_COLUMNS = new HashSet<>(Arrays.asList(
            "id", "create_by", "create_time", "update_by", "update_time", "del_flag", "remark"));

    /** 默认作为搜索条件的字段个数 */
    private static final int SEARCH_FIELD_LIMIT = 3;

    /** MySQL 类型 → Java 类型 */
    private static final Map<String, String> TYPE_MAP = new LinkedHashMap<>();

    static {
        TYPE_MAP.put("varchar", "String");
        TYPE_MAP.put("char", "String");
        TYPE_MAP.put("text", "String");
        TYPE_MAP.put("mediumtext", "String");
        TYPE_MAP.put("longtext", "String");
        TYPE_MAP.put("bigint", "Long");
        TYPE_MAP.put("int", "Integer");
        TYPE_MAP.put("integer", "Integer");
        TYPE_MAP.put("smallint", "Integer");
        TYPE_MAP.put("mediumint", "Integer");
        TYPE_MAP.put("tinyint", "Integer");
        TYPE_MAP.put("datetime", "Date");
        TYPE_MAP.put("timestamp", "Date");
        TYPE_MAP.put("date", "Date");
        TYPE_MAP.put("decimal", "BigDecimal");
        TYPE_MAP.put("numeric", "BigDecimal");
        TYPE_MAP.put("double", "Double");
        TYPE_MAP.put("float", "Float");
        TYPE_MAP.put("bit", "Boolean");
    }

    @Autowired
    private GenMapper genMapper;

    /** 业务表列表 */
    public List<Map<String, Object>> listTables() {
        return genMapper.selectTables();
    }

    /** 校验配置并返回规范化后的配置 */
    private GenConfig checkConfig(GenConfig config) {
        if (StrUtil.isBlank(config.getTableName())) {
            throw new BusinessException("表名不能为空");
        }
        if (StrUtil.isBlank(config.getModule()) || !config.getModule().matches("[a-z][a-z0-9]*")) {
            throw new BusinessException("模块名必须为小写字母开头（如 teacher）");
        }
        if (StrUtil.isBlank(config.getClassName()) || !config.getClassName().matches("[A-Z][A-Za-z0-9]*")) {
            throw new BusinessException("类名必须为大写字母开头的大驼峰（如 Teacher）");
        }
        if (StrUtil.isBlank(config.getFunctionName())) {
            throw new BusinessException("功能名不能为空");
        }
        if (config.getMenuId() == null || config.getMenuId() < 100 || config.getMenuId() > 9999) {
            throw new BusinessException("菜单ID需在 100~9999 之间");
        }
        // 表必须真实存在
        if (genMapper.selectColumns(config.getTableName()).isEmpty()) {
            throw new BusinessException("表 " + config.getTableName() + " 不存在或没有字段");
        }
        return config;
    }

    /** 预览：文件路径 → 文件内容（有序） */
    public Map<String, String> preview(GenConfig config) {
        return renderAll(checkConfig(config));
    }

    /** 下载：全部文件打包为 ZIP */
    public byte[] download(GenConfig config) {
        Map<String, String> files = renderAll(checkConfig(config));
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(bos)) {
            for (Map.Entry<String, String> entry : files.entrySet()) {
                zip.putNextEntry(new ZipEntry("gen-" + config.getModule() + "/" + entry.getKey()));
                zip.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
            }
            zip.finish();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("打包失败：" + e.getMessage());
        }
    }

    /* ==================== 渲染 ==================== */

    private Map<String, String> renderAll(GenConfig config) {
        List<Map<String, Object>> columns = genMapper.selectColumns(config.getTableName());
        List<Field> fields = parseFields(columns);

        String pkg = "src/main/java/com/gp/business/" + config.getModule();
        Map<String, String> files = new LinkedHashMap<>();
        files.put(pkg + "/controller/" + config.getClassName() + "Controller.java", render("controller.java.tpl", config, fields));
        files.put(pkg + "/domain/" + config.getClassName() + ".java", render("domain.java.tpl", config, fields));
        files.put(pkg + "/mapper/" + config.getClassName() + "Mapper.java", render("mapper.java.tpl", config, fields));
        files.put(pkg + "/service/" + config.getClassName() + "Service.java", render("service.java.tpl", config, fields));
        files.put("gp-ui/src/api/business/" + config.getModule() + ".js", render("api.js.tpl", config, fields));
        files.put("gp-ui/src/views/business/" + config.getModule() + "/index.vue", render("index.vue.tpl", config, fields));
        files.put("sql/" + config.getModule() + "_menu.sql", render("menu.sql.tpl", config, fields));
        return files;
    }

    /** 解析字段元数据为渲染用字段对象 */
    private List<Field> parseFields(List<Map<String, Object>> columns) {
        List<Field> fields = new ArrayList<>();
        for (Map<String, Object> column : columns) {
            String name = String.valueOf(column.get("name"));
            if (BASE_COLUMNS.contains(name)) {
                continue;
            }
            Field field = new Field();
            field.column = name;
            field.attr = toCamel(name);
            field.cap = StrUtil.upperFirst(field.attr);
            field.javaType = TYPE_MAP.getOrDefault(String.valueOf(column.get("dataType")), "String");
            field.comment = StrUtil.emptyIfNull(String.valueOf(column.get("comment")));
            field.dateType = String.valueOf(column.get("dataType"));
            fields.add(field);
        }
        if (fields.isEmpty()) {
            throw new BusinessException("该表除基础字段外没有业务字段，无需生成");
        }
        return fields;
    }

    /** 渲染单个模板 */
    private String render(String templateName, GenConfig config, List<Field> fields) {
        String tpl = loadTemplate(templateName);
        String tableComment = genMapper.selectTables().stream()
                .filter(t -> config.getTableName().equals(String.valueOf(t.get("name"))))
                .map(t -> String.valueOf(t.get("comment")))
                .findFirst().orElse("");

        // 公共令牌
        tpl = tpl
                .replace("${className}", config.getClassName())
                .replace("${ClassName}", config.getClassName())
                .replace("${varName}", StrUtil.lowerFirst(config.getClassName()))
                .replace("${module}", config.getModule())
                .replace("${functionName}", config.getFunctionName())
                .replace("${table}", config.getTableName())
                .replace("${tableComment}", tableComment)
                .replace("${menuId}", String.valueOf(config.getMenuId()))
                .replace("${date}", cn.hutool.core.date.DateUtil.format(new Date(), "yyyy-MM-dd"));

        // Java 侧动态块
        List<Field> searchFields = pickSearchFields(fields);
        tpl = tpl
                .replace("${importLines}", buildImportLines(fields))
                .replace("${fieldLines}", buildFieldLines(fields))
                .replace("${queryConditions}", buildQueryConditions(config, searchFields))
                .replace("${searchNote}", searchFields.isEmpty() ? "（默认未选搜索字段，可自行添加）"
                        : searchFields.stream().map(f -> f.commentOrName()).reduce((a, b) -> a + "、" + b).orElse(""));

        // Vue 侧动态块
        tpl = tpl
                .replace("${searchItems}", buildSearchItems(searchFields))
                .replace("${tableColumns}", buildTableColumns(fields))
                .replace("${formItems}", buildFormItems(fields))
                .replace("${queryFields}", buildQueryFields(searchFields))
                .replace("${resetFields}", buildResetFields(searchFields))
                .replace("${formReset}", buildFormReset(fields))
                .replace("${formRules}", buildFormRules(fields));
        return tpl;
    }

    private String loadTemplate(String name) {
        try (InputStream in = new ClassPathResource("templates/gen/" + name).getInputStream()) {
            return IoUtil.read(in, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BusinessException("模板读取失败：" + name);
        }
    }

    /* ==================== 各动态块构建 ==================== */

    /** 前几个字符串字段作为默认搜索条件 */
    private List<Field> pickSearchFields(List<Field> fields) {
        List<Field> result = new ArrayList<>();
        for (Field field : fields) {
            if ("String".equals(field.javaType) && result.size() < SEARCH_FIELD_LIMIT) {
                result.add(field);
            }
        }
        return result;
    }

    private String buildImportLines(List<Field> fields) {
        boolean hasDate = fields.stream().anyMatch(f -> "Date".equals(f.javaType));
        boolean hasDecimal = fields.stream().anyMatch(f -> "BigDecimal".equals(f.javaType));
        StringBuilder sb = new StringBuilder();
        if (hasDate) {
            sb.append("import java.util.Date;\n");
            sb.append("import com.fasterxml.jackson.annotation.JsonFormat;\n");
        }
        if (hasDecimal) {
            sb.append("import java.math.BigDecimal;\n");
        }
        return sb.toString();
    }

    private String buildFieldLines(List<Field> fields) {
        StringBuilder sb = new StringBuilder();
        for (Field field : fields) {
            if (!field.comment.isEmpty()) {
                sb.append("    /** ").append(field.comment).append(" */\n");
            }
            if (!field.comment.isEmpty()) {
                sb.append("    @Excel(name = \"").append(field.comment).append("\")\n");
            }
            if ("datetime".equals(field.dateType) || "timestamp".equals(field.dateType)) {
                sb.append("    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")\n");
            } else if ("date".equals(field.dateType)) {
                sb.append("    @JsonFormat(pattern = \"yyyy-MM-dd\")\n");
            }
            sb.append("    private ").append(field.javaType).append(" ").append(field.attr).append(";\n\n");
        }
        // 去掉末尾多余空行
        String result = sb.toString();
        return result.endsWith("\n\n") ? result.substring(0, result.length() - 1) : result;
    }

    private String buildQueryConditions(GenConfig config, List<Field> searchFields) {
        StringBuilder sb = new StringBuilder();
        for (Field field : searchFields) {
            sb.append("        wrapper.like(query.get").append(field.cap).append("() != null && !query.get")
                    .append(field.cap).append("().isEmpty(),\n")
                    .append("                ").append(config.getClassName()).append("::get").append(field.cap)
                    .append(", query.get").append(field.cap).append("());\n");
        }
        if (sb.length() == 0) {
            return "        // 暂无默认查询条件，可按需添加\n";
        }
        return sb.toString();
    }

    private String buildSearchItems(List<Field> searchFields) {
        StringBuilder sb = new StringBuilder();
        for (Field field : searchFields) {
            sb.append("        <el-form-item label=\"").append(field.commentOrName()).append("\">\n")
                    .append("          <el-input v-model=\"queryParams.").append(field.attr)
                    .append("\" placeholder=\"请输入").append(field.commentOrName())
                    .append("\" clearable style=\"width:180px\" @keyup.enter=\"handleQuery\" />\n")
                    .append("        </el-form-item>\n");
        }
        return sb.toString();
    }

    private String buildTableColumns(List<Field> fields) {
        StringBuilder sb = new StringBuilder();
        for (Field field : fields) {
            sb.append("        <el-table-column label=\"").append(field.commentOrName())
                    .append("\" prop=\"").append(field.attr).append("\" show-overflow-tooltip />\n");
        }
        return sb.toString();
    }

    private String buildFormItems(List<Field> fields) {
        StringBuilder sb = new StringBuilder();
        for (Field field : fields) {
            sb.append("          <el-col :span=\"12\">\n")
                    .append("            <el-form-item label=\"").append(field.commentOrName())
                    .append("\" prop=\"").append(field.attr).append("\">\n")
                    .append("              <el-input v-model=\"form.").append(field.attr)
                    .append("\" placeholder=\"请输入").append(field.commentOrName()).append("\" />\n")
                    .append("            </el-form-item>\n")
                    .append("          </el-col>\n");
        }
        return sb.toString();
    }

    private String buildQueryFields(List<Field> searchFields) {
        StringBuilder sb = new StringBuilder();
        for (Field field : searchFields) {
            sb.append(", ").append(field.attr).append(": ''");
        }
        return sb.toString();
    }

    private String buildResetFields(List<Field> searchFields) {
        StringBuilder sb = new StringBuilder();
        for (Field field : searchFields) {
            sb.append(" queryParams.value.").append(field.attr).append(" = '';");
        }
        return sb.toString();
    }

    private String buildFormReset(List<Field> fields) {
        StringBuilder sb = new StringBuilder();
        for (Field field : fields) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(field.attr).append(": ''");
        }
        return sb.toString();
    }

    private String buildFormRules(List<Field> fields) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Field field : fields) {
            if (count >= 2) {
                break;
            }
            if (sb.length() > 0) {
                sb.append(",\n");
            }
            sb.append("  ").append(field.attr).append(": [{ required: true, message: '")
                    .append(field.commentOrName()).append("不能为空', trigger: 'blur' }]");
            count++;
        }
        return sb.toString();
    }

    /** 下划线列名 → 小驼峰属性名 */
    private String toCamel(String column) {
        StringBuilder sb = new StringBuilder();
        boolean upper = false;
        for (char c : column.toCharArray()) {
            if (c == '_') {
                upper = true;
            } else {
                sb.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return sb.toString();
    }

    /** 渲染用字段 */
    private static class Field {
        String column;
        String attr;
        String cap;
        String javaType;
        String dateType;
        String comment;

        String commentOrName() {
            return comment.isEmpty() ? column : comment;
        }
    }

}
