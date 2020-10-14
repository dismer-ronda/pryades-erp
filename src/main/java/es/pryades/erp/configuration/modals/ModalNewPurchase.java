package es.pryades.erp.configuration.modals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window;

import es.pryades.erp.application.SelectPaymentDlg;
import es.pryades.erp.application.ShowExternalUrlDlg;
import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Authorization;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dashboard.Dashboard;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.CompanyContact;
import es.pryades.erp.dto.Operation;
import es.pryades.erp.dto.Purchase;
import es.pryades.erp.dto.Transaction;
import es.pryades.erp.dto.User;
import es.pryades.erp.dto.query.CompanyContactQuery;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.dto.query.OperationQuery;
import es.pryades.erp.dto.query.UserQuery;
import es.pryades.erp.ioc.IOCManager;
import lombok.Getter;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewPurchase extends ModalWindowsCRUD implements ModalParent, Receiver, SucceededListener, FailedListener, ProgressListener
{
	private static final long serialVersionUID = 5833911702982111297L;

	private static final Logger LOG = Logger.getLogger( ModalNewPurchase.class );
	
	private final int UPLOAD_INVOICE 		= 1;
	private final int UPLOAD_QUOTATION 		= 2;
	private final int UPLOAD_PAYMENT		= 3;

	private List<Company> providers;
	private List<User> buyers;
	private List<CompanyContact>contacts;
	private List<Operation>operations;
	
	@Getter
	protected Purchase newPurchase;

	@Getter	private PopupDateField purchaseDateField;
	@Getter	private PopupDateField registerDateField;
	private ComboBox comboProviders;
	private ComboBox comboContacts;
	private ComboBox comboStatus;
	private ComboBox comboBuyer;
	private ComboBox comboType;
	private ComboBox comboOperation;
	private Button btnAdd;

	private TextField editTitle;
	private TextArea editDescription;
	private TextField editNetPrice;
	private TextField editNetTax;
	private TextField editNetRetention;
	private Label labelPayed;
	private TextField editInvoiceNumber;
	private TextField editQuotationNumber;

	private String fileName;
	private ByteArrayOutputStream os;

	
	/**
	 * editReference_order
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewPurchase( AppContext context, OperationCRUD modalOperation, Purchase orgDto, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgDto );

		setWidth( "75%" );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		loadUserDefaults();
		
		try
		{
			newPurchase = (Purchase) Utils.clone( (Purchase) orgDto );
		}
		catch ( Throwable e1 )
		{
			newPurchase = new Purchase();
			newPurchase.setPurchase_date( CalendarUtils.getTodayAsLong() );
			newPurchase.setRegister_date( CalendarUtils.getTodayAsLong() );
			newPurchase.setPurchase_type( Purchase.TYPE_SELL );
			newPurchase.setStatus( Purchase.STATUS_CREATED );
			newPurchase.setRef_buyer( getContext().getUser().getId() );
			newPurchase.setPayed( 0.0 );
			newPurchase.setNet_tax( 0.0 );
		}

		layout.setHeight( "-1px" );
		
		bi = new BeanItem<BaseDto>( newPurchase );
		
		purchaseDateField = new PopupDateField(getContext().getString( "modalNewPurchase.purchaseDateField" ));
		purchaseDateField.setResolution( Resolution.DAY );
		purchaseDateField.setDateFormat( "dd-MM-yyyy" );
		purchaseDateField.setWidth( "100%" );
		purchaseDateField.setValue( CalendarUtils.getDateFromString( Long.toString( newPurchase.getPurchase_date() ), "yyyyMMddHHmmss" ) );
		purchaseDateField.setRequired( true );
		purchaseDateField.setRequiredError( getContext().getString( "words.required" ) );
		purchaseDateField.setInvalidCommitted( true );
		
		registerDateField = new PopupDateField(getContext().getString( "modalNewPurchase.registerDateField" ));
		registerDateField.setResolution( Resolution.DAY );
		registerDateField.setDateFormat( "dd-MM-yyyy" );
		registerDateField.setWidth( "100%" );
		registerDateField.setValue( CalendarUtils.getDateFromString( Long.toString( newPurchase.getRegister_date() ), "yyyyMMddHHmmss" ) );
		registerDateField.setRequired( true );
		registerDateField.setRequiredError( getContext().getString( "words.required" ) );
		registerDateField.setInvalidCommitted( true );
		
		loadProviders();
		comboProviders = new ComboBox(getContext().getString( "modalNewPurchase.comboProviders" ));
		comboProviders.setWidth( "100%" );
		comboProviders.setNullSelectionAllowed( false );
		comboProviders.setTextInputAllowed( true );
		comboProviders.setImmediate( true );
		fillComboProviders();
		comboProviders.setPropertyDataSource( bi.getItemProperty( "ref_provider" ) );
		comboProviders.addValueChangeListener( new Property.ValueChangeListener() 
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 2319268721819976985L;

			public void valueChange(ValueChangeEvent event) 
		    {
		        onSelectedProvider();
		    }
		});
		comboProviders.setRequired( true );
		comboProviders.setRequiredError( getContext().getString( "words.required" ) );
		comboProviders.setInvalidCommitted( true );
		
		loadContacts();
		comboContacts = new ComboBox(getContext().getString( "modalNewPurchase.comboContact" ));
		comboContacts.setWidth( "100%" );
		comboContacts.setNullSelectionAllowed( true );
		comboContacts.setTextInputAllowed( true );
		comboContacts.setImmediate( true ); 
		fillComboContacts();
		comboContacts.setPropertyDataSource( bi.getItemProperty( "ref_contact" ) );

		loadBuyers();
		comboBuyer = new ComboBox(getContext().getString( "modalNewPurchase.comboBuyer" ));
		comboBuyer.setWidth( "100%" );
		comboBuyer.setNullSelectionAllowed( false );
		comboBuyer.setTextInputAllowed( true );
		comboBuyer.setImmediate( true );
		fillComboBuyers();
		comboBuyer.setPropertyDataSource( bi.getItemProperty( "ref_buyer" ) );
		comboBuyer.setRequired( true );
		comboBuyer.setRequiredError( getContext().getString( "words.required" ) );
		comboBuyer.setInvalidCommitted( true );

		loadOperations();
		comboOperation = new ComboBox(getContext().getString( "modalNewPurchase.comboOperation" ));
		comboOperation.setWidth( "100%" );
		comboOperation.setNullSelectionAllowed( false );
		comboOperation.setTextInputAllowed( true );
		comboOperation.setImmediate( true );
		fillComboOperations();
		comboOperation.setPropertyDataSource( bi.getItemProperty( "ref_operation" ) );
		comboOperation.setRequired( true );
		comboOperation.setRequiredError( getContext().getString( "words.required" ) );
		comboOperation.setInvalidCommitted( true );

		if ( !getOperation().equals( OperationCRUD.OP_ADD ) )
		{
			comboStatus = new ComboBox(getContext().getString( "modalNewQuotation.comboStatus" ));
			comboStatus.setWidth( "100%" );
			comboStatus.setNullSelectionAllowed( false );
			comboStatus.setTextInputAllowed( false );
			comboStatus.setImmediate( true );
			fillComboStatus();
			comboStatus.setPropertyDataSource( bi.getItemProperty( "status" ) );
		}
		
		comboType = new ComboBox(getContext().getString( "modalNewPurchase.comboType" ));
		comboType.setWidth( "100%" );
		comboType.setNullSelectionAllowed( false );
		comboType.setTextInputAllowed( false );
		comboType.setImmediate( true );
		fillComboType();
		comboType.setPropertyDataSource( bi.getItemProperty( "purchase_type" ) );
		comboType.setRequired( true );
		comboType.setRequiredError( getContext().getString( "words.required" ) );
		comboType.setInvalidCommitted( true );

		editTitle = new TextField( getContext().getString( "modalNewPurchase.editTitle" ), bi.getItemProperty( "title" ) );
		editTitle.setWidth( "100%" );
		editTitle.setNullRepresentation( "" );
		editTitle.setRequired( true );
		editTitle.setRequiredError( getContext().getString( "words.required" ) );
		editTitle.setInvalidCommitted( true );
		
		editDescription = new TextArea( getContext().getString( "modalNewPurchase.editDescription" ), bi.getItemProperty( "description" ) );
		editDescription.setWidth( "100%" );
		editDescription.setNullRepresentation( "" );
		//editDescription.setRows( 3 );

		editNetPrice = new TextField( getContext().getString( "modalNewPurchase.editNetPrice" ), bi.getItemProperty( "net_price" ) );
		editNetPrice.setWidth( "100%" );
		editNetPrice.setNullRepresentation( "" );
		editNetPrice.setRequired( true );
		editNetPrice.setRequiredError( getContext().getString( "words.required" ) );
		editNetPrice.setInvalidCommitted( true );
		
		editNetTax = new TextField( getContext().getString( "modalNewPurchase.editNetTax" ), bi.getItemProperty( "net_tax" ) );
		editNetTax.setWidth( "100%" );
		editNetTax.setNullRepresentation( "" );
		editNetTax.setRequired( true );
		editNetTax.setRequiredError( getContext().getString( "words.required" ) );
		editNetTax.setInvalidCommitted( true );

		editNetRetention = new TextField( getContext().getString( "modalNewPurchase.editNetRetention" ), bi.getItemProperty( "net_retention" ) );
		editNetRetention.setWidth( "100%" );
		editNetRetention.setNullRepresentation( "" );
		editNetRetention.setRequired( true );
		editNetRetention.setRequiredError( getContext().getString( "words.required" ) );
		editNetRetention.setInvalidCommitted( true );

		editQuotationNumber = new TextField( getContext().getString( "modalNewPurchase.editQuotationNumber" ), bi.getItemProperty( "quotation_number" ) );
		editQuotationNumber.setWidth( "100%" );
		editQuotationNumber.setNullRepresentation( "" );

		editInvoiceNumber = new TextField( getContext().getString( "modalNewPurchase.editInvoiceNumber" ), bi.getItemProperty( "invoice_number" ) );
		editInvoiceNumber.setWidth( "100%" );
		editInvoiceNumber.setNullRepresentation( "" );

		btnAdd = new Button(" + ");
		btnAdd.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 2127064478675096874L;

			public void buttonClick( ClickEvent event )
			{
				onAddProvider();
			}
		} );

		if ( !getOperation().equals( OperationCRUD.OP_ADD ) )
		{
			labelPayed = new Label();
			labelPayed.setWidth( "100%" );
			labelPayed.setValue( getContext().getString( "modalNewPurchase.editPayed" ) + " " + newPurchase.getPayedAsString() );
			labelPayed.addStyleName( (newPurchase.isFullyPayed() ? "green" : "payed") + " centered" );
			
			Button btnDuplicate = new Button();
			btnDuplicate.setCaption( getContext().getString( "modalNewPurchase.btnDuplicate" ) );
			btnDuplicate.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 7538387530542749190L;

				public void buttonClick( ClickEvent event )
				{
					onDuplicate();
				}
			} );

			getDefaultOperationsRow().addComponentAsFirst( btnDuplicate );
			getDefaultOperationsRow().setComponentAlignment( btnDuplicate, Alignment.MIDDLE_LEFT );

			Button btnPay = new Button();
			btnPay.setCaption( getContext().getString( "modalNewPurchase.btnPay" ) );
			btnPay.setEnabled( newPurchase.pendingPayment() );
			btnPay.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = -1265552269291389575L;

				public void buttonClick( ClickEvent event )
				{
					onPay();
				}
			} );

			getDefaultOperationsRow().addComponentAsFirst( btnPay );
			getDefaultOperationsRow().setComponentAlignment( btnPay, Alignment.MIDDLE_LEFT );
		}

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( comboBuyer );
		row1.addComponent( purchaseDateField );
		row1.addComponent( registerDateField );
		row1.addComponent( comboType );
		
		HorizontalLayout rowProvider = new HorizontalLayout();
		rowProvider.setWidth( "100%" );
		rowProvider.setSpacing( true );
		rowProvider.addComponent( comboProviders );
		rowProvider.addComponent( btnAdd );
		rowProvider.setComponentAlignment( btnAdd, Alignment.BOTTOM_LEFT );
		rowProvider.setExpandRatio( comboProviders, 1.0f );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( comboOperation );
		if ( !getOperation().equals( OperationCRUD.OP_ADD ) )
		{
			row2.addComponent( comboStatus );
		}
		row2.addComponent( rowProvider );
		row2.addComponent( comboContacts );
		
		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( editTitle );
		
		HorizontalLayout row4 = new HorizontalLayout();
		row4.setWidth( "100%" );
		row4.addComponent( editDescription );

		HorizontalLayout row5 = new HorizontalLayout();
		row5.setWidth( "100%" );
		row5.setSpacing( true );
		row5.addComponent( editNetPrice );
		row5.addComponent( editNetTax );
		row5.addComponent( editNetRetention );
		row5.addComponent( editQuotationNumber );
		row5.addComponent( editInvoiceNumber );
		if ( !getOperation().equals( OperationCRUD.OP_ADD ) )
		{
			row5.addComponent( labelPayed );
			row5.setComponentAlignment( labelPayed, Alignment.BOTTOM_CENTER );
		}
			
		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		componentsContainer.addComponent( row3 );
		componentsContainer.addComponent( row4 );
		componentsContainer.addComponent( row5 );

		Upload uploadQuotation = new Upload( null, this );
		uploadQuotation.setWidth( "200px" );
		uploadQuotation.setImmediate( true );
		uploadQuotation.setButtonCaption( getContext().getString( "modalNewPurchase.captionUploadQuotation" ) );
		uploadQuotation.addSucceededListener( this );
		uploadQuotation.setData( new Integer( UPLOAD_QUOTATION ) );

		HorizontalLayout row6 = new HorizontalLayout();
		row6.setSpacing( true );
			
		row6.addComponent( uploadQuotation );
		row6.setComponentAlignment( uploadQuotation, Alignment.BOTTOM_LEFT );
		
		if ( !getOperation().equals( OperationCRUD.OP_ADD ) )
		{
			/*Button bttnPdf = new Button();
			bttnPdf.setCaption( getContext().getString( "modalNewPurchase.pdfOrder" ) );
			bttnPdf.addClickListener( new Button.ClickListener()
			{
				public void buttonClick( ClickEvent event )
				{
					onPdfOrder();
				}
			} );

			getDefaultOperationsRow().addComponentAsFirst( bttnPdf );
			getDefaultOperationsRow().setComponentAlignment( bttnPdf, Alignment.MIDDLE_LEFT );

			Button btnEmail = new Button();
			btnEmail.setCaption( getContext().getString( "modalNewPurchase.emailOrder" ) );
			btnEmail.addClickListener( new Button.ClickListener()
			{
				public void buttonClick( ClickEvent event )
				{
					onEmailOrder();
				}
			} );

			getDefaultOperationsRow().addComponentAsFirst( btnEmail );
			getDefaultOperationsRow().setComponentAlignment( btnEmail, Alignment.MIDDLE_LEFT );*/
			
			Upload uploadInvoice = new Upload( null, this );
			uploadInvoice.setWidth( "200px" );
			uploadInvoice.setImmediate( true );
			uploadInvoice.setButtonCaption( getContext().getString( "modalNewPurchase.captionUploadInvoice" ) );
			uploadInvoice.addSucceededListener( this );
			uploadInvoice.setData( new Integer( UPLOAD_INVOICE ) );

			Upload uploadPayment = new Upload( null, this );
			uploadPayment.setWidth( "200px" );
			uploadPayment.setImmediate( true );
			uploadPayment.setButtonCaption( getContext().getString( "modalNewPurchase.captionUploadPayment" ) );
			uploadPayment.addSucceededListener( this );
			uploadPayment.setData( new Integer( UPLOAD_PAYMENT ) );

			row6.addComponent( uploadInvoice );
			row6.addComponent( uploadPayment );
		}

		componentsContainer.addComponent( row6 );
		
		if ( operation.equals( OperationCRUD.OP_MODIFY ) )
		{
			Button bttnViewQuotation = new Button( getContext().getString( "modalNewPurchase.showQuotation" ) );
			bttnViewQuotation.setWidth( "200px" );
			bttnViewQuotation.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 7538387530542749191L;

				public void buttonClick( ClickEvent event )
				{
					showPurchaseQuotation();
				}
			} );

			getCustomOperationsRow().addComponent( bttnViewQuotation );

			Button bttnViewInvoice = new Button( getContext().getString( "modalNewPurchase.showInvoice" ) );
			bttnViewInvoice.setWidth( "200px" );
			bttnViewInvoice.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = -1265552269291389575L;

				public void buttonClick( ClickEvent event )
				{
					showPurchaseInvoice();
				}
			} );
			
			getCustomOperationsRow().addComponent( bttnViewInvoice );
			
			Button bttnViewPayment = new Button( getContext().getString( "modalNewPurchase.showPayment" ) );
			bttnViewPayment.setWidth( "200px" );
			bttnViewPayment.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = -8195744485998107398L;

				public void buttonClick( ClickEvent event )
				{
					showPurchasePayment();
				}
			} );

			getCustomOperationsRow().addComponent( bttnViewPayment );
			
			//getCustomOperationsRow().setComponentAlignment( bttnView, Alignment.MIDDLE_LEFT );
		}
	}
	
	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewPurchase";
	}

	@Override
	protected void defaultFocus()
	{
		comboProviders.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			LOG.info(  "date "+ purchaseDateField.getValue() );
			
			newPurchase.setId( null );
			newPurchase.setPurchase_date( CalendarUtils.getDayAsLong( purchaseDateField.getValue() ) );
			newPurchase.setRegister_date( CalendarUtils.getDayAsLong( registerDateField.getValue() ) );
			
			IOCManager._PurchasesManager.setRow( getContext(), null, newPurchase );
			
			saveUserDefaults();

			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
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
	protected boolean onModify()
	{
		if ( newPurchase.getGrossPrice() > 0 && Utils.roundDouble( newPurchase.getGrossPrice(), 2 ) < Utils.roundDouble( newPurchase.getPayed(), 2 ) )
		{
			Utils.showNotification( getContext(), getContext().getString( "modalNewPurchase.grossLessThanPayed" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}
			
		if ( newPurchase.getGrossPrice() < 0 && Utils.roundDouble( newPurchase.getGrossPrice(), 2 ) > Utils.roundDouble( newPurchase.getPayed(), 2 ) )
		{
			Utils.showNotification( getContext(), getContext().getString( "modalNewPurchase.grossLessThanPayed" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}
			
		try
		{
			newPurchase.setPurchase_date( CalendarUtils.getDayAsLong( purchaseDateField.getValue() ) );
			newPurchase.setRegister_date( CalendarUtils.getDayAsLong( registerDateField.getValue() ) );
			
			IOCManager._PurchasesManager.setRow( getContext(), (Purchase) orgDto, newPurchase );

			saveUserDefaults();

			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
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
	protected boolean onDelete()
	{
		try
		{
			IOCManager._PurchasesManager.delRow( getContext(), newPurchase );

			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
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

	@SuppressWarnings("unchecked")
	private void loadProviders()
	{
		try
		{
			CompanyQuery query = new CompanyQuery();
			query.setType_company( Company.TYPE_PROVIDER );
			
			providers = IOCManager._CompaniesManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			providers = new ArrayList<Company>();
		}
	}
	
	private void fillComboProviders()
	{
		for ( Company company : providers )
		{
			comboProviders.addItem( company.getId() );
			comboProviders.setItemCaption( company.getId(), company.getAlias() );
		}
	}

	private void fillComboStatus()
	{
		comboStatus.addItem( Purchase.STATUS_CREATED );
		comboStatus.setItemCaption( Purchase.STATUS_CREATED, getContext().getString( "purchase.status." + Purchase.STATUS_CREATED ) );

		comboStatus.addItem( Purchase.STATUS_ORDERED );
		comboStatus.setItemCaption( Purchase.STATUS_ORDERED, getContext().getString( "purchase.status." + Purchase.STATUS_ORDERED ) );

		comboStatus.addItem( Purchase.STATUS_RECEIVED );
		comboStatus.setItemCaption( Purchase.STATUS_RECEIVED, getContext().getString( "purchase.status." + Purchase.STATUS_RECEIVED ) );
	}

	private void fillComboType()
	{
		for ( int i = Purchase.TYPE_SELL; i <= Purchase.TYPE_OTHER; i++ )
		{
			comboType.addItem( i );
			comboType.setItemCaption( i, getContext().getString( "purchase.type." + i ) );
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadContacts()
	{
		try
		{
			CompanyContactQuery query = new CompanyContactQuery();
			query.setRef_company( newPurchase.getRef_provider() );
			
			contacts = IOCManager._CompaniesContactsManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			contacts = new ArrayList<CompanyContact>();
		}
	}
	
	private void fillComboContacts()
	{
		comboContacts.removeAllItems();
		
		for ( CompanyContact contact : contacts )
		{
			comboContacts.addItem( contact.getId() );
			comboContacts.setItemCaption( contact.getId(), contact.getName() );
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadBuyers()
	{
		try
		{
			buyers = IOCManager._UsersManager.getRows( getContext(), new UserQuery() );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			buyers = new ArrayList<User>();
		}
	}

	private void fillComboBuyers()
	{
		comboBuyer.removeAllItems();
		
		for ( User user : buyers )
		{
			comboBuyer.addItem( user.getId() );
			comboBuyer.setItemCaption( user.getId(), user.getName() );
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadOperations()
	{
		try
		{
			OperationQuery query = new OperationQuery();
			query.setStatus( Operation.STATUS_EXCECUTION );
			operations = IOCManager._OperationsManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			operations = new ArrayList<Operation>();
		}
	}

	private void fillComboOperations()
	{
		comboOperation.removeAllItems();
		
		for ( Operation user : operations )
		{
			comboOperation.addItem( user.getId() );
			comboOperation.setItemCaption( user.getId(), user.getTitle() );
		}
	}
	/*private void reloadQuotation()
	{
		try
		{
			QuotationQuery query = new QuotationQuery();
			query.setId( newPurchase.getId() );
			
			Quotation temp = (Quotation)IOCManager._QuotationsManager.getRow( getContext(), query );
			
			newPurchase.setCustomer( temp.getCustomer() );
			newPurchase.setContact( temp.getContact() );
			newPurchase.setUser( temp.getUser() );
			
			newPurchase.setAttachments( temp.getAttachments() );
			newPurchase.setLines( temp.getLines() );
			newPurchase.setDeliveries( temp.getDeliveries() );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
	*/
	
	@Override
	public void refreshVisibleContent( boolean repage )
	{
		/*reloadQuotation();
		
		showQuotationDeliveries();
		showQuotationAttachments();
		
		configLines.refreshVisibleContent( repage );
		
		getModalParent().refreshVisibleContent( true );*/
	}
	
	@Override
	protected boolean editAfterNew()
	{
		return false;
	}

	@Override
	protected void doEditAfterNew()
	{
		new ModalNewPurchase( getContext(), OperationCRUD.OP_MODIFY, (Purchase)newPurchase, getModalParent() ).showModalWindow();
	}

	private void loadUserDefaults()
	{
		/*customer = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_CUSTOMER );
		validity = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_VALIDITY );
		delivery = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_DELIVERY );
		packaging = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_PACKAGING );
		warranty = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_WARRANTY );
		payment = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_PAYMENT );
		tax_rate = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_TAX_RATE );*/
	}

	private void saveUserDefaults()
	{
		/*IOCManager._UserDefaultsManager.setUserDefault( getContext(), customer, newPurchase.getRef_customer().toString() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), validity, newPurchase.getValidity().toString() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), delivery, newPurchase.getDelivery() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), packaging, newPurchase.getPackaging() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), warranty, newPurchase.getWarranty() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), payment, newPurchase.getPayment_terms() );
		if ( newPurchase.getTax_rate() != null )
			IOCManager._UserDefaultsManager.setUserDefault( getContext(), tax_rate, newPurchase.getTax_rate().toString() );*/
	}
	
	public void onPdfOrder()
	{
		/*if ( onModify() )
		{
			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshInvoicesTab();
			dashboard.refreshQuotationsTab();
			dashboard.refreshShipmentsTab();
		
			try
			{
				QuotationQuery queryQuotation = new QuotationQuery();
				queryQuotation.setId( newPurchase.getId() );
				Quotation quotation = (Quotation)IOCManager._QuotationsManager.getRow( getContext(), queryQuotation );

				long ts = CalendarUtils.getTodayAsLong( "UTC" );
				
				String pagesize = "A4";
				String template = newPurchase.getCustomer().getTaxable().booleanValue() ? "national-quotation-template" : "international-quotation-template";
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
		}*/
	}

	public void onDuplicate()
	{
		if ( IOCManager._PurchasesManager.duplicatePurchase( getContext(), newPurchase ) )
		{
			refreshVisibleContent( true );
			
			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshPurchasesTab();
			dashboard.refreshOperationsTab();

			Utils.showNotification( getContext(), getContext().getString( "modalNewPurchase.duplicateSuccess" ), Notification.Type.TRAY_NOTIFICATION );
		}
		else
			Utils.showNotification( getContext(), getContext().getString( "modalNewPurchase.duplicateError" ), Notification.Type.ERROR_MESSAGE );
	}

	
	public void onEmailOrder()
	{
		/*if ( onModify() )
		{
			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshInvoicesTab();
			dashboard.refreshQuotationsTab();
			dashboard.refreshShipmentsTab();
		
			try
			{
				QuotationQuery queryQuotation = new QuotationQuery();
				queryQuotation.setId( newPurchase.getId() );
				Quotation quotation = (Quotation)IOCManager._QuotationsManager.getRow( getContext(), queryQuotation );
			
				String template = quotation.getCustomer().getTaxable().booleanValue() ? "national-quotation-template" : "international-quotation-template";
				String name = "QT-" + quotation.getFormattedNumber() + "-" +  quotation.getReference_request() + ".pdf";
				
				PdfExportQuotation export = new PdfExportQuotation( quotation );
				
				export.setOrientation( newPurchase.getDeliveries().size() > 1 ? "landscape" : "portrait" );
				export.setPagesize( "A4" );
				export.setTemplate( template );
				
				AppContext ctx1 = new AppContext( quotation.getCustomer().getLanguage() );
				IOCManager._ParametersManager.loadParameters( ctx1 );
				ctx1.setUser( getContext().getUser() );
				ctx1.addData( "Url", getContext().getData( "Url" ) );
		    	ctx1.loadOwnerCompany();

				export.setContext( ctx1 );
			
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				export.doExport( os );
	
				List<Attachment> attachments = new ArrayList<Attachment>();
				attachments.add( new Attachment( name, "application/pdf", os.toByteArray() ) );
				
				String subject = ctx1.getString( "modalNewQuotation.emailSubject" ).replaceAll( "%name%", name );  
				String text = ctx1.getString( "modalNewQuotation.emailText" ).
						replaceAll( "%reference_request%", quotation.getReference_request() ).
						replaceAll( "%contact_person%", quotation.getContact().getName() );  
	
				String body = text + "\n\n" + ctx1.getCompanyDataAndLegal( newPurchase.getUser() ); 
	
				final SendEmailDlg dlg = new SendEmailDlg( getContext(), getContext().getString( "modalNewQuotation.emailTitle" ), attachments, quotation.getCustomer().getContacts() );
				dlg.setTo( quotation.getContact().getEmail() );
				dlg.setCopy( "" );
				dlg.setReply_to( quotation.getUser().getEmail() );
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
		}*/
	}

	@Override
	protected boolean hasDelete()
	{
		return getContext().hasRight( "configuration.purchases.delete" );
	}

	@Override
	public boolean checkAddRight()
	{
		return getContext().hasRight( "configuration.purchases.add" );
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.purchases.modify" );
	}

	private void onSelectedProvider()
	{
		newPurchase.setRef_contact( null );
		loadContacts();
		fillComboContacts();
	}

	@Override
	public OutputStream receiveUpload( String filename, String mimeType )
	{
		os = new ByteArrayOutputStream();
		fileName = filename;
		
		LOG.error( "upload file " + filename );
		return os;
	}

	@Override
	public void uploadSucceeded( SucceededEvent event )
	{
		if ( ((Integer)event.getUpload().getData()).equals( UPLOAD_INVOICE ) )
			newPurchase.setInvoice( os.toByteArray() );
		else if ( ((Integer)event.getUpload().getData()).equals( UPLOAD_QUOTATION ) )
			newPurchase.setQuotation( os.toByteArray() );
		else if ( ((Integer)event.getUpload().getData()).equals( UPLOAD_PAYMENT ) )
			newPurchase.setPayment( os.toByteArray() );
		
		Utils.showNotification( getContext(), getContext().getString( "modalNewPurchase.uploadMessage" ).replaceAll( "%filename%", fileName ), Notification.Type.TRAY_NOTIFICATION );		
		//labelAttachment.setCaption( fileName + " " + getContext().getString( "words.success" ) );
	}
	
	@Override
	public void uploadFailed( FailedEvent event )
	{
		LOG.error( "Upload Fail <><> File: " + event.getFilename() );
		
		os = null;
		//upload.setEnabled( true );
		//editMd5.setEnabled( false );
	}
	
	@Override
	public void updateProgress( long readBytes, long contentLength )
	{
		LOG.info( "upload " + readBytes + " of " + contentLength );
	}
	
	public void showPurchaseInvoice()
	{
		if ( onModify() )
		{
			try
			{
				long ts = CalendarUtils.getTodayAsLong( "UTC" );
				
				String timeout = "60";
				
				String extra = "ts=" + ts + 
								"&id=" + newPurchase.getId() + 
								"&timeout=" + timeout;
				
				String user = getContext().getUser().getLogin();
				String password = getContext().getUser().getPwd();
				
				String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
				String code = "code=" + Authorization.encrypt( extra, password ) ;
	
				LOG.debug( "extra " +  extra );
				
				String url = getContext().getData( "Url" ) + "/services/purchase-invoice" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
				
				String caption = getContext().getString( "modalNewPurchase.showQuotation" );
	
				ShowExternalUrlDlg dlg = new ShowExternalUrlDlg(); 
		
				dlg.setContext( getContext() );
				dlg.setUrl( url );
				dlg.setCaption( caption );
				dlg.createComponents();
				
				getUI().addWindow( dlg );
	
				//closeModalWindow( true, true );
			}
	
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}
		}
	}

	public void showPurchaseQuotation()
	{
		if ( onModify() )
		{
			try
			{
				long ts = CalendarUtils.getTodayAsLong( "UTC" );
				
				String timeout = "60";
				
				String extra = "ts=" + ts + 
								"&id=" + newPurchase.getId() + 
								"&timeout=" + timeout;
				
				String user = getContext().getUser().getLogin();
				String password = getContext().getUser().getPwd();
				
				String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
				String code = "code=" + Authorization.encrypt( extra, password ) ;
	
				LOG.debug( "extra " +  extra );
				
				String url = getContext().getData( "Url" ) + "/services/purchase-quotation" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
				
				String caption = getContext().getString( "modalNewPurchase.showQuotation" );
	
				ShowExternalUrlDlg dlg = new ShowExternalUrlDlg(); 
		
				dlg.setContext( getContext() );
				dlg.setUrl( url );
				dlg.setCaption( caption );
				dlg.createComponents();
				
				getUI().addWindow( dlg );
	
				//closeModalWindow( true, true );
			}
	
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}
		}
	}

	public void showPurchasePayment()
	{
		if ( onModify() )
		{
			try
			{
				long ts = CalendarUtils.getTodayAsLong( "UTC" );
				
				String timeout = "60";
				
				String extra = "ts=" + ts + 
								"&id=" + newPurchase.getId() + 
								"&timeout=" + timeout;
				
				String user = getContext().getUser().getLogin();
				String password = getContext().getUser().getPwd();
				
				String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
				String code = "code=" + Authorization.encrypt( extra, password ) ;
	
				LOG.debug( "extra " +  extra );
				
				String url = getContext().getData( "Url" ) + "/services/purchase-payment" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
				
				String caption = getContext().getString( "modalNewPurchase.showPayment" );
	
				ShowExternalUrlDlg dlg = new ShowExternalUrlDlg(); 
		
				dlg.setContext( getContext() );
				dlg.setUrl( url );
				dlg.setCaption( caption );
				dlg.createComponents();
				
				getUI().addWindow( dlg );
				
				//closeModalWindow( true, true );
			}
	
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}
		}
	}

	private void onAddProvider()
	{
		final ModalNewCompany modal = new ModalNewCompany( getContext(), OperationCRUD.OP_ADD, null, ModalNewPurchase.this );
		modal.setType( Company.TYPE_PROVIDER );
		modal.addCloseListener
		( 
			new Window.CloseListener() 
			{
				private static final long serialVersionUID = 6841622953032797314L;

				@Override
			    public void windowClose( CloseEvent e ) 
			    {
					loadProviders();
					fillComboProviders();
					
					selectNewProvider( modal.getNewCompany().getId() );
			    }
			}
		);
		
		modal.showModalWindow();
	}

	private void selectNewProvider( Long id )
	{
		if ( id != null )
		{
			for ( Company company : providers )
				if ( company.getId().equals( id ) )
					comboProviders.setValue( company.getId() );
		}
	}

	public void onPay()
	{
		if ( onModify() )
		{
			try
			{
				final Transaction transaction = new Transaction();
				transaction.setTransaction_type( Transaction.TYPE_PAYMENT );
				transaction.setTransaction_date( CalendarUtils.getTodayAsLong() );
				transaction.setRef_purchase( newPurchase.getId() );
				transaction.setAmount( newPurchase.getGrossPrice() - newPurchase.getPayed() );
				transaction.setDescription( newPurchase.getTitle() );

				final SelectPaymentDlg dlg = new SelectPaymentDlg( getContext(), getContext().getString( "modalNewPurchase.paymentTitle" ), transaction );
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
								newPurchase.setPayed( newPurchase.getPayed() + transaction.getAmount() );
								
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
	
				Utils.showNotification( getContext(), getContext().getString( "modalNewPurchase.payError" ), Notification.Type.ERROR_MESSAGE );
			}
		}
	}
}
