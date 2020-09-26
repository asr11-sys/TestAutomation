package com.deloitte.common;

import org.apache.commons.lang.StringUtils;

import java.util.Locale;

public class ComparisionUtilFunctions {

    public static boolean startWithAndEndWith(String value, String startWith, String endsWith){
        return StringUtils.isNotBlank(value)
                && value.startsWith(startWith)
                && value.endsWith(endsWith);
    }


}
