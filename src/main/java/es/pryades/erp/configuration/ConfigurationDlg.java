package es.pryades.erp.configuration;

import org.apache.log4j.Logger;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Component;

import es.pryades.erp.application.MainWnd;
import es.pryades.erp.common.MinimizedIcon;
import es.pryades.erp.common.MinimizerContainer;
import es.pryades.erp.common.SizeableWindow;
import es.pryades.erp.common.Utils;

public class ConfigurationDlg extends SizeableWindow 
{
	private static final long serialVersionUID = 1463264651467849218L;
	private static final Logger LOG = Logger.getLogger( ConfigurationDlg.class );

	public ConfigurationDlg()
	{
	}
	
	public boolean hasMinimizeIcon()
	{
		return true;
	}
	
	public void createComponents()
	{
		super.createComponents();

		setCaption( getContext().getString( "ConfigurationDlg.title" ) );

		EnermetTabbedContainerConfig instance = new EnermetTabbedContainerConfig( getContext() );

		instance.setSizeFull();

		getWorkLayout().addComponent( instance );
		
		try
		{
			instance.render();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}	

	@Override
	public Component getOptionsBar()
	{
		return null;
	}

	@Override
	public MinimizerContainer getMinimizerContainer()
	{
		return (MainWnd)getContext().getData( "MainWnd" );
	}

	@Override
	public Component getMinimizedIcon( final MinimizerContainer wnd )
	{
		final MinimizedIcon icon = new MinimizedIcon( getContext(), "words.configuration", "images/settings-128.png" );
		icon.addLayoutClickListener( new LayoutClickListener()
		{
			private static final long serialVersionUID = -5107732746597927472L;

			@Override
			public void layoutClick( LayoutClickEvent event )
			{
				wnd.restoreWindow( icon );
			}
		} );

		return icon;
	}
}
