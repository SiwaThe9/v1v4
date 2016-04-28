package id.co.viva.news.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

import id.co.viva.news.app.R;
import id.co.viva.news.app.interfaces.OnCompleteListener;
import id.co.viva.news.app.services.UserAccount;
import id.co.viva.news.app.services.Validation;

/**
 * Created by reza on 12/12/14.
 */
public class ActForgotPassword extends ActionBarActivity implements View.OnClickListener, OnCompleteListener {

    private TextView mSuccessEmail;
    private EditText mInputEmail;
    private ActionProcessButton mBtnSendEmail;
    private UserAccount userAccount;
    private Validation validation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_forgot_password);
        getPrefActionBar();
        defineView();
    }

    private void defineView() {
        validation = new Validation();
        mSuccessEmail = (TextView)findViewById(R.id.text_success_send_email);
        mInputEmail = (EditText)findViewById(R.id.et_forgot_password);
        mBtnSendEmail = (ActionProcessButton)findViewById(R.id.btn_send_forgot_password);
        mBtnSendEmail.setMode(ActionProcessButton.Mode.ENDLESS);
        mBtnSendEmail.setOnClickListener(this);
        mSuccessEmail.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_send_forgot_password) {
            String email = mInputEmail.getText().toString();
            if(validation.isEmailValid(email)) {
                disableViews();
                mBtnSendEmail.setProgress(1);
                userAccount = new UserAccount(this, email);
                userAccount.sendForgotPassword();
            } else {
                mInputEmail.setError(getResources().getString(R.string.label_validation_email));
            }
        }
    }

    private void disableViews() {
        mInputEmail.setEnabled(false);
        mBtnSendEmail.setEnabled(false);
    }

    private void enableViews() {
        mInputEmail.setEnabled(true);
        mBtnSendEmail.setEnabled(true);
    }

    private void refreshContent() {
        finish();
        Intent intent = new Intent(this, ActLanding.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

    private void getPrefActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Forgot Password");
    }

    @Override
    public void onComplete(String message) {
        mBtnSendEmail.setProgress(100);
        mInputEmail.setText(null);
        mSuccessEmail.setVisibility(View.VISIBLE);
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(String message) {
        mBtnSendEmail.setProgress(0);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        enableViews();
        mInputEmail.setText(null);
    }

    @Override
    public void onError(String message) {
        mBtnSendEmail.setProgress(0);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        enableViews();
        mInputEmail.setText(null);
    }

}
