package core.activities.ui.view_docs;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import api.clients.middleware.entity.Document;
import core.shared.Tagged;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
public class ViewDocumentsModel extends ViewModel implements Tagged {
    MutableLiveData<List<Document>> docs;

    public ViewDocumentsModel addDoc(Document doc) {
        if (docs == null) {
            docs = new MutableLiveData<>(new ArrayList<>());
        }
        Objects.requireNonNull(docs.getValue()).add(doc);
        // notify observers
        docs.setValue(docs.getValue());
        return this;
    }
}