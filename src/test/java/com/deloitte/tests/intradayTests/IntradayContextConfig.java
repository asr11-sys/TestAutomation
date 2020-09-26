package com.deloitte.tests.intradayTests;

import com.deloitte.common.ConfigurationPropertiesUtil;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;


@ComponentScan(
        value = {"com.deloitte.tests.intradayTests",
                    "com.deloitte.tests.datasets",
                    "com.deloitte.tests.processors",
                    "com.deloitte.common",
                    "com.deloitte.service"}
)
@Configuration
@PropertySources({
        @PropertySource("classpath:/trade.properties"),
        @PropertySource("classpath:/application.properties")
})
public class IntradayContextConfig extends CamelConfiguration {

    @Autowired
    protected ConfigurationPropertiesUtil configurationPropertiesUtil;

    @Bean
    public PropertiesComponent properties() {
        PropertiesComponent pc = new PropertiesComponent();
        pc.setLocations(
                new String[]{
                        "classpath:/trade.properties",
                        "classpath:/application.properties"
                });
        return pc;
    }

}