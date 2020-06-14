package org.hugoandrade.worldcup2018.predictor.admin.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.hugoandrade.worldcup2018.predictor.admin.MVP;
import org.hugoandrade.worldcup2018.predictor.admin.R;
import org.hugoandrade.worldcup2018.predictor.data.LoginData;
import org.hugoandrade.worldcup2018.predictor.admin.presenter.LoginPresenter;
import org.hugoandrade.worldcup2018.predictor.utils.UIUtils;
import org.hugoandrade.worldcup2018.predictor.admin.view.main.MainActivity;
import org.hugoandrade.worldcup2018.predictor.utils.SharedPreferencesUtils;
import org.hugoandrade.worldcup2018.predictor.view.ActivityBase;

public class LoginActivity extends ActivityBase<MVP.RequiredLoginViewOps,
                                                MVP.ProvidedLoginPresenterOps,
                                                LoginPresenter>
        implements MVP.RequiredLoginViewOps {

    // Views
    private RelativeLayout btLogin;
    private EditText etUsername;
    private EditText etPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initializeViews();

        enableUI();

        super.onCreate(LoginPresenter.class, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        populateInputViews();
    }

    private void populateInputViews() {
        LoginData loginData = SharedPreferencesUtils.getLoginData(this);

        etUsername.setText(loginData.getUsername());
        etPassword.setText(loginData.getPassword());
    }

    @Override
    public void disableUI() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        btLogin.setBackgroundResource(R.drawable.btn_default_pressed);
        etUsername.setEnabled(false);
        etPassword.setEnabled(false);
        btLogin.setEnabled(false);
    }

    @Override
    public void enableUI() {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        btLogin.setBackgroundResource(android.R.drawable.btn_default);
        etUsername.setEnabled(true);
        etPassword.setEnabled(true);
        btLogin.setEnabled(true);
    }

    @Override
    public void successfulLogin() {
        startActivity(MainActivity.makeIntent(getActivityContext()));
        finish();
    }

    @Override
    protected void onDestroy() {
        getPresenter().onDestroy(isChangingConfigurations());

        super.onDestroy();
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.et_username_login);
        etPassword = findViewById(R.id.et_password_login);
        btLogin = findViewById(R.id.bt_login);
        progressBar = findViewById(R.id.progressBar_login);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().login(
                        etUsername.getText().toString(),
                        etPassword.getText().toString());
            }
        });
    }

    public void showSnackBar(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }

    @Override
    public void reportMessage(String message) {
        showSnackBar(message);
    }
}
