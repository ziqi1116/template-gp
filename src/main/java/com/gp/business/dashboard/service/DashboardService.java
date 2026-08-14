package com.gp.business.dashboard.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gp.business.dashboard.mapper.DashboardMapper;
import com.gp.business.student.mapper.StudentMapper;
import com.gp.framework.domain.SysLogininfor;
import com.gp.framework.domain.SysOperLog;
import com.gp.framework.mapper.SysLogininforMapper;
import com.gp.framework.mapper.SysOperLogMapper;
import com.gp.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据大屏：把分散在各表的统计聚合成一次接口返回
 */
@Service
public class DashboardService {

    /** 操作类型编码 → 名称（与 BusinessTypeEnum 对应） */
    private static final Map<String, String> OPER_TYPE_NAMES = new LinkedHashMap<>();

    /** 性别编码 → 名称 */
    private static final Map<String, String> GENDER_NAMES = new HashMap<>();

    static {
        OPER_TYPE_NAMES.put("0", "其他");
        OPER_TYPE_NAMES.put("1", "新增");
        OPER_TYPE_NAMES.put("2", "修改");
        OPER_TYPE_NAMES.put("3", "删除");
        OPER_TYPE_NAMES.put("4", "导出");
        OPER_TYPE_NAMES.put("5", "导入");
        OPER_TYPE_NAMES.put("6", "清空");
        OPER_TYPE_NAMES.put("7", "授权");
        GENDER_NAMES.put("0", "男");
        GENDER_NAMES.put("1", "女");
        GENDER_NAMES.put("2", "未知");
    }

    @Autowired
    private DashboardMapper dashboardMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private SysLogininforMapper logininforMapper;

    @Autowired
    private SysOperLogMapper operLogMapper;

    /** 大屏全量数据 */
    public Map<String, Object> screenData() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sevenDaysAgo = LocalDate.now().minusDays(6).format(fmt);
        Date todayStart = DateUtil.beginOfDay(new Date());

        Map<String, Object> result = new LinkedHashMap<>();
        // 顶部统计卡
        result.put("date", DateUtil.today());
        result.put("userCount", userMapper.selectCount(null));
        result.put("studentCount", studentMapper.selectCount(null));
        result.put("todayLogin", logininforMapper.selectCount(new LambdaQueryWrapper<SysLogininfor>()
                .ge(SysLogininfor::getLoginTime, todayStart)));
        result.put("todayOper", operLogMapper.selectCount(new LambdaQueryWrapper<SysOperLog>()
                .ge(SysOperLog::getOperTime, todayStart)));
        // 趋势与分布
        result.put("userTrend", fillZeroDates(dashboardMapper.userTrend(sevenDaysAgo), sevenDaysAgo));
        result.put("loginTrend", fillZeroDates(dashboardMapper.loginTrend(sevenDaysAgo), sevenDaysAgo));
        result.put("roleDist", dashboardMapper.roleDistribution());
        result.put("classDist", dashboardMapper.classDistribution());
        result.put("genderDist", translateNames(dashboardMapper.genderDistribution(), GENDER_NAMES));
        result.put("operTypeDist", translateNames(dashboardMapper.operTypeDistribution(), OPER_TYPE_NAMES));
        return result;
    }

    /** 把编码值翻译成中文名称 */
    private List<Map<String, Object>> translateNames(List<Map<String, Object>> rows, Map<String, String> dict) {
        return rows.stream().map(row -> {
            Map<String, Object> item = new LinkedHashMap<>();
            String key = String.valueOf(row.get("name"));
            item.put("name", dict.getOrDefault(key, key));
            item.put("value", row.get("value"));
            return item;
        }).collect(Collectors.toList());
    }

    /** 趋势数据补零：没有数据的日期也显示 0，图表横轴才连续 */
    private List<Map<String, Object>> fillZeroDates(List<Map<String, Object>> rows, String start) {
        Map<String, Object> byDate = rows.stream()
                .collect(Collectors.toMap(r -> String.valueOf(r.get("date")), r -> r.get("value")));
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate cursor = LocalDate.parse(start);
        DateTimeFormatter label = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 0; i < 7; i++) {
            String key = cursor.format(label);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", key);
            item.put("value", byDate.getOrDefault(key, 0));
            result.add(item);
            cursor = cursor.plusDays(1);
        }
        return result;
    }

}
