package core.activities.ui.view_docs;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import api.clients.middleware.entity.Document;
import core.activities.R;
import core.activities.ui.shared.UserMessageShower;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocViewActivity extends AppCompatActivity implements Traceable, UserMessageShower {
    TextView titleTextView;
    TextView dateTextView;
    TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_view);
        titleTextView = findViewById(R.id.item_title);
        dateTextView = findViewById(R.id.item_date);
        statusTextView = findViewById(R.id.item_status);
        final Document doc = getIntent().getParcelableExtra("doc");
        updateUi(doc);
    }

    private void updateUi(Document doc) {
        titleTextView.setText(doc.getTitle());
        dateTextView.setText(doc.getDate());
        statusTextView.setText(doc.getStatus());
    }
}