package api.clients.middleware.entity;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class Student implements Parcelable {
    String fullName;
    String nationality;
    String group;
    Boolean onGovernmentPay;

    protected Student(Parcel in) {
        fullName = in.readString();
        nationality = in.readString();
        group = in.readString();
        byte tmpOnGovernmentPay = in.readByte();
        onGovernmentPay = tmpOnGovernmentPay == 0 ? null : tmpOnGovernmentPay == 1;
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(nationality);
        dest.writeString(group);
        dest.writeByte((byte) (onGovernmentPay == null ? 0 : onGovernmentPay ? 1 : 2));
    }
}
