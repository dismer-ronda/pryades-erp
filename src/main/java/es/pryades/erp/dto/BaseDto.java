package es.pryades.erp.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class BaseDto extends Query
{
	private static final long serialVersionUID = 5525038683283246979L;
	
	private Long id;
}
