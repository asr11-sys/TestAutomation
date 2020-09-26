package com.deloitte.service.impl;

import com.deloitte.service.DatabaseService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class AbstractDatabaseService implements DatabaseService {

    protected JdbcTemplate jdbcTemplate ;
    protected NamedParameterJdbcTemplate namedJdbcTemplate ;

    @PostConstruct
    public void init(){
        jdbcTemplate = new JdbcTemplate(getDataSource(), true);
        namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    protected  abstract DataSource getDataSource();

    @Override
    public List<Map<String,Object>> queryForList(String sql, Object... args){
        return  jdbcTemplate.queryForList(sql, args);
    }

    @Override
    public Callable<List<Map<String,Object>>> queryForListCallable(final String sql, final Object... args){
        return () -> queryForList(sql, args);
    }






}
