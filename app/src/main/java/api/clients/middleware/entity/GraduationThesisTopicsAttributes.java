package api.clients.middleware.entity;

import android.os.Build;
import android.os.Parcel;
import androidx.annotation.RequiresApi;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public class GraduationThesisTopicsAttributes extends Attributes {
    String group;
    String speciality;
    String studyType;
    List<GraduationThesisTopicsStudent> students;
    public static final Creator<GraduationThesisTopicsAttributes> CREATOR = new Creator<GraduationThesisTopicsAttributes>() {
        @Override
        public GraduationThesisTopicsAttributes createFromParcel(Parcel in) {
            return new GraduationThesisTopicsAttributes(in);
        }

        @Override
        public GraduationThesisTopicsAttributes[] newArray(int size) {
            return new GraduationThesisTopicsAttributes[size];
        }
    };

    public GraduationThesisTopicsAttributes(Parcel in) {
        super(in);
        this.group = in.readString();
        this.speciality = in.readString();
        this.studyType = in.readString();
        this.students = new ArrayList<>();
        in.readParcelableList(this.students, GraduationThesisTopicsStudent.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(group);
        dest.writeString(speciality);
        dest.writeString(studyType);
        dest.writeParcelableList(students, flags);
    }
}
