package co.com.almundo.task;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.almundo.controllers.Dispatcher;
import co.com.almundo.model.Task;

/**
 * La presente tarea automatica informa que las llamadas no atendidas no han sido procesadas
 * porque todos los empleados se encuentran ocupados con otras llamadas, que por favor esperen un 
 * momento para ser atendidos
 * @author fagalind
 *
 */
public class TaskCallHold implements Job {
	
	private static final Logger log = LoggerFactory.getLogger(TaskCallHold.class);
	
	/**
	 * Se valida que el tamaño de la lista de hilos (Empleados) ocupados con otras llamadas sea igual a 10
	 * y que la cola de llamadas sea mayor que cero, con el fin de imprimir un mensaje que permita
	 * comprender que en determinado momento las llamadas con sus respectivos id's no pueden ser atendidas
	 * porque no hay quien las procese.
	 */
    public void execute(JobExecutionContext context) throws JobExecutionException {

    	Map<String, Object> mapRecursos = context.getJobDetail().getJobDataMap().getWrappedMap();
    	Dispatcher dispatcher = (Dispatcher) mapRecursos.get("dispatcher");

        if( dispatcher.getHilosQueue().size() == 10 && dispatcher.getCommonQueue().size() > 0){
        	StringBuilder mensaje = new StringBuilder();
        	mensaje.append("Las tareas con id ");
        	Object[] listaLlamada = dispatcher.getCommonQueue().toArray();
        	for( Object llamadaLista :  listaLlamada){
        		Task llamada = (Task) llamadaLista;
        		mensaje.append(llamada.getId()+" , ");
        	}
        	mensaje.append("no han sido atendidas aun porque en este momento todos los empleados se encuentran ocupados, "
        			+ "por favor aguardar un momento para ser atendidos");
        	log.info(mensaje.toString());
        }
    }//end method

}
