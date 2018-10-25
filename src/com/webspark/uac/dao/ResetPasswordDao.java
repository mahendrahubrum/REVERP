package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.model.S_LoginModel;

public class ResetPasswordDao extends SHibernate implements Serializable{

	/**
	 * @param args
	 */
	List resultList = new ArrayList();
		
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
	
	
	
	
	public List getlabels() throws Exception {
		try {

			begin();
			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(loginId.id, loginId.login_name) from UserModel where " +
					"loginId.userType.active='Y')").list();
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
	
	public void resetPaswrd(long id,String pass) throws Exception {
		try {
			
			begin();
			int r=getSession().createQuery("update S_LoginModel set password=:pas where id=:eid").
			setParameter("pas",pass).setParameter("eid",id).executeUpdate();
			
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
	
	
	public S_LoginModel getselecteditem(long Id) throws Exception {
		S_LoginModel lm=null;
		try {
			

			begin();
			lm = (S_LoginModel)getSession().get(S_LoginModel.class,Id);
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


}
