package com.deloitte.tests.eodTests;

import com.deloitte.common.Constants;
import com.deloitte.service.StoreService;
import com.deloitte.tests.processors.EodProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.deloitte.tests.processors.TradeProcessor.EXTERBAL_ID_HEADER;
import static com.deloitte.tests.processors.TradeProcessor.EXT_ID;
import static org.apache.camel.test.junit4.TestSupport.createDirectory;
import static org.apache.camel.test.junit4.TestSupport.deleteDirectory;

@Slf4j
@Component
public class EodRoute extends RouteBuilder {

    protected static final String INBOX_PATH = "build/security/input/";
    protected static final String SECURITY_INPUT_PATH = "src/test/resources/data/eod/security/input/";
    protected static final String SECURITY_EXPECTED_FILE_PATH = "src/test/resources/data/eod/security/expected";

    @Value("${integration.demo.jmsOutboundRouteUri}")
    private String jmsOutboundRouteUri;

    @Value("${integration.demo.jmsTestRouteUri}")
    private String jmsTestRouteUri;

    @Autowired
    private StoreService storeService ;

    @Override
    public void configure() throws Exception {

        from("file://"+ INBOX_PATH)
                .to("log:?level=INFO&showBody=true")
                .to(jmsOutboundRouteUri);


        /*from(jmsTestRouteUri)
                .to("log:?level=INFO&showBody=true")
                .split(body().tokenizeXML("TRADE", "TRANSACTIONS"))
                .bean(EodProcessor.class, "constructTradeHubExternalId")
                .process(ex -> {
                    storeService.store((String)ex.getIn().getHeader(EXTERBAL_ID_HEADER) , ex.getIn().getBody());
                    storeService.store((String)ex.getIn().getHeader(EXT_ID) , ex.getIn().getHeader(EXTERBAL_ID_HEADER));
                })
                .end();*/
    }


    public static void sendData(Path input ) throws Exception {
        deleteDirectory(INBOX_PATH);
        createDirectory(INBOX_PATH);

        Path targetLocation = Paths.get(INBOX_PATH + Constants.SECURITY_XML);
        Path inputXML = Paths.get(SECURITY_INPUT_PATH+Constants.SECURITY_XML);
        Files.copy(inputXML, targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }
}
