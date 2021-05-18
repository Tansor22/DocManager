package core.activities.ui.view_docs;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import api.clients.middleware.entity.Document;
import core.activities.R;
import core.activities.ui.shared.UserMessageShower;
import core.shared.Traceable;

import java.util.List;

// todo singel static doc with details view
public class DocViewActivity extends AppCompatActivity implements Traceable, UserMessageShower {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final List<Document> docs = getIntent().getParcelableArrayListExtra("docs");
        final int position = getIntent().getIntExtra("position", 0);
        //trace("Docs received = %s", docs.toString());
        //trace("Position received = %s", position);
        setContentView(R.layout.activity_doc_view);
    }
}