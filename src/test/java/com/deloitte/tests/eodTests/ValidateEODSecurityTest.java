package com.deloitte.tests.eodTests;

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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@RunWith(Parameterized.class)
public class ValidateEODSecurityTest extends AbstractTest {

    private String query;
    private DatabaseService markitDbService;
    private StoreService storeService;


    @Parameterized.Parameters(name = "{index}: {0}")
    public static List<SecurityDataSet> data() throws Exception {

        List<String> listOfCusip = FileUtil.getListOfElement(EodRoute.SECURITY_INPUT_PATH + Constants.SECURITY_XML, Constants.ASSET_CUSIP);
        List<Map<String, String>> expectedRecords = CsvUtil.parseCsvFile(EodRoute.SECURITY_EXPECTED_FILE_PATH + "/" + Constants.EXPECTED_SECURITY_CSV);

        List<SecurityDataSet> res = new ArrayList<>();

        IntStream.range(0, listOfCusip.size()).forEach( i -> res.add(new SecurityDataSet(listOfCusip.get(i), expectedRecords.get(i))) );

        return res;
    }

    @Parameterized.Parameter
    public SecurityDataSet record;

    @BeforeClass
    public static void cleanUp() throws Exception {
        deleteDirectory("build/");
        createDirectory("build/");
        List<Path> inputFiles = getFilePathFromDirectory(EodRoute.SECURITY_INPUT_PATH + Constants.SECURITY_XML);
        EodRoute.sendData(inputFiles.get(0));
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

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext acc = new AnnotationConfigApplicationContext();
        acc.register(EodContextConfig.class);
        return acc;
    }

    @Override
    protected void wireDependencies() {
        query = applicationContext.getEnvironment().getProperty(Constants.SECURITY_QUERY);
        markitDbService = applicationContext.getBean(MarkitEDMDatabaseService.class);
        storeService = applicationContext.getBean(StoreService.class);

    }

    @Test
    public void testCreateTrade() throws Exception {

        log.info(" -- CUSIP -- " + record.getInput());

        List<Map<String, String>> actual =
                Awaitility.await(String.format(""))
                        .atMost(60, TimeUnit.SECONDS)
                        .pollInterval(10, TimeUnit.SECONDS)
                        .until(markitDbService.queryForListCallable(query, record.getInput()),
                                (List<Map<String, Object>> res) -> !res.isEmpty()).stream()
                        .map(this::convertToMapOfStrings)
                        .collect(Collectors.toList());


        List<String> diff = ComparisionUtil.compareStringMaps(record.getExpected(), actual.get(0), "TEST_ID");
        if (diff.size() > 0) {
            String table = ConsoleUtil.printTable(diff, actual.get(0), record.getExpected());
            assertListSize("More Values Didnot match the expected data provided." + table, diff, 0);
        }


    }
}
