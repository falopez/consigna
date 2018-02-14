package co.com.almundo.task;

import java.text.ParseException;
import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.almundo.controllers.Dispatcher;

/**
 * Permite configurar elementos relevantes de la tarea automatica construida para poder informar
 * que determinadas llamadas en cola que no ha sido atendidas aun, y seran procesadas previo a la disponibilidad
 * de uno de los empleados
 * @author fagalind
 *
 */
public class GestorTareaCallHold {
	
	private static final String CRON_EXPRESSION_CALL_HOLD = "0/3 * * * * ?";
	
	private static GestorTareaCallHold GestorTareaCallHold = null;
	private static final Logger log = LoggerFactory.getLogger(GestorTareaCallHold.class);
	
    private static final String TRIGGER = "trigger";
    private static final String GRUPO = "grupo";
    private static final String TASK_PROCESS_CALL_HOLD = "TaskCallHold";
    
    Scheduler scheduler = null;
    
    private GestorTareaCallHold(){
    	try {
            scheduler = new StdSchedulerFactory().getScheduler();
        } catch (SchedulerException ex) {
        	log.error(ex.getMessage(), ex);
        }
    }
    
    public static GestorTareaCallHold getInstance(){
        if(GestorTareaCallHold==null){
        	GestorTareaCallHold = new GestorTareaCallHold();
        }
        return GestorTareaCallHold;
    }//end method
    
    public static synchronized void iniciarTarea(Dispatcher dispatcher) {
        getInstance().validarLlamadasEnEspera(dispatcher);
    }//end method
    
    public static synchronized void finalizarTarea() {
        getInstance().terminarTarea();
    }//end method
    
    

	private void validarLlamadasEnEspera(Dispatcher dispatcher) {
		try {
	
			if (scheduler.isStarted()) {
				scheduler.clear();
			}
				JobDetail jobDetail = new JobDetailImpl(TASK_PROCESS_CALL_HOLD,GRUPO, TaskCallHold.class, true, true);
				jobDetail.getJobDataMap().put("dispatcher", dispatcher);
				CronTrigger cronTrigger = null;

				cronTrigger = new CronTriggerImpl(TRIGGER, GRUPO, CRON_EXPRESSION_CALL_HOLD);
				Date scheduleJob = scheduler.scheduleJob(jobDetail, cronTrigger);

				scheduler.start();

			
		} catch (SchedulerException ex) {
			log.error(ex.getMessage(), ex);
		} catch (ParseException ex) {
			log.error(ex.getMessage(), ex);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}// Fin del metodo
		
    private synchronized void terminarTarea(){
        try {
            scheduler.shutdown();
            
        } catch (SchedulerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }    

}
