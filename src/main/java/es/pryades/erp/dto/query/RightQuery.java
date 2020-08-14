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
public class RightQuery extends BaseDto
{
	private static final long serialVersionUID = 3314675085674755228L;

	private Long ref_user;
	private String right_code;
}
