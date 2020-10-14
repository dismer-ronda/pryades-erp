package es.pryades.erp.dashboard.tabs;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Utils;
import lombok.Getter;

public class TransactionsTab extends DashboardTab 
{
	private static final long serialVersionUID = -3069056079070085578L;

	private static final Logger LOG = Logger.getLogger( TransactionsTab.class );

	@Getter
	private TransactionsTabContent transactionsTable;

	private HorizontalLayout mainLayout;
	
	public TransactionsTab( AppContext context )
	{
		super( context );
	}

	@Override
	public Component getTabContent()
	{
		mainLayout = new HorizontalLayout();
		
		mainLayout.setSizeFull();
		
		transactionsTable = new TransactionsTabContent( getContext() );
		transactionsTable.setSizeFull();
		transactionsTable.initializeComponent();
		
		mainLayout.addComponent( transactionsTable );

		refreshContent();
		
		return mainLayout;
	}
	
	public void refreshContent()
	{
		try
		{
			transactionsTable.refreshVisibleContent( true );
			
			setTitle( getContext().getString( "transactionsTab.title" ) );
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
