package com.deloitte.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public interface DatabaseService {

    List<Map<String,Object>> queryForList(String sql, Object... args);

    Callable<List<Map<String, Object>>> queryForListCallable(final String sql , final Object... args);


}
