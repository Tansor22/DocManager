package api.clients.middleware.entity;

import android.os.Parcel;
import android.os.Parcelable;
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
    String description;
    String org;
    String date;
    String content;
    String status;
    List<String> signsRequired;
    List<String> signedBy;

    protected Document(Parcel in) {
        documentId = in.readString();
        title = in.readString();
        description = in.readString();
        org = in.readString();
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
        dest.writeString(description);
        dest.writeString(org);
        dest.writeString(date);
        dest.writeString(content);
        dest.writeString(status);
        dest.writeStringList(signsRequired);
        dest.writeStringList(signedBy);
    }
}
