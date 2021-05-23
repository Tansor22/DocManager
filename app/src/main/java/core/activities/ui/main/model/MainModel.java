package core.activities.ui.main.model;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import api.clients.middleware.HLFMiddlewareAPIClient;
import api.clients.middleware.entity.Document;
import api.clients.middleware.exception.HLFException;
import api.clients.middleware.request.GetDocsRequest;
import core.activities.R;
import core.activities.ui.shared.Async;
import core.activities.ui.shared.TokenedModel;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class MainModel extends TokenedModel implements Traceable {
    MutableLiveData<GetDocsResult> docsResult = new MutableLiveData<>();

    public MainModel(@NonNull Application application) {
        super(application);
        docsResult.setValue(
                GetDocsResult.builder()
                        .documents(Collections.emptyList())
                        .build()
        );
        Async.execute(this::getDocuments);
    }

    public void getDocuments() {
        final String group = token.getClaim("group").asString();
        GetDocsRequest getDocsRequest = GetDocsRequest.builder()
                .group(group)
                .build();
        try {
            final List<Document> documents = HLFMiddlewareAPIClient.getInstance().getDocs(getDocsRequest, token.toString()).getDocuments();
            // only postValue in background!
            docsResult.postValue(
                    GetDocsResult.builder()
                            .documents(documents)
                            .build()
            );
        } catch (HLFException e) {
            docsResult.postValue(
                    GetDocsResult.builder()
                            .error(R.string.unexpected_error)
                            .build()
            );
        }
    }
}