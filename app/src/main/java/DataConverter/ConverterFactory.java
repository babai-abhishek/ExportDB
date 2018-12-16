package DataConverter;

import android.content.Context;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * this is a public java class that will return types of export and the object of converter class.
 * Java Reflection API and XML SAX parser has been used upon Config.xml file to get the export types
 * and their respective class name.
 */
public class ConverterFactory {

    //context
    Context context = null;

    //Constructor to get the context
    public ConverterFactory(Context ctx) {
        this.context = ctx;
    }

    /*this is a public method,
    it will return the object of converter classes*/
    public IConverter getConverter(String exportType) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        Class c = null;
        IConverter ic = null;

        /*class name of the export type*/
        String ConverterClass = getConverterClassName(exportType);

        /*object creation using refletion API of the converter classes*/
        c = Class.forName("DataConverter." + ConverterClass);

        /*interface reference variable can refer to a subclass object*/
        ic = (IConverter) c.newInstance();

        /*return the object of the coverter class*/
        return ic;
    }

    /*this is a private class
    * used to return the desired converter class name of the selected export type
    * from the Config.xml file ,
    * we have used SAX parser to parse the xml file*/
    private String getConverterClassName(final String exportType) throws ParserConfigurationException, SAXException {

        final String[] convertclassName = {null};
            SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();
            final DefaultHandler defaultHandler = new DefaultHandler() {
            boolean className = false;
            String type;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

                if (qName.equalsIgnoreCase("converter") && exportType.equals(attributes.getValue("type"))) {
                    type = attributes.getValue("type");
                } else if (qName.equalsIgnoreCase("class-name") && exportType.equals(type)) {
                    className = true;

                }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {

                if (className && exportType.equals(type)) {
                    className = false;
                    convertclassName[0] =  new String(ch, start, length);
                    type = "";
                }
            }

        };

        try {
            InputStream is = context.getAssets().open("Config.xml");
            saxParser.parse(is, defaultHandler);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertclassName[0];
    }


    /*this a public method
    * that will return the list of export type to display on the screen*/
    public ArrayList<String> getExportType() {

        try {
            return getConvertTypeName();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /*this is a private method,
    * it will return the list of export types collected from Config.xml file*/
    private ArrayList<String> getConvertTypeName() throws ParserConfigurationException, SAXException, IOException {


        //arraylist to store export types
        final ArrayList<String> alist = new ArrayList<String>();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        final SAXParser saxParser = factory.newSAXParser();

        final DefaultHandler defaultHandler = new DefaultHandler() {

            String type;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (qName.equalsIgnoreCase("converter")) {
                    type = attributes.getValue("type");
                    alist.add(type);
                }
            }

        };

        try {
            InputStream is = context.getAssets().open("Config.xml");
            saxParser.parse(is, defaultHandler);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return alist;
    }
}
