package core.activities.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.auth0.android.jwt.JWT;
import com.google.android.material.navigation.NavigationView;
import core.activities.R;
import core.activities.ui.main.model.MainModel;
import core.sessions.SessionManager;
import core.shared.ApplicationContext;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainActivity extends AppCompatActivity implements Traceable {
    AppBarConfiguration appBarConfiguration;
    @Getter
    MainModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // queries docs async
        model = new ViewModelProvider(this).get(MainModel.class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navView);

        SessionManager.getInstance().getUserToken(ApplicationContext.get())
                .ifPresent(token -> {
                    trace("User token got = %s", token.toString());
                    // member
                    TextView userTextView = navigationView.getHeaderView(0).findViewById(R.id.memberTextView);
                    userTextView.setText(token.getClaim("member").asString());
                    // user email
                    userTextView = navigationView.getHeaderView(0).findViewById(R.id.memberEmailTextView);
                    userTextView.setText(token.getClaim("email").asString());
                    // todo avatar
                    final ImageView memberAvatar = navigationView.getHeaderView(0).findViewById(R.id.memberAvatar);
                });

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.navDocsToSign, R.id.navDocsView, R.id.navCreateDoc)
                .setOpenableLayout(drawer)
                .build();
        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        final NavController navController =
                Objects.requireNonNull(navHostFragment, "No nav host found, check configuration").getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}