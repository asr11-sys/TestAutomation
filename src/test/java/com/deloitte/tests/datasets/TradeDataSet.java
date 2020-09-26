package com.deloitte.tests.datasets;

import lombok.Data;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Data
public class TradeDataSet implements Serializable {

    private static Map<String, String> EXPECTED_HEADERS_MAP = new HashMap<>();

    private Path input ;
    private Path expectedPath ;

    private String tradeComment ;

    private Map<String, String> expected ;
    private Map<String, String> expectedMapped;

    static {
        TradeDataSet.ExpectedHeader.values();
    }

    public enum ExpectedHeader{
        COMMENT_GENERAL("COMMENT_GENERAL"),
        TRD_NUM("TRD_NUM"),
        TRD_VER("TRD_VER"),
        SETTL_CCY("SETTL_CCY"),
        TXN_TM("TXN_TM"),
        LAST_UPDATE_TM("LAST_UPDATE_TM"),
        LAST_PX("LAST_PX"),
        GROSS_TRD_AMT("GROSS_TRD_AMT"),
        TRANS_TYP("TRANS_TYP"),
        TRD_DT("TRD_DT"),
        CUSIP("CUSIP"),
        FUND("FUND")
        ;

        private String val;

        ExpectedHeader(String val){
            this.val = val ;
            EXPECTED_HEADERS_MAP.put(val,this.name());
        }

        public String getVal(){
            return val ;
        }
    }

    public TradeDataSet(){

    }

    public TradeDataSet(Path input , Map<String, String> expected){
        this.input = input;
        this.expected = expected ;

        expectedMapped = new HashMap<>();
        expected.forEach((String key, String value) -> {
            expectedMapped.put(EXPECTED_HEADERS_MAP.get(key), value);
        });
    }

    public TradeDataSet(String tradeComment , Map<String, String> expected){
        this.tradeComment = tradeComment;
        this.expected = expected ;

        expectedMapped = new HashMap<>();
        expected.forEach((String key, String value) -> {
            expectedMapped.put(EXPECTED_HEADERS_MAP.get(key), value);
        });
    }

    public TradeDataSet(Path input , Path expectedPath){
        this.input = input;
        this.expectedPath = expectedPath ;
    }

}
