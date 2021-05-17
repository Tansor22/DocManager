package core.activities.ui.doc_details.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import api.clients.middleware.exception.HLFException;
import api.clients.middleware.response.ErrorResponse;
import core.shared.Traceable;
import lombok.Getter;

import java.util.Objects;

@Getter
public class SignDocModel extends ViewModel implements Traceable {
    private final MutableLiveData<Result> result;

    public SignDocModel() {
        result = new MutableLiveData<>();
    }

    public void traceResult() throws HLFException {
        // call fabric functions
        final Result result = Objects.requireNonNull(this.result.getValue());
        trace(result.approved() ? "Approved" : result.rejected() ? "Rejected" : "Unknown status");
        trace("Document: " + result.getCardSwiped());
        if (result.rejected()) {
            trace("Reason " + ((Result.Reject) result).getReason());
        }
        throw HLFException.of(new ErrorResponse());
    }
}
