package es.pryades.erp.ioc;

import java.io.IOException;
import java.io.Serializable;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

import es.pryades.erp.dal.AccountsManager;
import es.pryades.erp.dal.AuditsManager;
import es.pryades.erp.dal.CompaniesContactsManager;
import es.pryades.erp.dal.CompaniesManager;
import es.pryades.erp.dal.FilesManager;
import es.pryades.erp.dal.InvoicesLinesManager;
import es.pryades.erp.dal.InvoicesManager;
import es.pryades.erp.dal.OperationsManager;
import es.pryades.erp.dal.ParametersManager;
import es.pryades.erp.dal.ProfilesManager;
import es.pryades.erp.dal.ProfilesRightsManager;
import es.pryades.erp.dal.PurchasesManager;
import es.pryades.erp.dal.QuotationsAttachmentsManager;
import es.pryades.erp.dal.QuotationsDeliveriesManager;
import es.pryades.erp.dal.QuotationsLinesDeliveriesManager;
import es.pryades.erp.dal.QuotationsLinesManager;
import es.pryades.erp.dal.QuotationsManager;
import es.pryades.erp.dal.RightsManager;
import es.pryades.erp.dal.ShipmentsBoxesLinesManager;
import es.pryades.erp.dal.ShipmentsBoxesManager;
import es.pryades.erp.dal.ShipmentsManager;
import es.pryades.erp.dal.TasksManager;
import es.pryades.erp.dal.UserDefaultsManager;
import es.pryades.erp.dal.UsersCompaniesManager;
import es.pryades.erp.dal.UsersManager;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@SuppressWarnings( {"unchecked","rawtypes"}) 
public class IOCManager  implements Serializable 
{
	private static final long serialVersionUID = 6442290324001703150L;

	static IOCManager instance = null;
	
	private RegistryBuilder builder;
	private Registry registry;

	public static UsersManager _UsersManager;
	public static RightsManager _RightsManager;
	public static ProfilesManager _ProfilesManager;
	public static ProfilesRightsManager _ProfilesRightsManager;
	public static ParametersManager _ParametersManager;
	public static UserDefaultsManager _UserDefaultsManager;
	public static AuditsManager _AuditsManager;
	public static FilesManager _FilesManager;
	public static TasksManager _TasksManager;
	public static CompaniesManager _CompaniesManager;
	public static QuotationsManager _QuotationsManager;
	public static QuotationsDeliveriesManager _QuotationsDeliveriesManager;
	public static QuotationsLinesManager _QuotationsLinesManager;
	public static QuotationsLinesDeliveriesManager _QuotationsLinesDeliveriesManager;
	public static QuotationsAttachmentsManager _QuotationsAttachmentsManager;
	public static InvoicesManager _InvoicesManager;
	public static InvoicesLinesManager _InvoicesLinesManager;
	public static ShipmentsManager _ShipmentsManager;
	public static ShipmentsBoxesManager _ShipmentsBoxesManager;
	public static ShipmentsBoxesLinesManager _ShipmentsBoxesLinesManager;
	public static UsersCompaniesManager _UsersCompaniesManager;
	public static CompaniesContactsManager _CompaniesContactsManager;
	public static OperationsManager _OperationsManager;
	public static PurchasesManager _PurchasesManager;
	public static AccountsManager _AccountsManager;

	public IOCManager() 
	{
		super();
		
		builder = new RegistryBuilder();
		builder.add( StandaloneModule.class );

		registry = builder.build();
		registry.performRegistryStartup();
	}

	/**
	 * Obtiene la instancia del manager de IOC
	 * 
	 * @return <code>IOCManager</code> Devuelve la instancia global del manager de IOC
	 *         
	 */
	public static IOCManager getInstance()
	{
		return instance;
	}
	
	/**
	 * Inicializa el manager de IOC 
	 * 
	 * @return <code>IOCManager</code> Devuelve una instancia del manager global de IOC
	 *         
	 */
	public static void Init() throws IOException
	{
		if ( instance == null )
			instance = new IOCManager();
		
		_UsersManager = (UsersManager)getInstanceOf( UsersManager.class );
		_RightsManager = (RightsManager)getInstanceOf( RightsManager.class );
		_ProfilesManager = (ProfilesManager)getInstanceOf( ProfilesManager.class );
		_ProfilesRightsManager = (ProfilesRightsManager)getInstanceOf( ProfilesRightsManager.class );
		_ParametersManager = (ParametersManager)getInstanceOf( ParametersManager.class );
		_UserDefaultsManager = (UserDefaultsManager)getInstanceOf( UserDefaultsManager.class );
		_AuditsManager = (AuditsManager)getInstanceOf( AuditsManager.class );
		_FilesManager = (FilesManager)getInstanceOf( FilesManager.class );
		_TasksManager = (TasksManager)getInstanceOf( TasksManager.class );
		_CompaniesManager = (CompaniesManager)getInstanceOf( CompaniesManager.class );
		_QuotationsManager = (QuotationsManager)getInstanceOf( QuotationsManager.class );
		_QuotationsDeliveriesManager = (QuotationsDeliveriesManager)getInstanceOf( QuotationsDeliveriesManager.class );
		_QuotationsLinesManager = (QuotationsLinesManager)getInstanceOf( QuotationsLinesManager.class );
		_QuotationsLinesDeliveriesManager = (QuotationsLinesDeliveriesManager)getInstanceOf( QuotationsLinesDeliveriesManager.class );
		_QuotationsAttachmentsManager = (QuotationsAttachmentsManager)getInstanceOf( QuotationsAttachmentsManager.class );
		_InvoicesManager = (InvoicesManager)getInstanceOf( InvoicesManager.class );
		_InvoicesLinesManager = (InvoicesLinesManager)getInstanceOf( InvoicesLinesManager.class );
		_ShipmentsManager = (ShipmentsManager)getInstanceOf( ShipmentsManager.class );
		_ShipmentsBoxesManager = (ShipmentsBoxesManager)getInstanceOf( ShipmentsBoxesManager.class );
		_ShipmentsBoxesLinesManager = (ShipmentsBoxesLinesManager)getInstanceOf( ShipmentsBoxesLinesManager.class );
		_UsersCompaniesManager = (UsersCompaniesManager)getInstanceOf( UsersCompaniesManager.class );
		_CompaniesContactsManager = (CompaniesContactsManager)getInstanceOf( CompaniesContactsManager.class );
		_OperationsManager = (OperationsManager)getInstanceOf( OperationsManager.class );
		_PurchasesManager = (PurchasesManager)getInstanceOf( PurchasesManager.class );
		_AccountsManager = (AccountsManager)getInstanceOf( AccountsManager.class );
	}
	
	/**
	 * Obtiene una instancia de un servicio
	 * 
	 * @param clazz: Interfaz del servicio solicitado.
	 * @return <code>Object</code> Devuelve una instancia de la interfaz del servicio solicitado
	 *         
	 */
	public static Object getInstanceOf( Class clazz )
	{
		return getInstance().registry.getService( clazz );
	}
}
