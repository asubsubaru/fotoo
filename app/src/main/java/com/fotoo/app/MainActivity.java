package com.fotoo.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.os.PowerManager;
import android.content.Context;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private WebView webView;
    private PowerManager.WakeLock wakeLock;

    // ⚠️ NAS IP adresini buraya yaz
    private static final String NAS_URL = "http://192.168.1.64:5000/slide_android44";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN |
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_FULLSCREEN |
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "Fotoo:WakeLock"
        );
        wakeLock.acquire();

        webView = new WebView(this);
        setContentView(webView);

        hideSystemUI();

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(false);

        webView.setBackgroundColor(Color.BLACK);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                hideSystemUI();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Bağlantı hatası — hata mesajı göster
                String html = "<html><body style='background:#000;color:#888;font-family:sans-serif;" +
                    "display:flex;align-items:center;justify-content:center;height:100vh;margin:0;text-align:center;'>" +
                    "<div><div style='font-size:48px;margin-bottom:20px;'>📡</div>" +
                    "<div style='font-size:18px;margin-bottom:10px;'>NAS\'a bağlanılamıyor</div>" +
                    "<div style='font-size:13px;color:#555;'>" + NAS_URL + "</div>" +
                    "<div style='font-size:12px;color:#444;margin-top:20px;'>Wi-Fi bağlantısını kontrol edin<br>ve uygulamayı yeniden başlatın</div>" +
                    "</div></body></html>";
                view.loadData(html, "text/html", "UTF-8");
            }
        });

        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(NAS_URL);
    }

    private void hideSystemUI() {
        webView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
        if (webView != null) webView.destroy();
    }

    @Override
    public void onBackPressed() {
        // Geri tuşu devre dışı
    }
}
