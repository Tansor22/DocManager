package api.clients.middleware.entity;

import android.os.Parcel;
import android.os.Parcelable;
import api.clients.middleware.HLFDataAdapter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

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
        return HLFDataAdapter.toUserStatus(status);
    }

    public String getDateForUser() {
        return HLFDataAdapter.parseDate(date);
    }

    public String getCurrentSign() {
        if (signedBy == null || signedBy.isEmpty()) {
            return signsRequired.get(0);
        }
        String lastSigned = signedBy.get(signedBy.size() - 1);
        if (!lastSigned.equals(signsRequired.get(signsRequired.size() - 1))) {
            // last signed is not last signed required (doc is not signed)
            for (int i = 0; i < signsRequired.size() - 1; i++) {
                if (signsRequired.get(i).equals(lastSigned)) {
                    return signsRequired.get(i + 1);
                }
            }
        }
        return signsRequired.get(signsRequired.size() - 1);
    }
}
