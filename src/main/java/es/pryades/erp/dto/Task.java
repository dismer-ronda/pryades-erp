package es.pryades.erp.dto;

import es.pryades.erp.common.AppContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class Task extends BaseDto
{
	private static final long serialVersionUID = -2274211758046839179L;

	private String timezone;
	private String language;
	private String month;
	private String day;
	private String hour;
	private Integer times;
	private Integer system;
	
	private Integer clazz;
	private String description;
	private String details;

	public String getTaskClazzAsString( AppContext ctx )
   	{
   		return ctx.getString( "task.clazz." + getClazz() ); 
   	}
}
