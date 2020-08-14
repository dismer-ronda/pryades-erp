package es.pryades.erp.application;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

import es.pryades.erp.common.AppContext;

public class FooterComponent extends HorizontalLayout
{
	private static final long serialVersionUID = 4609154621782766530L;

	private static final String URL = "http://www.pryades.com";

	private AppContext context;

	public FooterComponent( AppContext context )
	{
		this.context = context;

		setWidth( "100%" );
		setStyleName( "enermet_footer" );

		Component component = buildLeft();
		addComponent( component );

		Component center = buildCenter();
		addComponent( center );

		component = buildRight();
		addComponent( component );
		setComponentAlignment( component, Alignment.MIDDLE_RIGHT );

		setExpandRatio( center, 1.0f );
	}

	private Component buildLeft()
	{
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth( "-1px" );
		Link link = new Link();
		link.setResource( new ExternalResource( URL ) );
		link.setDescription( URL );
		link.setIcon( new ThemeResource( "images/company-logo.png" ) );
		addComponent( link );

		return layout;
	}

	private Component buildCenter()
	{
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth( "100%" );

		return layout;
	}

	private Component buildRight()
	{
		HorizontalLayout layout = new HorizontalLayout();

		layout.setSpacing( true );

		Label label = new Label( context.getString( "words.copyright" ) );
		layout.addComponent( label );

		Link link = new Link( context.getString( "words.company" ), new ExternalResource( URL ) );
		link.setDescription( URL );
		layout.addComponent( link );

		label = new Label( context.getString( "words.all.rights" ) );
		layout.addComponent( label );

		return layout;
	}

}
