package podam;

import api.clients.middleware.entity.Document;
import uk.co.jemos.podam.api.AbstractRandomDataProviderStrategy;
import uk.co.jemos.podam.api.AttributeMetadata;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static podam.DocumentProvider.*;

public class DocumentDataProviderStrategy extends AbstractRandomDataProviderStrategy {
    private static final Random random = new Random(System.currentTimeMillis());

    @Override
    public <T> T getTypeValue(AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap, Class<T> pojoType) {
        // pojo that is built => Document
        if (attributeMetadata.getPojoClass() == Document.class) {
            switch (attributeMetadata.getAttributeName()) {
                case "documentId":
                    return (T) String.valueOf(++ID);
                case "title":
                    return (T) random(TITLES);
                case "owner":
                    return (T) random(OWNERS);
                case "group":
                    return (T) random(ORGS);
                case "type":
                    return (T) random(TYPES);
                case "content":
                    return (T) random(CONTENTS);
                case "status":
                    return (T) random(STATUSES);
                case "changes":
                    return (T) Collections.emptyList();
                case "signsRequired":
                    return (T) getNRandomEntries(OWNERS.clone(), random.nextInt(5));
                case "signedBy":
                    return (T) Collections.emptyList();
                case "date":
                    return (T) randomDate(LocalDate.ofYearDay(2020, 1))
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            }
        }
        return super.getTypeValue(attributeMetadata, genericTypesArgumentsMap, pojoType);
    }

    private <T> T random(T[] arr) {
        return arr[random.nextInt(arr.length)];
    }

    private <T> List<T> getNRandomEntries(T[] src, int n) {
        List<T> output = new ArrayList<>(n);
        final List<T> entries = new ArrayList<>(Arrays.asList(src));
        for (int i = 0; i < n; i++) {
            Collections.shuffle(entries);
            final T entry = entries.remove(0);
            output.add(entry);
        }
        return output;
    }

    private static <T> void shuffleArray(T[] array) {
        int index;
        T temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private LocalDate randomDate(LocalDate origin) {
        return LocalDate.ofEpochDay(ThreadLocalRandom
                .current()
                .nextLong(origin.toEpochDay(), LocalDate.now().toEpochDay()));
    }

    @Override
    public int getNumberOfCollectionElements(Class<?> type) {
        return super.getNumberOfCollectionElements(type);
    }

}
