package com.inventory.config.stock.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.stock.model.SupplierQuotationDetailsModel;
import com.inventory.config.stock.model.SupplierQuotationModel;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Dec 27, 2013
 */
public class SupplierQuotationDao extends SHibernate {

	private static final long serialVersionUID = 3362574346164877360L;
	
	List resultList;

	public long save(SupplierQuotationModel quotationModel) throws Exception {
		try {
			begin();
			getSession().save(quotationModel);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
			return quotationModel.getId();
		}
	}
	
	public long update(SupplierQuotationModel quotationModel) throws Exception {
		try {
			begin();
			
			List lst=getSession().createQuery(
					"select b.id from SupplierQuotationModel a join a.quotation_details_list b where a.id=:id")
					.setParameter("id", quotationModel.getId()).list();
			
			getSession().update(quotationModel);
			
			flush();
			
			getSession().createQuery(
					"delete SupplierQuotationDetailsModel where id in(:ids)")
					.setParameterList("ids", lst).executeUpdate();
			
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
			return quotationModel.getId();
		}
	}
	

	public SupplierQuotationModel getQuotationModel(long id) throws Exception {
		SupplierQuotationModel dailyQuotationModel = null;
		try {
			begin();

			dailyQuotationModel = (SupplierQuotationModel) getSession().get(SupplierQuotationModel.class, id);

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return dailyQuotationModel;
	}
	
	public SupplierQuotationDetailsModel getDetailModel(long id) throws Exception {
		SupplierQuotationDetailsModel mdl = null;
		try {
			begin();

			mdl = (SupplierQuotationDetailsModel) getSession().get(SupplierQuotationDetailsModel.class, id);

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return mdl;
	}
	
	
	
	public List getAllQuotation(long login) throws Exception {
		try {
			begin();
			
			resultList = getSession().createQuery(
							"select new com.inventory.config.stock.bean.QuotationBean(id, cast(quotation_number as string))" +
							" from SupplierQuotationModel where login_id=:login order by id desc")
					.setParameter("login", login).list();

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	

	public void delete(long id) throws Exception {
		try {
			begin();
			getSession().delete(getSession().get(SupplierQuotationModel.class, id));
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
	}
	
	
	
	public List getQuotationReport(long office_id, long itmId, long suplId, long countryId, Date stDt, 
						Date endDt) throws Exception {
		try {
			resultList=new ArrayList();
			String criteria="";
			
			
			if(itmId!=0)
				criteria+=" and b.item.id="+itmId;
			if(countryId!=0)
				criteria+=" and b.countryId="+countryId;
			
			begin();
			
			if(suplId==0) {
//				if(countryId==0) {   //Constructor 43
					resultList = getSession().createQuery(
							"select new com.webspark.bean.ReportBean(a.date," +
																	"a.quotation_number," +
																	"(select c.name from SupplierModel c where c.login_id=a.login_id)," +
																	"concat(b.item.name, ' (',b.unit.symbol,' )')," +
																	"(select ct.name from CountryModel ct where id=b.countryId)," +
																	"b.rate," +
																	"b.currency.code,a.id,b.id)" +
							"from SupplierQuotationModel a join a.quotation_details_list b where a.office_id=:ofc and a.date between :stdt and :enddt "+criteria)
					.setParameter("ofc", office_id).setParameter("stdt", stDt).setParameter("enddt", endDt).list();
//				}
//				else {
//					List lst=getSession().createQuery("select login_id  from SupplierModel where ledger.office.id=:ofc  and ledger.address.country.id=:ctry and ledger.status=:val and login_id!=0")
//					.setParameter("ctry", countryId).setParameter("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
//					.list();
//					
//					if(lst.size()>0)
//						resultList = getSession().createQuery(
//								"select new com.webspark.bean.ReportBean(a.date, a.quotation_number,(select c.login_name from S_LoginModel c where c.id=a.login_id),concat(b.item.name, ' (',b.unit.symbol,' )'),(select ct.name from CountryModel ct where id=b.countryId),b.rate,b.currency.code)" +
//								"from SupplierQuotationModel a join a.quotation_details_list b where a.office_id=:ofc and a.date between :stdt and :enddt and a.login_id in (:ids) "+criteria)
//								.setParameterList("ids", lst).setParameter("ofc", office_id).setParameter("stdt", stDt).setParameter("enddt", endDt).list();
//				}
			}
			else {
				criteria+=" and a.login_id="+suplId;
				
				resultList = getSession().createQuery(
								"select new com.webspark.bean.ReportBean(a.date, a.quotation_number,(select c.name from SupplierModel c where c.login_id=a.login_id),concat(b.item.name, ' (',b.unit.symbol,' )'),(select ct.name from CountryModel ct where id=b.countryId),b.rate,b.currency.code,a.id,b.id)" +
								"from SupplierQuotationModel a join a.quotation_details_list b where a.office_id=:ofc and a.date between :stdt and :enddt "+criteria)
						.setParameter("ofc", office_id).setParameter("stdt", stDt).setParameter("enddt", endDt).list();
			}
			
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	
	
	
	

}
