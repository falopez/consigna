package co.com.almundo.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.almundo.controllers.Dispatcher;
import co.com.almundo.model.Task;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(AppTest.class);

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}

	/**
	 * Simulando 10 llamadas concurrentes para ser atendidas por 
	 * 5 OPERADORES, 3 SUPERVISORES y 2 DIRECTORES disponibles
	 * @throws InterruptedException
	 * 
	 */
	public void testSimluadorLlamadas() throws InterruptedException {

		log.info("********** Simulando 10 llamadas Concurrentes **********.");
		// Se define el total de operadores 5, supervisores 2 y directores 3 disponibles
		Dispatcher dispatcher = new Dispatcher(5, 3, 2);
		// Se simula el registro de 10 llamadas
		for(int i = 0 ; i<= 10 ; i++){
			dispatcher.recibirLlamadas(new Task(i));			
		}
	}
	

}