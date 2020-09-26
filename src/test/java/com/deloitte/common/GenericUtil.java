package com.deloitte.common;

import com.google.common.collect.MapDifference;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class GenericUtil {



    /* Generate HTML report out of MAP Differences */
    public static void createHtmlpage(MapDifference<String, Object> differences, String reportFilePath) {
        Map<String, Object> entriesInCommon = differences.entriesInCommon();
        Map<String, MapDifference.ValueDifference<Object>> entriesDiffering = differences.entriesDiffering();
        StringBuilder buf = new StringBuilder();
        buf.append("<html>" +
                "<body>" +
                "<table border=\"1px\">" +
                "<tr>" +
                "<th> Field Name </th>" +
                "<th> Expected Value </th>" +
                "<th> DB Result Value </th>" +
                "</tr>");
        Iterator<Map.Entry<String, Object>> itr = entriesInCommon.entrySet().iterator();
        Iterator<Map.Entry<String, MapDifference.ValueDifference<Object>>> itrDiff = entriesDiffering.entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry<String, Object> entry = itr.next();
            Object value = entry.getValue();
            buf.append("<tr><td>")
                    .append(entry.getKey())
                    .append("</td><td><font color=\"DarkGreen\">")
                    .append(value.toString())
                    .append("</font></td><td><font color=\"DarkGreen\">")
                    .append(value.toString())
                    .append("</font></td></tr>");
        }

        while(itrDiff.hasNext()){
            Map.Entry<String, MapDifference.ValueDifference<Object>> entry = itrDiff.next();
            MapDifference.ValueDifference<Object> value = entry.getValue();
            buf.append("<tr><td>")
                    .append(entry.getKey())
                    .append("</td><td><font color=\"red\">")
                    .append(value.leftValue())
                    .append("</font></td><td><font color=\"red\">")
                    .append(value.rightValue())
                    .append("</font></td></tr>");
        }

        buf.append("</table>" +
                "</body>" +
                "</html>");
        String html = buf.toString();

        FileWriter fWriter = null;
        BufferedWriter writer = null;
        try {
            fWriter = new FileWriter(reportFilePath);
            writer = new BufferedWriter(fWriter);
            writer.write(html);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void createSummaryHtmlpage(
            String executionStartTime, String actualFilePath, List<Integer> mismatchedLines, String reportFilePath) {

        StringBuilder buf = new StringBuilder();
        buf.append("<html>" + "<body>" + "<table border=\"1px\">")
                .append("<tr><td>")
                .append("Execution Start Time:")
                .append("</td><td>")
                .append(executionStartTime)
                .append("</td></tr>")
                .append("<tr><td>")
                .append("Generated Output File: ")
                .append("</td><td>")
                .append(actualFilePath)
                .append("</td></tr>");
        if (mismatchedLines.isEmpty()) {
            buf.append("<tr><td>")
                    .append(
                            "<font color=\"DarkGreen\">No mismatches are found in the output file with database.")
                    .append("</td><td>")
                    .append("   ")
                    .append("</font></td></tr>");
        } else {
            buf.append("<tr><td>")
                    .append("<font color=\"red\">THe line numbers in output file having mismatches from DB:")
                    .append("</td><td>")
                    .append(mismatchedLines.toString())
                    .append("</font></td></tr>");
        }
        buf.append("</table>").append("</body>").append("</html>");
        writeStringBuilderToFile(buf, reportFilePath);
    }

    private static final void writeStringBuilderToFile(StringBuilder sb, String fileName) {

        FileWriter fWriter = null;
        BufferedWriter writer = null;
        try {
            fWriter = new FileWriter(fileName);
            writer = new BufferedWriter(fWriter);
            writer.write(sb.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static final String getTimeInUTC() {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(new Date());
        DateFormat simpleDateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss.SSS");
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static void createSummaryHtmlpage(Map<String, String> summaryMap, String reportFilePath) {

        StringBuilder buf = new StringBuilder();
        buf.append("<html>" +
                "<body>" +
                "<table style=\"width: 300px; height: 100px;\" border=\"1px\"  cellspacing=\"0\" cellpadding=\"0\" >" +
                "<tr>" +
                "<th> CUSIP </th>" +
                "<th> Status </th>" +
                "</tr>");
        Iterator<Map.Entry<String, String>> itr = summaryMap.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            Object value = entry.getValue();
            buf.append("<tr><td>")
                    .append(entry.getKey());
            if ("Success".equalsIgnoreCase(value.toString())) {
                buf.append("</td><td><font color=\"DarkGreen\">");
                buf.append(value.toString());
            } else {
                buf.append("</td><td><font color=\"red\">");
                buf.append(value.toString());
            }
            buf.append("</font></td></tr>");
        }


        buf.append("</table>" +
                "</body>" +
                "</html>");
        String html = buf.toString();

        FileWriter fWriter = null;
        BufferedWriter writer = null;
        try {
            fWriter = new FileWriter(reportFilePath);
            writer = new BufferedWriter(fWriter);
            writer.write(html);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getDifferenceFromMapAndGenerateReport(File directoryPath, Map<String, Object> expectedData, Map<String, String> dbResult, String reportFilePath) {
        StringBuilder buf = new StringBuilder();
        buf.append("<html>" +
                "<body>" +
                "<h4> Input File Path : "+directoryPath+"</h4>"+
                "<h4> Database Instance : "+"UAT"+"</h4>"+
                "<table style=\"width: 600px; height: 100px;\" border=\"1px\"  cellspacing=\"0\" cellpadding=\"0\" >" +
                "<tr>" +
                "<th> Field Name </th>" +
                "<th> Expected Value </th>" +
                "<th> DB Result Value </th>" +
                "</tr>");
        Set<String> itr = expectedData.keySet();
        for (String key : itr) {
            String expectedValue = String.valueOf(expectedData.get(key));
            String dbValue = String.valueOf(dbResult.get(key));
            boolean isMatch = expectedValue.equalsIgnoreCase(dbValue);
            if(isMatch){
                buf.append("<tr><td>")
                        .append(key)
                        .append("</td><td><font color=\"DarkGreen\">")
                        .append(expectedValue)
                        .append("</font></td><td><font color=\"DarkGreen\">")
                        .append(dbValue)
                        .append("</font></td></tr>");
            }else{
                buf.append("<tr><td>")
                        .append(key)
                        .append("</td><td><font color=\"red\">")
                        .append(expectedValue)
                        .append("</font></td><td><font color=\"red\">")
                        .append(dbValue)
                        .append("</font></td></tr>");
            }
        }
        buf.append("</table>" +
                "</body>" +
                "</html>");
        String html = buf.toString();

        FileWriter fWriter = null;
        BufferedWriter writer = null;
        try {
            fWriter = new FileWriter(reportFilePath);
            writer = new BufferedWriter(fWriter);
            writer.write(html);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

