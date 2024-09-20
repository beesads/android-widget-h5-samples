package org.wgt.widget.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WidgetActivity extends AppCompatActivity {

    @Nullable
    private WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String widgetUrl = getIntent().getStringExtra("widget_content_url");
        if (TextUtils.isEmpty(widgetUrl)) {
            finish();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_widget);
        mWebView = findViewById(R.id.webview);

        ViewCompat.setOnApplyWindowInsetsListener(mWebView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                Log.e("Test", "onPageFinished: " + url);
                super.onPageFinished(view, url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.e("Test", "onConsoleMessage: " + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }

        });
        mWebView.loadUrl(widgetUrl);
    }

    @Override protected void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
            mWebView.pauseTimers();
        }
        super.onPause();
    }

    @Override protected void onResume() {
        if (mWebView != null) {
            mWebView.onResume();
            mWebView.resumeTimers();
        }
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override protected void onDestroy() {
        if (mWebView != null) {
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

}