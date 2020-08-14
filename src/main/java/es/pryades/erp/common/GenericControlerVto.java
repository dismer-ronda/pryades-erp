package es.pryades.erp.common;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

@Data
public abstract class GenericControlerVto implements VtoControler, Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8740278562881272973L;
	@Getter protected AppContext context;
	
	public GenericControlerVto(AppContext ctx)
	{
		this.context = ctx;
	}
	
	@Override
	public String getVtoIdFieldName()
	{
		return "id";
	}
}
