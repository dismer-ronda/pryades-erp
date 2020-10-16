package es.pryades.erp.dto;

import java.util.List;

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
public class Account extends BaseDto
{
	private static final long serialVersionUID = 5896253253258495760L;

	static final Logger LOG = Logger.getLogger( Account.class );

	public static final int TYPE_BANK				= 1;
	public static final int TYPE_CUSTOMER			= 2;
	public static final int TYPE_PROVIDER			= 3;
	public static final int TYPE_CREDIT				= 4;

	private Integer account_type;
	private String name;
  	private String number;
  	private Double credit;
  	
  	private Long ref_company;
  	
	private Company company;
	private List<Transaction> transactions;
}
