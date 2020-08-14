package es.pryades.erp.dashboard.tabs;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Utils;
import lombok.Getter;

public class ShipmentsTab extends DashboardTab 
{
	private static final long serialVersionUID = -1195472272493710552L;

	private static final Logger LOG = Logger.getLogger( ShipmentsTab.class );

	@Getter
	private ShipmentsTabContent shipmentsTable;

	private HorizontalLayout mainLayout;
	
	public ShipmentsTab( AppContext context )
	{
		super( context );
	}

	@Override
	public Component getTabContent()
	{
		mainLayout = new HorizontalLayout();
		
		mainLayout.setSizeFull();
		
		shipmentsTable = new ShipmentsTabContent( getContext() );
		shipmentsTable.setSizeFull();
		shipmentsTable.initializeComponent();
		
		mainLayout.addComponent( shipmentsTable );

		refreshContent();
		
		return mainLayout;
	}
	
	public void refreshContent()
	{
		try
		{
			shipmentsTable.refreshVisibleContent( true );
			
			setTitle( getContext().getString( "shipmentsTab.title" ) );
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
