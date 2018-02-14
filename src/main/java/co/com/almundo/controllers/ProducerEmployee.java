package co.com.almundo.controllers;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.almundo.model.Task;
import co.com.almundo.util.Util;

/**
 * Esta clase permite simular la actividad de un empleado * 
 * Comparte la clase dispatcher, que contiene la cola de asignación de tareas.
 */
public class ProducerEmployee implements Runnable {

	private static final Logger log = LoggerFactory
			.getLogger(ProducerEmployee.class);

	/** The dispatcher. */
	Dispatcher dispatcher;
	int id = 0;
	int total = 0;
		
	/**
	 * Inicializa un nuevo producer task con un id
	 *
	 * @param dispatcher
	 *            the dispatcher
	 */
	public ProducerEmployee(Dispatcher dispatcher, int id) {
		this.dispatcher = dispatcher;
		this.id=id;
	}

	/**
	 * Libera el empleado que ya atendio una llamada en particular,
	 * para que pueda ser asignado en la atención de una nueva llamada,
	 * teniendo presente el tipo de empleado se identifica la cola a la
	 * cual hay que eliminar el empleado.
	 * @param tipoEmpleado
	 */
	public void liberarCola(Util.ROL tipoEmpleado) {
		switch (tipoEmpleado) {

		case OPERADOR:
			dispatcher.operarioQueue.poll();
			break;

		case SUPERVISOR:
			dispatcher.supervisorQueue.poll();
			break;

		case DIRECTOR:
			dispatcher.directorQueue.poll();
			break;

		}
	}

	/**
	 * Valida si hay llamadas por atender para enviarlas al metodo dispatchCall de la clase
	 * Dispatcher y así puedan ser atendidas
	 */
	public void run() {
		
		// Se valida que las llamadas a atender este dentro del horario laboral de los empleados
		// , para que el siguiente ciclo while se ejecute constantemente la siguiente logica solo
		// durante la jornada de trabajo
		while (Util.validarHorarioLaboral()) {
			try {			
				//	log.info("Ejecutandose Hilo con id: " + id); Se calcula un tiempo aleatorio entre 5 y 10 segundos para
				// determinar la duración de la llamada
				Long time = Util.asignRandomTimeToTask();
				// Valida que existan llamadas por atender
				if (!dispatcher.commonQueue.isEmpty()
						&& dispatcher.commonQueue.size() > 0) {
					dispatcher.hilosQueue.add(this);
					// se obtiene y elimina la llamada a atender de la cola de llamadas registradas, con el fin de que no
					// pueda ser atendida por otro hilo (Empleado)
					Task llamada = dispatcher.commonQueue.poll();
					// Se valida que la información de la llamada sea adecuada
					if (llamada != null && llamada.getId() != 0) {
						// Se envia la llamada al metodo dispatchCall de la clase Dispatcher para que pueda ser atendida
						Util.ROL tipoEmpleado = dispatcher.dispatchCall(
								llamada, time, this.id);
						// Se pregunta que el tipoEmpleado retornado sea valido, ya que si el tipoEmpleado es diferente de null
						// quiere decir que la llamada fue atendida y se puede proceder a liberar el empleado previo al tiempo
						// de duración de la llamada, por otro lado si el tipoEmpleado es null da a entender que la llamada no
						// fue atendida y es necesario agregar nuevamente esta llamada a la cola.
						if (tipoEmpleado != null) {
							// El hilo se duerme cuando se cumpla el tiempo de la llamada simulando el tiempo que tiene el empleado
							// para atender la llamada
							Thread.sleep(time);
							// Finalmente se procede a liberar el empleado para que quede disponible y así poder atender una
							// nueva llamada entrante
							liberarCola(tipoEmpleado);
							log.info("Finalizando la llamada con id: "
									+ llamada.getId());
							dispatcher.hilosQueue.poll();
						} else {
							// Se agreaga nuevamente la llamada porque no pudo ser atendida
							dispatcher.commonQueue.add(llamada);
							log.info("La llamada con id "+llamada.getId()+" No pudo ser atendida porque en este momento"
									+ " todos nuestros empleados se encuentran ocupados, Espere un momento por favor para ser atendido ");
						}
					}

				}
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
		// Solo el hilo con id = 1 tendra la tarea de Bajar los 10 hilos 
		if(this.id == 1){			
			try {				
				dispatcher.executor.shutdown();
				dispatcher.executor.awaitTermination(2, TimeUnit.MINUTES);
				log.info("Hilo con id "+id+ " Ha bajado los 10 hilos");
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
		
	}
}
