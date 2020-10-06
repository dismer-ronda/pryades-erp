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
import es.pryades.erp.common.AppUtils;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.ShipmentBox;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.ioc.IOCManager;
import lombok.Getter;
import lombok.Setter;

public class SelectLabelsConfigurationDlg extends Window 
{
	private static final long serialVersionUID = -8060700375740929738L;

	private VerticalLayout layout;

	@Getter private AppContext context;
	
	private ComboBox comboLabelTypes;
	private ComboBox comboPagesize;
	private ComboBox comboFormat;
	private ComboBox comboFontSize;
	
	@Getter	private BeanItem<SelectLabelsConfigurationDlg> bi;

	@Getter @Setter private Integer label_type;
	@Getter @Setter private String pagesize;
	@Getter @Setter private String format;
	@Getter @Setter private String fontsize;

	@Getter private boolean ok = false;
	
	private UserDefault default_type;
	private UserDefault default_pagesize;
	private UserDefault default_format;
	private UserDefault default_fontsize;

	public SelectLabelsConfigurationDlg( AppContext ctx, String title )
	{
		super( title );
		
		this.context = ctx;
		
		setContent( layout = new VerticalLayout() );
		
		layout.setMargin( true );
		layout.setSpacing( true );
		
		layout.setWidth( "480px" );
		
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

		Button button1 = AppUtils.createButton( context.getString( "words.ok" ), context.getString( "words.ok" ), "SelectLabelsConfigurationDlg.ok", layout );
		button1.setClickShortcut( KeyCode.ENTER );
		button1.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = 31563218448960611L;

			public void buttonClick(ClickEvent event) 
            {
				ok = true;
				
				onOk();
            }
        });
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( comboLabelTypes );
		row1.addComponent( comboPagesize );
		
		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( comboFormat );
		row2.addComponent( comboFontSize );
		
		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.addComponent( button1 );
		row3.setComponentAlignment( button1, Alignment.BOTTOM_RIGHT );

		layout.addComponent( row1 );
		layout.addComponent( row2 );
		layout.addComponent( row3 );
        
        comboPagesize.focus();
	}

	private void onOk()
	{
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


	private void loadUserDefaults()
	{
		default_type = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.LABELS_TYPE );
		default_pagesize = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.LABELS_PAGESIZE );
		default_format = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.LABELS_FORMAT );
		default_fontsize = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.LABELS_FONTSIZE );
		
		label_type = Utils.getInt( default_type.getData_value(), ShipmentBox.LABEL_DETAIL );
		pagesize = default_pagesize.getData_value();
		format = default_format.getData_value();
		fontsize = default_fontsize.getData_value();
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_type, label_type.toString() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_pagesize, pagesize );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_format, format );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_fontsize, fontsize );
	}
}
