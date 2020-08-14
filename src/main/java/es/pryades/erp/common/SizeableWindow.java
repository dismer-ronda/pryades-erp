package es.pryades.erp.common;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import lombok.Getter;
import lombok.Setter;

public abstract class SizeableWindow extends Window 
{
	private static final long serialVersionUID = 890140079347435293L;

	@Getter @Setter private AppContext context;
	@Getter private VerticalLayout mainLayout;
	@Getter private VerticalLayout workLayout;
	
	private Button sizeButton;

	public SizeableWindow()
	{
	}
	
	public void createComponents()
	{
		setResizable( false );
		setClosable( false );

    	double h = Page.getCurrent().getBrowserWindowHeight();
    	double w = Page.getCurrent().getBrowserWindowWidth();

   		w = w * 0.8 < 1024 ? 1024 : w * 0.8;
   		h = h - 140 < 600 ? 600 : h - 140;
    	
		setWidth( w + "px" ); 
		setHeight( h + "px" );
		
		setContent( mainLayout = new VerticalLayout() );
		
		mainLayout.setSizeFull();
		
		mainLayout.setMargin( true );
		mainLayout.setSpacing( true );

		Component toolBar = getTopBar();
		mainLayout.addComponent( toolBar );
		mainLayout.setComponentAlignment( toolBar, Alignment.TOP_CENTER );

		Panel panel = new Panel();

		panel.setSizeFull();
		panel.setStyleName( "borderless light" );
		workLayout = new VerticalLayout();
		workLayout.setSizeFull();
		
		panel.setContent( workLayout );
		
		mainLayout.addComponent( panel );
		mainLayout.setExpandRatio( panel, 1.0f );
		
		center();
	}

	public abstract Component getOptionsBar();

	public boolean hasMinimizeIcon()
	{
		return true;
	}

	public boolean hasMaximizeIcon()
	{
		return true;
	}

	public Component getTopBar()
	{
		HorizontalLayout topBar = new HorizontalLayout();
		topBar.setWidth( "100%" );
		
		Component component = getOptionsBar();
		
		if ( component != null )
		{
			topBar.addComponent( component );
			topBar.setComponentAlignment( component, Alignment.MIDDLE_LEFT );
			topBar.setExpandRatio( component, 1.0f );
		}
		
		component = getToolBar();
		topBar.addComponent( component );
		topBar.setComponentAlignment( component, Alignment.MIDDLE_RIGHT );
		
		return topBar;
	}
	
	public Component getToolBar()
	{
		HorizontalLayout toolBar = new HorizontalLayout();
		
		toolBar.setSpacing( true );
		
		Button btn = null;

		if ( hasMinimizeIcon() )
		{
			btn = new Button( getContext().getString( "words.minimize" ) );
			//btn.setIcon(new ThemeResource( "images/minimize.png" ) );
			//btn.addStyleName("borderless icon-on-top");
			btn.addStyleName( "menu" );
			btn.addClickListener( new Button.ClickListener()	
			{
				private static final long serialVersionUID = 1337306948211142339L;

				public void buttonClick( ClickEvent event )
				{
					doMinimize(); 
				}
			} );
			toolBar.addComponent(btn);
		}

		if ( hasMaximizeIcon() )
		{
			sizeButton = new Button( (hasMinimizeIcon() ? "| " : "") + getContext().getString( "words.maximize" ) );
			//sizeButton.setIcon(new ThemeResource( "images/maximize.png" ) );
			//sizeButton.addStyleName("borderless icon-on-top");
			sizeButton.addStyleName( "menu" );
			sizeButton.addClickListener( new Button.ClickListener()	
			{
				private static final long serialVersionUID = -3751106203656802894L;
	
				public void buttonClick( ClickEvent event )
				{
					doMaximizeRestore(); 
				}
			} );
			toolBar.addComponent(sizeButton);
		}
		
		btn = new Button( (hasMinimizeIcon() || hasMaximizeIcon() ? "| " : "") + getContext().getString( "words.close" ) );
		//btn.setIcon(new ThemeResource( "images/close.png" ) );
		//btn.addStyleName("borderless icon-on-top");
		btn.addStyleName( "menu" );
		btn.addClickListener( new Button.ClickListener()	
		{
			private static final long serialVersionUID = 2912509131743613155L;

			public void buttonClick( ClickEvent event )
			{
				doClose(); 
			}
		} );
		toolBar.addComponent(btn);

		return toolBar;
	}

	public abstract MinimizerContainer getMinimizerContainer();
	public abstract Component getMinimizedIcon( MinimizerContainer wnd );
	
	public void doMinimize()
	{
		MinimizerContainer wnd = getMinimizerContainer();
		
		Component icon = getMinimizedIcon( wnd );
		
		wnd.minimizeWindow( this, icon );
	}
	
	public void doClose()
	{
		close();
	}

	public void doMaximizeRestore()
	{
		if ( this.getWindowMode().equals( WindowMode.NORMAL ) )
		{
			setWindowMode( WindowMode.MAXIMIZED );
			sizeButton.setCaption( (hasMinimizeIcon() ? "| " : "") + getContext().getString( "words.restore" ) );
		}
		else
		{
			setWindowMode( WindowMode.NORMAL );
			sizeButton.setCaption( (hasMinimizeIcon() ? "| " : "") + getContext().getString( "words.maximize" ) );
		}
	}
}
