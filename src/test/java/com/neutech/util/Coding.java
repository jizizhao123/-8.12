package com.neutech.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class Coding {

    static final String URL = "jdbc:mysql://localhost:3306/db?serverTimezone=Asia/Shanghai";
    static final String USER = "root";
    static final String PASSWORD = "123456";

    public static void main(String[] args) {
        // 项目目录
        String basePath = System.getProperty("user.dir");

        // 生成的表名
        String table = "user_application"; // 修改为你需要生成的表名

        FastAutoGenerator.create(URL, USER, PASSWORD)
                // 全局配置
                .globalConfig(builder -> {
                    // 设置项目路径
                    builder.outputDir(basePath + "/src/main/java")
                            // 配置作者
                            .author("jzz")
                            .disableOpenDir();
                })
                // 包配置
                .packageConfig(builder -> {
                    // 配置父包
                    builder.parent("com.neutech");
                    // 自定义xml生成路径
                    Map<OutputFile, String> pathInfo = new HashMap<>();
                    pathInfo.put(OutputFile.xml, basePath + "/src/main/resources/com/neutech/mapper");
                    builder.pathInfo(pathInfo);
                })
                // 策略配置
                .strategyConfig(builder -> {
                    // 配置生成的表名
                    builder.addInclude(table)  // 生成多个表
                            // 配置entity策略
                            .entityBuilder()
                            // 所有entity都加Lombok注解
                            .enableLombok()
                            // entity类名要从下划线转驼峰
                            .naming(NamingStrategy.underline_to_camel)
                            // 属性名也要下划线转驼峰
                            .columnNaming(NamingStrategy.underline_to_camel)
                            // 配置controller策略
                            .controllerBuilder()
                            // 每一个controller都加@RestController注解
                            .enableRestStyle()
                            // 配置service策略
                            .serviceBuilder()
                            // 去掉service接口前面的I
                            .formatServiceFileName("%sService");
                })
                // 配置模板引擎
                .templateEngine(new FreemarkerTemplateEngine())
                // 执行代码生成
                .execute();
    }
}
