package es.pryades.erp.vto;

import es.pryades.erp.common.GenericVto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Dismer Ronda
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class OperationVto extends GenericVto
{
	private static final long serialVersionUID = 2581885072833928461L;
	
	private String title;		
	private String customer_name;
	private String quotation_number;
	private String status;
	private String predicted_cost;
	private String real_cost;
	private String price;
	private String profit;
    
	public OperationVto()
	{
	}
}
