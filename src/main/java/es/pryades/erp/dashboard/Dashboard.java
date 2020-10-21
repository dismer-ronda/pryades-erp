package es.pryades.erp.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dashboard.tabs.DashboardTab;
import es.pryades.erp.dashboard.tabs.InvoicesTab;
import es.pryades.erp.dashboard.tabs.InvoicesTabContent;
import es.pryades.erp.dashboard.tabs.LogTab;
import es.pryades.erp.dashboard.tabs.OperationsTab;
import es.pryades.erp.dashboard.tabs.OperationsTabContent;
import es.pryades.erp.dashboard.tabs.PurchasesTab;
import es.pryades.erp.dashboard.tabs.PurchasesTabContent;
import es.pryades.erp.dashboard.tabs.QuotationsTab;
import es.pryades.erp.dashboard.tabs.QuotationsTabContent;
import es.pryades.erp.dashboard.tabs.ShipmentsTab;
import es.pryades.erp.dashboard.tabs.ShipmentsTabContent;
import es.pryades.erp.dashboard.tabs.TransactionsTab;
import es.pryades.erp.dashboard.tabs.TransactionsTabContent;

import com.vaadin.ui.VerticalLayout;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Dismer Ronda
 * 
 */
public class Dashboard extends VerticalLayout implements SelectedTabChangeListener
{
	private static final long serialVersionUID = -6455560039333566825L;

	private static final Logger LOG = Logger.getLogger( Dashboard.class );

	@Getter @Setter private AppContext context;
	
	private TabSheet tabsheet;

	private List<DashboardTab> tabs;

	private boolean refresh;
	
	private QuotationsTab quotationsTab;
	private InvoicesTab invoicesTab;
	private ShipmentsTab shipmentsTab;
	private OperationsTab operationsTab;
	private PurchasesTab purchasesTab;
	private TransactionsTab transactionsTab;

	public Dashboard( AppContext context ) 
	{
		this.context = context;
		
		tabs = new ArrayList<DashboardTab>();
		
		context.addData( "dashboard", this );
		
		refresh = false;
	}
	
	private void initComponents() throws BaseException
	{
		try
		{
			setSpacing( true );
			setSizeFull();
			
			addStyleName( "dashboard" );
			
			tabsheet = new TabSheet();
			
			tabsheet.setSizeFull();
			
			addComponent( tabsheet );

			setExpandRatio( tabsheet, 1.0f );

			createTabs();
			
			for ( DashboardTab tab : tabs )
			{
				tab.setSizeFull();
				
				tabsheet.addTab( tab );
				tabsheet.getTab( tab ).setCaption( tab.getTitle() );
			}
			
			tabsheet.addSelectedTabChangeListener( this );
			
			DashboardTab tab = (DashboardTab)tabsheet.getSelectedTab();
			if ( tab != null )
				selectTab( tab );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			
			if ( !(e instanceof BaseException) )
				throw new BaseException( e, LOG, BaseException.UNKNOWN );
			
			throw (BaseException)e;
		}
	}
	
	public void render() throws BaseException
	{
		this.initComponents();
	}
	
	public void refreshVisibleContent()
	{
		for ( DashboardTab tab : tabs )
		{
			tab.refreshContent();
			
			tabsheet.getTab( tab ).setCaption( tab.getTitle() );
		}
	}
	
	private void createLogTab()
	{
		LogTab tab = new LogTab( context );
		
		tab.initComponents();
	
		tabs.add( tab );
	}
	
	private void createQuotationsTab()
	{
		quotationsTab = new QuotationsTab( context );
		
		quotationsTab.initComponents();
		
		tabs.add( quotationsTab );
	}
	
	private void createInvoicesTab()
	{
		invoicesTab = new InvoicesTab( context );
		
		invoicesTab.initComponents();

		tabs.add( invoicesTab );
	}

	private void createShipmentsTab()
	{
		shipmentsTab = new ShipmentsTab( context );
		
		shipmentsTab.initComponents();
	
		tabs.add( shipmentsTab );
	}

	private void createOperationsTab()
	{
		operationsTab = new OperationsTab( context );
		
		operationsTab.initComponents();
	
		tabs.add( operationsTab );
	}

	private void createPurchasesTab()
	{
		purchasesTab = new PurchasesTab( context );
		
		purchasesTab.initComponents();
	
		tabs.add( purchasesTab );
	}

	private void createTransactionsTab()
	{
		transactionsTab = new TransactionsTab( context );
		
		transactionsTab.initComponents();
	
		tabs.add( transactionsTab );
	}

	private void createTabs()
	{
		/* && Utils.getEnviroment( "LOGFILE" ) != null*/

		if ( getContext().hasRight( "configuration.quotations" ) )
			createQuotationsTab();

		if ( getContext().hasRight( "configuration.purchases" ) )
			createPurchasesTab();
		
		if ( getContext().hasRight( "configuration.invoices" ) )
			createInvoicesTab();
		
		if ( getContext().hasRight( "configuration.shipments" ) )
			createShipmentsTab();
		
		if ( getContext().hasRight( "configuration.operations" ) )
			createOperationsTab();
		
		if ( getContext().hasRight( "configuration.transactions" ) )
			createTransactionsTab();
		
		if ( getContext().hasRight( "main.log" )  )
			createLogTab();
		
		getContext().addData( "dashboard", this );
	}

	private void selectTab( DashboardTab tab )
	{
		tab.defaultFocus();

		if ( refresh )
		{
			refreshVisibleContent();
			refresh = false;
		}
	}
	
	public void selectedTabChange( SelectedTabChangeEvent event )
	{
		Tab tab = tabsheet.getTab( event.getTabSheet().getSelectedTab() );
		
		if ( tab != null )
			selectTab( (DashboardTab)tab.getComponent() );
	}

	public void updateComponent()
	{
	}
	
	public void refreshQuotationsTab()
	{
		QuotationsTabContent content = quotationsTab.getQuotationsTable();
		if ( content != null )
			content.refreshVisibleContent( true );
	}

	public void refreshInvoicesTab()
	{
		InvoicesTabContent content = invoicesTab.getInvoicesTable();
		if ( content != null )
			content.refreshVisibleContent( true );
	}

	public void refreshShipmentsTab()
	{
		ShipmentsTabContent content = shipmentsTab.getShipmentsTable();
		if ( content != null )
			content.refreshVisibleContent( true );
	}
	
	public void refreshOperationsTab()
	{
		OperationsTabContent content = operationsTab.getOperationsTable();
		if ( content != null )
			content.refreshVisibleContent( true );
	}
	
	public void refreshPurchasesTab()
	{
		PurchasesTabContent content = purchasesTab.getPurchasesTable();
		if ( content != null )
			content.refreshVisibleContent( true );
	}
	
	public void refreshTransactionsTab()
	{
		TransactionsTabContent content = transactionsTab.getTransactionsTable();
		if ( content != null )
			content.refreshVisibleContent( true );
	}
	
	public void refreshCustomers()
	{
		QuotationsTabContent content1 = quotationsTab.getQuotationsTable();
		if ( content1 != null )
			content1.refreshCustomers();
		
		InvoicesTabContent content2 = invoicesTab.getInvoicesTable();
		if ( content2 != null )
			content2.refreshCustomers();
		
		ShipmentsTabContent content3 = shipmentsTab.getShipmentsTable();
		if ( content3 != null )
			content3.refreshCustomers();
	}

	public void refreshProviders()
	{
		PurchasesTabContent content4 = purchasesTab.getPurchasesTable();
		if ( content4 != null )
			content4.refreshProviders();
	}
	
	public void refreshOperations()
	{
		PurchasesTabContent content3 = purchasesTab.getPurchasesTable();
		if ( content3 != null )
			content3.refreshOperations();
	}
	
	public void refreshAccounts()
	{
		TransactionsTabContent content = transactionsTab.getTransactionsTable();
		if ( content != null )
			content.refreshAccounts();
	}

}
