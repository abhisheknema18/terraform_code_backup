package com.amtsybex.fieldreach.fdm.admin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.ConfigurationFiles;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.services.utils.xml.XMLUtils;
import com.amtsybex.fieldreach.utils.impl.Common;

@WindowScoped
@Named
public class ConfigurationManagement extends PageCodebase implements Serializable {

	private static final long serialVersionUID = 8915560834633505003L;

	private static Logger _logger = LoggerFactory.getLogger(ConfigurationManagement.class);

	private List<ConfigurationFiles> viewWMConfigList;

	private List<ConfigurationFiles> viewScriptConfigList;

	private List<ConfigurationFiles> filteredWMConfigList;

	private List<ConfigurationFiles> filteredScriptConfigList;

	private ConfigurationFiles selectedWMConfig;

	private ConfigurationFiles selectedScriptConfig;
	
	private String activeTab;
	
	private ConfigurationFiles uploadFile;
	
	public void onTabChange(TabChangeEvent event) {

		if (event.getTab().getId().equals("wmConfigFilesSearchResults")) {
			this.activeTab = "WMConfig";
		} else if ((event.getTab().getId().equals("scriptConfigFilesSearchResults"))) {
			this.activeTab = "ScriptConfig";
		}
	}

	public void viewConfig() {

		try {
			List<ConfigurationFiles> viewConfigList = this.getConfigurationService().getConfigurationFiles(null);

			Map<String, List<ConfigurationFiles>> mapView = viewConfigList.stream()
					.collect(Collectors.groupingBy(ConfigurationFiles::getConfigFileType));

			this.viewWMConfigList = this.filteredWMConfigList = mapView.get("WMCONFIG");

			this.viewScriptConfigList = this.filteredScriptConfigList = mapView.get("SCRIPTCONFIG");

		} catch (FRInstanceException e) {

			_logger.error(e.getMessage());
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			return ;
		}
		
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "configurationFilesList");
	}

	public StreamedContent downloadSelectedFile(ConfigurationFiles configFile) {
	
		StreamedContent file = null;
		Blob xmlFile = configFile.getConfigFile();
		
		if (Common.blobToByteArray(xmlFile) == null) {
			
			ConfigurationFiles cf=null;
			
			if(this.activeTab.equalsIgnoreCase("WMConfig")) {
				cf = this.viewWMConfigList.stream().filter(cfs -> cfs.getConfigFileName().equals(configFile.getConfigFileName())).findFirst().orElse(null);
				this.setSelectedWMConfig(configFile);
				
			}else {
				cf = this.viewScriptConfigList.stream().filter(cfs -> cfs.getConfigFileName().equals(configFile.getConfigFileName())).findFirst().orElse(null);
				this.setSelectedScriptConfig(configFile);
			}
			xmlFile = cf.getConfigFile();
		}
		byte[] bytes = Common.blobToByteArray(xmlFile);
		file = DefaultStreamedContent.builder().contentType("application/xml").name(configFile.getConfigFileName())
				.stream(() -> new ByteArrayInputStream(bytes)).build();

		return file;

	}

	public void validateConfigFile(FileUploadEvent event) {
		// Get uploaded file from the FileUploadEvent
		try {
			byte[] bytes = IOUtils.toByteArray(event.getFile().getInputStream());

			String xmlData = new String(bytes, StandardCharsets.UTF_8);

			Document doc = null;
			boolean isExistingFile=false;
			String workGroup = null;

			String filename = event.getFile().getFileName();

			ConfigurationFiles uploadFile = new ConfigurationFiles();
			uploadFile.setConfigFileName(filename);
			uploadFile.setPublishDate(Common.generateFieldreachDBDate());
			uploadFile.setPublishTime(StringUtils.leftPad(String.valueOf(Common.generateFieldreachDBTime()), 6, '0'));
			uploadFile.setSystemUser(getUserDetails()+" ("+ getUsername()+")");
			uploadFile.setConfigFile(new SerialBlob(bytes));

			uploadFile.setCheckSum(Common.generateSHA512Checksum(bytes));

			if (this.activeTab.equalsIgnoreCase("WMConfig") && filename.toUpperCase().startsWith("WMCONFIG")) {
				uploadFile.setConfigFileType("WMCONFIG");
				if(!filename.toUpperCase().equals("WMCONFIG.XML")) {
					
					workGroup=filename.substring(("WMCONFIG_").length(),filename.toUpperCase().indexOf(".XML"));
				}
				doc = WRONGparseAndValidateXml(xmlData, "WMConfig-FieldSmartBase.xsd");
				if (viewWMConfigList == null) {
					viewWMConfigList = new ArrayList<ConfigurationFiles>();
				}
				if (viewWMConfigList.stream().anyMatch(wm -> wm.getConfigFileName().equalsIgnoreCase(filename))) {
					isExistingFile = true;
				}

			} else if (this.activeTab.equalsIgnoreCase("ScriptConfig") && filename.toUpperCase().startsWith("SCRIPTCONFIG")) {
				uploadFile.setConfigFileType("SCRIPTCONFIG");
				if (!filename.toUpperCase().equals("SCRIPTCONFIG.XML")) {

					workGroup = filename.substring(("SCRIPTCONFIG_").length(),filename.toUpperCase().indexOf(".XML"));
				}
				doc = PROPERWAYparseAndValidateXml(xmlData, "ScriptConfig-FieldSmartBase.xsd");
				if (viewScriptConfigList == null) {
					viewScriptConfigList = new ArrayList<ConfigurationFiles>();
				}
				if (viewScriptConfigList.stream().anyMatch(sc -> sc.getConfigFileName().equalsIgnoreCase(filename))) {
					isExistingFile = true;
				}
			} else {
				_logger.error("Invalid file name/type");
				MessageHelper.setGlobalWarnMessage(this.activeTab +" file is not valid.");
				return;
			}
			if (workGroup != null) {
				HPCWorkGroups hpcWg = this.getUserService().findHPCWorkGroup(null, workGroup);
				if(hpcWg!=null) {
					uploadFile.setWorkGroupCode(workGroup);
				}else {
					throw new FRInstanceException("Invalid work group: "+workGroup);
				}
			}

			if (doc != null) {
				this.setUploadFile(uploadFile);

				if(isExistingFile){
					PrimeFaces.current().executeScript("PF('replaceConfigDialog').show();");
				}else{
					uploadConfigFile();
				}

			} else {
				MessageHelper.setGlobalWarnMessage(this.activeTab +" file is not valid.");
				return;
			}

		} catch (IOException e) {
			_logger.error(e.getMessage());
			MessageHelper.setGlobalWarnMessage(this.activeTab +" file is not valid.");
			return;
		} catch (SerialException e) {
			_logger.error(e.getMessage());
			MessageHelper.setGlobalWarnMessage(this.activeTab +" file is not valid.");
			return;
		} catch (SQLException e) {
			_logger.error(e.getMessage());
			MessageHelper.setGlobalWarnMessage(this.activeTab +" file is not valid.");
			return;
		} catch (SAXException e) {
			_logger.error(e.getMessage());
			MessageHelper.setGlobalWarnMessage(this.activeTab +" file is not valid.");
			return;
		} catch (ParserConfigurationException e) {
			_logger.error(e.getMessage());
			MessageHelper.setGlobalWarnMessage(this.activeTab +" file is not valid.");
			return;
		} catch (FRInstanceException e) {
			_logger.error(e.getMessage());
			MessageHelper.setGlobalWarnMessage("Target Work Group does not exist");
			return;
		}
	}
	
	public void uploadConfigFile() {
		try {
			this.getConfigurationService().saveConfigFiles(null, uploadFile);
			if (this.activeTab.equalsIgnoreCase("ScriptConfig")) {
				this.viewScriptConfigList.removeIf(s->s.getConfigFileName().equalsIgnoreCase(uploadFile.getConfigFileName()));
				this.viewScriptConfigList.add(this.uploadFile);
				this.filteredScriptConfigList = this.viewScriptConfigList;
				
			} else {
				this.viewWMConfigList.removeIf(wm->wm.getConfigFileName().equalsIgnoreCase(uploadFile.getConfigFileName()));
				this.viewWMConfigList.add(this.uploadFile);
				this.filteredWMConfigList = this.viewWMConfigList;
			}
			MessageHelper.setGlobalInfoMessage("Configuration File Uploaded.");
		} catch (FRInstanceException e) {
			_logger.error(e.getMessage());
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			return;
		}
		
		this.viewConfig();
	}
	
	
	public void deleteSelectedFile() {
		
		try {
			if (this.getActiveTab().equalsIgnoreCase("ScriptConfig")) {
			
				this.viewScriptConfigList.remove(this.selectedScriptConfig);
				this.filteredScriptConfigList.remove(this.selectedScriptConfig);
				
				this.getConfigurationService().deleteConfigFile(null, this.selectedScriptConfig);
				
			} else {
				this.viewWMConfigList.remove(this.selectedWMConfig);
				this.filteredWMConfigList.remove(this.selectedScriptConfig);
				this.getConfigurationService().deleteConfigFile(null, this.selectedWMConfig);
				
			}
			
			//FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "configurationFilesList");
			
		} catch (FRInstanceException e) {
			_logger.error(e.getMessage());
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			return;
		}
		
	}

	//TODO having to put in a hack here to allow mobile team to continue. this needs to be removed and the properway below used instead
	public Document WRONGparseAndValidateXml(String xmlData, String schemaName)
			throws SAXException, ParserConfigurationException, IOException {

		if (_logger.isDebugEnabled()) {

			_logger.debug(">>> parseAndValidateXml XML=" + Common.CRLFEscapeString(xmlData));
		}

		Document doc = null;

		// Get the schema being used to validate the work order XML
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

		Schema schema = factory.newSchema(this.getClass().getClassLoader().getResource(schemaName));
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(false); 
    	domFactory.setSchema(schema);
    	
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		builder.setErrorHandler(new validationErrorHandler());
		
		InputSource is = new InputSource(new StringReader(xmlData));
		
		doc = builder.parse(is);

		if (_logger.isDebugEnabled())
			_logger.debug("<<< parseAndValidateXml");

		return doc;

	}
	
	//TODO REMOVE THIS TOO AS PART OF THE parseAndValidateXml WORK AROUND
	// Class to handle validation errors when validating xml against an xsd
	class validationErrorHandler implements ErrorHandler
	{

		public void error(SAXParseException e) throws SAXException 
		{
			_logger.error(e.getMessage());
			throw new SAXException(e.getMessage());
		}

		
		public void fatalError(SAXParseException e) throws SAXException 
		{
			_logger.error(e.getMessage());
			throw new SAXException(e.getMessage());
		}

		
		public void warning(SAXParseException e) throws SAXException 
		{
			_logger.error(e.getMessage());
			throw new SAXException(e.getMessage());
		}
		
	}
	
	//TODO RENAME THIS BACK TO parseAndValidateXml ONCE MOBILE TEAM HAVE VALID WORK ORDERS
	public Document PROPERWAYparseAndValidateXml(String xmlData, String schemaName)
			throws SAXException, ParserConfigurationException, IOException {

		if (_logger.isDebugEnabled()) {

			_logger.debug(">>> parseAndValidateXml XML=" + Common.CRLFEscapeString(xmlData));
		}

		Document doc = null;

		// Get the schema being used to validate the work order XML
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

		Schema schema = factory.newSchema(this.getClass().getClassLoader().getResource(schemaName));

		// Parse and validate the XML file. Exceptions thrown if any error
		// occurs.
		XMLUtils xmlUtils = new XMLUtils(schema);
		doc = xmlUtils.parseXML(xmlData);

		if (_logger.isDebugEnabled())
			_logger.debug("<<< parseAndValidateXml");

		return doc;

	}

	
	public String getTitle() {
		return "Configuration File List";
	}

	public List<ConfigurationFiles> getViewWMConfigList() {
		return viewWMConfigList;
	}

	public void setViewWMConfigList(List<ConfigurationFiles> viewWMConfigList) {
		this.viewWMConfigList = viewWMConfigList;
	}

	public List<ConfigurationFiles> getViewScriptConfigList() {
		return viewScriptConfigList;
	}

	public void setViewScriptConfigList(List<ConfigurationFiles> viewScriptConfigList) {
		this.viewScriptConfigList = viewScriptConfigList;
	}

	public List<ConfigurationFiles> getFilteredWMConfigList() {
		return filteredWMConfigList;
	}

	public void setFilteredWMConfigList(List<ConfigurationFiles> filteredWMConfigList) {
		this.filteredWMConfigList = filteredWMConfigList;
	}

	public List<ConfigurationFiles> getFilteredScriptConfigList() {
		return filteredScriptConfigList;
	}

	public void setFilteredScriptConfigList(List<ConfigurationFiles> filteredScriptConfigList) {
		this.filteredScriptConfigList = filteredScriptConfigList;
	}

	public ConfigurationFiles getSelectedWMConfig() {
		return selectedWMConfig;
	}

	public void setSelectedWMConfig(ConfigurationFiles selectedWMConfig) {
		this.selectedWMConfig = selectedWMConfig;
	}

	public ConfigurationFiles getSelectedScriptConfig() {
		return selectedScriptConfig;
	}

	public void setSelectedScriptConfig(ConfigurationFiles selectedScriptConfig) {
		this.selectedScriptConfig = selectedScriptConfig;
	}

	public String getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(String activeTab) {
		this.activeTab = activeTab;
	}

	public ConfigurationFiles getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(ConfigurationFiles uploadFile) {
		this.uploadFile = uploadFile;
	}

}
