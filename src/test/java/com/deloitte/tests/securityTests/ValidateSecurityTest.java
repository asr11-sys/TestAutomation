package com.deloitte.tests.securityTests;

import com.deloitte.common.*;
import com.deloitte.service.DatabaseService;
import com.deloitte.service.StoreService;
import com.deloitte.service.impl.MarkitEDMDatabaseService;
import com.deloitte.tests.AbstractTest;
import com.deloitte.tests.datasets.SecurityDataSet;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@RunWith(Parameterized.class)
public class ValidateSecurityTest extends AbstractTest {

    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_kkmmss");
    private String query;
    private DatabaseService markitDbService;
    private StoreService storeService;

    @Parameterized.Parameter
    public SecurityDataSet record;


    private static String constructTradeFileName() {
        return String.format("TRADEIN_%s_NEW_%s.csv", "create", DATE_TIME_FORMATTER.format(LocalDateTime.now()));
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static List<SecurityDataSet> data() throws Exception {
        List<Path> inputFiles = getFilePathFromDirectory(SecurityRoute.INPUT_PATH);
        List<Path> expectedFiles = getFilePathFromDirectory(SecurityRoute.EXPECTED_FILE_PATH);
        List<SecurityDataSet> res = new ArrayList<>();
        IntStream.range(0,inputFiles.size()).forEach(i -> res.add(new SecurityDataSet(inputFiles.get(i), expectedFiles.get(i))));
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
        acc.register(SecurityContextConfig.class);
        return acc;
    }

    @Override
    protected void wireDependencies() {
        query = applicationContext.getEnvironment().getProperty("security.sql");
        markitDbService = applicationContext.getBean(MarkitEDMDatabaseService.class);
        storeService = applicationContext.getBean(StoreService.class);

    }

    @Test
    public void validateSecurityTest() throws Exception {
        SecurityRoute.sendData(record.getInputPath(), Constants.SECURITY_XML);
        List<String> listOfCusip = FileUtil.getListOfElement(record.getInputPath().toString(), Constants.ASSET_CUSIP);

        List<Map<String, String>> actual =
                Awaitility.await(String.format("Retrieving SECURITY DATA FROM DB FOR CUSIP %S ",listOfCusip.get(0)))
                        .atMost(60, TimeUnit.SECONDS)
                        .pollInterval(10, TimeUnit.SECONDS)
                        .until(markitDbService.queryForListCallable(query, listOfCusip.get(0)),
                                (List<Map<String, Object>> res) -> !res.isEmpty()).stream()
                        .map(this::convertToMapOfStrings)
                        .collect(Collectors.toList());

        List<Map<String, String>> expectedRecods =
                CsvUtil.parseCsvFile(record.getExpectedPath().toString());

        List<String> diff = ComparisionUtil.compareStringMaps(expectedRecods.get(0), actual.get(0), "TEST_ID");
        if (diff.size() > 0) {
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
