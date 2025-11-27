package com.xyex;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xyex.mapper")
public class XYESApplication {

    public static void main(String[] args) {
        SpringApplication.run(XYESApplication.class, args);
    }

}
