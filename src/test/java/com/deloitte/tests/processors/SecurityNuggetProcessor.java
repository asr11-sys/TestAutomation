package com.deloitte.tests.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;

public class SecurityNuggetProcessor implements Processor {

    public static final String TRADE_KEY = "TRANSACTIONS";
    public static final String SECURITY_KEY = "ASSETS";
    public static final String TRADE_PROPERTY_KEY = "TRADE";


    @SuppressWarnings("unchecked")
    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println(" Received Message "+exchange.getIn().getBody().toString());
        Map<String, String> tradeMap = (Map<String,String>)exchange.getIn().getBody();

        String securitySource = tradeMap.get(SECURITY_KEY);
        exchange.setProperty(SECURITY_KEY, securitySource);

        String tradeSource = tradeMap.get(TRADE_KEY);
        exchange.setProperty(TRADE_PROPERTY_KEY, tradeSource);

        exchange.getIn().setBody(tradeSource);

    }
}
