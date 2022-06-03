/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	07/08/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult.oxm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.stax2.XMLInputFactory2;

import com.amtsybex.fieldreach.utils.impl.Common;
import com.amtsybex.fieldreach.xmlloader.exception.scriptresult.ElementNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to aide with parsing a Script result XML file. This class support
 * parsing in parts as it can cause memory issues loading scripts result
 * documents entirely into memory.
 */
public class Parser {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory.getLogger(Parser.class
			.getName());
	DParser dParse = null;


	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public Parser() {

	}

	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	// FDP1165 TM 25/11/2015 - Modified method signature
	/**
	 * Parse and validate the specified Script Result file into memory using JAXB.
	 * 
	 * @param fileUri
	 * URI of the file to parse.
	 * 
	 * @param scriptResultSchema
	 * Script Result schema to use to validate the XML.
	 * 
	 * @return
	 * A ResultSet object representing the XML file specified.
	 * 
	 * @throws JAXBException
	 * An error occurs parsing the XML file.
	 * @throws UnsupportedEncodingException 
	 */
	public ResultSet parseJAXB(String fileUri, Schema scriptResultSchema) throws JAXBException, UnsupportedEncodingException  {
		
		if (log.isDebugEnabled())
			log.debug(">>> parseJe.AXB fileUri=" + fileUri);
		
		ResultSet resultSet = null;
		
		XMLStreamReader xmler = null;
		FileInputStream fis = null;
		
		try {
			
			// FDP1165 24/11/2015
			fis = new FileInputStream(FilenameUtils.normalizeNoEndSeparator(fileUri));
			
			XMLInputFactory xmlif = XMLInputFactory2.newInstance();
			xmler = xmlif.createXMLStreamReader(fis);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(ResultSet.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setSchema(scriptResultSchema);
			
			resultSet = (ResultSet) jaxbUnmarshaller.unmarshal(xmler);
			// End FDP1165
			
			// Now parse the extended element
			
			Extended ext = this.getExtendedElement(fileUri);
			
			//29886-Check if Result File is encrypted
			if(Common.decryptStr(resultSet.getScriptResults().getPROFILE().getGENERAL().getCOMPLETEDATE())!=null) {
				dParse = new DParser();
				resultSet=dParse.decryptResultSet(resultSet);
				ext = dParse.deExtended(ext);
				Common.secureWipeResource();
			}
			
			log.info("Not Decrypting >> ResultSet Completed Date is +"+resultSet.getScriptResults().getPROFILE().getGENERAL().getCOMPLETEDATE());
			resultSet.setExtended(ext);
			
			// FDP1100 TM 19/05/2015

			// When elements are contained in the Extended and General section
			// elements found in the General section take precedence.
			for (String elementName : resultSet.getScriptResults().getPROFILE().getGENERAL().getElementNames()) {

				// Check if a value exists in the Extended section.
				String elementValue = resultSet.getScriptResults().getPROFILE().getEXTENDED().getValue(elementName);

				if (elementValue != null) {

					try {

						// Value exists in the Extended section, now check to
						// see if there is a value
						// in the General section. If there isn't, use the value
						// found in the Extended
						// section.
						Class<?> cls = Class.forName("com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.General");

						Method getter = cls.getDeclaredMethod("get" + elementName);

						String generalVal = null;
						generalVal = (String) getter.invoke(resultSet.getScriptResults().getPROFILE().getGENERAL());

						if (generalVal == null) {

							Method setter = cls.getDeclaredMethod("set" + elementName, new Class[] { String.class });

							setter.invoke(resultSet.getScriptResults().getPROFILE().getGENERAL(), elementValue);
						}

					} catch (Exception e) {

						log.error(e.getMessage());
					}
				}
			}

			// End FDP1100
			
		} catch (XMLStreamException e) {
			
			throw new JAXBException(e);
		
		} catch (ElementNotFoundException e) {
			
			throw new JAXBException(e);
			
		} catch (FileNotFoundException e) {
			
			throw new JAXBException(e);
			
		} finally {
			
			try {
				
				if (xmler != null)xmler.close();
				if (fis != null) fis.close();
					
			} catch (XMLStreamException e) {

			} catch (IOException e) {

			}
			
		}
		
		if (log.isDebugEnabled())
			log.debug("<<< parseJAXB");
		
		return resultSet;
	}
	
	
	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/
	
	/**
	 * Get the 'EXTENDED' element of a script result XML file.
	 * 
	 * @param fileUri
	 *            Location of the script result file to parse.
	 * 
	 * @return EXTENDED element of the script result file specified.
	 * 
	 * @throws XMLStreamException
	 * @throws JAXBException
	 * @throws ElementNotFoundException
	 */
	private Extended getExtendedElement(String fileUri)
			throws XMLStreamException, JAXBException, ElementNotFoundException {

		if (log.isDebugEnabled())
			log.debug(">>> getExtendedElement fileUri=" + fileUri);

		FileInputStream fis = this.createFis(fileUri);

		XMLStreamReader xsr = this
				.getXMLStreamReader(fis, XmlElements.EXTENDED);

		Extended extended = new Extended();
		int eventType;

		while (xsr.hasNext()) {

			eventType = xsr.next();

			if (eventType == XMLStreamReader.START_ELEMENT) {

				extended.setValue(xsr.getLocalName(), xsr.getElementText());

			} else if (eventType == XMLStreamReader.END_ELEMENT
					&& xsr.getLocalName().equals(XmlElements.EXTENDED)) {

				break;
			}
		}

		xsr.close();

		if (fis != null)
			IOUtils.closeQuietly(fis);

		if (log.isDebugEnabled())
			log.debug("<<< getExtendedElement");

		return extended;
	}
	
	/**
	 * Get an XMLStreamReader object containing the specified element. Supports
	 * parsing a file in parts rather than loading the whole document into
	 * memory.
	 * 
	 * @param fis
	 *            FileInputStream pointing to file ot be parsed.
	 * 
	 * @param elementName
	 *            Element to search for in the file.
	 * 
	 * @return XMLStreamReader object containing the supplied element.
	 * 
	 * @throws XMLStreamException
	 * @throws ElementNotFoundException
	 */
	private XMLStreamReader getXMLStreamReader(FileInputStream fis,
			String elementName) throws XMLStreamException,
			ElementNotFoundException {

		if (log.isDebugEnabled())
			log.debug(">>> getXMLStreamReader fis=XXX elementName="
					+ elementName);

		XMLStreamReader xsr = null;

		try {

			XMLInputFactory xif = XMLInputFactory2.newInstance();

			xsr = xif.createXMLStreamReader(fis);

			String currentElement = "";
			int eventType;

			while (xsr.hasNext()) {

				eventType = xsr.next();

				if (eventType == XMLStreamReader.START_ELEMENT
						&& elementName.equals(xsr.getLocalName()))
					currentElement = xsr.getLocalName();

				if (currentElement.equals(elementName))
					break;
			}

			if (currentElement.equals("")) {

				String msg = "End of document reached element not found: "
						+ elementName;

				log.error(msg);

				throw new ElementNotFoundException(msg);
			}

		} catch (XMLStreamException e) {

			if (xsr != null)
				xsr.close();

			if (fis != null)
				IOUtils.closeQuietly(fis);

			throw e;
		}

		if (log.isDebugEnabled())
			log.debug("<<< getXMLStreamReader");

		return xsr;
	}
	
	/**
	 * Private method to create a FileInputStream object using the file uri
	 * supplied.
	 * 
	 * @param fileUri
	 * @return
	 * @throws XMLStreamException
	 */
	private FileInputStream createFis(String fileUri) throws XMLStreamException {

		FileInputStream fis = null;

		try {

			fis = new FileInputStream(
					FilenameUtils.normalizeNoEndSeparator(fileUri));

		} catch (FileNotFoundException e) {

			throw new XMLStreamException(e);
		}

		return fis;
	}

}