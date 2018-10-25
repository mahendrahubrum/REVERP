package com.webspark.dao;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SEncryption;
import com.webspark.model.S_LoginHistoryModel;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.UserModel;

/**
 * @Author Jinshad P.T.
 */

public class LoginDao extends SHibernate implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -713057005135085260L;
	private List resultList=new ArrayList();

	public boolean isExist(String login_name) throws Exception {
		int ct=0;
		try {
			begin();
			ct=getSession().createCriteria(S_LoginModel.class)
				.add(Restrictions.eq("login_name", login_name)).list().size();
			commit();
			
			if(ct>0)
				return true;
			else
				return false;
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			
		}
	}
	
	
	public boolean isProductExpire(long orgId) throws Exception {
		boolean expired=false;
		try {
			begin();
			
			Object obj=getSession().createQuery("select expiry_date from ProductLicenseModel where organizationId=:orgId").setParameter("orgId", orgId).uniqueResult();
			commit();
			
			if(obj!=null) {
				Timestamp exp=(Timestamp) obj;
				if(exp.getTime()< new Date().getTime())
					expired=true;
			}
				
			
			return expired;
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			
		}
	}
	
	
	
	public UserModel getUserFromLoginName(String login_name) throws Exception {
		UserModel obj=null;
		try {
			begin();
			
			obj=(UserModel) getSession().createQuery("from UserModel where loginId.login_name=:ln")
					.setParameter("ln", login_name).uniqueResult();
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return obj;
		}
	}
	
	
	public boolean isValidPass(String login_name,String plainPass) throws Exception {
		int ct=0;
		try {
			begin();
			ct=getSession().createCriteria(S_LoginModel.class)
					.add(Restrictions.eq("login_name", login_name))
				.add(Restrictions.eq("password", SEncryption.encrypt(plainPass))).list().size();
			commit();
			
			if(ct>0)
				return true;
			else
				return false;
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			
		}
	}
	
	
	

	//Added By Anil
	public UserModel getUserFromLoginId(long loginId) throws Exception {
		UserModel obj=null;
		try {
			begin();
			
			obj=(UserModel) getSession().createQuery("from UserModel where loginId.id=:loginId")
					.setParameter("loginId", loginId).uniqueResult();
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return obj;
		}
	}
	
	public S_LoginModel getLoginFromLoginName(String login_name) throws Exception {
		S_LoginModel obj=null;
		try {
			begin();
			
			obj=(S_LoginModel) getSession().createQuery("from S_LoginModel where login_name=:ln")
					.setParameter("ln", login_name).uniqueResult();
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return obj;
		}
	}
	
	
	public S_LoginModel getLoginModel(long id) throws Exception {
		S_LoginModel obj=null;
		try {
			begin();
			
			obj=(S_LoginModel) getSession().get(S_LoginModel.class, id);
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return obj;
		}
	}
	
	
	
	public long doRecordUserLogin(long login_id) throws Exception {
		S_LoginHistoryModel logHisModel=new S_LoginHistoryModel();
		try {
			
			logHisModel.setActive('Y');
			logHisModel.setLogged_in_time(new Timestamp(CommonUtil.getFormattedCurrentTime().getTime()));
			logHisModel.setLogged_out_time(logHisModel.getLogged_in_time());
			logHisModel.setLogin_id(login_id);
			
			begin();
			
//			getSession().createQuery("update S_LoginHistoryModel set active='N' where login_id=:login and active='Y'")
//			.setParameter("login", login_id).executeUpdate();
			
			getSession().save(logHisModel);
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return logHisModel.getId();
		}
	}
	
	
	public void doLogoutAllUsers() throws Exception {
		try {
			
			begin();
			
			getSession().createQuery("update S_LoginHistoryModel set active='N' where active='Y'")
						.executeUpdate();
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	
	public void doRecordUserLogout(long hist_id) throws Exception {
		try {
			
			begin();
			
//			Object obj=getSession().createQuery("select max(id) from S_LoginHistoryModel where login_id=:login and active='Y'")
//					.setLong("login", login_id).uniqueResult();
			
//			if(obj!=null)
				
			getSession().createQuery("update S_LoginHistoryModel set logged_out_time=:tim,active='N' where id=:id")
					.setLong("id", hist_id).setTimestamp("tim", new Timestamp(new Date().getTime())).executeUpdate();
			
//			getSession().createQuery("update S_LoginHistoryModel set active='N' where login_id=:login and active='Y'")
//					.setParameter("login", login_id).executeUpdate();
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	
}
