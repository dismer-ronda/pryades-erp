package es.pryades.erp.configuration.tabs;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.Constants;
import es.pryades.erp.common.DateRange;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.common.PagedTable;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.Audit;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.User;
import es.pryades.erp.dto.query.AuditQuery;
import es.pryades.erp.dto.query.UserQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.AuditVto;
import es.pryades.erp.vto.controlers.AuditControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class AuditsConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -5428522530136369407L;
	private static final Logger LOG = Logger.getLogger( AuditsConfig.class );

	private ComboBox comboDate;
	private ComboBox comboType;
	private ComboBox comboUsers;
	private TextField textQuery;

	private Button bttnApply;
	
	public AuditsConfig( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "audit_date" );
		setOrder( "desc" );
	}

	@Override
	public String getResourceKey()
	{
		return "auditsConfig";
	}

	public boolean hasNew() 		{ return false; }
	public boolean hasModify() 		{ return false; }
	public boolean hasDelete() 		{ return false; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "audit_date", "user_name", "audit_type", "audit_details" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "audit_date", "user_name", "audit_type" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( AuditVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	@Override
	public Component getQueryComponent()
	{
		comboDate = new ComboBox( getContext().getString( "words.date" ) );
		comboDate.setNullSelectionAllowed( true );
		comboDate.setTextInputAllowed( false );
		comboDate.setImmediate( true );
		Utils.fillComboDates( comboDate, getContext() );
		
		comboType = new ComboBox( getContext().getString( "auditsConfig.type" ) );
		comboType.setNullSelectionAllowed( true );
		comboType.setTextInputAllowed( false );
		comboType.setImmediate( true );
		fillComboProducts();
		
		comboUsers = new ComboBox( getContext().getString( "auditsConfig.user" ) );
		comboUsers.setNullSelectionAllowed( true );
		comboUsers.setTextInputAllowed( true );
		comboUsers.setImmediate( true );
		fillComboUsers();
		
		textQuery = new TextField( getContext().getString( "words.details" ) );
		textQuery.setWidth( "100%" );
		textQuery.setImmediate( true );
		textQuery.setNullRepresentation( "" );
		
		bttnApply = new Button( getContext().getString( "words.search" ) );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
		//bttnApply.setStyleName( "borderless" );
		//bttnApply.setIcon( new ThemeResource( "images/accept.png" ) );
		addButtonApplyFilterClickListener();

		HorizontalLayout rowQuery = new HorizontalLayout();
		rowQuery.setSpacing( true );
		rowQuery.addComponent( comboDate );
		rowQuery.addComponent( comboType );
		rowQuery.addComponent( comboUsers );
		rowQuery.addComponent( textQuery );
		rowQuery.addComponent( bttnApply );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		AuditQuery query = new AuditQuery();
		
		DateRange dateRange = Utils.getDateRange( (Integer)comboDate.getValue(), CalendarUtils.getCurrentCalendar( "UTC" ) );
		
		query.setFrom_date( dateRange.getFrom() );
		query.setTo_date( dateRange.getTo() );

		query.setAudit_type( (Integer)comboType.getValue() );
		query.setRef_user( (Long)comboUsers.getValue() );
		
		if ( !textQuery.getValue().isEmpty() )
			query.setTextQuery( "%" + textQuery.getValue() + "%" );

		return query;
	}

	@Override
	public void onOperationNew()
	{
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new AuditControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Audit();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._AuditsManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
	}
	
	@Override
	public boolean hasAddRight()
	{
		return false;
	}

	@Override
	public void updateComponent()
	{
	}

	@Override
	public void onFieldEvent( Component component, String column )
	{
	}

	public void addButtonApplyFilterClickListener()
	{
		bttnApply.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 7406758301827706990L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				refreshVisibleContent( true );
			}
		} );
	}

	private void fillComboProducts()
	{
		for ( int i = 0; i < Constants.AUDIT_TYPES; i++ )
		{
			comboType.addItem( i );
			comboType.setItemCaption( i, getContext().getString( "audit.type." + i ) );
		}
	}
	
	@SuppressWarnings("unchecked")
	private void fillComboUsers()
	{
		comboUsers.removeAllItems();
		
		try
		{
			UserQuery query = new UserQuery();

			List<User> users = IOCManager._UsersManager.getRows( getContext(), query );

			for ( User user : users )
			{
				comboUsers.addItem( user.getId() );
				comboUsers.setItemCaption( user.getId(), user.getName() );
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
}
