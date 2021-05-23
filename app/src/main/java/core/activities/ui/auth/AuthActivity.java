package core.activities.ui.auth;

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
import com.auth0.android.jwt.JWT;
import core.activities.R;
import core.activities.ui.main.MainActivity;
import core.activities.ui.shared.UserMessageShower;
import core.sessions.SessionConstants;
import core.sessions.SessionManager;
import core.shared.ApplicationContext;

import java.util.Objects;

public abstract class AuthActivity extends AppCompatActivity implements View.OnClickListener, UserMessageShower {

    private AuthViewModel authViewModel;
    private @IdRes
    int layout;
    public static String MEMBER_AVATAR_STORED_KEY = SessionConstants.SESSION_PREFERENCES_PREFIX + "MEMBER_AVATAR_STORED_KEY";

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
        // todo for dev purposes, delete later
        loginEditText.setText("kantor_s@mail.altstu.ru");
        passEditText.setText("newton32");
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
            } else if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
                final JWT jwt = loginResult.getSuccess().getJwt();
                SessionManager.getInstance().startUserSession(ApplicationContext.get(), jwt);
                SessionManager.getInstance().store(ApplicationContext.get(), MEMBER_AVATAR_STORED_KEY, loginResult.getSuccess().getAvatar());
                goToMain(jwt);
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
        // todo set false
        submitButton.setEnabled(true);
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

    private void goToMain(JWT jwt) {
        setResult(Activity.RESULT_OK);
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // must be the first line in code
        ApplicationContext.getInstance().init(getApplicationContext());
        super.onCreate(savedInstanceState);
        // todo for dev purposes, uncomment for making signing in required all the time as token get cleaned
        //SessionManager.getInstance().endUserSession(ApplicationContext.get());
        final JWT token = SessionManager.getInstance().getUserToken(getApplicationContext()).orElse(null);
        if (isSessionActive(token)) {
            goToMain(token);
        } else {
            SessionManager.getInstance().endUserSession(ApplicationContext.get());
            // setup signIn/signUp UI
            setContentView(layout);
            setupUI();
        }
    }

    private boolean isSessionActive(JWT token) {
        return Objects.nonNull(token) && !token.isExpired(0);
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = String.format(getString(R.string.welcome), model.getDisplayName());
        showUserMessage(welcome);
    }

    private void showLoginFailed(@StringRes Integer errorRes) {
        showUserMessage(errorRes);
    }
}