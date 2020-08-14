package es.pryades.erp.vto.controlers;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.InvoiceLine;
import es.pryades.erp.vto.InvoiceLineVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class InvoiceLineControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = 6923185505448286496L;

	public InvoiceLineControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		InvoiceLineVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(InvoiceLine.class) )
			{
				result = new InvoiceLineVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				InvoiceLine line = ((InvoiceLine) dtoObj);

				result.setId( line.getId() );
				
				result.setTitle( line.getQuotation_line().getTitle() ); 
				result.setPrice( line.getPriceAsString() ); 
				result.setQuantity( line.getQuantity() ); 
				result.setTotal_packed( line.getTotal_packed() );
			}
			else
			{
			}
		}

		return result;
	}
}
