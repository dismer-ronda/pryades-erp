package es.pryades.erp.dto;

import org.apache.log4j.Logger;

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
public class Transaction extends BaseDto
{
	private static final long serialVersionUID = -4099466512430050312L;

	static final Logger LOG = Logger.getLogger( Transaction.class );

	public static final int TYPE_INIT				= 0;
	public static final int TYPE_PAYMENT			= 1;
	public static final int TYPE_INCOME				= 2;
	public static final int TYPE_TRANSFER_SRC		= 3;
	public static final int TYPE_TRANSFER_DST		= 4;
	public static final int TYPE_LOAN				= 5;

	private Long transaction_date;
	private Integer transaction_type;

	private Double amount;
	private Double balance;
  	
	private String description;

	private Long ref_purchase;
	private Long ref_invoice;
	private Long ref_source;
	private Long ref_account;

	private Purchase purchase;
	private Invoice invoice;
	private Account account;
	private Account source;
	
	public String getAmountAsString()
	{
		return Utils.getFormattedCurrency( amount );
	}

	public String getBalanceAsString()
	{
		return Utils.getFormattedCurrency( balance );
	}
}
