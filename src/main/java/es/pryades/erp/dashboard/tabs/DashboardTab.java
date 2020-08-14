package es.pryades.erp.dashboard.tabs;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import es.pryades.erp.common.AppContext;
import lombok.Getter;
import lombok.Setter;

public abstract class DashboardTab extends HorizontalLayout 
{
	private static final long serialVersionUID = 5481448092458334140L;

	@Getter	private AppContext context;
	
	@Getter @Setter	private String title;
	
	private VerticalLayout mainLayout;
		
	public DashboardTab( AppContext context )
	{
		this.context = context;
		
		setSizeFull();
	}
	
	public void initComponents()
	{
		mainLayout = new VerticalLayout();
		
		mainLayout.addStyleName( "indigo_widget" );
		mainLayout.setSizeFull();
		mainLayout.setSpacing( true );
		
		Component component = getTabContent();
		
		if ( component != null )
			mainLayout.addComponent( component );

		addComponent( mainLayout) ;
	}
	
	public abstract Component getTabContent();
	public abstract void defaultFocus();
	public abstract void refreshContent();
}
