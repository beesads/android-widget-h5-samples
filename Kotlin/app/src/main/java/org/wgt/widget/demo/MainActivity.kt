package org.wgt.widget.demo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {


    private var mWebView: WebView? = null


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mWebView = findViewById<WebView>(R.id.webview)?.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = WidgetWebViewClient { widgetUrl ->
                val intent = Intent(this@MainActivity, WidgetActivity::class.java)
                intent.putExtra("widget_content_url", widgetUrl)
                startActivity(intent)
            }
            loadUrl("file:///android_asset/widget.html")
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

    private class WidgetWebViewClient(
        private val redirectBlock: (String) -> Unit
    ) : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            request?.url.toString().let {
                redirectBlock.invoke(it)
            }
            return true
        }

    }
}