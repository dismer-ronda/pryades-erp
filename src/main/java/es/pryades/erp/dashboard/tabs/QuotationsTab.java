package es.pryades.erp.dashboard.tabs;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Utils;
import lombok.Getter;

public class QuotationsTab extends DashboardTab 
{
	private static final long serialVersionUID = -3196111505532488984L;

	private static final Logger LOG = Logger.getLogger( QuotationsTab.class );

	@Getter
	private QuotationsTabContent quotationsTable;

	private HorizontalLayout mainLayout;
	
	public QuotationsTab( AppContext context )
	{
		super( context );
	}

	@Override
	public Component getTabContent()
	{
		mainLayout = new HorizontalLayout();
		
		mainLayout.setSizeFull();
		
		quotationsTable = new QuotationsTabContent( getContext() );
		quotationsTable.setSizeFull();
		quotationsTable.initializeComponent();
		
		mainLayout.addComponent( quotationsTable );

		refreshContent();
		
		return mainLayout;
	}
	
	public void refreshContent()
	{
		try
		{
			quotationsTable.refreshVisibleContent( true );
			
			setTitle( getContext().getString( "quotationsTab.title" ) );
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
