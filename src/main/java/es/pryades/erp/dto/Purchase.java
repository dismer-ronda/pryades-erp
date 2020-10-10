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
public class Purchase extends BaseDto
{
	private static final long serialVersionUID = -9010544362542408721L;

	static final Logger LOG = Logger.getLogger( Purchase.class );

	public static final int STATUS_CREATED		= 1;
	public static final int STATUS_ORDERED		= 2;
	public static final int STATUS_RECEIVED		= 3;

	public static final int TYPE_SELL 			= 1;
	public static final int TYPE_CONSUMABLE		= 2;
	public static final int TYPE_TRANSPORT		= 3;
	public static final int TYPE_BANK_COMISSION = 4;
	public static final int TYPE_BANK_INTEREST	= 5;
	public static final int TYPE_COMMUNICATIONS	= 6;
	public static final int TYPE_EQUIPMENT		= 7;
	public static final int TYPE_SERVICE		= 8;
	public static final int TYPE_OTHER			= 20;

	private Integer purchase_type;						

	private Integer number;						
	private String title;
	private String description;

	private Long purchase_date;
	private Long register_date;
	
	private Double net_price;
	private Double net_tax;

	private Double payed;
	private Integer status;
	
	private byte[] quotation;
	private String quotation_number;
	
	private byte[] invoice;
	private String invoice_number;
  	
	private byte[] payment;

	private Long ref_operation;
	private Long ref_buyer;
	private Long ref_provider;
	private Long ref_contact;

  	private Company provider;
  	private CompanyContact contact;
	private User buyer;
  	private Operation operation;
  	
	public String getNetPriceAsString()
	{
		return Utils.getFormattedCurrency( net_price );
	}

	public String getNetTaxAsString()
	{
		return Utils.getFormattedCurrency( net_tax );
	}

	public String getPayedAsString()
	{
		return Utils.getFormattedCurrency( payed );
	}

	public double getGrossPrice()
  	{
  		return net_price + net_tax;
  	}
  	
	public String getGrossPriceAsString()
	{
		return Utils.getFormattedCurrency( getGrossPrice() );
	}

	public String getFormattedNumber() 
	{
		int year = number / 100000;
		int index = number % 100000;
		
		return Integer.toString( year ) + "-" + String.format("%05d", index );
	}
}