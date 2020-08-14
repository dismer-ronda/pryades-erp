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
public class InvoiceLineVto extends GenericVto
{
	private static final long serialVersionUID = 3385558827900543418L;
	
	private String title;
	private String price;
	private Integer quantity;
	private Integer total_packed;

	public InvoiceLineVto()
	{
	}
}
