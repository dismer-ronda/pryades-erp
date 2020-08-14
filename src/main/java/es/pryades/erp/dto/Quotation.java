package es.pryades.erp.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

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
public class Quotation extends BaseDto
{
	private static final long serialVersionUID = 3281091605470723259L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( Quotation.class );
	
	public static final int STATUS_CREATED 		= 0;
	public static final int STATUS_READY		= 1;
	public static final int STATUS_SENT 		= 2;
	public static final int STATUS_APPROVED		= 3;
	public static final int STATUS_FINISHED		= 4;
	public static final int STATUS_DISCARDED	= 5;
	
	private Integer number;						
	private String title;						
	private Long quotation_date;						
	private Integer validity;
	
	private Long ref_customer;

	private String reference_request;
	private String reference_order;
	
	private String origin;
	private String packaging;
	private String delivery;
	private String warranty;
	private String payment_terms;
	private Double tax_rate;
	private Integer status;
	
	private Double transport_invoiced;
	
	private List<QuotationDelivery> deliveries;
	private List<QuotationLine> lines;
	private List<QuotationAttachment> attachments;

	private String customer_name;
	private String customer_address;
	private String customer_tax_id;
	private String customer_email;
	private String customer_phone;
	private Boolean customer_taxable;
	private String customer_language;
	private Boolean customer_signature;
	private String customer_contact_person;

	public String getCustomerAddressAsHtml()
	{
		return Utils.getStringAsHtml( StringEscapeUtils.escapeXml( customer_address != null ? customer_address : "" ) );
	}
	
	public String getFormattedDate()
	{
		return CalendarUtils.getDateFromLongAsString( getQuotation_date(), "dd-MM-yyyy" );
	}
	
	public double getTotalPrice()
	{
		double total_price = 0;
		for ( QuotationLine line : getLines() )
			total_price += line.getTotalPrice();
		 
		return Utils.roundDouble( total_price, 2 );
	}

	public String getTotalPriceAsString()
	{
		return Utils.getFormattedCurrency( getTotalPrice() );
	}

	public double getTotalCost()
	{
		double total_cost = 0;
		for ( QuotationLine line : getLines() )
			total_cost += line.getTotalCost();
		 
		return Utils.roundDouble( total_cost, 2 );
	}

	public String getTotalCostAsString()
	{
		return Utils.getFormattedCurrency( getTotalCost() );
	}

	public double getTotalMargin()
	{
		double total_margin = 0;
		for ( QuotationLine line : getLines() )
			total_margin += line.getTotalMargin();
		 
		return Utils.roundDouble( total_margin, 2 );
	}

	public String getTotalMarginAsString()
	{
		return Utils.getFormattedCurrency( getTotalMargin() );
	}

	public double getTotalTaxes()
	{
		double total_taxes = 0;
		
		if ( customer_taxable.booleanValue() )
			for ( QuotationLine line : getLines() )
				total_taxes += line.getTotalPrice() * (line.getTax_rate() / 100.0);
		
		return Utils.roundDouble( total_taxes, 2 );
	}
	
	public String getTotalTaxesAsString()
	{
		return Utils.getFormattedCurrency( getTotalTaxes() );
	}

	public double getTotalTransportCost()
	{
		double total_cost = 0;
		
		for ( QuotationDelivery delivery : getDeliveries() )
			total_cost += delivery.getFree_delivery().booleanValue() ? 0 : delivery.getCost();
		 
		return Utils.roundDouble( total_cost, 2 );
	}

	public String getTotalTransportCostAsString()
	{
		return Utils.getFormattedCurrency( getTotalTransportCost() );
	}

	public double getTotalTransportCostWithoutFree()
	{
		double total_cost = 0;
		
		for ( QuotationDelivery delivery : getDeliveries() )
			total_cost += delivery.getCost();
		 
		return Utils.roundDouble( total_cost, 2 );
	}

	public String getTotalTransportCostWithoutFreeAsString()
	{
		return Utils.getFormattedCurrency( getTotalTransportCostWithoutFree() );
	}

	public double getTotalTransportTaxes()
	{
		if ( customer_taxable.booleanValue() )
			return getTotalTransportCost() * (getTax_rate() / 100.0);
		 
		return 0;
	}

	public double getGrandTotalQuotation()
	{
		return getTotalPrice() + getTotalTransportCost();
	}

	public String getGrandTotalQuotationAsString()
	{
		return Utils.getFormattedCurrency( getGrandTotalQuotation() );
	}

	public double getGrandTotalQuotationAfterTaxes()
	{
		return getTotalPrice() + getTotalTransportCost() + getTotalTaxes() + getTotalTransportTaxes();
	}
	
	public String getGrandTotalQuotationAfterTaxesAsString()
	{
		return Utils.getFormattedCurrency( getGrandTotalQuotationAfterTaxes() );
	}
	
	public boolean getFreeDeliveries()
	{
		for ( QuotationDelivery delivery : getDeliveries() )
			if ( delivery.getFree_delivery().booleanValue() )
				return true;
		
		return false;
	}
	
	public boolean getTotalFreeDeliveries()
	{
		for ( QuotationDelivery delivery : getDeliveries() )
			if ( !delivery.getFree_delivery().booleanValue() )
				return false;
		
		return true;
	}

	public String getDeparturePorts()
	{
		String ports = "";
		for ( QuotationDelivery delivery : getDeliveries() )
		{
			if ( !ports.isEmpty() )
				ports += ", ";
			ports += delivery.getDeparture_port();
		}
		return ports;
	}
	
	public Integer getItemIndex( QuotationLine line )
	{
		return getLines().indexOf( line ) + 1;
	}
	
	public List<TaxRate> getDetailedTaxes()
	{
		HashMap<Double, Double> tax_totals = new HashMap<Double, Double>();

		tax_totals.put( getTax_rate(), getTotalTransportTaxes() );
		
		for ( QuotationLine line : getLines() )
		{
			Double total = tax_totals.get( line.getTax_rate() );
			if ( total == null )
				total = new Double(0);
			
			for ( QuotationLineDelivery delivery : line.getLine_deliveries() )
				total += delivery.getQuantity() * line.getPrice() * (line.getTax_rate() / 100.0 );
			
			tax_totals.put( line.getTax_rate(), total );
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
	
	public boolean pendingLinesForInvoice()
	{
		for ( QuotationLine line : lines )
		{
			if ( line.getTotal_invoiced() == null )
				return true;
			
			if ( line.getTotal_invoiced() < line.getTotalQuantity() )
				return true;
		}
		
		return false;
	}
	
	public double getPendingTansportCost()
	{
		return getTotalTransportCost() - (transport_invoiced == null ? 0 : transport_invoiced);
	}
	
	public boolean showReferences()
	{
		for ( QuotationLine line : getLines() )
			if ( line.getReference() != null && !line.getReference().isEmpty() )
				return true;
		
		return false;
	}
	
	public double getTransportInvoiced()
	{
		return transport_invoiced != null ? transport_invoiced : 0.0;
	}

	public String getPaymentTermsAsHtml()
	{
		return Utils.getStringAsHtml( StringEscapeUtils.escapeXml( payment_terms ) );
	}

	public String getWarrantyAsHtml()
	{
		return Utils.getStringAsHtml( StringEscapeUtils.escapeXml( warranty ) );
	}
	
	public String getCustomerDataAsHtml( AppContext ctx )
	{
		String data = getCustomer_contact_person() + "\n" + 
				getCustomer_name() + "\n" +
				ctx.getString( "template.common.tax_id" ) + ": " + getCustomer_tax_id() + "\n" + 
				getCustomer_address() + "\n" +
				ctx.getString( "template.common.phone" ) + ": " + getCustomer_phone() + "\n" + 
				ctx.getString( "template.common.email" ) + ": " + getCustomer_email();
		
		return Utils.getStringAsHtml( StringEscapeUtils.escapeXml( data ) );
	}
	
	public boolean isExpired()
	{
		return Utils.getDurationInSeconds( getQuotation_date(), CalendarUtils.getTodayAsLong() ) / (60 * 60 * 24) > getValidity();
	}
}
