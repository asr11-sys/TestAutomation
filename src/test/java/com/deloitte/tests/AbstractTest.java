package com.deloitte.tests;

import com.google.common.collect.Maps;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import java.math.BigDecimal;
import java.util.Map;

public abstract class AbstractTest extends CamelSpringTestSupport {

    @Override
    public boolean isCreateCamelContextPerClass(){
        return true ;
    }

    protected Map<String,String> convertToMapOfStrings(final Map<String, Object> map){
        return Maps.transformEntries(map, this::convertToString);
    }

    protected String convertToString(String key, Object val){
        if(val == null){
            return "";
        } else if (val instanceof  String){
            return (String)val ;
        }else if (val instanceof BigDecimal){
            return ((BigDecimal) val).setScale(2, BigDecimal.ROUND_HALF_DOWN).toPlainString();
        }else if (val instanceof  java.sql.Timestamp){
            return  val.toString();
        } else {
            return  val.toString();
        }
    }

    @Override
    protected void doPostSetup() throws Exception {
        super.doPostSetup();
        wireDependencies();
    }

    @Override
    public void postProcessTest() throws  Exception{
        super.postProcessTest();
        wireDependencies();
    }

    protected  abstract  void wireDependencies() throws  InstantiationException, IllegalAccessException;

}
