package core.activities.ui.auth;

import android.content.Intent;
import android.view.View;
import core.activities.R;

public class SignInActivity extends AuthActivity {
    {
        init(R.layout.activity_sign_in);
    }

    @Override
    // todo rename to onSwitchFormButtonClick
    public void onClick(View v) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
