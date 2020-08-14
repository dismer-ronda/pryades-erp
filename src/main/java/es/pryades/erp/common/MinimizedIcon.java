package es.pryades.erp.common;

import lombok.Getter;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class MinimizedIcon extends HorizontalLayout 
{
	private static final long serialVersionUID = -6494050268351722913L;

	@Getter
	private AppContext context;
	
	private VerticalLayout mainLayout;
	private String title;
	private String icon;
		
	public MinimizedIcon( AppContext context, String title, String icon )
	{
		this.context = context;
		this.title = title;
		this.icon = icon;
		
		setWidth("-1px");
		setHeight("-1px");
		
		setStyleName("centercard");
		
		mainLayout = new VerticalLayout();
		
		mainLayout.setWidth("200px");
		
		mainLayout.setStyleName( "centercard" );
		
		Component component = getTitleLabel();
		mainLayout.addComponent( component );
		mainLayout.setComponentAlignment( component, Alignment.MIDDLE_CENTER );

		component = getIconImage();
		mainLayout.addComponent( component );
		mainLayout.setComponentAlignment( component, Alignment.MIDDLE_CENTER );

		addComponent(mainLayout);
	}
	
	private Component getTitleLabel()
	{
		Label label = new Label( getContext().getString( title ) );
		label.setStyleName( "centercard_label" );
		
		return label;
	}
	
	private Component getIconImage()
	{
		VerticalLayout layout = new VerticalLayout();

		layout.setHeight( "155px" );
		layout.setWidth( "175px" );
		
		VerticalLayout inside = new VerticalLayout();
		inside.setSizeUndefined();
		
		layout.addComponent( inside );
		layout.setComponentAlignment( inside, Alignment.MIDDLE_CENTER );
		
		inside.addComponent( new Embedded( null, new ThemeResource( icon ) ) );
		
		return layout;
	}
}
