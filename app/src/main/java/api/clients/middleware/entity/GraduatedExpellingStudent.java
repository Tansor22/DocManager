package api.clients.middleware.entity;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class GraduatedExpellingStudent implements Parcelable {
    Student commonInfo;
    Boolean hasHonoursDegree;
    String examDate;

    protected GraduatedExpellingStudent(Parcel in) {
        commonInfo = in.readParcelable(Student.class.getClassLoader());
        byte tmpHasHonoursDegree = in.readByte();
        hasHonoursDegree = tmpHasHonoursDegree == 0 ? null : tmpHasHonoursDegree == 1;
        examDate = in.readString();
    }

    public static final Creator<GraduatedExpellingStudent> CREATOR = new Creator<GraduatedExpellingStudent>() {
        @Override
        public GraduatedExpellingStudent createFromParcel(Parcel in) {
            return new GraduatedExpellingStudent(in);
        }

        @Override
        public GraduatedExpellingStudent[] newArray(int size) {
            return new GraduatedExpellingStudent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(commonInfo, flags);
        dest.writeByte((byte) (hasHonoursDegree == null ? 0 : hasHonoursDegree ? 1 : 2));
        dest.writeString(examDate);
    }
}
