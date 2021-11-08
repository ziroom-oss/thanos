package com.ziroom.qa.quality.defende.provider.controller;

import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/10/15 3:56 下午
 */
@Slf4j
@RestController
@RequestMapping("/executeSql")
public class ExecuteSqlController {
    @RequestMapping("/executeSql")
    public RestResultVo executeSql(String sql){
        String jdbcURL = "jdbc:mysql://10.216.26.90:3306/quality_defende?useUnicode=true&characterEncoding=utf-8";
        String jdbcUser = "ceshi_qualitydefende_user";
        String jdbcPassword = "a55720040febffbc";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(jdbcURL,jdbcUser,jdbcPassword);
            Statement stmt = conn.createStatement();
            //修改表结构
            stmt.executeUpdate(sql);
            System.out.println("执行成功...");
        } catch (Exception e) {
            log.error("执行失败",e);
            throw new CustomException(e.getCause().toString());
        }
        return RestResultVo.fromData("执行成功");
    }
}