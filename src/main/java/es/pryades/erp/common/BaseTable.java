package es.pryades.erp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;

import org.apache.log4j.Logger;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Query;

/**
 * 
 * @author Dismer Ronda
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BaseTable extends VerticalLayout
{
	private static final long serialVersionUID = -2723943854769099953L;

	private static final Logger LOG = Logger.getLogger( BaseTable.class ); 
	
	@Getter protected CustomTable table = new CustomTable();
	@Getter protected HashMap<Item, Object> rawTableContent;
	@Getter protected AppContext context;

	protected HorizontalLayout tableContainer;
	
	protected String tableTitle="";
	protected List<String> tableHeaders;
	protected List<String> visibleCols;	
	
	protected VtoControllerFactory factory;
	
	protected HashMap<String,String> tableHeadersNames;
	
	protected List objectListColection;
	private Class<?> tableObjectType;
	
	public BaseTable(Class<?> tableObjectType, VtoControllerFactory factory, AppContext ctx)throws BaseException
    {
    	this.tableObjectType = tableObjectType;
    	
    	this.factory = factory; 
    	this.rawTableContent = new HashMap<Item, Object>();
    	this.context = ctx;
    }
    
	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	   	table.setCaption(this.tableTitle);
	}

	public void setVisibleCols(List<String> visibles) {
		if (visibleCols == null){
			visibleCols = new ArrayList<String>();
		}
		
		visibleCols.clear();
		
		for (String col : visibles) {
			visibleCols.add( col );
		}
	}
	
	public void setRawTableContent(HashMap<Item, Object> rawTableContent) {
		this.rawTableContent = rawTableContent;
	}
	
	public HashMap<String, String> getTableHeadersNames() {
		return tableHeadersNames;
	}

	public void setTableHeadersNames(HashMap<String, String> tableHeadersNames) {
		this.tableHeadersNames = tableHeadersNames;
	}
	
	public void initializeTable( String resourceKey ) throws BaseException
	{
		this.setSizeFull();
    	
    	table.addStyleName( "striped" );

		this.table.setWidth("100%");
		this.table.setHeight("100%");

        this.table.setImmediate(true); 

        this.table.setSelectable(true);
        this.table.setMultiSelect(false); 
        //this.table.setColumnReorderingAllowed(true);
        //this.table.setColumnCollapsingAllowed(true);
        
        this.tableContainer = new HorizontalLayout();
        this.tableContainer.setWidth("100%");
        this.tableContainer.setHeight("100%");
        this.tableContainer.addComponent(table);
        this.tableContainer.setExpandRatio(table, 1.0f);
    	this.addComponent(tableContainer);
    	this.setExpandRatio(tableContainer, 1.0f);
    	
    	table.setResourceKey( resourceKey );
    	table.setContext( getContext() );
    	
    	//table.setControllerFactory( factory );
    	//table.setContext( getContext() );
    	//table.setVisibleCols( visibleCols );
    	//table.setColumns( visibleCols.size() );
    	
    	for ( String keyCol : visibleCols ) 
    		table.addContainerProperty(keyCol, Utils.getFieldClass( tableObjectType, keyCol ), null, tableHeadersNames.get(keyCol), null, Table.Align.LEFT );
	}

	protected void fillTableContainer(List objectElemsList, String idFieldName, List tableElemsList) throws BaseException
	{
		this.clearTableData();
		objectListColection = objectElemsList;
		
		if ( tableElemsList.size() > 0 )
		{
			int i = 0;

			for ( Object rowObject: tableElemsList ) 
	        {
    			Object id = Utils.getFieldObject( rowObject, idFieldName );
        		
    			Item item = table.addItem( id );

        		this.rawTableContent.put(item, objectElemsList.get(i));
        	
        		for (String keyCol : visibleCols) 
        		{
        			Property property = item.getItemProperty(keyCol);
        			
    				property.setValue( Utils.getFieldObject( rowObject, keyCol ) );
        		}
	        	
	        	i++;
	        }
		}
    }
	
	public Object getRowValue(Long rowId) throws BaseException
	{
		return rawTableContent.get( table.getItem(rowId) );
    }
	
	public void clearTableData()
	{
		this.table.removeAllItems();
		this.table.markAsDirty();;
		
		if(this.objectListColection != null)
			this.objectListColection.clear();
	}

	public void loadTableValues(List<BaseDto> objListElems) throws BaseException
	{
		ArrayList<GenericVto> tableValues = new ArrayList<GenericVto>();

		GenericControlerVto controller = factory.getControlerVto(context);

		for ( BaseDto elemItem: objListElems )
			tableValues.add((GenericVto) controller.generateVtoFromDto(factory, elemItem));

		GenericControlerVto elemItemControler = factory.getControlerVto(context);
		
        fillTableContainer(objListElems, elemItemControler.getVtoIdFieldName(), tableValues);
	}
	
	public void refreshVisibleContent( Query query, boolean repage  )
	{
		try
		{
			List<BaseDto>  listaElementos = factory.getFieldManagerImp().getRows( getContext(), query);
			factory.preProcessRows(listaElementos);

			loadTableValues(listaElementos);
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
 
	public void focus()
	{
		table.focus();
	}
	
	public HorizontalLayout getTablePageOppContainer() 
	{
		return null;
	}
	
	public long getNumberOfRows()
	{
		return table.size();
	}
}
