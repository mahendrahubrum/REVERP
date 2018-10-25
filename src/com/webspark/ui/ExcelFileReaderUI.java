package com.webspark.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.stock.model.PaymentTermsModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.XLSUploader;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.model.AddressModel;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 9, 2013
 */
public class ExcelFileReaderUI extends SparkLogic {

	private static final long serialVersionUID = -2452314896366678777L;

	private Upload upload;
	private XLSUploader uploader;
	private SRadioButton typeRadioButton;
	private SButton importButton;
	private SLabel label;
	private SButton clearButton;

	private FileDownloader downloader;
	private SButtonLink link;

	@Override
	public SPanel getGUI() {

		setSize(340, 240);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		SHorizontalLayout layout = new SHorizontalLayout();
		layout.setSpacing(true);

		typeRadioButton = new SRadioButton("", 200, Arrays.asList(new KeyValue(
				(long) 1, getPropertyName("Customer")), new KeyValue((long) 2,
				(getPropertyName("Supplier")))), "key", "value");
		typeRadioButton.addStyleName("radio_horizontal");
		typeRadioButton.setValue((long) 1);

		uploader = new XLSUploader();
		upload = new Upload(getPropertyName("upload_supplier"), uploader);
		upload.setImmediate(true);

		label = new SLabel("");

		importButton = new SButton(getPropertyName("import"));
		clearButton = new SButton(getPropertyName("clear"));

		downloader = new FileDownloader(new FileResource(new File(VaadinServlet
				.getCurrent().getServletContext().getRealPath("/")
				+ "Files/Supplier.xls")));
		link = new SButtonLink(getPropertyName("download_format"));
		downloader.extend(link);

		mainFormLayout.addComponent(typeRadioButton);
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
					String message = "";
					if ((Long) typeRadioButton.getValue() == (long) 1) {
						message = getPropertyName("import_msg");
					} else {
						message = getPropertyName("import_msg");
					}

					ConfirmDialog.show(getUI(), message,
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

		typeRadioButton.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if ((Long) typeRadioButton.getValue() == (long) 1) {
					upload.setCaption(getPropertyName("upload_supplier"));
					if (uploader.getFile() != null)
						uploader.deleteFile();
					downloader.setFileDownloadResource(new FileResource(
							new File(VaadinServlet.getCurrent()
									.getServletContext().getRealPath("/")
									+ "Files/Supplier.xls")));
					label.setValue("");
				} else {
					upload.setCaption(getPropertyName("upload_customer"));
					if (uploader.getFile() != null)
						uploader.deleteFile();
					downloader.setFileDownloadResource(new FileResource(
							new File(VaadinServlet.getCurrent()
									.getServletContext().getRealPath("/")
									+ "Files/Customer.xls")));
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

			LedgerModel objModel;
			SupplierModel supplier;
			AddressModel addressModel;
			CustomerModel customerModel;

			SupplierDao supplierDao = new SupplierDao();
			CustomerDao customerDao = new CustomerDao();

			FileInputStream fileStream = new FileInputStream(files);

			// Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(fileStream);

			// Get first sheet from the workbook
			HSSFSheet sheet = workbook.getSheetAt(0);

			// Iterate through each rows from first sheet
			Row row;
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {

				row = rowIterator.next();

				if (row.getRowNum() != 0) {

					objModel = new LedgerModel();
					supplier = new SupplierModel();
					addressModel = new AddressModel();
					customerModel = new CustomerModel();

					addressModel.setPhone(getStringValue(row.getCell(10)));
					addressModel.setMobile(getStringValue(row.getCell(11)));
//					addressModel.setFax(getStringValue(row.getCell(12)));
//					addressModel.setEmail(getStringValue(row.getCell(13)));
//					addressModel.setBuilding_name(getStringValue(row
//							.getCell(14)));
//					addressModel.setArea(getStringValue(row.getCell(15)));
					addressModel.setCountry(new OfficeDao().getOffice(
							getOfficeID()).getCountry());

					objModel.setName(getStringValue(row.getCell(0)));
					customerModel.setAddress(addressModel);
					objModel.setCurrent_balance(0);
//					objModel.setOpening_balance(0);
					objModel.setStatus(1);
					objModel.setOffice(new S_OfficeModel(getOfficeID()));

					if ((Long) typeRadioButton.getValue() == (long) 1) {

						if (!supplierDao.isAlreadyExists(getOfficeID(),
								getStringValue(row.getCell(0)),
								getStringValue(row.getCell(1)))) {
							objModel.setGroup(new GroupModel(
									getSettings().getCUSTOMER_GROUP()));

							supplier.setName(getStringValue(row.getCell(0)));
							supplier.setSupplier_code(getStringValue(
									row.getCell(1)).replaceAll(".0", ""));
							supplier.setCredit_limit(toDouble(getStringValue(row
									.getCell(3))));
							supplier.setDescription(getStringValue(row
									.getCell(4)));
							supplier.setWebsite(getStringValue(row.getCell(5)));
							supplier.setBank_name(getStringValue(row.getCell(6)));

							supplier.setContact_person(getStringValue(row
									.getCell(7)));
							supplier.setContact_person_fax(getStringValue(row
									.getCell(8)));
							supplier.setContact_person_email(getStringValue(row
									.getCell(9)));
							supplier.setLedger(objModel);

							// supplier.setTax_group((long) 1);
							supplier.setSupplier_currency(new CurrencyModel(
									getCurrencyID()));
							supplier.setPayment_terms(new PaymentTermsModel(
									(long) 1));

							try {
								supplierDao.save(supplier, null);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {

						objModel.setGroup(new GroupModel(
								getSettings().getCUSTOMER_GROUP()));

						if (!customerDao.isAlreadyExists(getOfficeID(),
								getStringValue(row.getCell(0)),
								getStringValue(row.getCell(1)))) {

							customerModel
									.setName(getStringValue(row.getCell(0)));
							customerModel.setCustomer_code(getStringValue(
									row.getCell(1)).replaceAll(".0", ""));
							customerModel
									.setCredit_limit(toDouble(getStringValue(row
											.getCell(3))));
							customerModel
									.setCustomer_currency(new CurrencyModel(
											getCurrencyID()));
							customerModel.setDescription(getStringValue(row
									.getCell(4)));

							customerModel
									.setPayment_terms(new PaymentTermsModel(
											(long) 1));
							customerModel.setSales_type((long) 1);

							customerModel.setLedger(objModel);

							try {
								customerDao.save(customerModel, null);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				fileStream.close();
			}
			SNotification.show(getPropertyName("Success"),
					Type.HUMANIZED_MESSAGE);
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

	protected void WriteFile() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Sample sheet");

		Map<String, Object[]> data = new HashMap<String, Object[]>();
		data.put("1", new Object[] { "Emp No.", "Name", "Salary" });
		data.put("2", new Object[] { 1d, "John", 1500000d });
		data.put("3", new Object[] { 2d, "Sam", 800000d });
		data.put("4", new Object[] { 3d, "Dean", 700000d });

		Set<String> keyset = data.keySet();
		int rownum = 0;
		for (String key : keyset) {
			Row row = sheet.createRow(rownum++);
			Object[] objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof Date)
					cell.setCellValue((Date) obj);
				else if (obj instanceof Boolean)
					cell.setCellValue((Boolean) obj);
				else if (obj instanceof String)
					cell.setCellValue((String) obj);
				else if (obj instanceof Double)
					cell.setCellValue((Double) obj);
			}
		}

		try {
			FileOutputStream out = new FileOutputStream(new File("new.xls"));
			workbook.write(out);
			out.close();
			System.out.println("Excel written successfully..");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
