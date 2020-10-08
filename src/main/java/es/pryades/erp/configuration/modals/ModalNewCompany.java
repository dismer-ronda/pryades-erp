package es.pryades.erp.configuration.modals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dashboard.Dashboard;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.CompanyContact;
import es.pryades.erp.dto.query.CompanyContactQuery;
import es.pryades.erp.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewCompany extends ModalWindowsCRUD implements Receiver, SucceededListener
{
	private static final long serialVersionUID = 7540891041170699438L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalNewCompany.class );

	protected Company newCompany;

	private ComboBox comboLanguage;
	private ComboBox comboTypes;
	private TextField editAlias;
	private TextField editName;
	private TextField editTax_id;
	private TextArea editAddress;
	private TextField editEmail;
	private TextField editPhone;
	private CheckBox checkTaxable;
	private CheckBox checkSignature;

	private Label labelAttachment;
	private String fileName;
	private ByteArrayOutputStream os;
	
	private List<CompanyContact> contacts;
	private Panel panelContacts;
	private List<HorizontalLayout> contacts_rows;

	/**
	 * 
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewCompany( AppContext context, OperationCRUD modalOperation, Company orgParameter, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgParameter );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		setWidth( "1024px" );

		try
		{
			newCompany = (Company) Utils.clone( (Company) orgDto );
		}
		catch ( Throwable e1 )
		{
			newCompany = new Company();
			newCompany.setType_company( Company.TYPE_PROVIDER );
			newCompany.setTaxable( Boolean.FALSE );
			newCompany.setSignature( Boolean.FALSE );
		}

		bi = new BeanItem<BaseDto>( newCompany );

		comboLanguage = new ComboBox(getContext().getString( "modalNewCompany.comboLanguage" ));
		comboLanguage.setWidth( "100%" );
		comboLanguage.setNullSelectionAllowed( false );
		comboLanguage.setTextInputAllowed( false );
		comboLanguage.setImmediate( true );
		comboLanguage.setRequired( true );
		comboLanguage.setRequiredError( getContext().getString( "words.required" ) );
		fillComboLanguage();
		comboLanguage.setPropertyDataSource( bi.getItemProperty( "language" ) );
		
		if ( newCompany.getType_company() > Company.TYPE_OWNER )
		{
			comboTypes = new ComboBox(getContext().getString( "modalNewCompany.comboTypes" ));
			comboTypes.setWidth( "100%" );
			comboTypes.setNullSelectionAllowed( false );
			comboTypes.setTextInputAllowed( false );
			comboTypes.setImmediate( true );
			comboTypes.setRequired( true );
			comboTypes.setRequiredError( getContext().getString( "words.required" ) );
			fillComboTypes();
			comboTypes.setPropertyDataSource( bi.getItemProperty( "type_company" ) );
		}

		editAlias = new TextField( getContext().getString( "modalNewCompany.editAlias" ), bi.getItemProperty( "alias" ) );
		editAlias.setWidth( "100%" );
		editAlias.setNullRepresentation( "" );
		editAlias.setRequired( true );
		editAlias.setRequiredError( getContext().getString( "words.required" ) );
		editAlias.setInvalidCommitted( true );
		
		editName = new TextField( getContext().getString( "modalNewCompany.editName" ), bi.getItemProperty( "name" ) );
		editName.setWidth( "100%" );
		editName.setNullRepresentation( "" );
		editName.setRequired( true );
		editName.setRequiredError( getContext().getString( "words.required" ) );
		editName.setInvalidCommitted( true );
		
		editTax_id = new TextField( getContext().getString( "modalNewCompany.editTax_id" ), bi.getItemProperty( "tax_id" ) );
		editTax_id.setWidth( "100%" );
		editTax_id.setNullRepresentation( "" );
		
		editAddress = new TextArea( getContext().getString( "modalNewCompany.editAddress" ), bi.getItemProperty( "address" ) );
		editAddress.setWidth( "100%" );
		editAddress.setNullRepresentation( "" );

		editEmail = new TextField( getContext().getString( "modalNewCompany.editEmail" ), bi.getItemProperty( "email" ) );
		editEmail.setWidth( "100%" );
		editEmail.setNullRepresentation( "" );
		
		editPhone = new TextField( getContext().getString( "modalNewCompany.editPhone" ), bi.getItemProperty( "phone" ) );
		editPhone.setWidth( "100%" );
		editPhone.setNullRepresentation( "" );

		checkTaxable = new CheckBox( getContext().getString( "modalNewCompany.checkTaxable" ), bi.getItemProperty( "taxable" ) );
		checkTaxable.setWidth( "100%" );
		
		checkSignature = new CheckBox( getContext().getString( "modalNewCompany.checkSignature" ), bi.getItemProperty( "signature" ) );
		checkSignature.setWidth( "100%" );
		
		labelAttachment = new Label();

		Upload upload = new Upload( null, this );
		upload.setImmediate( true );
		upload.setButtonCaption( getContext().getString( "modalNewCompany.captionUploadButton" ) );
		upload.addSucceededListener( this );

		getCustomOperationsRow().addComponent( upload );

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( comboLanguage );
		if ( newCompany.getType_company() > Company.TYPE_OWNER )
			row1.addComponent( comboTypes );
		row1.addComponent( editTax_id );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( editAlias );
		row2.addComponent( editName );

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( editEmail );
		row3.addComponent( editPhone );

		HorizontalLayout row4 = new HorizontalLayout();
		row4.setWidth( "100%" );
		row4.setSpacing( true );
		row4.addComponent( checkTaxable );
		row4.setComponentAlignment( checkTaxable, Alignment.MIDDLE_LEFT );
		row4.addComponent( checkSignature );
		row4.setComponentAlignment( checkSignature, Alignment.MIDDLE_LEFT );
		
		HorizontalLayout row5 = new HorizontalLayout();
		row5.setWidth( "100%" );
		row5.setSpacing( true );
		row5.addComponent( editAddress );
		
		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		componentsContainer.addComponent( row3 );
		componentsContainer.addComponent( row4 );
		componentsContainer.addComponent( row5 );
		
		if ( !getOperation().equals( OperationCRUD.OP_ADD ) )
		{
			panelContacts = new Panel( getContext().getString( "modalNewCompany.contacts" ) );
			panelContacts.setStyleName( "borderless light" );
			panelContacts.setHeight( "240px" );
			
			contacts_rows = new ArrayList<HorizontalLayout>();
			
			VerticalLayout col = new VerticalLayout();
			col.setWidth( "100%" );
			col.setMargin( true );
			panelContacts.setContent( col );
			
			showContacts();
			
			componentsContainer.addComponent( panelContacts );
		}

		HorizontalLayout row6 = new HorizontalLayout();
		row6.setWidth( "100%" );
		row6.setSpacing( true );
		row6.addComponent( labelAttachment );
		row6.setComponentAlignment( labelAttachment, Alignment.MIDDLE_CENTER );

		componentsContainer.addComponent( row6 );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewCompany";
	}

	@Override
	protected void defaultFocus()
	{
		editAlias.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newCompany.setId( null );

			IOCManager._CompaniesManager.setRow( getContext(), null, newCompany );

			if ( newCompany.getType_company().equals( Company.TYPE_CUSTOMER ) )
			{
				Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
				dashboard.refreshCustomers();
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
			IOCManager._CompaniesManager.setRow( getContext(), (Company) orgDto, newCompany );

			if ( newCompany.getType_company().equals( Company.TYPE_CUSTOMER ) )
			{
				Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
				dashboard.refreshCustomers();
			}

			saveContacts();
			
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
		if ( newCompany.getType_company().equals( Company.TYPE_OWNER ) )
		{
			Utils.showNotification( getContext(), getContext().getString( "modalNewCompany.delete.owner" ), Notification.Type.WARNING_MESSAGE );
			
			return false;
		}
		
		try
		{
			IOCManager._CompaniesManager.delRow( getContext(), newCompany );

			if ( newCompany.getType_company().equals( Company.TYPE_CUSTOMER ) )
			{
				Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
				dashboard.refreshCustomers();
			}
			
			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	private void fillComboLanguage()
	{
		comboLanguage.addItem( "es" );
		comboLanguage.setItemCaption( "es", getContext().getString( "language.es" ) );
		comboLanguage.addItem( "en" );
		comboLanguage.setItemCaption( "en", getContext().getString( "language.en" ) );
		
		comboLanguage.select( newCompany.getLanguage() );
	}

	private void fillComboTypes()
	{
		for ( int i = Company.TYPE_PROVIDER; i <= Company.TYPE_BANK; i++ )
		{
			comboTypes.addItem( i );
			comboTypes.setItemCaption( i, getContext().getString( "company.type." + i ) );
		}	
	}
	
	@Override
	protected boolean hasDelete()
	{
		return getContext().hasRight( "configuration.companies.delete" );
	}

	@Override
	public boolean checkAddRight()
	{
		return getContext().hasRight( "configuration.companies.add" );
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.companies.modify" );
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
		newCompany.setLogo( os.toByteArray() );
		
		labelAttachment.setCaption( fileName + " " + getContext().getString( "words.success" ) );
	}
	
	@SuppressWarnings("unchecked")
	private void loadContacts()
	{
		try
		{
			CompanyContactQuery query = new CompanyContactQuery();
			query.setRef_company( newCompany.getId() );
			
			contacts = IOCManager._CompaniesContactsManager.getRows( getContext(), query );
			contacts_rows.clear();
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			contacts = new ArrayList<CompanyContact>();
		}
	}

	private void showContacts()
	{
		((VerticalLayout)panelContacts.getContent()).removeAllComponents();
		
		loadContacts();
		
		for ( CompanyContact contact : contacts )
		{
			HorizontalLayout row = new HorizontalLayout();
			row.setWidth( "100%" );
			row.setSpacing( true );

			HorizontalLayout rowEdits = new HorizontalLayout();
			rowEdits.setWidth( "100%" );
			rowEdits.setSpacing( true );

			TextField editName = new TextField( getContext().getString( "modalNewCompany.editName" ) );
			editName.setWidth( "100%" );
			editName.setNullRepresentation( "" );
			editName.setRequired( true );
			editName.setRequiredError( getContext().getString( "words.required" ) );
			editName.setValue( contact.getName() );
			
			TextField editEmail = new TextField( getContext().getString( "modalNewCompany.editEmail" ) );
			editEmail.setWidth( "100%" );
			editEmail.setNullRepresentation( "" );
			editEmail.setValue( contact.getEmail() );
			
			TextField editPhone = new TextField( getContext().getString( "modalNewCompany.editPhone" ) );
			editPhone.setWidth( "100%" );
			editPhone.setNullRepresentation( "" );
			editPhone.setValue( contact.getPhone() );

			Button btnDel = new Button("-");
			btnDel.setWidth( "48px" );
			btnDel.setData( contact );
			btnDel.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 6139166285625663025L;

				public void buttonClick( ClickEvent event )
				{
					onDeleteContact( (CompanyContact)event.getButton().getData() );
				}
			} );

			rowEdits.addComponent( editName );
			rowEdits.addComponent( editEmail );
			rowEdits.addComponent( editPhone );
			
			row.addComponent( rowEdits );
			row.addComponent( btnDel );
			row.setComponentAlignment( btnDel, Alignment.BOTTOM_LEFT );
			row.setExpandRatio( rowEdits, 1.0f );
			
			((VerticalLayout)panelContacts.getContent()).addComponent( row );
			
			rowEdits.setData( contact );
			contacts_rows.add( rowEdits );
		}
		
		HorizontalLayout row = new HorizontalLayout();
		row.setWidth( "100%" );
		row.setSpacing( true );

		HorizontalLayout rowEdits = new HorizontalLayout();
		rowEdits.setWidth( "100%" );
		rowEdits.setSpacing( true );
		
		TextField editName = new TextField( getContext().getString( "modalNewCompany.editName" ) );
		editName.setWidth( "100%" );
		editName.setNullRepresentation( "" );
		editName.setRequired( true );
		editName.setRequiredError( getContext().getString( "words.required" ) );
		
		TextField editEmail = new TextField( getContext().getString( "modalNewCompany.editEmail" ) );
		editEmail.setWidth( "100%" );
		editEmail.setNullRepresentation( "" );
		
		TextField editPhone = new TextField( getContext().getString( "modalNewCompany.editPhone" ) );
		editPhone.setWidth( "100%" );
		editPhone.setNullRepresentation( "" );

		Button btnAdd = new Button("+");
		btnAdd.setWidth( "48px" );
		btnAdd.setData( rowEdits );
		btnAdd.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 4835729502890544577L;

			public void buttonClick( ClickEvent event )
			{
				HorizontalLayout row = (HorizontalLayout)event.getButton().getData();
				
				String name = ((TextField)row.getComponent( 0 )).getValue();
				String email = ((TextField)row.getComponent( 1 )).getValue();
				String phone = ((TextField)row.getComponent( 2 )).getValue();
				
				onAddContact( name, email, phone );
			}
		} );

		rowEdits.addComponent( editName );
		rowEdits.addComponent( editEmail );
		rowEdits.addComponent( editPhone );
		
		row.addComponent( rowEdits );
		row.addComponent( btnAdd );
		row.setComponentAlignment( btnAdd, Alignment.BOTTOM_LEFT );
		row.setExpandRatio( rowEdits, 1.0f );
		
		((VerticalLayout)panelContacts.getContent()).addComponent( row );
	}
	
	private void onDeleteContact( CompanyContact contact )
	{
		try
		{
			IOCManager._CompaniesContactsManager.delRow( getContext(), contact );
			
			showContacts();
		}
		catch ( Throwable e  )
		{
			showErrorMessage( e );
		}
	}

	private boolean onAddContact( String name, String email, String phone )
	{
		try
		{
			if ( !name.isEmpty() && !email.isEmpty() )
			{
				CompanyContact contact = new CompanyContact();
				contact.setRef_company( newCompany.getId() );
				contact.setName( name );
				contact.setEmail( email );
				contact.setPhone( phone );
				
				IOCManager._CompaniesContactsManager.setRow( getContext(), null, contact );
				
				showContacts();
				
				return true;
			}
		}
		catch ( Throwable e  )
		{
			showErrorMessage( e );
		}
		
		return false;
	}
	
	private void saveContacts()
	{
		for ( HorizontalLayout row : contacts_rows )
		{
			CompanyContact contact = (CompanyContact)row.getData();
			
			String name = ((TextField)row.getComponent( 0 )).getValue();
			String email = ((TextField)row.getComponent( 1 )).getValue();
			String phone = ((TextField)row.getComponent( 2 )).getValue();

			try
			{
				CompanyContact newContact = (CompanyContact)Utils.clone( contact );

				newContact.setName( name );
				newContact.setEmail( email );
				newContact.setPhone( phone );
				
				IOCManager._CompaniesContactsManager.setRow( getContext(), contact, newContact );
			}
			catch ( Throwable e  )
			{
				showErrorMessage( e );
			}
		}
	}
}
