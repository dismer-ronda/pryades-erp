package es.pryades.erp.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class UserDefault extends BaseDto
{
	private static final long serialVersionUID = -7744392550886070713L;

	public static final String LAST_PASSWORD1 = "last.password.1";
	public static final String LAST_PASSWORD2 = "last.password.2";
	
	public static final String QUOTATION_NUMBER = "quotation.number";
	public static final String QUOTATION_CUSTOMER = "quotation.customer";
	public static final String QUOTATION_VALIDITY = "quotation.validity";
	public static final String QUOTATION_DISCOUNT = "quotation.discount";
	public static final String QUOTATION_DELIVERY = "quotation.delivery";
	public static final String QUOTATION_PACKAGING = "quotation.packing";
	public static final String QUOTATION_WARRANTY = "quotation.warranty";
	public static final String QUOTATION_PAYMENT = "quotation.payment";
	public static final String QUOTATION_TAX_RATE = "quotation.tax_rate";

	public static final String QUOTATION_DELIVERY_DEPARTURE = "quotation_delivery.departure";
	public static final String QUOTATION_DELIVERY_INCOTERMS = "quotation_delivery.incoterms";
	public static final String QUOTATION_DELIVERY_COST = "quotation_delivery.cost";

	public static final String QUOTATION_LINE_MARGIN = "quotation_line.margin";
	public static final String QUOTATION_LINE_ORDER = "quotation_line.order";
	public static final String QUOTATION_LINE_PROVIDER = "quotation_line.provider";
	public static final String QUOTATION_LINE_TAX_RATE = "quotation_line.tax_rate";
	public static final String QUOTATION_LINE_ORIGIN = "quotation_line.origin";

	public static final String INVOICE_INCOTERMS = "invoice.incoterms";
	public static final String INVOICE_DEPARTURE_PORT = "invoice.departure.port";
	public static final String INVOICE_TRANSPORT_COST = "invoice.transport.cost";

	public static final String SHIPMENT_CONSIGNEE = "shipment.consginee";
	public static final String SHIPMENT_NOTIFY = "shipment.notify";
	public static final String SHIPMENT_INCOTERMS = "shipment.incoterms";
	public static final String SHIPMENT_DEPARTURE_PORT  = "shipment.departure.port";
	public static final String SHIPMENT_ARRIVAL_PORT  = "shipment.arrival.port";
	public static final String SHIPMENT_CARRIER = "shipment.carrier";
	public static final String SHIPMENT_TRANSPORTER = "shipment.transporter";

	public static final String SHIPMENT_BOX_TYPE = "shipment.box.type";
	public static final String SHIPMENT_BOX_LENGTH = "shipment.box.length";
	public static final String SHIPMENT_BOX_WIDTH = "shipment.box.width";
	public static final String SHIPMENT_BOX_HEIGHT = "shipment.box.height";

	public static final String LABELS_PAGESIZE = "labels.pagesize";
	public static final String LABELS_FORMAT = "labels.format";
	public static final String LABELS_TYPE = "labels.type";
	public static final String LABELS_FONTSIZE = "labels.fontsize";
	public static final String LABELS_COPIES = "labels.copies";

	public static final String QUOTATIONS_FROM = "quotations.from";
	public static final String QUOTATIONS_TO = "quotations.to";
	public static final String QUOTATIONS_CUSTOMER = "quotations.customer";
	public static final String QUOTATIONS_STATUS = "quotations.status";
	public static final String QUOTATIONS_REFERENCE_REQUEST = "quotations.reference.request";
	public static final String QUOTATIONS_REFERENCE_ORDER = "quotations.reference.order";

	public static final String INVOICES_FROM = "invoices.from";
	public static final String INVOICES_TO = "invoices.to";
	public static final String INVOICES_OPERATION = "invoices.operation";
	public static final String INVOICES_CUSTOMER = "invoices.customer";
	public static final String INVOICES_REFERENCE_REQUEST = "invoices.reference.request";
	public static final String INVOICES_REFERENCE_ORDER = "invoices.reference.order";

	public static final String SHIPMENTS_FROM = "shipments.from";
	public static final String SHIPMENTS_TO = "shipments.to";
	public static final String SHIPMENTS_CUSTOMER = "shipments.customer";
	public static final String SHIPMENTS_STATUS = "shipments.status";

	public static final String OPERATIONS_CUSTOMER = "operations.customer";
	public static final String OPERATIONS_STATUS = "operations.status";
	public static final String OPERATIONS_TITLE = "operations.title";

	public static final String PURCHASES_FROM = "purchases.from";
	public static final String PURCHASES_TO = "purchases.to";
	public static final String PURCHASES_PROVIDER = "purchases.provider";
	public static final String PURCHASES_OPERATION = "purchases.operation";
	public static final String PURCHASES_BUYER = "purchases.buyer";
	public static final String PURCHASES_STATUS = "purchases.status";
	public static final String PURCHASES_TYPE = "purchases.type";

	public static final String TRANSACTIONS_FROM = "transactions.from";
	public static final String TRANSACTIONS_TO = "transactions.to";

	public static final String TRANSACTION_ACCOUNT = "transaction.account";
	public static final String TRANSACTION_DATE = "transaction.date";

	public static final String TRANSFER_FROM_DATE = "transfer.from.date";
	public static final String TRANSFER_TO_DATE = "transfer.to.date";

	private String data_key;
	private String data_value;
	
	private Long ref_user;
	
	public Long asLong()
	{
		try
		{
			return Long.valueOf( data_value );
		}
		catch ( Throwable e )
		{
			return null;
		}
	}
}
