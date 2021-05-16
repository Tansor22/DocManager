package core.activities.ui.view_docs;

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
import core.activities.ui.view_docs.entity.GetDocsResult;
import core.podam.DocumentDataProviderStrategy;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ViewDocumentsModel extends TokenedModel implements Traceable {
    MutableLiveData<GetDocsResult> docsResult = new MutableLiveData<>();

    public ViewDocumentsModel(@NonNull Application application) {
        super(application);
        docsResult.setValue(
                GetDocsResult.builder()
                        .documents(Collections.emptyList())
                        .build()
        );
        Async.execute(this::getDocuments);
    }

    // TODO: delete
    List<Document> getFakeDocuments() {
        PodamFactory pf = new PodamFactoryImpl(new DocumentDataProviderStrategy());
        return IntStream.range(0, 20)
                .mapToObj(ignored -> pf.manufacturePojo(Document.class))
                .collect(Collectors.toList());
    }

    private void getDocuments() {
        final String org = token.getClaim("org").asString();
        GetDocsRequest getDocsRequest = GetDocsRequest.builder()
                .orgName(org)
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