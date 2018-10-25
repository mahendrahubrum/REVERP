package com.webspark.uac.dao;

import java.util.Iterator;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.uac.model.StickerPrintingDetailsModel;
import com.webspark.uac.model.StickerPrintingModel;

public class StickerPrintingDao extends SHibernate{

	@SuppressWarnings("finally")
	public long save(StickerPrintingModel obj) throws Exception {

		try {
	
			begin();
			getSession().save(obj);
			commit();
	
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return obj.getId();		
		}
	}
	
	
	public void Update(StickerPrintingModel obj,List<Long> deletedList) throws Exception {

		try {

			begin();
			getSession().update(obj);
			flush();
			getSession().clear();
			Iterator<Long> itr = deletedList.iterator();
			System.out.println("itr ==== ");
			long id;
			while(itr.hasNext()){
				id = itr.next();
				System.out.println(id);
				getSession().delete(new StickerPrintingDetailsModel(id));
			}
			
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}
	
	
	public void delete(long id) throws Exception {

		try {
			begin();
			getSession().delete(new StickerPrintingModel(id));
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getStickers(long org_id) throws Exception {
		List resultList=null;
		try {

			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.StickerPrintingModel(id,name) from StickerPrintingModel where organization_id="+org_id).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	
	public StickerPrintingModel getSticker(long Id) throws Exception {
		StickerPrintingModel lm=null;
		try {
			begin();
			lm = (StickerPrintingModel)getSession().get(StickerPrintingModel.class,Id);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return lm;
	}
	
}
