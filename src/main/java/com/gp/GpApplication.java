package com.gp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gp.**.mapper")
public class GpApplication {

    public static void main(String[] args) {
        SpringApplication.run(GpApplication.class, args);
        System.out.println("\n" +
            "========================================\n" +
            "  GP-Framework 启动成功!\n" +
            "  接口文档: http://localhost:8081/doc.html\n" +
            "========================================\n");
    }

}
