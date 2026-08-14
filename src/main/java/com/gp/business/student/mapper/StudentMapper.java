package com.gp.business.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.business.student.domain.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

}
