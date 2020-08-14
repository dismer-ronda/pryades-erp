package es.pryades.erp.dashboard.tabs;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Utils;
import lombok.Getter;

public class InvoicesTab extends DashboardTab 
{
	private static final long serialVersionUID = -479537628376932471L;

	private static final Logger LOG = Logger.getLogger( InvoicesTab.class );

	@Getter
	private InvoicesTabContent invoicesTable;

	private HorizontalLayout mainLayout;
	
	public InvoicesTab( AppContext context )
	{
		super( context );
	}

	@Override
	public Component getTabContent()
	{
		mainLayout = new HorizontalLayout();
		
		mainLayout.setSizeFull();
		
		invoicesTable = new InvoicesTabContent( getContext() );
		invoicesTable.setSizeFull();
		invoicesTable.initializeComponent();
		
		mainLayout.addComponent( invoicesTable );

		refreshContent();
		
		return mainLayout;
	}
	
	public void refreshContent()
	{
		try
		{
			invoicesTable.refreshVisibleContent( true );
			
			setTitle( getContext().getString( "invoicesTab.title" ) );
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
