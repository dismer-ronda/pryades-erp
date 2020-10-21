package es.pryades.erp.dto;

import org.apache.log4j.Logger;

import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class Transaction extends BaseDto
{
	private static final long serialVersionUID = -4099466512430050312L;

	static final Logger LOG = Logger.getLogger( Transaction.class );

	public static final int TYPE_INIT				= 0;
	public static final int TYPE_PAYMENT			= 1;
	public static final int TYPE_INCOME				= 2;
	public static final int TYPE_TRANSFER_SRC		= 3;
	public static final int TYPE_TRANSFER_DST		= 4;
	public static final int TYPE_LOAN				= 5;
	
	public static final int TRANSACTION_OK 						= 0;
	public static final int TRANSACTION_ERROR_NOT_INITIALIZED	= 1;
	public static final int TRANSACTION_ERROR_CREDIT_LIMIT		= 2;
	public static final int TRANSACTION_ERROR_DATE_BEFORE		= 3;
	public static final int TRANSACTION_ERROR_EXCEPTION			= 4;
		
	private Long transaction_date;
	private Integer transaction_type;

	private Double amount;
	private Double balance;
  	
	private String description;

	private Long ref_purchase;
	private Long ref_invoice;
	private Long ref_account;

	private Long ref_target;			// Referencia a cuenta origen/destino de transacción
	private Long transfer;				// Para identificar las dos transacciones de una transferencia

	private Purchase purchase;
	private Invoice invoice;
	private Account account;
	private Account target;
	
	public String getFormattedDate()
	{
		return CalendarUtils.getDateFromLongAsString( getTransaction_date(), "dd-MM-yyyy" );
	}
	
	public String getAmountAsString()
	{
		return Utils.getFormattedCurrency( amount );
	}

	public String getBalanceAsString()
	{
		return Utils.getFormattedCurrency( balance );
	}
	
	public String getTransactedItem()
	{
		if ( transaction_type.equals( TYPE_PAYMENT ) && purchase != null )
			return purchase.getFormattedNumber();

		if ( transaction_type.equals( TYPE_INCOME ) && invoice != null )
			return invoice.getFormattedNumber();

		if ( transaction_type.equals( TYPE_TRANSFER_DST ) && transfer != null )
			return transfer.toString().toUpperCase();
		
		if ( transaction_type.equals( TYPE_TRANSFER_SRC ) && transfer != null )
			return transfer.toString().toUpperCase();
		
		return "";
	}
	
	public String getTransactionMessage( String message )
	{
		if ( transaction_type.equals( TYPE_INIT ) && account != null )
			return message.replaceAll( "%amount%", getAmountAsString() )
					.replaceAll( "%account%", account.getName() );
		
		if ( transaction_type.equals( TYPE_PAYMENT ) && purchase != null )
			return message.replaceAll( "%amount%", getAmountAsString() )
					.replaceAll( "%invoice%", purchase.getFormattedNumber() );

		if ( transaction_type.equals( TYPE_INCOME ) && invoice != null )
			return message.replaceAll( "%amount%", getAmountAsString() )
					.replaceAll( "%invoice%", invoice.getFormattedNumber() );

		if ( transaction_type.equals( TYPE_TRANSFER_DST ) && transfer != null )
			return message.replaceAll( "%amount%", getAmountAsString() )
					.replaceAll( "%transfer%", transfer.toString().toUpperCase() );
		
		if ( transaction_type.equals( TYPE_TRANSFER_SRC ) && transfer != null )
			return message.replaceAll( "%amount%", getAmountAsString() )
					.replaceAll( "%transfer%", transfer.toString().toUpperCase() );
		
		return "";
	}
}
