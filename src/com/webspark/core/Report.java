package com.webspark.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SNotification;
import com.webspark.business.AddressBusiness;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.AddressDao;
import com.webspark.dao.LoginDao;
import com.webspark.model.AddressModel;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/**
 * @author Anil K P
 * 
 *         Jul 9, 2013
 */
public class Report {

	private boolean isWindows;
	private boolean isLinux;
	private boolean isMac;

	private String reportFileName = "";
	private String jrxmlFileName = "";
	private int reportType = 0;

	private String logoPath = "";
	private boolean includeDateInName = false;
	private boolean includeHeader = false;
	private boolean includeFooter = false;
	private boolean exportReport = true;

	public static final int PDF = 0;
	public static final int EXCEL = 1;
	public static final int HTML = 2;

	private JasperPrint jasperPrint;

	private long loginId;
	private long organizationId;

	private String reportTitle;
	private String reportSubTitle;
	private String officeName;
	private String reportFile;
	S_OfficeModel ofcModel;
	private WrappedSession session;
	private SettingsValuePojo settings;

	public Report(Long loginId) {
		detectOS();
		setReportFileName("webspark");
		setJrxmlFileName("webspark");
		setIncludeDateInName(true);
		setExportReport(true);
		setReportType(1);
		setLoginId(loginId);

		session = new SessionUtil().getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		organizationId = Long.parseLong(session.getAttribute("organization_id").toString());
		try {
			ofcModel = new OfficeDao().getOffice(Long.parseLong(session.getAttribute("office_id").toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Report(String jrxmlFileName, String reportFileName, int reportType, boolean includeDate, long loginId) {
		detectOS();
		setReportFileName(reportFileName);
		setJrxmlFileName(jrxmlFileName);
		setIncludeDateInName(includeDate);
		setReportType(reportType);
		setLoginId(loginId);

		session = new SessionUtil().getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		organizationId = Long.parseLong(session.getAttribute("organization_id").toString());
		try {
			ofcModel = new OfficeDao().getOffice(Long.parseLong(session.getAttribute("office_id").toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createReport(List<Object> reportList, Map<String, Object> parameters) {

		try {

			String reportPath = "";
			String headerFile = "";
			String footerFile = "";
			String jrxmlFile = "";
			String jasperPath = "";
			String headerImage = "";
			String footerImage = "";

			String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/");

			formatReportFileName();

			jrxmlFileName += ".jrxml";

			if (isWindows) {
				headerFile = rootPath + "Jasper\\Header.jasper";
				footerFile = rootPath + "Jasper\\Footer.jasper";
				jasperPath = rootPath + "Jasper\\";
				reportPath = rootPath + "Reports\\" + reportFileName;
				logoPath = rootPath + "VAADIN\\themes\\testappstheme\\OrganizationLogos\\";

				headerImage = rootPath + "images/" + ofcModel.getHeader();
				footerImage = rootPath + "images/" + ofcModel.getFooter();

				File file = new File(headerImage);
				if (file == null || !file.exists()) {
					headerImage = rootPath + "images/blank.png";
				}
				file = new File(footerImage);
				if (file == null || !file.exists()) {
					footerImage = rootPath + "images/blank.png";
				}

			} else {
				headerFile = rootPath + "Jasper/Header.jasper";
				footerFile = rootPath + "Jasper/Footer.jasper";
				jasperPath = rootPath + "Jasper/";
				reportPath = rootPath + "Reports/" + reportFileName;
				logoPath = rootPath + "VAADIN/themes/testappstheme/OrganizationLogos/";

				headerImage = rootPath + "images/" + ofcModel.getHeader();
				footerImage = rootPath + "images/" + ofcModel.getFooter();

				File file = new File(headerImage);
				if (file == null || !file.exists()) {
					headerImage = rootPath + "images/blank.png";
				}
				file = new File(footerImage);
				if (file == null || !file.exists()) {
					footerImage = rootPath + "images/blank.png";
				}
			}

			String logoFileName = logoPath + organizationId + ".png";

			File logoFile = new File(logoFileName);
			if (logoFile.exists()) {
				logoPath += organizationId + ".png";
			} else {
				logoPath += "BaseLogo.png";
			}

			jrxmlFile = jasperPath + jrxmlFileName;

			File oldFile = new File(reportPath);
			if (oldFile.exists()) {
				oldFile.delete();
			}

			if (parameters == null) {
				parameters = new HashMap<String, Object>();
			}

			parameters.put("REPORT_TITLE", reportTitle);
			parameters.put("REPORT_SUB_TITLE", reportSubTitle);

			if (includeHeader) {
				parameters.put("HEADER_DIR", headerFile);
				parameters.put("LOGO_PATH", logoPath);
				populateHeaderReport(jasperPath, parameters);
				parameters.put("HEADER", headerImage);
			}
			if (includeFooter) {
				parameters.put("FOOTER", footerImage);
				// parameters.put("FOOTER_DIR", footerFile);
				// populateFooterReport(jasperPath);
			}

			jasperPrint = compileReportFile(reportList, parameters, jrxmlFile);
			reportFile = reportPath;
			if (isExportReport())
				exoprtReport(jasperPrint, reportPath);

			System.out.println("Filee EXPORTED--------------------------------> ");
		} catch (JRException e) {
			Notification.show("Report generation failed. Please try again..!", Type.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (Exception e) {
			Notification.show("Report generation failed. Please try again..!", Type.ERROR_MESSAGE);
			e.printStackTrace();
		}

		parameters.clear();
		reportList.clear();
		System.gc();
	}

	private JasperPrint compileReportFile(List<Object> reportList, Map<String, Object> parameters, String jrxmlfile)
			throws JRException {

		String jasperFile = jrxmlfile.replaceAll(".jrxml", ".jasper");
		JasperPrint print;

		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportList, true);

		JasperDesign jasperDesign = JRXmlLoader.load(jrxmlfile);

		JasperCompileManager.compileReportToFile(jasperDesign, jasperFile);

		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

		print = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		return print;
	}

	private void populateHeaderReport(String path, Map<String, Object> parameters) {

		AddressModel addressModel;
		AddressBusiness addrBusiness = new AddressBusiness();
		String orgName = "";
		String address = "";
		String date = "";
		String login = "";

		String jrxmlFile = path + "Header.jrxml";

		UserModel userModel;
		S_LoginModel loginMdl;
		try {
			userModel = new LoginDao().getUserFromLoginId(getLoginId());
			loginMdl = new LoginDao().getLoginModel(getLoginId());
			orgName = session.getAttribute("organization_name").toString();
			if (getOfficeName() == null || getOfficeName().trim().length() <= 0) {
				officeName = session.getAttribute("office_name").toString();
			}

			date = CommonUtil.getCurrentSQLDate().toString();

			if (userModel != null) {
				addressModel = userModel.getLoginId().getOffice().getOrganization().getAddress();
				login = userModel.getFirst_name() + " " + userModel.getMiddle_name() + " " + userModel.getLast_name();
			} else {
				addressModel = loginMdl.getOffice().getOrganization().getAddress();
				login = loginMdl.getLogin_name();
			}
			if (settings.isHIDE_ORGANIZATION_DETAILS()) {
				orgName = officeName;
				officeName = "";
				if (userModel != null)
					addressModel = userModel.getLoginId().getOffice().getOrganization().getAddress();
				else
					addressModel = loginMdl.getOffice().getOrganization().getAddress();
			}
			if (addressModel != null) {
				address = addrBusiness.getAddressString(addressModel.getId());
			}

			parameters.put("DATE", date);
			parameters.put("LOGIN", login);
			parameters.put("ORGANIZATION", orgName);
			parameters.put("OFFICE", officeName);
			parameters.put("ADDRESS", address);

			// List<Object> headerList = new ArrayList<Object>();
			// headerList.add(new ReportBean(orgName, officeName, address, date,
			// login));
			// Map<String, Object> paramMap = new HashMap<String, Object>();
			compileReportFile(null, new HashMap<String, Object>(), jrxmlFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private void populateFooterReport(String path) {
	// try {
	// String jrxmlFile = path + "Footer.jrxml";
	// compileReportFile(new ArrayList<Object>(),
	// new HashMap<String, Object>(), jrxmlFile);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	private void exoprtReport(JasperPrint jasperPrint, String report) throws JRException {

//		VaadinSession.getCurrent().addRequestHandler(new RequestHandler() {
//			
//			@Override
//			public boolean handleRequest(VaadinSession session, VaadinRequest request,
//					VaadinResponse response) throws IOException {
////				response.setContentType("application/pdf");
////				 
////				URL url = new URL(request.getParameter("url"));
////				PdfReader reader = new PdfReader(url);
////				try {
////					PdfStamper stamper = new PdfStamper(reader,response.getOutputStream());
////					PdfWriter writer = stamper.getWriter();
////		 
////					StringBuffer javascript = new StringBuffer();
////					javascript.append("JSSilentPrint(this)");
////		 
////					PdfAction pdfAction= PdfAction.javaScript(javascript.toString(), writer);
////					writer.addJavaScript(pdfAction);
////					stamper.close();
////		 
////				} catch (DocumentException de) {
////					de.printStackTrace();
////					System.err.println("document: " + de.getMessage());
////				}
////				return false;
//				
//				Document document = new Document();
//				response.setContentType("application/pdf");
//				try {
//					PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
//					document.open();
//		 
//					StringBuffer javascript = new StringBuffer();
//		 
//					javascript.append("var params = this.getPrintParams();");
//		 
//					javascript.append("params.interactive =	params.constants.interactionLevel.silent;");
//					javascript.append("params.printerName=\"MY_PRINTER_NAME\";");
//					javascript.append("params.pageHandling = params.constants.handling.shrink;");
//		 
//					javascript.append("this.print(params);");
//		 
//					PdfAction pdfAction= PdfAction.javaScript(javascript.toString(), writer);
//					writer.addJavaScript(pdfAction);
//		 
//					document.add(new Paragraph("Testing Silent Printing with iText"));
//		 
//					document.add(new Paragraph("Hello World"));
//					document.add(new Paragraph(new Date().toString()));
//				} catch (DocumentException de) {
//					de.printStackTrace();
//					System.err.println("document: " + de.getMessage());
//				}
//				document.close();
//				return true;
//			}
//		});

		switch (reportType) {
		case PDF:

			JasperExportManager.exportReportToPdfFile(jasperPrint, report);

			break;

		case EXCEL:
			JRXlsExporter exporterXLS = new JRXlsExporter();
			exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
			exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME, report);
			exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
			exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
			exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);
			exporterXLS.exportReport();
			break;

		case HTML:
			JRHtmlExporter exporter = new JRHtmlExporter();
			exporter.setParameter(JRHtmlExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, false);
			exporter.setParameter(JRHtmlExporterParameter.OUTPUT_FILE_NAME, report);
			exporter.exportReport();
			// JasperExportManager.exportReportToHtmlFile(jasperPrint, report);
			break;
		default:
			JasperExportManager.exportReportToPdfFile(jasperPrint, report);
			break;
		}

		File file = new File(report);

		FileResource resource = new FileResource(file);

		if (file != null && file.exists()) {

			Page.getCurrent().open(resource, reportFileName, true);

			file.deleteOnExit();

		} else {
			SNotification.show("Report generation failed. Please try again..!", Type.ERROR_MESSAGE);
		}

	}

	public void print() {

//		try {
//			JRExporter exporter = new JRPrintServiceExporter();
//
//			// --- Get printjob and service (default printer)
//			PrinterJob pj = PrinterJob.getPrinterJob();
//			PrintService ps = pj.getPrintService();
//
//			// --- Set print properties
//			PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
//			printRequestAttributeSet.add(MediaSizeName.ISO_A4);
//
//			// --- Set print parameters
//			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
//			exporter.setParameter(
//					JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET,
//					printRequestAttributeSet);
//			exporter.setParameter(
//					JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET,
//					ps.getAttributes());
//			exporter.setParameter(
//					JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG,
//					Boolean.FALSE);
//			exporter.setParameter(
//					JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG,
//					Boolean.TRUE);
//
//			exporter.exportReport();
//		} catch (Exception e) {
//			SNotification.show("No active printers found",
//					Type.TRAY_NOTIFICATION);
//		}

	}

	public void printReport() {
		try {

			switch (reportType) {
			case PDF:
				JRPdfExporter pdfexporter = new JRPdfExporter();
				pdfexporter.setParameter(JRPdfExporterParameter.JASPER_PRINT, jasperPrint);
				pdfexporter.setParameter(JRPdfExporterParameter.OUTPUT_FILE_NAME, reportFile);
				pdfexporter.setParameter(JRPdfExporterParameter.PDF_JAVASCRIPT, "this.print();");
				pdfexporter.exportReport();
				break;

			case HTML:
				JRHtmlExporter exporter = new JRHtmlExporter();
				exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, true);
				exporter.setParameter(JRHtmlExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRHtmlExporterParameter.OUTPUT_FILE_NAME, reportFile);
				exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER,
						"<html>" + "<head>" + "  <title></title>"
								+ "  <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>"
								+ "  <link rel=\"stylesheet\" type=\"text/css\" href=\"css/jasper.css\" />"
								+ "  <style type='text/css'>" + "    a {text-decoration: none}" + "  </style>"
								+ "</head>" + "<body text='#000000' link='#000000' alink='#000000' vlink='#000000'>"
								+ "<table width='100%' cellpadding='0' cellspacing='0' border='0'>"
								+ "<tr><td width='50%'>&nbsp;</td><td align='center'>");
				exporter.exportReport();

//				StringBuffer sbuf = new StringBuffer();
//				sbuf.append("<script type=\"text/javascript\">window.alert('hello');</script>");
//				pdfexporter.setParameter(JRHtmlExporterParameter.OUTPUT_STRING_BUFFER, sbuf);

				exporter.exportReport();
				break;

			default:
				break;
			}

		} catch (JRException e) {
			e.printStackTrace();
		}
	}

	private void formatReportFileName() {

		String time = new Date().toString().replace('.', ' ').replace(',', ' ').replace(':', ' ').replaceAll(" ", "");

		reportFileName = loginId + "" + reportFileName;
		if (isIncludeDateInName()) {
			reportFileName = reportFileName + time;
		}

		switch (reportType) {
		case PDF:
			reportFileName += ".pdf";
			break;
		case EXCEL:
			reportFileName += ".xls";
			break;

		case HTML:
			reportFileName += ".html";
			break;

		default:
			reportFileName += ".pdf";
			break;
		}

	}

	private void detectOS() {
		isWindows = false;
		isLinux = false;
		isMac = false;
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			isWindows = true;
		} else if (osName.contains("nix") || osName.contains("nux")) {
			isLinux = true;
		} else if (osName.contains("mac")) {
			isMac = true;
		}
	}

	public String getReportFileName() {
		return reportFileName;
	}

	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}

	public String getJrxmlFileName() {
		return jrxmlFileName;
	}

	public void setJrxmlFileName(String jrxmlFileName) {
		this.jrxmlFileName = jrxmlFileName;
	}

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public boolean isIncludeDateInName() {
		return includeDateInName;
	}

	public void setIncludeDateInName(boolean includeDateInName) {
		this.includeDateInName = includeDateInName;
	}

	public long getLoginId() {
		return loginId;
	}

	public void setLoginId(long loginId) {
		this.loginId = loginId;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public boolean isIncludeHeader() {
		return includeHeader;
	}

	public void setIncludeHeader(boolean includeHeader) {
		this.includeHeader = includeHeader;
	}

	public String getReportSubTitle() {
		return reportSubTitle;
	}

	public void setReportSubTitle(String reportSubTitle) {
		this.reportSubTitle = reportSubTitle;
	}

	public boolean isIncludeFooter() {
		return includeFooter;
	}

	public void setIncludeFooter(boolean includeFooter) {
		this.includeFooter = includeFooter;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getReportFile() {
		return reportFile;
	}

	public void setReportFile(String reportFile) {
		this.reportFile = reportFile;
	}

	public boolean isExportReport() {
		return exportReport;
	}

	public void setExportReport(boolean exportReport) {
		this.exportReport = exportReport;
	}

}