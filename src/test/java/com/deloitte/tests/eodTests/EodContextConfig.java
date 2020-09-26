package com.deloitte.tests.eodTests;

import com.deloitte.common.ConfigurationPropertiesUtil;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;


@ComponentScan(
        value = {"com.deloitte.tests.eodTests",
                    "com.deloitte.tests.datasets",
                    "com.deloitte.tests.processors",
                    "com.deloitte.common",
                    "com.deloitte.service"}
)
@Configuration
@PropertySources({
        @PropertySource("classpath:/eod.properties"),
        @PropertySource("classpath:/application.properties")
})
public class EodContextConfig extends CamelConfiguration {

    @Autowired
    protected ConfigurationPropertiesUtil configurationPropertiesUtil;

    @Bean
    public PropertiesComponent properties() {
        PropertiesComponent pc = new PropertiesComponent();
        pc.setLocations(
                new String[]{
                        "classpath:/eod.properties",
                        "classpath:/application.properties"
                });
        return pc;
    }

}