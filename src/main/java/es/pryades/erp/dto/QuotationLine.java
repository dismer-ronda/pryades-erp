package es.pryades.erp.dto;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

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
public class QuotationLine extends BaseDto
{
	private static final long serialVersionUID = -8950910464292763179L;

	private Long ref_quotation;						
	private Long ref_provider;
	
	private Integer line_order;
	private String origin;
	private String reference;
	private String title;
	private String description;

	private Double cost;
	private Double margin;
	private Double real_cost;
	private Double tax_rate;

	private Integer total_invoiced;

	private String provider_name;

	private List<QuotationLineDelivery> line_deliveries;
	
	public double getPrice()
	{
		if ( getMargin() == 1 )
			return getCost();
		
		double price = getCost() / getMargin();
		
		return price < 1 ? Utils.roundDouble( price,  2 ) : Utils.roundToNext( price );
	}

	public String getPriceAsString()
	{
		return Utils.getFormattedCurrency( getPrice() );
	}

	public int getTotalQuantity()
	{
		int ret = 0;
		
		for ( QuotationLineDelivery delivery : line_deliveries )
			ret += delivery.getQuantity();
			
		return ret;
	}
	
	public double getTotalPrice()
	{
		return getPrice() * getTotalQuantity();
	}
	
	public String getTotalPriceAsString()
	{
		return Utils.getFormattedCurrency( getTotalPrice() );
	}

	public double getTotalCost()
	{
		return getReal_cost() * getTotalQuantity();
	}
	
	public String getTotalCostAsString()
	{
		return Utils.getFormattedCurrency( getTotalCost() );
	}

	public double getTotalMargin()
	{
		return getTotalPrice() - getTotalCost();
	}
	
	public String getTotalMarginAsString()
	{
		return Utils.getFormattedCurrency( getTotalMargin() );
	}

	public double getTotalTaxes()
	{
		return getTax_rate() != 0 ? getTotalPrice() * (tax_rate / 100) : 0;
	}

	public String getTotalTaxesAsString()
	{
		return Utils.getFormattedCurrency( getTotalTaxes() );
	}
	
	public String getTitleAsHtml()
	{
		return Utils.getStringAsHtml( StringEscapeUtils.escapeXml( title ) );
	}
	
	public String getDescriptionAsHtml()
	{
		return  Utils.getStringAsHtml( StringEscapeUtils.escapeXml( description == null ? "" : description ) );
	}

	public int getQuantity( QuotationDelivery quotation_delivery )
	{
		for ( QuotationLineDelivery delivery : line_deliveries )
			if ( quotation_delivery.getId().equals( delivery.getRef_quotation_delivery() ) )
				return delivery.getQuantity();
			
		return 0;
	}
	
	public String getReferenceAsHtml()
	{
		return Utils.getStringAsHtml( StringEscapeUtils.escapeXml( reference != null ? reference : "" ) );
	}
	
	public double getGrandTotalPrice()
	{
		return getTotalPrice() + getTotalTaxes();
	}
	
	public String getGrandTotalPriceAsString()
	{
		return Utils.getFormattedCurrency( getGrandTotalPrice() );
	}

	public Integer getMaxQuantity()
	{
		if ( getTotal_invoiced() == null )
			return getTotalQuantity();
		
		return getTotalQuantity() - getTotal_invoiced();
	}
	
	public String getOrigin()
	{
		return origin != null ? origin : "";
	}
}
