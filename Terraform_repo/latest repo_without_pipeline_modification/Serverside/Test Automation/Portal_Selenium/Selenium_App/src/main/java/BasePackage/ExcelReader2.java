package BasePackage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader2 {

     /**
     * @param filePath  excel file path
     * @param sheetName  sheet name in xlsx file
     * @return excel data
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static Object[][] readExcel(String filePath, String sheetName) throws InvalidFormatException, IOException {
            FileInputStream file= new FileInputStream(filePath);
            
           
            XSSFWorkbook wb = new XSSFWorkbook(file);
            XSSFSheet sheet = wb.getSheet(sheetName);
            int rowCount = sheet.getLastRowNum();
            int column = sheet.getRow(0).getLastCellNum();
            
            Object[][] data = new Object[rowCount][1];
            
            for (int i = 0; i < rowCount; i++) {
                Map<Object, Object> datamap = new HashMap<>();
                for (int j = 0; j < column; j++) {
                  datamap.put(sheet.getRow(0).getCell(j).toString(), sheet.getRow(i+1).getCell(j).toString());    
                }
                data[i][0] = datamap;
                wb.close();
            }

            return data;
        }
}