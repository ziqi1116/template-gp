package com.gp.business.${module}.domain;

${importLines}
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.annotation.Excel;
import com.gp.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ${functionName}实体（${tableComment}）
 *
 * 生成于 ${date}，可按业务需要自由修改
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("${table}")
public class ${className} extends BaseEntity {

${fieldLines}
}
