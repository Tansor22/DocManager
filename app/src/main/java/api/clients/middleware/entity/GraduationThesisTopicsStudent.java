package api.clients.middleware.entity;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class GraduationThesisTopicsStudent implements Parcelable {
    Student commonInfo;
    String topic;
    String academicAdvisorFullName;

    protected GraduationThesisTopicsStudent(Parcel in) {
        commonInfo = in.readParcelable(Student.class.getClassLoader());
        topic = in.readString();
        academicAdvisorFullName = in.readString();
    }

    public static final Creator<GraduationThesisTopicsStudent> CREATOR = new Creator<GraduationThesisTopicsStudent>() {
        @Override
        public GraduationThesisTopicsStudent createFromParcel(Parcel in) {
            return new GraduationThesisTopicsStudent(in);
        }

        @Override
        public GraduationThesisTopicsStudent[] newArray(int size) {
            return new GraduationThesisTopicsStudent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(commonInfo, flags);
        dest.writeString(topic);
        dest.writeString(academicAdvisorFullName);
    }
}
