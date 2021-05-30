package api.clients.middleware.entity;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class PracticePermissionStudent implements Parcelable {
    Student commonInfo;
    String practiceLocation;
    String headFullName;

    protected PracticePermissionStudent(Parcel in) {
        commonInfo = in.readParcelable(Student.class.getClassLoader());
        practiceLocation = in.readString();
        headFullName = in.readString();
    }

    public static final Creator<PracticePermissionStudent> CREATOR = new Creator<PracticePermissionStudent>() {
        @Override
        public PracticePermissionStudent createFromParcel(Parcel in) {
            return new PracticePermissionStudent(in);
        }

        @Override
        public PracticePermissionStudent[] newArray(int size) {
            return new PracticePermissionStudent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(commonInfo, flags);
        dest.writeString(practiceLocation);
        dest.writeString(headFullName);
    }
}
