package es.pryades.erp.configuration;

import lombok.Getter;
import lombok.Setter;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.dto.BaseDto;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class SelectConfigDlg extends Window 
{
	private static final long serialVersionUID = -1414146791351989978L;

	@Getter @Setter private AppContext context;
	@Getter private VerticalLayout mainLayout;
	@Getter private VerticalLayout workLayout;

	@Getter @Setter private PagedContent configContent;

	@Getter @Setter private BaseDto selected; 
	@Getter @Setter private String resourceKey;
	
	public SelectConfigDlg()
	{
	}
	
	public boolean hasMinimizeIcon()
	{
		return true;
	}
	
	public void createComponents()
	{
		setCaption( getContext().getString( getResourceKey() +  ".title" ) );

		setModal( true );
		setResizable( false );
		setClosable( false );

		//setCloseShortcut( KeyCode.ESCAPE );
		
		setWidth( "800px" ); 
		setHeight( "600px" );
		
		setContent( mainLayout = new VerticalLayout() );
		
		mainLayout.setSizeFull();
		
		mainLayout.setMargin( true );
		mainLayout.setSpacing( true );

		Panel panel = new Panel();

		panel.setSizeFull();
		panel.setStyleName( "borderless light" );
		
		workLayout = new VerticalLayout();
		workLayout.setSizeFull();
		
		panel.setContent( workLayout );
		
		HorizontalLayout rowButtons = new HorizontalLayout();
		rowButtons.setSpacing( true );
		
		Button btnExport = new Button();
		btnExport.setCaption( getContext().getString( "words.ok" ) );
		btnExport.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -2068205560012385993L;

			public void buttonClick( ClickEvent event )
			{
				onOk();
			}
		} );
		
		Button btnUpdate = new Button();
		btnUpdate.setCaption( getContext().getString( "words.cancel" ) );
		btnUpdate.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 8355161094089454796L;

			public void buttonClick( ClickEvent event )
			{
				onCancel();
			}
		} );

		rowButtons.addComponent( btnExport );
		rowButtons.addComponent( btnUpdate );
		
		mainLayout.addComponent( panel );
		mainLayout.addComponent( rowButtons );
		mainLayout.setComponentAlignment( rowButtons, Alignment.BOTTOM_RIGHT );
		mainLayout.setExpandRatio( panel, 1.0f );
		
		configContent.initializeComponent();
		configContent.setSizeFull();
		
		getWorkLayout().addComponent( configContent );

		center();
	}	
	
	void onOk()
	{
		Long rowId = (Long)configContent.getTable().getTable().getValue();
		
		try
		{
			setSelected( rowId != null ? (BaseDto)configContent.getTable().getRowValue( rowId ) : null );
		}
		catch ( Throwable e )
		{
			setSelected( null );
		}

		if ( getSelected() != null )
			getUI().removeWindow( this );
	}
	
	void onCancel()
	{
       	getUI().removeWindow( this );
	}
}
