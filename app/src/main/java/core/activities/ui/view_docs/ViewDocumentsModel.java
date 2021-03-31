package core.activities.ui.view_docs;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import api.clients.middleware.HLFMiddlewareAPIClient;
import api.clients.middleware.entity.Document;
import core.podam.DocumentDataProviderStrategy;
import core.shared.Tagged;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ViewDocumentsModel extends AndroidViewModel implements Tagged {
    MutableLiveData<List<Document>> docs;
    HLFMiddlewareAPIClient hlfClient;

    public ViewDocumentsModel(@NonNull Application application) {
        super(application);
        hlfClient = new HLFMiddlewareAPIClient(application.getResources());
       /* docs = new MutableLiveData<>(hlfClient.getDocs(GetDocsRequest.builder()
                .orgName("someOrg")
                .build())
                .getDocuments()); */
        docs = new MutableLiveData<>(
                populateModel()
        );
    }

    // TODO: delete
    List<Document> populateModel() {
        PodamFactory pf = new PodamFactoryImpl(new DocumentDataProviderStrategy());
        return IntStream.range(0, 20)
                .mapToObj(ignored -> pf.manufacturePojo(Document.class))
                .collect(Collectors.toList());
    }

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