package com.gp.business.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.business.student.domain.Student;
import com.gp.business.student.mapper.StudentMapper;
import com.gp.common.core.page.PageQuery;
import com.gp.common.core.page.PageResult;
import org.springframework.stereotype.Service;

@Service
public class StudentService extends ServiceImpl<StudentMapper, Student> {

    public PageResult<Student> pageList(PageQuery pageQuery, Student student) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(student.getStudentNo() != null && !student.getStudentNo().isEmpty(),
                Student::getStudentNo, student.getStudentNo());
        wrapper.like(student.getStudentName() != null && !student.getStudentName().isEmpty(),
                Student::getStudentName, student.getStudentName());
        wrapper.eq(student.getStatus() != null && !student.getStatus().isEmpty(),
                Student::getStatus, student.getStatus());
        wrapper.orderByDesc(Student::getId);

        Page<Student> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<Student> result = this.page(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

}
