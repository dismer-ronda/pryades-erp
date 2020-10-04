package es.pryades.erp.dto;

import org.apache.log4j.Logger;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class CompanyContact extends BaseDto
{
	private static final long serialVersionUID = -1552770918638243731L;

	static final Logger LOG = Logger.getLogger( CompanyContact.class );

	private Long ref_company;
	
	private String name;
  	private String email;
  	private String phone;
}
