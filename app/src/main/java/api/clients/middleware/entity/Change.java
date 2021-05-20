package api.clients.middleware.entity;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class Change implements Parcelable {
    String member;
    String type;
    String date;
    String details;

    protected Change(Parcel in) {
        member = in.readString();
        type = in.readString();
        date = in.readString();
        details = in.readString();
    }

    public static final Creator<Change> CREATOR = new Creator<Change>() {
        @Override
        public Change createFromParcel(Parcel in) {
            return new Change(in);
        }

        @Override
        public Change[] newArray(int size) {
            return new Change[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(member);
        dest.writeString(type);
        dest.writeString(date);
        dest.writeString(details);
    }
}
