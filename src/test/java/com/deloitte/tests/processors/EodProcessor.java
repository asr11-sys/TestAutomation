package com.deloitte.tests.processors;

import org.apache.camel.Exchange;
import org.apache.camel.language.XPath;

public class EodProcessor {

    public static final String EXTERBAL_ID_HEADER =  "EXTERNAL-ID";
    public static final String EXT_ID =  "EXT_ID";

    public void constructTradeHubExternalId(Exchange ex,
                                            @XPath("/TRADE/INVNUM/text()")
                                                    String invnum,
                                            @XPath("/TRADE/TRDCOMM_COMMENTS/text()")
                                                    String commentGeneralField
                                            ){
        ex.getIn().setHeader(EXTERBAL_ID_HEADER,
                String.format("%s",
                        invnum));

        ex.getIn().setHeader(EXT_ID,
                String.format("%s",
                        commentGeneralField));
    }

}
