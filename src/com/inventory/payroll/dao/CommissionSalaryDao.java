package com.inventory.payroll.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.payroll.model.CommissionSalaryModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Feb 21, 2015
 */

@SuppressWarnings("serial")
public class CommissionSalaryDao extends SHibernate implements Serializable{

	public long save(CommissionSalaryModel mdl, TransactionModel salary)throws Exception{
		try{
			begin();
			getSession().save(salary);
			TransactionDetailsModel tdm=null;
			
			Iterator<TransactionDetailsModel> sitr = salary.getTransaction_details_list().iterator();
			while (sitr.hasNext()) {
				tdm = (TransactionDetailsModel) sitr.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
					.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
					.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

				flush();
			}
			
			mdl.setTransaction_id(salary.getTransaction_id());
			flush();
			getSession().save(mdl);
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl.getId();
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void update(CommissionSalaryModel mdl, TransactionModel salary)throws Exception{
		try{
			begin();
			
			List oldSalaryList = getSession().createQuery("select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + salary.getTransaction_id()).list();
			
			List deleteList=new ArrayList();
			
			TransactionDetailsModel tdm=null;
			
			Iterator<TransactionDetailsModel> ositr = oldSalaryList.iterator();
			while (ositr.hasNext()) {
				tdm = ositr.next();

				getSession()
						.createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

				getSession()
						.createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

				flush();

				deleteList.add(tdm.getId());
			}
			
			getSession().update(salary);
			
			Iterator<TransactionDetailsModel> sitr = salary.getTransaction_details_list().iterator();
			while (sitr.hasNext()) {
				tdm = (TransactionDetailsModel) sitr.next();
				System.out.println("Salary = "+tdm.getAmount());
				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
					.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
					.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

				flush();
			}
			
			getSession().update(mdl);
			flush();
			getSession().createQuery("delete from TransactionDetailsModel where id in (:list)").setParameterList("list", (Collection)deleteList).executeUpdate();
			
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}
	
	
	
	public void delete(CommissionSalaryModel mdl)throws Exception{
		try{
			begin();
			
			TransactionModel transaction=null;
			TransactionDetailsModel tdm=null;
			
			transaction=(TransactionModel)getSession().get(TransactionModel.class, mdl.getTransaction_id());
			
			Iterator<TransactionDetailsModel> ositr = transaction.getTransaction_details_list().iterator();
			while (ositr.hasNext()) {
				tdm = ositr.next();

				getSession()
						.createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

				getSession()
						.createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

				flush();

			}
			getSession().delete(transaction);
			
			flush();
			
			getSession().delete(mdl);
			
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public List getAllCommissionSalary(long office)throws Exception{
		List list=new ArrayList();
		try{
			begin();
			list=getSession().createQuery(	"select new com.inventory.payroll.model.CommissionSalaryModel(id, concat(payment_number,' - ',employee.first_name,' '," +
											"employee.middle_name,' ',employee.last_name,' [ ',cast(date as string),' ]')) " +
											"from CommissionSalaryModel where office.id=:office").setParameter("office", office).list();
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return list;
	}
	
	
	
	public CommissionSalaryModel getCommissionSalaryModel(long id)throws Exception{
		CommissionSalaryModel mdl=null;
		try{
			begin();
			mdl=(CommissionSalaryModel)getSession().get(CommissionSalaryModel.class, id);
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}


	
	public TransactionModel getTransactionModel(long id)throws Exception{
		TransactionModel mdl=null;
		try{
			begin();
			mdl=(TransactionModel)getSession().get(TransactionModel.class, id);
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}

	
	
	public double getPreviousDue(long employee,long office,Date date)throws Exception{
		double due=0;
		try{
			begin();
			Object object=getSession().createQuery("select coalesce(sum(salary-paid_amount),0) from CommissionSalaryModel where employee.id=:employee and" +
					" office.id=:office and date<:date")
					.setParameter("employee", employee).setParameter("office", office).setParameter("date", date).uniqueResult();
			if(object!=null)
				due=(Double)object;
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return due;
	}
	
	
	
	
	public double getCommissionSalary(long employee,long office,Date start, Date end)throws Exception{
		double due=0;
		try{
			begin();
			Object object=getSession().createQuery("select coalesce(sum(salary-paid_amount),0) from CommissionSalaryModel where employee.id=:employee and" +
					" office.id=:office and date between :start and :end")
					.setParameter("employee", employee).setParameter("office", office).setParameter("start", start).setParameter("end", end)
					.uniqueResult();
			if(object!=null)
				due=(Double)object;
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return due;
	}
	
}