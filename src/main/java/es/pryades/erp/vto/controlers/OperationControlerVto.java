package es.pryades.erp.vto.controlers;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.Utils;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.Operation;
import es.pryades.erp.dto.Quotation;
import es.pryades.erp.vto.OperationVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class OperationControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = 4773035025933841142L;

	public OperationControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		OperationVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Operation.class) )
			{
				result = new OperationVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((Operation) dtoObj).getId());

				result.setTitle(((Operation) dtoObj).getTitle());
				
				double predicted_cost = 0;
				double real_cost = 0;
				double total_invoiced = 0;
				
				Quotation quotation = ((Operation) dtoObj).getQuotation();
				if ( quotation != null )
				{
					result.setCustomer_name( quotation.getCustomer().getAlias() );
					result.setQuotation_number( quotation.getFormattedNumber() );
					
					predicted_cost = quotation.getTotalCost();
					total_invoiced = quotation.getTotalInvoiced();
				}

				result.setPredicted_cost( Utils.getFormattedCurrency( predicted_cost ) );
				result.setReal_cost( Utils.getFormattedCurrency( real_cost ) );
				result.setTotal_invoiced( Utils.getFormattedCurrency( total_invoiced ) );
				
				result.setProfit( Utils.getFormattedCurrency( total_invoiced - real_cost ) );
				
				result.setStatus( getContext().getString( "operation.status." + ((Operation) dtoObj).getStatus()) );
			}
			else
			{
			}
		}

		return result;
	}
}
