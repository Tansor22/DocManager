package core.activities.ui.doc_details;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import api.clients.middleware.entity.Document;
import core.activities.R;
import core.shared.Traceable;

public class DocDetailsActivity extends AppCompatActivity implements Traceable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Document doc = getIntent().getParcelableExtra("doc");
        trace("Doc received = %s", doc.toString());
        setContentView(R.layout.activity_doc_details);
    }
}