package com.amtsybex.fieldreach.fdm.web.jsf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.primefaces.component.export.ExcelOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
/**
 * 
 * @author CroninM
 *
 *File not in use but can be used to place logos inside pdf and excell doqwnloads from list screens
 */
@Named
@WindowScoped
public class Download implements Serializable{

	private static final long serialVersionUID = 2086069699850725349L;

	private static Logger _logger = LoggerFactory.getLogger(Download.class.getName());
	
	private ExcelOptions excelOpt;
	
	@PostConstruct
    public void init() {
        excelOpt = new ExcelOptions();
        excelOpt.setFacetFontSize("10");
        excelOpt.setFacetFontColor("#0000ff");
        excelOpt.setFacetFontStyle("BOLD");
        excelOpt.setCellFontColor("#00ff00");
        excelOpt.setCellFontSize("8");
    }
	
	public ExcelOptions getExcelOpt() {
        return excelOpt;
    }
	
	public void preProcessPDF(Object document) {

        try {
    		Document doc = (Document) document;

    		doc.setPageSize(PageSize.A4.rotate());
    		
    		/*doc.open();
    		
    		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            String logo = externalContext.getRealPath("") + File.separator + "images" + File.separator + "amtsybex_logo.png";
             
			doc.add(Image.getInstance(logo));*/

		} catch (Exception e) {

			_logger.error("Exception generating pdf logo ", e);
		}
	}


	public void postProcessXLS(Object document) {
		
		try {
	    HSSFWorkbook wb = (HSSFWorkbook) document;
	    HSSFSheet sheet = wb.getSheetAt(0);
	    
	    sheet.shiftRows(0, sheet.getLastRowNum(), 4);
	
	    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	    String logo = externalContext.getRealPath("") + File.separator + "images" + File.separator + "amtsybex_logo.png";
	
	    InputStream is = new FileInputStream(logo);
	    byte[] bytes = IOUtils.toByteArray(is);
	
	    int my_picture_id = wb.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
	    
	    HSSFPatriarch drawing = sheet.createDrawingPatriarch();
	    
        ClientAnchor my_anchor = new HSSFClientAnchor();
        my_anchor.setCol1(0);
        my_anchor.setRow1(0);
        
        HSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
        my_picture.resize();
   
        wb.close();
        
		}catch (Exception e) {
			_logger.error("Exception generating pdf logo ", e);
		}
	
	}

}


