package es.pryades.erp.vto.controlers;

import com.vaadin.ui.Label;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.Operation;
import es.pryades.erp.dto.Purchase;
import es.pryades.erp.vto.PurchaseVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class PurchaseControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = 4565286760716270727L;

	public PurchaseControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		PurchaseVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Purchase.class) )
			{
				result = new PurchaseVto();
				
				Purchase purchase = (Purchase) dtoObj;
				Operation operation = purchase.getOperation();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId( purchase.getId() );
				
				result.setNumber( purchase.getFormattedNumber());
				result.setTitle( purchase.getTitle());
				result.setPurchase_date( CalendarUtils.getDateFromLongAsString( purchase.getPurchase_date(), "dd-MM-yyyy" ) );
				result.setRegister_date( CalendarUtils.getDateFromLongAsString( purchase.getRegister_date(), "dd-MM-yyyy" ) );
				
				result.setProvider_name( purchase.getProvider().getAlias() ); 
				result.setInvoice_number( purchase.getInvoice_number()!= null ? purchase.getInvoice_number() : "" ); 
				result.setOperation_title( operation != null ? operation.getTitle() : "" ); 
				
				result.setNet_price( purchase.getNetPriceAsString() );
				result.setNet_tax( purchase.getNetTaxAsString() );
				result.setGross_price( purchase.getGrossPriceAsString() );
				
				Label payed = new Label( purchase.getPayedAsString() );
				payed.setStyleName( purchase.isFullyPayed() ? "green" : "red" );
				result.setPayed( payed );
				
				Label forPayment = new Label( purchase.getForPaymentAsString() );
				forPayment.setStyleName( purchase.isFullyPayed() ? "green" : "red" );
				result.setFor_payment( forPayment );
				
				result.setStatus( getContext().getString( "purchase.status." + purchase.getStatus() ) );
			}
			else
			{
			}
		}

		return result;
	}
}
