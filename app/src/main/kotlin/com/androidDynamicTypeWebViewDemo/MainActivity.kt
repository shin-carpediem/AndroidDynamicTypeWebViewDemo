package com.androidDynamicTypeWebViewDemo

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var fontScaleLabel: TextView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        fontScaleLabel = findViewById(R.id.fontScaleLabel)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // ローカルの assets/demo.html を読み込む
        webView.loadUrl("file:///android_asset/demo.html")

        applyFontScale(resources.configuration.fontScale)
    }

    // android:configChanges="fontScale" により Activity の再生成を抑制し、
    // ここでフォントスケール変化を受け取る（iOS の didChangeNotification に相当）
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applyFontScale(newConfig.fontScale)
    }

    private fun applyFontScale(fontScale: Float) {
        // fontScale 1.0 → 100（標準）, 2.0 → 200（200%）
        // px・em 問わず WebView 内のテキスト全体をスケールする
        webView.settings.textZoom = (fontScale * 100).roundToInt()

        fontScaleLabel.text = fontScaleDisplayName(fontScale)
    }

    private fun fontScaleDisplayName(scale: Float): String = when {
        scale <= 0.87f -> "Small（小）"
        scale <= 1.02f -> "Default（標準）"
        scale <= 1.17f -> "Large（大）"
        scale <= 1.32f -> "Larger（特大）"
        else           -> "Largest（最大）"
    }
}
