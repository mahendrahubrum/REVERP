package com.webspark.Components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.WrappedSession;
import com.webspark.common.util.SessionUtil;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 19, 2013
 */
public class SOfficeComboField extends SComboField {
	private static final long serialVersionUID = -1254873667481681944L;

	public SOfficeComboField(String caption, int width) {
		try {
			WrappedSession session= new SessionUtil().getHttpSession();
			setCaption(caption);
			setWidth(width + "px");

			SCollectionContainer bic;

			List<Object> list = new ArrayList<Object>();
			S_OfficeModel officeModel = new S_OfficeModel();
			officeModel.setId(0);
			officeModel.setName("ALL OFFICES");
			list.add(0, officeModel);
			list.addAll(new OfficeDao().getAllOfficeNamesUnderOrg((Long)session.getAttribute("organization_id")));
			bic = SCollectionContainer.setList(list, "id");

			setItemCaptionPropertyId("name");
			setContainerDataSource(bic);
			setNullSelectionAllowed(false);
			select((Long)session.getAttribute("office_id"));

			if((Boolean)session.getAttribute("isOrganizationAdmin") || 
					(Boolean)session.getAttribute("isSuperAdmin")|| 
					(Boolean)session.getAttribute("isSystemAdmin")) {
				setVisible(true);
			} else {
				setVisible(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public SOfficeComboField(String caption, int width, long org_id) {
		try {
			WrappedSession session= new SessionUtil().getHttpSession();
			setCaption(caption);
			setWidth(width + "px");

			SCollectionContainer bic;

			List<Object> list = new ArrayList<Object>();
			S_OfficeModel officeModel = new S_OfficeModel();
			officeModel.setId(0);
			officeModel.setName("ALL OFFICES");
			list.add(0, officeModel);
			list.addAll(new OfficeDao().getAllOfficeNamesUnderOrg((Long)session.getAttribute("organization_id")));
			bic = SCollectionContainer.setList(list, "id");

			setItemCaptionPropertyId("name");
			setContainerDataSource(bic);
			setNullSelectionAllowed(false);
			select((Long)session.getAttribute("office_id"));
			
			if((Boolean)session.getAttribute("isOrganizationAdmin") || 
					(Boolean)session.getAttribute("isSuperAdmin")|| 
					(Boolean)session.getAttribute("isSystemAdmin") || 
					(Boolean)session.getAttribute("daily_quotation_office_change_allowed")) {
				setVisible(true);
			} else {
				setVisible(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
}
