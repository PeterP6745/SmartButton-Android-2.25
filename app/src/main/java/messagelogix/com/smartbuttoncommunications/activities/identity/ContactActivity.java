package messagelogix.com.smartbuttoncommunications.activities.identity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.model.Contact;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.DatabaseHandler;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import messagelogix.com.smartbuttoncommunications.utils.Strings;

/**
 * Created by Vahid
 * This is the activity that let us add our contacts to the custom emergency contacts
 */
public class ContactActivity extends AppCompatActivity {

    private static final String LOG_TAG = ContactActivity.class.getSimpleName();

    private static final int REQUEST_CODE_PICK_CONTACTS = 1;

    private Uri uriContact;

    private String contactID;

    private Context context = this;

    private EditText nameEntry, phoneEntry, emailEntry;

    private Button saveButton;

    private DatabaseHandler dbHandler;

    private List<Contact> Contacts = new ArrayList<>();

    private int contactId;

    /**
     * Performs validation on given email
     *
     * @return valid or not
     */
    public static boolean isValidEmail(CharSequence target) {

        return TextUtils.isEmpty(target) || android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * Performs validation on given phone
     *
     * @param target phone
     * @return valid or not
     */
    public static boolean isValidPhone(CharSequence target) {

        return target != null && !(target.length() < 10 || target.length() > 10) && android.util.Patterns.PHONE.matcher(target).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_me_contact);
        nameEntry = (EditText) findViewById(R.id.nameEditText);
        phoneEntry = (EditText) findViewById(R.id.phoneNumberEditText);
        emailEntry = (EditText) findViewById(R.id.emailAddressEditText);
        saveButton = (Button) findViewById(R.id.save_button);
        Preferences.init(context);
        if (!Preferences.getBoolean(Config.TEXT_IS_ENABLED)) {
            phoneEntry.setVisibility(View.GONE);
        } else {
            //Log.d(LOG_TAG, "text is disabled");
        }
        dbHandler = new DatabaseHandler(context);
        Contacts = dbHandler.getAllContacts();
        contactId = Integer.valueOf(getIntent().getStringExtra("contactId"));
        if (contactExists()) {
            saveButton.setEnabled(true);
            saveButton.setText(R.string.update_contact);
            Contact contact = dbHandler.getContact(contactId);
            //populate the view
            nameEntry.setText(contact.getName());
            phoneEntry.setText(contact.getPhoneNumber());
            emailEntry.setText(contact.getEmail());
        }
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String name = String.valueOf(nameEntry.getText());
                String phoneNumber = String.valueOf(phoneEntry.getText());
                String email = String.valueOf(emailEntry.getText());
                Contact contact = new Contact(contactId, name, phoneNumber.toString().trim().replaceAll("-|\\(|\\)", ""), email);
                if (validateData(contact)) {
                    if (contactIsNotDuplicate(contact)) {
                        if (!contactExists()) {
                            dbHandler.createContact(contact);
                            Contacts.add(contact);
                            Toast.makeText(getApplicationContext(), name + " has been added to your Contacts!", Toast.LENGTH_SHORT).show();
                        } else {
                            dbHandler.updateContact(contact);
                            Toast.makeText(getApplicationContext(), name + "'s information has been updated", Toast.LENGTH_SHORT).show();
                        }
                        goBack();
                    } else {
                        Toast.makeText(getApplicationContext(), "This contact already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        phoneEntry.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        emailEntry.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Boolean hasPhoneOrEmail = ((!phoneEntry.getText().toString().trim().isEmpty()) ||
                //        (!emailEntry.getText().toString().trim().isEmpty()));
                //saveButton.setEnabled(hasPhoneOrEmail);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setTouchListenerForKeyboardDismissal();
    }

    private void goBack() {

        super.onBackPressed();
    }

    private boolean contactIsNotDuplicate(Contact contact) {

        if (nameExists(contact.getName())) {
            Toast.makeText(getApplicationContext(), "The name you provided already exists in your emergency contacts, please enter a different one", Toast.LENGTH_LONG).show();
            return false;
        }
        if (phoneNumberExists(contact.getPhoneNumber())) {
            Toast.makeText(getApplicationContext(), "The cell phone number you provided already exists in your emergency contacts, please enter a different one", Toast.LENGTH_LONG).show();
            return false;
        }
        if (emailExists(contact.getEmail())) {
            Toast.makeText(getApplicationContext(), "The email address you provided already exists in your emergency contacts, please enter a different one", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean emailExists(String email) {

        if (Strings.isNullOrEmpty(email)) {
            return false;
        }
        for (Contact contact : Contacts) {
            if (contactId != contact.getId() && email.equals(contact.getEmail())) {
                return true;
            }
        }
        for (Contact contact : IdentityActivity.defaultContacts) {
            if (contactId != contact.getId() && email.equals(contact.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean phoneNumberExists(String phoneNumber) {

        if (Strings.isNullOrEmpty(phoneNumber)) {
            return false;
        }
        for (Contact contact : Contacts) {
            if (contactId != contact.getId() && phoneNumber.equals(contact.getPhoneNumber())) {
                return true;
            }
        }
        for (Contact contact : IdentityActivity.defaultContacts) {
            if (contactId != contact.getId() && phoneNumber.equals(contact.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean nameExists(String name) {

        if (Strings.isNullOrEmpty(name)) {
            return false;
        }
        for (Contact contact : Contacts) {
            if (contactId != contact.getId() && name.toLowerCase().equals(contact.getName().toLowerCase())) {
                return true;
            }
        }
        for (Contact contact : IdentityActivity.defaultContacts) {
            if (contactId != contact.getId() && name.toLowerCase().equals(contact.getName().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean validateData(Contact contact) {

        if (Strings.isNullOrEmpty(contact.getName())) {
            Toast.makeText(getApplicationContext(), "You must enter a name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (Strings.isNullOrEmpty(contact.getEmail()) && Strings.isNullOrEmpty(contact.getPhoneNumber())) {
            Toast.makeText(getApplicationContext(), "You must enter a cell number or an email", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!Strings.isNullOrEmpty(contact.getEmail()) && !isValidEmail(contact.getEmail())) {
            Toast.makeText(getApplicationContext(), "You have entered an invalid email", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!Strings.isNullOrEmpty(contact.getPhoneNumber()) && !isValidPhone(contact.getPhoneNumber())) {
            Toast.makeText(getApplicationContext(), "You have entered an invalid phone number", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * on Options Item Selected
     *
     * @param item is the menu item
     * @return true or false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_contact:
                checkContactPermission();
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_PICK_CONTACTS);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_locate_me_contact, menu);
        return true;
    }

    /**
     * If the SDK is bigger 23 Marshmallow, we should use this procedure for reading from storage
     */
    private void checkContactPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            3);
                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            //Log.d(LOG_TAG, "Response: " + data.toString());
            uriContact = data.getData();
            String contactName = retrieveContactName();
            if (contactName.length() != 0) {
                nameEntry.setText(contactName);
            }
            String contactNumber = retrieveContactNumber();
            if (contactNumber.length() != 0) {
                phoneEntry.setText(contactNumber.replaceAll("[^0-9]", ""));
            }
            String contactEmail = retrieveContactEmail();
            if (contactEmail.length() != 0) {
                emailEntry.setText(contactEmail);
            }
        }
    }

    /**
     * retrieves the contact email
     *
     * @return email
     */
    private String retrieveContactEmail() {

        Cursor cursor = null;
        String email = "";
        try {
            //Log.v(LOG_TAG, "Got a contact result: " + uriContact.toString());
            // get the contact id from the Uri
            String id = uriContact.getLastPathSegment();
            // query for everything email
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[]{id}, null);
            if (cursor != null) {
                int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                // let's just get the first email
                if (cursor.moveToFirst()) {
                    email = cursor.getString(emailIdx);
                    //Log.v(LOG_TAG, "Got email: " + email);
                } else {
                    //Log.w(LOG_TAG, "No results");
                }
            }
        } catch (Exception e) {
            //Log.e(LOG_TAG, "Failed to get email data", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (email.length() == 0) {
                Toast.makeText(this, "No Email for Selected Contact", Toast.LENGTH_LONG).show();
            }
        }
        return email;
    }

    /**
     * retrieves the contact number
     *
     * @return phone number
     */
    private String retrieveContactNumber() {

        String contactNumber = "";
        Cursor cursorPhone = null;
        try {
            // getting contacts ID
            Cursor cursorID = getContentResolver().query(uriContact,
                    new String[]{ContactsContract.Contacts._ID},
                    null, null, null);
            if (cursorID != null && cursorID.moveToFirst()) {
                contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            }
            if (cursorID != null) {
                cursorID.close();
            }
            //Log.d(LOG_TAG, "Contact ID: " + contactID);
            // Using the contact ID now we will get contact phone number
            cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                    new String[]{contactID},
                    null);
            if (cursorPhone != null && cursorPhone.moveToFirst()) {
                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            //Log.d(LOG_TAG, "Contact Phone Number: " + contactNumber);
        } catch (Exception e) {
            //Log.e(LOG_TAG, "Failed to get email data", e);
        } finally {
            if (cursorPhone != null) {
                cursorPhone.close();
            }
            if (contactNumber.length() == 0) {
                Toast.makeText(this, "No Phone number for Selected Contact", Toast.LENGTH_LONG).show();
            }
        }
        return contactNumber;
    }

    /**
     * retrieves the contact name
     *
     * @return the name
     */
    private String retrieveContactName() {

        String contactName = "";
        Cursor cursor = null;
        try {
            // querying contact data store
            cursor = getContentResolver().query(uriContact, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // DISPLAY_NAME = The display name for the contact.
                // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            }
            //Log.d(LOG_TAG, "Contact Name: " + contactName);
        } catch (Exception e) {
            //Log.e(LOG_TAG, "Failed to get email data", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (contactName.length() == 0) {
                Toast.makeText(this, "No Contact name for Selected Contact", Toast.LENGTH_LONG).show();
            }
        }
        return contactName;
    }

    /**
     * Checks if the contact exist
     *
     * @return true or false
     */
    private boolean contactExists() {

        return contactId != 0;
    }

    public void setTouchListenerForKeyboardDismissal() {

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.Step1Layout);
        assert layout != null;
        layout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motion) {

                hideKeyboard(view);
                return false;
            }
        });
    }

    protected void hideKeyboard(View view) {

        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
