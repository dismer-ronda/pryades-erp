package es.pryades.erp.configuration.modals;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Profile;
import es.pryades.erp.dto.ProfileRight;
import es.pryades.erp.dto.Right;
import es.pryades.erp.ioc.IOCManager;
import lombok.Getter;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewProfile extends ModalWindowsCRUD
{
	private static final long serialVersionUID = -5487483550129077794L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalNewProfile.class );

	@Getter
	protected Profile newProfile;

	private TextField editDescription;

	@Getter
	private List<ProfileRight> profileRights;
	private List<Right> rights;

	private ArrayList<CheckBox> checksLines;

	/**
	 * 
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewProfile( AppContext context, OperationCRUD modalOperation, Profile orgProfile, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgProfile );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		try
		{
			newProfile = (Profile) Utils.clone( (Profile) orgDto );
		}
		catch ( Throwable e1 )
		{
			newProfile = new Profile();
		}

		bi = new BeanItem<BaseDto>( newProfile );

		loadProfileRights();
		loadRights();
		
		editDescription = new TextField( getContext().getString( "modalNewProfile.description" ) );
		editDescription.setWidth( "100%" );
		editDescription.setNullRepresentation( "" );
		editDescription.setRequired( true );
		editDescription.setRequiredError( getContext().getString( "words.required" ) );
		editDescription.setPropertyDataSource( bi.getItemProperty( "description" ) );
		
		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( editDescription );

		componentsContainer.addComponent( row2 );
		
		Panel panelLines = new Panel( getContext().getString( "modalNewProfile.rights" ) );
		panelLines.setStyleName( "borderless light" );
		panelLines.setHeight( "400px" );

		VerticalLayout col = new VerticalLayout();
		
		checksLines = new ArrayList<CheckBox>();
		
		for ( Right customer: rights )
		{
			CheckBox checkLine = new CheckBox( getContext().getString( "right."+ customer.getCode() ) );
			checkLine.setData( customer );
			checkLine.setValue( hasProfileRight( customer ) ); 

			col.addComponent( checkLine );
			checksLines.add( checkLine );
		}
		
		panelLines.setContent( col );

		componentsContainer.addComponent( panelLines );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewProfile";
	}

	@Override
	protected void defaultFocus()
	{
		editDescription.focus();
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
			IOCManager._ProfilesManager.setRow( getContext(), (Profile) orgDto, newProfile );
	
			for ( ProfileRight profileRight : profileRights )
				IOCManager._ProfilesRightsManager.delRow( getContext(), profileRight );
			
			for ( CheckBox check : checksLines )
			{
				if ( check.getValue().booleanValue() )
				{
					Right right = (Right)check.getData();
					
					ProfileRight profileRight = new ProfileRight();
					profileRight.setRef_profile( newProfile.getId() );
					profileRight.setRef_right( right.getId() );
					
					IOCManager._ProfilesRightsManager.setRow( getContext(), null, profileRight );
				}
			}

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

	@SuppressWarnings("unchecked")
	private void loadProfileRights()
	{
		if ( newProfile.getId() != null )
		{
			try
			{
				ProfileRight query = new ProfileRight();
				query.setRef_profile( newProfile.getId() );
	
				profileRights = IOCManager._ProfilesRightsManager.getRows( getContext(), query );
				
				return;
			}
			catch ( Throwable e )
			{
				
			}
		}
		
		profileRights = new ArrayList<ProfileRight>();
	}

	@SuppressWarnings("unchecked")
	private void loadRights()
	{
		try
		{
			rights = IOCManager._RightsManager.getRows( getContext(), new Right() );
		}
		catch ( Throwable e )
		{
			rights = new ArrayList<Right>();
		}
	}

	@Override
	protected boolean hasDelete()
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
		return getContext().hasRight( "configuration.profiles.modify" );
	}

	private boolean hasProfileRight( Right right )
	{
		for ( ProfileRight userCompany : profileRights )
			if ( userCompany.getRef_right().equals( right.getId() ) )
				return true;
		
		return false;
	}

}
