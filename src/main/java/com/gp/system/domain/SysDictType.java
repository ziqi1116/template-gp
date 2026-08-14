package com.gp.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
public class SysDictType extends BaseEntity {

    private String dictName;

    private String dictType;

    private String status;

}