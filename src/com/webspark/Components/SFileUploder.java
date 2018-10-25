package com.webspark.Components;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;


public class SFileUploder implements Receiver {
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
            	if (MIMEType.startsWith("image")) {
            	    
            	
            	
                // Open the file for writing.
                file = new File( VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"TempImages/"+filename);
                fos = new FileOutputStream(file);
                
                
            	}
            	else {
            		
            		  file = new File( VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"TempImages/"+filename);
                      fos = new FileOutputStream(file);
            	}
                
            } catch (final java.io.FileNotFoundException e) {
                new Notification("Could not open file<br/>",
                                 e.getMessage(),
                                 Notification.Type.ERROR_MESSAGE)
                    .show("Err");
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
        }
        

    }