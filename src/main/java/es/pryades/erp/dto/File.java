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
public class File extends BaseDto
{
	private static final long serialVersionUID = -5454063112645785714L;

	private String file_name;
	private Long file_date;
	private byte[] file_binary;
}
