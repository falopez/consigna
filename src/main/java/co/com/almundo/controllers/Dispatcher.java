package co.com.almundo.controllers;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.almundo.model.Task;
import co.com.almundo.task.GestorTareaCallHold;
import co.com.almundo.util.Util;

/**
 * La presente clase gestiona las llamadas recibiendolas y asignandolas a los empleados disponibles.
 * El constructor de la presente clase levanta 10 hilos simulando los 10 empleados de la compañia, 
 * los cuales permiten atender 10 llamadas al mismo tiempo (De modo recurrente)
 */
//@Controller
public class Dispatcher {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);


	/** Cola de llamadas. */
	Queue<Task> commonQueue = new ConcurrentLinkedQueue<Task>();

	/** Cola de Operarios . */
	LinkedBlockingQueue<Task> operarioQueue;

	/** Cola de Directores. */
	LinkedBlockingQueue<Task> directorQueue;

	/** Cola de Supervisores. */
	LinkedBlockingQueue<Task> supervisorQueue;
	
	/** Cola de Supervisores. */
	LinkedBlockingQueue<ProducerEmployee> hilosQueue;

	/** Total de operarios prestos a atender llamadas. */
	int totalOperarios;

	/** Total de supervisores prestos a atender llmadas. */
	int totalSupervisores;

	/** Total de directores prestos a atender llamadas. */
	int totalDirectores;

	ExecutorService executor;
	
	/**
	 * Inicializar un nuevo dispatcher indicando el total de empleados,
	 * supervisores y directores que existen, ademas inicializa un pool de
	 * 10 hilos que representan 10 empleados disponibles quienes juntos podran atender
	 * 10 llamadas concurrentes y se inicializa una tarea automatica encargada de brindar
	 * solución sobre qué pasa con una llamada cuando no hay ningún empleado libre.
	 * @param totalOperarios
	 *            total de operarios
	 * @param totalSupervisores
	 *            total de supervisores
	 * @param totalDirectores
	 *            total de directores
	 */
	public Dispatcher(int totalOperarios, int totalSupervisores, int totalDirectores) {
		this.totalOperarios = totalOperarios;
		this.totalSupervisores = totalSupervisores;
		this.totalDirectores = totalDirectores;
		operarioQueue = new LinkedBlockingQueue<Task>(totalOperarios);
		directorQueue = new LinkedBlockingQueue<Task>(totalDirectores);
		supervisorQueue = new LinkedBlockingQueue<Task>(totalSupervisores);
		hilosQueue = new LinkedBlockingQueue<ProducerEmployee>();
		executor = Executors.newFixedThreadPool(10);
		try {
			for (int i = 1; i <= 10; i++) {
				executor.execute(new ProducerEmployee(this, i));
			}
		} catch (RejectedExecutionException e) {
			log.error(e.getMessage(), e);
		}
		GestorTareaCallHold.iniciarTarea(this);
	}

	/**
	 * Brinda información sobre la atención de la llamada.
	 *
	 * @param tipoEmpleado
	 *            Tipo de empleado
	 * @param tiempoLlamada
	 *            el tiempo de duración de la llamada
	 */
	public void informarAtencionLlamada(Util.ROL tipoEmpleado, Long tiempoLlamada, Task llamada, int idHiloEjecucion) {
		switch (tipoEmpleado) {
		case OPERADOR:

				log.info( tipoEmpleado +" atendiendo llamada "
						+ llamada.getId() + " tiempo llamada " + tiempoLlamada+
						" Hilo id "+idHiloEjecucion+
						" Cantidad de OPERADORES libres "+(totalOperarios - operarioQueue.size())
						+" Cantidad de SUPERVISORES libres "+(totalSupervisores - supervisorQueue.size())
						+" Cantidad de DIRECTORES libres "+(totalDirectores - directorQueue.size()));
			break;
		case SUPERVISOR:

				log.info( tipoEmpleado + " atendiendo llamada "
						+ llamada.getId() + " tiempo llamada " + tiempoLlamada+
						" Hilo id "+idHiloEjecucion+
						" Cantidad de OPERADORES libres "+(totalOperarios - operarioQueue.size())
						+" Cantidad de SUPERVISORES libres "+(totalSupervisores - supervisorQueue.size())
						+" Cantidad de DIRECTORES libres "+(totalDirectores - directorQueue.size()));
			break;
		case DIRECTOR:

				log.info( tipoEmpleado + " atendiendo llamada "
						+ llamada.getId() + " tiempo llamada " + tiempoLlamada+
						" Hilo id "+idHiloEjecucion+
						" Cantidad de OPERADORES libres "+(totalOperarios - operarioQueue.size())
						+" Cantidad de SUPERVISORES libres "+(totalSupervisores - supervisorQueue.size())
						+" Cantidad de DIRECTORES libres "+(totalDirectores - directorQueue.size()));
			break;

		}

	}

	/**
	 * Valida que las llamadas se emitan dentro del horario laboral
	 * para posteriormente recibir las llamadas y almacenarlas en la cola de llamadas.
	 *
	 * @param task
	 *            the task
	 */
	public void recibirLlamadas(Task llamada) {
		if(Util.validarHorarioLaboral() ){
			commonQueue.add(new Task(llamada.getId()));
			
		} else{
			log.info("La llamada con id "+ llamada.getId()+" no puede ser atendida porque se emite fuera del horario laboral comprendido entre"
					+ " 8 am - 12 pm y 1 pm - 6 pm  ");
		}	
	}

	/**
	 * Asigna las llamadas a los empleados disponibles teniendo presente que una llamada
		telefónica en primera instancia debe ser atendida por un operador, si
		no hay ninguno libre debe ser atendida por un supervisor, y de no
		haber tampoco supervisores libres debe ser atendida por un director
	 *
	 * @param llamada
	 *            Información de llamada a atender
	 * @param tiempoLlamada
	 *            Tiempo de duración de la llamada
	 * @param idHiloEjecucion
	 *            id del hilo en ejecución           
	 * @return Util.ROL
	 *            Tipo de empleado
	 */
	public Util.ROL dispatchCall(Task llamada, Long tiempoLlamada, int idHiloEjecucion) {
		try{
		
			// Se asigna la llamada a un operario, adicionandola a la cola de operadores para simular que un operador ha sido ocupado con una llamada
			operarioQueue.add(llamada);
			informarAtencionLlamada(Util.ROL.OPERADOR, tiempoLlamada, llamada, idHiloEjecucion);
			return Util.ROL.OPERADOR;
		
		}catch (IllegalStateException e){	
			// La presente excepcion se lanza al no poder adicionar un objeto mas a la cola de operarios, indicando que ya no hay operarios disponibles
			// y que se deben validar disponivilidad de supervisores
			return atenderSupervisor(llamada,tiempoLlamada,idHiloEjecucion);
		}
		
	}
	
	/**
	 * Asigna la llamada a un supervisor
	 * @param llamada
	 * @param tiempoLlamada
	 * @param idHiloEjecucion
	 * @return
	 */
	public Util.ROL atenderSupervisor(Task llamada, Long tiempoLlamada, int idHiloEjecucion){
		try{
			
			// Se asigna la llamada a un supervisor, adicionandola a la cola de supervisores para simular que un supervisor ha sido ocupado con una llamada
			supervisorQueue.add(llamada);
			informarAtencionLlamada(Util.ROL.SUPERVISOR, tiempoLlamada, llamada, idHiloEjecucion);
			return Util.ROL.SUPERVISOR;
		
		}catch (IllegalStateException e){
			// La presente excepcion se lanza al no poder adicionar un objeto mas a la cola de supervisores, indicando que ya no hay operarios disponibles
			// y que se deben validar disponivilidad de directores
			return atenderDirector(llamada,tiempoLlamada,idHiloEjecucion);
		}
	}
	
	/**
	 * Asigna la llamada a un director
	 * @param llamada
	 * @param tiempoLlamada
	 * @param idHiloEjecucion
	 * @return
	 */
	public Util.ROL atenderDirector(Task llamada, Long tiempoLlamada, int idHiloEjecucion){
		try{
			
			// Se asigna la llamada a un director, adicionandola a la cola de directores para simular que un director ha sido ocupado con una llamada
			directorQueue.add(llamada);
			informarAtencionLlamada(Util.ROL.DIRECTOR, tiempoLlamada, llamada, idHiloEjecucion);
			return Util.ROL.DIRECTOR;
		
		}catch (IllegalStateException e){
			return null;
		}
	}

	/**
	 * Gets the common queue.
	 *
	 * @return the common queue
	 */
	public Queue<Task> getCommonQueue() {
		return commonQueue;
	}

	/**
	 * Sets the common queue.
	 *
	 * @param commonQueue
	 *            the new common queue
	 */
	public void setCommonQueue(Queue<Task> commonQueue) {
		this.commonQueue = commonQueue;
	}

	/**
	 * Obtiene la cola de operadores.
	 *
	 * @return cola de operadores
	 */
	public LinkedBlockingQueue<Task> getOperario() {
		return operarioQueue;
	}

	/**
	 * fija una cola de operadores
	 *
	 * @param employeeQueue
	 *            the new employee queue
	 */
	public void setOperarioQueue(LinkedBlockingQueue<Task> operarioQueue) {
		this.operarioQueue = operarioQueue;
	}

	/**
	 * Gets the director queue.
	 *
	 * @return the director queue
	 */
	public LinkedBlockingQueue<Task> getDirectorQueue() {
		return directorQueue;
	}

	/**
	 * Sets the director queue.
	 *
	 * @param directorQueue
	 *            the new director queue
	 */
	public void setDirectorQueue(LinkedBlockingQueue<Task> directorQueue) {
		this.directorQueue = directorQueue;
	}

	/**
	 * Obtiene una cola de supervisores.
	 *
	 * @return Cola de supervisores
	 */
	public LinkedBlockingQueue<Task> getSupervisorQueue() {
		return supervisorQueue;
	}

	/**
	 * Fija una cola de supervisores
	 *
	 * @param supervisorQueue
	 *            Cola de supervisores
	 */
	public void setSupervisorQueue(LinkedBlockingQueue<Task> supervisorQueue) {
		this.supervisorQueue = supervisorQueue;
	}

	/**
	 * Obtiene total de operarios.
	 *
	 * @return total de operarios
	 */
	public int getTotalOperarios() {
		return totalOperarios;
	}

	/**
	 * Fija total de operarios
	 *
	 * @param totalOperarios
	 *            Total de operarios
	 */
	public void setTotalOperarios(int totalOperarios) {
		this.totalOperarios = totalOperarios;
	}

	/**
	 * Obtiene total de supervisores.
	 *
	 * @return Total de supervisores
	 */
	public int getTotalSupervisores() {
		return totalSupervisores;
	}

	/**
	 * Fija total supervisores.
	 *
	 * @param totalSupervisores
	 *            Total supervisores
	 */
	public void setTotalSupervisor(int totalSupervisores) {
		this.totalSupervisores = totalSupervisores;
	}

	/**
	 * Obtiene total de directores.
	 *
	 * @return total directores
	 */
	public int getTotalDirectores() {
		return totalDirectores;
	}

	/**
	 * Fija total de directores
	 *
	 * @param totalDirectores
	 *            Total de directores
	 */
	public void setTotalDirectores(int totalDirectores) {
		this.totalDirectores = totalDirectores;
	}

	public LinkedBlockingQueue<ProducerEmployee> getHilosQueue() {
		return hilosQueue;
	}

	public void setHilosQueue(LinkedBlockingQueue<ProducerEmployee> hilosQueue) {
		this.hilosQueue = hilosQueue;
	}

	public LinkedBlockingQueue<Task> getOperarioQueue() {
		return operarioQueue;
	}

	public void setTotalSupervisores(int totalSupervisores) {
		this.totalSupervisores = totalSupervisores;
	}

}
