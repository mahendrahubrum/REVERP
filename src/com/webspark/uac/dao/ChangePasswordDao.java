package com.webspark.uac.dao;

import java.io.Serializable;

import com.webspark.dao.SHibernate;
import com.webspark.model.S_LoginModel;

public class ChangePasswordDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5507793242284082291L;



	/**
	 * @param args
	 */
	
	
	
	
	public void Update(S_LoginModel md) throws Exception {

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
	
	
	
	public String getPassword(long id) throws Exception {
		String nam=null;
		try {

			begin();
			 nam=(String) getSession().createQuery("select password from S_LoginModel where id=:cid")
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
	
	
	
	public void resetPaswrd(long id,String pass) throws Exception {
		try {
			
			begin();
			int r=getSession().createQuery("update S_LoginModel set password=:pas where id=:eid").
			setParameter("pas",pass).setParameter("eid",id).executeUpdate();
			System.out.println("rows updated"+r);
			
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
	
	
	
	}
	
	
	

	

