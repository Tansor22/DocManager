package api.clients.middleware.entity;

import android.os.Parcel;

public class GraduationThesisTopicsAttributes extends Attributes{
    String group;
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
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(group);
    }
}
