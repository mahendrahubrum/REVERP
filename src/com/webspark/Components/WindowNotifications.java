package com.webspark.Components;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SLabel;
import com.webspark.Components.SVerticalLayout;

public class WindowNotifications extends AbsoluteLayout {
   
	public static String SAVE_SESSION="1", REPORT_ISSUE="2", HELP="3";
	
	SButton savetoSes,reportIssue,help;
	public WindowNotifications() {
		SLabel downArrow=new SLabel();
        SVerticalLayout btns=new SVerticalLayout();
        savetoSes=new SButton();
        savetoSes.setId(SAVE_SESSION);
//        savetoSes.setDescription("Save this on Session.");
        savetoSes.setPrimaryStyleName("save_session_style");
        reportIssue=new SButton();
//        reportIssue.setDescription("Report Issue on selected.");
        reportIssue.setId(REPORT_ISSUE);
        reportIssue.setPrimaryStyleName("report_issue_style");
        help=new SButton();
//        help.setDescription("Help");
        help.setId(HELP);
        help.setPrimaryStyleName("help_btn_style");
        downArrow.setStyleName("down_arrow_style");
        btns.setPrimaryStyleName("bbttnlay");
        btns.addComponent(savetoSes);
        btns.addComponent(reportIssue);
        btns.addComponent(help);
        btns.addComponent(downArrow);
        addComponent(btns, "top:-105px;left:96%; right:0px; z-index:100;");
        
        setStyleName("right_top_absolute_btn_lay");
    }
	
	public SButton getSaveSessionButton() {
		return savetoSes;
	}
	public SButton getReportIssueButton() {
		return reportIssue;
	}
	public SButton getHelpButton() {
		return help;
	}
	
	public void setClickListener(ClickListener listener) {
		savetoSes.addClickListener(listener);
		reportIssue.addClickListener(listener);
		help.addClickListener(listener);
	}
}