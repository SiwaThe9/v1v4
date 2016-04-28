package id.co.viva.news.app.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;

import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.component.ProgressWheel;

/**
 * Created by reza on 13/03/15.
 */
public class ActBrowser extends ActionBarActivity {

    private WebView webView;
    private String mUrl;
    private String mChannel;
    private ProgressWheel progressWheel;
    private boolean isInternetPresent = false;
    private boolean isFirstShow = false;
    private String clickedUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get URL
        getParameterIntent();

        setContentView(R.layout.act_browser);

        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();

        //Define Views
        defineViews();

        //Set Header
        setActionBarTheme(mChannel);

        //Show Web Page
        if (isInternetPresent) {
            if (mUrl != null) {
                if (mUrl.length() > 0) {
                    startLoadWeb(mUrl);
                    isFirstShow = true;
                }
            } else {
                onBackPressed();
                Toast.makeText(this, R.string.label_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            onBackPressed();
            Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void defineViews() {
        webView = (WebView) findViewById(R.id.web_view_from_link);
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
    }

    private void getParameterIntent() {
        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        mChannel = intent.getStringExtra("channel");
    }

    private void startLoadWeb(String url) {
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                clickedUrl = url;
                invalidateOptionsMenu();
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressWheel.setVisibility(View.GONE);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    private void setActionBarTheme(String channel) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        if (channel != null) {
            if (channel.length() > 0) {
                ColorDrawable colorDrawable = new ColorDrawable();
                if (channel.equalsIgnoreCase("bola")) {
                    colorDrawable.setColor(getResources().getColor(R.color.color_bola));
                    getSupportActionBar().setBackgroundDrawable(colorDrawable);
                    progressWheel.setBarColor(getResources().getColor(R.color.color_bola));
                } else if (channel.equalsIgnoreCase("vivalife")) {
                    colorDrawable.setColor(getResources().getColor(R.color.color_life));
                    getSupportActionBar().setBackgroundDrawable(colorDrawable);
                    progressWheel.setBarColor(getResources().getColor(R.color.color_life));
                } else if (channel.equalsIgnoreCase("otomotif")) {
                    colorDrawable.setColor(getResources().getColor(R.color.color_auto));
                    getSupportActionBar().setBackgroundDrawable(colorDrawable);
                    progressWheel.setBarColor(getResources().getColor(R.color.color_auto));
                } else {
                    colorDrawable.setColor(getResources().getColor(R.color.color_news));
                    getSupportActionBar().setBackgroundDrawable(colorDrawable);
                    progressWheel.setBarColor(getResources().getColor(R.color.color_news));
                }
                getSupportActionBar().setTitle(channel);
            }
        } else {
            progressWheel.setBarColor(ContextCompat.getColor(this, R.color.news_base_color));
            getSupportActionBar().setTitle(getResources().getString(R.string.label_reference_article));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onPause", (Class[]) null)
                    .invoke(webView, (Object[]) null);
        } catch (ClassNotFoundException cnfe) {
            cnfe.getMessage();
        } catch (NoSuchMethodException nsme) {
            nsme.getMessage();
        } catch (InvocationTargetException ite) {
            ite.getMessage();
        } catch (IllegalAccessException iae) {
            iae.getMessage();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        isFirstShow = false;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_browser, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        android.support.v7.widget.ShareActionProvider myShareActionProvider =
                (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_SEND);
        if (isFirstShow) {
            myIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
        } else {
            myIntent.putExtra(Intent.EXTRA_TEXT, clickedUrl);
        }
        myIntent.setType("text/plain");
        myShareActionProvider.setShareIntent(myIntent);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch(keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    finish();
                    onBackPressed();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
