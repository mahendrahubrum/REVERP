package com.inventory.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.stock.dao.BrandDao;
import com.inventory.config.stock.dao.ColourDao;
import com.inventory.config.stock.dao.GradeDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemModelDao;
import com.inventory.config.stock.dao.SizeDao;
import com.inventory.config.stock.dao.StyleDao;
import com.inventory.config.stock.model.ColourModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ItemModelModel;
import com.inventory.config.stock.model.SizeModel;
import com.inventory.config.stock.model.StyleModel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.model.ItemUnitMangementModel;
import com.inventory.dao.ImportItemApparelDao;
import com.inventory.purchase.model.ItemStockModel;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload;
import com.webspark.Components.SButton;
import com.webspark.Components.SButtonLink;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.XLSUploader;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 17-Jun-2015
 */

@SuppressWarnings("serial")
public class ImportItemApparelUI extends SparkLogic {

	private Upload upload;
	private XLSUploader uploader;
	private SButton importButton;
	private SLabel label;
	private SButton clearButton;

	private FileDownloader downloader;
	private SButtonLink link;
	
	
	ImportItemApparelDao dao;
	BrandDao brandDao;
	GradeDao gradeDao;

	@Override
	public SPanel getGUI() {
		dao=new ImportItemApparelDao();
		setSize(345, 240);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		SHorizontalLayout layout = new SHorizontalLayout();
		layout.setSpacing(true);

		brandDao=new BrandDao();
		gradeDao=new GradeDao();
		
		uploader = new XLSUploader();
		upload = new Upload(getPropertyName("choose_excel_file"), uploader);
		upload.setImmediate(true);
		upload.setButtonCaption(getPropertyName("upload"));

		label = new SLabel("");

		importButton = new SButton(getPropertyName("import"));
		clearButton = new SButton(getPropertyName("clear"));

		downloader = new FileDownloader(new FileResource(new File(VaadinServlet
				.getCurrent().getServletContext().getRealPath("/")
				+ "Files/Items.xls")));
		link = new SButtonLink(getPropertyName("download_format"));
		downloader.extend(link);

		mainFormLayout.addComponent(link);
		mainFormLayout.addComponent(upload);
		mainFormLayout.addComponent(label);
		layout.addComponent(importButton);
		layout.addComponent(clearButton);
		mainFormLayout.addComponent(layout);

		importButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				if (uploader.getFile() != null) {

					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										readFile(uploader.getFile());
									}
								}
							});
				} else {
					SNotification.show(getPropertyName("upload_xls_file"),
							Type.ERROR_MESSAGE);
				}
			}
		});

		upload.addListener(new Listener() {
			@Override
			public void componentEvent(Event event) {
				if (uploader.getFile() != null) {
					label.setValue(uploader.getFile().getName());
				} else {
					label.setValue("");
				}
			}
		});

		clearButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (uploader.getFile() != null) {
					uploader.deleteFile();
					label.setValue("");
				}
			}
		});

		panel.setContent(mainFormLayout);
		return panel;
	}

	protected void readFile(File files) {
		try {

			ItemModel objModel;
			ItemStockModel supplier;
//			ItemPriceModel objPrice;
			ItemUnitMangementModel untMngtMdl;

			ItemDao itmDao = new ItemDao();

			FileInputStream fileStream = new FileInputStream(files);

			// Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(fileStream);

			// Get first sheet from the workbook
			HSSFSheet sheet = workbook.getSheetAt(1);

			TaxModel tax = new TaxDao().getDefaultTax(getOfficeID());
			// TaxModel tax=new TaxModel(1);
			// Iterate through each rows from first sheet
			Row row;
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {

				row = rowIterator.next();
				if (row.getRowNum() != 0) {
					SizeModel sizeModel;
					ColourModel colourModel;
					StyleModel styleModel;
					ItemModelModel modelModel;
					ItemModel itemModel;
					long size=dao.getSizeModel(getStringValue(row.getCell(5)), getOfficeID());
					long colour=dao.getColourModel(getStringValue(row.getCell(6)), getOfficeID());
					long style=dao.getStyleModel(getStringValue(row.getCell(7)), getOfficeID());
					long model=dao.getItemModelModel(getStringValue(row.getCell(8)), getOfficeID());
					if(size==0){
						sizeModel=new SizeModel();
						sizeModel.setName(getStringValue(row.getCell(5)));
						sizeModel.setOffice(new S_OfficeModel(getOfficeID()));
						size=new SizeDao().save(sizeModel);
					}
					
					if(colour==0){
						colourModel=new ColourModel();
						colourModel.setName(getStringValue(row.getCell(6)));
						colourModel.setOffice(new S_OfficeModel(getOfficeID()));
						colour=new ColourDao().save(colourModel);
					}
					
					if(style==0){
						styleModel=new StyleModel();
						styleModel.setName(getStringValue(row.getCell(7)));
						styleModel.setOffice(new S_OfficeModel(getOfficeID()));
						style=new StyleDao().save(styleModel);
					}
					
					if(model==0){
						modelModel=new ItemModelModel();
						modelModel.setName(getStringValue(row.getCell(8)));
						modelModel.setOffice(new S_OfficeModel(getOfficeID()));
						size=new ItemModelDao().save(modelModel);
					}
					itemModel=dao.getItemModel(size, colour, model, style, getOfficeID());
					if(itemModel!=null){
						itemModel = new ItemModel();
					}
					else{
						
					}
					/*if (!itmDao.isAlreadyExists(getOfficeID(),
							getStringValue(row.getCell(3)),
							getStringValue(row.getCell(0)))) {

						objModel = new ItemModel();
						objModel.setItem_code(getStringValue(row.getCell(0)));
						objModel.setName(getStringValue(row.getCell(3)));
						objModel.setCurrent_balalnce(0);
						objModel.setSub_group(itmDao.getItemSubGroupByCreate(
								getOrganizationID(),
								getStringValue(row.getCell(2)), tax));
						objModel.setOffice(new S_OfficeModel(getOfficeID()));
						objModel.setCess_enabled('N');
						if (row.getCell(10) != null
								&& row.getCell(10).toString().trim().length() > 0){
							try{
							objModel.setRate(Double
									.parseDouble(getStringValue(row.getCell(10))));
							}catch(Exception e){
								objModel.setRate(0.0);
							}
						}
						else
							objModel.setRate(0.0);
						objModel.setAffect_type(1);
						objModel.setReservedQuantity(0);

						objModel.setSalesTax(tax);
						objModel.setPurchaseTax(tax);

						objModel.setUnit(itmDao.getUnitByCreate(
								getOrganizationID(),
								getStringValue(row.getCell(9))));
						
						BrandModel brand=brandDao.getBrandFromName(getStringValue(row.getCell(6)));
						if(brand==null){
							brand=new BrandModel();
							brand.setBrandCode("");		
							brand.setName(getStringValue(row.getCell(6)));		
							brand.setOrganization(new S_OrganizationModel(getOrganizationID()));
							brandDao.save(brand);
						}
						objModel.setBrand(brand.getId());
						objModel.setSpecification(getStringValue(row.getCell(7)));
						objModel.setDesciption(getStringValue(row.getCell(4)));
						objModel.setColor(getStringValue(row.getCell(5)));
						if (row.getCell(12) != null
								&& row.getCell(12).toString().trim().length() > 0){
							try{
								objModel.setDeliveryCharge(Double
									.parseDouble(getStringValue(row.getCell(12))));
						
							}catch(Exception e){
								objModel.setDeliveryCharge(0.0);
							}
						}
						else
							objModel.setDeliveryCharge(0.0);
						
						if (row.getCell(13) != null
								&& row.getCell(13).toString().trim().length() > 0){
							try{
								objModel.setReorder_level(Double
										.parseDouble(getStringValue(row.getCell(13))));
								
							}catch(Exception e){
								objModel.setReorder_level(0.0);
							}
						}
						else
							objModel.setReorder_level(0.0);
						
						if (row.getCell(15) != null
								&& row.getCell(15).toString().trim().length() > 0){
							try{
								objModel.setOpening_balance(Double
										.parseDouble(getStringValue(row.getCell(15))));
								
							}catch(Exception e){
								objModel.setOpening_balance(0);
							}
						}
						else
							objModel.setOpening_balance(0);
						
						objModel.setCurrent_balalnce(objModel.getOpening_balance());
						
						if (row.getCell(14) != null
								&& row.getCell(14).toString().trim().length() > 0){
							try{
								objModel.setStatus(Integer
										.parseInt(getStringValue(row.getCell(14))));
								
							}catch(Exception e){
								objModel.setStatus(1);
							}
						}
						else
							objModel.setStatus(1);
						
						
						objModel.setModel(getStringValue(row.getCell(8)).replaceAll(".0",""));
						
						
						if (row.getCell(11) != null
								&& row.getCell(11).toString().trim().length() > 0){
							try{
								objModel.setInstallationCharge(Double
									.parseDouble(getStringValue(row.getCell(11))));
							}catch(Exception e){
								objModel.setInstallationCharge(0.0);
							}
						}
						else
							objModel.setInstallationCharge(0.0);
						
						objModel.setRent_period(0);

						objPrice = new ItemPriceModel();
						objPrice.setItem(objModel);
						objPrice.setRate(objModel.getRate());
						objPrice.setSales_type(new SalesTypeModel(1));

						untMngtMdl = new ItemUnitMangementModel();

						untMngtMdl.setAlternateUnit(objModel.getUnit().getId());
						untMngtMdl.setBasicUnit(objModel.getUnit().getId());
						untMngtMdl.setConvertion_rate(1);
						untMngtMdl.setSales_type(0);
						untMngtMdl.setItem_price(objModel.getRate());
						untMngtMdl.setStatus(2);
						
						GradeModel grade=gradeDao.getGradeFromName(getStringValue(row.getCell(16)));
						if(grade==null){
							grade=new GradeModel();
							grade.setCode("");
							grade.setDescription("");
							grade.setName(getStringValue(row.getCell(16)));
							grade.setOfficeId(getOfficeID());
							grade.setPercentage(100);
							gradeDao.save(grade);
						}
						
						itmDao.importItem(objPrice, untMngtMdl,
								getStringValue(row.getCell(1)),grade.getId());

					}*/
				}
			}

			fileStream.close();
			SNotification.show(getPropertyName("Success"), Type.HUMANIZED_MESSAGE);
		} catch (FileNotFoundException e) {
			SNotification.show(getPropertyName("invalid_file"),
					Type.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			SNotification.show(getPropertyName("Error"), Type.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (Exception e1) {
			SNotification.show(getPropertyName("upload_xls_file"),
					Type.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}

	private String getStringValue(Cell cell) {

		String value = "";

		if (cell != null) {
			switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_NUMERIC:
				value = cell.getNumericCellValue() + "".trim();
				break;

			case HSSFCell.CELL_TYPE_STRING:
				value = cell.getStringCellValue().trim();
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				value = "";
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				value = cell.getBooleanCellValue() + "".trim();
				break;

			default:
				value = cell.getStringCellValue().trim();
				break;
			}
		}

		return value;
	}


	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
