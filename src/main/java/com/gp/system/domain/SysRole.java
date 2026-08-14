package com.gp.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    private String roleName;
    private String roleKey;
    private Integer roleSort;
    private String status;

}
