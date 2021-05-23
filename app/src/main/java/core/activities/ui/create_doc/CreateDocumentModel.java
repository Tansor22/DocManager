package core.activities.ui.create_doc;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import core.shared.Tagged;
import lombok.Getter;

import java.util.List;

public class CreateDocumentModel extends ViewModel implements Tagged {
    @Getter
    private final MutableLiveData<List<String>> docTypes;

    public CreateDocumentModel() {
        docTypes = new MutableLiveData<>();
    }
}