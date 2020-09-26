package com.deloitte.tests.intradayTests;

import com.deloitte.common.*;
import com.deloitte.service.DatabaseService;
import com.deloitte.service.StoreService;
import com.deloitte.service.impl.TradeHubDatabaseService;
import com.deloitte.tests.AbstractTest;
import com.deloitte.tests.datasets.TradeDataSet;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@RunWith(Parameterized.class)
public class CreateTradeTest extends AbstractTest {

    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_kkmmss");
    private String query;
    private DatabaseService tradeDbService;
    private StoreService storeService;

    @Parameterized.Parameter
    public TradeDataSet record;


    private static String constructTradeFileName() {
        return String.format("TRADEIN_%s_NEW_%s.csv", "create", DATE_TIME_FORMATTER.format(LocalDateTime.now()));
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static List<TradeDataSet> data() throws Exception {
        List<Path> inputFiles = getFilePathFromDirectory(TradeRoute.INPUT_PATH);
        List<Path> expectedFiles = getFilePathFromDirectory(TradeRoute.EXPECTED_FILE_PATH);
        List<TradeDataSet> res = new ArrayList<>();
        IntStream.range(0,inputFiles.size()).forEach(i -> res.add(new TradeDataSet(inputFiles.get(i), expectedFiles.get(i))));
        return res;
    }

    @BeforeClass
    public static void cleanUp() throws Exception {
        deleteDirectory("build/");
        createDirectory("build/");
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext acc = new AnnotationConfigApplicationContext();
        acc.register(IntradayContextConfig.class);
        return acc;
    }

    @Override
    protected void wireDependencies() {
        query = applicationContext.getEnvironment().getProperty("trade.sql");
        tradeDbService = applicationContext.getBean(TradeHubDatabaseService.class);
        storeService = applicationContext.getBean(StoreService.class);

    }

    @Test
    public void createTradeTest() throws Exception {
        TradeRoute.sendData(record.getInput(), Constants.TRADE_FILE_NAME);
        List<String> tradeComment = FileUtil.getListOfElement(record.getInput().toString(), Constants.TRDCOMM_COMMENTS_XPATH);
        String identifier = tradeComment.get(0);
        log.info(" -- IDENTIFIER -- " + identifier);
        String inv =  Awaitility.await(String.format("Retrieving TRADE MESSAGE FROM QUEUE FOR %S IDENTIFIER ",identifier))
                .atMost(60, TimeUnit.SECONDS)
                .pollDelay(10, TimeUnit.SECONDS)
                .until(storeService.retrieveFun(identifier, String.class), Objects::nonNull);
        log.info(" -- INVNUM -- " + inv);

        List<Map<String, String>> actual =
                Awaitility.await(String.format("Retrieving TRADE DATA FROM DB FOR  INVNUM %S ",inv))
                        .atMost(60, TimeUnit.SECONDS)
                        .pollInterval(10, TimeUnit.SECONDS)
                        .until(tradeDbService.queryForListCallable(query,  new BigInteger(inv)),
                                (List<Map<String, Object>> res) -> !res.isEmpty()).stream()
                        .map(this::convertToMapOfStrings)
                        .collect(Collectors.toList());

        List<Map<String, String>> expectedRecods =
                CsvUtil.parseCsvFile(record.getExpectedPath().toString());

        /*List<String> diff = ComparisionUtil.compareStringMaps(expectedRecods.get(0), actual.get(0), "COMMENT_GENERAL");
        if (diff.size() > 0) {
            String table = ConsoleUtil.printTable(diff, actual.get(0), expectedRecods.get(0));
            assertListSize("More Values Didnot match the expected data provided." + table, diff, 0);
        }*/

        List<String> diff = ComparisionUtil.compareStringMaps(expectedRecods.get(0), actual.get(0), "COMMENT_GENERAL");
        if(diff.size() > 0){
            MapDifference<String, Object> differences = Maps.difference(expectedRecods.get(0), actual.get(0));
            GenericUtil.createHtmlpage(
                    differences, Constants.JUNIT_TRADE_REPORT_FILES_DIRECTORY + identifier + "_" + inv+"_"+timeStamp+"_TradeComparision.html");
            String table = ConsoleUtil.printTable(diff, actual.get(0), expectedRecods.get(0));
            assertListSize("More Values Didnot match the expected data provided." + table, diff, 0);
        }

    }

    public static List<Path> getFilePathFromDirectory(String path) throws IOException {
        List<Path> listOfFilePath = new ArrayList<>();
        try (Stream<Path> filePathStream = Files.walk(Paths.get(path))) {
            filePathStream.forEach(
                    filePath -> {
                        if (Files.isRegularFile(filePath)) {
                            listOfFilePath.add(filePath);
                        }
                    });
        }
        return listOfFilePath;
    }
}
