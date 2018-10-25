package com.webspark.Components;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Image;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Nov 22, 2013
 */
public class DocumentAttach extends SFormLayout {

	private static final long serialVersionUID = 3195816527410705407L;

	private SFileUpload fileUpload;
	private SFileUploder fileUploder;
	private SButtonLink fileNameLink;
	private SButton removeButton;

	private int attachemntType = 0;
	private FileDownloader downloader;
	private FileResource fileResource;
	private SPopupView popupView;
	private SFormLayout popupLayout;
	
	private SButtonLink downloadButton;
	
	private WrappedSession session;
	ResourceBundle bundle ;

	private Image image;

	public DocumentAttach(int attachemntType) {
		
		session=new SessionUtil().getHttpSession();
		if(session.getAttribute("property_file")!=null)
			bundle = ResourceBundle.getBundle(session.getAttribute("property_file").toString());
		
		

		this.attachemntType = attachemntType;

		SHorizontalLayout layout = new SHorizontalLayout();
		layout.setSpacing(true);

		fileNameLink = new SButtonLink();
		fileNameLink.setImmediate(true);

		fileUploder = new SFileUploder();
		fileUpload = new SFileUpload(null, fileUploder);
		fileUpload.setImmediate(true);
		if(attachemntType==SConstants.documentAttach.CHEQUE)
			fileUpload.setButtonCaption(getPropertyName("upload_cheque"));
		else if(attachemntType==SConstants.documentAttach.PURCHASE_BILL)
			fileUpload.setButtonCaption(getPropertyName("upload_bill"));
		
		removeButton = new SButton(null,getPropertyName("remove"));
		removeButton.setStyleName("deleteItemBtnStyle");

		downloadButton = new SButtonLink(getPropertyName("download"));

		popupLayout = new SFormLayout();
		popupLayout.setSizeFull();
		SFormLayout contentLayout = new SFormLayout();
		contentLayout.addComponent(downloadButton);
		contentLayout.addComponent(popupLayout);

		popupView = new SPopupView("", contentLayout);

		layout.addComponent(popupView);
		layout.addComponent(fileUpload);
		layout.addComponent(removeButton);
		layout.addComponent(fileNameLink);
		addComponent(layout);

		removeButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				if (fileUploder.getFile() != null) {
					
					ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
					
					

										File file = new File(getParentFolder()
												+ fileNameLink.getCaption());
										if (file.exists())
											file.delete();
										fileUploder.deleteFile();
										
										fileNameLink.setCaption("");
									
									}
								}
							});
				}
				
			}
		});

		fileUpload.addListener(new Listener() {
			@Override
			public void componentEvent(Event event) {
				if (fileUploder.getFile() != null) {
					fileNameLink.setCaption(fileUploder.getFile().getName());
				}
			}
		});

		fileNameLink.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				popupLayout.removeAllComponents();
				String fil = getParentFolder() + fileNameLink.getCaption();
				image = new Image("", new FileResource(new File(fil)));
				image.setStyleName("user_photo");
				image.setSizeFull();
				popupLayout.addComponent(image);
				popupView.setPopupVisible(true);
			}
		});

	}

	public void saveDocument(long billId, long officeId, int option) {
		// deleteDocument(billId, officeId, option);
		if (fileUploder.getFile() != null) {
			saveImageAsPNG(fileUploder.getFile(), getParentFolder()
					+ getFileName(billId, officeId, option));
			loadDocument(billId, officeId, option);
		}
	}

	private String getParentFolder() {
		String path = VaadinServlet.getCurrent().getServletContext()
				.getRealPath("/")
				+ "VAADIN/themes/testappstheme/Docs/";

		switch (attachemntType) {
		case SConstants.documentAttach.CHEQUE:
			path += "Cheques/";
			break;
		case SConstants.documentAttach.PURCHASE_BILL:
			path += "Purchase/";
			break;

		default:
			path += "Cheques/";
			break;
		}

		return path;
	}

	public void loadDocument(long billId, long officeId, int option) {

		File file = new File(getParentFolder()
				+ getFileName(billId, officeId, option));

		if (file.exists()) {
			fileNameLink.setCaption(file.getName());
			fileResource = new FileResource(file);
			if (downloader!=null&&downloadButton.getExtensions().size() > 0) {
				downloadButton.removeExtension(downloader);
			}
			downloader = new FileDownloader(fileResource);
			downloader.extend(downloadButton);
			downloadButton.setImmediate(true);
			fileUploder.receiveUpload(file.getName(), "image");
		}

	}

	public void deleteDocument(long billId, long officeId, int option) {
		File file = new File(getParentFolder()
				+ getFileName(billId, officeId, option));
		if (file.exists())
			file.delete();
	}

	private String getFileName(long billId, long officeId, int option) {
		return billId + "#" + officeId + "$" + option + ".png";
	}

	public void saveImageAsPNG(File file, String fileName) {

		BufferedImage bufferedImage;

		try {
			FileInputStream fis = new FileInputStream(file);
			bufferedImage = ImageIO.read(fis);
			if (bufferedImage == null) {
				bufferedImage = ImageIO.read(new File(getParentFolder()
						+ fileNameLink.getCaption()));
			}
			int width = bufferedImage.getWidth(), height = bufferedImage
					.getHeight();
			if (bufferedImage.getWidth() > 400) {
				int div = width / 400;
				width = 400;
				if (div > 1)
					height = height / div;
			}

			BufferedImage newBufferedImage = new BufferedImage((int) width,
					(int) height, BufferedImage.TYPE_INT_RGB);
			newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0,
					(int) width, (int) height, Color.WHITE, null);

			ImageIO.write(newBufferedImage, "png", new File(fileName));

		} catch (IOException e) {
		}
	}

	public void clear() {
		if (fileUploder.getFile() != null) {
			fileUploder.deleteFile();
			fileNameLink.setCaption("");
		}
	}
	
	public String getPropertyName(String name) {
		try {
			if(bundle!=null)
				name=bundle.getString(name);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return name;
	}

}
