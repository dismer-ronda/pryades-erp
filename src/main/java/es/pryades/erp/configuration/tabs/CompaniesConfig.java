package es.pryades.erp.configuration.tabs;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD.Operation;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.common.PagedTable;
import es.pryades.erp.configuration.modals.ModalNewCompany;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.CompanyVto;
import es.pryades.erp.vto.controlers.CompanyControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class CompaniesConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -4112153786008059013L;

	private static final Logger LOG = Logger.getLogger( CompaniesConfig.class );

	private TextField editAlias;
	private TextField editTax_id;

	private Button bttnApply;
	
	public CompaniesConfig( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "alias" );
		setOrder( "desc" );
	}

	@Override
	public String getResourceKey()
	{
		return "companiesConfig";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "tax_id", "alias", "name", "email", "phone", "type_company" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "tax_id", "alias", "name", "email", "phone", "type_company" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( CompanyVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	public List<Component> getCustomOperations()
	{
		return null;
	}

	@Override
	public Component getQueryComponent()
	{
		editAlias = new TextField( getContext().getString( "companiesConfig.editAlias" ) );
		editAlias.setWidth( "100%" );
		editAlias.setNullRepresentation( "" );
		
		editTax_id = new TextField( getContext().getString( "companiesConfig.editTax_id" ) );
		editTax_id.setWidth( "100%" );
		editTax_id.setNullRepresentation( "" );
		
		bttnApply = new Button( getContext().getString( "words.search" ) );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
		//bttnApply.setStyleName( "borderless" );
		//bttnApply.setIcon( new ThemeResource( "images/accept.png" ) );
		addButtonApplyFilterClickListener();

		HorizontalLayout rowQuery = new HorizontalLayout();
		rowQuery.setSpacing( true );
		rowQuery.addComponent( editAlias );
		rowQuery.addComponent( editTax_id );
		rowQuery.addComponent( bttnApply );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		CompanyQuery query = new CompanyQuery();
		
		if ( !editAlias.getValue().isEmpty() )
			query.setAlias( "%" + editAlias.getValue() + "%" );
		
		if ( !editTax_id.getValue().isEmpty() )
			query.setTax_id( editTax_id.getValue() );
		
		//query.setRef_user( getContext().getUser().getId() );
		
		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewCompany( getContext(), Operation.OP_ADD, null, CompaniesConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewCompany( getContext(), Operation.OP_MODIFY, (Company)dto, CompaniesConfig.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new CompanyControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Company();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._CompaniesManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
		for ( BaseDto row : rows )
			LOG.info( "company " + row );
	}
	
	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.companies.add" );
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
			private static final long serialVersionUID = 309512710669351392L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				refreshVisibleContent( true );
			}
		} );
	}
}

