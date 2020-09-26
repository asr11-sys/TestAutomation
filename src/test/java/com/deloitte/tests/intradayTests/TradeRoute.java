package com.deloitte.tests.intradayTests;

import com.deloitte.common.Constants;
import com.deloitte.service.StoreService;
import com.deloitte.tests.processors.TradeNuggetProcessor;
import com.deloitte.tests.processors.TradeProcessor;
import com.deloitte.tests.processors.ZipCompressProcessor;
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
public class TradeRoute extends RouteBuilder {

    protected static final String INBOX_PATH = "build/trade/input/";
    protected static final String OUTBOX_PATH = "build/trade/output/";
    protected static final String INPUT_PATH = "src/test/resources/data/intraday/input/";
    protected static final String EXPECTED_FILE_PATH = "src/test/resources/data/intraday/expected";

    @Value("${integration.demo.jmsOutboundRouteUri}")
    private String jmsOutboundRouteUri;

    @Value("${integration.demo.jmsTestRouteUri}")
    private String jmsTestRouteUri;

    @Value("${integration.demo.nuggetOutboxUri}")
    private String nuggetOutboxRouteUri;

    @Autowired
    private StoreService storeService ;

    @Override
    public void configure() throws Exception {

        from("file://"+ INBOX_PATH)
                .setHeader("cusip",xpath(Constants.TRADE_CUSIP))
                .setHeader("dirPath",simple(INBOX_PATH))
                .setHeader("outDirPath",simple("build/trade/output/"))
                .process(new ZipCompressProcessor());

        from("file://build/trade/output/")
                .to(nuggetOutboxRouteUri);


        from(jmsTestRouteUri)
                .to("log:?level=INFO&showBody=true")
                .process(new TradeNuggetProcessor())
                .split(body().tokenizeXML("TRADE", "TRANSACTIONS"))
                .bean(TradeProcessor.class, "constructTradeHubExternalId")
                .process(ex -> {
                    storeService.store((String)ex.getIn().getHeader(EXTERBAL_ID_HEADER) , ex.getIn().getBody());
                    storeService.store((String)ex.getIn().getHeader(EXT_ID) , ex.getIn().getHeader(EXTERBAL_ID_HEADER));
                })
                .end();
    }


    public static void sendData(Path input, String fileName ) throws Exception {
        deleteDirectory(INBOX_PATH);
        deleteDirectory(OUTBOX_PATH);

        createDirectory(INBOX_PATH);
        createDirectory(OUTBOX_PATH);

        Path targetLocation = Paths.get(INBOX_PATH + fileName);
        Path inputXML = Paths.get(input.toString());
        Files.copy(inputXML, targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }
}
