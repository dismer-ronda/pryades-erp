package es.pryades.erp.dashboard.tabs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
import es.pryades.erp.configuration.modals.ModalNewOperation;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Operation;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.Quotation;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.dto.query.OperationQuery;
import es.pryades.erp.dto.query.QuotationQuery;
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

	private ComboBox comboQuotations;
	private ComboBox comboStatus;
	private TextField editTitle;

	private Button bttnApply;

	private Label labelTotalPrice;

	private List<Quotation> quotations;

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
		return new String[]{ "status", "title", "customer_name", "quotation_number", "predicted_cost", "real_cost", "total_invoiced", "profit" };
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
		
		/*Button bttnPdf = new Button();
		bttnPdf.setCaption( getContext().getString( "operationsTab.pdf" ) );
		bttnPdf.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 8302552775699479003L;

			public void buttonClick( ClickEvent event )
			{
				onShowPdf();
			}
		} );

		ops.add( bttnPdf );

		HorizontalLayout rowTotals = new HorizontalLayout();
		rowTotals.setWidth( "100%" );
		rowTotals.setSpacing( true );
		rowTotals.setMargin( new MarginInfo( false, true, false, true ) );
		
		labelTotalPrice = new Label();
		labelTotalPrice.setWidth( "300px" );
		labelTotalPrice.addStyleName( "green" );
		
		rowTotals.addComponent( labelTotalPrice );

		ops.add( rowTotals );*/
		
		return ops;
	}

	@Override
	public Component getQueryComponent()
	{
		loadQuotations();

		comboQuotations = new ComboBox(getContext().getString( "operationsTab.comboQuotation" ));
		comboQuotations.setWidth( "100%" );
		comboQuotations.setNullSelectionAllowed( true );
		comboQuotations.setTextInputAllowed( true );
		comboQuotations.setImmediate( true );
		fillComboCustomers();
		comboQuotations.setValue( getDefaultCustomer() );
		comboQuotations.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -3155362332814804334L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});

		comboStatus = new ComboBox(getContext().getString( "operationsTab.comboStatus" ));
		comboStatus.setWidth( "100%" );
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
		editTitle.setWidth( "100%" );
		editTitle.setNullRepresentation( "" );
		editTitle.setValue( default_title.getData_value() );
		
		bttnApply = new Button( getContext().getString( "words.search" ) );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
		addButtonApplyFilterClickListener();

		HorizontalLayout rowQuery = new HorizontalLayout();
		rowQuery.setSpacing( true );
		rowQuery.addComponent( comboQuotations );
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
		
		if ( comboQuotations.getValue() != null )
			query.setRef_quotation( (Long)comboQuotations.getValue() );
		
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
		/*double totalPrice = 0;
		
		for ( BaseDto row : rows )
		{
			Invoice quotation = (Invoice)row;
			
			totalPrice += quotation.getGrandTotalInvoice();
		}
		
		labelTotalPrice.setValue(  getContext().getString( "invoicesConfig.totalPrice" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalPrice ) ) );*/
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
			private static final long serialVersionUID = 6308412745183922396L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				refreshVisibleContent( true );
			}
		} );
	}

	@SuppressWarnings("unchecked")
	private void loadQuotations()
	{
		try
		{
			QuotationQuery query = new QuotationQuery();
			query.setRef_user( getContext().getUser().getId() );

			quotations = IOCManager._QuotationsManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			quotations = new ArrayList<Quotation>();
		}
	}
	
	private void fillComboCustomers()
	{
		comboQuotations.removeAllItems();
		for ( Quotation quotation : quotations )
		{
			comboQuotations.addItem( quotation.getId() );
			comboQuotations.setItemCaption( quotation.getId(), quotation.getTitle() );
		}
	}

	public void refreshCustomers()
	{
		loadQuotations();
		fillComboCustomers();
	}

	private void loadUserDefaults()
	{
		default_quotation = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.OPERATIONS_QUOTATION );
		default_status = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.OPERATIONS_STATUS );
		default_title = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.OPERATIONS_TITLE );
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_quotation, comboQuotations.getValue() != null ? comboQuotations.getValue().toString() : null );
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

}

