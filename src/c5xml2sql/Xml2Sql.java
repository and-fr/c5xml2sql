package c5xml2sql;

import static c5xml2sql.App.increaseProgress;
import static c5xml2sql.App.sqlCode;
import static c5xml2sql.App.xmlFiles;
import java.io.*;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

public class Xml2Sql {

    private static String detail;
    
    
    
    public static String convert(String xmlString) {

        List<String> tables = new ArrayList<>();
        
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
	DocumentBuilder b;
	Document doc = null;

        try {
            b = f.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            System.out.println("EX_PCE: " + ex);
            return detail = "ERR_NO_PROPER_XML_STRUCTURE";
        }
        
        
        if(b != null){
            try {
                doc = b.parse(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
            } catch (UnsupportedEncodingException ex) {
                System.out.println("EX_UEE: " + ex);
                return detail = "ERR_NO_PROPER_XML_STRUCTURE\n" + ex.toString() + "\n";
            } catch (SAXException | IOException ex) {
                System.out.println("EX_SAX_IO: " + ex);
                return detail = "ERR_NO_PROPER_XML_STRUCTURE\n" + ex.toString() + "\n";
            }
        }

        
        if(doc == null){
            System.out.println("DOC_NULL");
            return detail = "ERR_NO_XML_CONTENT";
        } else {
            detail = "OK";
            Element rootElement = doc.getDocumentElement();
        
            if (rootElement.getNodeName().equals("GameData")){

                // iterating through nodes and generating table list
                NodeList nodeList = doc.getDocumentElement().getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node instanceof Element) {
                        Element childElement = (Element) node;
                        String tableName = childElement.getTagName();

                        tables.add(tableName);
                    }
                }

                // iterate through table nodes
                tables.forEach((String table) -> {
                    // System.out.println(table);
                    Map<String, String> data = new HashMap<>();

                    NodeList nodeTableRoot = rootElement.getElementsByTagName(table).item(0).getChildNodes();
                    for (int i = 0; i < nodeTableRoot.getLength(); i++) {
                        Node node = nodeTableRoot.item(i);

                        // processing Rows for Inserts
                        if (node.getNodeName().equals("Row")) {
                            // System.out.println(node.getNodeName());
                            if (node instanceof Element) {
                                // System.out.println("  " + node.getNodeName());
                                NodeList nodeData;
                                nodeData = node.getChildNodes();
                                for (int j = 0; j < nodeData.getLength(); j++) {
                                    Node node2 = nodeData.item(j);
                                    if (node2 instanceof Element) {
                                        // System.out.println("    " + node2.getNodeName() + " : " + node2.getTextContent());

                                        String column = node2.getNodeName();
                                        String value = node2.getTextContent();

                                        data.put(column, value);
                                    }
                                }

                                String columns = "";
                                String values = "";

                                for (String key : data.keySet()) {
                                    String value = data.get(key);
                                    columns += key + ", ";
                                    values += wrapInQuotes(value) + ", ";
                                    // System.out.println(value + " : " + key);
                                }

                                columns = columns.substring(0, columns.length() - 2);
                                values = values.substring(0, values.length() - 2);

                                App.sqlCode += "\n\nINSERT INTO " + table + " (" + columns + ") VALUES\n" +
                                        "(" + values + ");";
                            }
                        } // end of processing rows for Inserts
                        
                        
                        // processing update nodes for updates
                        if (node.getNodeName().equals("Update")){
                            String setStr = "";
                            String whereStr = "";

                            // System.out.println(node.getNodeName());
                            if (node instanceof Element) {
                                // System.out.println("  " + node.getNodeName());
                                NodeList nodeData;
                                nodeData = node.getChildNodes();
                                for (int j = 0; j < nodeData.getLength(); j++) {
                                    Node node2 = nodeData.item(j);
                                    if (node2 instanceof Element) {
                                        // System.out.println("    " + node2.getNodeName() + " : " + node2.getTextContent());

                                        if (node2.getNodeName().equals("Where")){
                                            String column = "";
                                            String value = "";
                                            NamedNodeMap innerElmnt_gold_attr = node2.getAttributes();
                                            for (int k = 0; k < innerElmnt_gold_attr.getLength(); k++)
                                            {
                                                Node attr = innerElmnt_gold_attr.item(k);
                                                column = attr.getNodeName();
                                                value = attr.getNodeValue();
                                                // System.out.println(attr.getNodeName() + " = \"" + attr.getNodeValue() + "\"");
                                            }
                                            whereStr = column + " = " + wrapInQuotes(value);
                                            // System.out.println(whereStr);
                                        }

                                        if (node2.getNodeName().equals("Set")){
                                            String column;
                                            String value;
                                            String valuesList = "";
                                            NamedNodeMap innerElmnt_gold_attr = node2.getAttributes();
                                            
                                            // System.out.println(innerElmnt_gold_attr.getLength());
                                            
                                            if (innerElmnt_gold_attr.getLength() > 0) {
                                                for (int k = 0; k < innerElmnt_gold_attr.getLength(); k++)
                                                {
                                                    Node attr = innerElmnt_gold_attr.item(k);
                                                    column = attr.getNodeName();
                                                    value = attr.getNodeValue();
                                                    value = wrapInQuotes(value);
                                                    valuesList += column + " = " + value + ",\n";
                                                    // System.out.println(attr.getNodeName() + " = \"" + attr.getNodeValue() + "\"");
                                                }
                                                setStr = valuesList.substring(0, valuesList.length() - 2);
                                                // System.out.println(setStr);
                                            } else {
                                                setStr = "";
                                                detail = "ERR_NO_CORRECT_SET_ATTRIBUTES";
                                            }
                                        }
                                    }
                                }
                                String updateStr = "";
                                if (!setStr.isEmpty()) {
                                    updateStr = "\n\nUPDATE " + table + " SET\n" + setStr + "\nWHERE " + whereStr + ";";
                                }
                                App.sqlCode += updateStr;
                                // System.out.println(updateStr);
                            }
                        } // end of processing update nodes for updates

                    }
                    // C5xml2sql.sqlString += "\n\n\n";

                    // System.out.println(sqlString);
                    // System.out.println(data);
                });

            } else {
                detail = "ERR_NO_GAMEDATA_BLOCK";
            }

        }

        return detail;
    }

    
    
    public static void parse() {
        final AtomicInteger totalCounter = new AtomicInteger(0);
        final StringProperty status = new SimpleStringProperty();
        
        Log.add("\n\n\nProcessing XML files:\n");
        
        xmlFiles.forEach((String file) -> {
            sqlCode += "\n\n\n-- " + file;
            
            totalCounter.getAndIncrement();
            
            // System.out.println(totalCounter.toString() + " " + file);
            status.setValue( convert(File.getContent(file)) );
            
            Log.add("\n" + totalCounter.toString() + " " + status.getValue() + " " + file);
            increaseProgress();
            
            try {
                Thread.sleep(250); 
            } catch (InterruptedException e) {
                Log.add(e.toString());
            }
        });

        // Log.add(logString.toString());
        Log.add("\n\nFinished processing XML files.\n\n\n");

        // System.out.println(sqlCode);
    }

        
        
        
        
    
    public static String wrapInQuotes (String str)
    {
        // match a number with optional '-' and decimal in given string
        // if numeric return it, if text add quotes
        
        String value;
        
        if (str.matches("-?\\d+(\\.\\d+)?")){
            value = str;
        } else {
            value = "'" + str + "'";
        }
        
        return value;
    }
    
    
    
}
