package api.clients.middleware.entity;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public class PracticePermissionAttributes extends Attributes{
    String practiceType;
    String studyType;
    String speciality;
    Integer course;
    String dateFrom;
    String dateTo;
    List<PracticePermissionStudent> students;

    protected PracticePermissionAttributes(Parcel in) {
        super(in);
        practiceType = in.readString();
        studyType = in.readString();
        speciality = in.readString();
        if (in.readByte() == 0) {
            course = null;
        } else {
            course = in.readInt();
        }
        dateFrom = in.readString();
        dateTo = in.readString();
        students = in.createTypedArrayList(PracticePermissionStudent.CREATOR);
    }

    public static final Creator<PracticePermissionAttributes> CREATOR = new Creator<PracticePermissionAttributes>() {
        @Override
        public PracticePermissionAttributes createFromParcel(Parcel in) {
            return new PracticePermissionAttributes(in);
        }

        @Override
        public PracticePermissionAttributes[] newArray(int size) {
            return new PracticePermissionAttributes[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(practiceType);
        dest.writeString(studyType);
        dest.writeString(speciality);
        if (course == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(course);
        }
        dest.writeString(dateFrom);
        dest.writeString(dateTo);
        dest.writeTypedList(students);
    }
}
