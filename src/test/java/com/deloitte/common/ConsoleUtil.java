package com.deloitte.common;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class ConsoleUtil {

    public static  String printTable(List<String> headers, Map<String, String> actual, Map<String, String> expected){
        if(headers.isEmpty())
            return "\n\t-- EMPTY HEADERS --\t\n";

            StringBuilder result = new StringBuilder();
            String tablePrefix = "\n\n";
            StringBuilder headerBuff = new StringBuilder("| Headers \t |");
            StringBuilder line1 = new StringBuilder("| Actual \t |");
            StringBuilder line2 = new StringBuilder("| Expected \t |");
            StringBuilder divider = new StringBuilder();

            for(String s : headers){
                headerBuff.append(String.format("%-25s", s)).append(" | ");
                line1.append(String.format("%-25s", actual.get(s))).append(" | ");
                line2.append(String.format("%-25s", expected.get(s))).append(" | ");
            }

            String tableTrailer = "\n\n" ;

            int length = Math.max(line1.length(), line2.length());
            for (int i = 0; i < length; i++)
                divider.append("-");

            return result.append(tablePrefix)
                    .append(headerBuff).append("\n")
                    .append(divider).append("\n")
                    .append(line1).append("\n")
                    .append(divider).append("\n")
                    .append(line2).append("\n")
                    .append(tableTrailer).append("\n")
                    .toString();
    }

    public static String printTable(List<String>headers, List<Map<String, String>> rows){
        if(headers.isEmpty())
            return "\n\t-- EMPTY HEADERS --\t\n";

        StringBuilder result = new StringBuilder();
        String tablePrefix = "\n\n";
        StringBuilder headersBuff = new StringBuilder(" | ");
        StringBuilder line1 = new StringBuilder(" | ");
        StringBuilder divider = new StringBuilder();

        for(String s : headers){
            headersBuff.append(String.format("%-25s", s)).append(" | ");
        }

        int lenght = Math.max(line1.length(), headersBuff.length());
        for (int i = 0 ; i < lenght; i++)
            divider.append("-");

        for(Map<String, String>row : rows){
            for(String s : headers){
                line1.append(String.format("%-25s", row.get(s))).append(" | ");
            }
            line1.append("\n").append(divider).append("\n");
        }

        return result.append(tablePrefix)
                .append(headersBuff).append("\n")
                .append(divider).append("\n")
                .append(line1)
                .append("\n\n")
                .toString();
    }

    public static String printTwoResultSets(String resultSetOneAlias, List<Map<String, String>> resultSetOne, String resultSetTwoAlias, List<Map<String, String>> resultSetTwo, String errorDetail){
        StringBuilder toPrint = new StringBuilder().append("\n\n")
                .append(errorDetail).append("\n")
                .append(resultSetOneAlias).append("\n")
                .append(resultSetOne).append("\n\n")
                .append(resultSetTwoAlias).append("\n")
                .append(resultSetTwo).append("\n\n");

        return toPrint.toString();
    }

}
