package es.pryades.erp.dashboard.tabs;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Utils;
import lombok.Getter;

public class OperationsTab extends DashboardTab 
{
	private static final long serialVersionUID = -7054664642837612277L;

	private static final Logger LOG = Logger.getLogger( OperationsTab.class );

	@Getter
	private OperationsTabContent operationsTable;

	private HorizontalLayout mainLayout;
	
	public OperationsTab( AppContext context )
	{
		super( context );
	}

	@Override
	public Component getTabContent()
	{
		mainLayout = new HorizontalLayout();
		
		mainLayout.setSizeFull();
		
		operationsTable = new OperationsTabContent( getContext() );
		operationsTable.setSizeFull();
		operationsTable.initializeComponent();
		
		mainLayout.addComponent( operationsTable );

		refreshContent();
		
		return mainLayout;
	}
	
	public void refreshContent()
	{
		try
		{
			operationsTable.refreshVisibleContent( true );
			
			setTitle( getContext().getString( "operationsTab.title" ) );
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
