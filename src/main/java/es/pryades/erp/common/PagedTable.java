package es.pryades.erp.common;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Query;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * 
 * @author Dismer Ronda
 *
 */
public class PagedTable extends BaseTable implements TablePaginator
{
	private static final long serialVersionUID = 3977615258355978643L;
	private static final Logger LOG = Logger.getLogger( PagedTable.class );

	protected Button pageButtonIni;
	protected Button pageButtonPrevious;

	protected Label lbCurrPage;
	protected Label lbPageCantOf;
	protected Label lbTotalPage;
	
	protected Button pageButtonEnd;
	protected Button pageButtonNext;

	protected HorizontalLayout tablePageOppContainer;
	
	protected TablePaginator enermetPaginator;
	
	protected Integer pageSize;
	 
    public PagedTable( Class<?> tableObjectType, VtoControllerFactory factory, AppContext ctx, Integer pageSize ) throws BaseException
    {
    	super(tableObjectType, factory, ctx);
    	
    	this.pageSize = pageSize;
    	
    	enermetPaginator = new EnermetPaginatorImp(pageSize, factory, ctx);
    }
    
	private void addButtnIniListener()
	{
		pageButtonIni.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = -3014118248413804001L;

			@Override
			public void buttonClick(ClickEvent event) 
			{
				try 
				{
					refreshVisibleContent(getFirstPage());
				} 
				catch (Throwable e) 
				{
					Utils.logException( e, LOG );
				}
			}
		});
	}
	
	private void addButtnNextListener()
	{
		pageButtonNext.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = 9138731042237969082L;

			@Override
			public void buttonClick(ClickEvent event) 
			{
				try 
				{
					refreshVisibleContent(getNextPage());
				} 
				catch (Throwable e) 
				{
					Utils.logException( e, LOG );
				}
			}
		});
	}
	
	private void addButtnPreviousListener()
	{
		pageButtonPrevious.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = 8492032704650355736L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				try 
				{
					refreshVisibleContent(getPreviousPage());
				} 
				catch (Throwable e) 
				{
					Utils.logException( e, LOG );
				}
			}
		});
	}
	
	private void addButtnEndListener()
	{
		pageButtonEnd.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = -6446606919092770538L;

			@Override
			public void buttonClick(ClickEvent event) 
			{
				try 
				{
					refreshVisibleContent(getLastPage());
				} 
				catch (Throwable e) 
				{
					Utils.logException( e, LOG );
				}
			}
		});
	}

	public EnermetPaginatorFilter getEnermetPaginatorFilter()
	{
		return (EnermetPaginatorFilter)enermetPaginator;
	}
	
	public TablePaginator getEnermetPaginator()
	{
		return enermetPaginator;
	}

	@Override
	public void initializeTable( String resourceKey ) throws BaseException
	{
		super.initializeTable( resourceKey );
    
    	lbCurrPage =  new Label();
    	lbCurrPage.setValue(" ");

    	lbPageCantOf = new Label(getContext().getString( "table.of"));
    	
    	lbTotalPage =  new Label();
    	lbTotalPage.setValue(" ");
    	
    	pageButtonIni = new Button();
		pageButtonIni.setStyleName( "borderless" );
		pageButtonIni.setDescription(getContext().getString( "table.first"));
		pageButtonIni.setIcon( new ThemeResource( "images/first.png" ) );
		pageButtonIni.setEnabled(false);
		addButtnIniListener();
		
    	pageButtonPrevious = new Button();
		pageButtonPrevious.setStyleName( "borderless" );
		pageButtonPrevious.setDescription(getContext().getString( "table.previous"));
		pageButtonPrevious.setIcon( new ThemeResource( "images/left.png" ) );
		pageButtonPrevious.setEnabled(false);
		addButtnPreviousListener();
		
    	pageButtonNext = new Button();
		pageButtonNext.setStyleName( "borderless" );
		pageButtonNext.setDescription(getContext().getString( "table.next"));
		pageButtonNext.setIcon( new ThemeResource( "images/right.png" ) );
		pageButtonNext.setEnabled(false);
		addButtnNextListener();
		
		pageButtonEnd = new Button();
		pageButtonEnd.setStyleName( "borderless" );
		pageButtonEnd.setDescription(getContext().getString( "table.last"));
		pageButtonEnd.setIcon( new ThemeResource( "images/last.png" ) );
		pageButtonEnd.setEnabled(false);
		addButtnEndListener();
		
    	tablePageOppContainer = new HorizontalLayout();
    	tablePageOppContainer.setSpacing(true);
    	tablePageOppContainer.addComponent(pageButtonIni);
    	tablePageOppContainer.addComponent(pageButtonPrevious);
    	tablePageOppContainer.addComponent(lbCurrPage);
    	tablePageOppContainer.addComponent(lbPageCantOf);
    	tablePageOppContainer.addComponent(lbTotalPage);
    	tablePageOppContainer.addComponent(pageButtonNext);	
    	tablePageOppContainer.addComponent(pageButtonEnd);	
	}
	
	private void updateNavButtons()
	{
		pageButtonIni.setEnabled(isFirstPageEnable());
		pageButtonPrevious.setEnabled(isPreviousPageEnable());
		pageButtonEnd.setEnabled(isLastPageEnable());
		pageButtonNext.setEnabled(isNextPageEnable());
		
		lbCurrPage.setValue( Long.toString( getCurrPag() ) );
		lbTotalPage.setValue( Long.toString( getTotalPag() ) );
		
		if ( getTotalPag() > 1 )
		{
			pageButtonIni.setVisible(true);
			pageButtonIni.setVisible(true);
			pageButtonPrevious.setVisible(true);
			pageButtonEnd.setVisible(true);
			pageButtonNext.setVisible(true);
		}
		else
		{
			pageButtonIni.setVisible(false);
			pageButtonIni.setVisible(false);
			pageButtonPrevious.setVisible(false);
			pageButtonEnd.setVisible(false);
			pageButtonNext.setVisible(false);
		}
	}
	
	public void refreshVisibleContent(List<BaseDto> listadoElementos)
	{
		try
		{
			loadTableValues(listadoElementos); 
			
	        updateNavButtons();
		}
		catch (Throwable e) 
    	{
			Utils.logException( e, LOG );
		}
	}

	@Override
	public List<BaseDto>  getPageNum(Long pagNum) throws BaseException {
		return null;
	}

	@Override
	public List<BaseDto>  getFirstPage() throws BaseException 
	{
		List<BaseDto>  result = enermetPaginator.getFirstPage();
		updateNavButtons();
		
		return result;
	}

	@Override
	public List<BaseDto>  getLastPage() throws BaseException 
	{
		List<BaseDto>  result = enermetPaginator.getLastPage();
		updateNavButtons();
		
		return result;
	}

	@Override
	public List<BaseDto>  getCurrPage() throws BaseException 
	{
		List<BaseDto>  result = enermetPaginator.getCurrPage();
		updateNavButtons();
		
		return result;
	}

	@Override
	public List<BaseDto>  getNextPage() throws BaseException
	{
		List<BaseDto>  result = enermetPaginator.getNextPage();
		updateNavButtons();
		
		return result;
	}

	@Override
	public List<BaseDto> getPreviousPage() throws BaseException 
	{
		List<BaseDto> result = enermetPaginator.getPreviousPage();
		updateNavButtons();
		
		return result;
	}

	@Override
	public Boolean isFirstPageEnable() 
	{
		return enermetPaginator.isFirstPageEnable();
	}

	@Override
	public Boolean isLastPageEnable()
	{
		return enermetPaginator.isLastPageEnable();
	}

	@Override
	public Boolean isNextPageEnable() 
	{
		return enermetPaginator.isNextPageEnable();
	}

	@Override
	public Boolean isPreviousPageEnable()
	{
		return enermetPaginator.isPreviousPageEnable();
	}

	@Override
	public long getCurrPag() 
	{
		return enermetPaginator.getCurrPag();
	}

	@Override
	public long getTotalPag() 
	{
		return enermetPaginator.getTotalPag();
	}

	@Override
	public HorizontalLayout getTablePageOppContainer() {
		return tablePageOppContainer;
	}

	public void setTablePageOppContainer(HorizontalLayout tablePageOppContainer) {
		this.tablePageOppContainer = tablePageOppContainer;
	}

	@Override
	public void refreshVisibleContent( Query query, boolean repage )
	{
		try 
		{
			getEnermetPaginatorFilter().setPaginatorQuery( query );
			
			refreshVisibleContent( repage ? getEnermetPaginatorFilter().getFirstPage() : getEnermetPaginatorFilter().getCurrPage() );

			updateNavButtons();
		} 
		catch ( Throwable e) 
		{
			Utils.logException( e, LOG );
		}
	}
	
	public long getNumberOfRows()
	{
		return getTotalPag() * pageSize;
	}
	
}
