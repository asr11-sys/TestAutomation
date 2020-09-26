package com.deloitte.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.swing.text.html.HTMLEditorKit;
import java.lang.reflect.Field;

@Component
public class ConfigurationPropertiesUtil {

    @Autowired
    protected Environment env ;

    public <T> T loadConfigFromProperties(String prefix, Class<T> clazz) throws IllegalAccessException , InstantiationException {
        T config = clazz.newInstance();
        return loadConfigFromProperties(prefix, config);
    }

    public <T> T loadConfigFromProperties(String prefix, T config) throws IllegalAccessException{
        Field[] fields = config.getClass().getDeclaredFields();
        for (Field field : fields){
            field.setAccessible(true);
            Class<?> clazz = field.getType();
            Object fieldValue = env.getProperty(String.format("%s.%s", prefix, field.getName()), clazz);
            if(fieldValue != null){
                field.set(config, fieldValue);
            }
        }
        return config;
    }
}
