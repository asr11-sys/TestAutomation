package com.deloitte.common;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class ComparisionUtil {

    private static final SpelParserConfiguration PARSER_CONFIG = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE,
            ComparisionUtil.class.getClassLoader());

    private static  final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser(PARSER_CONFIG);

    private static Map<String, Object> EXPRESSION_VARIABLES = new HashMap<>();

    static {
        for (Method declaredMethod : ComparisionUtilFunctions.class.getDeclaredMethods()){
            EXPRESSION_VARIABLES.put(declaredMethod.getName(), declaredMethod);
        }

        for(Method declaredMethod : StringUtils.class.getDeclaredMethods()){
            EXPRESSION_VARIABLES.put(declaredMethod.getName(), declaredMethod);
        }
    }

    public static List<String> compareStringMaps(Map<String,String>expected , Map<String,String>actual,
                                                 boolean isCaseSensitive){
        String actualFieldValue, expectedFieldValue;
        List<String> diff = new ArrayList<>();
        expected.remove(null);

        for(String key : expected.keySet()){
            if(key.startsWith("*"))
                continue;

            actualFieldValue = actual.get(key) != null ? actual.get(key).trim() : "";
            expectedFieldValue = expected.get(key) != null ? expected.get(key).trim()  : "";

            Object eval = evaluateExpression(expectedFieldValue, actualFieldValue, actual, expected);

            if(eval instanceof  Boolean) {
                if (Boolean.TRUE.equals(eval))
                    expectedFieldValue = actualFieldValue;
                else
                    expectedFieldValue = "[FAILED]";
            } else if (eval instanceof  String)
                expectedFieldValue = eval.toString();
             else
                expectedFieldValue = eval.toString();

             if(isCaseSensitive){
                 if(!actualFieldValue.equals(expectedFieldValue))
                     diff.add(key);
             } else {
                 if(!actualFieldValue.equalsIgnoreCase(expectedFieldValue)
                 && !((actualFieldValue.equalsIgnoreCase("")) && expectedFieldValue.equalsIgnoreCase("null")))
                 diff.add(key);
             }
        }
        return diff ;
    }

    public  static Object evaluateExpression(String expectedFieldValueExpression,
                                             String actualFieldValue,
                                             Map<String,String>actaul , Map<String,String>expected){

        return expectedFieldValueExpression.contains("#") ?
                evaluateExpressionInternal(expectedFieldValueExpression, actualFieldValue, actaul, expected) :
                expectedFieldValueExpression ;
    }

    private  static  Object evaluateExpressionInternal(String expectedFieldValueExpression,
                                                       String actualFieldValue,
                                                       Map<String,String>actual, Map<String,String>expected){
        boolean isTemplateAware = expectedFieldValueExpression.contains("#{");

        Expression exp = isTemplateAware ? EXPRESSION_PARSER.parseExpression(expectedFieldValueExpression, ParserContext.TEMPLATE_EXPRESSION)
                : EXPRESSION_PARSER.parseExpression(expectedFieldValueExpression);

        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariable("expected", expected);
        evaluationContext.setVariable("actual",actual);
        evaluationContext.setVariable("val",actualFieldValue);
        evaluationContext.setVariable("actualVal", actualFieldValue);
        evaluationContext.setVariables(EXPRESSION_VARIABLES);

        try{
            return exp.getValue(evaluationContext);
        } catch (Exception ex){
            throw ex;
        }
    }


    public static List<String> compareStringMaps(Map<String,String> expected, Map<String,String> actual, String ... ignoreFields){
        Map<String,String> modifiefExpected = new LinkedHashMap<>(expected);
        Lists.newArrayList(ignoreFields).forEach(modifiefExpected::remove);
        return compareStringMaps(modifiefExpected, actual, false);
    }


}
