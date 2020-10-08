package es.pryades.erp.configuration.modals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import es.pryades.erp.application.ChangePasswordDlg;
import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Profile;
import es.pryades.erp.dto.User;
import es.pryades.erp.dto.UserCompany;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

@SuppressWarnings({ "unchecked" })
public class ModalNewUser extends ModalWindowsCRUD implements Receiver, SucceededListener
{
	private static final long serialVersionUID = -5095261281038496825L;

	private static final Logger LOG = Logger.getLogger( ModalNewUser.class );

	protected User newUser;

	private TextField editUserLogin;
	private TextField editUserEmail;
	private TextField editUserName;
	private PasswordField editUserPassword;
	private ComboBox comboProfile;
	private ComboBox comboStatus;

	private List<Company> customers;
	private List<UserCompany> userCompanies;
	
	private ArrayList<CheckBox> checksLines;

	private Label labelAttachment;
	private String fileName;
	private ByteArrayOutputStream os;

	private UserDefault lastPassword1;
	private UserDefault lastPassword2;
	
	/**
	 * 
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewUser( AppContext context, OperationCRUD modalOperation, User orgUser, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgUser );
		setWidth( "750px" );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		lastPassword1 = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.LAST_PASSWORD1 ); 
		lastPassword2 = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.LAST_PASSWORD2 ); 

		try
		{
			newUser = (User) Utils.clone( (User) orgDto );
		}
		catch ( Throwable e1 )
		{
			newUser = new User();
			
			newUser.setStatus( User.PASS_NEW );
		}

		bi = new BeanItem<BaseDto>( newUser );

		loadCustomers();
		loadUserCompanies();

		editUserLogin = new TextField( getContext().getString( "words.login.noun" ) );
		editUserLogin.setWidth( "100%" );
		editUserLogin.setNullRepresentation( "" );
		editUserLogin.setRequired( true );
		editUserLogin.setRequiredError( getContext().getString( "words.required" ) );
		editUserLogin.setPropertyDataSource( bi.getItemProperty( "login" ) );

		editUserEmail = new TextField( getContext().getString( "words.email" ) );
		editUserEmail.setWidth( "100%" );
		editUserEmail.setNullRepresentation( "" );
		editUserEmail.setRequired( true );
		editUserEmail.setRequiredError( getContext().getString( "words.required" ) );
		editUserEmail.setPropertyDataSource( bi.getItemProperty( "email" ) );
		
		editUserName = new TextField( getContext().getString( "words.name" ) );
		editUserName.setWidth( "100%" );
		editUserName.setNullRepresentation( "" );
		editUserName.setRequired( true );
		editUserName.setRequiredError( getContext().getString( "words.required" ) );
		editUserName.setPropertyDataSource( bi.getItemProperty( "name" ) );

		editUserPassword = new PasswordField( getContext().getString( "words.password" ) );
		editUserPassword.setWidth( "100%" );
		editUserPassword.setNullRepresentation( "" );
		editUserPassword.setRequired( true );
		editUserPassword.setRequiredError( getContext().getString( "words.required" ) );
		editUserPassword.setPropertyDataSource( bi.getItemProperty( "pwd" ) );

		comboProfile = new ComboBox( getContext().getString( "words.profile" ) );
		comboProfile.setWidth( "100%" );
		comboProfile.setNullSelectionAllowed( false );
		comboProfile.setTextInputAllowed( false );
		comboProfile.setImmediate( true );
		comboProfile.setPropertyDataSource( bi.getItemProperty( "ref_profile" ) );
		comboProfile.setRequired( true );
		comboProfile.setRequiredError( getContext().getString( "words.required" ) );
		fillComboProfiles();

		comboStatus = new ComboBox( getContext().getString( "modalNewUser.password.status" ) );
		comboStatus.setWidth( "100%" );
		comboStatus.setNullSelectionAllowed( false );
		comboStatus.setTextInputAllowed( false );
		comboStatus.setImmediate( true );
		comboStatus.setPropertyDataSource( bi.getItemProperty( "status" ) );
		comboStatus.setReadOnly( false );
		comboStatus.setRequired( true );
		comboStatus.setRequiredError( getContext().getString( "words.required" ) );
		fillComboStatus();
		
		labelAttachment = new Label();

		Upload upload = new Upload( null, this );
		upload.setImmediate( true );
		upload.setButtonCaption( getContext().getString( "modalNewUser.captionUploadButton" ) );
		upload.addSucceededListener( this );

		getCustomOperationsRow().addComponent( upload );
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( editUserName );
		row1.addComponent( editUserEmail );
		
		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( editUserLogin );
		row2.addComponent( editUserPassword );

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( comboProfile );
		row3.addComponent( comboStatus );

		HorizontalLayout row4 = new HorizontalLayout();
		row4.setSpacing( true );
		row4.addComponent( labelAttachment );
		row4.setComponentAlignment( labelAttachment, Alignment.MIDDLE_CENTER );

		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		componentsContainer.addComponent( row3 );
		
		Panel panelLines = new Panel( getContext().getString( "modalNewUser.companies" ) );
		panelLines.setStyleName( "borderless light" );
		panelLines.setHeight( "400px" );

		VerticalLayout col = new VerticalLayout();
		
		checksLines = new ArrayList<CheckBox>();
		
		for ( Company customer: customers )
		{
			CheckBox checkLine = new CheckBox( customer.getName() );
			checkLine.setData( customer );
			checkLine.setValue( isCompanyAuthorized( customer ) ); 

			col.addComponent( checkLine );
			checksLines.add( checkLine );
		}
		
		panelLines.setContent( col );

		componentsContainer.addComponent( panelLines );
		componentsContainer.addComponent( row4 );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewUser";
	}

	@Override
	protected void defaultFocus()
	{
		editUserName.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newUser.setId( null );

			IOCManager._UsersManager.createUser( getContext(), newUser );

			for ( CheckBox check : checksLines )
			{
				if ( check.getValue().booleanValue() )
				{
					Company company = (Company)check.getData();
					
					UserCompany userCompany = new UserCompany();
					userCompany.setRef_user( newUser.getId() );
					userCompany.setRef_company( company.getId() );
					
					IOCManager._UsersCompaniesManager.setRow( getContext(), null, userCompany );
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
	protected boolean onModify()
	{
		try
		{
			String lastPassword = ((User)orgDto).getPwd();
			
			if ( !newUser.getPwd().equals( lastPassword ) )
			{
				String pwd1 = newUser.getPwd();
				AppContext ctx = getContext();
				
	       		if ( !ChangePasswordDlg.assertNotLogin( ctx, pwd1 ) ||
	       				!ChangePasswordDlg.assertCurrentPassword( ctx, pwd1 ) || 
	       				!ChangePasswordDlg.assertLastPasswords( ctx, lastPassword1, lastPassword2, pwd1 ) || 
	       				!ChangePasswordDlg.assertMinSize( ctx, pwd1 ) ||
	       				!ChangePasswordDlg.assertUpperCase( ctx, pwd1 ) || 
	       				!ChangePasswordDlg.assertDigit( ctx, pwd1 ) || 
	       				!ChangePasswordDlg.assertSymbol( ctx, pwd1 ) )
	       			return false;
	       		
	    		String last1 = lastPassword1.getData_value().isEmpty() ? lastPassword : lastPassword1.getData_value(); 

	    		IOCManager._UserDefaultsManager.setUserDefault( ctx, lastPassword2, last1 ); 
	    		IOCManager._UserDefaultsManager.setUserDefault( ctx, lastPassword1, lastPassword ); 
			}

			IOCManager._UsersManager.updateUser( getContext(), (User) orgDto, newUser );
			
			for ( UserCompany userCompany : userCompanies )
				IOCManager._UsersCompaniesManager.delRow( getContext(), userCompany );
			
			for ( CheckBox check : checksLines )
			{
				if ( check.getValue().booleanValue() )
				{
					Company company = (Company)check.getData();
					
					UserCompany userCompany = new UserCompany();
					userCompany.setRef_user( newUser.getId() );
					userCompany.setRef_company( company.getId() );
					
					IOCManager._UsersCompaniesManager.setRow( getContext(), null, userCompany );
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
		try
		{
			IOCManager._UsersManager.delRow( getContext(), newUser );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	private void fillComboProfiles()
	{
		try
		{
			Profile query = new Profile();

			List<Profile> profiles = IOCManager._ProfilesManager.getRows( getContext(), query );

			for ( Profile profile : profiles )
			{
				boolean add = getContext().getUser().getRef_profile() <= profile.getId();
				
				if ( add )
				{
					comboProfile.addItem( profile.getId() );
					comboProfile.setItemCaption( profile.getId(), profile.getDescription() );
				}
			}
		}
		catch ( Throwable e )
		{
			if ( !( e instanceof BaseException ) )
				new BaseException( e, LOG, BaseException.UNKNOWN );
		}
	}

	private void fillComboStatus()
	{
		for ( int i = User.PASS_OK; i <= User.PASS_BLOCKED; i++ )
		{
			comboStatus.addItem( i );
			comboStatus.setItemCaption( i, getContext().getString( "modalNewUser.password." + i ) );
		}
	}

	@Override
	protected boolean hasDelete()
	{
		return getContext().hasRight( "configuration.users.delete" );
	}

	@Override
	public boolean checkAddRight()
	{
		return getContext().hasRight( "configuration.users.add" );
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.users.modify" );
	}
	

	private void loadCustomers()
	{
		try
		{
			CompanyQuery query = new CompanyQuery();
			query.setType_company( Company.TYPE_CUSTOMER );

			customers = IOCManager._CompaniesManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			customers = new ArrayList<Company>();
		}
	}

	private void loadUserCompanies()
	{
		try
		{
			UserCompany query = new UserCompany();
			query.setRef_user( newUser.getId() );

			userCompanies = IOCManager._UsersCompaniesManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			userCompanies = new ArrayList<UserCompany>();
		}
	}

	private boolean isCompanyAuthorized( Company company )
	{
		for ( UserCompany userCompany : userCompanies )
			if ( userCompany.getRef_company().equals( company.getId() ) )
				return true;
		
		return false;
	}

	@Override
	public OutputStream receiveUpload( String filename, String mimeType )
	{
		os = new ByteArrayOutputStream();
		fileName = filename;
		
		return os;
	}

	@Override
	public void uploadSucceeded( SucceededEvent event )
	{
		newUser.setSignature( os.toByteArray() );
		
		labelAttachment.setCaption( fileName + " " + getContext().getString( "words.success" ) );
	}
}
