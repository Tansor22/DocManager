package api.clients.middleware.entity;

import android.os.Parcel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public class GraduatedExpellingAttributes extends Attributes {
    Integer course;
    String speciality;
    String studyType;
    String faculty;
    String qualification;
    List<GraduatedExpellingStudent> students;

    protected GraduatedExpellingAttributes(Parcel in) {
        super(in);
        if (in.readByte() == 0) {
            course = null;
        } else {
            course = in.readInt();
        }
        speciality = in.readString();
        studyType = in.readString();
        faculty = in.readString();
        qualification = in.readString();
        students = in.createTypedArrayList(GraduatedExpellingStudent.CREATOR);
    }

    public static final Creator<GraduatedExpellingAttributes> CREATOR = new Creator<GraduatedExpellingAttributes>() {
        @Override
        public GraduatedExpellingAttributes createFromParcel(Parcel in) {
            return new GraduatedExpellingAttributes(in);
        }

        @Override
        public GraduatedExpellingAttributes[] newArray(int size) {
            return new GraduatedExpellingAttributes[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        if (course == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(course);
        }
        dest.writeString(speciality);
        dest.writeString(studyType);
        dest.writeString(faculty);
        dest.writeString(qualification);
        dest.writeTypedList(students);
    }
}
