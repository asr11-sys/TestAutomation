package com.deloitte.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class CsvUtil {

    public static  List<List<String>> processColumns(List<List<String>> csvContent, BiFunction<String, String, String> processColumnFuntion){
        List<String> headers = csvContent.get(0);
        ArrayList<List<String>> res = new ArrayList<>(csvContent.size());
        res.add(headers);

        return csvContent.stream()
                .skip(1)
                .map(row ->
                        IntStream.range(0, row.size())
                        .mapToObj(i -> processColumnFuntion.apply(headers.get(i), row.get(i)))
                         .collect( Collectors.toList() )
                )
                .collect( Collectors.toCollection(() -> res) );
    }

    public static  List<Map<String, String>> filterMapRows(List<Map<String, String>> csvContent, Predicate<Map<String,String>> isRemoveRow){
        return csvContent.stream()
                .filter(mapRow -> isRemoveRow.test(mapRow))
                .collect(Collectors.toList());
    }

    public static  List<Map<String,String>> addRowWithHeaderValues(Map<String, String> record){
        List<Map<String,String>> csvContent = new ArrayList<>();
        csvContent.add(record);
        return addRowWithHeaderValues(csvContent);
    }

    public static List<Map<String, String>> addRowWithHeaderValues(List<Map<String, String>> csvContent){
        Map<String, String> headers = new LinkedHashMap<>();
        Map<String,String> firstRow = csvContent.get(0);
        firstRow.forEach((k,v) -> {
            headers.put(k,k);
        });

        csvContent.add(0,headers);
        return  csvContent;
    }

    public static  List<List<String>> filterRows(List<List<String>> csvContent, Predicate<Map<String,String>> isRemoveRow){
        List<String> headers = csvContent.get(0);
        ArrayList<List<String>> res = new ArrayList<>();
        res.add(headers);

        return csvContent.stream()
                .skip(1)
                .filter(list -> isRemoveRow.test(rowAsMap(list, headers)))
                .collect(Collectors.toCollection(() -> res));
    }

    public static  Map<String,String> rowAsMap(List<String> records, List<String> header){
        Map<String,String> res = new HashMap<>();
        for(int i = 0 ; i < header.size(); i++){
            res.put(header.get(i), records.get(i));
        }
        return res;
    }

    public static  List<Map<String,String>> csvRecordsToListOfMaps(List<List<String>> records){
        List<String> headers = records.get(0);
        return records.stream().skip(1)
                .map(list -> {
                    Map<String, String> res = new HashMap<>();
                    for(int i = 0 ; i < list.size(); i++){
                        res.put(headers.get(i), list.get(i));
                    }
                    return  res;
                }).collect(Collectors.toList());
    }

    public static List<List<String>> removeColumns(List<List<String>> csvContent, Predicate<String> isRemoveColumn){
        List<String> headers = csvContent.get(0);
        Set<Integer> headerIndexesToRemove = IntStream.range(0, headers.size())
                .filter(index -> isRemoveColumn.test(headers.get(index)))
                .boxed()
                .collect(Collectors.toSet());
        return csvContent.stream().map( row ->
                IntStream.range( 0, row.size() )
                    .filter( i -> ! headerIndexesToRemove.contains(i) )
                    .mapToObj( row::get )
                    .collect( Collectors.toList() )
        ).collect(Collectors.toList());
    }

    public static List<Map<String,String>> parseCsvFile(String filePath) throws IOException{
        return parseCsvFile(filePath, CSVFormat.EXCEL
                .withHeader()
                .withTrim());
    }

    public static  List<Map<String,String>> parseCsvFile(String filePath, CSVFormat csvFormat) throws IOException {
        Path path = Paths.get(filePath);
        File file = path.toFile();

        if(!file.exists() || !file.isFile())
            throw new IllegalArgumentException(String.format("",filePath));

        List<Map<String,String>> res = new ArrayList<>();

        try (Reader in = new FileReader(filePath)){
            CSVParser parsed = csvFormat.parse(in);
            List<CSVRecord> records = parsed.getRecords();
            Map<String,Integer> headerMap = parsed.getHeaderMap();

            for(CSVRecord record : records){
                Map<String,String> map = new LinkedHashMap<>();

                for(String header : headerMap.keySet())
                    map.put(header, record.get(header));

                res.add(map);
            }
            return  res ;
        } catch (Exception ex){
            throw ex;
        }
    }

}
