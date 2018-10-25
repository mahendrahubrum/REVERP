package com.inventory.payroll.dao;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.inventory.payroll.model.AttendanceModel;
import com.inventory.payroll.model.LeaveDateModel;
import com.inventory.payroll.model.OverTimeModel;
import com.inventory.payroll.model.PayrollEmployeeMapModel;
import com.inventory.payroll.model.SalaryDisbursalModel;
import com.inventory.payroll.model.UserLeaveMapModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class SalaryDisbursalDao extends SHibernate implements Serializable {

	@SuppressWarnings({"rawtypes" })
	public double getPayrollForUser(long user, long office)throws Exception {
		double payroll=0;
		try {
			begin();
			List list=new ArrayList();
			list = getSession().createQuery("from PayrollEmployeeMapModel where employee.id=:user and employee.office.id=:office")
								.setParameter("user", user).setParameter("office", office).list();
			if(list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					PayrollEmployeeMapModel map = (PayrollEmployeeMapModel) itr.next();
					
					if(map.getComponent().getAction()==SConstants.payroll.ADDITION){
						payroll+=map.getValue();
					}
					else if(map.getComponent().getAction()==SConstants.payroll.DEDUCTION){
						payroll-=map.getValue();
					}
				}
			}
			commit();
		} 
		catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return payroll;
	}
	
	
	public double getAdvancePaidToUser(long user, long office, Date end)throws Exception {
		double advance=0;
		try {
			begin();
			advance = (Double)getSession().createQuery("select coalesce(sum(amount/conversionRate),0) from EmployeeAdvancePaymentModel where user.id=:user" +
											"  and office.id=:office and date<:end and salary_id=0")
											.setParameter("user", user).setParameter("office", office).setParameter("end", end).uniqueResult();
			commit();
		} 
		catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return advance;
	}
	
	
	@SuppressWarnings("rawtypes")
	public double getLopDaysForUser(long user, long office, Date start, Date end)throws Exception {
		double days=0;
		try {
			begin();
			List leaveList=new ArrayList();
			leaveList=getSession().createQuery("from LeaveDateModel where officeId=:office and leave is not null and date " +
									" between :start and :end and leave.user.id=:user")
									.setParameter("office", office).setParameter("user", user)
									.setParameter("start", start).setParameter("end", end).list();
			Iterator itr=leaveList.iterator();
			while (itr.hasNext()) {
				LeaveDateModel leaveDate = (LeaveDateModel) itr.next();
				if(leaveDate.getLeave()!=null){
					if(leaveDate.getLeave().getLeave_type().isLop())
						days+=leaveDate.getDays();
				}
			}
			
			leaveList=getSession().createQuery("from UserLeaveMapModel where officeId=:office and date between :start and :end " +
					" and userId=:user")
					.setParameter("office", office).setParameter("user", user)
					.setParameter("start", start).setParameter("end", end).list();
			itr=leaveList.iterator();
			while (itr.hasNext()) {
				UserLeaveMapModel leaveMap = (UserLeaveMapModel) itr.next();
				if(leaveMap.isLossOfPay()){
					days+=leaveMap.getNoOfDays();
				}
			}
			commit();
		} 
		catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return days;
	}

	
	@SuppressWarnings("rawtypes")
	public double getOverTimeForUser(long user, long office, Date start, Date end)throws Exception {
		double overTime=0;
		try {
			begin();
			List attendanceList=new ArrayList();
			attendanceList=getSession().createQuery("from AttendanceModel where officeId=:office and presentLeave!=:status and date " +
									" between :start and :end and userId=:user")
									.setParameter("office", office).setParameter("user", user)
									.setParameter("start", start).setParameter("end", end)
									.setParameter("status", SConstants.attendanceStatus.LEAVE).list();
			
			Iterator itr=attendanceList.iterator();
			Calendar calendar=Calendar.getInstance();
			while (itr.hasNext()) {
				AttendanceModel attendance = (AttendanceModel) itr.next();
				
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				double perHour=0;
				
				if(attendance.getOvertime()!=0){
					
					OverTimeModel otMdl=(OverTimeModel)getSession().get(OverTimeModel.class, attendance.getOvertime());
					
					calendar.add(Calendar.HOUR_OF_DAY, new SalaryDisbursalDao().calculateHour(attendance.getOver_time_out(), attendance.getOver_time_in()));
					
					if(otMdl.getValueType()==SConstants.payroll.PERCENTAGE){
						double payroll=0;
						payroll=(Double)getSession().createQuery("select coalesce(value, 0) from PayrollEmployeeMapModel where employee.id=:user and component.id=:component")
										.setParameter("user", user).setParameter("component", otMdl.getPayrollComponent()).uniqueResult();
						perHour=CommonUtil.roundNumber(payroll*otMdl.getValue()/100);
					}
					else if(otMdl.getValueType()==SConstants.payroll.FIXED){
						perHour=CommonUtil.roundNumber(otMdl.getValue());
					}
					overTime+=CommonUtil.roundNumber(calendar.get(Calendar.HOUR_OF_DAY)* perHour);
				}
			}
			commit();
		} 
		catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return overTime;
	}
	
	
	public double getLoansForUser(long user, long office, Date start, Date end)throws Exception {
		double loanAmount=0;
		try {
			begin();
			loanAmount = (Double)getSession().createQuery("select coalesce(sum(amount/conversionRate),0) from LoanDateModel where loan.loanRequest.user.id=:user" +
											"  and officeId=:office and date between :start and :end and loanStatus!=:status")
											.setParameter("user", user).setParameter("office", office)
											.setParameter("start", start).setParameter("end", end)
											.setParameter("status", SConstants.loanPaymentStatus.PAYMENT_DONE).uniqueResult();
			commit();
		} 
		catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return loanAmount;
	}

	
	public double getCommissionForUser(long user, long office, Date start, Date end)throws Exception {
		double commission=0;
		try {
			begin();
			double salesAmount=0;
			int noOfSales=0;
			double percent=0;
			salesAmount= (Double)getSession().createQuery("select coalesce(sum(amount/conversionRate),0) from SalesModel where responsible_employee=:user" +
						" and office.id=:office and date between :start and :end")
						.setParameter("user", user).setParameter("office", office)
						.setParameter("start", start).setParameter("end", end).uniqueResult();
			
			noOfSales= (Integer)getSession().createQuery("select count(id) from SalesModel where responsible_employee=:user" +
											" and office.id=:office and date between :start and :end")
											.setParameter("user", user).setParameter("office", office)
											.setParameter("start", start).setParameter("end", end).uniqueResult();
			
			percent = (Double)getSession().createQuery("select coalesce(commissionPercentage,0) from SalesManCommissionMapModel where userId=:user" +
									"  and officeId=:office").setParameter("user", user).setParameter("office", office).uniqueResult();
			
			salesAmount=CommonUtil.roundNumber(salesAmount/noOfSales);
			
			commission=CommonUtil.roundNumber(salesAmount*percent/100);
			
			commit();
		} 
		catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return commission;
	}
	

	public SalaryDisbursalModel getSalaryDisbursalModel(long user, long office, Date start, Date end)throws Exception {
		SalaryDisbursalModel mdl=null;
		try {
			begin();
			mdl=(SalaryDisbursalModel)getSession().createQuery("from SalaryDisbursalModel where officeId=:office and user.id=:user and" +
										" from_date=:start and to_date=:end")
										.setParameter("user", user).setParameter("office", office)
										.setParameter("start", start).setParameter("end", end).uniqueResult();
			commit();
		} 
		catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return mdl;
	}

	
	public SalaryDisbursalModel getSalaryDisbursalModel(long id)throws Exception {
		SalaryDisbursalModel mdl=null;
		try {
			begin();
			mdl=(SalaryDisbursalModel)getSession().get(SalaryDisbursalModel.class, id);
			commit();
		} 
		catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return mdl;
	}
	
	
	public int calculateHour(Timestamp end , Timestamp start){
		int hour=0;
		try {
			hour = (int) ((end.getTime()-start.getTime())/(1000*60*60));
		} catch (Exception e) {
			hour=0;
		}
		return hour;
	}
	
//	public double getAmount(long id, long empId) throws Exception {
//
//		double amt = 0;
//		try {
//			PayrollEmployeeMapModel map = map.getPayRollMap(empId, id);
//			if (map != null) {
//				if (map.getComponent().getType() == SConstants.payroll.FIXED) {
//					amt = map.getValue();
//				} 
//				else {
//					double par_amt = getAmount(map.getComponent().getParent_id(), empId);
//					amt = par_amt * map.getValue() / 100;
//				}
//			}
//			return amt;
//		} catch (Exception e) {
//			throw e;
//		}
//
//	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void save(Hashtable<TransactionModel,SalaryDisbursalModel> hash, Date start, long office) throws Exception {
		try {
			begin();
			Calendar startCalendar=Calendar.getInstance();
			Calendar endCalendar=Calendar.getInstance();
			startCalendar.setTime(start);
			endCalendar.setTime(start);
			endCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			List list=new ArrayList();
			List oldChildList=new ArrayList();
			List oldTransList=new ArrayList();
			
			oldChildList=getSession().createQuery("select b.id from SalaryDisbursalModel a join a.detailsList b where a.from_date=:start" +
					" and a.to_date=:end and a.officeId=:office")
					.setParameter("office", office).setParameter("start", CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime()))
					.setParameter("end", CommonUtil.getSQLDateFromUtilDate(endCalendar.getTime())).list();
			
			list=getSession().createQuery("from SalaryDisbursalModel where from_date=:start and to_date=:end and officeId=:office")
					.setParameter("office", office).setParameter("start", CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime()))
					.setParameter("end", CommonUtil.getSQLDateFromUtilDate(endCalendar.getTime())).list();
			
			if(list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					SalaryDisbursalModel mdl = (SalaryDisbursalModel) itr.next();
					if(mdl.getTransactionId()!=0){
						TransactionModel trans=(TransactionModel)getSession().get(TransactionModel.class, mdl.getTransactionId());
						Iterator titr=trans.getTransaction_details_list().iterator();
						while (titr.hasNext()) {
							TransactionDetailsModel det = (TransactionDetailsModel) titr.next();
							getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
										.setDouble("amt", det.getAmount()).setLong("id", det.getFromAcct().getId()).executeUpdate();

							getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
										.setDouble("amt", det.getAmount()).setLong("id", det.getToAcct().getId()).executeUpdate();
							flush();
							oldTransList.add(det.getId());
						}
					}
					getSession().createQuery("update EmployeeAdvancePaymentModel set salary_id=0 where user.id=:user" +
							"  and office.id=:office and date<:end and salary_id=:id")
							.setParameter("office", office).setParameter("user", mdl.getUser().getId()).setParameter("id", mdl.getId())
							.setParameter("end", CommonUtil.getSQLDateFromUtilDate(endCalendar.getTime())).executeUpdate();
					flush();
				}
			}
			flush();			
			getSession().clear();
			if(hash!=null && hash.size()>0){
				Iterator itr=hash.keySet().iterator();
				while (itr.hasNext()) {
					TransactionModel trans = (TransactionModel) itr.next();
					SalaryDisbursalModel mdl=hash.get(trans);
					
					if(mdl.getTransactionId()!=0)
						getSession().update(trans);
					else
						getSession().save(trans);
					flush();
					
					Iterator titr=trans.getTransaction_details_list().iterator();
					while (titr.hasNext()) {
						TransactionDetailsModel det = (TransactionDetailsModel) titr.next();
						getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
									.setDouble("amt", det.getAmount()).setLong("id", det.getFromAcct().getId()).executeUpdate();

						getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
									.setDouble("amt", det.getAmount()).setLong("id", det.getToAcct().getId()).executeUpdate();
						flush();
					}
					mdl.setTransactionId(trans.getTransaction_id());
					
					if(mdl.getId()!=0)
						getSession().update(mdl);
					else
						getSession().save(mdl);
					
					flush();
					
					getSession().createQuery("update EmployeeAdvancePaymentModel set salary_id=:id where user.id=:user" +
							"  and office.id=:office and date<:end and salary_id=0")
							.setParameter("office", office).setParameter("user", mdl.getUser().getId()).setParameter("id", mdl.getId())
							.setParameter("end", CommonUtil.getSQLDateFromUtilDate(endCalendar.getTime())).executeUpdate();
					
					flush();
				}
			}
			if(oldChildList.size()>0){
				getSession().createQuery("delete from SalaryDisbursalDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) oldChildList).executeUpdate();
				flush();
			}
			
			if(oldTransList.size()>0){
				getSession().createQuery("delete from TransactionDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) oldTransList).executeUpdate();
				flush();
			}
			
			commit();
		} 
		catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
	}

	
	@SuppressWarnings({ "rawtypes"})
	public void delete(Date start, long office) throws Exception {
		try {
			begin();
			Calendar startCalendar=Calendar.getInstance();
			Calendar endCalendar=Calendar.getInstance();
			startCalendar.setTime(start);
			endCalendar.setTime(start);
			endCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			List list=new ArrayList();
			list=getSession().createQuery("from SalaryDisbursalModel where from_date=:start and to_date=:end and officeId=:office")
					.setParameter("office", office).setParameter("start", CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime()))
					.setParameter("end", CommonUtil.getSQLDateFromUtilDate(endCalendar.getTime())).list();
			if(list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					SalaryDisbursalModel mdl = (SalaryDisbursalModel) itr.next();
					if(mdl.getTransactionId()!=0){
						TransactionModel trans=(TransactionModel)getSession().get(TransactionModel.class, mdl.getTransactionId());
						Iterator titr=trans.getTransaction_details_list().iterator();
						while (titr.hasNext()) {
							TransactionDetailsModel det = (TransactionDetailsModel) titr.next();
							getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
										.setDouble("amt", det.getAmount()).setLong("id", det.getFromAcct().getId()).executeUpdate();

							getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
										.setDouble("amt", det.getAmount()).setLong("id", det.getToAcct().getId()).executeUpdate();
							flush();
						}
						getSession().delete(trans);
					}
					getSession().createQuery("update EmployeeAdvancePaymentModel set salary_id=0 where user.id=:user" +
							"  and office.id=:office and date<:end and salary_id=:id")
							.setParameter("office", office).setParameter("user", mdl.getUser().getId()).setParameter("id", mdl.getId())
							.setParameter("end", CommonUtil.getSQLDateFromUtilDate(endCalendar.getTime())).executeUpdate();
					flush();
					getSession().delete(mdl);
				}
			}
			flush();			
			commit();
		} 
		catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
	}
	
	
}
