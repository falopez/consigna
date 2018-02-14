package co.com.almundo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.almundo.controllers.Dispatcher;
import co.com.almundo.model.Task;

public class App {
	
	private static final Logger log = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
//		log.info("********** Inicio test llamadas y trabajo concurrente asincronico **********.");
		// Se define el total de operadores 5, supervisores 2 y directores 3 disponibles
		Dispatcher dispatcher = new Dispatcher(5, 3, 2);
		// Se simula el registro de 10 llamadas
		for(int i = 0 ; i<= 10 ; i++){
			dispatcher.recibirLlamadas(new Task(i));			
		}
	}
	
	

}
