package es.pryades.erp.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import es.pryades.erp.common.AppContext;
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
public class Invoice extends BaseDto
{
	private static final long serialVersionUID = -4885183308521631509L;
	
	private String title;						
	private Integer number;						
	private Long invoice_date;						
	
	private Long ref_quotation;
	private Long ref_shipment;

	private Double transport_cost;
	private Boolean free_delivery; 	
	private String payment_terms;
	private Double collected;

	private Quotation quotation;
	private Shipment shipment;
	
	private List<InvoiceLine> lines;
	
	public String getFormattedDate()
	{
		return CalendarUtils.getDateFromLongAsString( getInvoice_date(), "dd-MM-yyyy" );
	}
	
	public double getTotalPrice()
	{
		double total_price = 0;
		
		if ( getLines() != null )
		{
			for ( InvoiceLine line : getLines() )
				total_price += line.getTotalPrice();
		}
		 
		return Utils.roundDouble( total_price, 2 );
	}
	
	public String getTotalPriceAsString()
	{
		return Utils.getFormattedCurrency( getTotalPrice() );
	}

	public String getCollectedAsString()
	{
		return Utils.getFormattedCurrency( collected );
	}

	public double getTotalTaxes()
	{
		double total_taxes = 0;
		
		if ( getQuotation().getCustomer().getTaxable().booleanValue() )
		{
			for ( InvoiceLine line : getLines() )
				total_taxes += line.getTotalTaxes();
		}		 
		
		return Utils.roundDouble( total_taxes, 2 );
	}
	
	public String getTotalTaxesAsString()
	{
		return Utils.getFormattedCurrency( getTotalTaxes() );
	}

	public String getTransportCostAsString()
	{
		return Utils.getFormattedCurrency( getTransport_cost() );
	}

	public double getTotalTransportTaxes()
	{
		if ( getQuotation().getCustomer().getTaxable().booleanValue() )
			return getTransport_cost() * (getQuotation().getTax_rate() / 100.0);
		 
		return 0;
	}
	
	public double getGrandTotalInvoice()
	{
		return getTotalPrice() + (free_delivery ? 0 : getTransport_cost());
	}

	public String getGrandTotalInvoiceAsString()
	{
		return Utils.getFormattedCurrency( getGrandTotalInvoice() );
	}

	public double getGrandTotalTaxes()
	{
		return getTotalTaxes() + getTotalTransportTaxes();
	}
	
	public String getGrandTotalTaxesAsString()
	{
		return Utils.getFormattedCurrency( getTotalTaxes() + getTotalTransportTaxes() );
	}
	
	public double getGrandTotalInvoiceAfterTaxes()
	{
		return getTotalPrice() + getTransport_cost() + getTotalTaxes() + getTotalTransportTaxes();
	}
	
	public String getGrandTotalInvoiceAfterTaxesAsString()
	{
		return Utils.getFormattedCurrency( getGrandTotalInvoiceAfterTaxes() );
	}
	
	public Integer getItemIndex( InvoiceLine line )
	{
		int i = 1;
		for ( InvoiceLine line1 : lines )
		{
			if ( line1.getId().equals( line.getId() ) )
				return i;
			i++;
		}
		
		return 0;
	}
	
	public List<TaxRate> getDetailedTaxes()
	{
		HashMap<Double, Double> tax_totals = new HashMap<Double, Double>();

		if ( getQuotation().getTax_rate() != 0 )
			tax_totals.put( getQuotation().getTax_rate(), getTotalTransportTaxes() );
		
		for ( InvoiceLine line : getLines() )
		{
			double rate = line.getQuotation_line().getTax_rate();
			
			if ( rate != 0 )
			{
				Double total = tax_totals.get( line.getQuotation_line().getTax_rate() );

				if ( total == null )
					total = new Double(0);
				
				total += line.getTotalTaxes();
				
				tax_totals.put( line.getQuotation_line().getTax_rate(), total );
			}
		}
		
		List<TaxRate> ret = new ArrayList<TaxRate>();
		
		for ( Iterator<Map.Entry<Double, Double>> it = tax_totals.entrySet().iterator(); it.hasNext(); ) 
		{
            Map.Entry<Double, Double> pair = it.next();
			ret.add( new TaxRate( pair.getKey(), Utils.getFormattedCurrency( pair.getValue() ) ) ); 
        }
		
		return ret;
	}
	
	public String getFormattedNumber() 
	{
		int year = number / 10000;
		int index = number % 10000;
		
		return Integer.toString( year ) + "-" + String.format("%04d", index );
	}
	
	public Integer getLineQuantity( QuotationLine quotationLine )
	{
		if ( lines != null )
		{
			for ( InvoiceLine line : lines )
			{
				if ( line.getRef_quotation_line().equals( quotationLine.getId() ) )
					return line.getQuantity();
			}
		}
		
		return 0;
	}

	public Integer getLinePacked( QuotationLine quotationLine )
	{
		if ( lines != null )
		{
			for ( InvoiceLine line : lines )
			{
				if ( line.getRef_quotation_line().equals( quotationLine.getId() ) )
					return line.getTotal_packed() != null ? line.getTotal_packed() : 0;
			}
		}
		
		return 0;
	}

	public InvoiceLine getInvoiceLine( QuotationLine quotationLine )
	{
		if ( lines != null )
		{
			for ( InvoiceLine line : lines )
			{
				if ( line.getRef_quotation_line().equals( quotationLine.getId() ) )
					return line;
			}
		}
		
		return null;
	}


	public String getIncoterms()
	{
		return shipment != null ? shipment.getIncoterms() : "";
	}

	public String getDeparture_port()
	{
		return shipment != null ? shipment.getDeparture_port() : "";
	}

	public String getCustomerDataAsHtml( AppContext ctx )
	{
		return quotation.getCustomerDataAsHtml( ctx );
	}

	public String getPaymentTermsAsHtml()
	{
		return Utils.getStringAsHtml( StringEscapeUtils.escapeXml( payment_terms ) );
	}

	public int getTaxRate()
  	{
  		return (int)((getGrandTotalTaxes() / getGrandTotalInvoice()) * 100);
  	}
  	
	public String getTaxRateAsString()
	{
		return Integer.toString( getTaxRate() );
	}
	
	public boolean isFullyCollected()
	{
		return Utils.roundDouble( getGrandTotalInvoiceAfterTaxes(), 2 ) - Utils.roundDouble( collected, 2 ) == 0;
	}
}
