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
public class Profile extends BaseDto
{
	private static final long serialVersionUID = 7463395719344370925L;

	private String description;
}
