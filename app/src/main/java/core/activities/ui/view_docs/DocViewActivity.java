package core.activities.ui.view_docs;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import api.clients.middleware.entity.Document;
import core.activities.R;
import core.activities.ui.shared.UserMessageShower;
import core.shared.ApplicationContext;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true, prefix = "_")
public class DocViewActivity extends AppCompatActivity implements Traceable, UserMessageShower {
    TextView _title, _date, _status, _reason, _owner, _content;
    LinearLayout _signsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_view);
        _title = findViewById(R.id.item_title);
        _date = findViewById(R.id.item_date);
        _owner = findViewById(R.id.item_owner);
        _status = findViewById(R.id.item_status);
        _reason = findViewById(R.id.item_reason);
        _content = findViewById(R.id.item_content);
        _signsContainer = findViewById(R.id.signsContainer);
        final Document doc = getIntent().getParcelableExtra("doc");
        updateUi(doc);
    }

    private void updateUi(Document doc) {
        _title.setText(doc.getTitle());
        _owner.setText(ApplicationContext.get().getText(R.string.doc_owner_prefix) + " "
                + doc.getOwner());
        _date.setText(ApplicationContext.get().getString(R.string.doc_date_prefix) + " "
                + doc.getDateForUser());
        final String reason = doc.getLastChange().getDetails();
        if ("REJECTED".equals(doc.getStatus()) && StringUtils.isNotEmpty(reason)) {
            _reason.setVisibility(View.VISIBLE);
            _reason.setText(ApplicationContext.get().getString(R.string.doc_reject_reason) + " "
                    + reason);
        } else {
            _reason.setVisibility(View.GONE);
        }
        _status.setText(ApplicationContext.get().getString(R.string.doc_status_prefix) + " "
                + doc.getStatusForUser());
        _content.setText(doc.getAttributes().getContent());
        // signs
        for (int i = 0; i < doc.getSignsRequired().size(); i++) {
            String sign = (String) doc.getSignsRequired().get(i);
            TextView signTextView = new TextView(this);
            signTextView.setText(sign);
            signTextView.setTextColor(ContextCompat.getColor(ApplicationContext.get(), R.color.colorFullBlack));
            signTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            if (doc.getSignedBy().contains(sign)) {
                // green check
                signTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_approve_small, 0, 0, 0);
            } else {
                // red cross
                signTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reject_small, 0, 0, 0);
            }
            _signsContainer.addView(signTextView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
    }
}
