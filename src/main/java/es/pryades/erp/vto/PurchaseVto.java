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
public class PurchaseVto extends GenericVto
{
	private static final long serialVersionUID = 2405895611232265192L;
	
	private String number;						
	private String title;						
	private String purchase_date;						
	private String register_date;						
	
	private String invoice_number;
	private String provider_name;
	private String operation_title;

	private String net_price;
	private String net_tax;
	private String gross_price;
	private Label payed;
	private Label for_payment;
	
	private String status;
	
	public PurchaseVto()
	{
	}
}
