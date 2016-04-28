package id.co.viva.news.app.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dd.processbutton.iml.ActionProcessButton;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.component.FloatingLabelView;
import id.co.viva.news.app.interfaces.OnCompleteListener;
import id.co.viva.news.app.interfaces.OnFacebookInfoListener;
import id.co.viva.news.app.interfaces.OnPathListener;
import id.co.viva.news.app.services.GetFacebookInfo;
import id.co.viva.news.app.services.UserAccount;
import id.co.viva.news.app.services.Validation;

/**
 * Created by reza on 27/11/14.
 */
public class ActLogin extends ActionBarActivity implements OnCompleteListener, OnFacebookInfoListener, OnPathListener,
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //Google SignIn Instances
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 0;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    //Facebook Instances
    private final List<String> facebook_permission =
            Arrays.asList("public_profile", "email");

    //Path Instances
    public static final int REQUEST_PATH_AUTHENTICATE = 9999;

    private ActionProcessButton btnRegister;
    private ActionProcessButton btnSign;
    private FloatingLabelView mEmail;
    private FloatingLabelView mPassword;
    private TextView tvForgotPassword;
    private ImageView iconFb;
    private ImageView iconPath;
    private ImageView iconGPlus;
    private Validation validation;
    private UserAccount userAccount;
    private Boolean isInternetPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        getHeaderActionBar();
        isInternetPresent = Global.getInstance(this).
                getConnectionStatus().isConnectingToInternet();
        defineView();
        handleListener();
        getGoogleClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else if(requestCode == REQUEST_PATH_AUTHENTICATE) {
            if(responseCode == RESULT_OK) {
                userAccount = new UserAccount(this);
                userAccount.requestPathAccessToken(ActPath.socmedPath, this);
            }
        } else {
            if(Session.getActiveSession() != null) {
                Session.getActiveSession().onActivityResult(this, requestCode, responseCode, intent);
            }
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void getGoogleClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    private void handleListener() {
        btnSign.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        iconFb.setOnClickListener(this);
        iconGPlus.setOnClickListener(this);
        iconPath.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
    }

    private void defineView() {
        validation = new Validation();
        mEmail = (FloatingLabelView) findViewById(R.id.form_email);
        mPassword = (FloatingLabelView) findViewById(R.id.form_password);
        iconFb = (ImageView) findViewById(R.id.img_fb);
        iconGPlus = (ImageView) findViewById(R.id.img_gplus);
        iconPath = (ImageView) findViewById(R.id.img_path);
        btnSign = (ActionProcessButton) findViewById(R.id.btn_log_in);
        btnRegister = (ActionProcessButton) findViewById(R.id.btn_register);
        tvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        btnSign.setMode(ActionProcessButton.Mode.ENDLESS);
        btnRegister.setMode(ActionProcessButton.Mode.ENDLESS);
    }

    private void getHeaderActionBar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.news_base_color)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.label_login);
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
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        getProfileInfoFromGPlus();
        Toast.makeText(this, R.string.label_successful_gplus_connected,
                Toast.LENGTH_LONG).show();
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
        refreshContent();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_log_in) {
            String email = mEmail.getText();
            String password = mPassword.getText();
            if(isInternetPresent) {
                if(validation.isEmailValid(email) && validation.isLengthValid(password)) {
                    userAccount = new UserAccount(email, password, this, this);
                    disableWhenPressed();
                    btnSign.setProgress(1);
                    userAccount.signIn(Constant.CODE_VIVA);
                } else if(!validation.isEmailValid(email) && validation.isLengthValid(password)) {
                    Toast.makeText(this, R.string.label_validation_email, Toast.LENGTH_SHORT).show();
                } else if(validation.isEmailValid(email) && !validation.isLengthValid(password)) {
                    Toast.makeText(this, R.string.label_validation_password_length, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.label_validation_both, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
        } else if(view.getId() == R.id.btn_register) {
            Intent intent = new Intent(this, ActRegistration.class);
            startActivity(intent);
        } else if(view.getId() == R.id.img_path) {
            Intent intent = new Intent(this, ActPath.class);
            startActivityForResult(intent, REQUEST_PATH_AUTHENTICATE);
        } else if(view.getId() == R.id.img_gplus) {
            disableWhenPressed();
            signInWithGplus();
        } else if(view.getId() == R.id.img_fb) {
            try {
                openActiveSession();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if(view.getId() == R.id.tv_forgot_password) {
            Intent intent = new Intent(this, ActForgotPassword.class);
            startActivity(intent);
        }
    }

    private void disableParentView(LinearLayout parentLayout) {
        if(parentLayout != null) {
            for (int i=0; i<parentLayout.getChildCount(); i++) {
                View views = parentLayout.getChildAt(i);
                views.setEnabled(false);
            }
        }
    }

    private void enableParentView(LinearLayout parentLayout) {
        if(parentLayout != null) {
            for (int i=0; i<parentLayout.getChildCount(); i++) {
                View views = parentLayout.getChildAt(i);
                views.setEnabled(true);
            }
        }
    }

    private void disableWhenPressed() {
        btnSign.setEnabled(false);
        btnRegister.setEnabled(false);
        iconPath.setEnabled(false);
        iconGPlus.setEnabled(false);
        iconFb.setEnabled(false);
        tvForgotPassword.setEnabled(false);
        disableParentView(mEmail);
        disableParentView(mPassword);
    }

    private void enableWhenPressed() {
        btnSign.setEnabled(true);
        btnRegister.setEnabled(true);
        iconPath.setEnabled(true);
        iconGPlus.setEnabled(true);
        iconFb.setEnabled(true);
        tvForgotPassword.setEnabled(true);
        enableParentView(mEmail);
        enableParentView(mPassword);
    }

    @Override
    public void onComplete(String message) {
        btnSign.setProgress(100);
        Toast.makeText(this,
                message, Toast.LENGTH_SHORT).show();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                refreshContent();
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 1000);
    }

    @Override
    public void onDelay(String message) {
        btnSign.setProgress(100);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                refreshContent();
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 1000);
    }

    @Override
    public void onFailed(String message) {
        btnSign.setProgress(0);
        enableWhenPressed();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String message) {
        btnSign.setProgress(0);
        enableWhenPressed();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        if (!mIntentInProgress) {
            mConnectionResult = result;
            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    private void refreshContent() {
        finish();
        Intent intent = new Intent(this, ActLanding.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void getProfileInfoFromGPlus() {
        try {
            if(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String id = currentPerson.getId();
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                userAccount = new UserAccount(this);
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + Constant.PROFILE_PIC_SIZE;
                userAccount.saveLoginStatesSocmed(id, Constant.CODE_G_PLUS, email, personName, personPhotoUrl);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state);
        }
    };

    private void onSessionStateChange(Session session, SessionState state) {
        if (state.isOpened()) {
            new GetFacebookInfo(this, session).execute();
        } else if (state.isClosed()) {}
    }

    private void openActiveSession() {
        Session.OpenRequest request = new Session.OpenRequest(this);
        request.setCallback(statusCallback);
        request.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
        request.setPermissions(facebook_permission);
        Session session = new Session(this);
        session.openForRead(request);
        Session.setActiveSession(session);
    }

    @Override
    public void onCompleteGetInfo(GraphUser user, Response response) {
        String user_id = user.getId();
        String user_name = user.getFirstName();
        String user_email = user.getUsername();
        if(user_email == null) {
            user_email = user.getName().toString();
        }
        userAccount = new UserAccount(this);
        userAccount.saveLoginStatesSocmed(user_id, Constant.CODE_FACEBOOK, user_email, user_name,
                Constant.URL_FACEBOOK_PHOTO + user_id + "/picture?type=large");
    }

    @Override
    public void onSuccessGetInfo() {
        refreshContent();
    }

    @Override
    public void onSavePathAttributes(String access_token, String user_id) {
        userAccount = new UserAccount(this);
        userAccount.saveAttributesPath(access_token, user_id);
        startRequestPathUserInfo();
    }

    @Override
    public void onErrorGetAttributes(String error) {}

    private void startRequestPathUserInfo() {
        if(isInternetPresent) {
            StringRequest request = new StringRequest(Request.Method.GET, Constant.PATH_USER_INFO_URL,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Log.i(Constant.TAG, "Response Path User Info : " + s);
                            try {
                                JSONObject json = new JSONObject(s);
                                String type = json.getString("type");
                                JSONObject json_user = json.getJSONObject("user");
                                String user_name = json_user.getString("name");
                                String email = json_user.getString("email");
                                String id = json_user.getString("id");
                                String user_photo = json_user.getString("photo");
                                JSONObject jsonPhoto = new JSONObject(user_photo);
                                JSONObject sizeMedium = jsonPhoto.getJSONObject("medium");
                                String photoUrl = sizeMedium.getString("url");
                                if((type.equals("OK") || type.equals("CREATED") || type.equals("ACCEPTED"))) {
                                    userAccount = new UserAccount(ActLogin.this);
                                    userAccount.saveLoginStatesSocmed(id, Constant.CODE_PATH, email, user_name, photoUrl);
                                    Log.i(Constant.TAG, user_name + " " + email+ " " + photoUrl);
                                    refreshContent();
                                } else {
                                    Toast.makeText(ActLogin.this, getResources().getString(R.string.label_error_post_comment), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.getMessage();
                            Toast.makeText(ActLogin.this, getResources().getString(R.string.label_error_post_comment), Toast.LENGTH_SHORT).show();
                        }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Global.getInstance(ActLogin.this).getDefaultEditor();
                    String accessToken = Global.getInstance(ActLogin.this).getSharedPreferences(ActLogin.this)
                            .getString(Constant.ATTRIBUTE_PATH_ACCESS_TOKEN, "");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer " + accessToken);
                    return params;
                }
            };
            request.setShouldCache(true);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT_LONG,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(this).getRequestQueue().getCache().invalidate(Constant.PATH_USER_INFO_URL, true);
            Global.getInstance(this).getRequestQueue().getCache().get(Constant.PATH_USER_INFO_URL);
            Global.getInstance(this).addToRequestQueue(request, Constant.JSON_REQUEST);
        }
    }

}
