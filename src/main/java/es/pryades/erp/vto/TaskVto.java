package es.pryades.erp.vto;

import es.pryades.erp.common.GenericVto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Dismer Ronda
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TaskVto extends GenericVto
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1264823424260665825L;
	String month;
    String day;
    String hour;
    String clazz;
    String description;
    String details;
    
	public TaskVto()
	{
	}
}
