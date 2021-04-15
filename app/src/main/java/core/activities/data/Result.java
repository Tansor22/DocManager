package core.activities.data;

import core.activities.data.model.LoggedInUser;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
public class Result {
    private Result() {
    }

    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success success = (Result.Success) this;
            return "Success[data=" + success.getUser().toString() + "]";
        } else if (this instanceof Result.Error) {
            Result.Error error = (Result.Error) this;
            return "Error[exception=" + error.getError().toString() + "]";
        }
        return "";
    }
    @EqualsAndHashCode(callSuper = true)
    @Value
    public static class Success extends Result {
        LoggedInUser user;
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    public static class Error extends Result {
        Exception error;
    }
}