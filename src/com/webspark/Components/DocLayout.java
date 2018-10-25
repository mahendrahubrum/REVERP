package com.webspark.Components;

import org.vaadin.sebastian.dock.Dock;
import org.vaadin.sebastian.dock.DockItem;
import org.vaadin.sebastian.dock.LabelPosition;
import org.vaadin.sebastian.dock.events.DockClickListener;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;

public class DocLayout extends AbsoluteLayout {
   
	public static String SAVE_SESSION="1", REPORT_ISSUE="2", HELP="3";
	Dock dock;
	public DocLayout(DockClickListener listener) {
		
		dock= new Dock();
		dock.addStyleName("dock_bottom");
		dock.setSize(50);
		dock.setSizeMax(150);
		dock.setAlignment(org.vaadin.sebastian.dock.Alignment.LEFT);
		dock.setLabelPosition(LabelPosition.MIDDLE_RIGHT);
		dock.addClickListener(listener);

		dock.addItem(new DockItem(new ThemeResource("icons/home_icon2.png"),"Home"));
		dock.addItem(new DockItem(new ThemeResource("icons/update_item.png"),"Logout"));
		dock.addItem(new DockItem(new ThemeResource("icons/convert.png"),"Logout"));
		dock.addItem(new DockItem(new ThemeResource("icons/home_icon2.png"),"Logout"));
		dock.addItem(new DockItem(new ThemeResource("icons/help_black.png"),"Help"));
		dock.addItem(new DockItem(new ThemeResource("icons/logout_btn.png"),"Logout"));
		
        SHorizontalLayout btns=new SHorizontalLayout();
        btns.setWidth("200");
        btns.setHeight("700");
        btns.setPrimaryStyleName("doc_lay_style");
        btns.addComponent(dock);
        addComponent(btns, "left:-198px; top:30px; z-index:100;");
        
        setStyleName("right_top_absolute_btn_lay");
    }
}