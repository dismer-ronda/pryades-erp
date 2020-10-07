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

				result.setId(((Invoice) dtoObj).getId());
				
				result.setTitle(((Invoice) dtoObj).getTitle());
				result.setNumber(((Invoice) dtoObj).getFormattedNumber());
				result.setInvoice_date( CalendarUtils.getDateFromLongAsString( ((Invoice) dtoObj).getInvoice_date(), "dd-MM-yyyy" ) );
				
				result.setReference_request( ((Invoice) dtoObj).getQuotation().getReference_request() ); 
				result.setReference_order( ((Invoice) dtoObj).getQuotation().getReference_order() ); 
				result.setCustomer_name( ((Invoice) dtoObj).getQuotation().getCustomer().getName() ); 

				result.setTotal_price( Utils.roundDouble( ((Invoice) dtoObj).getTotalPrice() + ((Invoice) dtoObj).getTransport_cost(), 2 ) );
				result.setTotal_invoice( Utils.roundDouble( ((Invoice) dtoObj).getGrandTotalInvoiceAfterTaxes(), 2 ) );
				result.setTotal_taxes( Utils.roundDouble( ((Invoice) dtoObj).getTotalTaxes(), 2 ) );

				Double collected = ((Invoice) dtoObj).getCollected();
				Label labelCollected = new Label();
				labelCollected.setValue( Double.toString( Utils.roundDouble( collected,  2 ) ) );
				labelCollected.setStyleName( (collected < ((Invoice) dtoObj).getTotalPrice()) ? "red" : "green" );
				result.setCollected( labelCollected );
			}
			else
			{
			}
		}

		return result;
	}
}
