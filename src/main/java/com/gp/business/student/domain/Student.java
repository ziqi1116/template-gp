package com.gp.business.student.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.annotation.Excel;
import com.gp.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_student")
public class Student extends BaseEntity {

    @Excel(name = "学号")
    private String studentNo;

    @Excel(name = "学生姓名")
    private String studentName;

    @Excel(name = "性别", readConverterExp = "0=男,1=女,2=未知")
    private String gender;

    @Excel(name = "联系电话")
    private String phone;

    @Excel(name = "班级")
    private String className;

    @Excel(name = "状态", readConverterExp = "0=启用,1=停用")
    private String status;

    private String email;
    private String avatar;
    private String address;
    private Long deptId;

}
