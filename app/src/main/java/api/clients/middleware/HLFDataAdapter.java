package api.clients.middleware;

import android.content.Context;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import core.activities.R;
import core.shared.ApplicationContext;

import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

public class HLFDataAdapter {
    private static final BiMap<String, String> DOC_TYPES;
    private static final BiMap<String, String> DOC_STATUSES;
    private static final BiMap<String, Boolean> ON_GOVERNMENT_PAY;
    private static final BiMap<String, Boolean> HONOURS_DEGREE;
    private static final BiMap<String, String> STUDY_TYPES;

    static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

    static {
        final Context ctx = ApplicationContext.get();
        DOC_TYPES = HashBiMap.create(4);
        DOC_TYPES.put("General", ctx.getString(R.string.doc_type_general));
        DOC_TYPES.put("GraduatedExpelling", ctx.getString(R.string.doc_type_graduated_expelling));
        DOC_TYPES.put("PracticePermission", ctx.getString(R.string.doc_type_practice_permission));
        DOC_TYPES.put("GraduationThesisTopics", ctx.getString(R.string.doc_type_graduation_thesis_topics));


        DOC_STATUSES = HashBiMap.create(4);
        DOC_STATUSES.put("PROCESSING", ctx.getString(R.string.doc_status_processing));
        DOC_STATUSES.put("APPROVED", ctx.getString(R.string.doc_status_approved));
        DOC_STATUSES.put("CLOSED", ctx.getString(R.string.doc_status_closed));
        DOC_STATUSES.put("REJECTED", ctx.getString(R.string.doc_status_rejected));

        STUDY_TYPES = HashBiMap.create(3);
        STUDY_TYPES.put("FULL_TIME", ctx.getString(R.string.study_type_full_time));
        STUDY_TYPES.put("PART_TIME", ctx.getString(R.string.study_type_part_time));
        STUDY_TYPES.put("SELF_STUDY", ctx.getString(R.string.study_type_self_study));

        ON_GOVERNMENT_PAY = HashBiMap.create(2);
        ON_GOVERNMENT_PAY.put(ctx.getString(R.string.on_government_pay_yes), Boolean.TRUE);
        ON_GOVERNMENT_PAY.put(ctx.getString(R.string.on_government_pay_no), Boolean.FALSE);

        HONOURS_DEGREE = HashBiMap.create(2);
        HONOURS_DEGREE.put(ctx.getString(R.string.yes), Boolean.TRUE);
        HONOURS_DEGREE.put(ctx.getString(R.string.no), Boolean.FALSE);

    }

    private static <T> T throwIfNull(T ref) {
        return Optional.ofNullable(ref).orElseThrow(() -> new IllegalArgumentException(ref + " mustn't be null"));
    }

    public static String parseDate(String date) {
        final Calendar calendar = DatatypeConverter.parseDateTime(date);
        return DATE_FORMATTER.format(calendar.getTime());
    }

    public static String toUserStatus(String hlfStatus) {
        return throwIfNull(DOC_STATUSES.get(hlfStatus));
    }

    public static String fromUserStatus(String userStatus) {
        return throwIfNull(DOC_STATUSES.inverse().get(userStatus));
    }

    public static String fromUserDocumentType(String userDocType) {
        return throwIfNull(DOC_TYPES.inverse().get(userDocType));
    }

    public static String toUserDocumentType(String hlfDocType) {
        return throwIfNull(DOC_TYPES.get(hlfDocType));
    }

    public static String fromUserStudyType(String userStudyType) {
        return throwIfNull(STUDY_TYPES.inverse().get(userStudyType));
    }

    public static String toUserStudyType(String hlfStudy) {
        return throwIfNull(STUDY_TYPES.get(hlfStudy));
    }

    public static Boolean fromUserOnGovernmentPay(String userOnGovernmentPay) {
        return throwIfNull(ON_GOVERNMENT_PAY.get(userOnGovernmentPay));
    }

    public static String toUserOnGovernmentPay(Boolean hlfOnGovernmentPay) {
        return throwIfNull(ON_GOVERNMENT_PAY.inverse().get(hlfOnGovernmentPay));
    }

    public static Boolean fromUserHonoursDegree(String userOnGovernmentPay) {
        return throwIfNull(HONOURS_DEGREE.get(userOnGovernmentPay));
    }

    public static String toUserHonoursDegree(Boolean hlfOnGovernmentPay) {
        return throwIfNull(HONOURS_DEGREE.inverse().get(hlfOnGovernmentPay));
    }
}
