# AndroidDynamicTypeWebViewDemo

Android の **フォントスケール** を WebView 上でもスケールさせる方法を示すデモアプリです。  
[iOS 版（DynamicTypeWebViewDemo）](https://github.com/shin-carpediem/DynamicTypeWebViewDemo) の Android 対応版です。

<img width="350" alt="Screenshot_20260503_125638" src="https://github.com/user-attachments/assets/e926dfec-7b35-4737-b33f-20bb19c535a5" />

## 概要

ネイティブ View は Android のフォントスケール設定に自動対応しますが、WebView 内の HTML/CSS はデフォルトでは追従しません。  
このアプリでは `WebSettings.setTextZoom(int)` を使って OS のフォントスケールを WebView にブリッジする方法を示します。  
CSS の単位（px・em・rem）に関係なくテキスト全体がスケールされる点が特徴です。

## iOS 版との対応関係

| 項目 | iOS | Android |
|---|---|---|
| スケール変化の検知 | `UIContentSizeCategory.didChangeNotification` | `onConfigurationChanged`（`fontScale`） |
| WebView への適用 | CSS `font: -apple-system-body` | `WebSettings.setTextZoom(int)` |
| スケールする単位 | `em` | `px`・`em`・`rem` すべてスケールされる |
| スケールしない単位/要素 | `px` 固定・`rem`（ルート基準のため） | 幅・高さ・余白など非テキスト要素 |

## 実装のポイント

### ① AndroidManifest: `fontScale` を `configChanges` に追加

```xml
<activity
    android:configChanges="fontScale|uiMode|density"
    ... >
```

Activity を再生成せず `onConfigurationChanged` でフォントスケール変化を受け取れるようにします。

### ② Kotlin: フォントスケール変化を検知して `setTextZoom` で反映

```kotlin
// 起動時の初期化
applyFontScale(resources.configuration.fontScale)

// フォントスケール変化を検知（iOS の didChangeNotification に相当）
override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    applyFontScale(newConfig.fontScale)
}

private fun applyFontScale(fontScale: Float) {
    // fontScale 1.0 → 100（標準）, 2.0 → 200（200%）
    // px・em 問わず WebView 内のテキスト全体をスケールする
    webView.settings.textZoom = (fontScale * 100).roundToInt()
}
```

### ③ CSS: テキストは単位を問わずスケールされる

`setTextZoom` はレンダリングエンジンレベルで適用されるため、HTML/JS の変更は不要です。

```css
/* ✅ px 固定でも setTextZoom でスケールされる */
.title       { font-size: 16px; }

/* ✅ em 相対でも同様にスケールされる */
.description { font-size: 0.75em; }

/* ✅ rem 相対でも同様にスケールされる */
.caption     { font-size: 0.75rem; }

/* ⚠️ テキスト以外（幅・高さ・余白）は変化しない */
.icon        { width: 48px; height: 48px; }  /* → スケールされない */
```

## 動作確認方法

1. Android Studio でプロジェクトを開いてエミュレーターまたは実機で起動
2. **設定 → ディスプレイ → フォントサイズ** でフォントサイズを変更
3. アプリに戻ると `onConfigurationChanged` が発火し、`px`・`em`・`rem` 問わずテキスト全体がスケールされることを確認できる

> **Note:** 設定画面から戻った瞬間に変化が反映されます（リロード不要）。

## 動作環境

- Android 7.0+ (API 24+)
- Android Studio Ladybug 以降推奨
- Kotlin 2.0+
