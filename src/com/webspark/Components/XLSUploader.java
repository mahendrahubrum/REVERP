package com.webspark.Components;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Sep 10, 2013
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;

public class XLSUploader implements Receiver {
	private int counter;
	private int total;
	private boolean sleep;
	private File file;

	/**
	 * return an OutputStream that simply counts lineends
	 */
	@Override
	public OutputStream receiveUpload(final String filename,
			final String MIMEType) {
		FileOutputStream fos = null; // Stream to write to
		try {

				System.out.println("Came......");
				// Open the file for writing.
				file = new File(VaadinServlet.getCurrent().getServletContext()
						.getRealPath("/")
						+ "TempImages/" + filename);
				fos = new FileOutputStream(file);

				System.out.println("Done......");

		} catch (final java.io.FileNotFoundException e) {
			new Notification("Could not open file<br/>", e.getMessage(),
					Notification.Type.ERROR_MESSAGE).show("Err");
			return null;
		}
		return fos;
	}

	public int getLineBreakCount() {
		return counter;
	}

	public void setSlow(final boolean value) {
		sleep = value;
	}

	public File getFile() {
		return file;
	}

	public void deleteFile() {
		file.delete();
		file=null;
	}

}
