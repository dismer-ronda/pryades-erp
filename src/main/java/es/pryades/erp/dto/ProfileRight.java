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
public class ProfileRight extends BaseDto
{
	private static final long serialVersionUID = -8390588598517459612L;

	private Long ref_profile;
	private Long ref_right;
	
	private String right_code;
	private String right_description;
}
