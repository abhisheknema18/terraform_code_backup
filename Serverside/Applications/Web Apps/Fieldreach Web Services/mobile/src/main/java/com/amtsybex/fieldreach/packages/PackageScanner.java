package com.amtsybex.fieldreach.packages;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.amtsybex.fieldreach.packages.gfspec.GfSpecRootElement;

/**
 * Package scanner class, purpose to take a path property and scan that path for file updates, 
 * calling functions on a package index as files are added/updated/removed
 * @author CroninM
 *
 */

@Component
public class PackageScanner implements FileListener, DisposableBean, InitializingBean {

	private static Logger log = LoggerFactory.getLogger(PackageScanner.class.getName());

	public static final String PACKAGE_EXTENSION = ".gfpkg";
	public static final String PACKAGE_META_FILE_PATH = ".gfspec";
	
	private final FilenameFilter packageFilter = this.createFilenameFilter(PACKAGE_EXTENSION);

	@Autowired
	private PackageIndex packageIndex;

	@Value("${packages.dir}")
	private String packagePath;

	private DefaultFileSystemManager fileSystemManager;
	private DefaultFileMonitor fileMonitor;

	public PackageScanner() {

	}

	@Override
	public void afterPropertiesSet() throws Exception {

		log.debug(">>> initalise");

		if(packagePath != null && packagePath.length() != 0) {
			
			File dir = new File(packagePath);
			
			if(dir.isDirectory()) {
				
				log.info("Initialising package scanner");
				
				this.fullScan();
	
				log.info("Initialising package scanner file watcher");
				
		        FileObject dirToWatchFO = null;

		        fileSystemManager = new DefaultFileSystemManager();
		        fileSystemManager.addProvider("file", new DefaultLocalFileProvider());
		        fileSystemManager.init(); 
		        
		        dirToWatchFO = fileSystemManager.toFileObject(new File(packagePath));

		        fileMonitor = new DefaultFileMonitor(this);
		        fileMonitor.setRecursive(false);
		        fileMonitor.setRecursive(true);
		        fileMonitor.addFile(dirToWatchFO);
		        fileMonitor.start();
		        
			}else {
				throw new Exception("Package Directory does not exist or is not a directory");
			}
			
		}else {
			log.info("Package Sync directory not set, package scanning disabled");
		}

		log.debug("<<< initalise");
		
	}
	
	@Override
	public void destroy() {

		log.debug(">>> stopMonitoring");

		
		if(fileMonitor != null) {
			fileMonitor.stop();
		}
		
		if (fileSystemManager != null) {
			try {
				fileSystemManager.close();
			} catch (Exception e) {
				//do nothing
			}
		}


		log.debug("<<< stopMonitoring");
	}

	private void fullScan() throws Exception {

		log.debug(">>> fullScan");

		packageIndex.clearPackageIndex();

		File dir = new File(packagePath);

		if(dir.isDirectory()) {

			File[] files;
			files = dir.listFiles(packageFilter);

			for (File file : files) {

				log.debug("adding file to index  " + file.getName());

				this.addFileToPackageIndex(file);

			}
		}else {
			throw new Exception("Package Directory does not exist or is not a directory");
		}

		log.debug("<<< fullScan");
	}

	private synchronized void addFileToPackageIndex(File file) throws Exception {

		log.debug("<<< addFileToPackageIndex");

		log.debug("Loading gfspec");
		GfSpecRootElement pkg = this.loadGfSpec(Paths.get(file.getAbsolutePath()));

		if(pkg != null) {

			log.debug("Package Meta parsed, adding to index.");

			packageIndex.addOrUpdate(file, pkg.getMetadata());
		}


		log.debug("<<< addFileToPackageIndex");
	}

	@Override
	public void fileCreated(FileChangeEvent event) throws Exception {
		log.debug("File created on file system, updating in index");
		
		Path dirPath = Paths.get(this.packagePath);
		Path filePath = Paths.get(event.getFileObject().getName().getPath());
		
		File file = dirPath.resolve(filePath).toFile();
		
		log.debug(file.getName());

		if (file.getName().toLowerCase().endsWith(PACKAGE_EXTENSION)) {

			try {

				this.addFileToPackageIndex(file);

				log.debug("File creation handled");

			}catch(Exception e) {
				log.error("Error handing creation of file ",  e);
			}
		}
	}


	@Override
	public void fileDeleted(FileChangeEvent event) throws Exception {
		try {

			log.debug("File deleted on file system, removing from index");
			
			Path dirPath = Paths.get(this.packagePath);
			Path filePath = Paths.get(event.getFileObject().getName().getPath());
			
			File file = dirPath.resolve(filePath).toFile();
			
			log.debug(file.getName());
			
			if (file.getName().toLowerCase().endsWith(PACKAGE_EXTENSION)) {
				packageIndex.remove(file.getAbsolutePath());
			}

			log.debug("File deletion handled");

		}catch(Exception e) {
			log.error("Error handing deletion of file ",  e);
		}
	}


	@Override
	public void fileChanged(FileChangeEvent event) throws Exception {
		try {

			log.debug("File modified on file system, updating in index");

			Path dirPath = Paths.get(this.packagePath);
			Path filePath = Paths.get(event.getFileObject().getName().getPath());
			
			File file = dirPath.resolve(filePath).toFile();
			
			log.debug(file.getName());

			if (file.getName().toLowerCase().endsWith(PACKAGE_EXTENSION)) {

				packageIndex.remove(file.getAbsolutePath());
				this.addFileToPackageIndex(file);
			}

			log.debug("File modification handled");

		}catch(Exception e) {
			log.error("Error handing modification of file ",  e);
		}
	}

	private FilenameFilter createFilenameFilter(final String filterEnd) {

		log.debug("<<< createFilenameFilter");

		FilenameFilter pickupFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				String uppercaseName = name.toLowerCase();

				if (uppercaseName.endsWith(filterEnd)) {

					return true;

				} else {

					return false;
				}
			}
		};

		log.debug("<<< createFilenameFilter");

		return pickupFilter;
	}


	private GfSpecRootElement loadGfSpec(Path packageFilePath) throws IOException {
		try (ZipFile zipFile = new ZipFile(packageFilePath.toFile())) {
			
			ZipEntry entry = zipFile.getEntry(PACKAGE_META_FILE_PATH);
			if (entry == null) {
				return null;
			}
			
			try (InputStream stream = zipFile.getInputStream(entry)) {
				
				return readGfSpecFromXml(packageFilePath, stream);
			}
		}
	}
	
	private GfSpecRootElement readGfSpecFromXml(Path packageFilePath, InputStream stream) {

		XMLReader gfspecXmlReader = createGfSpecXmlReader();

		Source xmlSource = new SAXSource(gfspecXmlReader, new InputSource(stream));
		
		try {
			Unmarshaller jaxbUnmarshaller = createXmlUnmarshaller();
			
			return (GfSpecRootElement) jaxbUnmarshaller.unmarshal(xmlSource);
		} catch (JAXBException e) {
			log.warn("Unable to deserialize " + PACKAGE_META_FILE_PATH + " from file:" + packageFilePath.toString(), e);
			return null;
		}
	}
	
	private XMLReader createGfSpecXmlReader() throws IllegalStateException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			return spf.newSAXParser().getXMLReader();
		} catch (SAXException | ParserConfigurationException e) {
			throw new IllegalStateException("Unable to create XML parser: " + GfSpecRootElement.class.getName(), e);
		}
	}
	
	private Unmarshaller createXmlUnmarshaller() throws IllegalStateException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(GfSpecRootElement.class);	
			return jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw new IllegalStateException("Unable to create XML deserializer: " + GfSpecRootElement.class.getName(), e);
		}
	}	

}
