/**
 * 
 */
package se.sll.invoicedata.price;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import se.sll.invoicedata.core.service.dto.Price;
import se.sll.invoicedata.price.json.PriceListJson;
import se.sll.invoicedata.price.json.Service;

/**
 * @author muqkha
 *
 */
public class GeneratePriceList {
	
	private final char NUMERIC_SUFFIX = '0';	
	private final String SERVICE_CODE = "01";
	private final String OUTFILE_DIRECTORY = "Outdata\\";
	
	public static void main(String input[]) throws IOException {
		 Scanner in = new Scanner(System.in);
		 
		 System.out.println("Have you checked that the Lev/Leverantör/Supplier column in excel file matches the order in the program (see method processSheet)?");
		 
		 System.out.println("Enter the input file name ex:(Indata//NewPrice.xls):");
		 String indataFile = in.nextLine();
		 
		 System.out.println("Enter start row number (row starts from 0):");
		 int startRow = in.nextInt();
		 
		 System.out.println("Enter start column number (column start from 0):");
		 int startColumn = in.nextInt();
		 
		 System.out.println("Enter valid from date (yyyy-mm-dd):");
		 String validFrom = in.nextLine();
		 validFrom = in.nextLine();
		 
		 if (!validFrom.isEmpty()) {
			 GeneratePriceList generatePriceList = new GeneratePriceList();
			 HSSFSheet sheet = generatePriceList.read(indataFile);
			 generatePriceList.processSheet(sheet, validFrom, startRow, startColumn);
		 } else {
			 in.close();
			 throw new IllegalArgumentException("ValidFrom date must be given");
		 }
		 
		 System.out.println("Verify that the first and the last row guid matches that in the excel file");
		 in.close();
	}
	
	public HSSFSheet read(String fileName) throws IOException {
		FileInputStream file = new FileInputStream(new File(fileName));
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		return workbook.getSheetAt(0);
	}
	
	private ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
		mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);		
		return mapper;
	}
	
	private void processSheet(HSSFSheet sheet, String validFrom, final int startRow, final int startColumn) throws JsonGenerationException, JsonMappingException, IOException {
		List<String> guidList = getGuidList(sheet, startRow, startColumn);
		writeListToFile(guidList, "GUID.txt");
		
		PriceListJson priceList = new PriceListJson();
		
		/* See the order in the database matches with the order in the generated file */
		priceList.add(getTransVoice(sheet, guidList, validFrom, startRow, startColumn+1));
		priceList.add(getSprakService(sheet, guidList, validFrom, startRow, startColumn+2));		
		priceList.add(getTolkJouren(sheet, guidList, validFrom, startRow, startColumn+4));
		priceList.add(getJarva(sheet, guidList, validFrom, startRow, startColumn+5));
		//priceList.add(getBotkryka(sheet, guidList));
		priceList.add(getEquator(sheet, guidList, validFrom, startRow, startColumn+3));
		
		System.out.println("Items in list: " + guidList.size() + ". State OK");
		getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(OUTFILE_DIRECTORY + "PriceList.json"), priceList);
	}
	
	private Service getTransVoice(HSSFSheet sheet, List<String> guidList, String validFrom, int startRow, final int startColumn) {
		Service service = new Service();
		service.setSupplierId("556482-8654");
		service.setSupplierName("Transvoice");
		service.setServiceCode(SERVICE_CODE);
		service.setValidFrom(validFrom);
		
		List<String> transPrice = getServicePrice(sheet, startRow, startColumn);
		if (guidList.size() != transPrice.size()) {
			throw new IllegalStateException("Mismatch in row size transPrice");
		}
		writeListToFile(transPrice, "TRANSVOICE.txt");
		
		List<Price> priceList = new ArrayList<Price>();
		for (int index = 0; index < guidList.size(); index ++) {
			Price price = new Price();
			price.setItemId(guidList.get(index));
			price.setPrice(new BigDecimal(transPrice.get(index)));
			priceList.add(price);
		}
		service.setPrices(priceList);
		return service;
	}
	
	private Service getSprakService(HSSFSheet sheet, List<String> guidList, String validFrom,  int startRow, int startColumn) {
		Service service = new Service();
		service.setSupplierId("556629-1513");
		service.setSupplierName("Språkservice");
		service.setServiceCode(SERVICE_CODE);
		service.setValidFrom(validFrom);
		
		List<String> sprakPrice = getServicePrice(sheet, startRow, startColumn);
		if (guidList.size() != sprakPrice.size()) {
			throw new IllegalStateException("Mismatch in row size sprakPrice");
		}
		writeListToFile(sprakPrice, "SPRAKSERVICE.txt");
		
		List<Price> priceList = new ArrayList<Price>();
		for (int index = 0; index < guidList.size(); index ++) {
			Price price = new Price();
			price.setItemId(guidList.get(index));
			price.setPrice(new BigDecimal(sprakPrice.get(index)));
			priceList.add(price);
		}
		service.setPrices(priceList);
		return service;
	}
	
	private Service getEquator(HSSFSheet sheet, List<String> guidList, String validFrom,  int startRow, int startColumn) {
		Service service = new Service();
		service.setSupplierId("556560-0854");
		service.setSupplierName("Semantix Equator");
		service.setServiceCode(SERVICE_CODE);
		service.setValidFrom(validFrom);
		
		List<String> equatorPrice = getServicePrice(sheet, startRow, startColumn);
		if (guidList.size() != equatorPrice.size()) {
			throw new IllegalStateException("Mismatch in row size equatorPrice");
		}
		writeListToFile(equatorPrice, "EQUATOR.txt");
				
		List<Price> priceList = new ArrayList<Price>();
		for (int index = 0; index < guidList.size(); index ++) {
			Price price = new Price();
			price.setItemId(guidList.get(index));
			price.setPrice(new BigDecimal(equatorPrice.get(index)));
			priceList.add(price);
		}
		service.setPrices(priceList);
		return service;
	}
	
	private Service getTolkJouren(HSSFSheet sheet, List<String> guidList, String validFrom,  int startRow, int startColumn) {
		Service service = new Service();
		service.setSupplierId("556526-2630");
		service.setSupplierName("Semantix Tolkjouren");
		service.setServiceCode(SERVICE_CODE);
		service.setValidFrom(validFrom);
		
		List<String> tolkPrice = getServicePrice(sheet, startRow, startColumn);
		if (guidList.size() != tolkPrice.size()) { 
			throw new IllegalStateException("Mismatch in row size tolkPrice");
		}
		writeListToFile(tolkPrice, "TOLKJOUREN.txt");
				
		List<Price> priceList = new ArrayList<Price>();
		for (int index = 0; index < guidList.size(); index ++) {
			Price price = new Price();
			price.setItemId(guidList.get(index));
			price.setPrice(new BigDecimal(tolkPrice.get(index)));
			priceList.add(price);
		}
		service.setPrices(priceList);
		return service;
	}
	
	private Service getJarva(HSSFSheet sheet, List<String> guidList, String validFrom, int startRow, int startColumn) {
		Service service = new Service();
		service.setSupplierId("556613-1792");
		service.setSupplierName("Järva");
		service.setServiceCode(SERVICE_CODE);
		service.setValidFrom(validFrom);
		
		List<String> jarvaPrice = getServicePrice(sheet, startRow, startColumn);
		if (guidList.size() != jarvaPrice.size()) { 
			throw new IllegalStateException("Mismatch in row size jarvaPrice");
		}
		writeListToFile(jarvaPrice, "JARVA.txt");
				
		List<Price> priceList = new ArrayList<Price>();
		for (int index = 0; index < guidList.size(); index ++) {
			Price price = new Price();
			price.setItemId(guidList.get(index));
			price.setPrice(new BigDecimal(jarvaPrice.get(index)));
			priceList.add(price);
		}
		service.setPrices(priceList);
		return service;
		
	}
	/*
	private Service getBotkryka(HSSFSheet sheet, List<String> guidList) {
		Service service = new Service();
		service.setSupplierId("212000-2882");
		service.setSupplierName("Botkyrka");
		service.setServiceCode(SERVICE_CODE);
		service.setValidFrom(VALID_FROM);
		
		List<String> botkrykaPrice = getServicePrice(sheet, 0, 16);
		if (guidList.size() != botkrykaPrice.size()) { 
			throw new IllegalStateException("Mismatch in row size botkrykaPrice");
		}
		writeListToFile(botkrykaPrice, "BOTKRYKA.txt");
		
		List<Price> priceList = new ArrayList<Price>();
		for (int index = 0; index < guidList.size(); index ++) {
			Price price = new Price();
			price.setItemId(guidList.get(index));
			price.setPrice(new BigDecimal(botkrykaPrice.get(index)));
			priceList.add(price);
		}
		service.setPrices(priceList);
		return service;
	}*/
	
	private void writeListToFile(List<String> items, String fileName) {
		FileWriter writer;
		try {
			writer = new FileWriter(OUTFILE_DIRECTORY + fileName);
			
			for(String str: items) {
				  writer.write(str);
				  writer.write("\r\n");
				}
				writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	private List<String> getGuidList(HSSFSheet sheet, int startRow, int startColumn) {
		
		List<String> guids = new ArrayList<String>();
		
		for (int i = startRow; i < sheet.getPhysicalNumberOfRows(); i++) {
			Cell cell = sheet.getRow(i).getCell(startColumn);		
			
			if (!guids.contains(cell.getStringCellValue())) {
				guids.add(cell.getStringCellValue());
			} else {
				throw new IllegalArgumentException("GUID data is corrupt, duplicate item: " + cell.getStringCellValue());
			}
		}		
		return guids;
	}
	
	private List<String> getServicePrice(HSSFSheet sheet, int startRow, int serviceType) {
		FormulaEvaluator formulaEval = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();		
		List<String> priceList = new ArrayList<String>();
		
		for (int i = startRow; i < sheet.getPhysicalNumberOfRows(); i++) {
			Cell cell = sheet.getRow(i).getCell(serviceType);
			
			if (cell != null) {
				switch(cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						priceList.add(String.valueOf(cell.getNumericCellValue()) + NUMERIC_SUFFIX);
						break;
					case Cell.CELL_TYPE_FORMULA:
						priceList.add(formulaEval.evaluate(cell).formatAsString() + NUMERIC_SUFFIX);
						break;
					case Cell.CELL_TYPE_BLANK:
						priceList.add("0.0" + NUMERIC_SUFFIX);
						break;
					default:
						StringBuffer errorMsg = new StringBuffer("This type of cell is not handled by the program!");
						errorMsg.append(" cell type:").append(cell.getCellType());
						errorMsg.append(" cell row:").append(cell.getRowIndex());
						errorMsg.append(" cell column:").append(cell.getColumnIndex());
						errorMsg.append(" cell value:").append(cell.getStringCellValue());
						throw new IllegalStateException(errorMsg.toString());						
				}
			} else {
				priceList.add("0.0" + NUMERIC_SUFFIX);
			}
		}
		
		return priceList;
	}
	

}
