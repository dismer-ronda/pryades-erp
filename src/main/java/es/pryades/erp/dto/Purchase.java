package es.pryades.erp.dto;

import org.apache.log4j.Logger;

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
public class Purchase extends BaseDto
{
	private static final long serialVersionUID = -9010544362542408721L;

	static final Logger LOG = Logger.getLogger( Purchase.class );

	public static final int STATUS_CREATED		= 1;
	public static final int STATUS_ORDERED		= 2;
	public static final int STATUS_RECEIVED		= 3;

	public static final int TYPE_SELL 				= 1;
	public static final int TYPE_CONSUMABLE			= 2;
	public static final int TYPE_TRANSPORT			= 3;
	public static final int TYPE_BANK_COMISSION 	= 4;
	public static final int TYPE_BANK_INTEREST		= 5;
	public static final int TYPE_COMMUNICATIONS		= 6;
	public static final int TYPE_EQUIPMENT			= 7;
	public static final int TYPE_SERVICE			= 8;
	public static final int TYPE_SOCIAL_SECURITY	= 9;
	public static final int TYPE_PAYROLL			= 10;
	public static final int TYPE_VAT				= 11;
	public static final int TYPE_PIC				= 12;
	public static final int TYPE_SOCIETY_TAXES		= 13;
	public static final int TYPE_OTHER_TAXES		= 14;
	public static final int TYPE_INSURANCE			= 15;
	public static final int TYPE_MARKETING			= 16;
	public static final int TYPE_ALLOWANCES			= 17;
	public static final int TYPE_OTHER				= 18;

	private Integer purchase_type;						

	private Integer number;						
	private String title;
	private String description;

	private Long purchase_date;
	private Long register_date;
	
	private Double net_price;
	private Double net_tax;
	private Double net_retention;

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
  	
	public String getFormattedDate()
	{
		return CalendarUtils.getDateFromLongAsString( getPurchase_date(), "dd-MM-yyyy" );
	}
	
	public String getNetPriceAsString()
	{
		return Utils.getFormattedCurrency( net_price );
	}

	public String getNetTaxAsString()
	{
		return Utils.getFormattedCurrency( net_tax );
	}

	public String getRetentionAsString()
	{
		return Utils.getFormattedCurrency( net_retention );
	}

	public String getPayedAsString()
	{
		return Utils.getFormattedCurrency( payed );
	}

	public double getGrossPrice()
  	{
  		return net_price + net_tax - net_retention;
  	}
  	
	public String getGrossPriceAsString()
	{
		return Utils.getFormattedCurrency( getGrossPrice() );
	}

	public int getTaxRate()
  	{
  		return (int)((net_tax / net_price) * 100);
  	}
  	
	public String getTaxRateAsString()
	{
		return Integer.toString( getTaxRate() );
	}

	public int getRetentionRate()
  	{
  		return (int)((net_retention / net_price) * 100);
  	}
  	
	public String getRetentionRateAsString()
	{
		return Integer.toString( getRetentionRate() );
	}

	public String getFormattedNumber() 
	{
		int year = number / 100000;
		int index = number % 100000;
		
		return Integer.toString( year ) + "-" + String.format("%05d", index );
	}
}
