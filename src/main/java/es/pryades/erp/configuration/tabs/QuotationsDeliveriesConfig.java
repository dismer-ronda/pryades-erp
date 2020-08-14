package es.pryades.erp.configuration.tabs;

import java.util.List;

import com.vaadin.ui.Component;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.common.ModalWindowsCRUD.Operation;
import es.pryades.erp.configuration.modals.ModalNewQuotation;
import es.pryades.erp.configuration.modals.ModalNewQuotationDelivery;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.QuotationDelivery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.QuotationDeliveryVto;
import es.pryades.erp.vto.controlers.QuotationDeliveryControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class QuotationsDeliveriesConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = 7338073506355958346L;
	
	private ModalNewQuotation parent;
	
	public QuotationsDeliveriesConfig( AppContext ctx, ModalNewQuotation parent )
	{
		super( ctx );
	
		this.parent = parent;
		
		setOrderby( "departure_date" );
		setOrder( "asc" );
	}

	@Override
	public String getResourceKey()
	{
		return "quotationsDeliveriesConfig";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "departure_date", "departure_port", "internal_terms", "internal_cost", "external_terms", "external_cost", "free_delivery" };
	}

	@Override
	public BaseTable createTable() throws BaseException
	{
		return new BaseTable( QuotationDeliveryVto.class, this, getContext() );
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
		QuotationDelivery query = new QuotationDelivery();

		query.setRef_quotation( parent.getNewQuotation().getId() );
		
		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewQuotationDelivery( getContext(), Operation.OP_ADD, null, parent ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewQuotationDelivery( getContext(), Operation.OP_MODIFY, (QuotationDelivery)dto, parent ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new QuotationDeliveryControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new QuotationDelivery();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._QuotationsDeliveriesManager;
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

