package co.com.almundo.webServices;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import co.com.almundo.model.Task;

@WebService(targetNamespace = "http://sortedset.com/wsdl")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ConsignaWS {
	
	@WebMethod
	public String atenderLlamadas(@WebParam(name = "task") List<Task> listaLlamadas, @WebParam(name = "numeroTotalOperarios") int numeroTotalOperarios, 
			@WebParam(name = "numeroTotalSupervidores") int numeroTotalSupervidores, @WebParam(name = "numeroTotalDirectores") int numeroTotalDirectores);

}
