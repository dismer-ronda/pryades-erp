package es.pryades.erp.dto;

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
public class InvoiceLine extends BaseDto
{
	private static final long serialVersionUID = 2123720592407053614L;
	
	private Long ref_invoice;						
	private Long ref_quotation_line;
	
	private Integer quantity;
	private Integer total_packed;

	private QuotationLine quotation_line;
	
	public double getPrice()
	{
		return getQuotation_line().getPrice();
	}
	
	public String getPriceAsString()
	{
		return Utils.getFormattedCurrency( getPrice() );
	}

	public double getTotalPrice()
	{
		return getPrice() * getQuantity();
	}
	
	public String getTotalPriceAsString()
	{
		return Utils.getFormattedCurrency( getTotalPrice() );
	}
	
	public double getTotalTaxes()
	{
		return getQuotation_line().getTax_rate() != 0 ? getTotalPrice() * (getQuotation_line().getTax_rate() / 100) : 0;
	}

	public String getTotalTaxesAsString()
	{
		return Utils.getFormattedCurrency( getTotalTaxes() );
	}
	
	public String getDescriptionAsHtml()
	{
		return Utils.getStringAsHtml( StringEscapeUtils.escapeXml( getQuotation_line().getDescription() ) );
	}

	public int getQuotationLineQuantity( QuotationDelivery quotation_delivery )
	{
		for ( QuotationLineDelivery delivery : getQuotation_line().getLine_deliveries() )
			if ( quotation_delivery.getId().equals( delivery.getRef_quotation_delivery() ) )
				return delivery.getQuantity();
			
		return 0;
	}
	
	public String getReferenceAsHtml()
	{
		return getQuotation_line().getReferenceAsHtml();
	}
	
	public double getGrandTotalPrice()
	{
		return getTotalPrice() + getTotalTaxes();
	}
	
	public String getGrandTotalPriceAsString()
	{
		return Utils.getFormattedCurrency( getGrandTotalPrice() );
	}

	@Override
	public void removePrivateFields()	
	{
		super.removePrivateFields();
		
		quotation_line.removePrivateFields();
	}
}
