package messagelogix.com.smartbuttoncommunications.activities.identity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SharedElementCallback;
import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.core.TabBarActivity;
import messagelogix.com.smartbuttoncommunications.model.Contact;
import messagelogix.com.smartbuttoncommunications.model.ContactDefault;
import messagelogix.com.smartbuttoncommunications.model.Response;
import messagelogix.com.smartbuttoncommunications.model.SpinnerItem;
import messagelogix.com.smartbuttoncommunications.utils.ApiHelper;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.DatabaseHandler;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.LanguageManager;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.MarshMallowPermission;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import messagelogix.com.smartbuttoncommunications.utils.RetryCounter;
import messagelogix.com.smartbuttoncommunications.utils.Strings;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.shape.RectangleShape;
import uk.co.deanwild.materialshowcaseview.shape.Shape;

/**
 * Created by Vahid
 * This is the activity for showing the my identity page in our app
 */
public class IdentityActivity extends AppCompatActivity implements MaterialShowcaseSequence.OnSequenceItemDismissedListener, MaterialShowcaseSequence.OnSequenceItemShownListener {

    private static final int SELECT_IMAGE = 1;

    private static final int CAPTURE_IMAGE = 2;

    private static final String SHOWCASE_ID = "3";

    private static final String LOG_TAG = IdentityActivity.class.getSimpleName();

    public static List<Contact> defaultContacts = new ArrayList<>();

    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);

    private Context context = this;

    private List<Contact> Contacts = new ArrayList<>();

    private DatabaseHandler dbHandler;

    private ArrayAdapter<Contact> adapter;

    private int demoCounter = 0;

    String newPhotoFileName;
    Uri newPhotoUri;
    private ImageView viewImage;

    private TextView defaultContactsTitle;

    private ListView defaultContactListView;

    private ListView contactListView;

    private String timeStamp;

    private EditText userEmail;

    private EditText userCellphone;

    private EditText userNameEditText;

    private EditText userRoomEditText;

    private Button chooseImageButton;

    private Button addContactImageButton;

    private Button addRoomInfoButton;

    private Spinner buildingSpinner;

    private String selectedBuildingId, selectedBuildingName = "";

    private Boolean bIdWasUpdated = false;

    private Boolean sbListAlreadyLoaded = false;
    private Boolean titleListAlreadyLoaded = false;

    private String tempTitleId = null, tempTitleName = null;

    private static boolean isValidEmail(CharSequence target) {
        return TextUtils.isEmpty(target) || android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private static boolean isValidPhone(CharSequence target) {
        //strip parentheses characters
        String targetString = target.toString();
        targetString = targetString.replace("(","");
        targetString = targetString.replace(")","");
        targetString = targetString.replace(" ","");
        targetString = targetString.replace("-","");
        target = targetString;
        return target != null && !(target.length() < 10 || target.length() > 10) && android.util.Patterns.PHONE.matcher(target).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration config = getBaseContext().getResources().getConfiguration();
        Preferences.init(this);
        LanguageManager.setLocale(this, Preferences.getString(Config.LANGUAGE));

        setTitle(R.string.identity);
        setContentView(R.layout.activity_identity);
        setupFullNameEditText();
        setupEmailEditText();
        setupCellphoneEditText();
        setupImageView();
        setupImageButton();
        setupSaveButton();
        setupAddRoomInfoButton();
        setupAddContactButton();
        setupDefaultContactListView();
        setupContactListView();
        setupRoomField();                               //*************
        populateContactListView();
        //Asynchronous tasks

        //manageProfileLists();
        //new GetDefaultContacts().execute();
        //new GetSchoolBuildingsTask().execute();
        //new GetTitles().execute();
        checkPermissions();
        addBackButton();
        //setImageTimeStamp();
        setTouchListenerForKeyboardDismissal();
        //showCase();


    }
//    private void setAppLocale(String localeCode){
//        Resources resources = getResources();
//        DisplayMetrics dm = resources.getDisplayMetrics();
//        Configuration config = resources.getConfiguration();
//        Locale locale = new Locale(localeCode);
//        Locale.setDefault(locale);
//        config.locale = locale;
//        resources.updateConfiguration(config, dm);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        populateContactListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("respon","Here");
        manageProfileLists();
        populateContactListView();
        preSelectSchoolSpinner();
    }

    private void manageProfileLists() {
        LogUtils.debug("covidonlyaccount","is this account a covidonly account? "+Preferences.getBoolean(Config.COVIDSCREENING_ACCOUNT));
        if(Preferences.isAvailable(Config.COVIDSCREENING_ACCOUNT) && !Preferences.getBoolean(Config.COVIDSCREENING_ACCOUNT))
            getDefaultContactsList();

        getSchoolBuildingList();
        getTitleList();
    }

    private void setupDefaultContactListView() {

       // defaultContactListView = (ListView) findViewById(R.id.default_contact_listView);
//        defaultContactListView = (ListView) findViewById(R.id.new_default_contacts_listView);
        defaultContactsTitle = (TextView) findViewById(R.id.dec_title);
        defaultContactListView = (ListView) findViewById(R.id.listview_identity_default_contacts);
        assert defaultContactListView != null;
        ViewGroup.LayoutParams params = defaultContactListView.getLayoutParams();
        params.height = 400 * 2;
        defaultContactListView.setLayoutParams(params);
        defaultContactListView.requestLayout();
    }

    private void setupCellphoneEditText() {

       // userCellphone = (EditText) findViewById(R.id.user_cellphone);
//        userCellphone = (EditText) findViewById(R.id.new_phone);
        userCellphone = (EditText) findViewById(R.id.et_identity_phone);
        userCellphone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        if (Preferences.isAvailable(Config.CONTACT_CELLPHONE)) {
            if (!Preferences.getString(Config.CONTACT_CELLPHONE).trim().equals(""))
                userCellphone.setText(Preferences.getString(Config.CONTACT_CELLPHONE));
        }
    }

    private void setupSaveButton() {

     //   Button saveButton = (Button) findViewById(R.id.save_user_info_button);
//        Button saveButton = (Button) findViewById(R.id.new_save_user_info_button);
        Button saveButton = (Button) findViewById(R.id.button_identity_save_info);
        assert saveButton != null;
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Contact contact = new Contact(userCellphone.getText().toString().trim().replaceAll("-|\\(|\\)", ""), userEmail.getText().toString().trim());
                if (validateData(contact)) {
                    Preferences.putString(Config.CONTACT_EMAIL, userEmail.getText().toString().trim());
                    Preferences.putString(Config.CONTACT_CELLPHONE, userCellphone.getText().toString().trim().replaceAll("-|\\(|\\)", ""));
                    Preferences.putString(Config.USER_TITLE_ID, tempTitleId);
                    Preferences.putString(Config.USER_TITLE_NAME, tempTitleName);
                    LogUtils.debug("loadstoredtitlelistduringsave","current title id --> "+Preferences.getString(Config.USER_TITLE_ID)+" --> current title name is --> " + Preferences.getString(Config.USER_TITLE_NAME));
                    saveUserInfo();
                }
            }
        });
    }

    private void showCase() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        config.setShapePadding((int) getResources().getDimension(R.dimen.demo_shape_padding));
        config.setRenderOverNavigationBar(true);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
//        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setOnItemDismissedListener(this);
        sequence.setOnItemShownListener(this);
        config.setDismissTextColor(getResources().getColor(R.color.red));
        Shape shape = new RectangleShape((int) getResources().getDimension(R.dimen.demo_rectangle_width), (int) getResources().getDimension(R.dimen.demo_rectangle_height));
        config.setShape(shape);
        sequence.setConfig(config);
        //Demo name
        sequence.addSequenceItem(userNameEditText,
                getResources().getString(R.string.show_enter_name), getResources().getString(R.string.got_it));
        demoCounter++;
//        //Demo Add Image
//        sequence.addSequenceItem(chooseImageButton,
//                getResources().getString(R.string.add_your_picture), getResources().getString(R.string.got_it));
//        demoCounter++;
        //Demo choose school
//        Spinner buildingSpinner = (Spinner) findViewById(R.id.new_building_spinner);
        Spinner buildingSpinner = (Spinner) findViewById(R.id.spinner_identity_building);
        sequence.addSequenceItem(buildingSpinner,
                getResources().getString(R.string.show_choose_school), getResources().getString(R.string.got_it));
        demoCounter++;
        //Demo choose title
//        Spinner titleSpinner = (Spinner) findViewById(R.id.new_title_spinner);
        Spinner titleSpinner = (Spinner) findViewById(R.id.spinner_identity_title);
        sequence.addSequenceItem(titleSpinner,
                getResources().getString(R.string.show_choose_title), getResources().getString(R.string.got_it));
        demoCounter++;
        //Demo add your picture
        sequence.addSequenceItem(chooseImageButton,
                getResources().getString(R.string.show_add_your_picture), getResources().getString(R.string.got_it));
        demoCounter++;
        sequence.addSequenceItem(defaultContactListView,
                getResources().getString(R.string.show_default_contact), getResources().getString(R.string.got_it));
        demoCounter++;
        sequence.start();
    }

    @Override
    public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {

        if (demoCounter == 1) {
            final TabBarActivity activity = (TabBarActivity) this.getParent();
            TabHost host = activity.getTabHost();
            host.getTabWidget().setVisibility(View.VISIBLE);
            host.setCurrentTab(0);
        } else {
            demoCounter--;
        }
    }

    @Override
    public void onShow(MaterialShowcaseView materialShowcaseView, int i) {

    }

    private void setupImageButton() {

      //  chooseImageButton = (Button) findViewById(R.id.buttonAddPicture);
//        chooseImageButton = (Button) findViewById(R.id.new_button_image);
        chooseImageButton = (Button) findViewById(R.id.button_identity_changephoto);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void setupContactListView() {

        adapter = new ContactListAdapter();
        dbHandler = new DatabaseHandler(context);
        Contacts.clear();
       // contactListView = (ListView) findViewById(R.id.contact_listView);
//        contactListView = (ListView) findViewById(R.id.new_contact_listView);
        contactListView = (ListView) findViewById(R.id.listview_identity_other_contacts);
        assert contactListView != null;
        contactListView.setAdapter(adapter);
        contactListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a,
                                    View v, int position, long id) {

                int contactId = Contacts.get(position).getId();
                Intent intent = new Intent(v.getContext(), ContactActivity.class);
                intent.putExtra("contactId", String.valueOf(contactId));
                startActivity(intent);
            }
        });
    }

    private void populateContactListView() {

        Contacts.clear();
        Contacts.addAll(dbHandler.getAllContacts());
        adapter.notifyDataSetChanged();
        //TextView contactTextView = (TextView) findViewById(R.id.contact_textView);
        //TextView contactTextView = (TextView) findViewById(R.id.new_contact_textView);
        TextView contactTextView = (TextView) findViewById(R.id.textview_identity_other_contacts);
        if (Preferences.getBoolean(Config.ADD_DEFAULT_CONTACTS)) {
            if (dbHandler.getContactsCount() > 0) {
                contactListView.setVisibility(View.VISIBLE);
                //Set contacts list size, size of one is 70 dp
                ViewGroup.LayoutParams params = contactListView.getLayoutParams();
                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70 * dbHandler.getContactsCount(), getResources().getDisplayMetrics());
                contactListView.setLayoutParams(params);
                contactListView.requestLayout();
                assert contactTextView != null;
                contactTextView.setText(R.string.emergency_contact_string);
            } else {
                contactListView.setVisibility(View.INVISIBLE);
                assert contactTextView != null;
                contactTextView.setText(R.string.no_emergency_contacts);
            }
        } else {
            contactListView.setVisibility(View.GONE);
            addContactImageButton.setVisibility(View.GONE);
            assert contactTextView != null;
            contactTextView.setVisibility(View.GONE);
        }
    }

    private void setupImageView() {
        viewImage = findViewById(R.id.imageview_identity);

        Uri imageUri = Uri.parse(Preferences.getString(Config.USER_PROFILE_PICTURE));
        LogUtils.debug("ImageFileProcess","setupImageView() - imageUri is: "+imageUri);
//        try {
            if (imageUri != null && !imageUri.toString().isEmpty()) {
//            this.grantUriPermission(this.toString(),imageUri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
                LogUtils.debug("ImageFileProcess","setupImageView() - profilePicPath is not empty, so calling setPic()");
                viewImage.setImageURI(imageUri);
            } else {
                LogUtils.debug("ImageFileProcess","setupImageView() - profilePicPath is EMPTY, so setting image to default icon");
                viewImage.setImageResource(R.drawable.picture_icon);
            }
//        } catch(Exception ex) {
//            LogUtils.debug("ImageFileProcess","setupImageView() - encountered exception trying to access file referenced by uri");
//            viewImage.setImageResource(R.drawable.picture_icon);
//        }
    }

    private void setupEmailEditText() {

      //  userEmail = (EditText) findViewById(R.id.user_email);
//        userEmail = (EditText) findViewById(R.id.new_email);
        userEmail = (EditText) findViewById(R.id.et_identity_email);
        userEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Preferences.putString(Config.CONTACT_EMAIL, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (!Strings.isNullOrEmpty(Preferences.getString(Config.CONTACT_EMAIL)))
            userEmail.setText(Preferences.getString(Config.CONTACT_EMAIL));
    }

    private void setupAddContactButton() {

       // addContactImageButton = (Button) findViewById(R.id.contact_imageButton);
//        addContactImageButton = (Button) findViewById(R.id.new_contact_imageButton);
        addContactImageButton = (Button) findViewById(R.id.button_identity_add_contact);
        assert addContactImageButton != null;
        addContactImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int id = dbHandler.getContactsCount();
                if (id >= 5) {
                    Toast.makeText(getApplicationContext(), getString(R.string.Exceeded_Limit), Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(context, ContactActivity.class);
                    i.putExtra("contactId", "0");
                    startActivity(i);
                }
            }
        });
    }

    private void setupFullNameEditText() {

  //      userNameEditText = (EditText) findViewById(R.id.user_full_name);
//        userNameEditText = (EditText) findViewById(R.id.new_name);
        userNameEditText = (EditText) findViewById(R.id.et_identity_name);
        userNameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d(LOG_TAG, "CharSequence -> '" + s + "'");
                Preferences.putString(Config.USER_FULL_NAME, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (!Strings.isNullOrEmpty(Preferences.getString(Config.USER_FULL_NAME)))
            userNameEditText.setText(Preferences.getString(Config.USER_FULL_NAME));
    }

    private void setupAddRoomInfoButton(){
        //initiate the button
        addRoomInfoButton = (Button)findViewById(R.id.btn_add_room_information);

        //check toggle
        boolean hasRoom = Preferences.getBoolean(Config.ROOM_TOGGLE);

        if(hasRoom){
            addRoomInfoButton.setVisibility(View.VISIBLE);
            //add On Click Listener to Button
            addRoomInfoButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //Start the AddRoomInfoActivity
                    Intent intent = new Intent(context, AddRoomInfoActivity.class);
                    startActivity(intent);
                }
            });
        }
        else{
            addRoomInfoButton.setVisibility(View.GONE);
        }
    }

    private void setImageTimeStamp() {

        timeStamp = getTimeStamp();
    }

    private void addBackButton() {

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void checkPermissions() {

        if (!marshMallowPermission.checkPermissionForCamera()) {
            marshMallowPermission.requestPermissionForCamera();
        }
        if (!marshMallowPermission.checkPermissionForExternalStorage()) {
            marshMallowPermission.requestPermissionForExternalStorage();
        }
    }

    private boolean validateData(Contact contact) {

        if (Strings.isNullOrEmpty(contact.getEmail()) && Strings.isNullOrEmpty(contact.getPhoneNumber())) {
            Toast.makeText(getApplicationContext(), getString(R.string.Enter_num_or_email), Toast.LENGTH_LONG).show();
            return false;
        }
        if (!isValidEmail(contact.getEmail())) {
            Toast.makeText(getApplicationContext(), getString(R.string.Invalid_num_or_email), Toast.LENGTH_LONG).show();
            return false;
        }
        if (!isValidPhone(contact.getPhoneNumber())) {
            Toast.makeText(getApplicationContext(), getString(R.string.Invalid_num_or_email), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private String getTimeStamp() {

        Long tsLong = (System.currentTimeMillis() / 1000);
        return tsLong.toString();
    }

    private void saveUserInfo() {
        //new SetUserInfo(false).execute();
        setUserInfo(false);
    }

    private void setTouchListenerForKeyboardDismissal() {

       // LinearLayout layout = (LinearLayout) findViewById(R.id.Step1Layout);
//        RelativeLayout layout = (RelativeLayout) findViewById(R.id.new_identity_layout_container);
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_identity_container);
        layout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motion) {
                hideKeyboard();
                return false;
            }
        });
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private Uri createImageUri() throws Exception {
        // Create an image file name
        final ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, newPhotoFileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        final ContentResolver resolver = context.getContentResolver();
        Uri uri = null;

        final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        uri = resolver.insert(contentUri, values);

        if (uri == null)
            throw new Exception("Failed to create new MediaStore record.");

//                try (final OutputStream stream = resolver.openOutputStream(uri)) {
//                    if (stream == null)
//                        throw new IOException("Failed to open output stream.");
//
//                    if (!bitmap.compress(format, 95, stream))
//                        throw new IOException("Failed to save bitmap.");
//                }

        return uri;
    }

    private File createImageFile() throws Exception {
        // Create an image file name
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
                newPhotoFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private void dispatchTakePictureIntent() {
        timeStamp = getTimeStamp();
        newPhotoFileName = "EN_" + Preferences.getString(Config.UNIQUE_ID) + "_" + timeStamp;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    newPhotoUri = createImageUri();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, newPhotoUri);
//                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
                } else {
                    // Create the File where the photo should go
                    File photoFile = createImageFile();

                    // Continue only if the File was successfully created
                    newPhotoUri = FileProvider.getUriForFile(this, "messagelogix.com.smartbuttoncommunications.provider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, newPhotoUri);
//                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
                }
            } catch(IOException ex) {
                LogUtils.debug("ImageFileProcess","dispatchTakePictureIntent() - IOException encountered after call to createImageFile(): "+ex);
                Toast.makeText(this, "Failed to open Camera because Camera and Storage permissions are currently disabled. Please enable these permissions before trying again.", Toast.LENGTH_LONG).show();
            } catch(SecurityException ex) {
                LogUtils.debug("ImageFileProcess","dispatchTakePictureIntent() - security exception encountered is: "+ex);
                // Error occurred while creating the uri
                Toast.makeText(this, "Failed to open Camera because Camera and Storage permissions are currently disabled. Please enable these permissions before trying again.", Toast.LENGTH_LONG).show();
            } catch(Exception ex) {
                LogUtils.debug("ImageFileProcess","dispatchTakePictureIntent() - exception encountered is: "+ex);
                // Error occurred while creating the uri
                Toast.makeText(this, "Failed to generate image, please try again.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Failed to open Camera because the current device does not have access to a Camera.", Toast.LENGTH_LONG).show();
        }
    }

    private void selectImage() {
        final CharSequence[] options;
        if ("es".equals(Preferences.getString(Config.LANGUAGE)))
            options = new CharSequence[]{"Tomar foto", "Elige de mi galería", "Cancelar"};
        else
            options = new CharSequence[]{"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(IdentityActivity.this);
        builder.setTitle(R.string.select_image_title);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                Log.d("photoTest", item + "");
                switch (item) {
                    case 0: {
                        dispatchTakePictureIntent();
                        break;
                    }
                    case 1: {
                        Intent selectPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        selectPhoto.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
                        selectPhoto.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                        startActivityForResult(selectPhoto, SELECT_IMAGE);
                        break;
                    }
                    case 2: {
                        dialog.dismiss();
                        break;
                    }
                }
            }
        });
        builder.show();
    }

    private String getFileName(){
        Cursor returnCursor = this.getContentResolver().query(newPhotoUri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String fileName = returnCursor.getString(nameIndex);
        returnCursor.close();
        return fileName;
    }

    private void createBitmapAndUpload() {
        try {
            Bitmap imageBitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ImageDecoder.Source sourceObj = ImageDecoder.createSource(this.getContentResolver(),newPhotoUri);
                imageBitmap = ImageDecoder.decodeBitmap(sourceObj);
            } else {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), newPhotoUri);
            }

            String fileName = getFileName();

            if(!fileName.isEmpty() && imageBitmap != null) {
                LogUtils.debug("ImageFileProcess", "onActivityResult() - CaptureImage - getFileName() is: " + fileName);
                LogUtils.debug("ImageFileProcess","onActivityResult() - calling UploadPhotoFileTask()");
                new UploadPhotoFileTask(newPhotoUri,fileName,imageBitmap).execute(Config.API_URL);
                //Preferences.putString(Config.USER_PROFILE_PICTURE,newPhotoUri.toString());
            } else {
                Toast.makeText(this, "Failed to generate image, please try again.", Toast.LENGTH_LONG).show();
            }
        } catch(Exception ex) {
            LogUtils.debug("ImageFileProcess","inside catch block of createBitmapAndUpload()");
            Toast.makeText(this, "Failed to generate image, please try again.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
            createBitmapAndUpload();
        } else if(requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            if(data != null) {
//                LogUtils.debug("ImageFileProcess", "onActivityResult() - SELECT_IMAGE - data.getData() is: " + data.getData());
                newPhotoUri = data.getData();
                getContentResolver().takePersistableUriPermission(newPhotoUri,Intent.FLAG_GRANT_READ_URI_PERMISSION);

                createBitmapAndUpload();
            } else {
                Toast.makeText(this, "Failed to generate image, please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        //Log.e(LOG_TAG, "onRequestPermissionsResult = " + permsRequestCode);
        for (String s : permissions)
            //Log.e(LOG_TAG, "permission = " + s);
            switch (permsRequestCode) {
                case MarshMallowPermission.CAMERA_PERMISSION_REQUEST_CODE: {
                    if (grantResults.length > 0) {
                        boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        //Log.e(LOG_TAG, cameraAccepted ? "Accepted" : "rejected");
                    } else {
                        //Log.e(LOG_TAG, "grantResults IS EMPTY");
                    }
                }
                break;
            }
    }

    private class ContactListAdapter extends ArrayAdapter<Contact> {

        public ContactListAdapter() {

            super(IdentityActivity.this, R.layout.listview_item, Contacts);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);
            Contact currentContact = Contacts.get(position);
            TextView name = (TextView) view.findViewById(R.id.cNameTextView);
            name.setText(currentContact.getName());
            TextView phone = (TextView) view.findViewById(R.id.cPhoneTextView);
            phone.setText(currentContact.getPhoneNumber());
            TextView email = (TextView) view.findViewById(R.id.cEmailTextView);
            email.setText(currentContact.getEmail());
            Button deleteImageView = (Button) view.findViewById(R.id.delete_btn);
            deleteImageView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    dbHandler.deleteContact(Contacts.get(position));
                    Contacts.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(),
                            "Deleted contact", Toast.LENGTH_LONG)
                            .show();
                }
            });
            return view;
        }
    }

    private class DefaultContactListAdapter extends ArrayAdapter<Contact> {

        public DefaultContactListAdapter() {

            super(IdentityActivity.this, R.layout.listview_item_default, defaultContacts);
        }

        @Override
        public int getCount() {

            return defaultContacts.size();
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item_default, parent, false);
            Contact currentContact = defaultContacts.get(position);
            TextView name = (TextView) view.findViewById(R.id.cNameTextView);
            name.setText(currentContact.getName());
            TextView phone = (TextView) view.findViewById(R.id.cPhoneTextView);
            phone.setText(currentContact.getPhoneNumber());
            TextView email = (TextView) view.findViewById(R.id.cEmailTextView);
            email.setText(currentContact.getEmail());
            TextView title = (TextView) view.findViewById(R.id.cTitleTextView);
            title.setText(currentContact.getTitle());
            title.setSelected(true);
            return view;
        }
    }

    private class UploadPhotoFileTask extends AsyncTask<String, Integer, String> {

        Uri imageUri;
        String imageFileName;
        Bitmap imageBitmap;

        private ProgressDialog pDialog;

        public UploadPhotoFileTask(Uri photoUri, String fileName, Bitmap bitmap) {
            this.imageUri = photoUri;
            this.imageFileName = fileName;
            this.imageBitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // show progress
            pDialog = new ProgressDialog(IdentityActivity.this);
            pDialog.setMessage(getString(R.string.Uploading_Image));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            Toast.makeText(IdentityActivity.this,
                    getString(R.string.Uploading), Toast.LENGTH_LONG).show();
            chooseImageButton.setClickable(false);
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                //Compress data into Bitmap
                //If we don't do this it will fail due to large size
                //or time out

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                LogUtils.debug("ImageFileProcess","UploadPhotoFileTask() - bitmapOrg is NOT NULL, so compressing bitmap");
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                HashMap<String, String> params = new HashMap<>();
                params.put("controller", "WhiteZebra");
                params.put("action", "UploadUserPictureEncrypted");
                params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
                params.put("email", Preferences.getString(Config.CONTACT_EMAIL));
                params.put("cellphone", Preferences.getString(Config.CONTACT_CELLPHONE));
                params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
                params.put("image", encodedImage);
                params.put("fileName", this.imageFileName);
                params.put("uniqueId", Preferences.getString(Config.UNIQUE_ID));

                //LogUtils.debug("ImageFileProcess","UploadPhotoFileTask() - encodedImage (encodedBitmap) is: "+encodedImage);
                return FunctionHelper.apiCaller(context, params);
            } catch (Exception e) {
                LogUtils.debug("ImageFileProcess","UploadPhotoFileTask() - encountered exception: "+e);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //
        }

        @Override
        protected void onPostExecute(String response) {
            LogUtils.debug("ImageFileProcess", "UploadPhotoFileTask() - response is: "+response);
            if (response != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean(Config.SUCCESS);
                    if (success) {
                        Preferences.putString(Config.USER_PROFILE_PICTURE, this.imageUri.toString());
                        Toast.makeText(IdentityActivity.this, getString(R.string.Upload_Success), Toast.LENGTH_LONG).show();

                        Uri currUri = Uri.parse(Preferences.getString(Config.USER_PROFILE_PICTURE));
                        viewImage.setImageURI(currUri);//setImageBitmap(imageBitmap);
                        pDialog.cancel();
                    } else {
                        pDialog.cancel();
                        Toast.makeText(IdentityActivity.this, getString(R.string.Upload_Failure), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    pDialog.cancel();
                    Toast.makeText(IdentityActivity.this, getString(R.string.Failed_Response), Toast.LENGTH_LONG).show();
                }
            } else {
                pDialog.cancel();
                Toast.makeText(IdentityActivity.this, getString(R.string.Failed_Image), Toast.LENGTH_LONG).show();
            }
            chooseImageButton.setClickable(true);
        }
    }

    private void getSchoolBuildingList() {
        if(Preferences.isAvailable(Config.SCHOOLBUILDING_LIST_LOADDATE)) {
            try {
                boolean haveXDaysElapsed = Config.calcDaysSinceListLoad(Preferences.getString(Config.SCHOOLBUILDING_LIST_LOADDATE),3);
//                if (haveXDaysElapsed)
//                    callForSchoolBuildingList();
//                else {
                    LogUtils.debug("getschoolbuildinglistfromdefault", "loading stored schoolbuilding list");
                    loadStoredSchoolBuildingList();
                //}
            } catch(Exception e) {
                LogUtils.debug("getschoolbuildinglistfromdefault", "a parse exception was thrown while determining if the load date for the schoolbuildinglist was X days ago -> "+e);
                //callForSchoolBuildingList();
            }
        } //else {
//            callForSchoolBuildingList();
//        }
    }

    private void loadStoredSchoolBuildingList() {
        String schoolBuildingList = Preferences.getString(Config.SCHOOLBUILDING_LIST);
        LogUtils.debug("getschoolbuildinglistfromdefault","loadstoredschoolbuildinglist --> the stored schoolbuildinglist is --> "+schoolBuildingList);
        try {
            JSONObject jsonobject = new JSONObject(schoolBuildingList);
            Boolean success = jsonobject.getBoolean("success");

                final ArrayList<SpinnerItem> spinnerItems = new ArrayList<>();
                final ArrayList<String> buildingList = new ArrayList<>();
                // Locate the NodeList name
                JSONArray jsonarray = jsonobject.getJSONArray("data");
                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);
                    String id = jsonobject.getString("id");
                    String values = jsonobject.getString("value");
                    spinnerItems.add(new SpinnerItem(id, values));
                    // Populate spinner
                    buildingList.add(jsonobject.optString("value"));
                    if (Preferences.getString(Config.CONTACT_SCHOOLS).trim().equals(id)) {
                        Preferences.putString(Config.CONTACT_SCHOOLS_NAME, values);

                        //Log.d(LOG_TAG, "School name: " + values);
                    }
                }
            if ("es".equals(Preferences.getString(Config.LANGUAGE))){
                buildingList.add(0, "-Selecciona un edificio-");
                spinnerItems.add(0, new SpinnerItem("0", "-Selecciona un edificio-"));
            }else if("en".equals(Preferences.getString(Config.LANGUAGE))){
                buildingList.add(0, "-Select School-");
                spinnerItems.add(0, new SpinnerItem("0", "-Select School-"));
            }
                // Locate the spinner in activity_main.xml
                //buildingSpinner = (Spinner) findViewById(R.id.BuildingSpinner);
                //buildingSpinner = (Spinner) findViewById(R.id.new_building_spinner);
                buildingSpinner = (Spinner) findViewById(R.id.spinner_identity_building);
                // Spinner adapter
                assert buildingSpinner != null;
                buildingSpinner.setAdapter(new ArrayAdapter<String>(IdentityActivity.this,
                        //  android.R.layout.simple_spinner_dropdown_item,
                        R.layout.custom_spinner_layout,
                        buildingList) {
                            @Override
                            public boolean isEnabled(int position) {
                                if (position == 0) {
                                    return false;
                                }
                                return true;
                            }
                        }
                );
                // Spinner on item click listener
                buildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        if (position != 0) {
                            bIdWasUpdated = false;
                            selectedBuildingId = spinnerItems.get(position).getId();
                            selectedBuildingName = spinnerItems.get(position).getValue();
                            //Log.d(LOG_TAG, "1 Identity put- Building id: " + buildingId + " building name: " + buildingName);
                            if (!Preferences.getString(Config.USER_BUILDING_ID).equals(selectedBuildingId)) {
                                bIdWasUpdated = true;
                                //silently store the selected building
                                //new SetUserInfo(true).execute();
                                LogUtils.debug("callingSetUserIdentity","calling setUserInfo() method");
                                Preferences.putString(Config.USER_BUILDING_ID, selectedBuildingId);
                                Preferences.putString(Config.USER_BUILDING_NAME, selectedBuildingName);
                                setUserInfo(true);
                            }
                            //Log.d(LOG_TAG, "2 Identity put- Building name: " + Preferences.getString(Config.USER_BUILDING_NAME) + " building id: " + Preferences.getString(Config.USER_BUILDING_ID));
                            //new GetDefaultContacts().execute();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
//                        String buildingId = Preferences.getString(Config.CONTACT_SCHOOLS);
//                        String buildingName = Preferences.getString(Config.CONTACT_SCHOOLS_NAME);
                preSelectSchoolSpinner();
                LogUtils.debug("getschoolbuildinglistfromdefault","loadstoredschoolbuildinglist --> schoolbuildinglist was processed correctly");

        } catch (Exception e) {
            //Log.e("Error", e.getMessage());
            e.printStackTrace();
            LogUtils.debug("getschoolbuildinglistfromdefault","loadstoredschoolbuildinglist --> an exception was thrown while processing the schoolbuidling list");
        }
    }

    private void callForSchoolBuildingList() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("controller", "BlueDove");
        params.put("action", "GetBuildingsSB");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
        params.put("typeId", "0");

        final ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            Boolean success = jsonobject.getBoolean("success");
                            if (success) {
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy",Locale.ENGLISH);

                                LogUtils.debug("getschoolbuildinglistfromdefault","callforschoolbuildinglist --> storing date: "+dateFormat.format(cal.getTime()));
                                LogUtils.debug("getschoolbuildinglistfromdefault","callforschoolbuildinglist --> storing the list: "+response);
                                Preferences.putString(Config.SCHOOLBUILDING_LIST_LOADDATE,dateFormat.format(cal.getTime()));
                                Preferences.putString(Config.SCHOOLBUILDING_LIST,response);

                                loadStoredSchoolBuildingList();
                            }
                        } catch (Exception e) {
                            //Log.e("Error", e.getMessage());
                            e.printStackTrace();
                            LogUtils.debug("getschoolbuildinglistfromdefault","callforschoolbuildinglist --> an exception was thrown while processing the responsedata.");
                        }
                    }
                }
        );

        apiHelper.setOnErrorListener(
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Do not retry connection because the current layout of the app (as of 9/24/20) causes the app
                        // to call the GetUserProfile system to run twice when the app first runs through its onCreate() method.
//                        dismissActivityProgressDialog();
//                        setSBProfileInfoError(true);
                        LogUtils.debug("getschoolbuildinglistfromdefault","callforschoolbuildinglist --> an error was encountered while trying to establish a connection");
                    }
                }
        );

        apiHelper.prepareRequest(params, false);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private class GetSchoolBuildingsTask extends AsyncTask<Void, Void, String> {

        ArrayList<SpinnerItem> spinnerItems;

        ArrayList<String> buildingList;

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "Report");
            params.put("controller", "BlueDove");
            params.put("action", "GetBuildingsSB");
            params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
            params.put("typeId", "0");
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(String jsonData) {
            LogUtils.debug("testing","getbuild");
            if (jsonData != null) {
                spinnerItems = new ArrayList<>();
                buildingList = new ArrayList<>();
                boolean success = false;
                try {
                    JSONObject jsonobject = new JSONObject(jsonData);
                    success = jsonobject.getBoolean("success");
                    if (success) {
                        // Locate the NodeList name
                        JSONArray jsonarray = jsonobject.getJSONArray("data");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);
                            String id = jsonobject.getString("id");
                            String values = jsonobject.getString("value");
                            spinnerItems.add(new SpinnerItem(id, values));
                            // Populate spinner
                            buildingList.add(jsonobject.optString("value"));
                            if (Preferences.getString(Config.CONTACT_SCHOOLS).trim().equals(id)) {
                                Preferences.putString(Config.CONTACT_SCHOOLS_NAME, values);

                                //Log.d(LOG_TAG, "School name: " + values);
                            }
                        }
                        if ("es".equals(Preferences.getString(Config.LANGUAGE))){
                            buildingList.add(0, "-Selecciona un Edificio-");
                            spinnerItems.add(0, new SpinnerItem("0", "Selecciona un Edificio"));
                        }else if("en".equals(Preferences.getString(Config.LANGUAGE))){
                            buildingList.add(0, "-Select School-");
                            spinnerItems.add(0, new SpinnerItem("0", "Select School"));
                        }
                        // Locate the spinner in activity_main.xml
                        //buildingSpinner = (Spinner) findViewById(R.id.BuildingSpinner);
//                        buildingSpinner = (Spinner) findViewById(R.id.new_building_spinner);
                        buildingSpinner = (Spinner) findViewById(R.id.spinner_identity_building);
                        // Spinner adapter
                        assert buildingSpinner != null;
                        buildingSpinner
                                .setAdapter(new ArrayAdapter<String>(IdentityActivity.this,
                                      //  android.R.layout.simple_spinner_dropdown_item,
                                        R.layout.custom_spinner_layout,
                                        buildingList));
                        // Spinner on item click listener
                        buildingSpinner
                                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0,
                                                               View arg1, int position, long arg3) {

                                        if (position != 0) {
                                            bIdWasUpdated = false;
                                            selectedBuildingId = spinnerItems.get(position).getId();
                                            selectedBuildingName = spinnerItems.get(position).getValue();
                                            //Log.d(LOG_TAG, "1 Identity put- Building id: " + buildingId + " building name: " + buildingName);
                                            if (!Preferences.getString(Config.USER_BUILDING_ID).equals(selectedBuildingId)) {
                                                bIdWasUpdated = true;
                                                //silently store the selected building
                                                //new SetUserInfo(true).execute();
                                                setUserInfo(true);

                                            }
                                            Preferences.putString(Config.USER_BUILDING_ID, selectedBuildingId);
                                            Preferences.putString(Config.USER_BUILDING_NAME, selectedBuildingName);
                                            //Log.d(LOG_TAG, "2 Identity put- Building name: " + Preferences.getString(Config.USER_BUILDING_NAME) + " building id: " + Preferences.getString(Config.USER_BUILDING_ID));
                                            //new GetDefaultContacts().execute();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {

                                    }
                                });
//                        String buildingId = Preferences.getString(Config.CONTACT_SCHOOLS);
//                        String buildingName = Preferences.getString(Config.CONTACT_SCHOOLS_NAME);
                        preSelectSchoolSpinner();
                    }
                } catch (Exception e) {
                    //Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void preSelectSchoolSpinner() {

        String buildingName = Preferences.getString(Config.USER_BUILDING_NAME);
        String buildingId = Preferences.getString(Config.USER_BUILDING_ID);
        if (!buildingId.isEmpty() && !buildingName.isEmpty() && buildingSpinner != null) {
            ArrayAdapter arrayAdapter = (ArrayAdapter) buildingSpinner.getAdapter();
            buildingSpinner.setSelection(arrayAdapter.getPosition(buildingName));
        }

//        Spinner titleSpinner = (Spinner) findViewById(R.id.spinner_identity_title);
//        String userTitleId = Preferences.getString(Config.USER_TITLE_ID);
//        String userTitleName = Preferences.getString(Config.USER_TITLE_NAME);
//        if (!userTitleId.isEmpty() && !userTitleName.isEmpty()) {
//            ArrayAdapter arrayAdapter = (ArrayAdapter) titleSpinner.getAdapter();
//            titleSpinner.setSelection(arrayAdapter.getPosition(userTitleName));
//        }
    }

    private void getTitleList() {
        if(Preferences.isAvailable(Config.TITLE_LIST_LOADDATE)) {
            try {
                boolean haveXDaysElapsed = Config.calcDaysSinceListLoad(Preferences.getString(Config.TITLE_LIST_LOADDATE),3);
                if (haveXDaysElapsed)
                    callForTitleList();
                else {
                    LogUtils.debug("gettitlelistfromdefault", "loading stored title list");
                    loadStoredTitleList();
                }
            } catch(Exception e) {
                LogUtils.debug("gettitlelistfromdefault", "a parse exception was thrown while determining if the load date for the title list was X days ago -> "+e);
                callForTitleList();
            }
        } else {
            callForTitleList();
        }
    }

    private class GetTitles extends AsyncTask<Void, Void, String> {

        ArrayList<SpinnerItem> spinnerItems;

        ArrayList<String> titleList;

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "Locator");
            params.put("controller", "WhiteZebra");
            params.put("action", "GetTitles");
            params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
            String jsonData = FunctionHelper.apiCaller(context, params);
            return jsonData;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            LogUtils.debug("testing","gettitles");
            if (jsonData != null) {
                // Locate the spinner in activity_main.xml
                spinnerItems = new ArrayList<>();
                titleList = new ArrayList<>();
                boolean success = false;
                try {
                    JSONObject jsonobject = new JSONObject(jsonData);
                    success = jsonobject.getBoolean("success");
                   // Spinner titleSpinner = (Spinner) findViewById(R.id.TitleSpinner);
//                    Spinner titleSpinner = (Spinner) findViewById(R.id.new_title_spinner);
                    Spinner titleSpinner = (Spinner) findViewById(R.id.spinner_identity_title);
                    if (success) {
                        // Locate the NodeList name
                        JSONArray jsonarray = jsonobject.getJSONArray("data");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);
                            String id = jsonobject.getString("id");
                            String values = jsonobject.getString("value");
                            spinnerItems.add(new SpinnerItem(id, values));
                            // Populate spinner
                            titleList.add(jsonobject.optString("value"));
                            if (Preferences.getString(Config.CONTACT_TITLE).trim().equals(id)) {
                                Preferences.putString(Config.CONTACT_TITLE_NAME, values);
                            }
                        }


                        titleList.add(0, getString(R.string.select_title));
                        spinnerItems.add(0, new SpinnerItem("0", getString(R.string.select_title)));


                        // Spinner adapter
                        titleSpinner
                                .setAdapter(new ArrayAdapter<>(IdentityActivity.this,
                             //           android.R.layout.simple_spinner_dropdown_item,
                                        R.layout.custom_spinner_layout,
                                        titleList));
                        // Spinner on item click listener
                        titleSpinner
                                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0,
                                                               View arg1, int position, long arg3) {

                                        String titleId = spinnerItems.get(position).getId();
                                        String titleName = spinnerItems.get(position).getValue();
                                        Preferences.putString(Config.USER_TITLE_ID, titleId);
                                        Preferences.putString(Config.USER_TITLE_NAME, titleName);


                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {

                                    }
                                });
                        String titleId = Preferences.getString(Config.USER_TITLE_ID);
                        String titleName = Preferences.getString(Config.USER_TITLE_NAME);
                        if (!titleId.isEmpty() && !titleName.isEmpty()) {
                            ArrayAdapter arrayAdapter = (ArrayAdapter) titleSpinner.getAdapter();
                            titleSpinner.setSelection(arrayAdapter.getPosition(titleName));
                        }
                    }
//                    String titleId = Preferences.getString(Config.CONTACT_TITLE);
//                    String titleName = Preferences.getString(Config.CONTACT_TITLE_NAME);
//                    if (!titleId.isEmpty() && !titleName.isEmpty()) {
//                        ArrayAdapter arrayAdapter = (ArrayAdapter) titleSpinner.getAdapter();
//                        titleSpinner.setSelection(arrayAdapter.getPosition(titleName));
//                    }
                } catch (Exception e) {
                    //Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadStoredTitleList() {
        String storedTitleList = Preferences.getString(Config.TITLE_LIST);
        LogUtils.debug("gettitlesfromdefault","loadstoredtitlelist --> the stored title list is --> "+storedTitleList);

        try {
            JSONObject jsonobject = new JSONObject(storedTitleList);
            boolean success = jsonobject.getBoolean("success");

            Spinner titleSpinner = (Spinner) findViewById(R.id.spinner_identity_title);

            final ArrayList<SpinnerItem> spinnerItems = new ArrayList<>();
            ArrayList<String> titleList = new ArrayList<>();

            // Locate the NodeList name
            JSONArray jsonarray = jsonobject.getJSONArray("data");
            for (int i = 0; i < jsonarray.length(); i++) {
                jsonobject = jsonarray.getJSONObject(i);

                String id = jsonobject.getString("id");
                String values = jsonobject.getString("value");

                spinnerItems.add(new SpinnerItem(id, values));
                // Populate spinner
                titleList.add(jsonobject.optString("value"));
//                if (Preferences.getString(Config.CONTACT_TITLE).trim().equals(id)) {
//                    Preferences.putString(Config.CONTACT_TITLE_NAME, values);
//                }
            }

            if ("es".equals(Preferences.getString(Config.LANGUAGE))){
                titleList.add(0, "-Selecciona un Título-");
                spinnerItems.add(0, new SpinnerItem("0", "-Selecciona un Título-"));
            }else if("en".equals(Preferences.getString(Config.LANGUAGE))){
                titleList.add(0, "-Select title-");
                spinnerItems.add(0, new SpinnerItem("0", "-Select title-"));
            }
            // Spinner adapter
            titleSpinner
                    .setAdapter(new ArrayAdapter<>(IdentityActivity.this,
                            //           android.R.layout.simple_spinner_dropdown_item,
                            R.layout.custom_spinner_layout,
                            titleList));
            // Spinner on item click listener
            titleSpinner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int position, long arg3) {

                            /*String*/ tempTitleId = spinnerItems.get(position).getId();
                            /*String*/ tempTitleName = spinnerItems.get(position).getValue();


//                            Preferences.putString(Config.USER_TITLE_ID, titleId);
//                            Preferences.putString(Config.USER_TITLE_NAME, titleName);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });

            LogUtils.debug("loadstoredtitlelist","current title id --> "+Preferences.getString(Config.USER_TITLE_ID)+" --> current title name is --> " + Preferences.getString(Config.USER_TITLE_NAME));
            String userTitleId = Preferences.getString(Config.USER_TITLE_ID);
            String userTitleName = Preferences.getString(Config.USER_TITLE_NAME);
            if (!userTitleId.isEmpty() && !userTitleName.isEmpty()) {
                ArrayAdapter arrayAdapter = (ArrayAdapter) titleSpinner.getAdapter();
                titleSpinner.setSelection(arrayAdapter.getPosition(userTitleName));
            }

//            String titleId = Preferences.getString(Config.CONTACT_TITLE);
//            String titleName = Preferences.getString(Config.CONTACT_TITLE_NAME);
//
//            LogUtils.debug("gettitlesfromdefault","loadstoredtitlelist --> saved CONTACT_TITLE is: "+Preferences.getString(Config.CONTACT_TITLE));
//            LogUtils.debug("gettitlesfromdefault","loadstoredtitlelist --> saved CONTACT_TITLE_NAME is: "+Preferences.getString(Config.CONTACT_TITLE_NAME));
//            if (!titleId.isEmpty() && !titleName.isEmpty()) {
//                ArrayAdapter arrayAdapter = (ArrayAdapter) titleSpinner.getAdapter();
//                titleSpinner.setSelection(arrayAdapter.getPosition(titleName));
//            }

            LogUtils.debug("gettitlesfromdefault","loadstoredtitlelist --> storedtitlelist was processed correctly");

        } catch (Exception e) {
            LogUtils.debug("gettitlesfromdefault","loadstoredtitlelist --> an exception was thrown while processing the storedtitlelist");
        }
    }

    private void callForTitleList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "WhiteZebra");
        params.put("action", "GetTitles");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));

        final ApiHelper apiHelper = new ApiHelper();
        apiHelper.setOnSuccessListener(
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtils.debug("gettitlesfromdefault","callfortitlelist --> responsedata is: "+response);
                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            boolean success = jsonobject.getBoolean("success");

                            Spinner titleSpinner = (Spinner) findViewById(R.id.spinner_identity_title);

                            if(success) {
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy",Locale.ENGLISH);

                                LogUtils.debug("gettitlesfromdefault","callfortitlelist --> storing date: "+dateFormat.format(cal.getTime()));
                                LogUtils.debug("gettitlesfromdefault","callfortitlelist --> storing the list: "+response);
                                Preferences.putString(Config.TITLE_LIST_LOADDATE,dateFormat.format(cal.getTime()));
                                Preferences.putString(Config.TITLE_LIST,response);

                                loadStoredTitleList();
                            } else {
                                LogUtils.debug("gettitlesfromdefault","callfortitlelist --> responsedata has a data key and success is false.");
                            }

//                            String titleId = Preferences.getString(Config.CONTACT_TITLE);
//                            String titleName = Preferences.getString(Config.CONTACT_TITLE_NAME);
//                            if (!titleId.isEmpty() && !titleName.isEmpty()) {
//                                ArrayAdapter arrayAdapter = (ArrayAdapter) titleSpinner.getAdapter();
//                                titleSpinner.setSelection(arrayAdapter.getPosition(titleName));
//                            }

                        } catch (Exception e) {
                            //Log.e("Error", e.getMessage());
                            e.printStackTrace();
                            LogUtils.debug("gettitlesfromdefault","callfortitlelist --> an exception was thrown while processing the responsedata.");
                        }
                    }
                }
        );

        apiHelper.setOnErrorListener(
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Do not retry connection because the current layout of the app (as of 9/24/20) causes the app
                        // to call the GetUserProfile system to run twice when the app first runs through its onCreate() method.
//                        dismissActivityProgressDialog();
//                        setSBProfileInfoError(true);
                        LogUtils.debug("gettitlesfromdefault","callfortitlelist --> an error was encountered while trying to establish a connection");
                    }
                }
        );

        apiHelper.prepareRequest(params, false);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private void getDefaultContactsList() {
        if(Preferences.isAvailable(Config.DEFAULTCONTACTS_LIST_LOADDATE)) {
            try {
                boolean haveXDaysElapsed = Config.calcDaysSinceListLoad(Preferences.getString(Config.DEFAULTCONTACTS_LIST_LOADDATE),3);
                if (haveXDaysElapsed)
                    callForDefaultContactsList();
                else {
                    LogUtils.debug("getdefaultcontactsfromdefault", "loading stored defaultcontacts list");
                    loadStoredDefaultContactsList();
                }
            } catch(Exception e) {
                LogUtils.debug("getdefaultcontactsfromdefault", "a parse exception was thrown while determining if the load date for the default contact list was X days ago -> "+e);
                callForDefaultContactsList();
            }
        } else {
            callForDefaultContactsList();
        }
    }

    private void callForDefaultContactsList() {
        //LogUtils.debug("getDefaultContacts","calling getDefaultContacts");
        final HashMap<String, String> params = new HashMap<>();
        params.put("controller", "WhiteZebra");
        params.put("action", "GetDefaultContacts");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
        params.put("schoolId", Preferences.getString(Config.USER_BUILDING_ID));

        final ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            LogUtils.debug("getdefaultcontactsfromdefault","callfordefaultcontactslist --> responsedata is: "+response);
                            ContactDefault defaults = new Gson().fromJson(response, ContactDefault.class);
                            if (defaults.getSuccess()) {

                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy",Locale.ENGLISH);

                                LogUtils.debug("getdefaultcontactsfromdefault","callfordefaultcontactslist --> storing date: "+dateFormat.format(cal.getTime()));
                                LogUtils.debug("getdefaultcontactsfromdefault","callfordefaultcontactslist --> storing the list: "+response);
                                Preferences.putString(Config.DEFAULTCONTACTS_LIST_LOADDATE,dateFormat.format(cal.getTime()));
                                Preferences.putString(Config.DEFAULTCONTACTS_LIST,response);

                                loadStoredDefaultContactsList();
                                LogUtils.debug("getdefaultcontactsfromdefault","callfordefaultcontactslist --> responsedata has a data key and success is true");
                                //Set contacts list size, size of one item is 70 dp
                            }
//                            else {
//                                //no default
//                                defaultContactsTitle.setVisibility(View.GONE);
//                                defaultContactListView.setVisibility(View.GONE);
//                                LogUtils.debug("getDefaultContacts","responsedata has a data key and success is false");
//                            }
                        } catch (Exception e) {
                            LogUtils.debug("getdefaultcontactsfromdefault","callfordefaultcontactslist --> an exception was thrown while processing the response data");
                        }
                    }
                }
        );

        apiHelper.setOnErrorListener(
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Do not retry connection because the current layout of the app (as of 9/24/20) causes the app
                        // to call the GetUserProfile system to run twice when the app first runs through its onCreate() method.
                        LogUtils.debug("getdefaultcontactsfromdefault","callfordefaultcontactslist --> an error was encountered while trying to establish a connection");
                    }
                }
        );

        apiHelper.prepareRequest(params,false);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private void loadStoredDefaultContactsList() {
        String storedDefaultContactsList = Preferences.getString(Config.DEFAULTCONTACTS_LIST);

        try {
            LogUtils.debug("getdefaultcontactsfromdefault","loadstoreddefaultcontactslist -->  the stored defaultcontactslist is: "+storedDefaultContactsList);
            ContactDefault defaults = new Gson().fromJson(storedDefaultContactsList, ContactDefault.class);
            //Log.d(LOG_TAG, responseData);
            Log.d("identityJSON","getData: "+defaults.getData());
            Log.d("identityJSON","getData size: "+defaults.getData().size());
            defaultContacts = defaults.getData();
            for (Contact c : defaultContacts) {
                Log.d(LOG_TAG, "c.title = " + c.getTitle());
            }
            //Log.d(LOG_TAG, "Default contacts: " + defaultContacts.toString());
            ArrayAdapter<Contact> defaultAdapter = new DefaultContactListAdapter();
            //Log.d(LOG_TAG, "count = " + defaultAdapter.getCount());
            defaultContactListView.setAdapter(defaultAdapter);
            //Set default contacts list size, size of one is 70 dp
            ViewGroup.LayoutParams defaultParams = defaultContactListView.getLayoutParams();
            defaultParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 71 * defaultAdapter.getCount(), getResources().getDisplayMetrics());
            defaultContactListView.setLayoutParams(defaultParams);
            defaultContactListView.requestLayout();
            //Set the listview visibility
            if (defaultAdapter.getCount() > 0) {
                defaultContactsTitle.setVisibility(View.VISIBLE);
                defaultContactListView.setVisibility(View.VISIBLE);
            } else {
                defaultContactsTitle.setVisibility(View.GONE);
                defaultContactListView.setVisibility(View.GONE);
            }
            LogUtils.debug("getdefaultcontactsfromdefault","loadstoreddefaultcontactslist --> defaultcontactslist was processed correctly");
            //Set contacts list size, size of one item is 70 dp

        } catch (Exception e) {
            LogUtils.debug("getdefaultcontactsferomdefault","loadstoreddefaultcontactslist --> an exception was thrown while processing the response data");
        }
    }

//    private class GetDefaultContacts extends AsyncTask<Void, Void, String> {
//
//        @Override
//        protected String doInBackground(Void... voidParams) {
//
//            HashMap<String, String> params = new HashMap<>();
////            params.put("controller", "Locator");
//            params.put("controller", "WhiteZebra");
//            params.put("action", "GetDefaultContacts");
//            params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
//            params.put("schoolId", Preferences.getString(Config.USER_BUILDING_ID));
//            return FunctionHelper.apiCaller(context, params);
//        }
//
//        @Override
//        protected void onPostExecute(String responseData) {
//            LogUtils.debug("testing","getdef");
//            if (responseData != null) {
//                Log.d("identityJSON", "json = " + responseData);
//                ContactDefault defaults = new Gson().fromJson(responseData, ContactDefault.class);
//                if (defaults.getSuccess()) {
//                    //Log.d(LOG_TAG, responseData);
//                    Log.d("identityJSON","getData: "+defaults.getData());
//                    Log.d("identityJSON","getData size: "+defaults.getData().size());
//                    defaultContacts = defaults.getData();
//                    for (Contact c : defaultContacts) {
//                        Log.d(LOG_TAG, "c.title = " + c.getTitle());
//                    }
//                    //Log.d(LOG_TAG, "Default contacts: " + defaultContacts.toString());
//                    ArrayAdapter<Contact> defaultAdapter = new DefaultContactListAdapter();
//                    //Log.d(LOG_TAG, "count = " + defaultAdapter.getCount());
//                    defaultContactListView.setAdapter(defaultAdapter);
//                    //Set default contacts list size, size of one is 70 dp
//                    ViewGroup.LayoutParams defaultParams = defaultContactListView.getLayoutParams();
//                    defaultParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 71 * defaultAdapter.getCount(), getResources().getDisplayMetrics());
//                    defaultContactListView.setLayoutParams(defaultParams);
//                    defaultContactListView.requestLayout();
//                    //Set the listview visibility
//                    Log.d("identityJSON","adapter size: "+defaultAdapter.getCount());
//                    if (defaultAdapter.getCount() > 0) {
//                        defaultContactsTitle.setVisibility(View.VISIBLE);
//                        defaultContactListView.setVisibility(View.VISIBLE);
//                    } else {
//                        defaultContactsTitle.setVisibility(View.GONE);
//                        defaultContactListView.setVisibility(View.GONE);
//                    }
//                    //Set contacts list size, size of one item is 70 dp
//                } else {
//                    //no default
//                }
//            }
//            //ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
////            ScrollView scrollView = (ScrollView) findViewById(R.id.new_scroll_view);
//            //ScrollView scrollView = (ScrollView) findViewById(R.id.identity_scrollview);
//            //scrollView.fullScroll(ScrollView.FOCUS_UP);
//            //scrollView.smoothScrollTo(0,0);
//        }
//    }

    private void setUserInfo(final Boolean isSilent) {
        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "RedBear");
        params.put("action", "SaveIdentity");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
        params.put("unique_id", Preferences.getString(Config.UNIQUE_ID));
        params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
        params.put("fname", Preferences.getString(Config.USER_FULL_NAME));
        params.put("title", Preferences.getString(Config.USER_TITLE_ID));
        params.put("school", Preferences.getString(Config.USER_BUILDING_ID));
        params.put("cellphone", Preferences.getString(Config.CONTACT_CELLPHONE));
        params.put("email", Preferences.getString(Config.CONTACT_EMAIL));
        params.put("image", new File(Preferences.getString(Config.USER_PROFILE_PICTURE)).getName());
        params.put("app_type", "3");
        params.put("room", Preferences.getString(Config.USER_ROOM));
        params.put("floor", Preferences.getString(Config.USER_FLOOR));
        params.put("description", Preferences.getString(Config.USER_DESCRIPTION));

        final ApiHelper apiHelper = new ApiHelper();

        final RetryCounter retryCounter = new RetryCounter();

        apiHelper.setOnSuccessListener(new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Response defaults = new Gson().fromJson(response, Response.class);
                    if (defaults.getSuccess()) {
                        if (bIdWasUpdated) {
                            Preferences.putBoolean(Config.USER_INFO_WAS_UPDATED, true);
                        }
                        //Save successful
                        if (!isSilent) {
                            Toast.makeText(getApplicationContext(), getString(R.string.Info_Saved), Toast.LENGTH_SHORT).show();
                        }

                        LogUtils.debug("setuserinfoindefault","response data returned with data key and success key of true");

                    } else {
                        //error
                        if (!isSilent) {
                            Toast.makeText(getApplicationContext(), getString(R.string.Info_Error), Toast.LENGTH_SHORT).show();
                        }
                        LogUtils.debug("setuserinfoindefault","response data returned with data key and success key of false");
                    }

                } catch (Exception e) {
                    LogUtils.debug("setuserinfoindefault","an exception was thrown while processing the response data");
                    if(!isSilent){
                        Toast.makeText(getApplicationContext(), getString(R.string.Info_Error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        apiHelper.setOnErrorListener(new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int currRC = retryCounter.getRetryCount();//ApiHelper.getInstance(HomeActivity.this).getRetryCount();//ApiHelper.getInstance(HomeActivity.this).getRetryCount();
                LogUtils.debug("setuserinfodefault","retried: " + apiHelper.getUrl() +  " backend call\n" + currRC + " times. Received an error code from the server -> "+error);

                if(currRC == 0) {
                    if(!isSilent){
                        Toast.makeText(getApplicationContext(), getString(R.string.Info_Error), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    retryCounter.decrementRetryCount();
                    ApiHelper.getInstance(IdentityActivity.this).startRequest(apiHelper);
                }
            }
        });

        apiHelper.prepareRequest(params,true);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private class SetUserInfo extends AsyncTask<Void, Void, String> {
        boolean isSilent = false;
        SetUserInfo(Boolean silent){
            isSilent = silent;
        }

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "User");
            params.put("controller", "RedBear");
            params.put("action", "SaveIdentity");
            params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
            params.put("unique_id", Preferences.getString(Config.UNIQUE_ID));
            params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
            params.put("fname", Preferences.getString(Config.USER_FULL_NAME));
            params.put("title", Preferences.getString(Config.USER_TITLE_ID));
            params.put("school", Preferences.getString(Config.USER_BUILDING_ID));
            params.put("cellphone", Preferences.getString(Config.CONTACT_CELLPHONE));
            params.put("email", Preferences.getString(Config.CONTACT_EMAIL));
            params.put("image", new File(Preferences.getString(Config.USER_PROFILE_PICTURE)).getName());
            params.put("app_type", "3");
            //params.put("room", hasRoom ? Preferences.getString(Config.USER_ROOM) : " ");
            params.put("room", Preferences.getString(Config.USER_ROOM));
            params.put("floor", Preferences.getString(Config.USER_FLOOR));
            params.put("description", Preferences.getString(Config.USER_DESCRIPTION));

            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(String responseData) {

            if (responseData != null) {
                //Log.d(LOG_TAG, "json = " + responseData);
                Response defaults = new Gson().fromJson(responseData, Response.class);
                if (defaults.getSuccess()) {
                    if(bIdWasUpdated){
                        Preferences.putBoolean(Config.USER_INFO_WAS_UPDATED, true);

                    }
                    //Save successful
                    if(!isSilent){
                        Toast.makeText(getApplicationContext(), getString(R.string.Info_Saved), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    //error
                    if(!isSilent){
                        Toast.makeText(getApplicationContext(), getString(R.string.Info_Error), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
    }

    private void setupRoomField(){
        Boolean hasRoomField = Preferences.getBoolean(Config.ROOM_TOGGLE);
        userRoomEditText = (EditText) findViewById(R.id.et_identity_room);
        if(hasRoomField){
            userRoomEditText.setVisibility(View.VISIBLE);
            //load in any data stored in preferences


            userRoomEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //store in preferences
                    Preferences.putString(Config.USER_ROOM, s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        else{
            LinearLayout roomFieldContainer = (LinearLayout) findViewById(R.id.linear_layout_identity_room);
            roomFieldContainer.setVisibility(View.GONE);
            //userRoomEditText.setVisibility(View.GONE);
        }


        if (!Strings.isNullOrEmpty(Preferences.getString(Config.USER_ROOM)))
            userRoomEditText.setText(Preferences.getString(Config.USER_ROOM));
    }

    @Override
    public void onBackPressed() {

//        if(getFragmentManager().getBackStackEntryCount() == 0) {
        //this.finish();
//        } else {
        //getFragmentManager().popBackStack();
        //}
        LogUtils.debug("backPressed","Identity Activity - back button pressed");;
    }

}
