package es.pryades.erp.configuration.tabs;

import java.util.List;

import com.vaadin.ui.Component;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.configuration.modals.ModalNewInvoice;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.InvoiceLine;
import es.pryades.erp.dto.Query;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.InvoiceLineVto;
import es.pryades.erp.vto.controlers.InvoiceLineControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class InvoicesLinesConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -5880556340213327554L;
	
	private ModalNewInvoice parent;
	
	public InvoicesLinesConfig( AppContext ctx, ModalNewInvoice parent )
	{
		super( ctx );
	
		this.parent = parent;
		
		setOrderby( "id" );
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
		return new String[]{ "title", "price", "quantity", "total_packed" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "price", "quantity" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new BaseTable( InvoiceLineVto.class, this, getContext() );
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
		InvoiceLine query = new InvoiceLine();

		query.setRef_invoice( parent.getNewInvoice().getId() );
		
		return query;
	}

	@Override
	public void onOperationNew()
	{
		//new ModalNewQuotationLine( getContext(), Operation.OP_ADD, null, parent ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		//new ModalNewQuotationLine( getContext(), Operation.OP_MODIFY, (QuotationLine)dto, parent ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new InvoiceLineControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new InvoiceLine();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._InvoicesLinesManager;
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

