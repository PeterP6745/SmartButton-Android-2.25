package messagelogix.com.smartbuttoncommunications.utils;

import android.content.Context;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Vahid
 * This is the util class that we use for caching
 */
public class WebCacher {

    private static final String LOG_TAG = WebCacher.class.getSimpleName();

    public WebCacher(Context context, String url) {

        WebView mWebView = new WebView(context);
        assert mWebView != null;
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().supportZoom();
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setAppCachePath(context.getCacheDir().getAbsolutePath());
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                //view.setVisibility(View.GONE);
                Log.e(LOG_TAG, "---------------------onPageFinished--------------------------");
            }
        });
        mWebView.loadUrl(url);
    }
}
