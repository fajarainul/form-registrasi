package com.digitcreativestudio.registrasi;

import android.annotation.TargetApi;
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
import android.widget.TextView;

import com.digitcreativestudio.registrasi.utils.FileUtil;
import com.digitcreativestudio.registrasi.utils.PermissionUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;

public class WebActivity extends AppCompatActivity {
    private final String URL = "http://fajarainul.informatikaundip.com/register/form.php";
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

    View dialogView;
    AlertDialog.Builder builder;
    AlertDialog successDialog;

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

    @TargetApi(23)
    protected void askPermissions(String[] permissions) {
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        builder = new AlertDialog.Builder(WebActivity.this);
        dialogView = getLayoutInflater().inflate(R.layout.success_dialog, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                successDialog.dismiss();
                startActivity(new Intent(WebActivity.this, WebActivity.class));
                finish();
            }
        });
        builder.setTitle("Berhasil");

        String[] permissions = new String[]{
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.INTERNET
        };

        if(PermissionUtil.shouldAskPermissions(this, permissions)){
            askPermissions(permissions);
        }

        progressDialog = new ProgressDialog(WebActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        alertDialog = new AlertDialog.Builder(this).create();

        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
        webView.clearCache(true);
        webView.setWebViewClient(new PQClient());
        webView.setWebChromeClient(new PQChromeClient());

        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else if(Build.VERSION.SDK_INT < 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

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
        @Override
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
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

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.setType("file/*");
            contentSelectionIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);

            startActivityForResult(contentSelectionIntent, FILECHOOSER_RESULTCODE);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {

            openFileChooser(uploadMsg, acceptType);
        }

    }

    public class PQClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if(!url.contains("register_success"))
                showProgressBar();
            webViewFinished = false;
        }

        public void onPageFinished(WebView view, String url) {
            webViewFinished = true;

            if(url.contains("register_success")){
                webView.setVisibility(View.INVISIBLE);
            }

            webView.addJavascriptInterface(new Object(){
                @SuppressWarnings("unused")
                @JavascriptInterface
                public void showHTML(final String html) {
                    Document doc = Jsoup.parse(html);
                    Element blockContent = doc.select(".block-content").first();
                    Element table = blockContent.getElementsByTag("table").first();
                    Elements bs = table.getElementsByTag("b");
                    ((TextView) dialogView.findViewById(R.id.success_no_registration))
                            .setText(bs.eq(0).text());
                    ((TextView) dialogView.findViewById(R.id.success_name))
                            .setText(bs.eq(1).text());
                    ((TextView) dialogView.findViewById(R.id.success_license_name))
                            .setText(bs.eq(2).text());
                    successDialog = builder.create();
                    successDialog.show();
                }
            }, "HTMLOUT");
            webView.loadUrl("javascript:window.HTMLOUT.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

            try {
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
