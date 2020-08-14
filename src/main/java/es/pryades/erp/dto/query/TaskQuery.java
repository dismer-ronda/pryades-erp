package es.pryades.erp.dto.query;

import es.pryades.erp.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class TaskQuery extends BaseDto
{
	private static final long serialVersionUID = -6292323974427681863L;

	private Integer clazz;
	private Integer system;
}
