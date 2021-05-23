package core.activities.ui.docs_to_sign.model;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import api.clients.middleware.HLFMiddlewareAPIClient;
import api.clients.middleware.exception.HLFException;
import api.clients.middleware.request.ChangeDocRequest;
import api.clients.middleware.response.ErrorResponse;
import core.activities.ui.shared.Async;
import core.activities.ui.shared.TokenedModel;
import core.shared.Traceable;
import lombok.Getter;

import java.util.Objects;

@Getter
public class SignDocModel extends TokenedModel implements Traceable {
    private final MutableLiveData<Result> result;

    public SignDocModel(@NonNull Application application) {
        super(application);
        result = new MutableLiveData<>();
    }

    public void processDoc() throws HLFException {
        final Result result = Objects.requireNonNull(this.result.getValue());
        Async.execute(() -> {
            try {
                HLFMiddlewareAPIClient.getInstance().changeDoc(ChangeDocRequest.builder()
                        .documentId(result.getCardSwiped().getDocument().getDocumentId())
                        .member(token.getClaim("member").asString())
                        .type(result.approved() ? "APPROVE" : result.rejected() ? "REJECT" : "UNKNOWN")
                        .details(result.rejected() ? ((Result.Reject) result).getReason() : null)
                        .build(), token.toString());
            } catch (HLFException e) {
                e.printStackTrace();
            }
        });
    }
}
