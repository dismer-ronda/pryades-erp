package es.pryades.erp.configuration.tabs;

import java.util.List;

import com.vaadin.ui.Component;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.common.ModalWindowsCRUD.OperationCRUD;
import es.pryades.erp.configuration.modals.ModalNewQuotation;
import es.pryades.erp.configuration.modals.ModalNewQuotationLine;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.QuotationLine;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.QuotationLineVto;
import es.pryades.erp.vto.controlers.QuotationLineControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class QuotationsLinesConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = 7760565684390076239L;
	
	private ModalNewQuotation parent;
	
	public QuotationsLinesConfig( AppContext ctx, ModalNewQuotation parent )
	{
		super( ctx );
	
		this.parent = parent;
		
		setOrderby( "line_order" );
		setOrder( "asc" );
	}

	@Override
	public String getResourceKey()
	{
		return "quotationsLinesConfig";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "line_order", "title", "quantity", "total_invoiced", "cost", "real_cost", "margin", "price", "total_cost", "total_price", "profit", "provider_name" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "line_order", "title", "cost", "real_cost", "margin" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new BaseTable( QuotationLineVto.class, this, getContext() );
	}

	public List<Component> getCustomOperations()
	{
		return null;
	}

	@Override
	public Component getQueryComponent()
	{
		return null;
	}
	
	@Override
	public Query getQueryObject()
	{
		QuotationLine query = new QuotationLine();

		query.setRef_quotation( parent.getNewQuotation().getId() );
		
		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewQuotationLine( getContext(), OperationCRUD.OP_ADD, null, parent ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewQuotationLine( getContext(), OperationCRUD.OP_MODIFY, (QuotationLine)dto, parent ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new QuotationLineControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new QuotationLine();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._QuotationsLinesManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
	}
	
	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.quotations.add" );
	}

	@Override
	public void updateComponent()
	{
	}

	@Override
	public void onFieldEvent( Component component, String column )
	{
	}
}

