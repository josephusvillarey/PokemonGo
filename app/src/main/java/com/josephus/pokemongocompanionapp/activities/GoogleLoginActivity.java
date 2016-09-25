package com.josephus.pokemongocompanionapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.josephus.pokemongocompanionapp.R;

public class GoogleLoginActivity extends AppCompatActivity {

  private static final String TAG = GoogleLoginActivity.class.getSimpleName();

  @BindView(R.id.webview) WebView webView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_google_login);
    ButterKnife.bind(this);

    String url = getIntent().getStringExtra("url");
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setWebChromeClient(new WebChromeClient() {
      @Override public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (consoleMessage.message().startsWith("MAGIC")) {
          String msg = consoleMessage.message().substring(5); // strip off prefix
          String key = msg.replace("<head><title>Success code=", "");
          key = key.substring(0, key.indexOf("&amp;"));

          Log.d(TAG, "KEY: " + key);

          Intent i = new Intent();
          i.putExtra("key", key);
          setResult(RESULT_OK, i);
          finish();

          return true;
        }
        return false;
      }
    });
    webView.setWebViewClient(new WebViewClient() {
      @Override public void onPageFinished(WebView view, String url) {
        if (url.startsWith("https://accounts.google.com/o/oauth2/approval")) {
          // get key on this page
          view.loadUrl(
              "javascript:console.log('MAGIC'+document.getElementsByTagName('html')[0].innerHTML);");
        }
        super.onPageFinished(view, url);
      }
    });
    webView.loadUrl(url);
  }
}