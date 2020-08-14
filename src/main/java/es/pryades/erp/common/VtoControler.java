package es.pryades.erp.common;

/**
 * Interfaz con los metodos para mapear de un objeto Dto a Vto y viceversa
 * 
 * @author Dismer Ronda
 *
 */
public interface VtoControler 
{	
	public String getVtoIdFieldName();
	
	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException;
}
