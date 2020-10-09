package es.pryades.erp.dashboard.tabs;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Utils;
import lombok.Getter;

public class PurchasesTab extends DashboardTab 
{
	private static final long serialVersionUID = 9072153457569757795L;

	private static final Logger LOG = Logger.getLogger( PurchasesTab.class );

	@Getter
	private PurchasesTabContent purchasesTable;

	private HorizontalLayout mainLayout;
	
	public PurchasesTab( AppContext context )
	{
		super( context );
	}

	@Override
	public Component getTabContent()
	{
		mainLayout = new HorizontalLayout();
		
		mainLayout.setSizeFull();
		
		purchasesTable = new PurchasesTabContent( getContext() );
		purchasesTable.setSizeFull();
		purchasesTable.initializeComponent();
		
		mainLayout.addComponent( purchasesTable );

		refreshContent();
		
		return mainLayout;
	}
	
	public void refreshContent()
	{
		try
		{
			purchasesTable.refreshVisibleContent( true );
			
			setTitle( getContext().getString( "purchasesTab.title" ) );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
	
	@Override
	public void defaultFocus()
	{
	}
}
