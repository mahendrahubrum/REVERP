package com.webspark.Components;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickListener;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Nov 10, 2014
 */
public class ReportReview extends AbsoluteLayout {
   
	private static final long serialVersionUID = -8074877844608562294L;

	public static String REVIEW="1";
	
	SButton review;
	public ReportReview() {
		SLabel downArrow=new SLabel();
        SVerticalLayout btns=new SVerticalLayout();
        review=new SButton();
        review.setId(REVIEW);
        review.setPrimaryStyleName("review_style");
        downArrow.setStyleName("down_arrow_style");
        btns.setPrimaryStyleName("reviewlay");
        btns.addComponent(review);
        btns.addComponent(downArrow);
        addComponent(btns, "top:-40px; right:0px; z-index:100;");
        
        setStyleName("right_top_absolute_btn_lay");
    }
	
	public SButton getReviewButton() {
		return review;
	}
	
	public void setClickListener(ClickListener listener) {
		review.addClickListener(listener);
	}
}