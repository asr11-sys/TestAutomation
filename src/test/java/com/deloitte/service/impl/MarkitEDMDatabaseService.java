package com.deloitte.service.impl;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
public class MarkitEDMDatabaseService extends AbstractDatabaseService {

    private HikariDataSource ds = null ;

    @Value("${markit.jdbc.url}")
    private String jdbcUrl;

    @Value("${markit.jdbc.username}")
    private String username;

    @Value("${markit.jdbc.password}")
    private String pwd;

    protected  synchronized DataSource getDataSource(){
        if (ds == null)
            try {
                ds = new HikariDataSource();
                ds.setJdbcUrl(jdbcUrl);
                ds.setUsername(username);
                ds.setPassword(pwd);
                ds.setMaximumPoolSize(1);
                return  ds ;
            } catch (Exception e){
                throw e ;
            }
            else
                return ds ;
    }
}
