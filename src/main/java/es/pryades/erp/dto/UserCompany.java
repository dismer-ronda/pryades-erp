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
public class UserCompany extends BaseDto
{
	private static final long serialVersionUID = -3640421854553755345L;
	
	private Long ref_user;
  	private Long ref_company;
  	
  	private String user_name;
  	private String company_name;
}
