package com.webspark.bean;

import java.sql.Timestamp;
import java.util.Date;

public class ReportBean {

	private String organizationName = "";
	private String officeName = "";
	private String address = "";
	private String date;
	private String container, salesMan;

	private String login, description, contact_person, issuedDate,
			invoicePeriod;
	private String item_name, item_code, client_name, particulars, employee, unit,
			basic_unit, from_acct, to_acct, name, category, mobile, location,
			country, title, tasks, users, currency, from_account, activity,
			chequeDate, paymentNo,num;
	private double quantity, total, inwards, outwards, profit, rate, amount,
			discount, purchaseQty, purchaseRtnQty, salesQty, salesRtnQty,
			opening, closing, real_amount, wasteQty, returnQty, good_stock,
			balance, periodBalance, sale, purchase;
	private java.util.Date trn_date, dt, st_date, end_date, compl_date;
	private long emp_id, customer_id, item_id, unit_id, status, 
			login_id, id, container_no,number;
	private int cashOrCheque;

	private double grosswt, netwt, cbm, carton;

	Timestamp date_time;
	int type, level;
	private long currencyId;

	public ReportBean() {
		super();
	}

	// Constructor 1
	public ReportBean(String item_name, double opening, double purchaseQty,
			double purchaseRtnQty, double salesQty, double salesRtnQty,
			double closing) {
		super();
		this.item_name = item_name;
		this.opening = opening;
		this.purchaseQty = purchaseQty;
		this.purchaseRtnQty = purchaseRtnQty;
		this.salesQty = salesQty;
		this.salesRtnQty = salesRtnQty;
		this.closing = closing;
	}

	// Constructor 2
	public ReportBean(long number, String particulars, Date dt,
			String from_acct, String to_acct, double amount,
			double real_amount, int type, String description) {
		super();
		this.description = description;
		this.particulars = particulars;
		this.from_acct = from_acct;
		this.to_acct = to_acct;
		this.amount = amount;
		this.setReal_amount(real_amount);
		this.dt = dt;
		this.number = number;
		this.type = type;
	}

	// Constructor 3
	public ReportBean(long id, String description, String item_name,
			String client_name, String employee, String title, double amount,
			Date dt, long status, long number, int level, int type,
			String activity) {
		super();
		this.id = id;
		this.description = description;
		this.item_name = item_name;
		this.client_name = client_name;
		this.employee = employee;
		this.title = title;
		this.amount = amount;
		this.dt = dt;
		this.status = status;
		this.number = number;
		this.level = level;
		this.type = type;
		this.activity = activity;
	}

	// Constructor 4
	public ReportBean(String date, String item_name, String unit,
			double quantity) {
		super();
		this.date = date;
		this.item_name = item_name;
		this.unit = unit;
		this.quantity = quantity;
	}

	// Constructor 5
	public ReportBean(String login, String description, Date dt) {
		super();
		this.login = login;
		this.description = description;
		this.dt = dt;
	}

	// Constructor 6
	public ReportBean(Date dt, long id, String client_name,
			String from_account, double amount, String currency,
			String description) {
		super();
		this.id = id;
		this.client_name = client_name;
		this.from_account = from_account;
		this.currency = currency;
		this.amount = amount;
		this.dt = dt;
		this.description = description;
	}

	// Constructor 7
	public ReportBean(Date dt, long id, String client_name,
			String from_account, double amount, String currency,
			String description, String chequeDate, int cashOrCheque) {
		super();
		this.id = id;
		this.client_name = client_name;
		this.from_account = from_account;
		this.currency = currency;
		this.amount = amount;
		this.dt = dt;
		this.description = description;
		this.chequeDate = chequeDate;
		this.cashOrCheque = cashOrCheque;
	}

	// Constructor 8
	public ReportBean(Date dt, long id, String client_name,
			String from_account, double amount, String currency,
			String description, String chequeDate, int cashOrCheque, long number) {
		super();
		this.id = id;
		this.client_name = client_name;
		this.from_account = from_account;
		this.currency = currency;
		this.amount = amount;
		this.dt = dt;
		this.description = description;
		this.chequeDate = chequeDate;
		this.cashOrCheque = cashOrCheque;
		this.number = number;
	}

	// Constructor 9
	public ReportBean(Date dt, long id, String client_name,
			String from_account, double amount, String currency,
			String description, String chequeDate, int cashOrCheque,
			long number, String issuedDate, String invoicePeriod) {
		super();
		this.id = id;
		this.client_name = client_name;
		this.from_account = from_account;
		this.currency = currency;
		this.amount = amount;
		this.dt = dt;
		this.description = description;
		this.chequeDate = chequeDate;
		this.cashOrCheque = cashOrCheque;
		this.number = number;
		this.issuedDate = issuedDate;
		this.invoicePeriod = invoicePeriod;
	}

	// Constructor 10
	public ReportBean(Date dt, long id, String client_name,
			String from_account, double amount, String currency,
			String description, int type) {
		super();
		this.id = id;
		this.client_name = client_name;
		this.from_account = from_account;
		this.currency = currency;
		this.amount = amount;
		this.dt = dt;
		this.description = description;
		this.type = type;
	}

	// Constructor 11
	public ReportBean(Date dt, long id, String client_name,
			String from_account, double amount, String currency,
			String description, int type, String paymentNo) {
		super();
		this.id = id;
		this.client_name = client_name;
		this.from_account = from_account;
		this.currency = currency;
		this.amount = amount;
		this.dt = dt;
		this.description = description;
		this.type = type;
		this.paymentNo = paymentNo;
	}

	// Constructor 12
	public ReportBean(Date dt, long id, String client_name,
			String from_account, double amount, String currency,
			String description, int type, String paymentNo, String chequeDate,
			String issuedDate, String invoicePeriod) {
		super();
		this.id = id;
		this.client_name = client_name;
		this.from_account = from_account;
		this.currency = currency;
		this.amount = amount;
		this.dt = dt;
		this.description = description;
		this.type = type;
		this.paymentNo = paymentNo;
		this.issuedDate = issuedDate;
		this.invoicePeriod = invoicePeriod;
		this.chequeDate = chequeDate;
	}

	// Constructor 13
	public ReportBean(Date dt, long number, String login, String name,
			double rate, String currency) {
		super();
		this.name = name;
		this.currency = currency;
		this.rate = rate;
		this.dt = dt;
		this.number = number;
		this.login = login;
	}

	// Constructor 14
	public ReportBean(Date dt, long number, String login, String name,
			String country, double rate, String currency) {
		super();
		this.name = name;
		this.currency = currency;
		this.rate = rate;
		this.dt = dt;
		this.number = number;
		this.login = login;
		this.country = country;
	}

	// Constructor 15
	public ReportBean(Date dt, long number, long login_id, String name,
			double rate, String currency) {
		super();
		this.name = name;
		this.currency = currency;
		this.rate = rate;
		this.dt = dt;
		this.number = number;
		this.login_id = login_id;
	}

	// Constructor 16
	public ReportBean(String employee, String title, String tasks,
			String users, Date dt, Date st_date, Date end_date,
			Date compl_date, long status) {
		super();
		this.employee = employee;
		this.title = title;
		this.tasks = tasks;
		this.users = users;
		this.dt = dt;
		this.st_date = st_date;
		this.end_date = end_date;
		this.compl_date = compl_date;
		this.status = status;
	}

	// Constructor 17
	public ReportBean(String officeName, String address, String employee,
			String name, String category, String mobile, String location,
			Date dt) {
		super();
		this.officeName = officeName;
		this.address = address;
		this.employee = employee;
		this.name = name;
		this.category = category;
		this.mobile = mobile;
		this.location = location;
		this.dt = dt;
	}

	// Constructor 18
	public ReportBean(String officeName, String address, String employee,
			String name, String category, String mobile, String location,
			Date dt, String contact_person) {
		super();
		this.officeName = officeName;
		this.address = address;
		this.employee = employee;
		this.name = name;
		this.category = category;
		this.mobile = mobile;
		this.location = location;
		this.dt = dt;
		this.contact_person = contact_person;
	}

	// Constructor 19
	public ReportBean(String particulars, Date dt, String from_acct,
			String to_acct) {
		super();
		this.particulars = particulars;
		this.dt = dt;
		this.from_acct = from_acct;
		this.to_acct = to_acct;
	}

	// Constructor 20
	public ReportBean(String particulars, Date dt, String from_acct,
			String to_acct, double amount) {
		super();
		this.particulars = particulars;
		this.dt = dt;
		this.from_acct = from_acct;
		this.to_acct = to_acct;
		this.amount = amount;
	}

	// Constructor 21
	public ReportBean(String client_name, String employee, Date dt,
			String item_name, String unit, double rate) {
		super();
		this.item_name = item_name;
		this.unit = unit;
		this.rate = rate;
		this.dt = dt;
		this.employee = employee;
		this.client_name = client_name;
	}

	// Constructor 22
	public ReportBean(String particulars, double inwards, double outwards,
			java.util.Date trn_date) {
		super();
		this.particulars = particulars;
		this.inwards = inwards;
		this.outwards = outwards;
		this.trn_date = trn_date;
	}

	// Constructor 23
	public ReportBean(String organizationName, String officeName,
			String address, String date, String login) {
		super();
		this.organizationName = organizationName;
		this.address = address;
		this.date = date;
		this.login = login;
		this.officeName = officeName;
	}

	// Constructor 24
	public ReportBean(String item_name, String client_name, double quantity,
			double total) {
		super();
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.total = total;
	}

	// Constructor 25
	public ReportBean(String item_name, String client_name, double quantity,
			double total, Date date, String unit) {
		super();
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.total = total;
		this.dt = date;
		this.unit = unit;
	}

	// Constructor 26
	public ReportBean(String item_name, String client_name, double quantity,
			double total, Date date, String unit, double rate) {
		super();
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.total = total;
		this.dt = date;
		this.unit = unit;
		this.rate = rate;
	}

	// Constructor 27
	public ReportBean(String item_name, String client_name, double quantity,
			double total, Date date, String unit, double rate, long number) {
		super();
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.total = total;
		this.dt = date;
		this.unit = unit;
		this.rate = rate;
		this.number = number;
	}

	// Constructor 28
	public ReportBean(String item_name, String client_name, double quantity,
			double wasteQty, double returnQty, Date date, String unit,
			double rate, long number) {
		super();
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.dt = date;
		this.unit = unit;
		this.rate = rate;
		this.number = number;
		this.setWasteQty(wasteQty);
		this.setReturnQty(returnQty);
	}

	// Constructor 29
	public ReportBean(String item_name, String client_name, double quantity,
			double wasteQty, double returnQty, double good_stock, Date date,
			String unit, double rate, long number) {
		super();
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.dt = date;
		this.unit = unit;
		this.rate = rate;
		this.number = number;
		this.good_stock = good_stock;
		this.setWasteQty(wasteQty);
		this.setReturnQty(returnQty);
	}

	// Constructor 30
	public ReportBean(String item_name, String client_name, double quantity,
			double total, long status) {
		super();
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.total = total;
		this.status = status;
	}

	// Constructor 31
	public ReportBean(String item_name, String client_name, double quantity,
			double total, Date date, String unit, long status) {
		super();
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.total = total;
		this.dt = date;
		this.unit = unit;
		this.status = status;
	}

	// Constructor 32
	public ReportBean(String date, String officeName, String address,
			String organizationName) {
		super();
		this.organizationName = organizationName;
		this.officeName = officeName;
		this.address = address;
		this.date = date;
	}

	// Constructor 33
	public ReportBean(String client_name, String item_name, String date,
			String unit, String particulars, double quantity, double rate) {
		super();
		this.item_name = item_name;
		this.client_name = client_name;
		this.date = date;
		this.unit = unit;
		this.rate = rate;
		this.particulars = particulars;
		this.quantity = quantity;
	}

	// Constructor 34
	public ReportBean(String item_name, double purchaseQty,
			double purchaseRtnQty, double salesQty, double salesRtnQty) {
		super();
		this.item_name = item_name;
		this.purchaseQty = purchaseQty;
		this.purchaseRtnQty = purchaseRtnQty;
		this.salesQty = salesQty;
		this.salesRtnQty = salesRtnQty;
	}

	// Constructor 35
	public ReportBean(String item_name, String client_name, double quantity,
			double total, Date date, String unit, double rate, double grosswt,
			double netwt, double cbm, double carton) {
		super();
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.total = total;
		this.dt = date;
		this.unit = unit;
		this.rate = rate;
		this.grosswt = grosswt;
		this.netwt = netwt;
		this.cbm = cbm;
		this.setCarton(carton);
	}

	// Constructor 35 With Container
	public ReportBean(String item_name, String client_name, double quantity,
			double total, Date date, String unit, double rate, double grosswt,
			double netwt, double cbm, double carton, long container_no) {
		super();
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.total = total;
		this.dt = date;
		this.unit = unit;
		this.rate = rate;
		this.grosswt = grosswt;
		this.netwt = netwt;
		this.cbm = cbm;
		this.container_no = container_no;
		this.setCarton(carton);
	}

	// Constructor 36
	public ReportBean(long id, String particulars, double inwards,
			double outwards, java.util.Date trn_date) {
		super();
		this.id = id;
		this.particulars = particulars;
		this.inwards = inwards;
		this.outwards = outwards;
		this.trn_date = trn_date;
	}

	// Constructor 37
	public ReportBean(String item_name, double opening, double purchaseQty,
			double purchaseRtnQty, double salesQty, double salesRtnQty,
			double closing, String unit) {
		super();
		this.item_name = item_name;
		this.opening = opening;
		this.purchaseQty = purchaseQty;
		this.purchaseRtnQty = purchaseRtnQty;
		this.salesQty = salesQty;
		this.salesRtnQty = salesRtnQty;
		this.closing = closing;
		this.unit = unit;
	}

	// Constructor 38
	public ReportBean(long id, String particulars, double inwards,
			double outwards, java.util.Date trn_date, String from_acct,
			String to_acct, String category) {
		super();
		this.id = id;
		this.particulars = particulars;
		this.inwards = inwards;
		this.outwards = outwards;
		this.trn_date = trn_date;

		this.from_acct = from_acct;
		this.to_acct = to_acct;
		this.category = category;
	}

	// Constructor 39
	public ReportBean(long id, String particulars, String from_account,
			String to_acct, double inwards, double outwards,
			java.util.Date trn_date) {
		super();
		this.id = id;
		this.particulars = particulars;
		this.from_account = from_account;
		this.to_acct = to_acct;
		this.inwards = inwards;
		this.outwards = outwards;
		this.trn_date = trn_date;
	}

	// Constructor 40
	public ReportBean(String item_name, String client_name, double quantity,
			double wasteQty, double returnQty, double good_stock, Date date,
			String unit, double rate, long number, long id, long item_id) {
		super();
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.dt = date;
		this.unit = unit;
		this.rate = rate;
		this.number = number;
		this.good_stock = good_stock;
		this.setWasteQty(wasteQty);
		this.setReturnQty(returnQty);
		this.id = id;
		this.item_id = item_id;

	}

	// Constructor 41
	public ReportBean(long id, String item_name, String client_name,
			double quantity, double total, Date date, String unit, long status) {
		super();
		this.id = id;
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.total = total;
		this.dt = date;
		this.unit = unit;
		this.status = status;
	}

	// Constructor 42
	public ReportBean(long id, String item_name, String client_name,
			double quantity, double total, Date date, String unit, double rate,
			long number) {
		super();
		this.id = id;
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.total = total;
		this.dt = date;
		this.unit = unit;
		this.rate = rate;
		this.number = number;
	}

	// Constructor 43
	public ReportBean(Date dt, long number, String login, String name,
			String country, double rate, String currency, long id, long status) {
		super();
		this.name = name;
		this.currency = currency;
		this.rate = rate;
		this.dt = dt;
		this.number = number;
		this.login = login;
		this.country = country;
		this.id = id;
		this.status = status;
	}

	// Constructor 44
	public ReportBean(long id, String item_name, String client_name,
			double quantity, double total, Date date, String unit, double rate,
			double grosswt, double netwt, double cbm, double carton) {
		super();
		this.id = id;
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.total = total;
		this.dt = date;
		this.unit = unit;
		this.rate = rate;
		this.grosswt = grosswt;
		this.netwt = netwt;
		this.cbm = cbm;
		this.setCarton(carton);
	}

	// Constructor 44 With Container
	public ReportBean(long id, String item_name, String client_name,
			double quantity, double total, Date date, String unit, double rate,
			double grosswt, double netwt, double cbm, double carton,
			long container_no) {
		super();
		this.id = id;
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.total = total;
		this.dt = date;
		this.unit = unit;
		this.rate = rate;
		this.grosswt = grosswt;
		this.netwt = netwt;
		this.cbm = cbm;
		this.setCarton(carton);
		this.container_no = container_no;
	}

	// Constructor 45
	public ReportBean(long id, String particulars, Date dt, String from_acct,
			String to_acct, double amount) {
		super();
		this.id = id;
		this.particulars = particulars;
		this.dt = dt;
		this.from_acct = from_acct;
		this.to_acct = to_acct;
		this.amount = amount;
	}

	// Constructor 46
	public ReportBean(long number, String particulars, Date dt,
			String from_acct, String to_acct, double amount,
			double real_amount, int type, String description, long id) {
		super();
		this.description = description;
		this.particulars = particulars;
		this.from_acct = from_acct;
		this.to_acct = to_acct;
		this.amount = amount;
		this.setReal_amount(real_amount);
		this.dt = dt;
		this.number = number;
		this.type = type;
		this.id = id;
	}

	// Constructor 47
	public ReportBean(String client_name, String employee, Date dt,
			String item_name, String unit, double rate, long id) {
		super();
		this.item_name = item_name;
		this.unit = unit;
		this.rate = rate;
		this.dt = dt;
		this.employee = employee;
		this.client_name = client_name;
		this.id = id;
	}

	// Constructor 48
	public ReportBean(String officeName, String address, String employee,
			String name, String category, String mobile, String location,
			Date dt, String contact_person, long id) {
		super();
		this.officeName = officeName;
		this.address = address;
		this.employee = employee;
		this.name = name;
		this.category = category;
		this.mobile = mobile;
		this.location = location;
		this.dt = dt;
		this.contact_person = contact_person;
		this.id = id;
	}

	// Constructor 49
	public ReportBean(long id, String particulars, Date dt, String from_acct,
			String to_acct, double amount, String activity) {
		super();
		this.id = id;
		this.particulars = particulars;
		this.dt = dt;
		this.from_acct = from_acct;
		this.to_acct = to_acct;
		this.amount = amount;
		this.activity = activity;
	}

	// Constructor 50
	public ReportBean(long id, String item_name, double opening,
			double purchaseQty, double purchaseRtnQty, double salesQty,
			double salesRtnQty, double closing, String unit, double balance,
			double sale, double purchase) {
		super();
		this.id = id;
		this.item_name = item_name;
		this.opening = opening;
		this.purchaseQty = purchaseQty;
		this.purchaseRtnQty = purchaseRtnQty;
		this.salesQty = salesQty;
		this.salesRtnQty = salesRtnQty;
		this.closing = closing;
		this.unit = unit;
		this.balance = balance;
		this.sale = sale;
		this.purchase = purchase;
	}

	// Constructor 51
	public ReportBean(String employee, String title, String tasks,
			String users, Date dt, Date st_date, Date end_date,
			Date compl_date, long status, String description) {
		super();
		this.employee = employee;
		this.title = title;
		this.tasks = tasks;
		this.users = users;
		this.dt = dt;
		this.st_date = st_date;
		this.end_date = end_date;
		this.compl_date = compl_date;
		this.status = status;
		this.description = description;
	}

	// Constructor 52
	public ReportBean(long id, String name, double amount) {
		super();
		this.name = name;
		this.amount = amount;
		this.id = id;
	}

	// Constructor 53
	public ReportBean(long id, String name, double quantity, double wasteQty,
			double returnQty, double good_stock) {
		super();
		this.name = name;
		this.quantity = quantity;
		this.wasteQty = wasteQty;
		this.returnQty = returnQty;
		this.good_stock = good_stock;
		this.id = id;
	}

	// Constructor 54
	public ReportBean(long id, String item_name, String client_name,
			double quantity, double total, Date date, String unit, double rate) {
		super();
		this.id = id;
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.total = total;
		this.dt = date;
		this.unit = unit;
		this.rate = rate;
		// this.number = number;
	}

	// Constructor 55
	public ReportBean(long id, String item_name, String client_name,
			double quantity, double qty_in_basic_unit, String date, String unit) {
		super();
		this.id = id;
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.date = date;
		this.unit = unit;
	}
	// Constructor 56
	public ReportBean(String item_name, String client_name, double quantity,
			double qty_in_basic_unit, String date, String unit,
			double rate, String paymentNo) {
		super();
		//this.id = id;
		this.item_name = item_name;
		this.client_name = client_name;
		this.quantity = quantity;
		this.date = date;
		this.unit = unit;
		this.rate = rate;
		this.paymentNo = paymentNo;
		
	}
	
	// Constructor 57
		public ReportBean(String item_name, String client_name, double quantity,
				double wasteQty, double returnQty, double good_stock, Date date,
				String unit, double rate, String paymentNo, long id, long item_id) {
			super();
			this.item_name = item_name;
			this.client_name = client_name;
			this.quantity = quantity;
			this.dt = date;
			this.unit = unit;
			this.rate = rate;
			this.paymentNo = paymentNo;
			this.good_stock = good_stock;
			this.setWasteQty(wasteQty);
			this.setReturnQty(returnQty);
			this.id = id;
			this.item_id = item_id;

		}
		
		// Constructor 58
				public ReportBean(long id, String item_name, String client_name,
						double quantity, double total, Date date, String unit, double rate,
						String num) {
					super();
					this.id = id;
					this.item_name = item_name;
					this.client_name = client_name;
					this.quantity = quantity;
					this.total = total;
					this.dt = date;
					this.unit = unit;
					this.rate = rate;
					this.setNum(num);
				}
				
				// Constructor 59
				public ReportBean(long id, String item_name, String client_name,
						double quantity, double total, Date date, String unit, double rate,
						String num,long currencyId) {
					super();
					this.id = id;
					this.item_name = item_name;
					this.client_name = client_name;
					this.quantity = quantity;
					this.total = total;
					this.dt = date;
					this.unit = unit;
					this.rate = rate;
					this.setNum(num);
					this.setCurrencyId(currencyId);
				}
				// Constructor 60
				public ReportBean(long id, String name, double returnQty,String unit) {
					super();
					this.name = name;
					this.returnQty = returnQty;
					this.unit = unit;
					this.id = id;
				}
				
				// Constructor 61
				public ReportBean(String item_name, String client_name, double quantity,
						double wasteQty, double returnQty, double good_stock, Date date,
						String unit, double rate, String paymentNo, long id, long item_id,long currencyId) {
					super();
					this.item_name = item_name;
					this.client_name = client_name;
					this.quantity = quantity;
					this.dt = date;
					this.unit = unit;
					this.rate = rate;
					this.paymentNo = paymentNo;
					this.good_stock = good_stock;
					this.setWasteQty(wasteQty);
					this.setReturnQty(returnQty);
					this.id = id;
					this.item_id = item_id;
					this.setCurrencyId(currencyId);

				}
				
				// Constructor 62
				public ReportBean(String description) {
					super();
					this.description = description;
				}
				
				public ReportBean(long id, String item_name, String item_code,
						double quantity,String date) {
					super();
					this.id = id;
					this.item_name = item_name;
					this.item_code = item_code;
					this.quantity = quantity;
					this.date = date;
				}

	/*

	/*
	 * //Constructor 54 With Container public ReportBean(String item_name,
	 * String client_name, double quantity, double total, Date date, String
	 * unit, double rate) { super(); this.item_name = item_name;
	 * this.client_name = client_name; this.quantity = quantity; this.total =
	 * total; this.dt = date; this.unit = unit; this.rate = rate;
	 * 
	 * this.setCarton(carton); }
	 */

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getClient_name() {
		return client_name;
	}

	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	public double getInwards() {
		return inwards;
	}

	public void setInwards(double inwards) {
		this.inwards = inwards;
	}

	public double getOutwards() {
		return outwards;
	}

	public void setOutwards(double outwards) {
		this.outwards = outwards;
	}

	public java.util.Date getTrn_date() {
		return trn_date;
	}

	public void setTrn_date(java.util.Date trn_date) {
		this.trn_date = trn_date;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public java.util.Date getDt() {
		return dt;
	}

	public void setDt(java.util.Date dt) {
		this.dt = dt;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public long getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(long emp_id) {
		this.emp_id = emp_id;
	}

	public long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(long customer_id) {
		this.customer_id = customer_id;
	}

	public long getItem_id() {
		return item_id;
	}

	public void setItem_id(long item_id) {
		this.item_id = item_id;
	}

	public long getUnit_id() {
		return unit_id;
	}

	public void setUnit_id(long unit_id) {
		this.unit_id = unit_id;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public String getFrom_acct() {
		return from_acct;
	}

	public void setFrom_acct(String from_acct) {
		this.from_acct = from_acct;
	}

	public String getTo_acct() {
		return to_acct;
	}

	public void setTo_acct(String to_acct) {
		this.to_acct = to_acct;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTasks() {
		return tasks;
	}

	public void setTasks(String tasks) {
		this.tasks = tasks;
	}

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	public java.util.Date getSt_date() {
		return st_date;
	}

	public void setSt_date(java.util.Date st_date) {
		this.st_date = st_date;
	}

	public java.util.Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(java.util.Date end_date) {
		this.end_date = end_date;
	}

	public java.util.Date getCompl_date() {
		return compl_date;
	}

	public void setCompl_date(java.util.Date compl_date) {
		this.compl_date = compl_date;
	}

	public String getBasic_unit() {
		return basic_unit;
	}

	public void setBasic_unit(String basic_unit) {
		this.basic_unit = basic_unit;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public String getFrom_account() {
		return from_account;
	}

	public void setFrom_account(String from_account) {
		this.from_account = from_account;
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContact_person() {
		return contact_person;
	}

	public void setContact_person(String contact_person) {
		this.contact_person = contact_person;
	}

	public Timestamp getDate_time() {
		return date_time;
	}

	public void setDate_time(Timestamp date_time) {
		this.date_time = date_time;
	}

	public double getPurchaseQty() {
		return purchaseQty;
	}

	public void setPurchaseQty(double purchaseQty) {
		this.purchaseQty = purchaseQty;
	}

	public double getPurchaseRtnQty() {
		return purchaseRtnQty;
	}

	public void setPurchaseRtnQty(double purchaseRtnQty) {
		this.purchaseRtnQty = purchaseRtnQty;
	}

	public double getSalesQty() {
		return salesQty;
	}

	public void setSalesQty(double salesQty) {
		this.salesQty = salesQty;
	}

	public double getSalesRtnQty() {
		return salesRtnQty;
	}

	public void setSalesRtnQty(double salesRtnQty) {
		this.salesRtnQty = salesRtnQty;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public double getOpening() {
		return opening;
	}

	public void setOpening(double opening) {
		this.opening = opening;
	}

	public double getClosing() {
		return closing;
	}

	public void setClosing(double closing) {
		this.closing = closing;
	}

	public double getReal_amount() {
		return real_amount;
	}

	public void setReal_amount(double real_amount) {
		this.real_amount = real_amount;
	}

	public double getGrosswt() {
		return grosswt;
	}

	public void setGrosswt(double grosswt) {
		this.grosswt = grosswt;
	}

	public double getNetwt() {
		return netwt;
	}

	public void setNetwt(double netwt) {
		this.netwt = netwt;
	}

	public double getCbm() {
		return cbm;
	}

	public void setCbm(double cbm) {
		this.cbm = cbm;
	}

	public double getCarton() {
		return carton;
	}

	public void setCarton(double carton) {
		this.carton = carton;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public int getCashOrCheque() {
		return cashOrCheque;
	}

	public void setCashOrCheque(int cashOrCheque) {
		this.cashOrCheque = cashOrCheque;
	}

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	public double getWasteQty() {
		return wasteQty;
	}

	public void setWasteQty(double wasteQty) {
		this.wasteQty = wasteQty;
	}

	public double getReturnQty() {
		return returnQty;
	}

	public void setReturnQty(double returnQty) {
		this.returnQty = returnQty;
	}

	public String getInvoicePeriod() {
		return invoicePeriod;
	}

	public void setInvoicePeriod(String invoicePeriod) {
		this.invoicePeriod = invoicePeriod;
	}

	public String getIssuedDate() {
		return issuedDate;
	}

	public void setIssuedDate(String issuedDate) {
		this.issuedDate = issuedDate;
	}

	public double getGood_stock() {
		return good_stock;
	}

	public void setGood_stock(double good_stock) {
		this.good_stock = good_stock;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public double getPeriodBalance() {
		return periodBalance;
	}

	public void setPeriodBalance(double periodBalance) {
		this.periodBalance = periodBalance;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getSale() {
		return sale;
	}

	public void setSale(double sale) {
		this.sale = sale;
	}

	public double getPurchase() {
		return purchase;
	}

	public void setPurchase(double purchase) {
		this.purchase = purchase;
	}

	public long getContainer_no() {
		return container_no;
	}

	public void setContainer_no(long container_no) {
		this.container_no = container_no;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public String getSalesMan() {
		return salesMan;
	}

	public void setSalesMan(String salesMan) {
		this.salesMan = salesMan;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(long currencyId) {
		this.currencyId = currencyId;
	}

	public String getItem_code() {
		return item_code;
	}

	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}
	
}