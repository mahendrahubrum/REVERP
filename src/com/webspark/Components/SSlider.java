package com.webspark.Components;

import com.vaadin.ui.Slider;

/**
 * @author Jinshad P.T.
 *
 * Jul 25, 2013
 */
public class SSlider extends Slider {

	public SSlider() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SSlider(double min, double max, int resolution) {
		super(min, max, resolution);
		// TODO Auto-generated constructor stub
	}

	public SSlider(int min, int max) {
		super(min, max);
		// TODO Auto-generated constructor stub
	}

	public SSlider(String caption, int min, int max) {
		super(caption, min, max);
		// TODO Auto-generated constructor stub
	}
	
	public SSlider(String caption, int min, int max, int width) {
		super(caption, min, max);
		setWidth(width+"px");
		// TODO Auto-generated constructor stub
	}

	public SSlider(String caption) {
		super(caption);
		// TODO Auto-generated constructor stub
	}

}
