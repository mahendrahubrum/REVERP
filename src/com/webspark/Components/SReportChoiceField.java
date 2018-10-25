package com.webspark.Components;

import com.webspark.common.util.SConstants;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 12, 2013
 */
public class SReportChoiceField extends SNativeSelect {
	private static final long serialVersionUID = 4055038284336800759L;

	public SReportChoiceField(String caption) {
		setCaption(caption);

		SCollectionContainer bic = SCollectionContainer.setList(
				SConstants.reportTypes, "intKey");
		setItemCaptionPropertyId("value");
		setContainerDataSource(bic);
		setNullSelectionAllowed(false);
		select(0);
	}
}
