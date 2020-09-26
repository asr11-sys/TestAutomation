package com.deloitte.tests.datasets;

import lombok.Data;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Data
public class SecurityDataSet implements Serializable {

    private static Map<String, String> EXPECTED_HEADERS_MAP = new HashMap<>();

    private String input ;

    private Map<String, String> expected ;
    private Map<String, String> expectedMapped;

    private Path inputPath ;
    private Path expectedPath ;

    static {
        SecurityDataSet.ExpectedHeader.values();
    }

    public enum ExpectedHeader{
        TEST_ID("TEST_ID"),
        CUSIP("CUSIP"),
        ACCURAL_DT("ACCURAL_DT"),
        AGENCY("AGENCY"),
        NOTIONAL_FACE("NOTIONAL_FACE"),
        AMT_ISU("AMT_ISU"),
        ISSUE_DT("ISSUE_DT"),
        SEDOL("SEDOL"),
        ISIN("ISIN"),
        TICKER_EXCH("TICKER_EXCH"),
        ISSUER_ID("ISSUER_ID");

        private String val;

        ExpectedHeader(String val){
            this.val = val ;
            EXPECTED_HEADERS_MAP.put(val,this.name());
        }

        public String getVal(){
            return val ;
        }
    }

    public SecurityDataSet(){

    }

    public SecurityDataSet(String input , Map<String, String> expected){
        this.input = input;
        this.expected = expected ;

        expectedMapped = new HashMap<>();
        expected.forEach((String key, String value) -> {
            expectedMapped.put(EXPECTED_HEADERS_MAP.get(key), value);
        });
    }

    public SecurityDataSet(Path inputPath , Path expectedPath){
        this.inputPath = inputPath;
        this.expectedPath = expectedPath ;
    }

}
