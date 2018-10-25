package com.webspark.core;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.webspark.common.util.SMail;

public final class SScheduler {
  
	  private static long office_id=0;
  
  
  public SScheduler(long aInitialDelay, long aDelayBetweenRun, long aStopAfter,TimeUnit unit){
    fInitialDelay = aInitialDelay;
    fDelayBetweenRuns = aDelayBetweenRun;
    fShutdownAfter = aStopAfter;
    fScheduler = Executors.newScheduledThreadPool(NUM_THREADS);   
    this.unit=unit;
  }
  
//  public void activateSchedulerTimer(){
//	  Timer timer = new Timer();
//		TimerTask tt = new TimerTask(){
//			public void run(){
//				Calendar cal = Calendar.getInstance(); //this is the method you should use, not the Date(), because it is desperated.
//
//				int hour = cal.get(Calendar.HOUR_OF_DAY);//get the hour number of the day, from 0 to 23
//				System.out.println("Inside task");
//				if(hour == 18){
//					System.out.println("doing the scheduled task");
//				}
//			}
//		};
//		timer.schedule(tt, 10000, 100000*5);
//  }

  	public void activateScheduler(){
  		
  	  System.out.println("Scheduler will start after : "+fInitialDelay+" "+unit);
  	  System.out.println("Scheduler will run at every: "+fDelayBetweenRuns+" "+unit);
	  Runnable sendAlertMail = new SendAlertMail(new SMail());
	  fScheduler.scheduleWithFixedDelay(sendAlertMail, fInitialDelay, fDelayBetweenRuns,unit);
	 }

  public void activateBatchRunThenStop(long ofc_id){
	  this.office_id=ofc_id;
	  Runnable BatchStartTask = new BatchStartTask();
    ScheduledFuture<?> batchScheduleFuture = fScheduler.scheduleWithFixedDelay(
    		BatchStartTask, fInitialDelay, fDelayBetweenRuns, TimeUnit.SECONDS
    );
    Runnable stopBatchPgm = new StopBatchPgmTask(batchScheduleFuture);
    fScheduler.schedule(stopBatchPgm, fShutdownAfter, TimeUnit.SECONDS);
  }

  // PRIVATE 
  private final ScheduledExecutorService fScheduler;
  private final long fInitialDelay;
  private final long fDelayBetweenRuns;
  private final long fShutdownAfter;
  private final TimeUnit unit;
  

  
  /** If invocations might overlap, you can specify more than a single thread.*/ 
  private static final int NUM_THREADS = 1;
  private static final boolean DONT_INTERRUPT_IF_RUNNING = false;
  
  private static final class BatchStartTask implements Runnable {
    public void run() {
    }
  }
  
  private final class StopBatchPgmTask implements Runnable {
	  StopBatchPgmTask(ScheduledFuture<?> aSchedFuture){
      fSchedFuture = aSchedFuture;
    }
    public void run() {
     
      fSchedFuture.cancel(DONT_INTERRUPT_IF_RUNNING);
     
      fScheduler.shutdown();
    }
    private ScheduledFuture<?> fSchedFuture;
  }
  
  private static void log(String aMsg){
    System.out.println(aMsg);
  }



public long getOffice_id() {
	return office_id;
}



public void setOffice_id(long office_id) {
	this.office_id = office_id;
}
} 