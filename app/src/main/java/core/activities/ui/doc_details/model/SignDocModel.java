package core.activities.ui.doc_details.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import core.shared.Traceable;
import lombok.Getter;

@Getter
public class SignDocModel extends ViewModel implements Traceable {
    private final MutableLiveData<SignDocResult> result;

    public SignDocModel() {
        result = new MutableLiveData<>();
    }
}
