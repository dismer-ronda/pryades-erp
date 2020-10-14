package es.pryades.erp.dashboard.tabs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.Transaction;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.dto.query.TransactionQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.TransactionVto;
import es.pryades.erp.vto.controlers.TransactionControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class TransactionsTabContent extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = 9151147553455352570L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( TransactionsTabContent.class );

	private PopupDateField fromDateField;
	private PopupDateField toDateField;
	private Button bttnApply;
	
	private UserDefault default_from;
	private UserDefault default_to;

	public TransactionsTabContent( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "id" );
		setOrder( "desc" );

		loadUserDefaults();
	}

	@Override
	public String getResourceKey()
	{
		return "transactionsTab";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "transaction_date", "transaction_type", "account_name", "amount", "balance", "purchase_number", "invoice_number", "description", "source" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "transaction_date", "transaction_type", "account_name", "amount", "balance", "purchase_number", "invoice_number", "source" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new BaseTable( TransactionVto.class, this, getContext() );//, getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	public List<Component> getCustomOperations()
	{
		List<Component> ops = new ArrayList<Component>();
		
		Button bttnPdf = new Button();
		bttnPdf.setCaption( getContext().getString( "transactionsTab.list.pdf" ) );
		bttnPdf.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -3218524176600265294L;

			public void buttonClick( ClickEvent event )
			{
				onShowListPdf();
			}
		} );
		ops.add( bttnPdf );

		Button bttnXls = new Button();
		bttnXls.setCaption( getContext().getString( "transactionsTab.list.xls" ) );
		ops.add( bttnXls );
		
		FileDownloader fileDownloaderXls = new FileDownloader( getXlsResource() );
        fileDownloaderXls.setOverrideContentType( true );
        fileDownloaderXls.extend( bttnXls );

		return ops;
	}

	@Override
	public Component getQueryComponent()
	{
		fromDateField = new PopupDateField( getContext().getString( "words.from" ) );
		fromDateField.setResolution( Resolution.DAY );
		fromDateField.setDateFormat( "dd-MM-yyyy" );
		fromDateField.setWidth( "160px" );
		fromDateField.setValue( getDefaultDate( default_from.getData_value() ) );

		toDateField = new PopupDateField( getContext().getString( "words.to" ) );
		toDateField.setResolution( Resolution.DAY );
		toDateField.setDateFormat( "dd-MM-yyyy" );
		toDateField.setWidth( "160px" );
		toDateField.setValue( getDefaultDate( default_to.getData_value() ) );
		
		bttnApply = new Button( getContext().getString( "words.search" ) );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
		addButtonApplyFilterClickListener();

		HorizontalLayout rowQuery = new HorizontalLayout();
		rowQuery.setSpacing( true );
		rowQuery.addComponent( fromDateField );
		rowQuery.addComponent( toDateField );
		rowQuery.addComponent( bttnApply );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		TransactionQuery query = new TransactionQuery();
		
		query.setFrom_date( fromDateField.getValue() != null ? CalendarUtils.getDayAsLong( fromDateField.getValue() ) : null );
		query.setTo_date( toDateField.getValue() != null ? CalendarUtils.getDayAsLong( toDateField.getValue() ) : null );
		
		saveUserDefaults();

		return query;
	}

	@Override
	public void onOperationNew()
	{
		//new ModalNewTransaction( getContext(), OperationCRUD.OP_ADD, null, TransactionsTabContent.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		//new ModalNewInvoice( getContext(), OperationCRUD.OP_MODIFY, (Invoice)dto, TransactionsTabContent.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new TransactionControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Transaction();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._TransactionsManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
	}
	
	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.transactions.add" );
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
			private static final long serialVersionUID = -8959634811265682288L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				if ( CalendarUtils.checkValidPeriod( fromDateField.getValue(), toDateField.getValue() ) )
					refreshVisibleContent( true );
				else
					Utils.showNotification( getContext(), getContext().getString( "error.invalid.period" ), Notification.Type.ERROR_MESSAGE );
			}
		} );
	}

	private void loadUserDefaults()
	{
		default_from = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.TRANSACTIONS_FROM );
		default_to = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.TRANSACTIONS_TO );
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_from, fromDateField.getValue() != null ? Long.toString( CalendarUtils.getDayAsLong( fromDateField.getValue() ) ) : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_to, toDateField.getValue() != null ? Long.toString( CalendarUtils.getDayAsLong( toDateField.getValue() ) ) : null );
	}
	
	private Date getDefaultDate( String date )
	{
		try
		{
			return CalendarUtils.getServerCalendarFromDateLong( Long.parseLong( date ) ).getTime();
		}
		catch ( Throwable e )
		{
		}
		
		return null;
	}
	
	private void onShowListPdf()
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
			
			String caption = getContext().getString( "invoicesConfig.invoices" );

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
	
	private StreamResource getXlsResource() 
	{
		StreamResource res =
			new StreamResource( 
				new StreamSource() 
				{
					private static final long serialVersionUID = -4001495054474088591L;

					@Override
		            public InputStream getStream() 
		            {
						try
						{
							return new ByteArrayInputStream( IOCManager._TransactionsManager.exportListXls( getContext(), (TransactionQuery)getQueryObject() ) );
						}
						catch ( Throwable e )
						{
							e.printStackTrace();
						}
						
						return null;
		            }
		        }, 
		        Utils.getUUID() + ".xls" );
		
		res.setCacheTime( 0 );
			
		return res;
	}
}

