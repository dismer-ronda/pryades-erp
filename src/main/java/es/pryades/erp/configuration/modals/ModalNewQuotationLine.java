package es.pryades.erp.configuration.modals;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dashboard.Dashboard;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Operation;
import es.pryades.erp.dto.Purchase;
import es.pryades.erp.dto.Quotation;
import es.pryades.erp.dto.QuotationDelivery;
import es.pryades.erp.dto.QuotationLine;
import es.pryades.erp.dto.QuotationLineDelivery;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.dto.query.OperationQuery;
import es.pryades.erp.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewQuotationLine extends ModalWindowsCRUD implements ModalParent
{
	private static final long serialVersionUID = -4249686173355306766L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalNewQuotationLine.class );

	protected QuotationLine newQuotation;

	private List<Company> providers;

	private TextField editLine_order;
	private TextField editOrigin;
	private TextArea editReference;
	private TextField editTitle;
	private TextArea editDescription;
	private TextField editCost;
	private TextField editMargin;
	private TextField editTax_rate;
	private List<TextField> editsDeliveries;
	private ComboBox comboProviders;
	private Button btnAdd;

	private UserDefault defaultMargin;
	private UserDefault defaultLine_order;
	private UserDefault defaultProvider;
	private UserDefault defaultTax_rate;
	private UserDefault defaultOrigin;

	/**
	 * editReference_order
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewQuotationLine( AppContext context, OperationCRUD modalOperation, QuotationLine orgParameter, ModalParent parentWindow )
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
			newQuotation = (QuotationLine) Utils.clone( (QuotationLine) orgDto );
		}
		catch ( Throwable e1 )
		{
			newQuotation = new QuotationLine();
			newQuotation.setRef_quotation( ((ModalNewQuotation)getModalParent()).getNewQuotation().getId() );
			newQuotation.setLine_deliveries( new ArrayList<QuotationLineDelivery>() );
			newQuotation.setMargin( Utils.getDouble( defaultMargin.getData_value(), 0.0 ) );
			newQuotation.setLine_order( Utils.getInt( defaultLine_order.getData_value(), 1 ) );
			if ( Utils.getLong( defaultProvider.getData_value(), 0 ) != 0)
				newQuotation.setRef_provider( Utils.getLong( defaultProvider.getData_value(), 0 ) );
			newQuotation.setTax_rate( Utils.getDouble( defaultTax_rate.getData_value(), 0 ) );
			newQuotation.setOrigin( defaultOrigin.getData_value() );
		}

		bi = new BeanItem<BaseDto>( newQuotation );

		loadProviders();

		editLine_order = new TextField( getContext().getString( "modalNewQuotationLine.editLine_order" ), bi.getItemProperty( "line_order" ) );
		editLine_order.setWidth( "100%" );
		editLine_order.setNullRepresentation( "" );
		
		editOrigin = new TextField( getContext().getString( "modalNewQuotationLine.editOrigin" ), bi.getItemProperty( "origin" ) );
		editOrigin.setWidth( "100%" );
		editOrigin.setNullRepresentation( "" );
		
		editReference = new TextArea( getContext().getString( "modalNewQuotationLine.editReference" ), bi.getItemProperty( "reference" ) );
		editReference.setWidth( "100%" );
		editReference.setNullRepresentation( "" );
		
		editTitle = new TextField( getContext().getString( "modalNewQuotationLine.editTitle" ), bi.getItemProperty( "title" ) );
		editTitle.setWidth( "100%" );
		editTitle.setNullRepresentation( "" );
		
		editDescription = new TextArea( getContext().getString( "modalNewQuotationLine.editDescription" ), bi.getItemProperty( "description" ) );
		editDescription.setWidth( "100%" );
		editDescription.setNullRepresentation( "" );
		
		editCost = new TextField( getContext().getString( "modalNewQuotationLine.editCost" ), bi.getItemProperty( "cost" ) );
		editCost.setWidth( "100%" );
		editCost.setNullRepresentation( "" );
		
		editMargin = new TextField( getContext().getString( "modalNewQuotationLine.editMargin" ), bi.getItemProperty( "margin" ) );
		editMargin.setWidth( "100%" );
		editMargin.setNullRepresentation( "" );
		
		editTax_rate = new TextField( getContext().getString( "modalNewQuotationLine.editTax_rate" ), bi.getItemProperty( "tax_rate" ) );
		editTax_rate.setWidth( "100%" );
		editTax_rate.setNullRepresentation( "" );
		
		comboProviders = new ComboBox(getContext().getString( "modalNewQuotationLine.comboProviders" ));
		comboProviders.setWidth( "100%" );
		comboProviders.setNullSelectionAllowed( true );
		comboProviders.setTextInputAllowed( true );
		comboProviders.setImmediate( true );
		fillComboProviders();
		comboProviders.setPropertyDataSource( bi.getItemProperty( "ref_provider" ) );
		
		btnAdd = new Button(" + ");
		btnAdd.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -4086263529976395213L;

			public void buttonClick( ClickEvent event )
			{
				onAddProvider();
			}
		} );
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( editLine_order );
		row1.addComponent( editOrigin );
		row1.addComponent( editTitle );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( editReference );
		row2.addComponent( editDescription );

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( editCost );
		row3.addComponent( editTax_rate );
		row3.addComponent( editMargin );
		row3.addComponent( comboProviders );
		row3.addComponent( btnAdd );
		row3.setComponentAlignment( btnAdd, Alignment.BOTTOM_LEFT );

		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		componentsContainer.addComponent( row3 );
		
		List<QuotationDelivery> deliveries = ((ModalNewQuotation)getModalParent()).getNewQuotation().getDeliveries();
		
		editsDeliveries = new ArrayList<TextField>();
		
		if ( deliveries != null && deliveries.size() > 0 )
		{
			HorizontalLayout row5 = new HorizontalLayout();
			row5.setWidth( "100%" );
			row5.setSpacing( true );
			row5.setMargin( true );
			row5.setCaption( getContext().getString( "modalNewQuotationLine.deliveries" ) );
			
			for ( QuotationDelivery delivery : deliveries )
			{
				TextField tmp = new TextField( CalendarUtils.getDateFromLongAsString( delivery.getDeparture_date(), "dd-MM-yyyy" ) );
				tmp.setWidth( "100%" );
				tmp.setNullRepresentation( "0" );
				tmp.setData( delivery );
				tmp.setRequired( true );
				tmp.setValue( Integer.toString( getDeliveryValue( delivery ) ) );
				
				row5.addComponent( tmp );
				
				if ( ((ModalNewQuotation)getModalParent()).getNewQuotation().getStatus().equals( Quotation.STATUS_APPROVED ) )
				{
					Button btnPurchase = new Button( getContext().getString( "modalNewQuotationLine.btnPurchase" ) );
					btnPurchase.setData( tmp );
					btnPurchase.addClickListener( new Button.ClickListener()
					{
						private static final long serialVersionUID = 3735634355355625038L;

						public void buttonClick( ClickEvent event )
						{
							onPurchase( (TextField)event.getButton().getData() );
						}
					} );
					
					row5.addComponent( btnPurchase );
					row5.setComponentAlignment( btnPurchase, Alignment.BOTTOM_LEFT );
					row5.setExpandRatio( tmp, 1.0f );
				}

				editsDeliveries.add( tmp );
			}

			componentsContainer.addComponent( row5 );
		}
	}	
	
	private Operation getQuotationOperation()
	{
		try
		{
			OperationQuery query = new OperationQuery();
			query.setRef_quotation( ((ModalNewQuotation)getModalParent()).getNewQuotation().getId() );
			
			return (Operation)IOCManager._OperationsManager.getRow( getContext(), query );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return null;
	}
	
	private void onCreateOperation()
	{
		try
		{
			Operation operation = new Operation();

			operation.setRef_quotation( ((ModalNewQuotation)getModalParent()).getNewQuotation().getId() );
			operation.setTitle( ((ModalNewQuotation)getModalParent()).getNewQuotation().getTitle() );
			operation.setStatus( Operation.STATUS_EXCECUTION );

			IOCManager._OperationsManager.setRow( getContext(), null, operation );

			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshOperationsTab();
			dashboard.refreshOperations();

		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
			Utils.logException( e, LOG );
		}
	}
	
	private void onPurchase( TextField edit )
	{
		Operation operation = getQuotationOperation();
		
		if ( operation == null )
		{
			ConfirmDialog.show( (UI)getContext().getData( "Application" ), getContext().getString( "modalNewQuotationLine.operationNotFound" ),
		        new ConfirmDialog.Listener() 
				{
					private static final long serialVersionUID = -2451737407467323503L;

					public void onClose(ConfirmDialog dialog) 
		            {
		                if ( dialog.isConfirmed() ) 
							onCreateOperation();
		            }
		        });
			
			//Utils.showNotification( getContext(), getContext().getString( "modalNewQuotationLine.operationNotFound" ), Notification.Type.ERROR_MESSAGE );
		}
		else
		{
			try
			{
				Purchase purchase = new Purchase();
				
				double cost = newQuotation.getCost() * Utils.getInt( edit.getValue(), 0 );
				
				purchase.setRef_buyer( ((ModalNewQuotation)getModalParent()).getNewQuotation().getRef_user() );
				purchase.setPurchase_date( CalendarUtils.getTodayAsLong() );
				purchase.setRegister_date( CalendarUtils.getTodayAsLong() );
				purchase.setPurchase_type( Purchase.TYPE_SELL );
				purchase.setStatus( Purchase.STATUS_CREATED );
				purchase.setRef_buyer( getContext().getUser().getId() );
				purchase.setPayed( 0.0 );
				purchase.setNet_tax( 0.0 );
				purchase.setRef_provider( newQuotation.getRef_provider() );
				purchase.setNet_price( cost );
				purchase.setNet_tax( cost * newQuotation.getTax_rate() / 100 );
				purchase.setNet_retention( 0.0 );
				purchase.setTitle( newQuotation.getTitle() );
				purchase.setDescription( newQuotation.getDescription() );
				purchase.setRef_operation( getQuotationOperation().getId() );
		
				IOCManager._PurchasesManager.setRow( getContext(), null, purchase );
				
				Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
				dashboard.refreshPurchasesTab();

				Utils.showNotification( getContext(), getContext().getString( "modalNewQuotationLine.purchaseOk" ), Notification.Type.TRAY_NOTIFICATION );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
				
				Utils.showNotification( getContext(), getContext().getString( "modalNewQuotationLine.purchaseError" ), Notification.Type.ERROR_MESSAGE );
			}
		}
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewQuotationLine";
	}

	@Override
	protected void defaultFocus()
	{
		editReference.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newQuotation.setId( null );
			
			IOCManager._QuotationsLinesManager.setRow( getContext(), null, newQuotation );
			
			saveUserDefaults();

			for ( TextField edit : editsDeliveries )
			{
				QuotationLineDelivery lineDelivery = new QuotationLineDelivery();

				lineDelivery.setRef_quotation_delivery( ((QuotationDelivery)edit.getData()).getId() );
				lineDelivery.setRef_quotation_line( newQuotation.getId() );
				lineDelivery.setQuantity( Integer.valueOf( edit.getValue() ) );

				IOCManager._QuotationsLinesDeliveriesManager.delRow( getContext(), lineDelivery );

				if ( lineDelivery.getQuantity() > 0 )
					IOCManager._QuotationsLinesDeliveriesManager.setRow( getContext(), null, lineDelivery );
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

	private int getDeliveryValue( QuotationDelivery delivery )
	{
		for ( QuotationLineDelivery lineDelivery : newQuotation.getLine_deliveries() )
		{
			if ( lineDelivery.getRef_quotation_delivery().equals( delivery.getId() ) )
				return lineDelivery.getQuantity();
		}
		
		return 0;
	}
	
	@Override
	protected boolean onModify()
	{
		try
		{
			IOCManager._QuotationsLinesManager.setRow( getContext(), (QuotationLine) orgDto, newQuotation );
			
			saveUserDefaults();

			for ( TextField edit : editsDeliveries )
			{
				QuotationLineDelivery lineDelivery = new QuotationLineDelivery();

				lineDelivery.setRef_quotation_delivery( ((QuotationDelivery)edit.getData()).getId() );
				lineDelivery.setRef_quotation_line( newQuotation.getId() );
				lineDelivery.setQuantity( Utils.getInt( edit.getValue(), 0 ) );

				deleteOldQuantity( lineDelivery );
				
				if ( lineDelivery.getQuantity() > 0 )
					IOCManager._QuotationsLinesDeliveriesManager.setRow( getContext(), null, lineDelivery );
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

	private void deleteOldQuantity( QuotationLineDelivery lineDelivery )
	{
		try
		{
			IOCManager._QuotationsLinesDeliveriesManager.delRow( getContext(), lineDelivery );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
		}
	}
	
	@Override
	protected boolean onDelete()
	{
		try
		{
			IOCManager._QuotationsLinesManager.delRow( getContext(), newQuotation );

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	private void loadUserDefaults()
	{
		defaultMargin = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_LINE_MARGIN );
		defaultLine_order = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_LINE_ORDER );
		defaultProvider = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_LINE_PROVIDER );
		defaultTax_rate = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_LINE_TAX_RATE );
		defaultOrigin = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_LINE_ORIGIN );
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defaultMargin, newQuotation.getMargin().toString() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defaultLine_order, Integer.toString( newQuotation.getLine_order() + 1 ) );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defaultTax_rate, Double.toString( newQuotation.getTax_rate() ) );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defaultProvider, Long.toString( newQuotation.getRef_provider() != null ? newQuotation.getRef_provider() : 0 ) );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defaultOrigin, newQuotation.getOrigin() );
	}

	@SuppressWarnings("unchecked")
	private void loadProviders()
	{
		try
		{
			CompanyQuery query = new CompanyQuery();
			query.setType_company( Company.TYPE_PROVIDER );
			//query.setRef_user( getContext().getUser().getId() );

			providers = IOCManager._CompaniesManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			providers= new ArrayList<Company>();
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
	
	private void onAddProvider()
	{
		ModalNewCompany modal = new ModalNewCompany( getContext(), OperationCRUD.OP_ADD, null, ModalNewQuotationLine.this );
		modal.setType( Company.TYPE_PROVIDER );
		
		modal.showModalWindow();
	}

	@Override
	public void refreshVisibleContent( boolean repage )
	{
		loadProviders();
		fillComboProviders();
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
