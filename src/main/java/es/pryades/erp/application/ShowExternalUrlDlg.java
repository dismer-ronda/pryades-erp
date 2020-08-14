package es.pryades.erp.application;

import lombok.Getter;
import lombok.Setter;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import es.pryades.erp.common.MinimizedIcon;
import es.pryades.erp.common.MinimizerContainer;
import es.pryades.erp.common.SizeableWindow;

/**
 * 
 * @author Dismer Ronda
 * 
 */
@SuppressWarnings("serial")
public final class ShowExternalUrlDlg extends SizeableWindow
{
	@Getter	@Setter private String url;
	
	/**
	 * 
	 * @param ctx
	 * @param resources
	 * @param modalOperation
	 * @param orgCentro
	 * @param parentWindow
	 */
	public ShowExternalUrlDlg()
	{
		super();
	}

	public void createComponents()
	{
		super.createComponents();

		setClosable( true );
		setModal( true );

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin( new MarginInfo( false, false, true, false ) );
		layout.setSizeFull();

		ExternalResource resource = new ExternalResource( url );
		BrowserFrame e = new BrowserFrame( null, resource );
	    e.setSizeFull();

	    layout.addComponent( e );
	    
		getWorkLayout().addComponent( layout );
	}

	@Override
	public Component getOptionsBar()
	{
		return null;
	}

	public boolean hasMinimizeIcon()
	{
		return false;
	}

	@Override
	public MinimizerContainer getMinimizerContainer()
	{
		return (MainWnd)getContext().getData( "MainWnd" );
	}

	@Override
	public Component getMinimizedIcon( final MinimizerContainer wnd )
	{
		final MinimizedIcon icon = new MinimizedIcon( getContext(), getCaption(), "images/pdf-128.png" );
		icon.addLayoutClickListener( new LayoutClickListener()
		{
			@Override
			public void layoutClick( LayoutClickEvent event )
			{
				wnd.restoreWindow( icon );
			}
		} );

		return icon;
	}
}
