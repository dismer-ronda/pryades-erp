package es.pryades.erp.vto;

import com.vaadin.ui.Label;

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
public class TransactionVto extends GenericVto
{
	private static final long serialVersionUID = -1717005863550238687L;
	
	private String transaction_type;						
	private String transaction_date;						
	private String description;						
	private Label amount;
	private Label balance;
	private String account_name;						

	private String purchase_number;						
	private String invoice_number;						
	private String target;						
	private String transfer;						
	
	public TransactionVto()
	{
	}
}
