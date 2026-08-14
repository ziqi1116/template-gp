package com.gp.system.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private Long deptId;
    private String userName;
    private String nickName;
    private String email;
    private String phone;
    private String sex;
    private String password;
    private String status;
    private String avatar;
    private String loginIp;
    private Date loginDate;

}
