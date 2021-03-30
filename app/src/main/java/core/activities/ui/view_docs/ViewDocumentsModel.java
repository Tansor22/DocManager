package core.activities.ui.view_docs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import core.shared.Tagged;

public class ViewDocumentsModel extends ViewModel implements Tagged {

    private MutableLiveData<String> text;

    public ViewDocumentsModel() {
        text = new MutableLiveData<>();
        text.setValue("This is " + getTag() + " fragment");
    }

    public LiveData<String> getText() {
        return text;
    }
}