/*package com.inventory.gcm.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.inventory.gcm.dao.GcmUserDao;
import com.inventory.gcm.model.GcmUserModel;

*//**
 * @author Jinshad P.T.
 * 
 *         Dec 29, 2014
 *//*

@Path("adduser/{reg_id}/{device_id}/")
public class GCMReqistrationRequestHandler {
	
	@GET
	@Path("")
	@Produces(MediaType.TEXT_PLAIN)
	public String getUserById(@PathParam("reg_id") String reg_id, @PathParam("device_id") String device_id) {
		try {
			if(new GcmUserDao().save(new GcmUserModel(reg_id, device_id)))
				return "success";
			else
				return "failed";
		} catch (Exception e) {
			return "failed";
			// TODO: handle exception
		}
	}
 
	
	
	

}
*/