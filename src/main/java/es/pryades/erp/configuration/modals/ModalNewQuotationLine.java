package es.pryades.erp.configuration.modals;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.QuotationDelivery;
import es.pryades.erp.dto.QuotationLine;
import es.pryades.erp.dto.QuotationLineDelivery;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewQuotationLine extends ModalWindowsCRUD implements ModalParent
{
	private static final long serialVersionUID = -4249686173355306766L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalNewQuotationLine.class );

	protected QuotationLine newQuotation;

	private List<Company> providers;

	private TextField editLine_order;
	private TextField editOrigin;
	private TextArea editReference;
	private TextField editTitle;
	private TextArea editDescription;
	private TextField editCost;
	private TextField editReal_cost;
	private TextField editMargin;
	private TextField editTax_rate;
	private List<TextField> editsDeliveries;
	private ComboBox comboProviders;
	private Button btnAdd;

	private UserDefault defaultMargin;
	private UserDefault defaultLine_order;
	private UserDefault defaultProvider;
	private UserDefault defaultTax_rate;
	private UserDefault defaultOrigin;

	/**
	 * editReference_order
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewQuotationLine( AppContext context, Operation modalOperation, QuotationLine orgParameter, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgParameter );
		
		setWidth( "60%" );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		loadUserDefaults();

		try
		{
			newQuotation = (QuotationLine) Utils.clone( (QuotationLine) orgDto );
		}
		catch ( Throwable e1 )
		{
			newQuotation = new QuotationLine();
			newQuotation.setRef_quotation( ((ModalNewQuotation)getModalParent()).getNewQuotation().getId() );
			newQuotation.setLine_deliveries( new ArrayList<QuotationLineDelivery>() );
			newQuotation.setMargin( Utils.getDouble( defaultMargin.getData_value(), 0.0 ) );
			newQuotation.setLine_order( Utils.getInt( defaultLine_order.getData_value(), 1 ) );
			if ( Utils.getLong( defaultProvider.getData_value(), 0 ) != 0)
				newQuotation.setRef_provider( Utils.getLong( defaultProvider.getData_value(), 0 ) );
			newQuotation.setTax_rate( Utils.getDouble( defaultTax_rate.getData_value(), 0 ) );
			newQuotation.setOrigin( defaultOrigin.getData_value() );
		}

		bi = new BeanItem<BaseDto>( newQuotation );

		loadProviders();

		editLine_order = new TextField( getContext().getString( "modalNewQuotationLine.editLine_order" ), bi.getItemProperty( "line_order" ) );
		editLine_order.setWidth( "100%" );
		editLine_order.setNullRepresentation( "" );
		
		editOrigin = new TextField( getContext().getString( "modalNewQuotationLine.editOrigin" ), bi.getItemProperty( "origin" ) );
		editOrigin.setWidth( "100%" );
		editOrigin.setNullRepresentation( "" );
		
		editReference = new TextArea( getContext().getString( "modalNewQuotationLine.editReference" ), bi.getItemProperty( "reference" ) );
		editReference.setWidth( "100%" );
		editReference.setNullRepresentation( "" );
		
		editTitle = new TextField( getContext().getString( "modalNewQuotationLine.editTitle" ), bi.getItemProperty( "title" ) );
		editTitle.setWidth( "100%" );
		editTitle.setNullRepresentation( "" );
		
		editDescription = new TextArea( getContext().getString( "modalNewQuotationLine.editDescription" ), bi.getItemProperty( "description" ) );
		editDescription.setWidth( "100%" );
		editDescription.setNullRepresentation( "" );
		
		editCost = new TextField( getContext().getString( "modalNewQuotationLine.editCost" ), bi.getItemProperty( "cost" ) );
		editCost.setWidth( "100%" );
		editCost.setNullRepresentation( "" );
		
		editReal_cost = new TextField( getContext().getString( "modalNewQuotationLine.editReal_cost" ), bi.getItemProperty( "real_cost" ) );
		editReal_cost.setWidth( "100%" );
		editReal_cost.setNullRepresentation( "" );

		editMargin = new TextField( getContext().getString( "modalNewQuotationLine.editMargin" ), bi.getItemProperty( "margin" ) );
		editMargin.setWidth( "100%" );
		editMargin.setNullRepresentation( "" );
		
		editTax_rate = new TextField( getContext().getString( "modalNewQuotationLine.editTax_rate" ), bi.getItemProperty( "tax_rate" ) );
		editTax_rate.setWidth( "100%" );
		editTax_rate.setNullRepresentation( "" );
		
		comboProviders = new ComboBox(getContext().getString( "modalNewQuotationLine.comboProviders" ));
		comboProviders.setWidth( "100%" );
		comboProviders.setNullSelectionAllowed( true );
		comboProviders.setTextInputAllowed( true );
		comboProviders.setImmediate( true );
		fillComboProviders();
		comboProviders.setPropertyDataSource( bi.getItemProperty( "ref_provider" ) );
		
		btnAdd = new Button(" + ");
		btnAdd.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -4086263529976395213L;

			public void buttonClick( ClickEvent event )
			{
				onAddProvider();
			}
		} );
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( editLine_order );
		row1.addComponent( editOrigin );
		row1.addComponent( editTitle );
		row1.addComponent( editTax_rate );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( editReference );
		row2.addComponent( editDescription );

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( editCost );
		row3.addComponent( editReal_cost );
		row3.addComponent( editMargin );
		row3.addComponent( comboProviders );
		row3.addComponent( btnAdd );
		row3.setComponentAlignment( btnAdd, Alignment.BOTTOM_LEFT );

		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		componentsContainer.addComponent( row3 );
		
		List<QuotationDelivery> deliveries = ((ModalNewQuotation)getModalParent()).getNewQuotation().getDeliveries();
		
		editsDeliveries = new ArrayList<TextField>();
		
		if ( deliveries != null && deliveries.size() > 0 )
		{
			HorizontalLayout row5 = new HorizontalLayout();
			row5.setWidth( "100%" );
			row5.setSpacing( true );
			row5.setMargin( true );
			row5.setCaption( getContext().getString( "modalNewQuotationLine.deliveries" ) );
			
			for ( QuotationDelivery delivery : deliveries )
			{
				TextField tmp = new TextField( CalendarUtils.getDateFromLongAsString( delivery.getDeparture_date(), "dd-MM-yyyy" ) );
				tmp.setWidth( "100%" );
				tmp.setNullRepresentation( "0" );
				tmp.setData( delivery );
				tmp.setRequired( true );
				tmp.setValue( Integer.toString( getDeliveryValue( delivery ) ) );
				
				row5.addComponent( tmp );

				editsDeliveries.add( tmp );
			}

			componentsContainer.addComponent( row5 );
		}
	}	

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewQuotationLine";
	}

	@Override
	protected void defaultFocus()
	{
		editReference.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newQuotation.setId( null );
			
			if ( newQuotation.getReal_cost() == null )
				newQuotation.setReal_cost( newQuotation.getCost() );

			IOCManager._QuotationsLinesManager.setRow( getContext(), null, newQuotation );
			
			saveUserDefaults();

			for ( TextField edit : editsDeliveries )
			{
				QuotationLineDelivery lineDelivery = new QuotationLineDelivery();

				lineDelivery.setRef_quotation_delivery( ((QuotationDelivery)edit.getData()).getId() );
				lineDelivery.setRef_quotation_line( newQuotation.getId() );
				lineDelivery.setQuantity( Integer.valueOf( edit.getValue() ) );

				IOCManager._QuotationsLinesDeliveriesManager.delRow( getContext(), lineDelivery );

				if ( lineDelivery.getQuantity() > 0 )
					IOCManager._QuotationsLinesDeliveriesManager.setRow( getContext(), null, lineDelivery );
			}
			
			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	private int getDeliveryValue( QuotationDelivery delivery )
	{
		for ( QuotationLineDelivery lineDelivery : newQuotation.getLine_deliveries() )
		{
			if ( lineDelivery.getRef_quotation_delivery().equals( delivery.getId() ) )
				return lineDelivery.getQuantity();
		}
		
		return 0;
	}
	
	@Override
	protected boolean onModify()
	{
		try
		{
			if ( newQuotation.getReal_cost() == null )
				newQuotation.setReal_cost( newQuotation.getCost() );

			IOCManager._QuotationsLinesManager.setRow( getContext(), (QuotationLine) orgDto, newQuotation );
			
			saveUserDefaults();

			for ( TextField edit : editsDeliveries )
			{
				QuotationLineDelivery lineDelivery = new QuotationLineDelivery();

				lineDelivery.setRef_quotation_delivery( ((QuotationDelivery)edit.getData()).getId() );
				lineDelivery.setRef_quotation_line( newQuotation.getId() );
				lineDelivery.setQuantity( Utils.getInt( edit.getValue(), 0 ) );

				deleteOldQuantity( lineDelivery );
				
				if ( lineDelivery.getQuantity() > 0 )
					IOCManager._QuotationsLinesDeliveriesManager.setRow( getContext(), null, lineDelivery );
			}

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	private void deleteOldQuantity( QuotationLineDelivery lineDelivery )
	{
		try
		{
			IOCManager._QuotationsLinesDeliveriesManager.delRow( getContext(), lineDelivery );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
		}
	}
	
	@Override
	protected boolean onDelete()
	{
		try
		{
			IOCManager._QuotationsLinesManager.delRow( getContext(), newQuotation );

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	private void loadUserDefaults()
	{
		defaultMargin = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_LINE_MARGIN );
		defaultLine_order = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_LINE_ORDER );
		defaultProvider = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_LINE_PROVIDER );
		defaultTax_rate = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_LINE_TAX_RATE );
		defaultOrigin = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_LINE_ORIGIN );
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defaultMargin, newQuotation.getMargin().toString() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defaultLine_order, Integer.toString( newQuotation.getLine_order() + 1 ) );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defaultTax_rate, Double.toString( newQuotation.getTax_rate() ) );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defaultProvider, Long.toString( newQuotation.getRef_provider() != null ? newQuotation.getRef_provider() : 0 ) );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), defaultOrigin, newQuotation.getOrigin() );
	}

	@SuppressWarnings("unchecked")
	private void loadProviders()
	{
		try
		{
			CompanyQuery query = new CompanyQuery();
			query.setType_company( Company.TYPE_PROVIDER );
			//query.setRef_user( getContext().getUser().getId() );

			providers = IOCManager._CompaniesManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			providers= new ArrayList<Company>();
		}
	}

	private void fillComboProviders()
	{
		for ( Company company : providers )
		{
			comboProviders.addItem( company.getId() );
			comboProviders.setItemCaption( company.getId(), company.getName() );
		}
	}
	
	private void onAddProvider()
	{
		new ModalNewCompany( getContext(), Operation.OP_ADD, null, ModalNewQuotationLine.this ).showModalWindow();
	}

	@Override
	public void refreshVisibleContent( boolean repage )
	{
		loadProviders();
		fillComboProviders();
	}

	@Override
	protected boolean hasDelete()
	{
		return getContext().hasRight( "configuration.quotations.delete" );
	}

	@Override
	public boolean checkAddRight()
	{
		return getContext().hasRight( "configuration.quotations.add" );
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.quotations.modify" );
	}
}
