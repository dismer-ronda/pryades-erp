package es.pryades.erp.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.configuration.tabs.AuditsConfig;
import es.pryades.erp.configuration.tabs.CompaniesConfig;
import es.pryades.erp.configuration.tabs.ParametersConfig;
import es.pryades.erp.configuration.tabs.ProfilesConfig;
import es.pryades.erp.configuration.tabs.TasksConfig;
import es.pryades.erp.configuration.tabs.UsersConfig;

import com.vaadin.ui.VerticalLayout;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Dismer Ronda
 * 
 */
public class EnermetTabbedContainerConfig extends VerticalLayout implements TabSheet.SelectedTabChangeListener
{
	private static final long serialVersionUID = 4707864569555868624L;

	private static final Logger LOG = Logger.getLogger( EnermetTabbedContainerConfig.class );

	@Getter @Setter private AppContext context;
	
	private VerticalLayout content;
	private TabSheet tabsheet;
	private List<PagedContent> tabContentList;

	public EnermetTabbedContainerConfig( AppContext context ) 
	{
		this.context = context;
	}
	
	public void selectedTabChange( SelectedTabChangeEvent event )
	{
		TabSheet tabsheet = event.getTabSheet();
		Tab tab = tabsheet.getTab( tabsheet.getSelectedTab() );
		PagedContent configuration = (PagedContent)tab.getComponent();
		configuration.initializeComponent();
	}

	private void initComponents() throws BaseException
	{
		try
		{
			AppContext ctx = getContext();

			if ( content == null )
				content = new VerticalLayout();

			if ( tabsheet == null )
				tabsheet = new TabSheet();

			if ( tabContentList == null )
			{
				tabContentList = new ArrayList<PagedContent>();

				if ( ctx.hasRight( "configuration.companies" ) )
					tabContentList.add( new CompaniesConfig( ctx ) );

				if ( ctx.hasRight( "configuration.tasks" ) )
					tabContentList.add( new TasksConfig( ctx ) );

				if ( ctx.hasRight( "configuration.users" ) )
					tabContentList.add( new UsersConfig( ctx ) );

				if ( ctx.hasRight( "configuration.parameters" ) )
					tabContentList.add( new ParametersConfig( ctx ) );

				if ( ctx.hasRight( "configuration.profiles" ) )
					tabContentList.add( new ProfilesConfig( ctx ) );

				if ( ctx.hasRight( "configuration.audits" ) )
					tabContentList.add( new AuditsConfig( ctx ) );

				for ( PagedContent item : tabContentList )
				{
					item.setSizeFull();
					tabsheet.addTab( item );
					tabsheet.getTab( item ).setCaption( item.getTitle() );
				}

				PagedContent configuration = (PagedContent)tabContentList.get( 0 );
				configuration.initializeComponent();
				tabsheet.setSelectedTab( configuration );

				tabsheet.addSelectedTabChangeListener( this );
			}

		}
		catch ( Throwable e )
		{
			if ( !(e instanceof BaseException) )
				throw new BaseException( e, LOG, BaseException.UNKNOWN );
			
			throw (BaseException)e;
		}
	}

	public void render() throws BaseException
	{
		this.initComponents();
		
		content.setSizeFull();
		tabsheet.setSizeFull();

		content.addComponent( tabsheet );

		addComponent( content );
	}
}
