package es.pryades.erp.dto;

import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.XmlUtils;
import es.pryades.erp.dto.query.InvoiceQuery;
import es.pryades.erp.ioc.IOCManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class ShipmentBoxLine extends BaseDto
{
	private static final long serialVersionUID = 3707717219631526052L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ShipmentBoxLine.class );
	
	private Long ref_box;						
	private Long ref_invoice_line;						

	private Integer quantity; 
	
	private Double net_weight; 
	private Double gross_weight;

	private InvoiceLine invoice_line;

	public Invoice getInvoice( AppContext ctx )
	{
		try
		{
			InvoiceQuery query = new InvoiceQuery();
			query.setId( invoice_line.getRef_invoice() );
			
			return (Invoice)IOCManager._InvoicesManager.getRow( ctx, query );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getLineContents( AppContext ctx )
	{
		Invoice invoice = getInvoice( ctx );
		
		String invoiceItem = ctx.getString( "template.shipment.packing.item" ) + " " 
				+ invoice.getItemIndex( invoice_line ) + " " 
				+ ctx.getString( "template.shipment.packing.invoice" ) + " "
				+ invoice.getFormattedNumber();
		
		String item = "<div>" 
				+ "<div class=\"padding_left padding_top text_regular_size text_left\">" + invoice_line.getQuotation_line().getTitle() + "</div>" 
				+ "<div class=\"padding_left padding_bottom text_regular_size text_left text_bold\">" + "(" + invoiceItem + ")" + "</div>"
				+ "</div>";
		
		String cols = 
				XmlUtils.getTableCol( "", 	"full_height text-regular-size text_left borde", item ) +
				XmlUtils.getTableCol( "48px", 	"text-regular-size text_center borde", getNet_weight().toString() ) +
				XmlUtils.getTableCol( "48px", 	"text-regular-size text_center borde", getGross_weight().toString() ) +
				XmlUtils.getTableCol( "44px", 	"text-regular-size text_center borde", getQuantity().toString() );
		
		return cols;
	}
}
