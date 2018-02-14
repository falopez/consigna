package co.com.almundo.util;

import java.util.Calendar;
import java.util.Random;

/**
 * La presente clase brinda funcionalidades especificas que pueden ser usadas por diferentes componentes del
 * aplicativo.
 * 
 */
public class Util {
	
	public static int TIME_MIN = 5000;
	public static int TIME_MAX = 10000;
	public static final String ATENDIDA = "ATENDIDA";
	public static final String PROCESANDO = "PROCESANDO";
	
	/**
	 * Este metodo permite asignar aleatoriamente un valor 
	 * entre 5 y 10 segundos.
	 *
	 * @return the long
	 */
	public static Long asignRandomTimeToTask(){
		Random randomGenerator = new Random();
		int value = randomGenerator.nextInt(TIME_MAX - TIME_MIN + 1) + TIME_MIN;
		return new Long(value);
	}
	
	/**
	 * Enum clasificador del tipo de empleado
	 * @author fagalind
	 *
	 */
    public static enum ROL{
    	/** The employee. */
    	OPERADOR,
    	
    	/** The supervisor. */
    	SUPERVISOR,
    	
    	/** The director. */
    	DIRECTOR;
    	
    }//Fin enumeracion
    
    /**
	 * Valida que la hora actual este comprendida en los siguientes dos ragos de horas
	 * 1. 8:00:00 - 12:00:00 (Jornada laboral mañana)
	 * 2. 1:00:00 - 18:00:00 (Jornada laboral tarde)
	 * Lo anterior con el fin de verificar que las llamadas entrantes esten comprendidas en las 
	 * jorndas mañana y tarde en las cuales laboran los empleados
	 * @return
	 */
	public static boolean validarHorarioLaboral(){
		Calendar fechaActual = Calendar.getInstance();
		
		Calendar jornadaMananaInicial = Calendar.getInstance();
		jornadaMananaInicial.set(Calendar.HOUR_OF_DAY, 7);
		jornadaMananaInicial.set(Calendar.MINUTE, 59);
		jornadaMananaInicial.set(Calendar.SECOND, 59);
		
		Calendar jornadaMananaFinal = Calendar.getInstance();
		jornadaMananaFinal.set(Calendar.HOUR_OF_DAY, 12);
		jornadaMananaFinal.set(Calendar.MINUTE, 0);
		jornadaMananaFinal.set(Calendar.SECOND, 1);
		
		Calendar jornadaTardeInicial = Calendar.getInstance();
		jornadaTardeInicial.set(Calendar.HOUR_OF_DAY, 12);
		jornadaTardeInicial.set(Calendar.MINUTE, 59);
		jornadaTardeInicial.set(Calendar.SECOND, 59);
		
		Calendar jornadaTardeFinal = Calendar.getInstance();
		jornadaTardeFinal.set(Calendar.HOUR_OF_DAY, 18);
		jornadaTardeFinal.set(Calendar.MINUTE, 0);
		jornadaTardeFinal.set(Calendar.SECOND, 1);
		
		if( (fechaActual.after(jornadaMananaInicial) && fechaActual.before(jornadaMananaFinal)) ||
				(fechaActual.after(jornadaTardeInicial) && fechaActual.before(jornadaTardeFinal))
				){
//			log.info("Jornada laboral");
			return true;
		}
//		log.info("Jornada no laboral");
		return true;
	}

}
