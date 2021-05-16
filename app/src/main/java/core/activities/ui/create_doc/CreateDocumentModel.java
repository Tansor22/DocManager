package core.activities.ui.create_doc;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import core.shared.Tagged;

public class CreateDocumentModel extends ViewModel implements Tagged {

    //todo stub implementation

    private final MutableLiveData<String> text;

    public CreateDocumentModel() {
        text = new MutableLiveData<>();
        text.setValue("This is " + getTag() + " fragment");
    }

    public LiveData<String> getText() {
        return text;
    }
}