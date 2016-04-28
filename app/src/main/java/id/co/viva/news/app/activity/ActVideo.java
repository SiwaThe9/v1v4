package id.co.viva.news.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import id.co.viva.news.app.R;

/**
 * Created by reza on 13/01/15.
 */
public class ActVideo extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private String urlVideo;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Header
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getHeaderActionBar();

        //Get Parameter
        Bundle bundle = getIntent().getExtras();
        urlVideo = bundle.getString("urlVideo");

        setContentView(R.layout.act_video);

        //Play Video
        youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtube_player);
        youTubePlayerView.initialize(getResources().getString(R.string.public_api_key_google), this);
    }

    private void getHeaderActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("Video");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
        String[] separated_slash = urlVideo.split("/");
        String slash = separated_slash[separated_slash.length - 1];
        String[] separated_equal = slash.split("=");
        String url = separated_equal[separated_equal.length - 1];
        //if (separated.length < 5) {
        //    onBackPressed();
        //    Toast.makeText(this, R.string.label_error, Toast.LENGTH_SHORT).show();
        //} else {
        //    url = separated[4];
            if (!wasRestored) {
                youTubePlayer.cueVideo(url);
            }
        //}
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    "There was an error initializing the YouTubePlayer (%1$s)",
                    errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    public YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubePlayerView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            getYouTubePlayerProvider().initialize(getResources().getString(R.string.public_api_key_google), this);
        }
    }

}
