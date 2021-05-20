package api.clients.middleware.entity;

import android.os.Parcel;
import android.os.Parcelable;
import core.activities.R;
import core.shared.ApplicationContext;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Data
@ToString
@NoArgsConstructor
public class Document implements Parcelable {
    String documentId;
    String title;
    String owner;
    String group;
    String type;
    String date;
    String content;
    String status;
    List<Change> changes;
    List<String> signsRequired;
    List<String> signedBy;
    static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

    protected Document(Parcel in) {
        documentId = in.readString();
        title = in.readString();
        owner = in.readString();
        group = in.readString();
        type = in.readString();
        date = in.readString();
        content = in.readString();
        status = in.readString();
        signsRequired = in.createStringArrayList();
        signedBy = in.createStringArrayList();
    }

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(title);
        dest.writeString(owner);
        dest.writeString(group);
        dest.writeString(type);
        dest.writeString(date);
        dest.writeString(content);
        dest.writeString(status);
        dest.writeStringList(signsRequired);
        dest.writeStringList(signedBy);
    }

    public String getStatusForUser() {
        int resId = R.string.doc_status_unknown;
        switch (status) {
            case "PROCESSING":
                resId = R.string.doc_status_processing;
                break;
            case "APPROVED":
                resId = R.string.doc_status_approved;
                break;
            case "CLOSED":
                resId = R.string.doc_status_closed;
                break;
            case "REJECTED":
                resId = R.string.doc_status_rejected;
                break;
        }
        return ApplicationContext.get().getString(resId);
    }

    public String getDateForUser() {
        final Calendar calendar = DatatypeConverter.parseDateTime(date);
        return DATE_FORMATTER.format(calendar.getTime());
    }
}
