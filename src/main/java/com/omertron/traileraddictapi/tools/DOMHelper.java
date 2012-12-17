/*
 *      Copyright (c) 2004-2012 Stuart Boston
 *
 *      This software is licensed under a Creative Commons License
 *      See the LICENCE.txt file included in this package
 *
 *      For any reuse or distribution, you must make clear to others the
 *      license terms of this work.
 */
package com.omertron.traileraddictapi.tools;

import com.omertron.traileraddictapi.TrailerAddictException;
import com.omertron.traileraddictapi.TrailerAddictException.TrailerAddictExceptionType;
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Generic set of routines to process the DOM model data
 *
 * @author Stuart.Boston
 *
 */
public class DOMHelper {

    private static final Logger LOGGER = Logger.getLogger(DOMHelper.class);
    private static final String YES = "yes";
    private static final String ENCODING = "UTF-8";

    // Hide the constructor
    protected DOMHelper() {
        // prevents calls from subclass
        throw new UnsupportedOperationException("Class can not be initialised!");
    }

    /**
     * Gets the string value of the tag element name passed
     *
     * @param element
     * @param tagName
     * @return
     */
    public static String getValueFromElement(Element element, String tagName) {
        NodeList elementNodeList = element.getElementsByTagName(tagName);
        if (elementNodeList == null) {
            return "";
        } else {
            Element tagElement = (Element) elementNodeList.item(0);
            if (tagElement == null) {
                return "";
            }

            NodeList tagNodeList = tagElement.getChildNodes();
            if (tagNodeList == null || tagNodeList.getLength() == 0) {
                return "";
            }
            return ((Node) tagNodeList.item(0)).getNodeValue();
        }
    }

    /**
     * Get a DOM document from the supplied URL
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static synchronized Document getEventDocFromUrl(String url) throws TrailerAddictException {
        String webPage;
        InputStream in = null;

        try {
            webPage = WebBrowser.request(url);
            in = new ByteArrayInputStream(webPage.getBytes(ENCODING));
        } catch (UnsupportedEncodingException ex) {
            throw new TrailerAddictException(TrailerAddictExceptionType.INVALID_URL, "Unable to encode URL: " + url, ex);
        }

        Document doc = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            doc = db.parse(in);
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException error) {
            throw new TrailerAddictException(TrailerAddictExceptionType.PARSE_ERROR, "Unable to parse TheTVDb response, please try again later.", error);
        } catch (SAXException error) {
            throw new TrailerAddictException(TrailerAddictExceptionType.PARSE_ERROR, "Unable to parse TheTVDb response, please try again later.", error);
        } catch (IOException error) {
            throw new TrailerAddictException(TrailerAddictExceptionType.PARSE_ERROR, "Unable to parse TheTVDb response, please try again later.", error);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException error) {
                    // Input Stream was already closed or null
                }
            }
        }

        return doc;
    }

    /**
     * Convert a DOM document to a string
     *
     * @param doc
     * @return
     * @throws TransformerException
     */
    public static String convertDocToString(Document doc) throws TransformerException {
        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, YES);
        trans.setOutputProperty(OutputKeys.INDENT, YES);

        //create string from xml tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);
        return sw.toString();
    }

    /**
     * Write the Document out to a file using nice formatting
     *
     * @param doc The document to save
     * @param localFile The file to write to
     * @return
     */
    public static boolean writeDocumentToFile(Document doc, String localFile) {
        try {
            TransformerFactory transfact = TransformerFactory.newInstance();
            Transformer trans = transfact.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, YES);
            trans.setOutputProperty(OutputKeys.INDENT, YES);
            trans.transform(new DOMSource(doc), new StreamResult(new File(localFile)));
            return true;
        } catch (TransformerConfigurationException error) {
            LOGGER.warn("Error writing the document to " + localFile);
            LOGGER.warn("Message: " + error.getMessage());
            return false;
        } catch (TransformerException error) {
            LOGGER.warn("Error writing the document to " + localFile);
            LOGGER.warn("Message: " + error.getMessage());
            return false;
        }
    }

    /**
     * Add a child element to a parent element
     *
     * @param doc
     * @param parentElement
     * @param elementName
     * @param elementValue
     */
    public static void appendChild(Document doc, Element parentElement, String elementName, String elementValue) {
        Element child = doc.createElement(elementName);
        Text text = doc.createTextNode(elementValue);
        child.appendChild(text);
        parentElement.appendChild(child);
    }

}