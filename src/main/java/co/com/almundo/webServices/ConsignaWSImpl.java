package co.com.almundo.webServices;



import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.almundo.controllers.Dispatcher;
import co.com.almundo.model.Task;

@WebService(endpointInterface = "co.com.almundo.webServices.ConsignaWS", serviceName = "ConsignaWS",
portName = "ConsignaWSPort",
targetNamespace = "http://sortedset.com/wsdl")
public class ConsignaWSImpl implements ConsignaWS {
	
	private static final Logger log = LoggerFactory.getLogger(ConsignaWSImpl.class);

	public String atenderLlamadas(List<Task> listaLlamadas,
			int numeroTotalOperarios, int numeroTotalSupervidores,
			int numeroTotalDirectores) {
		Dispatcher dispatcher = new Dispatcher(numeroTotalOperarios, numeroTotalSupervidores, numeroTotalDirectores);
		for(Task llamada : listaLlamadas){
			dispatcher.recibirLlamadas(llamada);			
		}		
		return "Envio de llamadas correctamente";
	}

}
