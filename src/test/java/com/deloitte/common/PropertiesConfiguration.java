package com.deloitte.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@ConfigurationProperties(prefix = "integration.demo")
public class PropertiesConfiguration {

    private String portfolioCOdeKey;
    private String jmsOutboundRouteUri;
    private String jmsTestRouteUri;
    private String instanceId;
    private String jmsInboundRouteOptions;

    public String getJmsOutboundGatewayUri(){ return buildUri(jmsOutboundRouteUri,jmsInboundRouteOptions );}

    public String getJmsTestGatewayUri(){ return buildUri(jmsTestRouteUri,jmsInboundRouteOptions );}

    private String buildUri(String base , String options){
        if (options == null || options.isEmpty()){
            return base;
        }
        return base.startsWith("?") ? base + options : base + "?" + options;
    }
}
