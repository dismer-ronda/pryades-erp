package es.pryades.erp.configuration.modals;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewParameter extends ModalWindowsCRUD
{
	private static final long serialVersionUID = 8379631940980822844L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalNewParameter.class );

	protected Parameter newParameter;

	private TextField editValue;

	/**
	 * 
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewParameter( AppContext context, OperationCRUD modalOperation, Parameter orgParameter, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgParameter );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		try
		{
			newParameter = (Parameter) Utils.clone( (Parameter) orgDto );
		}
		catch ( Throwable e1 )
		{
			newParameter = new Parameter();
		}

		bi = new BeanItem<BaseDto>( newParameter );

		editValue = new TextField( bi.getItemProperty( "value" ) );
		editValue.setWidth( "100%" );
		editValue.setNullRepresentation( "" );
		
		Label label = new Label( newParameter.getDescription() );
		label.setWidth( "-1px" );
		
		HorizontalLayout rowName = new HorizontalLayout();
		rowName.setWidth( "100%" );
		rowName.setSpacing( true );
		rowName.addComponent( label );
		rowName.addComponent( editValue );
		rowName.setExpandRatio( editValue, 1.0f );

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( rowName );

		componentsContainer.addComponent( row1 );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewParameter";
	}

	@Override
	protected void defaultFocus()
	{
		editValue.focus();
	}

	@Override
	protected boolean onAdd()
	{
		return false;
	}

	@Override
	protected boolean onModify()
	{
		try
		{
			IOCManager._ParametersManager.setRow( getContext(), (Parameter) orgDto, newParameter );

			IOCManager._ParametersManager.loadParameters( getContext() );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onDelete()
	{
		return false;
	}

	@Override
	public boolean checkAddRight()
	{
		return false;
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.parameters.modify" );
	}

	@Override
	protected boolean hasDelete()
	{
		return false;
	}
}
