package com.gp.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {

    private String dictType;

    private String dictLabel;

    private String dictValue;

    private String cssClass;

    private String listClass;

    private String isDefault;

    private String status;

}