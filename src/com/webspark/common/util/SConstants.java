package com.webspark.common.util;

import java.util.Arrays;
import java.util.List;

/**
 * @Author Jinshad P.T.
 */

public class SConstants {

//	 public static final String DB_NAME = "inv_new";
//	 public static final String DB_NAME = "deployment";
//	 public static final String DB_NAME = "ndg_inventory";
//	 public static final String DB_NAME = "moazasai_inventory";
//	 public static final String DB_NAME = "sparkbiz_inventory";
//	 public static final String DB_NAME = "moaza_new";
//	 public static final String DB_NAME = "flower_shop";
//	 public static final String DB_NAME="inventory_testing";

	public static final int HIB_FLUSH_LIMIT = 50;

	public static class tb_names {

		public static final String S_PROJECT_TYPES = "s_project_types";
		public static final String S_PROJECT_OPTION_MAP = "s_project_option_map";
		public static final String S_OPTION = "s_option";
		public static final String S_STATUS = "s_status";
		public static final String S_ID_GENERATOR_MASTER = "s_id_generator_master";
		public static final String S_ID_GENERATOR_VALUES = "s_id_generator_values";
		public static final String S_LOGIN = "s_login";
		public static final String S_OFFICE = "s_office";
		public static final String S_ORGANIZATION = "s_organization";
		public static final String S_MODULE = "s_modules";
		public static final String S_OPTION_GROUP = "s_option_groups";
		public static final String S_USER_ROLE = "s_user_roles";
		public static final String S_ROLE_OPTION_MAPPING = "s_role_option_mapping";
		public static final String S_DEPARTMENT_OPTION_MAPPING = "s_department_option_mapping";
		public static final String S_LOGIN_OPTION_MAPPING = "s_login_option_mapping";
		public static final String S_LOGIN_HISTORY = "s_login_history";

		public static final String S_SYSTEM_SETTINGS = "s_system_settings";

		public static final String ADDRESS = "s_address";

		public static final String COUNTRY = "country";
		public static final String CURRENCY = "currency";
		public static final String USER = "user_management";
		public static final String DESIGNATION = "designation";
		public static final String I_DEPARTMENT = "i_department";
		public static final String I_DIVISION = "i_division";
		public static final String DISTRIBUTOR = "distributor";
		public static final String CURRENCY_RATE = "currency_rate";
		public static final String PAF = "paf";
		public static final String LOGIN_PAF_MAP = "login_paf_map";
		public static final String PAF_RECORD = "paf_record";
		public static final String PAF_STATUS = "pafstatus";
		public static final String PAF_HISTORY = "paf_history";

		// Inventory Starts From Here

		public static final String I_BUILDING = "i_building";
		public static final String I_ROOM = "i_room";
		public static final String I_RACK = "i_rack";
		public static final String I_ITEM_GROUP = "i_item_group";
		public static final String I_ITEM_DEPARTMENT = "i_item_department";
		public static final String I_ITEM_SUBGROUP = "i_item_subgroup";

		public static final String I_ITEM_STOCK = "i_item_stock";

		// Tax

		public static final String I_TAX = "i_tax";
		public static final String I_TAX_GROUP = "i_tax_group";

		public static final String I_UNIT = "i_unit";
		public static final String I_ITEM_UNIT_MANAGEMENT = "i_item_unit_management";

		// ITEM

		public static final String I_ITEM_PRICE = "i_item_price";
		public static final String I_SALES_TYPE = "i_sales_type";
		public static final String I_ITEM = "i_item";
		public static final String I_ITEM_FEATURE = "i_item_feature";
		public static final String I_PAYMENT_TERMS = "i_payment_terms";

		// Accounting tables

		public static final String I_GROUP = "i_group";
		public static final String I_LEDGER = "i_ledger";

		public static final String I_CUSTOMER = "i_customer";
		public static final String I_SUPPLIER = "i_supplier";
		public static final String I_CLEARING_AGENT = "i_clearing_agent";
		public static final String I_BANK_ACCOUNTS = "i_bank_accounts";
		public static final String I_CONTRACTOR = "i_contractor";
		public static final String I_TRANSPORTATION = "i_transportation";

		// Purchase tables

		public static final String I_PURCHASE = "i_purchase";
		public static final String I_PURCHASE_INVENTORY_DETAILS = "i_purchase_inventory_details";
		public static final String I_PURCHASE_ORDER = "i_purchase_order";
		 public static final String I_PURCHASE_ORDER_DETAILS = "i_purchase_order_details";
		public static final String I_PURCHASE_RETURN = "i_purchase_return";

	

		public static final String I_STOCK_RACK_MAPPING = "i_stock_rack_mapping";
		public static final String I_STOCK_SALES_PRICE = "i_stock_sales_price";

		public static final String I_STOCK_TRANSFER = "i_stock_transfer";
		public static final String I_STOCK_TRANSFER_INVENTORY_DETAILS = "i_stock_transfer_inventory_details";
		public static final String I_STOCK_TRANSFER_LINK = "i_stock_transfer_link";

		// Sales Tables

		public static final String I_SALES_NEW = "i_sales_new";
		public static final String I_SALES_NEW_INVENTORY_DETAILS = "i_sales_new_inventory_details";
		public static final String I_SALES = "i_sales";
		public static final String I_SALES_INVENTORY_DETAILS = "i_sales_inventory_details";
		public static final String I_SALES_EXPENSE_DETAILS = "i_sales_expense_details";
		public static final String I_SALES_RETURN_EXPENSE_DETAILS = "i_sales_return_expense_details";
		
		public static final String I_SALES_ORDER = "i_sales_order";
		 public static final String I_SALES_ORDER_DETAILS = "i_sales_order_details";
		public static final String I_SALES_RETURN = "i_sales_return";

		public static final String I_DELIVERY_NOTE = "i_delivery_note";
		public static final String I_DELIVERY_NOTE_DETAILS = "i_delivery_note_details";
		public static final String I_WORK_ORDER = "i_work_order";

		

		public static final String I_TRANSFER_STOCK_MAP = "i_transfer_stock_map";
		public static final String I_MANUFACTURING_STOCK_MAP = "i_manufacturing_stock_map";

		public static final String I_TRANSACTIONS = "i_transactions";
		public static final String I_TRANSACTION_DETAILS = "i_transaction_details";

		public static final String I_SETTINGS = "i_settings";

		public static final String I_ACCOUNT_SETTINGS = "i_account_settings";

		public static final String I_JOURNAL = "i_journal";
		public static final String I_JOURNAL_DETAILS = "i_journal_details";

		// Payment

		public static final String I_PAYMENT = "i_payment";
		public static final String I_TRANSPORTATION_PAYMENT = "i_transportation_payment";
		public static final String I_EMPLOYEE_ADVANCE_PAYMENT = "i_employee_advance_payment";
		public static final String I_CASH_INVESTMENT = "i_cash_investment";

		public static final String I_BANK_ACCOUNT_PAYMENT = "i_bank_account_payment";
		public static final String I_BANK_ACCOUNT_PAYMENT_DETAILS = "i_bank_account_payment_details";
		public static final String I_BANK_ACCOUNT_DEPOSIT = "i_bank_account_deposit";
		public static final String I_BANK_ACCOUNT_DEPOSIT_DETAILS = "i_bank_account_deposit_details";

		// Payroll

		public static final String I_PAYROLL_COMPONENTS = "i_payroll_components";
		public static final String I_PAYROLL_EMPLOYEE_MAP = "i_payroll_employee_map";
		public static final String I_SALARY_DISBURSAL = "i_salary_disbursal";
		public static final String I_SALARY_DISBURSAL_DETAILS = "i_salary_disbursal_details";

		public static final String I_END_PROCESS = "i_end_process";
		public static final String I_ITEM_CLOSING_STOCK = "i_item_closing_stock";
		public static final String I_FINANCIAL_YEARS = "i_financial_years";

		public static final String I_ATTENDANCE_DETAILS = "i_attendance_details";

		public static final String I_PAYMENT_DEPOSIT = "i_payment_deposit";

		public static final String I_ITEM_TRANSFER = "i_item_transfer";
		public static final String I_ITEM_TRANSFER_LINK = "i_item_transfer_link";
		public static final String I_ITEM_TRANSFER_INVENTORY_DETAILS = "i_item_transfer_inventory_details";
		public static final String I_ITEM_RECEIVE = "i_item_receive";

		public static final String I_EMPLOYEE_WORKING_TIME = "i_employee_working_time";
		public static final String I_SALARY_DISBURSAL_NEW = "i_salary_disbursal_new";

		public static final String BILL = "bill";
		public static final String BILL_NAME = "bill_name";

		public static final String PRODUCT_LICENCE = "product_licence";

		public static final String ACTIVITY_LOG = "activity_log";
		public static final String SESSION_ACTIVITY = "session_activity";
		public static final String REVIEW = "review";
		public static final String REPORT_ISSUE = "report_issue";
		
		public static final String I_SALES_RETURN_INVENTORY_DETAILS = "i_sales_return_inventory_details";

		public static final String I_PRIVILAGE_SETUP = "i_privilage_setup";

		public static final String I_OFFICE_ALLOCATION = "i_office_allocation";

		public static final String I_MANUAL_TRADING_MASTER = "i_manual_trading_master";
		public static final String I_MANUAL_TRADING_DETAILS = "i_manual_trading_details";
		public static final String I_PURCHASE_RETURN_INVENTORY_DETAILS = "i_purchase_return_inventory_details";

		public static final String I_DAILY_QUOTATION = "i_daily_quotation";
		public static final String I_DAILY_QUOTATION_DETAILS = "i_daily_quotation_details";

		public static final String I_ITEM_DAILY_RATE = "i_item_daily_rate";
		public static final String I_ITEM_DAILY_RATE_DETAILS = "i_item_daily_rate_details";

		public static final String I_SUPPLIER_QUOTATION = "i_supplier_quotation";
		public static final String I_SUPPLIER_QUOTATION_DETAILS = "i_supplier_quotation_details";

		public static final String I_TASK_COMPONENTS = "i_task_components";
		public static final String I_TASK_COMPONENT_DETAILS = "i_task_component_details";
		public static final String I_TASKS = "i_tasks";
		public static final String I_TASKS_ASSIGNED_USERS = "i_tasks_assigned_users";

		public static final String I_CONTACT_CATEGORY = "i_contact_category";
		public static final String I_CONTACTS = "i_contacts";

		public static final String I_MAIL = "i_mail";
		public static final String I_MOBILE_APP_SETTINGS = "i_mobile_app_settings";
		public static final String I_MOBILE_APP_CONFIG = "i_mobile_app_config";

		public static final String I_STOCK_RESET_DETAILS = "i_stock_reset_details";

		public static final String I_SALES_MAN_MAP = "i_sales_man_map";

		public static final String I_FINANCE_COMPONENTS = "i_finance_components";
		public static final String I_FINANCE_PAYMENT = "i_finance_payment";
		public static final String I_FINANCE_PAYMENT_DETAILS = "i_finance_payment_details";

		public static final String LAUNDRY_SALES = "laundry_sales";
		public static final String LAUNDRY_SALES_DETAILS = "laundry_sales_details";

		public static final String I_TAILORING_SALES = "i_tailoring_sales";
		public static final String I_TAILORING_SALES_INVENTORY_DETAILS = "i_tailoring_sales_inventory_details";
		public static final String I_TAILORING_ITEM_SPEC = "i_tailoring_item_spec";

		public static final String I_SURVEY = "i_survey";
		public static final String I_COMMISSION_SALES = "i_commission_sales";

		public static final String I_MANUFACTURING = "i_manufacturing";
		public static final String I_MANUFACTURING_DETAILS = "i_manufacturing_details";
		public static final String I_MANUFACTURING_MAP = "i_manufacturing_map";

		public static final String EMAIL_CONFIGURATION = "email_configuration";
		public static final String MAILS = "mails";

		public static final String CUSTOMER_COMMISSION_SALES = "customer_commission_sales";
		public static final String COMMISSION_SALES_CUSTOMER_DETAILS = "commission_sales_customer_details";

		public static final String EXPENDETURE_COMPONENTS = "expendeture_components";
		public static final String EXPENDETURE_PAYMENT_SETUP = "expendeture_payment_setup";
		public static final String EXPENDETURE_PAYMENT_SETUP_DETAILS = "expendeture_payment_setup_details";

		public static final String I_QUOTATION = "i_quotation";
		public static final String I_QUOTATION_DETAILS = "i_quotation_details";

		public static final String I_ONLINE_CUSTOMER = "i_online_customer";
		public static final String I_ONLINE_SALES_ORDER = "i_online_sales_order";
		public static final String I_ONLINE_SALES_ORDER_DETAILS = "i_online_sales_order_details";

		public static final String I_BRAND = "i_brand";

		public static final String I_CUSTOMER_ENQUIRY = "i_customer_enquiry";
		public static final String I_SUPPLIER_QUOTATION_REQUEST = "i_supplier_quotation_request";
		public static final String I_SUPPLIER_PROPOSAL_RECEIPTION = "i_supplier_proposal_receiption";
		public static final String I_PROPOSALS_SENT_TO_CUSTOMERS = "i_proposals_sent_to_customers";

		public static final String BUDGET_DEFINITION = "budget_definition";
		public static final String BUDGET = "budget";
		public static final String BUDGETLV_MASTER = "budgetlv_master";
		public static final String BUDGETLV_CHILD = "budgetlv_child";

		public static final String I_ITEM_COMBO = "i_item_combo";
		public static final String I_ITEM_COMBO_DETAILS = "i_item_combo_details";

		public static final String BATCH_EXPENDITURE_PAYMENT_DETAILS = "batch_expenditure_payment_details";
		public static final String BATCH_EXPENDITURE_PAYMENT_MASTER = "batch_expenditure_payment_master";

		public static final String I_SALES_STOCK_MAP = "i_sales_stock_map";
		public static final String I_GRADE = "i_grade";

		public static final String RENT_MANAGEMENT = "rent_management";
		public static final String I_RENT_INVENTORY_DETAILS = "i_rent_inventory_details";
		public static final String RENT_ITEM_DETAILS = "rent_item_details";
		public static final String RENT_TYPE = "rent_type";
		public static final String RENT_ITEM_RETURN_DETAIL = "rent_item_return_detail";

		public static final String RENT_PAYMENT = "rent_payment";
		
		public static final String I_STOCK_CREATE_DETAILS = "i_stock_create_details";
		public static final String I_STOCK_CREATE = "i_stock_create";
		public static final String QUICK_MENUS = "quick_menus";
		
		public static final String I_PAYMENT_INVOICE_MAP = "i_payment_invoice_map";
		public static final String I_DEBIT_CREDIT_INVOICE_MAP = "i_debit_credit_invoice_map";
		
		public static final String I_COMMISSION_PURCHASE = "i_commission_purchase";
		public static final String I_COMMISSION_PURCHASE_DETAILS = "i_commission_purchase_details";
		public static final String I_COMMISSION_STOCK = "i_commission_stock";
		
		public static final String I_COMMISSION_SALES_NEW = "i_commission_sales_new";
		public static final String I_COMMISSION_SALES_DETAILS_NEW = "i_commission_sales_details_new";
		public static final String I_COMMISSION_PAYMENT = "i_commission_payment";

		
		public static final String I_SUBSCRIPTION_CONFIGURATION = "i_subscription_configuration";
		public static final String I_SUBSCRIPTION = "i_subscription";
		public static final String I_SUBSCRIPTION_PAYMENT = "i_subscription_payment";
		public static final String I_SUBSCRIPTION_IN = "i_subscription_in";
		public static final String I_SUBSCRIPTION_CREATION = "i_subscription_creation";
		public static final String I_SUBSCRIPTION_INVENTORY_DETAILS = "i_subscription_inventory_details";
		public static final String I_SUBSCRIPTION_PAYMENT_DETAILS = "i_subscription_payment_details";
		public static final String I_SUBSCRIPTION_EXPENDETURE = "i_subscription_expendeture";

		public static final String I_EMPLOYEE_DOCUMENT_CATEGORY = "i_employee_document_category";
		public static final String I_EMPLOYEE_DOCUMENT = "i_employee_document";
		
		public static final String OFFICE_OPTION_MAPPING = "s_office_option_mapping";
		public static final String ORGANIZATION_OPTION_MAPPING = "s_organization_option_mapping";
		
		public static final String I_PRODUCTION_UNIT = "i_production_unit";
		public static final String I_MATERIAL_MAPPING = "i_material_mapping";
		public static final String I_MATERIAL_MAPPING_DETAILS = "i_material_mapping_details";
		
		public static final String I_GCM_USERS = "gcm_users";
		
		public static final String S_LANGUAGE = "s_language";
		public static final String S_LANGUAGE_MAPPING = "s_language_mapping";
		
		public static final String I_ITEM_PHYSICAL_STOCK = "i_item_physical_stock";
		
		public static final String I_RENTAL_TRANSACTION = "i_rental_transaction";
		public static final String I_RENTAL_TRANSACTION_DETAILS = "i_rental_transaction_details";
		public static final String I_RENTAL_PAYMENT = "i_rental_payment";
		public static final String I_RENTAL_PAYMENT_MAP = "i_rental_payment_map";

		public static final String I_CUSTOMER_GROUP = "i_customer_group";
		
		public static final String I_COMMISSION_SALARY = "i_commission_salary";
		public static final String I_SALARY_COMMISSION_MAP = "i_salary_commission_map";
		public static final String I_SALARY_BALANCE_MAP = "i_salary_balance_map";
		
		public static final String I_SIZE = "i_size";
		public static final String I_COLOUR = "i_colour";
		public static final String I_REASON = "i_reason";
		public static final String I_STYLE = "i_style";
		public static final String I_MODEL = "i_model";
		public static final String I_CONTAINER = "i_container";
		public static final String I_VISA_TYPE = "i_visa_type";
		public static final String I_QUALIFICATION = "i_qualification";
		public static final String USER_QUALIFICATION = "user_qualification";
		
		public static final String I_DOCUMENT_ACCESS = "i_document_access";
		public static final String I_DOCUMENT_ACCESS_DETAILS = "i_document_access_details";
		
        public static final String USER_CONTACT = "user_contact";
        public static final String USER_FAMILY_CONTACT = "user_family_contact";
        public static final String USER_PREVIOUS_EMPLOYER = "user_previous_employer";
        
        public static final String I_PURCHASE_INQUIRY = "i_purchase_inquiry";
        public static final String I_PURCHASE_INQUIRY_DETAILS = "i_purchase_inquiry_details";

        public static final String I_PURCHASE_QUOTATION = "i_purchase_quotation";
        public static final String I_PURCHASE_QUOTATION_DETAILS = "i_purchase_quotation_details";
        
        public static final String I_LOCATION = "i_location";
        
        public static final String I_PURCHASE_GRN = "i_purchase_grn";
        public static final String I_PURCHASE_GRN_DETAILS = "i_purchase_grn_details";
        
        public static final String I_SALES_INQUIRY = "i_sales_inquiry";
        public static final String I_SALES_INQUIRY_DETAILS = "i_sales_inquiry_details";
        
        public static final String I_BATCH = "i_batch";
        public static final String I_LEDGER_OPENING_BALANCE = "i_ledger_opening_balance";
        
        public static final String I_PURCHASE_EXPENSE_DETAILS = "i_purchase_expense_details";
        public static final String I_PURCHASE_RETURN_EXPENSE_DETAILS = "i_purchase_return_expense_details";
        
        public static final String I_PROFORMA_PURCHASE = "i_proforma_purchase";
        public static final String I_PROFORMA_PURCHASE_INVENTORY_DETAILS = "i_proforma_purchase_inventory_details";
        public static final String I_PROFORMA_PURCHASE_EXPENSE_DETAILS = "i_proforma_purchase_expense_details";
        
        public static final String I_CASH_ACCOUNT_PAYMENT = "i_cash_account_payment";
        public static final String I_CASH_ACCOUNT_PAYMENT_DETAILS = "i_cash_account_payment_details";
        public static final String I_CASH_ACCOUNT_DEPOSIT = "i_cash_account_deposit";
        public static final String I_CASH_ACCOUNT_DEPOSIT_DETAILS = "i_cash_account_deposit_details";
        
        public static final String I_DEBIT_NOTE = "i_debit_note";
        public static final String I_DEBIT_NOTE_DETAILS = "i_debit_note_details";
        public static final String I_CREDIT_NOTE = "i_credit_note";
        public static final String I_CREDIT_NOTE_DETAILS = "i_credit_note_details";
        public static final String LOAN_REQUEST = "loan_request";
        public static final String LOAN_APPROVAL = "loan_approval";
        
        public static final String I_BANK_DETAILS_INVOICE_MAP = "i_bank_details_invoice_map";
        
        public static final String I_CHEQUE_RETURN = "i_cheque_return";
        public static final String I_CHEQUE_RETURN_DETAILS = "i_cheque_return_details";
        
        public static final String I_PDC = "i_pdc";
        public static final String I_PDC_DETAILS = "i_pdc_details";
        
        public static final String I_PDC_PAYMENT = "i_pdc_payment";
        public static final String I_PDC_PAYMENT_DETAILS = "i_pdc_payment_details";
        
        public static final String I_BANK_RECONCILIATION = "i_bank_reconciliation";
        public static final String I_HOLIDAYS = "i_holidays";
        
        public static final String I_ITEM_CUSTOMER_BARCODE_MAP = "i_item_customer_barcode_map";
        
        public static final String I_LEAVE_TYPE = "i_leave_type";
        public static final String I_ROLE_LEAVE_MAP = "i_role_leave_map";
        public static final String I_USER_LEAVE_ALLOCATION = "i_user_leave_allocation";
        public static final String I_LEAVE_HISTORY = "i_leave_history";
        public static final String I_LEAVE= "i_leave";
        public static final String I_LEAVE_DATE= "i_leave_date";
        
        public static final String I_FIXED_ASSET = "i_fixed_asset";
        public static final String I_FIXED_ASSET_PURCHASE = "i_fixed_asset_purchase";
        public static final String I_FIXED_ASSET_PURCHASE_DETAILS = "i_fixed_asset_purchase_details";
        
        public static final String I_ATTENDANCE = "i_attendance";
        public static final String I_OVER_TIME = "i_over_time";
        public static final String I_USER_LEAVE_MAP = "i_user_leave_map";
        
        public static final String I_STICKER_PRINTING = "i_sticker_printing";
        public static final String I_STICKER_PRINTING_DETAILS = "i_sticker_printing_details";
        
        public static final String I_FIXED_ASSET_SALES = "i_fixed_asset_sales";
        public static final String I_FIXED_ASSET_SALES_DETAILS = "i_fixed_asset_sales_details";
        public static final String I_FIXED_ASSET_DEPRECIATION = "i_fixed_asset_depreciation";

        public static final String I_FIXED_ASSET_DEPRECIATION_MAIN = "i_fixed_asset_depreciation_main";
        
        public static final String I_SALES_MAN_COMMISSION_MAP = "i_sales_man_commission_map";
        public static final String I_LOAN_DATE = "i_loan_date";
        
        public static final String I_EMPLOYEE_STATUS = "i_employee_status";
        
        public static final String H_TABLE = "h_table";
        public static final String H_ITEM_PRODUCTION = "h_item_production";
        public static final String H_CASH_PAY_DETAILS = "h_cash_pay_details";
        public static final String H_CUSTOMER_BOOKING_MODEL = "h_customer_booking_model";
        public static final String H_HOTEL_SALES_INVENTORY = "h_hotel_sales_inventory_details";
        public static final String H_HOTEL_SALES_INVENTORY_DETAILS = "h_hotel_sales_inventory_details";
        public static final String H_HOTEL_SALES = "h_hotel_sales";
        public static final String H_ITEM_PRODUCTION_DETAILS = "h_item_production_details";
        
        public static final String I_DISPOSE_ITEMS_MODEL = "i_dispose_items_model";
        public static final String I_DISPOSAL_ITEMS_DETAILS_MODEL = "i_disposal_items_details_model";
        
        public static final String I_GRV_SALES = "i_grv_sales";
        public static final String I_GRV_SALES_INVENTORY_DETAILS = "i_grv_sales_inventory_details";
        public static final String I_GRV_SALES_EXPENSE_DETAILS = "i_grv_sales_expense_details";
        
        public static final String I_MACHINE_MODEL = "i_machine";
        public static final String I_MOULD_MODEL = "i_mould";
        
        public static final String INVOICE_FORMAT = "invoice_format";
        public static final String I_PAYMENT_MODE_MODEL = "i_payment_mode";
        
        public static final String I_SALES_PAYMENT_MODE_DETAILS = "i_sales_payment_mode_details";
        
        public static final String I_SALES_HOLD = "i_sales_hold";
        public static final String I_SALES_INVENTORY_DETAILS_HOLD = "i_sales_inventory_details_hold";
	}

	public static class scopes {
		public static final int SYSTEM_LEVEL = 1;
		public static final int ORGANIZATION_LEVEL = 2;
		public static final int OFFICE_LEVEL = 3;
		public static final int LOGIN_LEVEL = 4;
		public static final int OFFICE_LEVEL_GENERAL = 5;

		public static List<KeyValue> scopeList = Arrays.asList(new KeyValue(
				(long) 1, "System Level"), new KeyValue((long) 2,
				"Organization Level"), new KeyValue((long) 3, "Office Level"),
				new KeyValue((long) 4, "Login Level"));

	}

	public static List<KeyValue> userTypes = Arrays.asList(new KeyValue(
			(long) 1, "Super Admin"), new KeyValue((long) 2, "Admin"),
			new KeyValue((long) 3, "Employ"), new KeyValue((long) 4, "Staff"));

	public static final int CASH = 1, CHECK = 2,SUPPLIER=3,CUSTOMER=4;
	
	
	public static List<KeyValue> cashOrCheckList = Arrays.asList(new KeyValue((int) 1, "Cash"), new KeyValue((int) 2, "Cheque"));
	
	public static class stoc_rack_map {

		public static List<KeyValue> purchase_mode = Arrays
				.asList(new KeyValue((long) 1, "Purchase Mode"));

		public static List<KeyValue> common_modes = Arrays.asList(new KeyValue(
				(long) 2, "Fully Arranged"), new KeyValue((long) 3,
				"Not Arranged"));

	}

	public static class statuses {

		public static final long BUILDING_ACTIVE = 1;
		public static final long ITEM_GROUP_ACTIVE = 1;
		public static final long ITEM_SUBGROUP_ACTIVE = 1;
		public static final long RACK_ACTIVE = 1;
		public static final long ROOM_ACTIVE = 1;

		public static final long GROUP_ACTIVE = 1;
		public static final long LEDGER_ACTIVE = 1;

		public static final long TAX_ACTIVE = 1;
		public static final long TAX_GROUP_ACTIVE = 1;

		public static final long SALES_TYPE_ACTIVE = 1;

		public static final long PAYMENT_TERMS_ACTIVE = 1;

		public static final long ITEM_ACTIVE = 1;

		public static final long SALES_ORDER_DIRECT = 1;
		public static final long SALES_ORDER_CUSTOMER_APPROVED = 2;
		public static final long SALES_ORDER_CUSTOMER_SUBMITTED = 3;
		public static final long SALES_ORDER_CUSTOMER_CREATED = 4;

		public static final long SALES_ORDER_ONLINE_APPROVED = 5;

		public static final int FINANCE_COMPONENT_ACTIVE = 1;
		public static final int FINANCE_COMPONENT_INACTIVE = 2;
		
		public static final short LOAN_APPLY = 0;
		public static final short LOAN_APPROVED = 1;
		public static final short LOAN_REJECTED = 2;
		
		public static final short STOCK_TRANSFER = 0;
		public static final short STOCK_RECEIVED = 2;


		public static List<KeyValue> status = Arrays.asList(new KeyValue(
				(long) 1, "Active"), new KeyValue((long) 2, "Inactive"));

	}

	public static class tax {

		public static List<KeyValue> taxTypes = Arrays.asList(new KeyValue(
				(long) 1, "Sales Tax"), new KeyValue((long) 2, "Purchase Tax"));

		public static List<KeyValue> taxValueTypes = Arrays.asList(
				new KeyValue((long) 1, "Percentage"), new KeyValue((long) 2,
						"Amount"));

		public static final long SALES_TAX = 1;
		public static final long PURCHASE_TAX = 2;
		
		public static final long PERCENTAGE = 1;
		public static final long AMOUNT = 2;

	}

	public static class composearchOptions {

		public static List<KeyValue> likeOption = Arrays.asList(new KeyValue(
				(long) 1, "Starts"), new KeyValue((long) 2, "Ends"),
				new KeyValue(3, "Like"));

		public static int MINIMUM_CHAR_LENGTH = 3;

		public static int MAXIMUM_DATA_SIZE = 10000;

	}

	public static List<KeyValue> maritalStatusOptions = Arrays.asList(
			new KeyValue('M', "Married"), new KeyValue('U', "Unmaried"),
			new KeyValue('D', "Divorced"), new KeyValue('W', "Widower"));

	public static List<KeyValue> genderOptions = Arrays.asList(new KeyValue(
			'M', "Male"), new KeyValue('F', "Female"));

	public static List<KeyValue> defaultDateSelectionOptions = Arrays.asList(
			new KeyValue(1, "System Date"), new KeyValue(2,
					"Office Working Date"));

	public static int SYSTEM_DATE = 1, OFFICE_WORKING_DATE = 2;

	public static List<KeyValue> dateformatsOptions = Arrays.asList(
			new KeyValue("dd/MM/yyyy", "dd/MM/yyyy"), new KeyValue(
					"dd/MMM/yyyy", "dd/MMM/yyyy"), new KeyValue("MM/dd/yyyy",
					"MM/dd/yyyy"), new KeyValue("MMM/dd/yyyy", "MMM/dd/yyyy"),
			new KeyValue("yyyy/MM/dd", "yyyy/MM/dd"), new KeyValue(
					"yyyy/MMM/dd", "yyyy/MMM/dd"));

	public static class settings {

		public static final String NO_OF_PRECISIONS = "NO_OF_PRECISIONS";
		public static final String DEFAULT_DATE_SELECTION = "DEFAULT_DATE_SELECTION";
		public static final String DATE_FORMAT = "DATE_FORMAT";

		public static final String CESS_ENABLED = "CESS_ENABLED";
		public static final String CESS_PERCENTAGE = "CESS_PERCENTAGE";
		public static final String TAX_ENABLED = "TAX_ENABLED";
		public static final String MANUFACTURING_DATES_ENABLE = "MANUFACTURING_DATES_ENABLE";
		public static final String DISCOUNT_ENABLE = "DISCOUNT_ENABLE";
//		public static final String EXCISE_DUTY_ENABLE = "EXCISE_DUTY_ENABLE";
//		public static final String SHIPPINGCHARGEENABLE = "SHIPPINGCHARGEENABLE";
		public static final String FIN_YEAR_BACK_ENTRY_ENABLE = "FIN_YEAR_BACK_ENTRY_ENABLE";
		public static final String STOCK_MANAGEMENT = "STOCK_MANAGEMENT";
		public static final String THEME = "THEME";

		// Account Settings
		public static final String INVENTORY_ACCOUNT = "INVENTORY_ACCOUNT";
		public static final String CASH_ACCOUNT = "CASH_ACCOUNT";

		public static final String SALES_ACCOUNT = "SALES_ACCOUNT";
		public static final String SALES_RETURN_ACCOUNT = "SALES_RETURN_ACCOUNT";
		public static final String CGS_ACCOUNT = "CGS_ACCOUNT";
		public static final String SALES_TAX_ACCOUNT = "SALES_TAX_ACCOUNT";
		public static final String SALES_SHIPPING_CHARGE_ACCOUNT = "SALES_SHIPPING_CHARGE_ACCOUNT";
		public static final String CESS_ACCOUNT = "CESS_ACCOUNT";
//		public static final String SALES_EXCISE_DUTY_ACCOUNT = "SALES_EXCISE_DUTY_ACCOUNT";
		public static final String SALES_DESCOUNT_ACCOUNT = "SALES_DESCOUNT_ACCOUNT";
		public static final String SALES_REVENUE_ACCOUNT = "SALES_REVENUE_ACCOUNT";
		public static final String FOREX_DIFFERENCE_ACCOUNT = "FOREX_DIFFERENCE_ACCOUNT";

		public static final String PURCHASE_ACCOUNT = "PURCHASE_ACCOUNT";
		public static final String PURCHASE_RETURN_ACCOUNT = "PURCHASE_RETURN_ACCOUNT";
		public static final String PURCHASE_TAX_ACCOUNT = "PURCHASE_TAX_ACCOUNT";
		public static final String PURCHASE_SHIPPING_CHARGE_ACCOUNT = "PURCHASE_SHIPPING_CHARGE_ACCOUNT";
		public static final String PURCHASE_DISCOUNT_ACCOUNT = "PURCHASE_DISCOUNT_ACCOUNT";
//		public static final String PURCHASE_EXCISE_DUTY_ACCOUNT = "PURCHASE_EXCISE_DUTY_ACCOUNT";
		public static final String CASH_PAYABLE_ACCOUNT = "CASH_PAYABLE_ACCOUNT";

		public static final String HIDE_ORGANIZATION_DETAILS = "HIDE_ORGANIZATION_DETAILS";

		public static final String EXPENDETURE_SHOW_ACCOUNTS = "EXPENDETURE_SHOW_ACCOUNTS";
		public static final String SHOW_ALL_EMPLOYEES_ON_PAYROLL = "show_all_employees_on_payroll";

		public static final String SYSTEM_EMAIL_HOST = "SYSTEM_EMAIL_HOST";
		public static final String SYSTEM_EMAIL = "SYSTEM_EMAIL";
		public static final String SYSTEM_EMAIL_PASSWORD = "SYSTEM_EMAIL_PASSWORD";

		public static final String SALES_EMAIL_HOST = "SALES_EMAIL_HOST";
		public static final String SALES_EMAIL = "SALES_EMAIL";
		public static final String SALES_EMAIL_PASSWORD = "SALES_EMAIL_PASSWORD";
		
		public static final String APPLICATION_EMAIL_HOST = "APPLICATION_EMAIL_HOST";
		public static final String APPLICATION_EMAIL = "APPLICATION_EMAIL";
		public static final String APPLICATION_EMAIL_PASSWORD = "APPLICATION_EMAIL_PASSWORD";

		public static final String BARCODE_ENABLED = "BARCODE_ENABLED";
		public static final String DEFAULT_CUSTOMER = "DEFAULT_CUSTOMER";

		public static final String KEEP_DELETED_DATA = "KEEP_DELETED_DATA";
		public static final String SALE_PRICE_EDITABLE = "SALE_PRICE_EDITABLE";
		public static final String PAYMENT_BILL_SELECTION_MANDATORY = "PAYMENT_BILL_SELECTION_MANDATORY";
		public static final String DISABLE_SALES_FOR_CUSTOMERS_UNDER_CR_LIMIT = "DISABLE_SALES_FOR_CUSTOMERS_UNDER_CR_LIMIT";
		public static final String ALERT_FOR_UNDER_CREDIT_LIMIT = "ALERT_FOR_UNDER_CREDIT_LIMIT";

		public static final String AUTO_CREATE_SUBGROUP_CODE = "AUTO_CREATE_SUBGROUP_CODE";
		public static final String AUTO_CREATE_CUSTOMER_CODE = "AUTO_CREATE_CUSTOMER_CODE";
		public static final String AUTO_CREATE_SUPPLIER_CODE = "AUTO_CREATE_SUPPLIER_CODE";
		public static final String USE_SALES_NO_IN_SALES_ORDER = "USE_SALES_NO_IN_SALES_ORDER";
		public static final String USE_SYSTEM_MAIL_FOR_SEND_CUSTOMERMAIL = "USE_SYSTEM_MAIL_FOR_SEND_CUSTOMERMAIL";
		public static final String KEEP_OTHER_WINDOWS = "KEEP_OTHER_WINDOWS";
		public static final String HIDE_ALERTS = "HIDE_ALERTS";
		public static final String GRADING_ENABLED = "GRADING_ENABLED";
//		public static final String LOCAL_FOREIGN_TYPE_ENABLED = "LOCAL_FOREIGN_TYPE_ENABLED";
		public static final String RACK_ENABLED = "RACK_ENABLED";
		public static final String MULTIPLE_CURRENCY_ENABLED = "MULTIPLE_CURRENCY_ENABLED";
		public static final String USE_GROSS_AND_NET_WEIGHT = "USE_GROSS_AND_NET_WEIGHT";
		public static final String USE_SALES_RATE_FROM_STOCK = "USE_SALES_RATE_FROM_STOCK";
		public static final String SHOW_STOCK_IN_PROFIT_REPORT = "SHOW_STOCK_IN_PROFIT_REPORT";
		public static final String SALES_NO_CREATION_MANUAL = "SALES_NO_CREATION_MANUAL";
		public static final String SHOW_CONTAINER_NO = "SHOW_CONTAINER_NO";
		public static final String SHOW_ITEM_ATTRIBUTES = "SHOW_ITEM_ATTRIBUTES";
		public static final String UPDATE_RATE_AND_CONV_QTY = "UPDATE_RATE_AND_CONV_QTY";
		public static final String CURRENCY_FORMAT = "CURRENCY_FORMAT";
		public static final String PROFIT_CALCULATION = "PROFIT_CALCULATION";
		public static final String BARCODE_TYPE = "BARCODE_TYPE";
		
		public static final String DIVISION_ENABLED = "DIVISION_ENABLED";
		public static final String DEPARTMENT_ENABLED = "DEPARTMENT_ENABLED";
		
		public static final String ALERT_EMAIL = "ALERT_EMAIL";
		public static final String ALERT_NOTIFICATION = "ALERT_NOTIFICATION";
		public static final String ALERT_EMAILIDS = "ALERT_EMAILIDS";
		
		public static final String SUPPLIER_GROUP = "SUPPLIER_GROUP";
		public static final String CUSTOMER_GROUP = "CUSTOMER_GROUP";
		public static final String CLEARING_AGENT_GROUP = "CLEARING_AGENT_GROUP";
		public static final String CASH_GROUP = "CASH_GROUP";
		public static final String CHEQUE_ACCOUNT = "CHEQUE_ACCOUNT";
		public static final String PROFIT_ACCOUNT = "PROFIT_ACCOUNT";
		public static final String LOSS_ACCOUNT = "LOSS_ACCOUNT";
		public static final String COMMISSION_SALARY_ENABLED = "COMMISSION_SALARY_ENABLED";
		
		public static final String SALARY_ACCOUNT = "SALARY_ACCOUNT";
		public static final String SALARY_ADVANCE_ACCOUNT = "SALARY_ADVANCE_ACCOUNT";
		public static final String SALARY_LOAN_ACCOUNT = "SALARY_LOAN_ACCOUNT";
		
		 public static final String SALARY_PAYABLE_ACCOUNT = "SALARY_PAYABLE_ACCOUNT";
		 public static final String SALES_MAN_WISE_SALES = "SALES_MAN_WISE_SALES";
		 public static final String PURCHSE_ORDER_EXPIRY = "PURCHSE_ORDER_EXPIRY";
		 
		 public static final String SALES_DISCOUNT_ENABLED = "SALES_DISCOUNT_ENABLED";
		 public static final String ITEMS_IN_MULTIPLE_LANGUAGE = "ITEMS_IN_MULTIPLE_LANGUAGE";
		 public static final String SHOW_SUPPLIER_SPECIFIC_ITEM_IN_PURCHASE = "SHOW_SUPPLIER_SPECIFIC_ITEM_IN_PURCHASE";
		 
		 public static final String ITEM_GROUP_FILTER_IN_SALES = "ITEM_GROUP_FILTER_IN_SALES";
		 public static final String SALES_ORDER_FOR_SALES = "SALES_ORDER_FOR_SALES";
		 public static final String PAYROLL_CALCULATION = "PAYROLL_CALCULATION";
	}

	public static final int CR = 1;
	public static final int DR = 2;

	public static final int SALES = 1;
	public static final int PURCHASE = 2;
	public static final int JOURNAL = 3;
	public static final int PURCHASE_RETURN = 4;
	public static final int SALES_RETURN = 5;
	public static final int SUPPLIER_PAYMENTS = 6;
	public static final int CUSTOMER_PAYMENTS = 7;
	public static final int BANK_ACCOUNT_PAYMENTS = 8;
	public static final int BANK_ACCOUNT_DEPOSITS = 9;
	public static final int EXPENDETURE_TRANSACTION = 10;
	public static final int INCOME_TRANSACTION = 11;
	public static final int CONTRACTOR_PAYMENTS = 12;
	public static final int TRANSPORTATION_PAYMENTS = 13;
	public static final int EMPLOYEE_ADVANCE_PAYMENTS = 14;
	public static final int PAYROLL_PAYMENTS = 15;
	public static final int INVESTMENT = 16;
	public static final int COMMISSION_PURCHASE = 17;
	public static final int COMMISSION_SALES = 18;
	public static final int RENT_PAYMENTS = 19;
	public static final int SUBSCRIPTION_PAYMENTS = 20;
	public static final int COMMISSION_PAYMENTS = 21;
	public static final int TRANSPORTATION_EXPENDITUE = 22;
	public static final int RENTAL_EXPENDETURE = 23;
	public static final int RENTAL_TRANSACTION = 23;
	public static final int RENTAL_PAYMENTS = 24;
	public static final int COMMISSION_SALARY = 25;
	public static final int CASH_ACCOUNT_PAYMENTS = 26;
	public static final int CASH_ACCOUNT_DEPOSITS = 27;
	public static final int DEBIT_NOTE = 28;
	public static final int CREDIT_NOTE = 29;
	public static final int PDC_PAYMENT = 30;
	public static final int SALARY_LOAN = 32;
	public static final int FIXED_ASSET_SALES = 31;
	public static final int FIXED_ASSET_PURCHASE = 33;
	public static final int FIXED_ASSET_DEPRECIATION = 34;
	

	public static final long ROLE_SUPER_ADMIN = 1;
	public static final long ROLE_SYSTEM_ADMIN = 2;
	public static final long SEMI_ADMIN = 3;
	public static final long ROLE_MANAGER = 4;
	public static final long ROLE_CUSTOMER = 8;
	public static final long ROLE_SPECIAL_ADMIN = 9;
	public static final long ROLE_SUPPLIER = 10;

	public static List<KeyValue> amountTypes = Arrays.asList(new KeyValue("CR",
			"CR"), new KeyValue("DR", "DR"));
	
	public static List<KeyValue> amountTypesWithId = Arrays.asList(new KeyValue(1,
			"CR"), new KeyValue(2, "DR"));

	public static List<KeyValue> optionStatusList = Arrays.asList(new KeyValue(
			(long) 1, "Active To All User"), new KeyValue((long) 0,
			"Active To Super User Only"));

	public static List<KeyValue> actClassList = Arrays.asList(new KeyValue(
			(long) 1, "Assets"), new KeyValue((long) 2, "Liabilities"),
			new KeyValue((long) 3, "Income"),
			new KeyValue((long) 4, "Expenses"));

	public static List<KeyValue> balanceSheetClassList = Arrays.asList(
			new KeyValue((long) 1, "Assets"), new KeyValue((long) 2,
					"Liabilities"));

	public static List<KeyValue> reportTypes = Arrays.asList(new KeyValue(
			(int) 0, "PDF"), new KeyValue((int) 1, "EXCEL"), new KeyValue(
			(int) 2, "HTML"));

//	public static final long CAPITAL_ACCT_GROUP_ID = 1;
//	public static final long CUSTOMER_GROUP_ID = 25;
//	public static final long CONTRACTOR_GROUP_ID = 25;
//	public static final long SUPPLIER_GROUP_ID = 22;
//	public static final long BANK_ACCOUNT_GROUP_ID = 17;
//	public static final long INDIRECT_EXPENSE_GROUP_ID = 15;
//	public static final long CASH_GROUP = 18;
//	public static final long CASH_RECEIVABLE = 18;

//	public static final long TRANSPORTATION_GROUP_ID = 3; // Current Liability

//	public static List<Long> acctList1 = Arrays.asList(new Long(
//			CUSTOMER_GROUP_ID), new Long(SUPPLIER_GROUP_ID));

//	public static List<Long> acctCustSuppl = Arrays.asList(new Long(
//			CUSTOMER_GROUP_ID), new Long(SUPPLIER_GROUP_ID));

	public static class payroll {

		public static List<KeyValue> action = Arrays.asList(new KeyValue(
				(long) 1, "Addition"), new KeyValue((long) 2, "Deduction"));

		public static List<KeyValue> type = Arrays.asList(new KeyValue(
				(long) 1, "Percentage"), new KeyValue((long) 2, "Fixed"));

		public static final long ADDITION = 1;
		public static final long DEDUCTION = 2;
		public static final long PERCENTAGE = 1;
		public static final long FIXED = 2;

		public static final int DAILY_SALARY = 1;
		public static final int MONTHLY_SALARY = 2;

		public static List<KeyValue> salaryTypes = Arrays.asList(new KeyValue(
				0, "No Payment"), new KeyValue(1, "Daily"), new KeyValue(2,
				"Monthly"));

		public static final long STATUS_NOT_PAID = 1;
		public static final long STATUS_PAID = 2;

	}

	public static List<KeyValue> paymentModeList = Arrays.asList(new KeyValue(
			(long) 1, "Cash"), new KeyValue((long) 2, "Credit"));

	public static List<KeyValue> filterTypeList = Arrays.asList(new KeyValue(0,
			"ALL"), new KeyValue(1, "Cash"), new KeyValue(2, "Credit"));

	public static List<KeyValue> saleOrPurchaseList = Arrays.asList(
			new KeyValue(1, "Sales"), new KeyValue(2, "Purchase"));

	public static List<KeyValue> clientsList = Arrays.asList(new KeyValue(1,
			"Customers"), new KeyValue(2, "Suppliers"));

	public static List<KeyValue> resetModeList = Arrays.asList(new KeyValue(1,
			"Daily Repeat"), new KeyValue(2, "Monthly Repeat"), new KeyValue(3,
			"Yearly Repeat"), new KeyValue(4, "Not Repeat"));

	public static final String DAY_END = "DAY_END";
	public static final String YEAR_END = "YEAR_END";
	public static final String MONTH_END = "MONTH_END";

	public static final int DAILY_REPEAT = 1;
	public static final int MONTHLY_REPEAT = 2;
	public static final int YEARLY_REPEAT = 3;
	public static final int NOT_REPEAT = 4;

	public static final int FIFO_STK = 1;
	public static final int LIFO_STK = 2;

	public static List<KeyValue> stock_management = Arrays.asList(new KeyValue(
			1, "FIFO"), new KeyValue(2, "LIFO"));
	public static List<KeyValue> theme = Arrays.asList(new KeyValue(
			1, "BASIC"), new KeyValue(2, "REVERP"));

	public static class bills {

		public static final int SALES = 1;
        public static final int PURCHASE = 2;
        public static final int SALES_RETURN = 3;
        public static final int PURCHASE_RETURN = 4;
        public static final int SALES_ORDER = 5;
        public static final int PURCHASE_ORDER = 6;
        public static final int COMMISSION_SALES = 7;
        public static final int QUOTATION = 8;
        public static final int SUPPLIER_VOVCHER = 9;
        public static final int CUSTOMER_RECEIPT = 10;
        public static final int CASH_VOVCHER = 11;
        public static final int BANK_VOVCHER = 12;
        public static final int BARCODE = 13;

        public static List<KeyValue> billTypes = Arrays.asList(new KeyValue(1,
                        "SALES"), new KeyValue(2, "PURCHASE"), new KeyValue(3,
                        "SALES_RETURN"), new KeyValue(4, "PURCHASE_RETURN"),
                        new KeyValue(5, "SALES_ORDER"), new KeyValue(6,
                                        "PURCHASE_ORDER"), new KeyValue(7, "COMMISSION_SALES"),
                        new KeyValue(8, "QUOTATION"),new KeyValue(9, "SUPPLIER_VOVCHER"),new KeyValue(10, "CUSTOMER_RECEIPT"),
                        new KeyValue(11, "CASH_VOVCHER"),new KeyValue(12, "BANK_VOVCHER"),new KeyValue(BARCODE, "BARCODE"));
	}

	public static class deleteObjectsOld {

		public static final int SALES = 1;
		public static final int PURCHASE = 2;
		public static final int SALES_RETURN = 3;
		public static final int PURCHASE_RETURN = 4;
		public static final int SALES_ORDER = 5;
		public static final int PURCHASE_ORDER = 6;
		public static final int DELIVERY_NOTE = 7;

		public static final int CONTRACTOR_PAYMENTS = 11;
		public static final int SUPPLIER_PAYMENTS = 12;
		public static final int CUSTOMER_PAYMENTS = 13;
		public static final int JOURNAL = 14;
		public static final int EXPENDETURE_TRANSACTION = 15;
		public static final int INCOME_TRANSACTION = 16;
		public static final int BANK_ACCOUNT_PAYMENTS = 17;
		public static final int BANK_ACCOUNT_DEPOSITS = 18;
		public static final int PAYROLL_TRANSACTIONS = 19;
		public static final int EMPLOYEE_ADVANCES = 20;

		public static final int STOCK_TRANSFER = 21;
		public static final int GENERAL_LEDGERS = 22;
		public static final int BANK_ACCOUNTS = 23;

		public static final int TRANSPORTATION_PAYMENT = 27;
		public static final int TRANSPORTATION = 28;

		public static final int DAILY_QUOTATION = 29;
		public static final int SUPPLIER_QUOTATION = 30;
		public static final int ITEM_DAILY_RATE = 31;
		public static final int MANUAL_TRADING = 32;

		public static final int CASH_INVESTMENTS = 33;

		public static final int SUPPLIER = 47;
		public static final int CUSTOMER = 48;
		public static final int CONTRACTOR = 49;

		public static final int STOCK = 50;
		public static final int ITEM = 51;
		public static final int TAX = 52;
		public static final int USERS = 53;
		public static final int BUILDING = 54;
		public static final int REMAINING = 55;
		public static final int WORK_ORDER = 56;
		public static final int RENT = 57;
		public static final int FINANCE_COMPONENT = 58;
		public static final int FINANCE_PAYMENT = 59;
		public static final int BUDGET = 60;
		public static final int TASK = 61;
		public static final int RENTAL = 62;
		
		public static List<KeyValue> deleteTypes = Arrays.asList(new KeyValue(
				SALES, "Sales"), new KeyValue(PURCHASE, "Purchase"),
				new KeyValue(SALES_RETURN, "Sales Return"), new KeyValue(
						PURCHASE_RETURN, "Purchase Return"), new KeyValue(
						PURCHASE_ORDER, "Purchase Order"), new KeyValue(
						SALES_ORDER, "Sales Order"), new KeyValue(
						DELIVERY_NOTE, "Delivery Note"), new KeyValue(
						TRANSPORTATION_PAYMENT, "Transportation Payment"),
				new KeyValue(CONTRACTOR_PAYMENTS, "Contractor Payments"),
				new KeyValue(CUSTOMER_PAYMENTS, "Customer Payments"),
				new KeyValue(SUPPLIER_PAYMENTS, "Supplier Payments"),
				new KeyValue(INCOME_TRANSACTION, "Income Transaction"),
				new KeyValue(EXPENDETURE_TRANSACTION, "Expense Transaction"),
				new KeyValue(BANK_ACCOUNT_DEPOSITS, "Bank Deposits"),
				new KeyValue(BANK_ACCOUNT_PAYMENTS, "Bank Payments"),
				new KeyValue(JOURNAL, "Journal"), new KeyValue(
						PAYROLL_TRANSACTIONS, "Payroll"),
				new KeyValue(EMPLOYEE_ADVANCES, "Employee Advances"),
				new KeyValue(STOCK_TRANSFER, "Stock Transfer"), new KeyValue(
						CUSTOMER, "Customer"), new KeyValue(SUPPLIER,
						"Supplier"), new KeyValue(CONTRACTOR, "Contractor"),
				new KeyValue(TRANSPORTATION, "Transportation"), new KeyValue(
						DAILY_QUOTATION, "Daily Quotation"), new KeyValue(
						SUPPLIER_QUOTATION, "Supplier Quotation"),
				new KeyValue(ITEM_DAILY_RATE, "Item Daily Rate"), new KeyValue(
						MANUAL_TRADING, "Manual Trading"), new KeyValue(
						CASH_INVESTMENTS, "Cash Investments"), new KeyValue(
						GENERAL_LEDGERS, "General Ledgers"), new KeyValue(
						STOCK, "Stock"), new KeyValue(ITEM, "Item"),
				new KeyValue(TAX, "Tax"),
				new KeyValue(WORK_ORDER, "Work Order"), new KeyValue(RENT,
						"Rent"), new KeyValue(FINANCE_COMPONENT,
						"Finanace Component"), new KeyValue(FINANCE_PAYMENT,
						"Finanace Payment"), new KeyValue(BUDGET, "Budget"),
				new KeyValue(BANK_ACCOUNTS, "Bank Accounts"), new KeyValue(
						USERS, "Users"),new KeyValue(TASK, "Task"),new KeyValue(RENTAL, "Rental"));

	}
	
	public static class deleteObjects {

		public static final int SALES_ENQUIREY = 1;
		public static final int SALES_QUOTATION= 2;
		public static final int SALES_ORDER = 3;
		public static final int DELIVERY_NOTE = 4;
		public static final int SALES = 5;
		public static final int SALES_RETURN = 6;
		public static final int GRV_SALES = 7;

		public static final int PURCHASE_INQUIRY = 11;
		public static final int PURCHASE_QUOTATION = 12;
		public static final int PURCHASE_ORDER= 13;
		public static final int GRN = 14;
		public static final int PURCHASE = 15;
		public static final int PURCHASE_RETURN = 16;
		public static final int STOCK_CREATE = 39;
		public static final int MANUFACTURING = 40;
		public static final int MANUFACTURINGMAP = 41;
		
		public static final int BANK_ACCOUNT_PAYMENTS = 17;
		public static final int BANK_ACCOUNT_DEPOSITS = 18;
		public static final int CASH_ACCOUNT_PAYMENTS = 19;
		public static final int CASH_ACCOUNT_DEPOSITS = 20;
		public static final int JOURNEL = 21;
		public static final int CREDIT_NOTE=22;
	    public static final int DEBIT_NOTE = 23;
	    public static final int PDC = 36;
	    public static final int CHEQUE_RETURN= 37;
		
		public static final int PAYROLL_TRANSACTIONS = 24;
		public static final int EMPLOYEE_ADVANCES = 25;
		public static final int EMPLOYEE_LOAN = 26;

		public static final int STOCK_TRANSFER = 27;
		public static final int GENERAL_LEDGERS = 28;
		public static final int BANK_ACCOUNTS = 29;

		
		public static final int SUPPLIER = 30;
		public static final int CUSTOMER = 31;
		public static final int CLEARING_AGENT= 38;

		public static final int ITEM = 32;
		public static final int USERS = 33;
		public static final int BUILDING =34;
		public static final int TASK = 35;
		
		public static List<KeyValue> salesdeleteTypes = Arrays.asList(new KeyValue(
				SALES_ENQUIREY, "Sales enquirey"), new KeyValue(SALES_QUOTATION, "sales quotation"),
				new KeyValue(SALES_ORDER, "Sales order"),  new KeyValue(
								DELIVERY_NOTE, "Delivery note"), new KeyValue(
										SALES, "Sales"), new KeyValue(
												SALES_RETURN, "Sales Return"),
							new KeyValue(GRV_SALES, "GRV Sales"));
				
		
		
		public static List<KeyValue> purchasedeleteTypes = Arrays.asList(new KeyValue(
				PURCHASE_INQUIRY, "Purchase inquiry"), new KeyValue(PURCHASE_QUOTATION, "purchase quotation"),
				new KeyValue(SALES_ORDER, "purchase order"), new KeyValue(
						PURCHASE_RETURN, "Purchase Return"), new KeyValue(
						PURCHASE_ORDER, "Purchase Order"), new KeyValue(
								GRN, "GRN"), new KeyValue(
										PURCHASE, "Purchase"), new KeyValue(
												STOCK_CREATE, "Stock Create"), new KeyValue(
														STOCK_CREATE, "Stock Create"),new KeyValue(
																MANUFACTURINGMAP, "Manufacturingmap"),new KeyValue(
																MANUFACTURING,"Manufacturing"));
		
		
		
		public static List<KeyValue> AccountingdeleteTypes = Arrays.asList(new KeyValue(
				BANK_ACCOUNT_PAYMENTS, "Bank Accounting Payments"), new KeyValue(BANK_ACCOUNT_DEPOSITS, "Bank Account Deposit"),
				new KeyValue(CASH_ACCOUNT_PAYMENTS, "Cash Account Payments"), new KeyValue(
						CASH_ACCOUNT_DEPOSITS, "Cash Account Deposit"), new KeyValue(
								JOURNEL, "Journel"), new KeyValue(
										CREDIT_NOTE, "Credit Note"),new KeyValue(
												DEBIT_NOTE, "Debit Note"),new KeyValue(
														PDC, "pdc"),new KeyValue(
																CHEQUE_RETURN, "Cheque return"));
		
		public static List<KeyValue> payrolldeleteTypes = Arrays.asList(new KeyValue(
				PAYROLL_TRANSACTIONS, "Payroll Transactions"), new KeyValue(EMPLOYEE_ADVANCES, "Employee Advances"),
				new KeyValue(EMPLOYEE_LOAN, "Employee Loan"));
		
		
		public static List<KeyValue> generaldeleteTypes = Arrays.asList(new KeyValue(
				STOCK_TRANSFER, "Stock Transfer"), new KeyValue(GENERAL_LEDGERS, "General Ledgers"),
				new KeyValue(BANK_ACCOUNTS, "Bank Accounts"),new KeyValue(SUPPLIER,"Supplier"),new KeyValue(CUSTOMER,"Customer"),
				new KeyValue(CLEARING_AGENT,"Clearing agent"),
				new KeyValue(ITEM,"Item"),new KeyValue(USERS,"users"),new KeyValue(BUILDING,"Building"),new KeyValue(TASK,"Task"));
		
		
		
	}

	public static List<KeyValue> creditOrDebit = Arrays.asList(new KeyValue(1,
			"Payment"), new KeyValue(2, "Credit"));

	public static List<KeyValue> depositOrWithdrowal = Arrays.asList(
			new KeyValue(1, "Deposit"), new KeyValue(2, "Withdrawal"));

	public static class documentAttach {
		public static final int CHEQUE = 1;
		public static final int PURCHASE_BILL = 2;

	}

	public static class privilegeTypes {

		public static final int EDIT_SALES = 1;
		public static final int PRINT_PURCHASE = 2;
		public static final int ADD_TASK = 3;
		public static final int DAILY_QUOTATION_REPORT = 4;
		public static final int ADD_CONTACT_FOR_OTHERS = 5;
		public static final int EDIT_PURCHASE = 7;
		public static final int PRINT_SALE = 6;
		public static final int SALES_ADMIN = 8;
		public static final int SALES_DISCOUNT_ENABLED = 9;

		public static List<KeyValue> privilageTypes = Arrays.asList(
				new KeyValue((int) 1, "Edit Sales"), new KeyValue((int) 7,
						"Edit Purchase"), new KeyValue((int) 6, "Print Sale"),
				new KeyValue((int) 2, "Print Purchase"), new KeyValue((int) 3,
						"Task Assign"), new KeyValue((int) 4,
						"Daily Quotation Report"), new KeyValue((int) 5,
						"Add Contact For Others"), new KeyValue( SALES_ADMIN, "Sales Admin")
						, new KeyValue( SALES_DISCOUNT_ENABLED, "Sales Discount Enabled"));
	}

//	public static List<Long> specificLedgers = Arrays.asList(new Long(
//			CUSTOMER_GROUP_ID), new Long(SUPPLIER_GROUP_ID), new Long(
//			CONTRACTOR_GROUP_ID));

//	public static List<Long> bankAndCash = Arrays.asList(new Long(CASH_GROUP),
//			new Long(BANK_ACCOUNT_GROUP_ID));

	public static final int LEDGER_ADDED_INDIRECTLY = 0;
	public static final int LEDGER_ADDED_DIRECTLY = 1;

	public static final int BASIC_THEME = 1;
	public static final int REVERP_THEME = 2;

	public static class alerts {

		public static final String SALES_ALERTS = "SALES_ALERTS";
		public static final String PURCHASE_ALERTS = "PURCHASE_ALERTS";
		public static final String CUSTOMER_PAY_ALERT = "CUSTOMER_PAY_ALERT";
		public static final String SUPPLIER_PAY_ALERT = "SUPPLIER_PAY_ALERT";
		public static final String PAYROLL_ALERTS = "PAYROLL_ALERTS";
		public static final String EXP_TRANS_ALERTS = "EXP_TRANS_ALERTS";
		public static final String BANK_TRANS_ALERTS = "BANK_TRANS_ALERTS";
		public static final String CUSTOMER_SO_ALERT = "CUSTOMER_SO_ALERT";
		public static final String TASK_ALERT = "TASK_ALERT";

	}

	public static int SALES_MAN = 1, PURCHASE_MAN = 2;

	public static long SALES_RETURN_STOCK_STATUS = 3;

	public static class tailoring {
		public static long TYPE_CHECKBOX = 1;
		public static long TYPE_TEXT = 2;
		public static List<KeyValue> tailoringTypes = Arrays.asList(
				new KeyValue((long) TYPE_CHECKBOX, "CheckBox"), new KeyValue(
						(long) TYPE_TEXT, "Text"));
	}
	
	public static class affect_type {
		public static int AFFECT_ALL = 1;
		public static int PURCHASE_ONLY = 3;
		public static int SALES_ONLY = 2;
		public static int MANUFACTURING = 4;

		public static List<KeyValue> affect_type = Arrays.asList(new KeyValue(
				AFFECT_ALL, "Affect All"), new KeyValue(PURCHASE_ONLY,
				"Only Purchase"), new KeyValue(SALES_ONLY, "Only Sale"),
				new KeyValue(MANUFACTURING, "Manufacturing"));
	}

	public static List<KeyValue> emailFoldersList = Arrays.asList(new KeyValue(
			0, "ALL"), new KeyValue(1, "Inbox"), new KeyValue(2, "Sent"),
			new KeyValue(3, "Drafts"));

	public static List superiorList = Arrays.asList(ROLE_SYSTEM_ADMIN,
			ROLE_SPECIAL_ADMIN, SEMI_ADMIN, ROLE_MANAGER);

	public static List<KeyValue> budgetTypes = Arrays.asList(new KeyValue(
			(long) 0, "Daily"), new KeyValue((long) 1, "Weekly"), new KeyValue(
			(long) 2, "Monthly"), new KeyValue((long) 3, "Quarterly"),
			new KeyValue((long) 4, "Yearly"));

	public static List<KeyValue> proposalStatuses = Arrays.asList(new KeyValue(
			(long) 1, "Approved"), new KeyValue((long) 2,
			"Kept for Information"));
	
	public static List<KeyValue> periodTypes = Arrays.asList(new KeyValue(
			(long) 0, "hourly"), new KeyValue((long) 1, "Daily"), new KeyValue(
			(long) 2, "Weekly"), new KeyValue((long) 3, "Monthly"),
			new KeyValue((long) 4, "Yearly"));
	
	public static List<KeyValue> returnTypeList = Arrays.asList(new KeyValue(0,
			"ALL"), new KeyValue(1, "Returned"), new KeyValue(2,
			"Partially Returned"), new KeyValue(3, "Not Returned"));
	
	public static class local_foreign_type {
		public static int LOCAL = 1;
		public static int FOREIGN = 2;
		
		public static List<KeyValue> local_foreign_type = Arrays.asList(
				new KeyValue(LOCAL, "Local"), new KeyValue(FOREIGN, "Foreign"));
	}
	
	public static class rateAndConvQty_update {
		public static int NOT_UPDATE = 0;
		public static int RATE_ONLY = 1;
		public static int CONVERTION_QTY_ONLY = 2;
		public static int UPDATE_ALL = 3;
		
		public static List<KeyValue> rate_update_select = Arrays.asList(
				new KeyValue(NOT_UPDATE, "Not Update"), new KeyValue(RATE_ONLY, "Update Rate Only"),
			new KeyValue(CONVERTION_QTY_ONLY, "Update Convertion Qty Only"), new KeyValue(UPDATE_ALL, "Update ALL"));
	}
	
	
	public static class currencyFormat {
		public static int LAKHS = 0;
		public static int MILLIONS = 1;
		
		public static List<KeyValue> currencyFormat = Arrays.asList(
				new KeyValue(LAKHS, "Lakhs"), new KeyValue(MILLIONS, "Millions"));
	}
	
	public static class materialSource {
		public static int CUSTOMER = 1;
		public static int STOCK = 2;

		public static List<KeyValue> materialSource = Arrays.asList(
				new KeyValue(CUSTOMER, "Customer"), new KeyValue(STOCK, "Stock"));
	}
	
	public static class stock_statuses {
		public static long GRV_STOCK = 3;
		public static long GOOD_STOCK = 5;
		public static long MANUFACTURED_STOCK = 6;
		public static long TRANSFERRED_STOCK = 7;	
		public static long PURCHASED_STOCK = 2;	
		public static long ITEM_CREATION_STOCK = 8;	
		
	}
	
	public static List<KeyValue> period = Arrays.asList(new KeyValue((long) 1, "Daily"), new KeyValue(
			(long) 2, "Weekly"), new KeyValue((long) 3, "Monthly"),
			new KeyValue((long) 4, "Yearly"));
	
	public static List<KeyValue> allPeriod = Arrays.asList(new KeyValue((long) 0, "------------------All------------------"),new KeyValue((long) 1, "Daily"), new KeyValue(
			(long) 2, "Weekly"), new KeyValue((long) 3, "Monthly"),
			new KeyValue((long) 4, "Yearly"));
	
	
	public static List<KeyValue> periodType = Arrays.asList(
			new KeyValue((long) 1, "Days"), 
			new KeyValue((long) 2, "Weeks"),
			new KeyValue((long) 3, "Months"),
			new KeyValue((long) 4, "Years"));
	
	public static class paymentModes {
		public static int CASH = 1;
		public static int CREDIT = 2;
		public static int CHEQUE = 3;
		public static int CREDIT_CARD = 4;
		
		public static List<KeyValue> paymentModes = Arrays.asList(new KeyValue(
			 1, "Cash"), new KeyValue( 2, "Credit"), new KeyValue(3, "Cheque")
			, new KeyValue( 4, "Credit Card"));
		}
	
	public static List<KeyValue> rentalList = Arrays.asList(new KeyValue((long) 2, "Income"),new KeyValue((long) 3, "Transportation Supplier"));
	public static List<KeyValue> rentalTypeList = Arrays.asList(new KeyValue((long) 1, "Rent In"),new KeyValue((long) 2, "Rent Out"),new KeyValue((long) 3, "Both"));
	public static List<KeyValue> specialRentalTypeList = Arrays.asList(new KeyValue((long) 1, "Rent In"),new KeyValue((long) 2, "Rent Out"));
	public static List<KeyValue> accountList = Arrays.asList(new KeyValue((long) 1, "Customer"),new KeyValue((long) 2, "Transportation Supplier"));

	public static int RECENTLY_USED_OPTIONS_COUNT = 7;
	
	
	public static List<KeyValue> filterDayList = Arrays.asList(new KeyValue(
			(long) 1, "Daily"), new KeyValue((long) 2, "Weekly"), new KeyValue(
			(long) 3, "Monthly"));
	
	
	public static class account_parent_groups {
		public static long ASSET = 1;
		public static long LIABILITY = 2;
		public static long INCOME = 3;
		public static long EXPENSE = 4;
		
		public static List<KeyValue> classList = Arrays.asList(new KeyValue(ASSET, "ASSET"),
				new KeyValue(LIABILITY, "LIABILITY"), new KeyValue(EXPENSE, "EXPENSE"), new KeyValue(INCOME,
						"INCOME"));
	}
	
	public static List<KeyValue> transactionType = Arrays.asList(new KeyValue(1,"CR"), new KeyValue(2, "DR"));
	
	 public static class stockPurchaseType {

		   public static final int PURCHASE_GRN = 1;
	        public static final int PURCHASE = 2;
	        public static final int SALES_RETURN = 3;
	        public static final int STOCK_TRANSFER =4;
	        public static final int STOCK_CREATE =5;
 }
	 
	 public static class paymentMode {

			public static List<KeyValue> paymentModeList = Arrays.asList(new KeyValue((long) 1, "Payment"), new KeyValue((long) 2, "Credit"));

			public static final long PAYMENT = 1;
			public static final long CREDIT = 2;
			
			public static List<KeyValue> cashChequeList = Arrays.asList(new KeyValue((long) 1, "Cash"), new KeyValue((long) 2, "Cheque"));
			public static List<KeyValue> cashCardList = Arrays.asList(new KeyValue((long) 1, "Cash"), new KeyValue((long) 2, "Card"));
			public static final long CASH = 1;
			public static final long CHEQUE = 2;
	 }
	 
	 public static List<KeyValue> ledgerType = Arrays.asList(new KeyValue((long)1,"General"), new KeyValue((long)2, "Clearing Agent"));
     public static final long GENERAL = 1;
     public static final long CLEARING_AGENT = 2;
     
     public static final int NOT_PAID = 1, PARTIALLY_PAID = 2, FULLY_PAID=3;
     
     
     public static class bank_account{
    	 public static int CASH = 1;
     	 public static int CHEQUE = 2;
         public static int SUPPLIER = 3;
         public static int CUSTOMER = 4;
         public static int OTHERS = 5;
         
    	 public static List<KeyValue> cashChequeSupplierList = Arrays.asList(new KeyValue((int) 1, "Cash"), 
                 new KeyValue((int) 2, "Cheque"), 
                 new KeyValue((int) 3, "Supplier"),new KeyValue(OTHERS, "Others"));
         
         public static List<KeyValue> cashChequeCustomerList = Arrays.asList(new KeyValue((int) 1, "Cash"), 
                 new KeyValue((int) 2, "Cheque"), 
                 new KeyValue((int) 4, "Customer"),new KeyValue(OTHERS, "Others"));
         
     }
     
     
     public static class cash_account{
         
    	 public static List<KeyValue> cashSupplierList = Arrays.asList(new KeyValue((long) 1, "Supplier"),new KeyValue((long) 4, "Expense"));
         
         public static List<KeyValue> cashCustomerList = Arrays.asList(new KeyValue((long) 2, "Customer"), new KeyValue((long) 3, "Income"));
         
     	 public static long SUPPLIER = 1;
     	 public static long CUSTOMER = 2;
         public static long INCOME = 3;
         public static long EXPENSE = 4;
         
     }
     
     public static class creditDebitNote{
    	 public static List<KeyValue> supplierCustomerList = Arrays.asList(new KeyValue((int) 1, "Supplier"),new KeyValue((int) 2, "Customer"));
     	 public static int SUPPLIER = 1;
     	 public static int CUSTOMER = 2;
     	 public static int DEBIT = 3;
     	 public static int CREDIT = 4;
     }
     
     public static class SalesDeliveryType{
    	 public static int SALE_TYPE = 1;
    	 public static int DELIVERY_TYPE = 2;
     }
     
     public static class PDCStatus{
         public static int ISSUED = 1;
         public static int APPROVED = 2;
         public static int CANCELLED = 3;
 }

     public static class BankReconciliationStatus {
 		public static final int ALL = 0;
 		public static final int CLEARED = 1;
 		public static final int UNCLEARED = 2;	
 	}
     
 	public static class profitCalcutaion {
		public static int AVERAGE = 1;
		public static int FIFO = 2;
		public static int LIFO = 3;
		
		public static List<KeyValue> profitCalcutaion = Arrays.asList(
				new KeyValue(AVERAGE, "AVERAGE"), new KeyValue(FIFO, "FIFO"), new KeyValue(LIFO, "LIFO"));
	}
 	public static class barcode_types {
 		public static int STOCK_SPECIFIC = 1;
 		public static int CUSTOMER_SPECIFIC = 2;
 		public static int ITEM_SPECIFIC = 3;
 		
 		public static List<KeyValue> barcode_types = Arrays.asList(
 				new KeyValue(STOCK_SPECIFIC, "STOCK_SPECIFIC"), 
 				new KeyValue(CUSTOMER_SPECIFIC, "CUSTOMER_SPECIFIC"),
 				new KeyValue(ITEM_SPECIFIC, "ITEM_SPECIFIC"));
 	}
 	public static class payrollCalculation {
 		public static int TOTAL_WORKING_DAYS = 1;
 		public static int DAILY_ATTENDANCE = 2;
 		public static int BIOMETRIC_INTEGRATION = 3;
 		
 		public static List<KeyValue> payrollCalculation = Arrays.asList(
 				new KeyValue(TOTAL_WORKING_DAYS, "TOTAL_WORKING_DAYS"), 
 				new KeyValue(DAILY_ATTENDANCE, "DAILY_ATTENDANCE"),
 				new KeyValue(BIOMETRIC_INTEGRATION, "BIOMETRIC_INTEGRATION"));
 	}
 	
 	public static class weekDays {
        public static int SUNDAY = 1;
        public static int MONDAY=2;
        public static int TUESDAY=3;
        public static int WEDNESDAY=4;
        public static int THURSDAY=5;
        public static int FRIDAY=6;
        public static int SATURDAY=7;
 	}
 	
 	public static class leaveStatus{
        
        public static final int LEAVE_APPLIED=1;
        public static final int LEAVE_CANCELED=2;
        public static final int LEAVE_APPROVED=3;
        public static final int LEAVE_REJECTED=4;
        public static final int LEAVE_FORWARDED=5;
        
        public static final long NONE=0;
        public static final long FULL_DAY=1;
        public static final long HALF_DAY=2;
        public static final long FIRST_HALF=1;
        public static final long SECOND_HALF=2;
        
 	}

	public static class attendanceStatus {
		public static final int PRESENT = 1;
		public static final int LEAVE = 2;
		public static final int HALF_DAY_LEAVE = 3;

		public static final int FIRST_HALF = 1;
		public static final int SECOND_HALF = 2;
	}

	public static class FixedAsset {
 		public static int FLAT = 1;
 		public static int WRITTEN_DOWN_VALUE = 2;
 		
 		public static int MONTHLY = 1;
 		public static int QUARTERLY = 2;
 		public static int HALF_YEARLY = 3;
 		public static int YEARLY = 4;
 		
 		public static int SALES_DEPRECIATION = 1;
 		public static int NORMAL_DEPRECIATION = 2;
 	}
	
	 public static class loanPaymentStatus {
         public static final int PAYMENT_PENDING = 1;
         public static final int PAYMENT_FORWARDED = 2;
         public static final int PAYMENT_DONE = 3;
	 }

	 public static class EmployeeStatus {
	 		public static int ACTIVE = 1;
	 		public static int RESIGNED = 2; 		
	 		public static int TERMINATED = 3; 		
	 	}
	 
		public static class tableStatus {
			public static int AVAILABLE = 1;
			public static int BUSY = 2;
			public static int AWAITING_CLEANING = 3;
			public static int RESERVED = 4;
			
		}
		
		public static int INWARD = 1;
		public static int OUTWARD = 2;
		
		public static int BILL_TYPE_NORMAL = 1;
		public static int BILL_TYPE_EXCHANGE = 2;

		 public static class processStatus {
		 		public static long ACTIVE = 0;
		 		public static long INACTIVE = 1; 		
		 		
		 		public static List<KeyValue> status = Arrays.asList(
		 				new KeyValue(ACTIVE, "ACTIVE"), 
		 				new KeyValue(INACTIVE, "INACTIVE"));
		 	}
		 
		 public static class billViewOptions {
		 		public static long SALES = 1;
		 		public static long PURCHASE = 2; 		
		 		
		 		public static List<KeyValue> status = Arrays.asList(
		 				new KeyValue(SALES, "SALES"), 
		 				new KeyValue(PURCHASE, "PURCHASE"));
		 	}
		 
		 
		 public static class BillViewDetails{
             public static final int PURCHASE_ENQUIRY=1;
             public static final int PURCHASE_QUOTATION=2;
             public static final int PURCHASE_ORDER=3;
             public static final int GRN=4;
             public static final int PURCHASE=5;
             public static final int PURCHASE_RETURN=6;
             public static final int SALES_ENQIRY=7;
             public static final int SALES_QUOTATION=8;
             public static final int SALES_ORDER=9;
             public static final int DELIVERY_NOTE=10;
             public static final int SALES=11;
             public static final int CASH_DEPOSIT=12;
             public static final int CASH_PAYMENT=13;
             public static final int BANK_DEPOSIT=14;
             public static final int BANK_PAYMENT=15;
             public static final int JOURNEL=16;
             
             public static List billViewDetails=Arrays.asList(
                             new KeyValue(PURCHASE_ENQUIRY, "PURCHASE_ENQUIRY"), 
                              new KeyValue(PURCHASE_QUOTATION, "PURCHASE_QUOTATION"),
                              new KeyValue(PURCHASE_ORDER, "PURCHASE_ORDER"),
                              new KeyValue(GRN, "GRN"),
                              new KeyValue(PURCHASE, "PURCHASE"),
                              new KeyValue(PURCHASE_RETURN, "PURCHASE_RETURN"),
                              new KeyValue(SALES_ENQIRY, "SALES_ENQIRY"),
                              new KeyValue(SALES_QUOTATION, "SALES_QUOTATION"),
                              new KeyValue(SALES_ORDER, "SALES_ORDER"),
                              new KeyValue(DELIVERY_NOTE, "DELIVERY_NOTE"),
                              new KeyValue(SALES, "SALES"),
                              new KeyValue(CASH_DEPOSIT, "CASH_DEPOSIT"),
                              new KeyValue(CASH_PAYMENT, "CASH_PAYMENT"),
                              new KeyValue(BANK_DEPOSIT, "BANK_DEPOSIT"),
                              new KeyValue(BANK_PAYMENT, "BANK_PAYMENT"),
                              new KeyValue(JOURNEL, "JOURNAL"));
              }
}
