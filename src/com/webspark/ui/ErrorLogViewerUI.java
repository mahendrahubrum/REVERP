package com.webspark.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Apr 28, 2014
 */
public class ErrorLogViewerUI extends SparkLogic {

	private static final long serialVersionUID = 4335686171636868842L;

	SButton reloadButton;
	STextField linesField;
	STextArea logArea;

	@Override
	public SPanel getGUI() {

		setSize(850, 600);
		SPanel pan = new SPanel();
		pan.setSizeFull();

		SFormLayout lay = new SFormLayout();
		lay.setSpacing(true);

		SGridLayout hor = new SGridLayout(4, 1);
		hor.setSpacing(true);

		pan.setContent(lay);

		reloadButton = new SButton(getPropertyName("reload"));
		reloadButton.setClickShortcut(KeyCode.ENTER);
		linesField = new STextField();
		linesField.setValue("100");
		hor.addComponent(new SLabel(getPropertyName("no_lines")));
		hor.addComponent(linesField);
		hor.addComponent(reloadButton);

		logArea = new STextArea(null, 750, 450);

		lay.addComponent(hor);
		lay.addComponent(logArea);

		reloadButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					logArea.setValue("");
					long lineNo = toInt(linesField.getValue());

					File file = new File(System.getProperty("catalina.base")
							+ "/logs/catalina.out");

					try {
						RandomAccessFile randomAccessFile;
						randomAccessFile = new RandomAccessFile(file, "r");

						String log = "";
						int lines = 0;
						StringBuilder builder = new StringBuilder();
						long length = file.length();
						length--;
						randomAccessFile.seek(length);
						for (long seek = length; seek >= 0; --seek) {
							randomAccessFile.seek(seek);
							char c = (char) randomAccessFile.read();
							builder.append(c);
							if (c == '\n') {
								builder = builder.reverse();
								log += builder.toString();
								lines++;
								builder = null;
								builder = new StringBuilder();
								if (lines == lineNo) {
									break;
								}
							}

						}
						logArea.setValue(log);

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		});

		return pan;
	}

	@Override
	public Boolean isValid() {
		boolean flag = true;
		linesField.setComponentError(null);

		try {
			if (toInt(linesField.getValue()) <= 0) {
				setRequiredError(linesField, getPropertyName("invalid_data"),
						true);
				flag = false;
			}

		} catch (Exception e) {
			setRequiredError(linesField, getPropertyName("invalid_data"), true);
			flag = false;
		}
		return flag;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
