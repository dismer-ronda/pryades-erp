package es.pryades.erp.configuration.modals;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
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
import es.pryades.erp.configuration.tabs.QuotationsLinesConfig;
import es.pryades.erp.dashboard.Dashboard;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Quotation;
import es.pryades.erp.dto.QuotationAttachment;
import es.pryades.erp.dto.QuotationDelivery;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.dto.query.QuotationQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.reports.PdfExportQuotation;
import lombok.Getter;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewQuotation extends ModalWindowsCRUD implements ModalParent
{
	private static final long serialVersionUID = -6417058048202732508L;

	private static final Logger LOG = Logger.getLogger( ModalNewQuotation.class );

	private List<Company> customers;
	
	@Getter
	protected Quotation newQuotation;

	@Getter	private PopupDateField fromDateField;
	private ComboBox comboCustomers;
	private ComboBox comboStatus;
	
	private TextField editTitle;
	private TextField editValidity;
	private TextField editReference_request;
	private TextField editReference_order;
	private TextField editPackaging;
	private TextField editDelivery;
	private TextField editWarranty;
	private TextArea editPayment_terms;
	private TextField editTax_rate;
	
	private QuotationsLinesConfig configLines;
	
	private UserDefault customer;
	private UserDefault validity;
	private UserDefault packaging;
	private UserDefault delivery;
	private UserDefault warranty;
	private UserDefault payment;
	private UserDefault tax_rate;
	
	private HorizontalLayout rowCombined;
	private HorizontalLayout rowDeliveries;
	private HorizontalLayout rowAttachments;
	private Panel panelLines;

	
	/**
	 * editReference_order
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewQuotation( AppContext context, Operation modalOperation, Quotation orgParameter, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgParameter );

		setWidth( "90%" );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		loadUserDefaults();
		
		try
		{
			newQuotation = (Quotation) Utils.clone( (Quotation) orgDto );
		}
		catch ( Throwable e1 )
		{
			newQuotation = new Quotation();
			newQuotation.setQuotation_date( CalendarUtils.getTodayAsLong() );

			newQuotation.setRef_customer( Utils.getLong( customer.getData_value(), 0 ) );
			newQuotation.setValidity( Utils.getInt( validity.getData_value(), 0 ) );
			newQuotation.setDelivery( delivery.getData_value() );
			newQuotation.setPackaging( packaging.getData_value() );
			newQuotation.setWarranty( warranty.getData_value() );
			newQuotation.setPayment_terms( payment.getData_value() );
			newQuotation.setTax_rate( Utils.getDouble( tax_rate.getData_value(), 0 ) );
			newQuotation.setStatus( Quotation.STATUS_CREATED );
		}

		layout.setHeight( "-1px" );
		
		bi = new BeanItem<BaseDto>( newQuotation );

		loadCompanies();

		fromDateField = new PopupDateField(getContext().getString( "modalNewQuotation.popupDate" ));
		fromDateField.setResolution( Resolution.DAY );
		fromDateField.setDateFormat( "dd-MM-yyyy" );
		fromDateField.setWidth( "100%" );
		fromDateField.setValue( CalendarUtils.getDateFromString( Long.toString( newQuotation.getQuotation_date() ), "yyyyMMddHHmmss" ) );
		
		comboCustomers = new ComboBox(getContext().getString( "modalNewQuotation.comboCustomer" ));
		comboCustomers.setWidth( "100%" );
		comboCustomers.setNullSelectionAllowed( false );
		comboCustomers.setTextInputAllowed( true );
		comboCustomers.setImmediate( true );
		fillComboCompanies();
		comboCustomers.setPropertyDataSource( bi.getItemProperty( "ref_customer" ) );
		
		if ( !getOperation().equals( Operation.OP_ADD ) )
		{
			comboStatus = new ComboBox(getContext().getString( "modalNewQuotation.comboStatus" ));
			comboStatus.setWidth( "100%" );
			comboStatus.setNullSelectionAllowed( false );
			comboStatus.setTextInputAllowed( false );
			comboStatus.setImmediate( true );
			fillComboStatus();
			comboStatus.setPropertyDataSource( bi.getItemProperty( "status" ) );
		}
		
		editTitle = new TextField( getContext().getString( "modalNewQuotation.editTitle" ), bi.getItemProperty( "title" ) );
		editTitle.setWidth( "100%" );
		editTitle.setNullRepresentation( "" );
		
		editValidity = new TextField( getContext().getString( "modalNewQuotation.editValidity" ), bi.getItemProperty( "validity" ) );
		editValidity.setWidth( "100%" );
		editValidity.setNullRepresentation( "" );
		
		editReference_request = new TextField( getContext().getString( "modalNewQuotation.editReference_request" ), bi.getItemProperty( "reference_request" ) );
		editReference_request.setWidth( "100%" );
		editReference_request.setNullRepresentation( "" );
		
		editReference_order = new TextField( getContext().getString( "modalNewQuotation.editReference_order" ), bi.getItemProperty( "reference_order" ) );
		editReference_order.setWidth( "100%" );
		editReference_order.setNullRepresentation( "" );
		
		editPackaging = new TextField( getContext().getString( "modalNewQuotation.editPackaging" ), bi.getItemProperty( "packaging" ) );
		editPackaging.setWidth( "100%" );
		editPackaging.setNullRepresentation( "" );
		
		editDelivery = new TextField( getContext().getString( "modalNewQuotation.editDelivery" ), bi.getItemProperty( "delivery" ) );
		editDelivery.setWidth( "100%" );
		editDelivery.setNullRepresentation( "" );
		
		editWarranty = new TextField( getContext().getString( "modalNewQuotation.editWarranty" ), bi.getItemProperty( "warranty" ) );
		editWarranty.setWidth( "100%" );
		editWarranty.setNullRepresentation( "" );
		
		editPayment_terms = new TextArea( getContext().getString( "modalNewQuotation.editPayment_terms" ), bi.getItemProperty( "payment_terms" ) );
		editPayment_terms.setWidth( "100%" );
		editPayment_terms.setNullRepresentation( "" );
		editPayment_terms.setRows( 3 );
		
		editTax_rate= new TextField( getContext().getString( "modalNewQuotation.editTax_rate" ), bi.getItemProperty( "tax_rate" ) );
		editTax_rate.setWidth( "100%" );
		editTax_rate.setNullRepresentation( "" );
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( fromDateField );
		row1.addComponent( comboCustomers );
		if ( !getOperation().equals( Operation.OP_ADD ) )
			row1.addComponent( comboStatus );
		row1.addComponent( editTitle );
		row1.addComponent( editReference_request );
		row1.addComponent( editReference_order );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row1.addComponent( editValidity );
		row2.addComponent( editDelivery );
		row2.addComponent( editTax_rate );
		row2.addComponent( editPackaging );
		row2.addComponent( editWarranty );

		HorizontalLayout row4 = new HorizontalLayout();
		row4.setWidth( "100%" );
		row4.setSpacing( true );
		row4.addComponent( editPayment_terms );

		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		componentsContainer.addComponent( row4 );
		
		if ( !getOperation().equals( Operation.OP_ADD ) )
		{
			rowCombined = new HorizontalLayout();
			rowCombined.setSpacing( true );
			
			rowDeliveries = new HorizontalLayout();
			rowDeliveries.setSpacing( true );

			rowAttachments = new HorizontalLayout();
			rowAttachments.setSpacing( true );
			
			showQuotationDeliveries();
			rowCombined.addComponent( rowDeliveries );

			showQuotationAttachments();
			rowCombined.addComponent( rowAttachments );

			componentsContainer.addComponent( rowCombined );
			
			showQuotationLines();
			componentsContainer.addComponent( panelLines );
			
			Button btnDuplicate = new Button();
			btnDuplicate.setCaption( getContext().getString( "modalNewQuotation.duplicate" ) );
			btnDuplicate.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = -7706249561000995081L;

				public void buttonClick( ClickEvent event )
				{
					onDuplicateQuotation();
				}
			} );

			getDefaultOperationsRow().addComponentAsFirst( btnDuplicate );
			getDefaultOperationsRow().setComponentAlignment( btnDuplicate, Alignment.MIDDLE_LEFT );

			Button bttnPdf = new Button();
			bttnPdf.setCaption( getContext().getString( "modalNewQuotation.pdf" ) );
			bttnPdf.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 6750035234840398025L;

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
				private static final long serialVersionUID = -7806249561000995081L;

				public void buttonClick( ClickEvent event )
				{
					onEmailQuotation();
				}
			} );

			getDefaultOperationsRow().addComponentAsFirst( btnEmail );
			getDefaultOperationsRow().setComponentAlignment( btnEmail, Alignment.MIDDLE_LEFT );
		}
	}

	private void showQuotationLines()
	{
		panelLines = new Panel();
		panelLines.setStyleName( "borderless light" );
		panelLines.setHeight( "400px" );

		configLines = new QuotationsLinesConfig( getContext(), this );
		configLines.initializeComponent();
		configLines.setSizeFull();
		configLines.setSpacing( true );
		configLines.getTable().setMargin( false );
		configLines.getOppLayout().setMargin( false );

		panelLines.setContent( configLines );
	}
	
	private void showQuotationDeliveries()
	{
		rowDeliveries.removeAllComponents();
		
		List<QuotationDelivery> deliveries = newQuotation.getDeliveries();
		
		Label labelDeliveries = new Label( getContext().getString( "modalNewQuotation.deliveries" ) );
		rowDeliveries.addComponent( labelDeliveries );
		rowDeliveries.setComponentAlignment( labelDeliveries, Alignment.MIDDLE_CENTER );
		
		if ( deliveries != null )
		{
			for ( QuotationDelivery delivery : deliveries )
			{
				Button b = new Button( CalendarUtils.getDateFromLongAsString( delivery.getDeparture_date(), "dd-MM-yyyy" ) );
				b.setData( delivery );
				b.addClickListener( new Button.ClickListener() 
				{
					private static final long serialVersionUID = 5590025385768313934L;

					public void buttonClick(ClickEvent event) 
		            {
						onDeliveryClick( event.getButton() );
		            }
		        });
				rowDeliveries.addComponent( b );
			}
		}
		
		Button buttonAddDelivery = new Button( "+" );
		buttonAddDelivery.addClickListener( new Button.ClickListener() 
		{
			private static final long serialVersionUID = 7110604342494218089L;

			public void buttonClick(ClickEvent event) 
            {
				onDeliveryAddClick();
            }
        });
		
		rowDeliveries.addComponent( buttonAddDelivery );
	}

	private void showQuotationAttachments()
	{
		rowAttachments.removeAllComponents();
		
		List<QuotationAttachment> deliveries = newQuotation.getAttachments();
		
		Label labelAttachments = new Label( getContext().getString( "modalNewQuotation.attachments" ) );
		rowAttachments.addComponent( labelAttachments );
		rowAttachments.setComponentAlignment( labelAttachments, Alignment.MIDDLE_CENTER );
		
		if ( deliveries != null )
		{
			for ( QuotationAttachment delivery : deliveries )
			{
				Button b = new Button( delivery.getTitle() );
				b.setData( delivery );
				b.addClickListener( new Button.ClickListener() 
				{
					private static final long serialVersionUID = -8972535777112675820L;

					public void buttonClick(ClickEvent event) 
		            {
						onAttachmentClick( event.getButton() );
		            }
		        });
				rowAttachments.addComponent( b );
			}
		}
		
		Button buttonAddAttachment = new Button( "+" );
		buttonAddAttachment.addClickListener( new Button.ClickListener() 
		{
			private static final long serialVersionUID = -4843191838206569522L;

			public void buttonClick(ClickEvent event) 
            {
				onAttachmentAddClick();
            }
        });
		
		rowAttachments.addComponent( buttonAddAttachment );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewQuotation";
	}

	@Override
	protected void defaultFocus()
	{
		comboCustomers.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newQuotation.setId( null );
			newQuotation.setQuotation_date( CalendarUtils.getDateAsLong( fromDateField.getValue() ) );
			
			IOCManager._QuotationsManager.setRow( getContext(), null, newQuotation );
			
			saveUserDefaults();

			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshInvoicesTab();
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
	protected boolean onModify()
	{
		try
		{
			newQuotation.setQuotation_date( CalendarUtils.getDateAsLong( fromDateField.getValue() ) );

			IOCManager._QuotationsManager.setRow( getContext(), (Quotation) orgDto, newQuotation );

			saveUserDefaults();

			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshInvoicesTab();
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
	protected boolean onDelete()
	{
		try
		{
			IOCManager._QuotationsManager.delRow( getContext(), newQuotation );

			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshInvoicesTab();
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

	@SuppressWarnings("unchecked")
	private void loadCompanies()
	{
		try
		{
			CompanyQuery query = new CompanyQuery();
			query.setType_company( Company.TYPE_CUSTOMER );
			query.setRef_user( getContext().getUser().getId() );
			
			customers = IOCManager._CompaniesManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			customers = new ArrayList<Company>();
		}
	}
	
	private void fillComboCompanies()
	{
		for ( Company company : customers )
		{
			comboCustomers.addItem( company.getId() );
			comboCustomers.setItemCaption( company.getId(), company.getName() );
		}
	}

	private void fillComboStatus()
	{
		comboStatus.addItem( Quotation.STATUS_CREATED );
		comboStatus.setItemCaption( Quotation.STATUS_CREATED, getContext().getString( "quotation.status." + Quotation.STATUS_CREATED ) );

		comboStatus.addItem( Quotation.STATUS_READY );
		comboStatus.setItemCaption( Quotation.STATUS_READY, getContext().getString( "quotation.status." + Quotation.STATUS_READY ) );

		comboStatus.addItem( Quotation.STATUS_SENT );
		comboStatus.setItemCaption( Quotation.STATUS_SENT, getContext().getString( "quotation.status." + Quotation.STATUS_SENT ) );

		comboStatus.addItem( Quotation.STATUS_APPROVED );
		comboStatus.setItemCaption( Quotation.STATUS_APPROVED, getContext().getString( "quotation.status." + Quotation.STATUS_APPROVED ) );

		comboStatus.addItem( Quotation.STATUS_FINISHED );
		comboStatus.setItemCaption( Quotation.STATUS_FINISHED, getContext().getString( "quotation.status." + Quotation.STATUS_FINISHED ) );

		comboStatus.addItem( Quotation.STATUS_DISCARDED );
		comboStatus.setItemCaption( Quotation.STATUS_DISCARDED, getContext().getString( "quotation.status." + Quotation.STATUS_DISCARDED ) );
	}

	private void reloadQuotation()
	{
		try
		{
			QuotationQuery query = new QuotationQuery();
			query.setId( newQuotation.getId() );
			
			Quotation temp = (Quotation)IOCManager._QuotationsManager.getRow( getContext(), query );
			
			newQuotation.setAttachments( temp.getAttachments() );
			newQuotation.setLines( temp.getLines() );
			newQuotation.setDeliveries( temp.getDeliveries() );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
	
	@Override
	public void refreshVisibleContent( boolean repage )
	{
		reloadQuotation();
		
		showQuotationDeliveries();
		showQuotationAttachments();
		
		configLines.refreshVisibleContent( repage );
		
		getModalParent().refreshVisibleContent( true );
	}
	
	@Override
	protected boolean editAfterNew()
	{
		return true;
	}

	@Override
	protected void doEditAfterNew()
	{
		new ModalNewQuotation( getContext(), Operation.OP_MODIFY, (Quotation)newQuotation, getModalParent() ).showModalWindow();
	}

	private void loadUserDefaults()
	{
		customer = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_CUSTOMER );
		validity = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_VALIDITY );
		delivery = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_DELIVERY );
		packaging = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_PACKAGING );
		warranty = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_WARRANTY );
		payment = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_PAYMENT );
		tax_rate = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_TAX_RATE );
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), customer, newQuotation.getRef_customer().toString() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), validity, newQuotation.getValidity().toString() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), delivery, newQuotation.getDelivery() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), packaging, newQuotation.getPackaging() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), warranty, newQuotation.getWarranty() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), payment, newQuotation.getPayment_terms() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), tax_rate, newQuotation.getTax_rate().toString() );
	}
	
	private void onDeliveryClick( Button button )
	{
		new ModalNewQuotationDelivery( getContext(), Operation.OP_MODIFY, (QuotationDelivery)button.getData(), this ).showModalWindow();
	}
	
	private void onDeliveryAddClick()
	{
		new ModalNewQuotationDelivery( getContext(), Operation.OP_ADD, null, this ).showModalWindow();
	}

	private void onAttachmentClick( Button button )
	{
		new ModalNewQuotationAttachment( getContext(), Operation.OP_MODIFY, (QuotationAttachment)button.getData(), this ).showModalWindow();
	}
	
	private void onAttachmentAddClick()
	{
		new ModalNewQuotationAttachment( getContext(), Operation.OP_ADD, null, this ).showModalWindow();
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
				QuotationQuery queryQuotation = new QuotationQuery();
				queryQuotation.setId( newQuotation.getId() );
				Quotation quotation = (Quotation)IOCManager._QuotationsManager.getRow( getContext(), queryQuotation );

				long ts = CalendarUtils.getTodayAsLong( "UTC" );
				
				String pagesize = "A4";
				String template = newQuotation.getCustomer_taxable().booleanValue() ? "national-quotation-template" : "international-quotation-template";
				String timeout = "0";
				
				String extra = "ts=" + ts + 
						"&id=" + quotation.getId() + 
						"&name=QT-" + quotation.getFormattedNumber() + "-" +  quotation.getReference_request() + ".pdf" + 
						"&pagesize=" + pagesize + 
						"&template=" + template +
						"&url=" + getContext().getData( "Url" ) +
						"&timeout=" + timeout;
				
				String user = getContext().getUser().getLogin();
				String password = getContext().getUser().getPwd();
				
				String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
				String code = "code=" + Authorization.encrypt( extra, password ) ;
	
				String url = getContext().getData( "Url" ) + "/services/quotation" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
				
				String caption = getContext().getString( "template.quotation.quotation" ) + " " + quotation.getFormattedNumber() ;
	
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

	public void onDuplicateQuotation()
	{
		try
		{
			IOCManager._QuotationsManager.duplicateQuotation( getContext(), newQuotation );
			
			refreshVisibleContent( true );
			
			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshQuotationsTab();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
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
				QuotationQuery queryQuotation = new QuotationQuery();
				queryQuotation.setId( newQuotation.getId() );
				Quotation quotation = (Quotation)IOCManager._QuotationsManager.getRow( getContext(), queryQuotation );
			
				String template = quotation.getCustomer_taxable().booleanValue() ? "national-quotation-template" : "international-quotation-template";
				String name = "QT-" + quotation.getFormattedNumber() + "-" +  quotation.getReference_request() + ".pdf";
				
				PdfExportQuotation export = new PdfExportQuotation( quotation );
				
				export.setOrientation( newQuotation.getDeliveries().size() > 1 ? "landscape" : "portrait" );
				export.setPagesize( "A4" );
				export.setTemplate( template );
				
				AppContext ctx1 = new AppContext( quotation.getCustomer_language() );
				IOCManager._ParametersManager.loadParameters( ctx1 );
				ctx1.setUser( getContext().getUser() );
				ctx1.addData( "Url", getContext().getData( "Url" ) );
	
				export.setContext( ctx1 );
			
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				export.doExport( os );
	
				List<Attachment> attachments = new ArrayList<Attachment>();
				attachments.add( new Attachment( name, "application/pdf", os.toByteArray() ) );
				
				String subject = ctx1.getString( "modalNewQuotation.emailSubject" ).replaceAll( "%name%", name );  
				String text = ctx1.getString( "modalNewQuotation.emailText" ).
						replaceAll( "%reference_request%", quotation.getReference_request() ).
						replaceAll( "%contact_person%", quotation.getCustomer_contact_person() );  
	
				CompanyQuery query = new CompanyQuery();
				query.setType_company( Company.TYPE_OWNER );
				Company company = (Company)IOCManager._CompaniesManager.getRow( getContext(), query );
	
				String body = text + "\n\n" + getContext().getCompanyDataAndLegal( company ); 
	
				final SendEmailDlg dlg = new SendEmailDlg( getContext(), getContext().getString( "modalNewQuotation.emailTitle" ), attachments );
				dlg.setTo( quotation.getCustomer_email() );
				dlg.setReply_to( company.getEmail() );
				dlg.setSubject( subject );
				dlg.setBody( body );
				dlg.setData( quotation );
				dlg.addCloseListener
				( 
					new Window.CloseListener() 
					{
						private static final long serialVersionUID = -798063903136075292L;

						@Override
					    public void windowClose( CloseEvent e ) 
					    {
							if ( dlg.isSuccess() )
							{
								try
								{
									Quotation org = (Quotation)dlg.getData();
									Quotation clone = (Quotation)Utils.clone( org ); 

									org.setStatus( Quotation.STATUS_SENT );
									
									IOCManager._QuotationsManager.setRow( getContext(), clone, org );
									
									Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
									dashboard.refreshInvoicesTab();
									dashboard.refreshQuotationsTab();
									dashboard.refreshShipmentsTab();
								}
								catch ( Throwable e1 )
								{
									Utils.logException( e1, LOG );
						
									Utils.showNotification( getContext(), getContext().getString( "modalNewQuotation.emailError" ), Notification.Type.ERROR_MESSAGE );
								}
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

	@Override
	protected boolean hasDelete()
	{
		return getContext().hasRight( "configuration.quotations.delete" );
	}

	@Override
	public boolean checkAddRight()
	{
		return getContext().hasRight( "configuration.quotations.add" );
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.quotations.modify" );
	}
}
