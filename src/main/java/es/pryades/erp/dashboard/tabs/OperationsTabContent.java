package es.pryades.erp.dashboard.tabs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD.OperationCRUD;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.common.Utils;
import es.pryades.erp.configuration.modals.ModalNewOperation;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Operation;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.dto.query.OperationQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.OperationVto;
import es.pryades.erp.vto.controlers.OperationControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class OperationsTabContent extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -4451055479050491492L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( OperationsTabContent.class );

	private ComboBox comboCustomers;
	private ComboBox comboStatus;
	private TextField editTitle;

	private Button bttnApply;

	private Label labelTotalCost;
	private Label labelTotalPrice;
	private Label labelTotalMargin;

	private List<Company> customers;

	private UserDefault default_quotation;
	private UserDefault default_status;
	private UserDefault default_title;

	public OperationsTabContent( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "id" );
		setOrder( "desc" );

		loadUserDefaults();
	}

	@Override
	public String getResourceKey()
	{
		return "operationsTab";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "status", "title", "customer_name", "quotation_number", "predicted_cost", "real_cost", "price", "profit" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "status", "title", "customer_name", "quotation_title" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new BaseTable( OperationVto.class, this, getContext() );
	}

	public List<Component> getCustomOperations()
	{
		List<Component> ops = new ArrayList<Component>();
		
		HorizontalLayout rowTotals = new HorizontalLayout();
		rowTotals.setWidth( "100%" );
		rowTotals.setSpacing( true );
		rowTotals.setMargin( new MarginInfo( false, true, false, true ) );
		
		labelTotalCost = new Label();
		labelTotalCost.setWidth( "300px" );
		labelTotalPrice = new Label();
		labelTotalPrice.setWidth( "300px" );
		labelTotalMargin = new Label();
		labelTotalMargin.setWidth( "300px" );
		labelTotalMargin.addStyleName( "green" );
		
		rowTotals.addComponent( labelTotalCost );
		rowTotals.addComponent( labelTotalPrice );
		rowTotals.addComponent( labelTotalMargin );

		ops.add( rowTotals );
		
		return ops;
	}

	@Override
	public Component getQueryComponent()
	{
		loadCustomers();

		comboCustomers = new ComboBox(getContext().getString( "operationsTab.comboCustomer" ));
		comboCustomers.setWidth( "200px" );
		comboCustomers.setNullSelectionAllowed( true );
		comboCustomers.setTextInputAllowed( true );
		comboCustomers.setImmediate( true );
		fillComboCustomers();
		comboCustomers.setValue( getDefaultCustomer() );
		comboCustomers.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -5463401325801238863L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});

		comboStatus = new ComboBox(getContext().getString( "operationsTab.comboStatus" ));
		comboStatus.setWidth( "160px" );
		comboStatus.setNullSelectionAllowed( true );
		comboStatus.setTextInputAllowed( true );
		comboStatus.setImmediate( true );
		fillComboStatus();
		comboStatus.setValue( getDefaultStatus() );
		comboStatus.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -4409929584803801172L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});

		editTitle = new TextField( getContext().getString( "operationsTab.editTitle" ) );
		editTitle.setWidth( "200px" );
		editTitle.setNullRepresentation( "" );
		editTitle.setValue( default_title.getData_value() );
		
		bttnApply = new Button( getContext().getString( "words.search" ) );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
		addButtonApplyFilterClickListener();

		HorizontalLayout rowQuery = new HorizontalLayout();
		rowQuery.setSpacing( true );
		rowQuery.addComponent( comboCustomers );
		rowQuery.addComponent( comboStatus );
		rowQuery.addComponent( editTitle );
		rowQuery.addComponent( bttnApply );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		OperationQuery query = new OperationQuery();
		
		if ( !editTitle.getValue().isEmpty() ) 
			query.setTitle( "%" + editTitle.getValue() + "%");
		
		if ( comboCustomers.getValue() != null )
			query.setRef_customer( (Long)comboCustomers.getValue() );
		
		if ( comboStatus.getValue() != null )
			query.setStatus( (Integer)comboStatus.getValue() );
		
		//query.setRef_user( getContext().getUser().getId() );
		
		saveUserDefaults();

		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewOperation( getContext(), OperationCRUD.OP_ADD, null, OperationsTabContent.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewOperation( getContext(), OperationCRUD.OP_MODIFY, (Operation)dto, OperationsTabContent.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new OperationControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Operation();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._OperationsManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
		double totalCost = 0;
		double totalPrice = 0;
		double totalMargin = 0;
		
		for ( BaseDto row : rows )
		{
			Operation quotation = (Operation)row;
			
			double cost = quotation.getTotalPurchased();
			double price = quotation.getTotalSold();
			
			totalCost += cost;
			totalPrice += price;
			totalMargin += price - cost;
		}
		
		labelTotalCost.setValue(  getContext().getString( "quotationsConfig.totalCost" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalCost ) ) );
		labelTotalPrice.setValue(  getContext().getString( "quotationsConfig.totalPrice" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalPrice ) ) );
		labelTotalMargin.setValue(  getContext().getString( "quotationsConfig.totalMargin" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalMargin ) ) );
	}
	
	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.operations.add" );
	}

	@Override
	public void updateComponent()
	{
	}

	@Override
	public void onFieldEvent( Component component, String column )
	{
	}

	public void addButtonApplyFilterClickListener()
	{
		bttnApply.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 7428847307624807218L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				refreshVisibleContent( true );
			}
		} );
	}

	private void loadUserDefaults()
	{
		default_quotation = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.OPERATIONS_CUSTOMER );
		default_status = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.OPERATIONS_STATUS );
		default_title = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.OPERATIONS_TITLE );
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_quotation, comboCustomers.getValue() != null ? comboCustomers.getValue().toString() : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_status, comboStatus.getValue() != null ? comboStatus.getValue().toString() : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_title, editTitle.getValue() );
	}
	
	private Long getDefaultCustomer() 
	{
		try
		{
			return Long.parseLong( default_quotation.getData_value() );
		}
		catch ( Throwable e )
		{
		}
		
		return null;
	}
	
	private Integer getDefaultStatus() 
	{
		try
		{
			return Integer.parseInt( default_status.getData_value() );
		}
		catch ( Throwable e )
		{
		}
		
		return null;
	}
	
	private void onShowPdf()
	{
		/*try
		{
			long ts = CalendarUtils.getTodayAsLong( "UTC" );
			
			String pagesize = "A4";
			String template = "list-invoices-template";
			String timeout = "0";
			
			InvoiceQuery query = (InvoiceQuery)getQueryObject();
			
			String extra = "ts=" + ts + 
					"&query=" + Utils.getUrlEncoded( Utils.toJson( query ) ) + 
					"&pagesize=" + pagesize + 
					"&language=" + getContext().getLanguage() +
					"&template=" + template +
					"&name=" + getContext().getString( "invoicesConfig.invoices" ) + "-" + query.getPeriodToString() +
					"&url=" + getContext().getData( "Url" ) +
					"&timeout=" + timeout;
			
			String user = getContext().getUser().getLogin();
			String password = getContext().getUser().getPwd();
			
			String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
			String code = "code=" + Authorization.encrypt( extra, password ) ;

			String url = getContext().getData( "Url" ) + "/services/invoices" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
			
			String caption = getContext().getString( "invoicesConfig.list" );

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
		}*/
	}

	private void fillComboStatus()
	{
		comboStatus.addItem( Operation.STATUS_EXCECUTION );
		comboStatus.setItemCaption( Operation.STATUS_EXCECUTION, getContext().getString( "operation.status." + Operation.STATUS_EXCECUTION ) );
		
		comboStatus.addItem( Operation.STATUS_FINISHED );
		comboStatus.setItemCaption( Operation.STATUS_FINISHED, getContext().getString( "operation.status." + Operation.STATUS_FINISHED ) );

		comboStatus.addItem( Operation.STATUS_CLOSED );
		comboStatus.setItemCaption( Operation.STATUS_CLOSED, getContext().getString( "operation.status." + Operation.STATUS_CLOSED ) );
	}

	@SuppressWarnings("unchecked")
	private void loadCustomers()
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
	
	private void fillComboCustomers()
	{
		comboCustomers.removeAllItems();
		for ( Company company : customers )
		{
			comboCustomers.addItem( company.getId() );
			comboCustomers.setItemCaption( company.getId(), company.getAlias() );
		}
	}
}

