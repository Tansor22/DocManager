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
    private static final BiMap<String, String> DOC_TYPE;
    private static final BiMap<String, String> DOC_STATUS;

    static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

    static {
        final Context ctx = ApplicationContext.get();
        DOC_TYPE = HashBiMap.create(4);
        DOC_TYPE.put("General", ctx.getString(R.string.doc_type_general));
        DOC_TYPE.put("GraduatedExpelling", ctx.getString(R.string.doc_type_graduated_expelling));
        DOC_TYPE.put("PracticePermission", ctx.getString(R.string.doc_type_practice_permission));
        DOC_TYPE.put("GraduationThesisTopics", ctx.getString(R.string.doc_type_graduation_thesis_topics));


        DOC_STATUS = HashBiMap.create(4);
        DOC_STATUS.put("PROCESSING", ctx.getString(R.string.doc_status_processing));
        DOC_STATUS.put("APPROVED", ctx.getString(R.string.doc_status_approved));
        DOC_STATUS.put("CLOSED", ctx.getString(R.string.doc_status_closed));
        DOC_STATUS.put("REJECTED", ctx.getString(R.string.doc_status_rejected));

    }

    private static <T> T throwIfNull(T ref) {
        return Optional.ofNullable(ref).orElseThrow(() -> new IllegalArgumentException(ref + " mustn't be null"));
    }

    public static String parseDate(String date) {
        final Calendar calendar = DatatypeConverter.parseDateTime(date);
        return DATE_FORMATTER.format(calendar.getTime());
    }

    public static String toUserStatus(String hlfStatus) {
        return throwIfNull(DOC_STATUS.get(hlfStatus));
    }

    public static String fromUserStatus(String userStatus) {
        return throwIfNull(DOC_STATUS.inverse().get(userStatus));
    }

    public static String fromUserDocumentType(String userDocType) {
        return throwIfNull(DOC_TYPE.inverse().get(userDocType));
    }

    public static String toUserDocumentType(String hlfDocType) {
        return throwIfNull(DOC_TYPE.get(hlfDocType));
    }
}
