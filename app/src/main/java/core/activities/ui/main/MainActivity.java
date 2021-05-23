package core.activities.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import core.activities.R;
import core.activities.ui.auth.SignInActivity;
import core.activities.ui.main.model.MainModel;
import core.activities.ui.shared.UserMessageShower;
import core.activities.ui.shared.ui.MultiSelectionSpinner;
import core.sessions.SessionManager;
import core.shared.ApplicationContext;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainActivity extends AppCompatActivity implements Traceable, UserMessageShower {
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
        // NavigationUI.setupWithNavController(navigationView, navController);
        // not complete impl of NavigationUI.setupWithNavController
        navigationView.setNavigationItemSelectedListener(item -> {
            // special behavior
            if (item.getItemId() == R.id.navLogout) {
                buildAndShowLogoutDialog();
            }
            // ordinary behavior
            NavigationUI.onNavDestinationSelected(item, navController);

            //close navigation drawer
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void buildAndShowLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.logout_dialog_title);
        builder.setPositiveButton(R.string.answer_yes, (dialog, which) -> {
            SessionManager.getInstance().getUserToken(ApplicationContext.get())
                    .ifPresent(token ->
                            showUserMessage(
                                    String.format(getString(R.string.logout_hint), token.getClaim("member").asString())
                            )
                    );
            SessionManager.getInstance().endUserSession(ApplicationContext.get());
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton(R.string.answer_no, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}