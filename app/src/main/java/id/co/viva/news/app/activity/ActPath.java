package id.co.viva.news.app.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.model.Path;

/**
 * Created by reza on 11/12/14.
 */
public class ActPath extends ActionBarActivity {

    private WebView mWebiew;
    private ProgressBar mProgressBar;
    public static Path socmedPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_path);
        getHeaderActionBar();
        defineView();
        try {
            mWebiew.loadUrl(Constant.PATH_AUTHENTICATE_URL + "?response_type=code&client_id=" +
                    Constant.PATH_CLIENT_ID);
            Log.i(Constant.TAG, "URL FIRST : " + Constant.PATH_AUTHENTICATE_URL + "?response_type=code&client_id=" +
                    Constant.PATH_CLIENT_ID);
            mWebiew.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    mProgressBar.setVisibility(View.VISIBLE);
                    if(url != null) {
                        Log.i(Constant.TAG, "URL : " + url);
                        String redirect_url = Constant.PATH_REDIRECT;
                        if(url.startsWith(redirect_url)) {
                            socmedPath.setPath_code(url.replace(redirect_url + "/?code=", ""));
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void defineView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mWebiew = (WebView) findViewById(R.id.web_path);
        socmedPath = new Path();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getHeaderActionBar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.news_base_color)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Path");
    }

}
