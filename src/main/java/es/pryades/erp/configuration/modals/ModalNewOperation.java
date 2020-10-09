package es.pryades.erp.configuration.modals;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Operation;
import es.pryades.erp.dto.Quotation;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.dto.query.QuotationQuery;
import es.pryades.erp.ioc.IOCManager;
import lombok.Getter;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewOperation extends ModalWindowsCRUD implements ModalParent
{
	private static final long serialVersionUID = -1773321123857962917L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalNewOperation.class );

	private List<Quotation> quotations;
	
	@Getter
	protected Operation newOperation;

	private ComboBox comboQuotation;
	private TextField editTitle;
	private ComboBox comboStatus;
	
	private UserDefault quotation;
	
	/**
	 * editReference_order
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewOperation( AppContext context, OperationCRUD modalOperation, Operation orgParameter, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgParameter );

		//setWidth( "60%" );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		loadUserDefaults();
		
		try
		{
			newOperation = (Operation) Utils.clone( (Operation) orgDto );
		}
		catch ( Throwable e1 )
		{
			newOperation = new Operation();

			if ( Utils.getLong( quotation.getData_value(), 0 ) != 0)
				newOperation.setRef_quotation( Utils.getLong( quotation.getData_value(), 0 ) );
			newOperation.setStatus( Operation.STATUS_EXCECUTION );
		}

		layout.setHeight( "-1px" );
		
		bi = new BeanItem<BaseDto>( newOperation );

		if ( getOperation().equals( OperationCRUD.OP_ADD ) )
		{
			loadQuotations();
	
			comboQuotation = new ComboBox(getContext().getString( "modalNewOperation.comboQuotation" ));
			comboQuotation.setWidth( "100%" );
			comboQuotation.setNullSelectionAllowed( true );
			comboQuotation.setTextInputAllowed( true );
			comboQuotation.setImmediate( true );
			comboQuotation.setRequired( false );
			fillComboQuotations();
			comboQuotation.setPropertyDataSource( bi.getItemProperty( "ref_quotation" ) );
			comboQuotation.addValueChangeListener( new Property.ValueChangeListener() 
			{
				private static final long serialVersionUID = -8924294581355553622L;
	
				public void valueChange(ValueChangeEvent event) 
			    {
			        onSelectedQuotation();
			    }
			});
		}
		else
		{
			comboStatus = new ComboBox(getContext().getString( "modalNewOperation.comboStatus" ));
			comboStatus.setWidth( "100%" );
			comboStatus.setNullSelectionAllowed( false );
			comboStatus.setTextInputAllowed( false );
			comboStatus.setImmediate( true );
			fillComboStatus();
			comboStatus.setPropertyDataSource( bi.getItemProperty( "status" ) );
			
			editTitle = new TextField( getContext().getString( "modalNewOperation.editTitle" ), bi.getItemProperty( "title" ) );
			editTitle.setWidth( "100%" );
			editTitle.setNullRepresentation( "" );
		}
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		if ( getOperation().equals( OperationCRUD.OP_ADD ) )
			row1.addComponent( comboQuotation );
		else
		{
			row1.addComponent( editTitle );
			row1.addComponent( comboStatus );
		}
		
		componentsContainer.addComponent( row1 );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewOperation";
	}

	@Override
	protected void defaultFocus()
	{
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newOperation.setId( null );
			
			IOCManager._OperationsManager.setRow( getContext(), null, newOperation );
			
			saveUserDefaults();

			/*Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshInvoicesTab();
			dashboard.refreshQuotationsTab();*/

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onModify()
	{
		try
		{
			IOCManager._OperationsManager.setRow( getContext(), (Operation) orgDto, newOperation );

			saveUserDefaults();

			/*Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshInvoicesTab();
			dashboard.refreshQuotationsTab();*/

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onDelete()
	{
		try
		{
			IOCManager._OperationsManager.delRow( getContext(), newOperation );

			/*Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshInvoicesTab();
			dashboard.refreshQuotationsTab();*/
			
			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private void loadQuotations()
	{
		try
		{
			QuotationQuery query = new QuotationQuery();
			query.setStatus( Quotation.STATUS_APPROVED );
			query.setRef_user( getContext().getUser().getId() );

			quotations = IOCManager._QuotationsManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			quotations = new ArrayList<Quotation>();
		}
	}
	
	private void fillComboQuotations()
	{
		comboQuotation.removeAllItems();
		for ( Quotation quotation : quotations )
		{
			comboQuotation.addItem( quotation.getId() );
			comboQuotation.setItemCaption( quotation.getId(), quotation.getTitle() );
		}
	}

	private void fillComboStatus()
	{
		comboStatus.addItem( Operation.STATUS_EXCECUTION );
		comboStatus.setItemCaption( Operation.STATUS_EXCECUTION, getContext().getString( "operation.status." + Operation.STATUS_EXCECUTION ) );
		
		comboStatus.addItem( Operation.STATUS_FINISHED );
		comboStatus.setItemCaption( Operation.STATUS_FINISHED, getContext().getString( "operation.status." + Operation.STATUS_FINISHED ) );

		comboStatus.addItem( Operation.STATUS_CLOSED );
		comboStatus.setItemCaption( Operation.STATUS_CLOSED, getContext().getString( "operation.status." + Operation.STATUS_CLOSED ) );
	}

	@Override
	public void refreshVisibleContent( boolean repage )
	{
		getModalParent().refreshVisibleContent( true );
	}
	
	@Override
	protected boolean editAfterNew()
	{
		return true;
	}

	@Override
	protected void doEditAfterNew()
	{
		new ModalNewOperation( getContext(), OperationCRUD.OP_MODIFY, (Operation)newOperation, getModalParent() ).showModalWindow();
	}

	private void loadUserDefaults()
	{
		quotation = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.OPERATIONS_QUOTATION );
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), quotation, newOperation.getRef_quotation() != null ? newOperation.getRef_quotation().toString() : null );
	}
	
	@Override
	protected boolean hasDelete()
	{
		return getContext().hasRight( "configuration.operations.delete" );
	}

	@Override
	public boolean checkAddRight()
	{
		return getContext().hasRight( "configuration.operations.add" );
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.operations.modify" );
	}
	
	public void onShowPdf()
	{
		/*if ( onModify() )
		{
			try
			{
				ShipmentQuery queryShipment = new ShipmentQuery();
				queryShipment.setId( newShipment.getId() );
				Shipment shipment = (Shipment)IOCManager._ShipmentsManager.getRow( getContext(), queryShipment );
			
				long ts = CalendarUtils.getTodayAsLong( "UTC" );
				
				String pagesize = "A4";
				String template = shipment.getConsignee().getTaxable().booleanValue() ? "national-packing-template" : "international-packing-template";
				String timeout = "0";
				
				String extra = "ts=" + ts + 
								"&id=" + shipment.getId() + 
								"&pagesize=" + pagesize + 
								"&template=" + template +
								"&url=" + getContext().getData( "Url" ) +
								"&timeout=" + timeout;
				
				String user = getContext().getUser().getLogin();
				String password = getContext().getUser().getPwd();
				
				String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
				String code = "code=" + Authorization.encrypt( extra, password ) ;
	
				String url = getContext().getData( "Url" ) + "/services/packing" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
				
				String caption = getContext().getString( "template.shipment.packing" ) + " " + shipment.getFormattedNumber() ;
	
				ShowExternalUrlDlg dlg = new ShowExternalUrlDlg(); 
		
				dlg.setContext( getContext() );
				dlg.setUrl( url );
				dlg.setCaption( caption );
				dlg.createComponents();
				
				getUI().addWindow( dlg );
				
				closeModalWindow( true, true );
			}
	
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}
		}*/
	}

	private void onSelectedQuotation()
	{
		newOperation.setTitle( comboQuotation.getItemCaption( comboQuotation.getValue() ) );
	}
}