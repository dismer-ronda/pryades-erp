package es.pryades.erp.configuration.modals;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import es.pryades.erp.application.SelectPaymentDlg;
import es.pryades.erp.application.SendEmailDlg;
import es.pryades.erp.application.ShowExternalUrlDlg;
import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Attachment;
import es.pryades.erp.common.Authorization;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dashboard.Dashboard;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Invoice;
import es.pryades.erp.dto.InvoiceLine;
import es.pryades.erp.dto.Operation;
import es.pryades.erp.dto.Quotation;
import es.pryades.erp.dto.QuotationLine;
import es.pryades.erp.dto.Shipment;
import es.pryades.erp.dto.Transaction;
import es.pryades.erp.dto.query.InvoiceQuery;
import es.pryades.erp.dto.query.OperationQuery;
import es.pryades.erp.dto.query.QuotationQuery;
import es.pryades.erp.dto.query.ShipmentQuery;
import es.pryades.erp.ioc.IOCManager;
import lombok.Getter;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewInvoice extends ModalWindowsCRUD implements ModalParent
{
	private static final long serialVersionUID = -7865229183075485556L;

	private static final Logger LOG = Logger.getLogger( ModalNewInvoice.class );

	private List<Quotation> quotations;
	private List<Shipment> shipments;
	
	@Getter
	protected Invoice newInvoice;

	private TextField editTitle;
	private PopupDateField fromDateField;
	private ComboBox comboQuotations;
	private ComboBox comboShipments;
	private TextField editTransport_cost;
	private CheckBox checkFree_delivery;
	private TextArea editPayment_terms;
	private Label labelCollected;

	private List<TextField> editsLines;
	private List<CheckBox> checksLines;
	
	private Panel panelLines;
	
	/**
	 * editReference_order
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewInvoice( AppContext context, OperationCRUD modalOperation, Invoice orgParameter, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgParameter );

		setWidth( "60%" );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		loadUserDefaults();
		
		try
		{
			newInvoice = (Invoice) Utils.clone( (Invoice) orgDto );
		}
		catch ( Throwable e1 )
		{
			newInvoice = new Invoice();
			newInvoice.setInvoice_date( CalendarUtils.getTodayAsLong() );

			newInvoice.setTransport_cost( 0.0 );
			newInvoice.setFree_delivery( Boolean.FALSE );
			newInvoice.setCollected( 0.0 );
		}

		layout.setHeight( "-1px" );
		
		bi = new BeanItem<BaseDto>( newInvoice );

		fromDateField = new PopupDateField(getContext().getString( "modalNewInvoice.popupDate" ));
		fromDateField.setResolution( Resolution.DAY );
		fromDateField.setDateFormat( "dd-MM-yyyy" );
		fromDateField.setWidth( "100%" );
		fromDateField.setValue( CalendarUtils.getDateFromString( Long.toString( newInvoice.getInvoice_date() ), "yyyyMMddHHmmss" ) );
		
		editTitle = new TextField( getContext().getString( "modalNewInvoice.editTitle" ), bi.getItemProperty( "title" ) );
		editTitle.setWidth( "100%" );
		editTitle.setNullRepresentation( "" );
		
		if ( getOperation().equals( OperationCRUD.OP_ADD ) )
		{
			loadQuotations();
			
			comboQuotations = new ComboBox(getContext().getString( "modalNewInvoice.comboQuotation" ));
			comboQuotations.setWidth( "100%" );
			comboQuotations.setNullSelectionAllowed( false );
			comboQuotations.setTextInputAllowed( true );
			comboQuotations.setImmediate( true );
			comboQuotations.setPropertyDataSource( bi.getItemProperty( "ref_quotation" ) );
			fillComboQuotations();
			comboQuotations.addValueChangeListener( new Property.ValueChangeListener() 
			{
				private static final long serialVersionUID = -3667459101751070494L;

				public void valueChange(ValueChangeEvent event) 
			    {
			        onSelectedQuotation();
			    }
			});
		}

		loadShipments();
		if ( shipments.size() > 0 )
		{
			comboShipments = new ComboBox(getContext().getString( "modalNewInvoice.comboShipment" ));
			comboShipments.setWidth( "100%" );
			comboShipments.setNullSelectionAllowed( true );
			comboShipments.setTextInputAllowed( true );
			comboShipments.setImmediate( true );
			comboShipments.setPropertyDataSource( bi.getItemProperty( "ref_shipment" ) );
			fillComboShipments();
		}

		editTransport_cost = new TextField( getContext().getString( "modalNewInvoice.editTransport_cost" ), bi.getItemProperty( "transport_cost" ) );
		editTransport_cost.setWidth( "100%" );
		editTransport_cost.setNullRepresentation( "" );
		
		checkFree_delivery = new CheckBox( getContext().getString( "modalNewInvoice.checkFree_delivery" ) );
		checkFree_delivery.setWidth( "100%" );
		checkFree_delivery.setValue( newInvoice.getFree_delivery() );

		editPayment_terms = new TextArea( getContext().getString( "modalNewInvoice.editPayment_terms" ), bi.getItemProperty( "payment_terms" ) );
		editPayment_terms.setWidth( "100%" );
		editPayment_terms.setNullRepresentation( "" );
		editPayment_terms.setRows( 3 );
		
		/*editMonth = new TextField( getContext().getString( "modalNewInvoice.editMonth" ), bi.getItemProperty( "month" ) );
		editMonth.setWidth( "100%" );
		editMonth.setNullRepresentation( "" );
		editMonth.setRequired( true );
		editMonth.setRequiredError( getContext().getString( "words.required" ) );
		editMonth.setInvalidCommitted( true );*/

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		if ( getOperation().equals( OperationCRUD.OP_ADD ) )
			row1.addComponent( comboQuotations );
		row1.addComponent( fromDateField );
		//row1.addComponent( editMonth );
		row1.addComponent( editTitle );
		//row1.setExpandRatio( editTitle, 1.0f );
		
		HorizontalLayout row2 = new HorizontalLayout();
		//row2.setWidth( "100%" );
		row2.setSpacing( true );
		if ( shipments.size() > 0)
			row2.addComponent( comboShipments );
		row2.addComponent( editTransport_cost );
		row2.addComponent( checkFree_delivery );
		row2.setComponentAlignment( checkFree_delivery, Alignment.BOTTOM_LEFT );

		HorizontalLayout row4 = new HorizontalLayout();
		row4.setWidth( "100%" );
		row4.setSpacing( true );
		row4.addComponent( editPayment_terms );
		if ( !getOperation().equals( OperationCRUD.OP_ADD ) )
		{
			labelCollected = new Label();
			labelCollected.setWidth( "-1px" );
			labelCollected.setValue( getContext().getString( "modalNewInvoice.editCollected" ) + " " + newInvoice.getCollectedAsString() );
			labelCollected.addStyleName( (newInvoice.isFullyCollected() ? "green" : "red") + " centered" );
			
			row4.addComponent( labelCollected );
			row4.setComponentAlignment( labelCollected, Alignment.MIDDLE_CENTER );
		}
		row4.setExpandRatio( editPayment_terms, 1.0f );

		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		componentsContainer.addComponent( row4 );
		
		if ( !getOperation().equals( OperationCRUD.OP_ADD ) )
		{
			showQuotationLinesGrid();
			componentsContainer.addComponent( panelLines );
			
			Button bttnPdf = new Button();
			bttnPdf.setCaption( getContext().getString( "modalNewQuotation.pdf" ) );
			bttnPdf.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 6740035234840398025L;

				public void buttonClick( ClickEvent event )
				{
					onShowPdf();
				}
			} );

			getDefaultOperationsRow().addComponentAsFirst( bttnPdf );
			getDefaultOperationsRow().setComponentAlignment( bttnPdf, Alignment.MIDDLE_LEFT );

			Button btnEmail = new Button();
			btnEmail.setCaption( getContext().getString( "modalNewQuotation.email" ) );
			btnEmail.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = -5689451836532157264L;

				public void buttonClick( ClickEvent event )
				{
					onEmailQuotation();
				}
			} );

			getDefaultOperationsRow().addComponentAsFirst( btnEmail );
			getDefaultOperationsRow().setComponentAlignment( btnEmail, Alignment.MIDDLE_LEFT );
			
			Button btnCollect = new Button();
			btnCollect.setCaption( getContext().getString( "modalNewInvoice.btnCollect" ) );
			btnCollect.setEnabled( !newInvoice.isFullyCollected() );
			btnCollect.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 6029965102566653456L;

				public void buttonClick( ClickEvent event )
				{
					onCollect();
				}
			} );

			getDefaultOperationsRow().addComponentAsFirst( btnCollect );
			getDefaultOperationsRow().setComponentAlignment( btnCollect, Alignment.MIDDLE_LEFT );
		}
	}

	private void showQuotationLinesGrid()
	{
		panelLines = new Panel();
		panelLines.setStyleName( "borderless light" );
		panelLines.setHeight( "400px" );

		GridLayout grid = new GridLayout();
		grid.setColumns( newInvoice.getRef_shipment() != null ? 5 : 4 );
		grid.setSpacing( true );

		editsLines = new ArrayList<TextField>();
		checksLines = new ArrayList<CheckBox>();
		
		Label temp = new Label( getContext().getString( "modalNewInvoice.item" ) );
		temp.setWidth( "40px" );
		grid.addComponent( temp );
		
		temp = new Label( getContext().getString( "modalNewInvoice.title" ) );
		grid.addComponent( temp );
		
		temp = new Label( getContext().getString( "modalNewInvoice.quantity" ) );
		temp.setWidth( "96px" );
		grid.addComponent( temp );
		
		temp = new Label( getContext().getString( "modalNewInvoice.add" ) );
		temp.setWidth( "96px" );
		grid.addComponent( temp );

		if ( newInvoice.getRef_shipment() != null )
			grid.addComponent( new Label( getContext().getString( "modalNewInvoice.packed" ) ) );


		int i = 1;
		
		for ( QuotationLine line: newInvoice.getQuotation().getLines() )
		{
			Integer count = newInvoice.getLineQuantity( line );
			Integer packed = newInvoice.getLinePacked( line );
			
			TextField editLine = new TextField();
			editLine.setNullRepresentation( "" );
			editLine.setData( line );
			editLine.setWidth( "96px" );
			
			if ( packed != 0 && shipments.size() > 0 )
				comboShipments.setReadOnly( true );

			// Item number
			Label labelItem = new Label( Integer.toString( i++ ) );
			grid.addComponent( labelItem );
			grid.setComponentAlignment( labelItem, Alignment.MIDDLE_CENTER );
			
			// Item title
			grid.addComponent( new Label( line.getTitle() ) );

			// Iten input
			editLine.setValue( count != 0 ? count.toString() : line.getMaxQuantity().toString() );
			editLine.addStyleName( (count != 0  || line.getMaxQuantity() == 0) ? "" : "invoice_line" );
			grid.addComponent( editLine );
			editsLines.add( editLine );
			
			// Check to add
			CheckBox checkLine = new CheckBox();
			checkLine.setData( line );
			checkLine.setValue( count != 0 );
			checkLine.setReadOnly( count == 0 && line.getMaxQuantity() == 0 );
			grid.addComponent( checkLine );
			grid.setComponentAlignment( checkLine, Alignment.MIDDLE_CENTER );
			checksLines.add( checkLine );
			
			// Number of packed items
			if ( newInvoice.getRef_shipment() != null )
			{
				Label labelPacked = new Label( packed.toString() ); 
				grid.addComponent( labelPacked );
				grid.setComponentAlignment( labelPacked, Alignment.MIDDLE_CENTER );
			}
		}

		panelLines.setContent( grid );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewInvoice";
	}

	@Override
	protected void defaultFocus()
	{
		fromDateField.focus();
	}

	@Override
	protected boolean onAdd()
	{
		Operation operation = getSelectedOperation();
		
		if ( operation == null )
		{
			ConfirmDialog.show( (UI)getContext().getData( "Application" ), getContext().getString( "modalNewQuotationLine.operationNotFound" ),
		        new ConfirmDialog.Listener() 
				{
					private static final long serialVersionUID = 8965420096699876003L;

					public void onClose(ConfirmDialog dialog) 
		            {
		                if ( dialog.isConfirmed() ) 
							createOperation();
		            }
		        });
			
			return false;
		}
		else
		{
			if ( !checkMaxQuantities() )
				return false;
			
			if ( !checkMinQuantities() )
				return false;
			
			if ( !checkMaxTransportCost() )
				return false;
	
			try
			{
				newInvoice.setId( null );
				newInvoice.setInvoice_date( CalendarUtils.getDayAsLong( fromDateField.getValue() ) );
				newInvoice.setFree_delivery( checkFree_delivery.getValue().booleanValue() ? Boolean.TRUE : Boolean.FALSE );
				
				IOCManager._InvoicesManager.setRow( getContext(), null, newInvoice );
				
				saveUserDefaults();
	
				Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
				dashboard.refreshShipmentsTab();
				dashboard.refreshOperationsTab();
	
				return true;
			}
			catch ( Throwable e )
			{
				e.printStackTrace();
				showErrorMessage( e );
			}
	
			return false;
		}
	}
	
	private boolean checkMaxQuantities()
	{
		if ( editsLines != null )
		{
			for ( TextField edit : editsLines )
			{
				QuotationLine quotationLine = (QuotationLine)edit.getData();
				
				if ( selectedToAdd( quotationLine ) )
				{
					Integer current = newInvoice.getLineQuantity( quotationLine );
					if ( current == null )
						current = 0;
					
					if ( Utils.getInt( edit.getValue(), 0 ) > current + quotationLine.getMaxQuantity() )
					{
						String error = getContext().getString( "modalNewInvoice.quantityExceeded" ).
								replaceAll( "%title%", quotationLine.getTitle() ).
								replaceAll( "%max_quantity%", Integer.toString( current + quotationLine.getMaxQuantity() ) );
						Utils.showNotification( getContext(), error, Notification.Type.ERROR_MESSAGE );
						
						return false;
					}
				}
			}
			}
		
		return true;
	}

	private boolean checkMinQuantities()
	{
		if ( editsLines != null )
		{
			for ( TextField edit : editsLines )
			{
				QuotationLine quotationLine = (QuotationLine)edit.getData();
	
				if ( selectedToAdd( quotationLine ) )
				{
					Integer quantity = Utils.getInt( edit.getValue(), 0 );
					Integer packed = newInvoice.getLinePacked( quotationLine );
					
					if ( quantity < packed )
					{
						String error = getContext().getString( "modalNewInvoice.packedExceeded" ).
								replaceAll( "%title%", quotationLine.getTitle() ).
								replaceAll( "%total_packed%", Integer.toString( packed ) );
						Utils.showNotification( getContext(), error, Notification.Type.ERROR_MESSAGE );
						
						return false;
					}
				}
			}
		}
		
		return true;
	}

	private boolean checkMaxTransportCost()
	{
		if ( checkFree_delivery.getValue().booleanValue() )
			return true;
		
		double orgInvoiced = orgDto != null ? ( ((Invoice)orgDto).getFree_delivery() ? 0 : ((Invoice)orgDto).getTransport_cost()) : 0; 
		Double total_invoiced = newInvoice.getQuotation().getTransportInvoiced() - orgInvoiced;
		
		Double max = newInvoice.getQuotation().getTotalTransportCost() - total_invoiced;

		if ( Utils.getDouble( editTransport_cost.getValue(), 0 ) > max )
		{
			String error = getContext().getString( "modalNewInvoice.transportCostExceeded" ).
			replaceAll( "%max_transport_cost%", Double.toString( max) );
			Utils.showNotification( getContext(), error, Notification.Type.ERROR_MESSAGE );
			
			return false;
		}
			
		return true;
	}
	
	@Override
	protected boolean onModify()
	{
		if ( !checkMaxQuantities() )
			return false;
		
		if ( !checkMinQuantities() )
			return false;
		
		if ( !checkMaxTransportCost() )
			return false;
		
		try
		{
			newInvoice.setInvoice_date( CalendarUtils.getDayAsLong( fromDateField.getValue() ) );
			newInvoice.setFree_delivery( checkFree_delivery.getValue().booleanValue() ? Boolean.TRUE : Boolean.FALSE );

			IOCManager._InvoicesManager.setRow( getContext(), (Invoice) orgDto, newInvoice );

			saveUserDefaults();

			for ( TextField edit : editsLines )
			{
				QuotationLine quotationLine = (QuotationLine)edit.getData();
				
				boolean add = selectedToAdd( quotationLine );
				
				InvoiceLine invoiceLine = newInvoice.getInvoiceLine( quotationLine );
				
				int quantity = Integer.valueOf( edit.getValue() );
				
				if ( invoiceLine == null && quantity != 0 )
				{
					if ( add )
					{
						InvoiceLine line = new InvoiceLine();
	
						line.setRef_invoice( newInvoice.getId() );
						line.setRef_quotation_line( quotationLine.getId() );
						line.setQuantity( quantity );
	
						IOCManager._InvoicesLinesManager.setRow( getContext(), null, line );
					}
				}
				else if ( invoiceLine != null )
				{
					if ( !invoiceLine.getQuantity().equals( quantity ) )
					{
						if ( quantity > 0 )
						{
							if ( add )
							{
								InvoiceLine clone = (InvoiceLine)Utils.clone( invoiceLine );
								clone.setQuantity( quantity );
								
								IOCManager._InvoicesLinesManager.setRow( getContext(), invoiceLine, clone );
							}
							else
							{
								InvoiceLine line = new InvoiceLine();
	
								line.setRef_invoice( newInvoice.getId() );
								line.setRef_quotation_line( quotationLine.getId() );
	
								IOCManager._InvoicesLinesManager.delRow( getContext(), line );
							}
						}
						else
						{
							InvoiceLine line = new InvoiceLine();
	
							line.setRef_invoice( newInvoice.getId() );
							line.setRef_quotation_line( quotationLine.getId() );
	
							IOCManager._InvoicesLinesManager.delRow( getContext(), line );
						}
					}
					else
					{
						if ( !add )
						{
							InvoiceLine line = new InvoiceLine();
	
							line.setRef_invoice( newInvoice.getId() );
							line.setRef_quotation_line( quotationLine.getId() );
	
							IOCManager._InvoicesLinesManager.delRow( getContext(), line );
						}
					}
				}
				
				Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
				dashboard.refreshShipmentsTab();
				dashboard.refreshOperationsTab();
			}

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onDelete()
	{
		try
		{
			IOCManager._InvoicesManager.delRow( getContext(), newInvoice );

			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshShipmentsTab();
			dashboard.refreshOperationsTab();

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	public void refreshVisibleContent( boolean repage )
	{
	}
	
	@Override
	protected boolean editAfterNew()
	{
		return true;
	}

	@Override
	protected void doEditAfterNew()
	{
		new ModalNewInvoice( getContext(), OperationCRUD.OP_MODIFY, (Invoice)newInvoice, getModalParent() ).showModalWindow();
	}

	private void loadUserDefaults()
	{
	}

	private void saveUserDefaults()
	{
	}

	@SuppressWarnings("unchecked")
	private void loadQuotations()
	{
		try
		{
			QuotationQuery query = new QuotationQuery();
			query.setStatus( Quotation.STATUS_APPROVED );
			
			quotations = IOCManager._QuotationsManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			quotations = new ArrayList<Quotation>();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadShipments()
	{
		try
		{
			ShipmentQuery query = new ShipmentQuery();
			shipments = IOCManager._ShipmentsManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			shipments = new ArrayList<Shipment>();
		}
	}

	private void fillComboQuotations()
	{
		for ( Quotation quotation : quotations )
		{
			if ( !quotation.getReference_order().isEmpty() && quotation.pendingLinesForInvoice() )
			{
				comboQuotations.addItem( quotation.getId() );
				comboQuotations.setItemCaption( quotation.getId(), quotation.getReference_order() + " - " + quotation.getTitle() );
			}
		}
	}
	
	private void fillComboShipments()
	{
		for ( Shipment shipment : shipments )
		{
			comboShipments.addItem( shipment.getId() );
			comboShipments.setItemCaption( shipment.getId(), shipment.getFormattedNumber() + " - " + shipment.getTitle() );
		}
	}
	
	private void onSelectedQuotation()
	{
		try
		{
			Quotation quotation = getSelectedQuotation();
			
			if ( quotation != null )
			{
				newInvoice.setTransport_cost( quotation.getPendingTansportCost() );
				newInvoice.setPayment_terms( quotation.getPayment_terms() );
				newInvoice.setQuotation( quotation );
				
				editTransport_cost.markAsDirty();
				editPayment_terms.markAsDirty();
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}

	private boolean selectedToAdd( QuotationLine quotationLine )
	{
		for ( CheckBox check : checksLines )
		{
			if ( quotationLine.getId().equals( ((QuotationLine)check.getData()).getId() ) )
				return check.getValue();
					
		}
		
		return false;
	}
	
	@Override
	protected boolean hasDelete()
	{
		return getContext().hasRight( "configuration.invoices.delete" );
	}

	@Override
	public boolean checkAddRight()
	{
		return getContext().hasRight( "configuration.invoices.add" );
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.invoices.modify" );
	}

	public void onShowPdf()
	{
		if ( onModify() )
		{
			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshInvoicesTab();
			dashboard.refreshQuotationsTab();
			dashboard.refreshShipmentsTab();
			
			try
			{
				InvoiceQuery query = new InvoiceQuery();
				query.setId( newInvoice.getId() );
				Invoice invoice = (Invoice)IOCManager._InvoicesManager.getRow( getContext(), query );
				
				long ts = CalendarUtils.getTodayAsLong( "UTC" );
				
				String pagesize = "A4";
				String template = invoice.getQuotation().getCustomer().getTaxable().booleanValue() ? "national-invoice-template" : "international-invoice-template";
				String timeout = "0";
				
				String extra = "ts=" + ts + 
						"&id=" + invoice.getId() + 
						"&name=" + invoice.getFormattedNumber() + "-" + invoice.getQuotation().getTitle() + 
						"&pagesize=" + pagesize + 
						"&template=" + template +
						"&url=" + getContext().getData( "Url" ) +
						"&timeout=" + timeout;
				
				String user = getContext().getUser().getLogin();
				String password = getContext().getUser().getPwd();
				
				String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
				String code = "code=" + Authorization.encrypt( extra, password ) ;
	
				String url = getContext().getData( "Url" ) + "/services/invoice" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
				
				String caption = getContext().getString( "template.invoice.invoice" ) + " " + invoice.getFormattedNumber() ;
	
				ShowExternalUrlDlg dlg = new ShowExternalUrlDlg(); 
		
				dlg.setContext( getContext() );
				dlg.setUrl( url );
				dlg.setCaption( caption );
				dlg.createComponents();
				
				getUI().addWindow( dlg );

				closeModalWindow( true, true );
			}
	
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}
		}
	}

	public void onEmailQuotation()
	{
		if ( onModify() )
		{
			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshInvoicesTab();
			dashboard.refreshQuotationsTab();
			dashboard.refreshShipmentsTab();
			
			try
			{
				InvoiceQuery queryInvoice = new InvoiceQuery();
				queryInvoice.setId( newInvoice.getId() );
				Invoice invoice = (Invoice)IOCManager._InvoicesManager.getRow( getContext(), queryInvoice );

				byte[] bytes = IOCManager._InvoicesManager.generatePdf( getContext(), invoice );
				
				AppContext ctx1 = new AppContext( invoice.getQuotation().getCustomer().getLanguage() );
				IOCManager._ParametersManager.loadParameters( ctx1 );
				ctx1.setUser( getContext().getUser() );
				ctx1.addData( "Url", getContext().getData( "Url" ) );
		    	ctx1.loadOwnerCompany();

				String name = newInvoice.getFormattedNumber() + "-" + newInvoice.getQuotation().getTitle() + ".pdf";
				
				List<Attachment> attachments = new ArrayList<Attachment>();
				attachments.add( new Attachment( name, "application/pdf", bytes ) );
				
				String subject = ctx1.getString( "modalNewInvoice.emailSubject" ).replaceAll( "%name%", name );  
				String text = ctx1.getString( "modalNewInvoice.emailText" ).
						replaceAll( "%contact_person%", invoice.getQuotation().getContact().getName() ).
						replaceAll( "%reference_order%",invoice.getQuotation().getReference_order() );  
	
				String body = text + "\n\n" + ctx1.getCompanyDataAndLegal( invoice.getQuotation().getUser() ); 
				
				final SendEmailDlg dlg = new SendEmailDlg( getContext(), getContext().getString( "modalNewInvoice.emailTitle" ), attachments, invoice.getQuotation().getCustomer().getContacts() );
				dlg.setTo( invoice.getQuotation().getContact().getEmail() );
				dlg.setCopy( "" );
				dlg.setReply_to( invoice.getQuotation().getUser().getEmail() );
				dlg.setSubject( subject );
				dlg.setBody( body );
				dlg.addCloseListener
				( 
					new Window.CloseListener() 
					{
						private static final long serialVersionUID = -5280799582624434015L;
	
						@Override
					    public void windowClose( CloseEvent e ) 
					    {
							if ( dlg.isSuccess() )
							{
							}
					    }
					}
				);
				getUI().addWindow( dlg );

				closeModalWindow( true, true );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
	
				Utils.showNotification( getContext(), getContext().getString( "modalNewQuotation.emailError" ), Notification.Type.ERROR_MESSAGE );
			}
		}
	}

	public void onCollect()
	{
		if ( onModify() )
		{
			try
			{
				final Transaction transaction = new Transaction();
				transaction.setTransaction_type( Transaction.TYPE_INCOME );
				transaction.setRef_invoice( newInvoice.getId() );
				transaction.setInvoice( newInvoice );
				transaction.setAmount( newInvoice.getGrandTotalInvoiceAfterTaxes() - newInvoice.getCollected() );
				transaction.setDescription( newInvoice.getTitle() );

				final SelectPaymentDlg dlg = new SelectPaymentDlg( getContext(), getContext().getString( "modalNewInvoice.collectTitle" ), transaction );
				dlg.addCloseListener
				( 
					new Window.CloseListener() 
					{
						private static final long serialVersionUID = 5423140716473782690L;

						@Override
					    public void windowClose( CloseEvent e ) 
					    {
							if ( dlg.isSuccess() )
							{
								newInvoice.setCollected( newInvoice.getCollected() + transaction.getAmount() );
								
								onModify();
								
								Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
								dashboard.refreshTransactionsTab();

								closeModalWindow( true, true );
							}
					    }
					}
				);
				getUI().addWindow( dlg );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
	
				Utils.showNotification( getContext(), getContext().getString( "modalNewInvoice.payError" ), Notification.Type.ERROR_MESSAGE );
			}
		}
	}

	private Operation getSelectedOperation()
	{
		try
		{
			OperationQuery query = new OperationQuery();
			query.setRef_quotation( (Long)comboQuotations.getValue() );
			
			return (Operation)IOCManager._OperationsManager.getRow( getContext(), query );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return null;
	}
	
	private Quotation getSelectedQuotation()
	{
		try
		{
			QuotationQuery queryQuotation = new QuotationQuery();
			queryQuotation.setId( (Long)comboQuotations.getValue() );
	
			return (Quotation)IOCManager._QuotationsManager.getRow( getContext(), queryQuotation );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return null;
	}	
	
	private void createOperation()
	{
		try
		{
			Operation operation = new Operation();

			Quotation quotation = getSelectedQuotation();
			
			if ( quotation != null )
			{
				operation.setRef_quotation( quotation.getId() );
				operation.setTitle( quotation.getTitle() );
				operation.setStatus( Operation.STATUS_EXCECUTION );
	
				IOCManager._OperationsManager.setRow( getContext(), null, operation );
	
				Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
				dashboard.refreshOperationsTab();
				dashboard.refreshOperations();
			}
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );

			Utils.logException( e, LOG );
		}
	}
}
