package podam;

import api.clients.middleware.entity.Document;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.TestCase;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import javax.xml.bind.DatatypeConverter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FakeDocsGenerator extends TestCase {
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    public void testGenerate() {
        PodamFactory pf = new PodamFactoryImpl(new DocumentDataProviderStrategy());
        List<Document> docs = IntStream.range(0, 10)
                .mapToObj(ignored -> pf.manufacturePojo(Document.class))
                .collect(Collectors.toList());
        System.out.println(gson.toJson(docs));
    }

    public void testDateParsing() throws ParseException { ;
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        final Calendar calendar = DatatypeConverter.parseDateTime("2021-05-20T12:30:45.709735914Z");
        System.out.println(format.format(calendar.getTime()));
    }
}
