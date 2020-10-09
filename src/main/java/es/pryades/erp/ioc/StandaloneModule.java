package es.pryades.erp.ioc;

import org.apache.tapestry5.ioc.ServiceBinder;

import es.pryades.erp.dal.*;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public class StandaloneModule 
{
	/**
	 * Registra los servicios soportados mediante IOC
	 * 
	 * @param binder: Binder de servicios en el que estos se registrarán
	 *         
	 */
	public static void bind( ServiceBinder binder )
	{
		binder.bind( UsersManager.class, UsersManagerImpl.class );
		binder.bind( RightsManager.class, RightsManagerImpl.class);
		binder.bind( ProfilesManager.class, ProfilesManagerImpl.class);
		binder.bind( ProfilesRightsManager.class, ProfilesRightsManagerImpl.class);
		binder.bind( ParametersManager.class, ParametersManagerImpl.class);
		binder.bind( UserDefaultsManager.class, UserDefaultsManagerImpl.class);
		binder.bind( AuditsManager.class, AuditsManagerImpl.class);
		binder.bind( FilesManager.class, FilesManagerImpl.class);
		binder.bind( TasksManager.class, TasksManagerImpl.class);
		binder.bind( CompaniesManager.class, CompaniesManagerImpl.class);
		binder.bind( QuotationsManager.class, QuotationsManagerImpl.class);
		binder.bind( QuotationsDeliveriesManager.class, QuotationsDeliveriesManagerImpl.class);
		binder.bind( QuotationsLinesManager.class, QuotationsLinesManagerImpl.class);
		binder.bind( QuotationsLinesDeliveriesManager.class, QuotationsLinesDeliveriesManagerImpl.class);
		binder.bind( QuotationsAttachmentsManager.class, QuotationsAttachmentsManagerImpl.class);
		binder.bind( InvoicesManager.class, InvoicesManagerImpl.class);
		binder.bind( InvoicesLinesManager.class, InvoicesLinesManagerImpl.class);
		binder.bind( ShipmentsManager.class, ShipmentsManagerImpl.class);
		binder.bind( ShipmentsBoxesManager.class, ShipmentsBoxesManagerImpl.class);
		binder.bind( ShipmentsBoxesLinesManager.class, ShipmentsBoxesLinesManagerImpl.class);
		binder.bind( UsersCompaniesManager.class, UsersCompaniesManagerImpl.class);
		binder.bind( CompaniesContactsManager.class, CompaniesContactsManagerImpl.class);
		binder.bind( OperationsManager.class, OperationsManagerImpl.class);
		binder.bind( PurchasesManager.class, PurchasesManagerImpl.class);
	}
}
