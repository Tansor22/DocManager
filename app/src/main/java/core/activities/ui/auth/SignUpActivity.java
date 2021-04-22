package core.activities.ui.auth;

import android.content.Intent;
import android.view.View;
import core.activities.R;

public class SignUpActivity extends AuthActivity {
    @Override
    protected int getLayout() {
        return R.layout.activity_sign_up;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}
