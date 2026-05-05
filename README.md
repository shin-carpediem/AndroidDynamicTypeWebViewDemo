# AndroidDynamicTypeWebViewDemo

Android の **フォントスケール** を WebView 上でも正しくスケールさせる方法を示すデモアプリです。  
[iOS 版（DynamicTypeWebViewDemo）](https://github.com/shin-carpediem/DynamicTypeWebViewDemo) の Android 対応版です。

<img width="350" alt="Screenshot_20260503_125638" src="https://github.com/user-attachments/assets/e926dfec-7b35-4737-b33f-20bb19c535a5" />

## 概要

ネイティブ View は Android のフォントスケール設定に自動対応しますが、WebView 内の HTML/CSS はデフォルトでは追従しません。  
このアプリでは、2 つのシンプルな実装ポイントを使って WebView 内のテキストをフォントスケールに連動させる方法を示します。

## iOS 版との対応関係

| 項目 | iOS | Android |
|---|---|---|
| スケール変化の検知 | `UIContentSizeCategory.didChangeNotification` | `onConfigurationChanged`（`fontScale`） |
| WebView への適用 | CSS `font: -apple-system-body` | `evaluateJavascript("setFontScale($scale)")` |
| スケールする単位 | `em` | `em` |
| スケールしない単位 | `px` 固定 | `px` 固定 |

## 実装のポイント

### ① AndroidManifest: `fontScale` を `configChanges` に追加

```xml
<activity
    android:configChanges="fontScale|uiMode|density"
    ... >
```

Activity を再生成せず `onConfigurationChanged` でフォントスケール変化を受け取れるようにします。

### ② Kotlin: フォントスケール変化を検知して WebView に注入

```kotlin
// 起動時の初期化
applyFontScale(resources.configuration.fontScale)

// フォントスケール変化を検知（iOS の didChangeNotification に相当）
override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    applyFontScale(newConfig.fontScale)
}

private fun applyFontScale(fontScale: Float) {
    // JavaScript 経由で HTML の body font-size を更新
    webView.evaluateJavascript("setFontScale($fontScale)", null)
}
```

### ③ HTML/JS: `setFontScale()` で body の font-size を更新

```javascript
function setFontScale(scale) {
    // 基準サイズ 16px にスケールを掛けて body に適用
    document.body.style.fontSize = (16 * scale) + 'px';
}
```

### ④ CSS: `font-size` は `em` 単位で指定

```css
/* ✅ em 単位 → body サイズ変化に追従してスケール */
.title       { font-size: 1.143em; }
.description { font-size: 0.857em; }

/* ❌ px 固定 → フォントスケールが変わっても変化しない */
.bad-title   { font-size: 16px; }
```

## 動作確認方法

1. Android Studio でプロジェクトを開いてエミュレーターまたは実機で起動
2. **設定 → ディスプレイ → フォントサイズ** でフォントサイズを変更
3. アプリに戻ると `onConfigurationChanged` が発火し、`em` 指定のテキストのみサイズが変化することを確認できる

> **Note:** 設定画面から戻った瞬間に変化が反映されます（リロード不要）。

## 動作環境

- Android 7.0+ (API 24+)
- Android Studio Ladybug 以降推奨
- Kotlin 2.0+
