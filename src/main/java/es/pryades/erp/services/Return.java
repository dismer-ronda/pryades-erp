package es.pryades.erp.services;

import java.io.Serializable;

import es.pryades.erp.common.Utils;
import lombok.Data;

/**
 * @author Dismer Ronda Betancourt (dismer.ronda@pryades.com)
 * @version 
 * @since Jul, 2010
 */
@Data 
public class Return implements Serializable
{
	private static final long serialVersionUID = 3835110462791866142L;
	
	private int Code;
    private String Desc;
	
	public Return()
	{
		Code = 200;
		Desc = "";
	}
	
	static public Return getReturn( String text ) throws Exception
	{
		return (Return) Utils.toPojo( text, Return.class, false );
	}
}
