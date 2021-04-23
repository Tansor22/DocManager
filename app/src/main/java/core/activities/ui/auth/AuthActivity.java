package core.activities.ui.auth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import core.activities.R;
import core.activities.ui.main.MainActivity;
import core.shared.ApplicationContext;

public abstract class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private AuthViewModel authViewModel;
    private @IdRes
    int layout;

    protected final void init(@IdRes int layout) {
        this.layout = layout;
    }

    protected void setupUI() {
        // activity's controls
        final EditText passEditText = findViewById(R.id.passEditText);
        final EditText loginEditText = findViewById(R.id.loginEditText);
        final Button submitButton = findViewById(R.id.submitAuthButton);
        final ProgressBar loadingProgressBar = findViewById(R.id.authProgressBar);
        final ImageView closeImageView = findViewById(R.id.closeImageView);
        final TextView changeFormTextView = findViewById(R.id.changeFormTextView);

        authViewModel =
                new ViewModelProvider(this, new AuthViewModelFactory()).get(AuthViewModel.class);
        authViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            submitButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                loginEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        authViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
                // todo move to another branch
                Intent intent = new Intent(this, MainActivity.class);
                finish();
                startActivity(intent);
            } else if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());

                setResult(Activity.RESULT_OK);

                // todo start main activity
                //Complete and destroy login activity once successful
                finish();
            }
        });
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                authViewModel.loginDataChanged(loginEditText.getText().toString(),
                        passEditText.getText().toString());
            }
        };
        loginEditText.addTextChangedListener(afterTextChangedListener);
        passEditText.addTextChangedListener(afterTextChangedListener);

        passEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String login = loginEditText.getText().toString();
                String pass = passEditText.getText().toString();
                auth(login, pass);
            }
            return false;
        });
        submitButton.setEnabled(false);
        submitButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            String login = loginEditText.getText().toString();
            String pass = passEditText.getText().toString();
            auth(login, pass);

        });
        changeFormTextView.setOnClickListener(this);
        closeImageView.setOnClickListener(self -> finish());
    }

    private void auth(String login, String pass) {
        if (layout == R.layout.activity_sign_up) {
            authViewModel.signUp(login, pass);
        } else if (layout == R.layout.activity_sign_in) {
            authViewModel.signIn(login, pass);
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // move to setupUI
        setContentView(layout);
        // mist be the first line in code
        ApplicationContext.getInstance().init(getApplicationContext());
        setupUI();
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = String.format(getString(R.string.welcome), model.getDisplayName());
        Toast.makeText(ApplicationContext.get(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(ApplicationContext.get(), errorString, Toast.LENGTH_SHORT).show();
    }
}