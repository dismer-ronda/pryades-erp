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
public class Operation extends BaseDto
{
	private static final long serialVersionUID = 4925551730711401507L;

	static final Logger LOG = Logger.getLogger( Operation.class );

	public static final int STATUS_ACCEPTED			= 1;
	public static final int STATUS_EXCECUTION 		= 2;
	public static final int STATUS_DELIVERED		= 3;
	public static final int STATUS_FINISHED			= 4;
	public static final int STATUS_CLOSED			= 5;

  	private Integer status;
  	private String title;
  	private Long ref_quotation;
  	
  	private Quotation quotation;
}
