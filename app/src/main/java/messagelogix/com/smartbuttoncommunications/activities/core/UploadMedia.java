package messagelogix.com.smartbuttoncommunications.activities.core;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.utils.AndroidMultiPartEntity;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.MarshMallowPermission;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

import static android.graphics.BitmapFactory.decodeFile;


/**
 * Created by Richard on 2/8/2017.
 */
public class UploadMedia extends AppCompatActivity {

    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
    private String timeStamp;


    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "SmartButtonMediaUpload";

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int CAMERA_SELECT_IMAGE_REQUEST_CODE = 300;
    private static final int CAMERA_SELECT_VIDEO_REQUEST_CODE = 400;


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final boolean IS_ABOVE_KITKAT = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    private Uri fileUri; // file url to store image/video
    public static  String imagePath;
    private static String videoPath;
    private static String sbCode = "";
    private static String unique_id = Preferences.getString(Config.UNIQUE_ID);
    private Context context = this;

    public static int serverResponseCode = 200;

    long totalSize = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_media);
        initButtons();
    }

    /**
     * Checking if device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    public void initButtons(){
        Button takePhotoBtn = (Button) findViewById(R.id.take_photo_button);
        Button selectPhotoBtn = (Button) findViewById(R.id.select_photo_button);

        Button takeVideoBtn = (Button) findViewById(R.id.take_video_button);
        Button selectVideoBtn = (Button) findViewById(R.id.select_video_button);

        Button attachBtn = (Button) findViewById(R.id.upload_media_button);

        /**
         *      Take Photo
         */
            takePhotoBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    takePhoto();
                }
            });

        /**
         *      Select Photo
         */

            selectPhotoBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectPhoto();
                }
            });


        /**
         *      Select Video
         */

            selectVideoBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectVideo();
                }
            });


        /**
         *      Take Video
         */

            takeVideoBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                   takeVideo();
                }
            });

        /**
         *      Attach Media
         */
        attachBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if (imagePath != null) {
//                    new UploadMedia.UploadPhotoFileTask(imagePath).execute(Config.API_URL);
//                    Preferences.putString(Config.USER_PROFILE_PICTURE, imagePath);
//                }
//                else{
//                    Toast.makeText(getApplicationContext(),
//                            "No Media", Toast.LENGTH_SHORT)
//                            .show();
//                }

                new UploadVideoToServer().execute();
            }
        });
    }



    private void selectPhoto(){
        if (marshMallowPermission.checkPermissionForCamera() && marshMallowPermission.checkPermissionForExternalStorage()) {
            timeStamp = getTimeStamp();
//            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            File file = new File(android.os.Environment.getExternalStorageDirectory(), "EN_" + Preferences.getString(Config.UNIQUE_ID) + "_" + timeStamp + ".jpg");
//            fileUri = Uri.fromFile(file);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, CAMERA_SELECT_IMAGE_REQUEST_CODE);

        }
    }

    private void takePhoto(){
        if (marshMallowPermission.checkPermissionForCamera() && marshMallowPermission.checkPermissionForExternalStorage()) {
            timeStamp = getTimeStamp();
            // permission has been granted, continue as usual
            //Log.e(LOG_TAG, "Permission Granted!");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(android.os.Environment.getExternalStorageDirectory(), "EN_" + Preferences.getString(Config.UNIQUE_ID) + "_" + timeStamp + ".jpg");
            fileUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            // 1 = Take Photo
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
    }

    private void selectVideo(){
        if (marshMallowPermission.checkPermissionForCamera() && marshMallowPermission.checkPermissionForExternalStorage()) {
            timeStamp = getTimeStamp();
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, ""), CAMERA_SELECT_VIDEO_REQUEST_CODE);
        }
    }

    private void takeVideo(){
        if (marshMallowPermission.checkPermissionForCamera() && marshMallowPermission.checkPermissionForExternalStorage()) {
            timeStamp = getTimeStamp();
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO, timeStamp);

            // set video quality
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
            // name

            // start the video capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
        }
    }

    private String getTimeStamp() {

        Long tsLong = (System.currentTimeMillis() / 1000);
        return tsLong.toString();
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        String picturePath = null;
        switch (requestCode) {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {

                    // successfully captured the image
                    String tempPath = "EN_" + Preferences.getString(Config.UNIQUE_ID) + "_" + timeStamp + ".jpg";
                    File file = new File(Environment.getExternalStorageDirectory(), tempPath);
                    picturePath = file.getAbsolutePath();
                    imagePath = picturePath;
                    //Preferences.putString(Config.USER_PROFILE_PICTURE, picturePath);

                } else if (resultCode == RESULT_CANCELED) {

                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(),
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                            .show();
                }

                break;
            case CAMERA_SELECT_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getApplicationContext(),
                            "We in there boyz", Toast.LENGTH_SHORT)
                            .show();
                    if (!IS_ABOVE_KITKAT) {
                        fileUri = Uri.parse(getAbsolutePath(getApplicationContext(), data.getData()));
                    } else {
                        fileUri = Uri.parse(getPath(getApplicationContext(), data.getData()));
                    }


                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        //Log.d(LOG_TAG, String.valueOf(bitmap));
                        //viewImage.setImageBitmap(bitmap);
                        String tempPath = "EN_" + Preferences.getString(Config.UNIQUE_ID) + "_" + timeStamp + ".jpg";
                        File file = new File(Environment.getExternalStorageDirectory(), tempPath);
                        picturePath = file.getAbsolutePath();
                        imagePath = picturePath;

                        Log.d("************","Picture Path: " + picturePath + "; Image Path: " + imagePath);
                        Log.d("************","FileUri: " + fileUri + " FileUri.path: " + fileUri.getPath());
//                        try {
//                            FileOutputStream out = new FileOutputStream(file);
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//                            out.flush();
//                            out.close();
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                } else if (resultCode == RESULT_CANCELED) {

                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(),
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                            .show();
                }

                break;
            case CAMERA_CAPTURE_VIDEO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {

                    // video successfully recorded
                    videoPath = fileUri.getPath();

                } else if (resultCode == RESULT_CANCELED) {

                    // user cancelled recording
                    Toast.makeText(getApplicationContext(),
                            "User cancelled video recording", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    // failed to record video
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case CAMERA_SELECT_VIDEO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (!IS_ABOVE_KITKAT) {
                        fileUri = Uri.parse(getAbsolutePath(getApplicationContext(), data.getData()));
                    } else {
                        fileUri = Uri.parse(getPath(getApplicationContext(), data.getData()));
                    }

                    videoPath = fileUri.getPath();
                } else if (resultCode == RESULT_CANCELED) {

                    // user cancelled recording
                    Toast.makeText(getApplicationContext(),
                            "User cancelled video selection", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    // failed to record video
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to select video", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
        }
    }



    private String getAbsolutePath(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.MediaColumns.DATA};
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);

        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type, String ts) {
        return Uri.fromFile(getOutputMediaFile(type, ts));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type, String ts) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("", "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + unique_id + "_" + ts + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + unique_id + "_" + ts + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */
    @TargetApi(19)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    private Bitmap decodeFile(String path) {

        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 225;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    private class UploadPhotoFileTask extends AsyncTask<String, Integer, String> {

        String photoFilePath;

        private ProgressDialog pDialog;

        public UploadPhotoFileTask(String filePath) {

            this.photoFilePath = filePath;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // show progress
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Uploading image. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            Toast.makeText(context,
                    "Uploading image ...", Toast.LENGTH_LONG).show();
         //   chooseImageButton.setClickable(false);
        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                //Compress data into Bitmap
                //If we don't do this it will fail due to large size
                //or time out
                Bitmap bitmapOrg = decodeFile(photoFilePath);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                if (bitmapOrg != null) {
                    bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                }
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                File f = new File(photoFilePath);
                String imageName = f.getName();
                //Log.d(LOG_TAG, "Image name: " + imageName);
                HashMap<String, String> params = new HashMap<>();
//                params.put("controller", "Locator");
                params.put("controller", "WhiteZebra");
                params.put("action", "UploadUserPictureEncrypted");
                params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
                params.put("email", Preferences.getString(Config.CONTACT_EMAIL));
                params.put("cellphone", Preferences.getString(Config.CONTACT_CELLPHONE));
                params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
                params.put("image", encodedImage);
                params.put("fileName", imageName);
                params.put("uniqueId", Preferences.getString(Config.UNIQUE_ID));
                return FunctionHelper.apiCaller(context, params);
            } catch (Exception e) {
                //Log.d(LOG_TAG, "Error" + e.toString());
                //Log.e(LOG_TAG, "Error in http connection " + e.toString());
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //
        }

        @Override
        protected void onPostExecute(String response) {

            pDialog.cancel();
            if (response != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Boolean success = jsonResponse.getBoolean(Config.SUCCESS);
                    if (success) {
                        Preferences.putString(Config.USER_PROFILE_PICTURE, photoFilePath);
                        Toast.makeText(context,
                                "Upload image success", Toast.LENGTH_LONG).show();
                        String profilePicPath = Preferences.getString(Config.USER_PROFILE_PICTURE);
                        //Show the picture
                        if (!profilePicPath.isEmpty()) {
                            //viewImage.setImageBitmap(decodeFile(profilePicPath));
                        }
                    } else {
                        Toast.makeText(context,
                                "Failed to upload image", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context,
                            "Failed to get a response", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context,
                        "Error: Failed to upload image", Toast.LENGTH_LONG).show();
            }
            //chooseImageButton.setClickable(true);
        }
    }

    /**
     * Uploading the file to server
     */

//    public static int upLoad2Server(String sourceFileUri) {
//        String upLoadServerUri = "your remote server link";
//        // String [] string = sourceFileUri;
//        String fileName = sourceFileUri;
//
//        HttpURLConnection conn = null;
//        DataOutputStream dos = null;
//        DataInputStream inStream = null;
//        String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        int maxBufferSize = 1 * 1024 * 1024;
//        String responseFromServer = "";
//
//        File sourceFile = new File(sourceFileUri);
//        if (!sourceFile.isFile()) {
//            Log.e("Huzza", "Source File Does not exist");
//            return 0;
//        }
//        try { // open a URL connection to the Servlet
//            FileInputStream fileInputStream = new FileInputStream(sourceFile);
//            URL url = new URL(upLoadServerUri);
//            conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
//            conn.setDoInput(true); // Allow Inputs
//            conn.setDoOutput(true); // Allow Outputs
//            conn.setUseCaches(false); // Don't use a Cached Copy
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
//            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//            conn.setRequestProperty("uploaded_file", fileName);
//            dos = new DataOutputStream(conn.getOutputStream());
//
//            dos.writeBytes(twoHyphens + boundary + lineEnd);
//            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
//            dos.writeBytes(lineEnd);
//
//            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
//            Log.i("Huzza", "Initial .available : " + bytesAvailable);
//
//            bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            buffer = new byte[bufferSize];
//
//            // read file and write it into form...
//            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//            while (bytesRead > 0) {
//                dos.write(buffer, 0, bufferSize);
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//            }
//
//            // send multipart form data necesssary after file data...
//            dos.writeBytes(lineEnd);
//            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//            // Responses from the server (code and message)
//            serverResponseCode = conn.getResponseCode();
//            String serverResponseMessage = conn.getResponseMessage();
//
//            Log.i("Upload file to server", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
//            // close streams
//            Log.i("Upload file to server", fileName + " File is written");
//            fileInputStream.close();
//            dos.flush();
//            dos.close();
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////this block will give the response of upload link
//        try {
//            BufferedReader rd = new BufferedReader(new InputStreamReader(conn
//                    .getInputStream()));
//            String line;
//            while ((line = rd.readLine()) != null) {
//                Log.i("Huzza", "RES Message: " + line);
//            }
//            rd.close();
//        } catch (IOException ioex) {
//            Log.e("Huzza", "error: " + ioex.getMessage(), ioex);
//        }
//        return serverResponseCode;  // like 200 (Ok)
//
//    } // end upLoad2Server


     private class UploadVideoToServer extends AsyncTask<Void, Integer, String> {


        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.API_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(videoPath);

                // Adding file data to http body
                entity.addPart("media_file", new FileBody(sourceFile));

                // Log.d("myLog", "aa_id" + aaId + "confirmation_code" + aaCode + "accountId" + accountId);
                // Extra parameters if you want to pass to server

                entity.addPart("api_key", new StringBody(Config.API_KEY));
                entity.addPart("app_id", new StringBody(Config.APP_ID));
                entity.addPart("controller", new StringBody("WhiteZebra"));
                entity.addPart("action", new StringBody("AttachSBMedia"));
                entity.addPart("unique_id", new StringBody(Preferences.getString(Config.UNIQUE_ID)));
                entity.addPart("unique_code", new StringBody(timeStamp));
                entity.addPart("accountId", new StringBody(Preferences.getString(Config.ACCOUNT_ID)));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }


        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
          //  progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
      //      progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
       //     progressBar.setProgress(progress[0]);

            // updating percentage value
        //    txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {

            return uploadFile();
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result !=null){
                Log.e("Response>>>>>>", "Response from server: " + result);
                String message;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        message = "Upload is successful";
                    } else {
                        message = "Upload failed";
                    }
                } catch (JSONException ex) {
                    message = "An error occurred please try again later";
                }
                // showing the server response in an alert dialog
            //    showAlert(message);

            }

        }

    }

}
