package com.gp.business.student.controller;

import java.util.List;

import com.gp.business.student.domain.Student;
import com.gp.business.student.service.StudentService;
import com.gp.common.annotation.Log;
import com.gp.common.core.page.PageQuery;
import com.gp.common.core.page.PageResult;
import com.gp.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "学生管理")
@RestController
@RequestMapping("/business/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Operation(summary = "学生分页查询")
    @GetMapping("/page")
    public Result<PageResult<Student>> page(PageQuery pageQuery, Student student) {
        return Result.success(studentService.pageList(pageQuery, student));
    }

    @Operation(summary = "学生详情")
    @GetMapping("/{id}")
    public Result<Student> getInfo(@PathVariable Long id) {
        return Result.success(studentService.getById(id));
    }

    @Operation(summary = "新增学生")
    @Log(title = "学生管理", operType = "1")
    @PostMapping
    public Result<Void> add(@RequestBody Student student) {
        studentService.save(student);
        return Result.success();
    }

    @Operation(summary = "修改学生")
    @Log(title = "学生管理", operType = "2")
    @PutMapping
    public Result<Void> edit(@RequestBody Student student) {
        studentService.updateById(student);
        return Result.success();
    }

    @Operation(summary = "删除学生")
    @Log(title = "学生管理", operType = "3")
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable List<Long> ids) {
        studentService.removeByIds(ids);
        return Result.success();
    }

    @Operation(summary = "修改状态")
    @Log(title = "学生管理", operType = "2")
    @PutMapping("/status")
    public Result<Void> changeStatus(@RequestBody Student student) {
        Student update = new Student();
        update.setId(student.getId());
        update.setStatus(student.getStatus());
        studentService.updateById(update);
        return Result.success();
    }

}
