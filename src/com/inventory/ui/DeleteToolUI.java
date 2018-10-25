package com.inventory.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.dao.DeleteDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ProgressBar;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOptionGroup;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.OfficeDao;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Nov 15, 2013
 */
public class DeleteToolUI extends SparkLogic {

	private static final long serialVersionUID = -8671400450122209455L;

	private SComboField offiComboField;

	private SOptionGroup salesOptionGroup;
	private SOptionGroup purchaseOptionGroup;
	private SOptionGroup accountingOptionGroup;
	private SOptionGroup payrollOptionGroup;
	private SOptionGroup generalOptionGroup;

	private SButton deleteButton;

	private DeleteDao dao;

	private HashSet associatedSet;
	private HashSet supplierSet, userSet;

	@Override
	public SPanel getGUI() {

		setSize(800, 700);
		SPanel pan = new SPanel();
		pan.setSizeFull();

		SFormLayout main = new SFormLayout();
		main.setMargin(true);

		SGridLayout lay = new SGridLayout();
		lay.setMargin(true);
		lay.setRows(3);
		lay.setColumns(3);
		lay.setSpacing(true);

		

		dao = new DeleteDao();

		try {
			offiComboField = new SComboField(getPropertyName("office"), 200,
					new OfficeDao()
							.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name");
			offiComboField.setValue(getOfficeID());
		
		salesOptionGroup = new SOptionGroup("Sales", 200,
				SConstants.deleteObjects.salesdeleteTypes, "intKey", "value", true);
		salesOptionGroup.setImmediate(true);
		lay.addComponent(salesOptionGroup);
		
		purchaseOptionGroup=new SOptionGroup("Purchase",200,SConstants.deleteObjects.purchasedeleteTypes,"intKey","value",true);
		purchaseOptionGroup.setImmediate(true);
		lay.addComponent(purchaseOptionGroup);
		
		accountingOptionGroup=new SOptionGroup("Accounting",200,SConstants.deleteObjects.AccountingdeleteTypes,"intKey","value",true);
		accountingOptionGroup.setImmediate(true);
		lay.addComponent(accountingOptionGroup);
		
		payrollOptionGroup=new SOptionGroup("Payroll",200,SConstants.deleteObjects.payrolldeleteTypes,"intKey","value",true);
		payrollOptionGroup.setImmediate(true);
		lay.addComponent(payrollOptionGroup);
		
		generalOptionGroup=new SOptionGroup("General",200,SConstants.deleteObjects.generaldeleteTypes,"intKey","value",true);
		generalOptionGroup.setImmediate(true);
		lay.addComponent(generalOptionGroup);

		deleteButton = new SButton(getPropertyName("Delete"));
		deleteButton.setClickShortcut(KeyCode.ENTER);

		
		main.addComponent(offiComboField);
		main.addComponent(lay);
		main.addComponent(deleteButton);
		pan.setContent(main);

		deleteButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
						new ConfirmDialog.Listener() {

							@SuppressWarnings("unchecked")
							@Override
							public void onClose(ConfirmDialog dlg) {
								if (dlg.isConfirmed()) {

									Set<Integer> selected = new HashSet<Integer>();
									selected.addAll((Set)salesOptionGroup
											.getValue());
									selected.addAll((Set)purchaseOptionGroup.getValue());
									selected.addAll((Set)accountingOptionGroup.getValue());
									selected.addAll((Set)payrollOptionGroup.getValue());
									selected.addAll((Set)generalOptionGroup.getValue());
									
									

									if (selected.size() > 0) {


										List arraList = new ArrayList(selected);
										Collections.sort(arraList,
												new Comparator<Integer>() {

													@Override
													public int compare(
															Integer a, Integer b) {

														return a.compareTo(b);
													}

												});

										try {
											dao.delete(arraList,
													(Long) offiComboField
															.getValue());
											Notification
													.show(getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);
										} catch (Exception e) {
											Notification.show(
													getPropertyName("Error"),
													Type.ERROR_MESSAGE);
											e.printStackTrace();
										}
									} else {
										SNotification
												.show(getPropertyName("invalid_selection"),
														Type.WARNING_MESSAGE);
									}
								}
							}
						});

			}
		});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pan;
	}

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
