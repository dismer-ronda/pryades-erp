package es.pryades.erp.dashboard.widgets;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.dashboard.tabs.DashboardTab;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import lombok.Getter;

public abstract class BaseWidget extends VerticalLayout
{
	private static final long serialVersionUID = -1915934061946674586L;
	
	@Getter private AppContext context;
	@Getter private DashboardTab dashboardTab;
	
	private Double figure;
	private Double reference;
	
	private Label labelTitle;
	private Label labelUnit;
	private Button buttonReference;
	private Label labelReference;
	
	public abstract Double getFigure();
	public abstract Double getReference();

	public abstract void showFigure( VerticalLayout col, Double value );
	public abstract void updateFigure( Double value );
	
	public abstract void setMainLayoutSize( VerticalLayout layout );
	
	public abstract String getPrintableValue( Double value );
	
	public abstract String getResourceString();
	
	//public abstract void loadQueryParameters( SetReferenceDlg dlg );

	public String getTitle()
	{
		return getContext().getString( getResourceString() + ".title" );
	}

	public String getUnits()
	{
		return getContext().getString( getResourceString() + ".units" );
	}
	
	public BaseWidget( AppContext context, DashboardTab dashboardTab )	
	{
		this.context = context;
		this.dashboardTab = dashboardTab;
		
		setMargin( true );
		
		setSizeUndefined();
		
		initComponents();
	}
	
	private void addHeader( HorizontalLayout rowHeader )
	{
		labelTitle = new Label( getTitle() );
		labelTitle.addStyleName( "widget_title" );
		labelTitle.setWidth( "100%" );
		rowHeader.addComponent( labelTitle );
	}
	
	private void addFooter( VerticalLayout column )
	{
		labelUnit = new Label( getUnits() );
		labelUnit.setWidth( "100%" );
		labelUnit.addStyleName( "widget_unit" );
		column.addComponent( labelUnit );
		column.setComponentAlignment( labelUnit, Alignment.MIDDLE_CENTER );
	}
	
	private void addFigure( VerticalLayout col )
	{
		figure = getFigure();
		reference = getReference();
		
		showFigure( col, figure );
	}

	private void addIcon( VerticalLayout col )
	{
		buttonReference = new Button();
		buttonReference.setSizeUndefined();
		buttonReference.setStyleName( "borderless" );
		buttonReference.setDescription( getContext().getString( "words.compare" ) );
		buttonReference.setIcon( new ThemeResource( "images/" + (figure < reference ? "down.png" : (figure > reference ? "up.png" : "equals.png")) ) );
		buttonReference.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 3305862271425424272L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				//setQueryReference();
			}
		} );
		
		col.addComponent( buttonReference );
		col.setComponentAlignment( buttonReference, Alignment.MIDDLE_CENTER );
	}

	private void addReference( VerticalLayout col )
	{
		Double value = figure - reference;
		Integer sign = value == 0 ? 0 : (value < 0 ? -1 : 1);
		String text = getPrintableValue( value );
		
		labelReference = new Label( sign.equals( 0 ) ? "" : text );
		labelReference.addStyleName( "widget_unit" );
		labelReference.setDescription( sign.equals( 0 ) ? "" :  
				getContext().getString( "comparison.reference" ).
					replaceAll( "%value%", text ).
					replaceAll( "%sign%", getContext().getString( "comparison.sign." + sign ) )
		);
		col.addComponent( labelReference );
	}

	private void addContents( HorizontalLayout rowContents )
	{
		VerticalLayout col1 = new VerticalLayout();
		col1.setId( "rowContents_col1" );
		col1.setSizeFull();
		rowContents.addComponent( col1 );
		
		VerticalLayout col2 = new VerticalLayout();
		col2.setId( "rowContents_col2" );
		rowContents.addComponent( col2 );
		rowContents.setComponentAlignment( col2, Alignment.MIDDLE_CENTER );
		
		rowContents.setExpandRatio( col1, 2 );
		rowContents.setExpandRatio( col2, 1 );

		addFigure( col1 );
		addFooter( col1 );

		addIcon( col2 );
		addReference( col2 );
	}

	public void initComponents()
	{
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setId( "mainLayout" );
		mainLayout.addStyleName( "widget" );

		setMainLayoutSize( mainLayout );
		
		HorizontalLayout rowHeader = new HorizontalLayout();
		rowHeader.setId( "rowHeader" );
		rowHeader.setWidth( "100%" );
		rowHeader.addStyleName( "widget_header" );
		rowHeader.setMargin( new MarginInfo( false, true, false, true ) );
		addHeader( rowHeader );
		
		HorizontalLayout rowContents = new HorizontalLayout();
		rowContents.setId( "rowContents" );
		rowContents.setSizeFull();
		addContents( rowContents );
		
		mainLayout.addComponent( rowHeader );
		mainLayout.addComponent( rowContents );
		mainLayout.setExpandRatio( rowContents, 1.0f );
		
		addComponent( mainLayout );
	}

	public void updateComponents()
	{
		figure = getFigure();
		reference = getReference();

		Double value = figure - reference;

		Integer sign = value == 0 ? 0 : (value < 0 ? -1 : 1);
		String text = getPrintableValue( value );

		updateFigure( figure );

		labelReference.setValue( sign.equals( 0 ) ? "" : text );
		labelReference.setDescription( sign.equals( 0 ) ? "" :
				getContext().getString( "comparison.reference" ).
					replaceAll( "%value%", text ).
					replaceAll( "%sign%", getContext().getString( "comparison.sign." + sign ) ) );

		buttonReference.setIcon( new ThemeResource( "images/" + (figure < reference ? "down.png" : (figure > reference ? "up.png" : "equals.png")) ) );
	}

	/*public void setQueryReference()
	{
		final SetReferenceDlg dlg = new SetReferenceDlg( getContext().getString( "setReferenceDlg.title" ), getContext() );
		loadQueryParameters( dlg );
		dlg.initComponents();
		dlg.addCloseListener( new Window.CloseListener()
		{
			private static final long serialVersionUID = 3491711844641367307L;

			@Override
			public void windowClose( CloseEvent e )
			{
				if ( dlg.isAccepted() )
				{
					saveQueryParameters( dlg );
					
					updateComponents();
				}
			}
		} );
		getUI().addWindow( dlg );
	}
	
	public void saveQueryParameters( SetReferenceDlg dlg )
	{
		TextField textDevice = dlg.getTextDevice();
		ComboBox comboRegion = dlg.getComboRegion();
		ComboBox comboPlant = dlg.getComboPlant();
		ComboBox comboHospital = dlg.getComboHospital();
		PopupDateField fromDateField = dlg.getFromDateField();
		PopupDateField toDateField = dlg.getToDateField();

		UserDefault defDevice = dlg.getDefDevice();
		UserDefault defPlant = dlg.getDefPlant();
		UserDefault defRegion = dlg.getDefRegion();
		UserDefault defHospital = dlg.getDefHospital();
		UserDefault defFrom = dlg.getDefFrom();
		UserDefault defTo = dlg.getDefTo();
		
		String device = textDevice.getValue() == null ? null : dlg.getTextDevice().getValue(); 
		String region = comboRegion.getValue() == null ? null : ((Region)comboRegion.getValue()).getId().toString();
		String plant = comboPlant.getValue() == null ? null : ((Plant)comboPlant.getValue()).getId().toString();
		String hospital = comboHospital.getValue() == null ? null : ((Hospital)comboHospital.getValue()).getId().toString();
		String from = fromDateField.getValue() != null ? Long.toString( CalendarUtils.getDateAsLong( fromDateField.getValue() ) ) : null;
		String to = toDateField.getValue() != null ? Long.toString( CalendarUtils.getDateAsLong( toDateField.getValue() ) ) : null;
				
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defFrom, from ); 
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defTo, to ); 
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defDevice, device ); 
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defRegion, region ); 
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defPlant, plant ); 
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defHospital, hospital ); 
	}*/
}
