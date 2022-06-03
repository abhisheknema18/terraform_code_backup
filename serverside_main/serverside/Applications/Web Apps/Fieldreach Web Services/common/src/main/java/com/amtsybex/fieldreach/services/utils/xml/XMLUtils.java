package com.amtsybex.fieldreach.services.utils.xml;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Class to provide a facility to create XML documents. 
 * This is designed to promote code reuse.
 */
public class XMLUtils 
{
	static Logger log = LoggerFactory.getLogger(XMLUtils.class.getName());
	
	private DocumentBuilderFactory domFactory;
		
	
	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/
	
	/**
	 * Default constructor.
	 */
	public XMLUtils()
	{
		domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); 
		
	}
	
	/**
	 * Constructor to use when any XMl parsed by this class needs to be validated against
	 * a schema.
	 * 
	 * @param schema
	 * An XSD schema to be used when validating XML.
	 * 
	 */
	public XMLUtils(Schema schema)
	{
		domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); 
    	domFactory.setSchema(schema);
	}
	
	
	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/
	
	
	/**
	 * Takes the XML string passed in and parses it into an XML document. If a schema has
	 * been set validation will also be carried out.
	 * 
	 * @param xml
	 * String containing the XML to be parsed.
	 * 
	 * @return
	 * A Document object produced by paring the string supplied.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document parseXML(String xml) 
			throws ParserConfigurationException, SAXException, IOException
	{
		// Parse and validate the document if a schema has been specified
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		builder.setErrorHandler(new validationErrorHandler());
		
		InputSource is = new InputSource(new StringReader(xml));
		
		Document doc = builder.parse(is);
		
		return doc;
	}
	
	
	/**
	 * Takes the XML document passed in and evaluates the xpath expression to a string 
	 * supplied using this document. If multiple instances of the element are found,
	 * the first one is used.
	 * 
	 * @param doc
	 * XML document to evaluate xpath expression against.
	 * 
	 * @param xpathExpr
	 * Xpath expression to evaluate.
	 * 
	 * @return
	 * Returns the evaluated XPath expression as a string.
	 */
	public String evaluateXPathToString(Document doc, String xpathExpr)
	{
		log.debug(">>> evaluateXPathToString xpathExpr=" + xpathExpr);
		
		String result = null;
		
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		
		XPathExpression expr;
		
		try 
		{
			expr = xpath.compile(xpathExpr);
			NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			
			if(nodes.getLength() > 0)
				result = nodes.item(0).getTextContent();
		} 
		catch (XPathExpressionException e) 
		{
			log.debug("error evaluating xpath expression: " + xpathExpr);
		}
		
		log.debug("<<< evaluateXPathToString");
		
		return StringUtils.isEmpty(result) ? null : result;
	}
	
}


// Class to handle validation errors when validating xml against an xsd
class validationErrorHandler implements ErrorHandler
{
	static Logger log = LoggerFactory.getLogger(validationErrorHandler.class.getName());
	
	
	public void error(SAXParseException e) throws SAXException 
	{
		log.error(e.getMessage());
		throw new SAXException(e.getMessage());
	}

	
	public void fatalError(SAXParseException e) throws SAXException 
	{
		log.error(e.getMessage());
		throw new SAXException(e.getMessage());
	}

	
	public void warning(SAXParseException e) throws SAXException 
	{
		log.error(e.getMessage());
		throw new SAXException(e.getMessage());
	}
	
}