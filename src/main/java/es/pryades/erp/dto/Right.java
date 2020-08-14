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
public class Right extends BaseDto
{
	private static final long serialVersionUID = -4503581797785002202L;

	private String code;
	private Integer display_order;
}
