package es.pryades.erp.dto;

import java.util.List;

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
public class Operation extends BaseDto
{
	private static final long serialVersionUID = 4925551730711401507L;

	static final Logger LOG = Logger.getLogger( Operation.class );

	public static final int STATUS_EXCECUTION 		= 1;
	public static final int STATUS_FINISHED			= 2;
	public static final int STATUS_CLOSED			= 3;

  	private Integer status;
  	private String title;
  	private Long ref_quotation;
  	
  	private Quotation quotation;
  	
  	private List<Invoice> invoices;
  	private List<Purchase> purchases;
  	
  	public Double getTotalSold()
  	{
  		double total = 0.0;
  		
  		for ( Invoice invoice : invoices )
  			total += invoice.getGrandTotalInvoice();
  		
  		return total;
  	}
  	
  	public String getTotalSoldAsString()
  	{
  		return Utils.getFormattedCurrency( getTotalSold() );
  	}

  	public Double getTotalSoldTaxes()
  	{
  		double total = 0.0;
  		
  		for ( Invoice invoice : invoices )
  			total += invoice.getTotalTaxes();
  		
  		return total;
  	}
  	
  	public String getTotalSoldTaxesAsString()
  	{
  		return Utils.getFormattedCurrency( getTotalSoldTaxes() );
  	}

  	public Double getTotalSoldAfterTaxes()
  	{
  		double total = 0.0;
  		
  		for ( Invoice invoice : invoices )
  			total += invoice.getGrandTotalInvoiceAfterTaxes();
  		
  		return total;
  	}
  	
  	public String getGrandTotalAfterTaxesAsString()
  	{
  		return Utils.getFormattedCurrency( getTotalSoldAfterTaxes() );

  	}

  	public Double getTotalPurchased()
  	{
  		double total = 0.0;
  		
  		for ( Purchase purchase : purchases )
  			total += purchase.getNet_price();
  		
  		return total;
  	}
  	
  	public String getTotalPurchasedAsString()
  	{
  		return Utils.getFormattedCurrency( getTotalPurchased() );
  	}

  	public Double getTotalPurchasedTaxes()
  	{
  		double total = 0.0;
  		
  		for ( Purchase invoice : purchases )
  			total += invoice.getNet_tax();
  		
  		return total;
  	}
  	
  	public String getTotalPurchasedTaxesAsString()
  	{
  		return Utils.getFormattedCurrency( getTotalPurchasedTaxes() );
  	}

  	public Double getTotalPurchasedAfterTaxes()
  	{
  		double total = 0.0;
  		
  		for ( Purchase invoice : purchases )
  			total += invoice.getGrossPrice();
  		
  		return total;
  	}
  	
  	public String getTotalPurchasedAfterTaxesAsString()
  	{
  		return Utils.getFormattedCurrency( getTotalPurchasedAfterTaxes() );

  	}

  	
}
