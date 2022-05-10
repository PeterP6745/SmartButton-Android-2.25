package messagelogix.com.smartbuttoncommunications.activities.help;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.spec.SecretKeySpec;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.core.MainActivity;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.Cryptography;
import messagelogix.com.smartbuttoncommunications.utils.FileDownloader;
import messagelogix.com.smartbuttoncommunications.utils.NetworkCheck;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Created by Vahid
 * This is the activity to show a web page inside our app
 */
public class WebViewActivity extends AppCompatActivity {

    private static final String LOG_TAG = WebViewActivity.class.getSimpleName();

    private WebView mWebView;

    private Context context = this;

    private ProgressDialog mDialog;

    private NetworkCheck networkCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        buildActionBar();
        initWebView();
    }

    private void initWebView() {
        if (mWebView != null) {
            mWebView.destroy();
        }
        mWebView = (WebView) findViewById(R.id.webView);
        assert mWebView != null;
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().supportZoom();
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

//        networkCheck = new NetworkCheck(context);

        String url = getIntent().getExtras().getString("url");
        String title = getIntent().getExtras().getString("title");
        setTitle(title);

        Log.e("WEBVIEW HELP", "URL = " + url);
        Log.e("WEBVIEW HELP", "Title = " + title);
        
        if (url != null) {
            //Check the network connection
//            if (!networkCheck.isNetworkConnected()) {
//                loadCache(url);
//            }
            loadUrl(url);
        }
    }

    private void loadUrl(String url) {
        Log.e("WEBVIEW HELP", "LOADING FROM URL");
        if (url.endsWith(".pdf")) {
            url = "https://docs.google.com/gview?embedded=true&url=".concat(url);
        }

        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e("WEBVIEW HELP", "Page STARTED");
                showProgress(true);
            }

            public void onPageFinished(WebView view, String url) {
                Log.e("WEBVIEW HELP", "Page FINISHED");
                //mWebView.loadUrl( "javascript:window.location.reload( true )" );
                showProgress(false);
            }
        });

        mWebView.loadUrl(url);
    }

    private void loadCache(String url) {
        Log.e("WEBVIEW HELP", "LOADING FROM CACHE");
        if (url.endsWith(".pdf")) {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            String extStorageDirectory = Environment.getExternalStorageDirectory()
                    .toString();
            File folder = new File(extStorageDirectory, FileDownloader.PDF);
            File file = new File(folder, fileName);
            File fileDecrypted = null;
            try {
                fileDecrypted = File.createTempFile("d" + fileName, null, this.getExternalCacheDir());
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream fos = null;
            try {
                assert fileDecrypted != null;
                fos = new FileOutputStream(fileDecrypted);
                // Writes bytes from the specified byte array to this file output stream
                fos.write(decryptFile(file));
            } catch (FileNotFoundException e) {
                System.out.println("File not found" + e);
            } catch (IOException ioe) {
                System.out.println("Exception while writing file " + ioe);
            } finally {
                // close the streams using close method
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException ioe) {
                    System.out.println("Error while closing stream: " + ioe);
                }
            }
            if (file.exists()) {
                displayPDF(fileDecrypted);
            }
            finish();
        }
    }

    private void displayPDF(File pdfFile) {
        Log.e("WEBVIEW HELP", "DISPLAY PDF");
        Uri path = Uri.fromFile(pdfFile);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(
                    context,
                    "PDF Reader application is not installed in your device",
                    Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, e.toString());
        }
    }

    public void buildActionBar() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void showProgress(boolean show) {
        if (show) {
            if (mDialog == null) {
                mDialog = new ProgressDialog(this);
                mDialog.setMessage(this.getResources().getString(R.string.generic_progressdialog_mess2));
                mDialog.setIndeterminate(false);
                mDialog.setCancelable(true);
            }
            mDialog.show();
        } else {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.cancel();
                mDialog.dismiss();
            }
        }
    }

    private void goBack() {
        super.onBackPressed();
    }

    private byte[] decryptFile(File file) {
        Preferences.init(context);
        SecretKeySpec secretKey = Preferences.getSecretKeySpec();
        Cryptography cryptography = new Cryptography(secretKey);
        // Decode the encoded data with AES
        byte[] decodedBytes = null;
        try {
            decodedBytes = cryptography.decryptAES(readBytes(file), secretKey);
        } catch (Exception e) {
            //Log.e(LOG_TAG, "AES decryption error");
        }
        return decodedBytes;
    }

    private byte[] readBytes(File file) {
        try {
            return Files.toByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
            case android.R.id.home:
                // app icon in action bar clicked; go home
                goBack();
                return true;
            case R.id.action_logout:
                Preferences.putBoolean(Config.IS_LOGGED_IN, false);
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * on Create Options Menu
     *
     * @param menu object
     * @return true or false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds options to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
