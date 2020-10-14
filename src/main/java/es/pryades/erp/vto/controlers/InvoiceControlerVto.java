package es.pryades.erp.vto.controlers;

import com.vaadin.ui.Label;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.Utils;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.Invoice;
import es.pryades.erp.vto.InvoiceVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class InvoiceControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = -4218564287476983781L;

	public InvoiceControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		InvoiceVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Invoice.class) )
			{
				result = new InvoiceVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );
				
				Invoice invoice = (Invoice) dtoObj; 

				result.setId( invoice.getId() );
				
				result.setTitle(invoice.getTitle());
				result.setNumber(invoice.getFormattedNumber());
				result.setInvoice_date( CalendarUtils.getDateFromLongAsString( invoice.getInvoice_date(), "dd-MM-yyyy" ) );
				
				result.setReference_request( invoice.getQuotation().getReference_request() ); 
				result.setReference_order( invoice.getQuotation().getReference_order() ); 
				result.setCustomer_name( invoice.getQuotation().getCustomer().getAlias() ); 
				result.setQuotation_number( invoice.getQuotation().getFormattedNumber() ); 

				result.setTotal_price( Utils.roundDouble( invoice.getTotalPrice() + invoice.getTransport_cost(), 2 ) );
				result.setTotal_invoice( Utils.roundDouble( invoice.getGrandTotalInvoiceAfterTaxes(), 2 ) );
				result.setTotal_taxes( Utils.roundDouble( invoice.getTotalTaxes(), 2 ) );

				Double collected = ((Invoice) dtoObj).getCollected();
				Label labelCollected = new Label();
				labelCollected.setValue( Double.toString( Utils.roundDouble( collected,  2 ) ) );
				labelCollected.setStyleName( invoice.isFullyCollected() ? "green" : "red" );
				result.setCollected( labelCollected );
			}
			else
			{
			}
		}

		return result;
	}
}
