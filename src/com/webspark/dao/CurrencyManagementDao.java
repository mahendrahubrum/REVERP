package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.model.CurrencyModel;

public class CurrencyManagementDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8154024201944425266L;
	/**
	 * @param args
	 */
	List resultList = new ArrayList();
	public long addOption(CurrencyModel lm) throws Exception {

		try {

			begin();
			getSession().save(lm);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return lm.getId();
		}
	}
	
	
	public void Update(CurrencyModel md) throws Exception {

		try {

			begin();
			getSession().update(md);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}
	
	
	public void delete(long id) throws Exception {

		try {
			begin();
			getSession().delete(new CurrencyModel(id));
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}
	
	
	public List getlabels() throws Exception {
		try {

			begin();
			resultList = getSession().createQuery("select new com.webspark.model.CurrencyModel(id,name) FROM CurrencyModel").list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	public List getCurrencyCode() throws Exception {
		try {

			begin();
			resultList = getSession().createQuery("select new com.webspark.model.CurrencyModel(id,code) FROM CurrencyModel").list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public List getCurrencySymbol() throws Exception {
		try {

			begin();
			resultList = getSession().createQuery("select new com.webspark.model.CurrencyModel(id,symbol) FROM CurrencyModel order by name").list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	public void setCurrency(long currid,long id) throws Exception {
		try {
			
			begin();
			getSession().createQuery("update S_OfficeModel set currency=:curid where id=:officid").setParameter("curid",new CurrencyModel(currid)).setParameter("officid",id).executeUpdate();
			
			commit();
			System.out.println("query ok");

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			
		}
	}
	
	public String getCurrency(long id) throws Exception {
		String nam=null;
		try {

			begin();
			 nam=(String) getSession().createQuery("select name from CurrencyModel where id=:cid")
					.setParameter("cid", id).uniqueResult();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return nam;
		}
	}
	
	public CurrencyModel getselecteditem(long Id) throws Exception {
		CurrencyModel lm=null;
		try {
			

			begin();
			lm = (CurrencyModel)getSession().get(CurrencyModel.class,Id);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return lm;
		}
	}
	
	
	
	public String getCurrencySymbolName(long id) throws Exception {
		String nam=null;
		try {

			begin();
			 nam=(String) getSession().createQuery("select symbol from CurrencyModel where id=:cid")
					.setParameter("cid", id).uniqueResult();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return nam;
		}
	}
}
