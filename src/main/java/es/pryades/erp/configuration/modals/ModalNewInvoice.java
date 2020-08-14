package es.pryades.erp.configuration.modals;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

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
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Invoice;
import es.pryades.erp.dto.InvoiceLine;
import es.pryades.erp.dto.Quotation;
import es.pryades.erp.dto.QuotationLine;
import es.pryades.erp.dto.Shipment;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.dto.query.QuotationQuery;
import es.pryades.erp.dto.query.ShipmentQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.reports.PdfExportInvoice;
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
	public ModalNewInvoice( AppContext context, Operation modalOperation, Invoice orgParameter, ModalParent parentWindow )
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
		
		if ( getOperation().equals( Operation.OP_ADD ) )
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
		comboShipments = new ComboBox(getContext().getString( "modalNewInvoice.comboShipment" ));
		comboShipments.setWidth( "100%" );
		comboShipments.setNullSelectionAllowed( true );
		comboShipments.setTextInputAllowed( true );
		comboShipments.setImmediate( true );
		comboShipments.setPropertyDataSource( bi.getItemProperty( "ref_shipment" ) );
		fillComboShipments();

		editTransport_cost = new TextField( getContext().getString( "modalNewInvoice.editTransport_cost" ), bi.getItemProperty( "transport_cost" ) );
		editTransport_cost.setWidth( "100%" );
		editTransport_cost.setNullRepresentation( "" );
		
		checkFree_delivery = new CheckBox( getContext().getString( "modalNewInvoice.checkFree_delivery" ) );
		checkFree_delivery.setWidth( "100%" );
		checkFree_delivery.setValue( newInvoice.getFree_delivery() );

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		if ( getOperation().equals( Operation.OP_ADD ) )
			row1.addComponent( comboQuotations );

		row1.addComponent( fromDateField );
		row1.addComponent( editTitle );
		
		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( comboShipments );
		row2.addComponent( editTransport_cost );
		row2.addComponent( checkFree_delivery );
		row2.setComponentAlignment( checkFree_delivery, Alignment.BOTTOM_LEFT );

		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		
		if ( !getOperation().equals( Operation.OP_ADD ) )
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
			TextField editLine = new TextField();
			editLine.setNullRepresentation( "" );
			editLine.setData( line );
			editLine.setWidth( "96px" );
			
			Integer count = newInvoice.getLineQuantity( line );
			Integer packed = newInvoice.getLinePacked( line );
			
			if ( packed != 0 )
				comboShipments.setReadOnly( true );

			// Item number
			Label labelItem = new Label( Integer.toString( i++ ) );
			grid.addComponent( labelItem );
			grid.setComponentAlignment( labelItem, Alignment.MIDDLE_CENTER );
			
			// Item title
			grid.addComponent( new Label( line.getTitle() ) );

			// Iten input
			editLine.setValue( count != 0 ? count.toString() : line.getMaxQuantity().toString() );
			editLine.addStyleName( count != 0 ? "" : "invoice_line" );
			grid.addComponent( editLine );
			editsLines.add( editLine );
			
			// Check to add
			CheckBox checkLine = new CheckBox();
			checkLine.setData( line );
			checkLine.setValue( count != 0 );
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
		if ( !checkMaxQuantities() )
			return false;
		
		if ( !checkMinQuantities() )
			return false;
		
		if ( !checkMaxTransportCost() )
			return false;

		try
		{
			newInvoice.setId( null );
			newInvoice.setInvoice_date( CalendarUtils.getDateAsLong( fromDateField.getValue() ) );
			newInvoice.setFree_delivery( checkFree_delivery.getValue().booleanValue() ? Boolean.TRUE : Boolean.FALSE );
			
			IOCManager._InvoicesManager.setRow( getContext(), null, newInvoice );
			
			saveUserDefaults();

			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshShipmentsTab();

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
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
			newInvoice.setInvoice_date( CalendarUtils.getDateAsLong( fromDateField.getValue() ) );
			newInvoice.setFree_delivery( checkFree_delivery.getValue().booleanValue() ? Boolean.TRUE : Boolean.FALSE );

			IOCManager._InvoicesManager.setRow( getContext(), (Invoice) orgDto, newInvoice );

			saveUserDefaults();

			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshShipmentsTab();
			
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
		new ModalNewInvoice( getContext(), Operation.OP_MODIFY, (Invoice)newInvoice, getModalParent() ).showModalWindow();
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
			query.setStatus( Shipment.STATUS_CREATED );
			
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
			comboShipments.setItemCaption( shipment.getId(), shipment.getFormattedNumber() + " - " + shipment.getIncoterms() );
		}
	}
	
	private void onSelectedQuotation()
	{
		try
		{
			QuotationQuery queryQuotation = new QuotationQuery();
			queryQuotation.setId( (Long)comboQuotations.getValue() );
	
			Quotation quotation = (Quotation)IOCManager._QuotationsManager.getRow( getContext(), queryQuotation );
		
			LOG.info( quotation );
			newInvoice.setTransport_cost( quotation.getPendingTansportCost() );
			newInvoice.setQuotation( quotation );
			
			editTransport_cost.markAsDirty();
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
		try
		{
			long ts = CalendarUtils.getTodayAsLong( "UTC" );
			
			String pagesize = "A4";
			String template = newInvoice.getQuotation().getCustomer_taxable().booleanValue() ? "national-invoice-template" : "international-invoice-template";
			String timeout = "0";
			
			String extra = "ts=" + ts + 
					"&id=" + newInvoice.getId() + 
					"&name=" + newInvoice.getFormattedNumber() + "-" + newInvoice.getQuotation().getTitle() + 
					"&pagesize=" + pagesize + 
					"&template=" + template +
					"&url=" + getContext().getData( "Url" ) +
					"&timeout=" + timeout;
			
			String user = getContext().getUser().getLogin();
			String password = getContext().getUser().getPwd();
			
			String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
			String code = "code=" + Authorization.encrypt( extra, password ) ;

			String url = getContext().getData( "Url" ) + "/services/invoice" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
			
			String caption = getContext().getString( "template.invoice.invoice" ) + " " + newInvoice.getFormattedNumber() ;

			ShowExternalUrlDlg dlg = new ShowExternalUrlDlg(); 
	
			dlg.setContext( getContext() );
			dlg.setUrl( url );
			dlg.setCaption( caption );
			dlg.createComponents();
			
			getUI().addWindow( dlg );
		}

		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}

	public void onEmailQuotation()
	{
		try
		{
			String template = newInvoice.getQuotation().getCustomer_taxable().booleanValue() ? "national-invoice-template" : "international-invoice-template";
			String name = newInvoice.getFormattedNumber() + "-" + newInvoice.getQuotation().getTitle() + ".pdf";
			
			PdfExportInvoice export = new PdfExportInvoice( newInvoice );
			
			export.setOrientation( "portrait" );
			export.setPagesize( "A4" );
			export.setTemplate( template );
		
			AppContext ctx1 = new AppContext( newInvoice.getQuotation().getCustomer_language() );
			IOCManager._ParametersManager.loadParameters( ctx1 );
			ctx1.setUser( getContext().getUser() );
			ctx1.addData( "Url", getContext().getData( "Url" ) );

			export.setContext( ctx1 );

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			LOG.info( "generating PDF ..." );
			export.doExport( os );

			List<Attachment> attachments = new ArrayList<Attachment>();
			attachments.add( new Attachment( name, "application/pdf", os.toByteArray() ) );
			
			String subject = ctx1.getString( "modalNewInvoice.emailSubject" ).replaceAll( "%name%", name );  
			String text = ctx1.getString( "modalNewInvoice.emailText" ).
					replaceAll( "%contact_person%", newInvoice.getQuotation().getCustomer_contact_person() ).
					replaceAll( "%reference_order%", newInvoice.getQuotation().getReference_order() );  

			CompanyQuery query = new CompanyQuery();
			query.setType_company( Company.TYPE_OWNER );
			Company company = (Company)IOCManager._CompaniesManager.getRow( getContext(), query );

			String body = text + "\n\n" + getContext().getCompanyDataAndLegal( company ); 
			
			final SendEmailDlg dlg = new SendEmailDlg( getContext(), getContext().getString( "modalNewInvoice.emailTitle" ), attachments );
			dlg.setTo( newInvoice.getQuotation().getCustomer_email() );
			dlg.setReply_to( company.getEmail() );
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
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			Utils.showNotification( getContext(), getContext().getString( "modalNewQuotation.emailError" ), Notification.Type.ERROR_MESSAGE );
		}
	}
}
