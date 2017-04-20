package com.digitcreativestudio.registrasi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.digitcreativestudio.registrasi.utils.FileUtil;

import java.util.Arrays;

public class WebActivity extends AppCompatActivity {
    private final String URL = "http://ternatekota.sicantik.layanan.go.id/perizinan_online";
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final String TAG = WebActivity.class.getSimpleName();
    private WebView webView;
    private boolean webViewFinished = false;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;

    private String [] mimeTypes = {
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    };

    ProgressDialog progressDialog;
    AlertDialog alertDialog;

    BroadcastReceiver receiver;
    IntentFilter filter;

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        progressDialog = new ProgressDialog(WebActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        alertDialog = new AlertDialog.Builder(this).create();

        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webView.setWebViewClient(new PQClient());
        webView.setWebChromeClient(new PQChromeClient());

        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else if(Build.VERSION.SDK_INT < 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected){
            findViewById(R.id.text_no_internet).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.text_no_internet).setVisibility(View.GONE);
            webView.loadUrl(URL);
        }

        receiver = new NetworkChangeReceiver();
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    public class WebAppInterface {
        /** Show a toast from the web page */
        @JavascriptInterface
        public void showProgress() {
            showProgressBar();
        }

        @JavascriptInterface
        public void hideProgress(){
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    results = new Uri[]{data.getData()};
                }
            }
            if(results != null){
                if(!Arrays.asList(mimeTypes).contains(FileUtil.getType(this, results[0]))) {
                    results = null;
                    showAlert("Gagal", "Silakan pilih file .xls .xlsx .doc .docx .pdf.", "OK", null);
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            if (requestCode == FILECHOOSER_RESULTCODE) {

                if (null == this.mUploadMessage) {
                    return;
                }

                Uri result = null;

                if (resultCode != RESULT_OK) {
                    result = null;
                } else {

                    // retrieve from the private variable if the intent is null
                    result = data.getData();
                }

                if(result !=null){
                    if(!Arrays.asList(mimeTypes).contains(FileUtil.getType(this, result))) {
                        result = null;
                        showAlert("Gagal", "Silakan pilih file .xls .xlsx .doc .docx .pdf.", "OK", null);
                    }
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }

        return;
    }

    public class PQChromeClient extends WebChromeClient {

        // For Android 5.0
        @Override
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.setType("file/*");
            contentSelectionIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);

            startActivityForResult(contentSelectionIntent, INPUT_FILE_REQUEST_CODE);

            return true;
        }

        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.setType("file/*");
            contentSelectionIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);

            // On select image call onActivityResult method of activity
            startActivityForResult(contentSelectionIntent, FILECHOOSER_RESULTCODE);
        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {

            openFileChooser(uploadMsg, acceptType);
        }

    }

    public class PQClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // If url contains mailto link then open Mail Intent
            if (url.contains("mailto:")) {
                // Could be cleverer and use a regex
                //Open links in new browser
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }else {
                // Stay within this webview and load url
                view.loadUrl(url);
                return true;
            }
        }

        //Show loader on url load
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // Then show progress  Dialog
            // in standard case YourActivity.this
            showProgressBar();
            webViewFinished = false;
        }

        // Called when all page resources loaded
        public void onPageFinished(WebView view, String url) {
            webViewFinished = true;
            webView.loadUrl("javascript:(function(){ "+
                    "document.getElementById('android-app').style.display='none';})()");

            try {
                // Close progressDialog
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            ConnectivityManager cm =
                    (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnected();

            if(!isConnected){
                findViewById(R.id.text_no_internet).setVisibility(View.VISIBLE);
            }else{
                findViewById(R.id.text_no_internet).setVisibility(View.GONE);
                if(!webViewFinished)
                    webView.loadUrl(URL);
            }
        }
    }

    private void showProgressBar() {
        progressDialog.show();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.progress_bar);
    }

    private void showAlert(String title, String message, String positiveButton, DialogInterface.OnClickListener listener){
        if(!alertDialog.isShowing()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setPositiveButton(positiveButton, listener);
            alertDialogBuilder.setCancelable(false);
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}
