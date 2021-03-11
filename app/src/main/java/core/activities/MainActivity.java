package core.activities;

import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import api.clients.middleware.HLFMiddlewareAPIClient;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

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
                    final String response = hlfMiddlewareAPIClient.accessHLF();
                    runOnUiThread(() -> resultLabel.setText(response));
                }).start()
        );
    }
}