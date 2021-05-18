package core.activities.ui.view_docs.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import core.shared.Traceable;
import lombok.Getter;

public class ViewDocumentsModel extends ViewModel implements Traceable {

    @Getter
    private final MutableLiveData<FilterConfig> filterConfig;

    public ViewDocumentsModel() {
        filterConfig = new MutableLiveData<>();
    }
}
