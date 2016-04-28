package id.co.viva.news.app.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

import id.co.viva.news.app.R;
import id.co.viva.news.app.interfaces.OnCompleteListener;
import id.co.viva.news.app.services.UserAccount;
import id.co.viva.news.app.services.Validation;

/**
 * Created by reza on 02/12/14.
 */
public class ActRegistration extends ActionBarActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener, OnCompleteListener {

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etRetypePassword;
    private ActionProcessButton btnRegistration;
    private String genderSelected;
    private Validation validation;
    private UserAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);
        validation = new Validation();
        getPrefActionBar();
        defineViews();
    }

    private void defineViews() {
        etUsername = (EditText) findViewById(R.id.form_regist_username);
        etEmail = (EditText) findViewById(R.id.form_regist_email);
        etPassword = (EditText) findViewById(R.id.form_regist_password);
        etRetypePassword = (EditText) findViewById(R.id.form_regist_password_retype);
        btnRegistration = (ActionProcessButton) findViewById(R.id.btn_daftar);
        btnRegistration.setOnClickListener(this);
        btnRegistration.setMode(ActionProcessButton.Mode.ENDLESS);
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
        getSupportActionBar().setTitle("Registrasi");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_daftar) {
            String username = etUsername.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String retype_password = etRetypePassword.getText().toString();
            //Some Validation
            if (!validation.isLengthValid(username)) {
                etUsername.setError(getResources().getString(R.string.label_registrasi_username_character_length));
            } else if (!validation.isEmailValid(email)) {
                etEmail.setError(getResources().getString(R.string.label_validation_email));
            } else if (!validation.isLengthValid(password)) {
                etPassword.setError(getResources().getString(R.string.label_validation_password_length));
            } else if (!retype_password.equals(password)) {
                etRetypePassword.setError(getResources().getString(R.string.label_validation_password_length_retype));
            } else {
                userAccount = new UserAccount(username, email, password, this, ActRegistration.this);
                disableView();
                btnRegistration.setProgress(1);
                userAccount.signUp();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        genderSelected = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onComplete(String message) {
        btnRegistration.setProgress(100);
        Toast.makeText(this,
                message, Toast.LENGTH_SHORT).show();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                finish();
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
        btnRegistration.setProgress(0);
        enableView();
        Toast.makeText(this,
                message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String message) {
        btnRegistration.setProgress(0);
        enableView();
        Toast.makeText(this,
                message, Toast.LENGTH_SHORT).show();
    }

    private void disableView() {
        etUsername.setEnabled(false);
        etEmail.setEnabled(false);
        etPassword.setEnabled(false);
        btnRegistration.setEnabled(false);
    }

    private void enableView() {
        etUsername.setEnabled(true);
        etEmail.setEnabled(true);
        etPassword.setEnabled(true);
        btnRegistration.setEnabled(true);
    }

}
