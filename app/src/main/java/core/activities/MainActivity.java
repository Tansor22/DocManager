package core.activities;

import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import api.clients.middleware.HLFMiddlewareAPIClient;
import api.clients.middleware.request.GetDocsRequest;
import api.clients.middleware.response.GetDocsResponse;

public class MainActivity extends AppCompatActivity {
    TextView resultLabel;
    HLFMiddlewareAPIClient hlfMiddlewareAPIClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                /*    final NewDocResponse response = hlfMiddlewareAPIClient.newDoc(NewDocRequest.builder()
                            .org("sampleOrg")
                            .content("Sample content")
                            .signRequired("DEAN")
                            .signRequired("STUDENT")
                            .signRequired("DOG_OF_THE_STUDENT")
                            .build());*/
                    runOnUiThread(() -> resultLabel.setText("ID got: " + response.toString()));
                }).start()
        );
    }
}