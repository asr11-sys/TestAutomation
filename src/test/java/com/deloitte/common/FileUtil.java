package com.deloitte.common;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class FileUtil {

    public static List<String>  getListOfElement(String filePath, String xpathQuery) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        List<String> listOfElement ;
        DocumentBuilder builder ;
        Document doc;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        builder = factory.newDocumentBuilder();
        doc = builder.parse(filePath);
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        listOfElement = getListOfValue(doc, xpath, xpathQuery);
        return  listOfElement;
    }

    private static  List<String> getListOfValue(Document doc, XPath xpath, String xpathQuery) throws  XPathExpressionException{
        List<String> list = new ArrayList<>();
        XPathExpression expr = xpath.compile(xpathQuery);
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i =0; i < nodes.getLength(); i++){
            list.add(nodes.item(i).getNodeValue());
        }
        return  list;
    }

}
