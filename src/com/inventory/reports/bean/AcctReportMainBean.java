package com.inventory.reports.bean;

import java.util.Date;
import java.util.List;

import com.webspark.common.util.CommonUtil;

public class AcctReportMainBean {

	private long id, transaction_id, number,invoiceId, currencyId;
	private int count, transaction_type, max_cedit_period, due_days;
	private String dispaly_name, name, particulars, amount_type, client_name,account,chequeDate,chequeNo,
			comments, ref_no, no, item, unit, customer,cheque_date,cheque_no,bill_no,from_or_to,date_string,currency,orderNo;
	private Date date, from_date, to_date;

	private double debit, credit, amount, quantity, total, payed, need_to_pay,
			balance, returned, period_balance, expence, gross_sale, waste,
			stock_qty,good, rate, price;


	private List subList;
	private char type;
	
	private double current_debit, current_credit, current_balance,
			opening_balance,return_qty;
	private double opening_debit, opening_credit, balance_debit,
			balance_credit;
	private double purchase_qty, current_qty;
	
	private double vat;
	
	public AcctReportMainBean() {
		super();
	}
	
	// Contsructor #1
	public AcctReportMainBean(String item,  double purchase_qty,double current_qty,String unit,
			double rate) {
		super();
		this.item = item;
		this.rate = rate;
		this.purchase_qty = purchase_qty;
		this.current_qty = current_qty;
		this.unit = unit;
	}
	
	// Contsructor #2
	public AcctReportMainBean(long id, String particulars, String no,
			Date date, String client_name, String item, double returned,
			String unit, double price, String ref_no, String comments) {
		super();
		this.id = id;
		this.no = no;
		this.particulars = particulars;
		this.client_name = client_name;
		this.comments = comments;
		this.ref_no = ref_no;
		this.date = date;
		this.price = price;
		this.item = item;
		this.returned = returned;
		this.unit = unit;
	}
	
	// Contsructor #3
	public AcctReportMainBean(long id, String particulars, String no,
			Date date, String client_name, String item, double waste,
			double returned, double stock_qty, String unit, double price,
			String ref_no, String comments) {
		super();
		this.id = id;
		this.no = no;
		this.particulars = particulars;
		this.client_name = client_name;
		this.comments = comments;
		this.ref_no = ref_no;
		this.date = date;
		this.price = price;
		this.item = item;
		this.waste = waste;
		this.returned = returned;
		this.stock_qty = stock_qty;
		this.unit = unit;
	}
	
	// Contsructor #4
	public AcctReportMainBean(long id, String particulars, String no,
			Date date, String client_name, String item, double good, double waste,
			double returned, double stock_qty, String unit, double price,
			String ref_no, String comments) {
		super();
		this.id = id;
		this.no = no;
		this.particulars = particulars;
		this.client_name = client_name;
		this.comments = comments;
		this.ref_no = ref_no;
		this.date = date;
		this.price = price;
		this.item = item;
		this.setGood(good);
		this.waste = waste;
		this.returned = returned;
		this.stock_qty = stock_qty;
		this.unit = unit;
	}

	// Contsructor #5
	public AcctReportMainBean(long id, String particulars, String no,
			Date date, String client_name, double amount, String ref_no,
			String comments) {
		super();
		this.id = id;
		this.no = no;
		this.particulars = particulars;
		this.client_name = client_name;
		this.comments = comments;
		this.ref_no = ref_no;
		this.date = date;
		this.amount = amount;
	}

	// Contsructor #6
	public AcctReportMainBean(int transaction_type, long id,
			String particulars, String no, Date date, String client_name,
			double amount, String ref_no, String comments) {
		super();
		this.transaction_type = transaction_type;
		this.id = id;
		this.no = no;
		this.particulars = particulars;
		this.client_name = client_name;
		this.comments = comments;
		this.ref_no = ref_no;
		this.date = date;
		this.amount = amount;
	}

	// Contsructor #7
	public AcctReportMainBean(String amount_type, Date date, double amount,
			int transaction_type) {
		super();
		this.amount_type = amount_type;
		this.date = date;
		this.amount = amount;
		this.transaction_type = transaction_type;
	}

	// Contsructor #8
	public AcctReportMainBean(long id, String amount_type, Date date,
			double amount, int transaction_type, String name) {
		super();
		this.id = id;
		this.amount_type = amount_type;
		this.date = date;
		this.amount = amount;
		this.transaction_type = transaction_type;
		this.name = name;
	}
	
	// Contsructor #9
	public AcctReportMainBean(long id, String amount_type, Date date,
			double amount, int transaction_type, String name, String from_or_to) {
		super();
		this.id = id;
		this.amount_type = amount_type;
		this.date = date;
		this.amount = amount;
		this.transaction_type = transaction_type;
		this.name = name;
		this.setFrom_or_to(from_or_to);
	}

	// Contsructor #10
	public AcctReportMainBean(String amount_type, Date date, double amount,
			String name) {
		super();
		this.amount_type = amount_type;
		this.date = date;
		this.amount = amount;
		this.name = name;
	}

	// Contsructor #11
	public AcctReportMainBean(String particulars, Date date, double amount,
			double payed) {
		super();
		this.particulars = particulars;
		this.date = date;
		this.amount = amount;
		this.payed = payed;
	}

	// Contsructor #12
	public AcctReportMainBean(String particulars, Date date, double amount,
			double payed, long number) {
		super();
		this.number = number;
		this.particulars = particulars;
		this.date = date;
		this.amount = amount;
		this.payed = payed;
	}

	// Contsructor #13
	public AcctReportMainBean(String particulars, double amount, double payed,
			double returned, double balance, double current_balance,
			double opening_balance) {
		super();
		this.particulars = particulars;
		this.amount = amount;
		this.payed = payed;
		this.returned = returned;
		this.balance = balance;
		this.current_balance = current_balance;
		this.opening_balance = opening_balance;
	}

	// Contsructor #14
	public AcctReportMainBean(String particulars, double amount, double payed,
			double returned, double balance, List subList) {
		super();
		this.particulars = particulars;
		this.amount = amount;
		this.payed = payed;
		this.returned = returned;
		this.balance = balance;
		this.subList = subList;
	}

	// Contsructor #15
	public AcctReportMainBean(long id, String particulars, Date date,
			double amount, double payed) {
		super();
		this.id = id;
		this.particulars = particulars;
		this.date = date;
		this.amount = amount;
		this.payed = payed;
	}

	// Contsructor #16
	public AcctReportMainBean(long id, String particulars, Date date,
			double amount, double payed, long number) {
		super();
		this.id = id;
		this.particulars = particulars;
		this.date = date;
		this.amount = amount;
		this.payed = payed;
		this.number = number;
	}
	
	// Contsructor #17
	public AcctReportMainBean(long id, String particulars, Date date,
			double amount, double payed, long number, String customer) {
		super();
		this.id = id;
		this.particulars = particulars;
		this.date = date;
		this.amount = amount;
		this.payed = payed;
		this.number = number;
		this.customer = customer;
	}
	
	// Contsructor #18
	public AcctReportMainBean(long id, String particulars, Date date,
			double amount, double payed, String customer) {
		super();
		this.id = id;
		this.particulars = particulars;
		this.date = date;
		this.amount = amount;
		this.payed = payed;
		this.customer = customer;
	}
	
	// Contsructor #19
	public AcctReportMainBean(long id, String particulars, Date date,
			double gross_sale, double amount, double payed, double expence,
			long number) {
		super();
		this.id = id;
		this.particulars = particulars;
		this.date = date;
		this.gross_sale = gross_sale;
		this.amount = amount;
		this.payed = payed;
		this.expence = expence;
		this.number = number;
	}
	
	// Contsructor #20
	public AcctReportMainBean(long id, String particulars, String no,
			Date date, String client_name, double amount, String ref_no,
			String comments,String cheque_date) {
		super();
		this.id = id;
		this.no = no;
		this.particulars = particulars;
		this.client_name = client_name;
		this.comments = comments;
		this.ref_no = ref_no;
		this.date = date;
		this.amount = amount;
		this.cheque_date = cheque_date;
	}
	// Contsructor #21
	public AcctReportMainBean(long id, String name, double amount) {
        super();
        this.id = id;
        this.name = name;
        this.amount = amount;
	}

	// Contsructor #22
	public AcctReportMainBean(int transaction_type, long id, String name, double amount) {
        super();
        this.transaction_type = transaction_type;
        this.id = id;
        this.name = name;
        this.amount = amount;
	}

	// Contsructor #23
	public AcctReportMainBean(long id, String name, double good, double waste,
                  double return_qty, double stock_qty) {
        super();
        this.id = id;
        this.name = name;
        this.waste = waste;
        this.stock_qty = stock_qty;
        this.good = good;
        this.return_qty = return_qty;
	}

	// Contsructor #24
	public AcctReportMainBean(long id, String name, double quantity,
                double balance) {
        super();
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.balance = balance;
}
	
	// Contsructor #25
	public AcctReportMainBean(long id, String particulars,double amount, double payed, String customer) {
		super();
		this.id = id;
		this.amount = amount;
		this.payed = payed;
		this.customer = customer;
		this.particulars = particulars;
	}
	
	// Contsructor #26
	public AcctReportMainBean(long invoiceId, String bill_no, String comments) {
		super();
		this.invoiceId = invoiceId;
		this.bill_no = bill_no;
		this.comments = comments;
	}
	
	// Contsructor #27
	public AcctReportMainBean(long id, String amount_type, Date date,
			double amount, int transaction_type, String name, String from_or_to, double rate, long currencyId) {
		super();
		this.id = id;
		this.amount_type = amount_type;
		this.date = date;
		this.amount = amount;
		this.transaction_type = transaction_type;
		this.name = name;
		this.rate=rate;
		this.currencyId=currencyId;
		this.setFrom_or_to(from_or_to);
	}
	
	// Contsructor #28
	public AcctReportMainBean(String particulars, String from_or_to, String name, String date_string,
			double debit, double credit, double rate, String currency, double period_balance, double balance) {
		super();
		this.particulars = particulars;
		this.from_or_to = from_or_to;
		this.name = name;
		this.date_string = date_string;
		this.debit = debit;
		this.rate = rate;
		this.credit = credit;
		this.currency=currency;
		this.period_balance=period_balance;
		this.balance=balance;
	}
	
	// Contsructor #29
	public AcctReportMainBean(long id, String particulars, double amount, double payed,
				double returned, double balance, double current_balance,
				double opening_balance) {
			super();
			this.id=id;
			this.particulars = particulars;
			this.amount = amount;
			this.payed = payed;
			this.returned = returned;
			this.balance = balance;
			this.current_balance = current_balance;
			this.opening_balance = opening_balance;
		}
	
	// Contsructor #30
		public AcctReportMainBean(String item, double quantity, String unit, double amount, double total) {
			super();
			this.item=item;
			this.quantity=quantity;
			this.unit=unit;
			this.amount=amount;
			this.total=total;
		}
		
		// Contsructor #31
		public AcctReportMainBean(String account, double amount, String currency) {
			super();
			this.setAccount(account);
			this.amount=amount;
			this.currency=currency;
		}
		
		// Contsructor #32
		public AcctReportMainBean(String account, double amount, String currency, String cheque_no, String cheque_date) {
			super();
			this.setAccount(account);
			this.amount=amount;
			this.currency=currency;
			this.setChequeNo(cheque_no);
			this.setChequeDate(cheque_date);
		}
		

		// Contsructor #33
		public AcctReportMainBean(long id, String particulars, Date date,
				double amount, double payed, String customer,String ref_no) {
			super();
			this.id = id;
			this.particulars = particulars;
			this.date = date;
			this.amount = amount;
			this.payed = payed;
			this.customer = customer;
			this.ref_no = ref_no;
		}
	
		// Contsructor #34
		public AcctReportMainBean(String name, String bill_no, double amount) {
			super();
			this.name = name;
			this.bill_no = bill_no;
			this.amount = amount;
		}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getDispaly_name() {
		return dispaly_name;
	}

	public void setDispaly_name(String dispaly_name) {
		this.dispaly_name = dispaly_name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	public double getDebit() {
		return debit;
	}

	public void setDebit(double debit) {
		this.debit = debit;
	}

	public double getCredit() {
		return credit;
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public List getSubList() {
		return subList;
	}

	public void setSubList(List subList) {
		this.subList = subList;
	}

	public double getCurrent_debit() {
		return current_debit;
	}

	public void setCurrent_debit(double current_debit) {
		this.current_debit = current_debit;
	}

	public double getCurrent_credit() {
		return current_credit;
	}

	public void setCurrent_credit(double current_credit) {
		this.current_credit = current_credit;
	}

	public double getOpening_debit() {
		return opening_debit;
	}

	public void setOpening_debit(double opening_debit) {
		this.opening_debit = opening_debit;
	}

	public double getOpening_credit() {
		return opening_credit;
	}

	public void setOpening_credit(double opening_credit) {
		this.opening_credit = opening_credit;
	}

	public double getBalance_debit() {
		return balance_debit;
	}

	public void setBalance_debit(double balance_debit) {
		this.balance_debit = balance_debit;
	}

	public double getBalance_credit() {
		return balance_credit;
	}

	public void setBalance_credit(double balance_credit) {
		this.balance_credit = balance_credit;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public String getAmount_type() {
		return amount_type;
	}

	public void setAmount_type(String amount_type) {
		this.amount_type = amount_type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getTransaction_type() {
		return transaction_type;
	}

	public void setTransaction_type(int transaction_type) {
		this.transaction_type = transaction_type;
	}

	public Date getFrom_date() {
		return from_date;
	}

	public void setFrom_date(Date from_date) {
		this.from_date = from_date;
	}

	public Date getTo_date() {
		return to_date;
	}

	public void setTo_date(Date to_date) {
		this.to_date = to_date;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getPayed() {
		return payed;
	}

	public void setPayed(double payed) {
		this.payed = payed;
	}

	public double getNeed_to_pay() {
		return need_to_pay;
	}

	public void setNeed_to_pay(double need_to_pay) {
		this.need_to_pay = need_to_pay;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getReturned() {
		return returned;
	}

	public void setReturned(double returned) {
		this.returned = returned;
	}

	public double getCurrent_balance() {
		return current_balance;
	}

	public void setCurrent_balance(double current_balance) {
		this.current_balance = current_balance;
	}

	public double getOpening_balance() {
		return opening_balance;
	}

	public void setOpening_balance(double opening_balance) {
		this.opening_balance = opening_balance;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public double getPeriod_balance() {
		return period_balance;
	}

	public void setPeriod_balance(double period_balance) {
		this.period_balance = period_balance;
	}

	public double getExpence() {
		return expence;
	}

	public void setExpence(double expence) {
		this.expence = expence;
	}

	public double getGross_sale() {
		return gross_sale;
	}

	public void setGross_sale(double gross_sale) {
		this.gross_sale = gross_sale;
	}

	public String getClient_name() {
		return client_name;
	}

	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getRef_no() {
		return ref_no;
	}

	public void setRef_no(String ref_no) {
		this.ref_no = ref_no;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getWaste() {
		return waste;
	}

	public void setWaste(double waste) {
		this.waste = waste;
	}

	public double getStock_qty() {
		return stock_qty;
	}

	public void setStock_qty(double stock_qty) {
		this.stock_qty = stock_qty;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setCheque_date(String cheque_date) {
		this.cheque_date = cheque_date;
	}
	
	public String getCheque_date() {
		if(this.cheque_date!=null && this.cheque_date.length()>2) {
		cheque_date=CommonUtil.formatDateToDDMMYYYY(java.sql.Date.valueOf(cheque_date));
		}

		return cheque_date;
		}

	public double getCurrent_qty() {
		return current_qty;
	}

	public void setCurrent_qty(double current_qty) {
		this.current_qty = current_qty;
	}

	public double getPurchase_qty() {
		return purchase_qty;
	}

	public void setPurchase_qty(double purchase_qty) {
		this.purchase_qty = purchase_qty;
	}


	public String getFrom_or_to() {
		return from_or_to;
	}


	public void setFrom_or_to(String from_or_to) {
		this.from_or_to = from_or_to;
	}


	public int getMax_cedit_period() {
		return max_cedit_period;
	}


	public void setMax_cedit_period(int max_cedit_period) {
		this.max_cedit_period = max_cedit_period;
	}


	public int getDue_days() {
		return due_days;
	}


	public void setDue_days(int due_days) {
		this.due_days = due_days;
	}


	public double getGood() {
		return good;
	}


	public void setGood(double good) {
		this.good = good;
	}

	public double getReturn_qty() {
		return return_qty;
	}

	public void setReturn_qty(double return_qty) {
		this.return_qty = return_qty;
	}

	public String getCheque_no() {
		return cheque_no;
	}

	public void setCheque_no(String cheque_no) {
		this.cheque_no = cheque_no;
	}

	public String getBill_no() {
		return bill_no;
	}

	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}

	public long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(long currencyId) {
		this.currencyId = currencyId;
	}

	public String getDate_string() {
		return date_string;
	}

	public void setDate_string(String date_string) {
		this.date_string = date_string;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	public double getVat() {
		return vat;
	}

	public void setVat(double vat) {
		this.vat = vat;
	}
}
