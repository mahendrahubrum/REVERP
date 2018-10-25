package com.inventory.config.stock.dao;

import java.util.List;

import com.inventory.config.stock.model.GradeModel;
import com.webspark.dao.SHibernate;

public class GradeDao extends SHibernate{

	public List getAllGrades(long officeID) throws Exception {
		List resultList;
			try {

				begin();
				resultList = getSession()
						.createQuery(
								"from GradeModel  where officeId=:ofc order by id")
						.setParameter("ofc", officeID).list();
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
			return resultList;
		}

	public void save(GradeModel objModel) throws Exception {
		try {

			begin();
			getSession().save(objModel);
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

	public GradeModel getGrade(Long gradeId) throws Exception {
		GradeModel gm;
		try {

			begin();
			gm=(GradeModel) getSession().get(GradeModel.class, gradeId);
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
		return gm;
	}

	public void delete(Long gradeId) throws Exception {
		try {
			GradeModel gm;
			begin();
			gm=(GradeModel) getSession().get(GradeModel.class, gradeId);
			getSession().delete(gm);
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

	public void update(GradeModel objModel) throws Exception {
		try {
			begin();
			getSession().update(objModel);
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

	public GradeModel getGradeFromStock(long purchaseInvDetailId) throws Exception {
		GradeModel mdl;
		long gradeId=0;
		try {
			begin();
			gradeId=(Long) getSession().createQuery("select gradeId from ItemStockModel where inv_det_id=:id").setParameter("id", purchaseInvDetailId).uniqueResult();
			mdl=(GradeModel) getSession().get(GradeModel.class, gradeId);
			
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
		
		return mdl;
	}

	public GradeModel getGradeFromName(String gradeName) throws Exception {
		GradeModel mdl=null;
		long gradeId=0;
		try {
			begin();
			Object obj= getSession().createQuery(" from GradeModel where name=:gradeName").setParameter("gradeName", gradeName).uniqueResult();
			if(obj!=null)
				mdl=(GradeModel) obj;
			
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
		return mdl;
	}


}
