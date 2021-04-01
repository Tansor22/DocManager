package core.activities;

import android.os.Bundle;
import android.view.Menu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import core.sessions.SessionConstants;
import core.sessions.SessionManager;
import core.shared.Traceable;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements Traceable {

    private AppBarConfiguration appBarConfiguration;

    /*
          Button accessButton = findViewById(R.id.accessButton);
          // init API client
          hlfMiddlewareAPIClient = new HLFMiddlewareAPIClient(getResources());
          resultLabel = findViewById(R.id.resultLabel);
          accessButton.setOnClickListener(e ->
                  new Thread(() -> {
                      final GetDocsResponse response =
                              hlfMiddlewareAPIClient.getDocs(GetDocsRequest.builder()
                                      .orgName("sampleOrg")
                                      .build());
                  *//*    final NewDocResponse response = hlfMiddlewareAPIClient.newDoc(NewDocRequest.builder()
                            .org("sampleOrg")
                            .content("Sample content")
                            .signRequired("DEAN")
                            .signRequired("STUDENT")
                            .signRequired("DOG_OF_THE_STUDENT")
                            .build());*//*
                    runOnUiThread(() -> resultLabel.setText("ID got: " + response.toString()));
                }).start()
        );*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navView);

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.navDocsView, R.id.navCreateDoc)
                .setOpenableLayout(drawer)
                .build();

        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        final NavController navController =
                Objects.requireNonNull(navHostFragment, "No nav host found, check configuration").getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // todo user authentication
        SessionManager.startUserSession(getApplicationContext(), 3_600);
        SessionManager.store(getApplicationContext(), SessionConstants.ORG, "org");
    }


/*
  Uncomment if menu is needed
  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}