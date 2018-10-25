package com.inventory.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.stock.model.ItemModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class ImportItemApparelDao extends SHibernate implements Serializable {

	@SuppressWarnings("rawtypes")
	public long getSizeModel(String size, long office) throws Exception{
		long id=0;
		try{
			begin();
			List list=new ArrayList();
			list=getSession().createQuery("select id from SizeModel where name=:parm and office.id=:office")
					.setParameter("parm", size).setParameter("office", office).list();
			if(list.size()>0){
				Object obj=getSession().createQuery("select id from SizeModel where name=:parm and office.id=:office")
						.setParameter("parm", size).setParameter("office", office).list().get(0);
				if(obj!=null)
					id=(Long)obj;

			}
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
		return id;
	}
	
	@SuppressWarnings("rawtypes")
	public long getColourModel(String colour, long office) throws Exception{
		long id=0;
		try{
			begin();
			List list=new ArrayList();
			list=getSession().createQuery("select id from ColourModel where name=:parm and office.id=:office")
					.setParameter("parm", colour).setParameter("office", office).list();
			if(list.size()>0){
				Object obj=getSession().createQuery("select id from ColourModel where name=:parm and office.id=:office")
						.setParameter("parm", colour).setParameter("office", office).list().get(0);
			if(obj!=null)
				id=(Long)obj;
			}
			
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
		return id;
	}
	
	@SuppressWarnings("rawtypes")
	public long getStyleModel(String style, long office) throws Exception{
		long id=0;
		try{
			begin();
			List list=new ArrayList();
			list=getSession().createQuery("select id from StyleModel where name=:parm and office.id=:office")
					.setParameter("parm", style).setParameter("office", office).list();
			if(list.size()>0){
				Object obj=getSession().createQuery("select id from StyleModel where name=:parm and office.id=:office")
						.setParameter("parm", style).setParameter("office", office).list().get(0);
				if(obj!=null)
					id=(Long)obj;
			}
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
		return id;
	}
	
	@SuppressWarnings("rawtypes")
	public long getItemModelModel(String itemModel, long office) throws Exception{
		long id=0;
		try{
			begin();
			List list=new ArrayList();
			list=getSession().createQuery("select id from ItemModelModel where name=:parm and office.id=:office")
					.setParameter("parm", itemModel).setParameter("office", office).list();
			if(list.size()>0){
				Object obj=getSession().createQuery("select id from ItemModelModel where name=:parm and office.id=:office")
						.setParameter("parm", itemModel).setParameter("office", office).list().get(0);
				if(obj!=null)
					id=(Long)obj;
			}
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
		return id;
	}
	
	@SuppressWarnings("rawtypes")
	public long getContainerModel(String container, long office) throws Exception{
		long id=0;
		try{
			begin();
			List list=new ArrayList();
			list=getSession().createQuery("select id from ContainerModel where name=:parm and office.id=:office")
					.setParameter("parm", container).setParameter("office", office).list();
			if(list.size()>0){
				Object obj=getSession().createQuery("select id from ContainerModel where name=:parm and office.id=:office")
						.setParameter("parm", container).setParameter("office", office).list().get(0);
				if(obj!=null)
					id=(Long)obj;
			}
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
		return id;
	}
	
	@SuppressWarnings("rawtypes")
	public ItemModel getItemModel(long size, long colour, long model, long style, long office) throws Exception{
		ItemModel itemModel=null;
		try{
			begin();
			List list=new ArrayList();
			list=getSession().createQuery("from ItemModel where size=:size and colour=:colour and style=:style and itemModel=:model " +
					"and office.id=:office")
					.setParameter("size", size).setParameter("colour", colour).setParameter("size", size)
					.setParameter("style", style).setParameter("model", model).list();
			if(list.size()>0){
				itemModel=(ItemModel)getSession().createQuery("from ItemModel where size=:size and colour=:colour and style=:style and itemModel=:model " +
						"and office.id=:office")
						.setParameter("size", size).setParameter("colour", colour).setParameter("size", size)
						.setParameter("style", style).setParameter("model", model).list().get(0);
			}
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
		return itemModel;
	}
	
}
