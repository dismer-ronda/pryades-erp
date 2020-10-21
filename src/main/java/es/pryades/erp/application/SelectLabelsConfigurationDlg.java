package es.pryades.erp.application;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.ShipmentBox;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.ioc.IOCManager;
import lombok.Getter;
import lombok.Setter;

public class SelectLabelsConfigurationDlg extends Window 
{
	private static final long serialVersionUID = 8674387250186918881L;

	private VerticalLayout layout;

	@Getter private AppContext context;
	
	private ComboBox comboLabelTypes;
	private ComboBox comboPagesize;
	private ComboBox comboFormat;
	private ComboBox comboFontSize;
	private ComboBox comboCopies;
	
	@Getter	private BeanItem<SelectLabelsConfigurationDlg> bi;

	@Getter @Setter private Integer label_type;
	@Getter @Setter private String pagesize;
	@Getter @Setter private String format;
	@Getter @Setter private String fontsize;
	@Getter @Setter private Integer copies;

	@Getter private boolean ok = false;
	
	private UserDefault default_type;
	private UserDefault default_pagesize;
	private UserDefault default_format;
	private UserDefault default_fontsize;
	private UserDefault default_copies;

	public SelectLabelsConfigurationDlg( AppContext ctx, String title )
	{
		super( title );
		
		this.context = ctx;
		
		setContent( layout = new VerticalLayout() );
		
		layout.setMargin( true );
		layout.setSpacing( true );
		
		layout.setWidth( "640px" );
		
		addComponents();
		
		center();
		
		setModal( true );
		setResizable( false );
		setClosable( true );
	}

	private void addComponents() 
	{
		bi = new BeanItem<SelectLabelsConfigurationDlg>( this );
		
		loadUserDefaults();
		
		comboLabelTypes = new ComboBox(getContext().getString( "SelectLabelsConfigurationDlg.comboLabelType" ));
		comboLabelTypes.setWidth( "100%" );
		comboLabelTypes.setNullSelectionAllowed( false );
		comboLabelTypes.setTextInputAllowed( false );
		comboLabelTypes.setImmediate( true );
		comboLabelTypes.setRequired( true );
		fillComboLabelTypes();
		comboLabelTypes.setPropertyDataSource( bi.getItemProperty( "label_type" ) );

		comboPagesize = new ComboBox(getContext().getString( "SelectLabelsConfigurationDlg.comboPageSize" ));
		comboPagesize.setWidth( "100%" );
		comboPagesize.setNullSelectionAllowed( false );
		comboPagesize.setTextInputAllowed( false );
		comboPagesize.setImmediate( true );
		comboPagesize.setRequired( true );
		fillComboPagesize();
		comboPagesize.setPropertyDataSource( bi.getItemProperty( "pagesize" ) );

		comboFormat = new ComboBox(getContext().getString( "SelectLabelsConfigurationDlg.comboFormat" ));
		comboFormat.setWidth( "100%" );
		comboFormat.setNullSelectionAllowed( false );
		comboFormat.setTextInputAllowed( false );
		comboFormat.setImmediate( true );
		comboFormat.setRequired( true );
		fillComboFormats();
		comboFormat.setPropertyDataSource( bi.getItemProperty( "format" ) );

		comboFontSize = new ComboBox(getContext().getString( "SelectLabelsConfigurationDlg.comboFontSize" ));
		comboFontSize.setWidth( "100%" );
		comboFontSize.setNullSelectionAllowed( false );
		comboFontSize.setTextInputAllowed( false );
		comboFontSize.setImmediate( true );
		comboFontSize.setRequired( true );
		fillComboFontsize();
		comboFontSize.setPropertyDataSource( bi.getItemProperty( "fontsize" ) );

		comboCopies = new ComboBox(getContext().getString( "SelectLabelsConfigurationDlg.comboCopies" ));
		comboCopies.setWidth( "100%" );
		comboCopies.setNullSelectionAllowed( false );
		comboCopies.setTextInputAllowed( false );
		comboCopies.setImmediate( true );
		comboCopies.setRequired( true );
		fillComboCopies();
		comboCopies.setPropertyDataSource( bi.getItemProperty( "copies" ) );

		Button button1 = new Button( getContext().getString( "words.ok" ) );
		button1.setClickShortcut( KeyCode.ENTER );
		button1.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = 9154772696786741535L;

			public void buttonClick(ClickEvent event) 
            {
				onOk(); 
            }
        });
		
		Button button2 = new Button( getContext().getString( "words.cancel" ) );
		button2.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = -5671452975867076669L;

			public void buttonClick(ClickEvent event) 
            {
				close();
            }
        });
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( comboLabelTypes );
		row1.addComponent( comboPagesize );
		row1.addComponent( comboCopies );
		
		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( comboFormat );
		row2.addComponent( comboFontSize );
		row2.addComponent( new HorizontalLayout() );
		
		HorizontalLayout row4 = new HorizontalLayout();
		row4.setSpacing( true );
		row4.addComponent( button1 );
		row4.addComponent( button2 );

		layout.addComponent( row1 );
		layout.addComponent( row2 );
		layout.addComponent( row4 );
		layout.setComponentAlignment( row4, Alignment.BOTTOM_RIGHT );

        comboPagesize.focus();
	}

	private void onOk()
	{
		ok = true;
		
		saveUserDefaults();
		
		close();
    }

	private void fillComboLabelTypes()
	{
		comboLabelTypes.addItem( ShipmentBox.LABEL_ALL );
		comboLabelTypes.setItemCaption( ShipmentBox.LABEL_ALL, getContext().getString( "shipment.label.type." + ShipmentBox.LABEL_ALL ) );

		comboLabelTypes.addItem( ShipmentBox.LABEL_DETAIL );
		comboLabelTypes.setItemCaption( ShipmentBox.LABEL_DETAIL, getContext().getString( "shipment.label.type." + ShipmentBox.LABEL_DETAIL ) );

		comboLabelTypes.addItem( ShipmentBox.LABEL_SIMPLE );
		comboLabelTypes.setItemCaption( ShipmentBox.LABEL_SIMPLE, getContext().getString( "shipment.label.type." + ShipmentBox.LABEL_SIMPLE ) );
		
		comboLabelTypes.select( ShipmentBox.LABEL_DETAIL );
	}

	private void fillComboPagesize()
	{
		comboPagesize.addItem( "A3" );
		comboPagesize.setItemCaption( "A3", "A3" );

		comboPagesize.addItem( "A4" );
		comboPagesize.setItemCaption( "A4", "A4" );

		comboPagesize.addItem( "A5" );
		comboPagesize.setItemCaption( "A5", "A5" );
	}

	private void fillComboFormats()
	{
		comboFormat.addItem( "2x2" );
		comboFormat.setItemCaption( "2x2", getContext().getString( "SelectLabelsConfigurationDlg.format.2x2" ) );

		comboFormat.addItem( "4x2" );
		comboFormat.setItemCaption( "4x2", getContext().getString( "SelectLabelsConfigurationDlg.format.4x2" ) );
	}

	private void fillComboFontsize()
	{
		comboFontSize.addItem( "12px" );
		comboFontSize.setItemCaption( "12px", getContext().getString( "SelectLabelsConfigurationDlg.fontsize.12px" ) );

		comboFontSize.addItem( "16px" );
		comboFontSize.setItemCaption( "16px", getContext().getString( "SelectLabelsConfigurationDlg.fontsize.16px" ) );
		
		comboFontSize.addItem( "18px" );
		comboFontSize.setItemCaption( "18px", getContext().getString( "SelectLabelsConfigurationDlg.fontsize.18px" ) );
	}

	private void fillComboCopies()
	{
		comboCopies.addItem( new Integer(1) );
		comboCopies.setItemCaption( new Integer(1), "1" );

		comboCopies.addItem( new Integer(2) );
		comboCopies.setItemCaption( new Integer(2), "2" );

		comboCopies.addItem( new Integer(3) );
		comboCopies.setItemCaption( new Integer(3), "3" );

		comboCopies.addItem( new Integer(4) );
		comboCopies.setItemCaption( new Integer(4), "4" );
	}

	private void loadUserDefaults()
	{
		default_type = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.LABELS_TYPE );
		default_pagesize = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.LABELS_PAGESIZE );
		default_format = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.LABELS_FORMAT );
		default_fontsize = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.LABELS_FONTSIZE );
		default_copies = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.LABELS_COPIES );
		
		label_type = Utils.getInt( default_type.getData_value(), ShipmentBox.LABEL_DETAIL );
		pagesize = default_pagesize.getData_value();
		format = default_format.getData_value();
		fontsize = default_fontsize.getData_value();
		copies = Utils.getInt( default_copies.getData_value(), 1 );
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_type, label_type.toString() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_pagesize, pagesize );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_format, format );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_fontsize, fontsize );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_copies, copies.toString() );
	}
}
