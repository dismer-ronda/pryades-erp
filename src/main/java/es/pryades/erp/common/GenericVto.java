package es.pryades.erp.common;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 
 * @author Dismer Ronda
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class GenericVto implements Serializable 
{
	private static final long serialVersionUID = 2383665855725296785L;
	
	protected Long id;
	protected VtoControllerFactory factory;
	protected Object dtoObj;
	
	public GenericVto()
	{
	}
}
