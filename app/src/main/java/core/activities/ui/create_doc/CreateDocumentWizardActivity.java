package core.activities.ui.create_doc;

import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import core.activities.R;
import core.activities.ui.shared.UserMessageShower;

public class CreateDocumentWizardActivity extends AppCompatActivity implements UserMessageShower {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_document_wizard);
        Spinner docTypeSelectionSpinner = findViewById(R.id.docTypeSelectionSpinner);

        docTypeSelectionSpinner.setOnItemSelectedListener((DocumentTypeSelectedListener) (parent, itemSelected, position, selectedId) -> {
            showUserMessage("Selected position" + position);
            // build form
        });
    }
}