package es.pryades.erp.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Authorization;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class Company extends BaseDto
{
	private static final long serialVersionUID = -545526390909070300L;
	
	static final Logger LOG = Logger.getLogger( Company.class );

	public static final int TYPE_OWNER				= 0;
	public static final int TYPE_PROVIDER 			= 1;
	public static final int TYPE_CUSTOMER 			= 2;
	public static final int TYPE_TRANSPORTER 		= 3;
	public static final int TYPE_INSURER			= 4;
	public static final int TYPE_BANK				= 5;

	private String alias;
	private String name;
  	private String tax_id;
  	private String address;
  	private String email;
  	private String phone;
  	private Boolean taxable;
	private String language;
  	private Boolean signature;
  	private Integer type_company;
	private String contact_person;

  	private byte[] logo;
}
