package org.wgt.widget.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WidgetActivity : AppCompatActivity() {

    private var mWebView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val widgetUrl = intent.getStringExtra("widget_content_url")
        if (TextUtils.isEmpty(widgetUrl)) {
            finish()
            return
        }
        enableEdgeToEdge()
        setContentView(R.layout.activity_widget)
        mWebView = findViewById(R.id.webview)
        mWebView?.let { webView ->
            ViewCompat.setOnApplyWindowInsetsListener(webView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.loadUrl(widgetUrl!!)
            webView.webViewClient = WidgetWebViewClient()

            onBackPressedDispatcher.addCallback {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    override fun onPause() {
        mWebView?.let {
            it.onPause()
            it.pauseTimers()
        }
        super.onPause()
    }

    override fun onResume() {
        mWebView?.let {
            it.onResume()
            it.resumeTimers()
        }
        super.onResume()
    }


    override fun onDestroy() {
        mWebView?.let {
            it.resumeTimers()
            it.loadUrl("about:blank")
            it.stopLoading()
            it.handler.removeCallbacksAndMessages(null)
            it.removeAllViews()
            (it.parent as? ViewGroup)?.removeView(it)
            it.webChromeClient = null
            it.tag = null
            it.clearHistory()
            it.destroy()
            mWebView = null
        }
        super.onDestroy()
    }


    private class WidgetWebViewClient: WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            Log.e("Test", "onPageFinished: $url")
            super.onPageFinished(view, url)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }
    }
}