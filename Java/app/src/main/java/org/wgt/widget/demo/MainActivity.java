package org.wgt.widget.demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Nullable
    private WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mWebView = findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        mWebView.setWebViewClient(new WidgetWebViewClient());
        mWebView.loadUrl("file:///android_asset/widget.html");
    }

    @Override protected void onPause() {
        if (mWebView!=null){
            mWebView.onPause();
            mWebView.pauseTimers();
        }
        super.onPause();
    }

    @Override protected void onResume() {
        if (mWebView!=null) {
            mWebView.onResume();
            mWebView.resumeTimers();
        }
        super.onResume();
    }

    @Override protected void onDestroy() {
        if (mWebView!=null){
            mWebView.resumeTimers();
            mWebView.loadUrl("about:blank");
            mWebView.stopLoading();
            if (mWebView.getHandler() != null) {
                mWebView.getHandler().removeCallbacksAndMessages(null);
            }
            mWebView.removeAllViews();
            ViewGroup viewGroup;
            if ((viewGroup = ((ViewGroup) mWebView.getParent())) != null) {
                viewGroup.removeView(mWebView);
            }
            mWebView.setWebChromeClient(null);
            mWebView.setTag(null);
            mWebView.clearHistory();
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    private class WidgetWebViewClient extends WebViewClient {
        @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Intent intent = new Intent(MainActivity.this, WidgetActivity.class);
            intent.putExtra("widget_content_url", request.getUrl().toString());
            startActivity(intent);
            return true;
        }
    }

}