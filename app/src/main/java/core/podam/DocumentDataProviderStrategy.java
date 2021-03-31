package core.podam;

import api.clients.middleware.entity.Document;
import uk.co.jemos.podam.api.AbstractRandomDataProviderStrategy;
import uk.co.jemos.podam.api.AttributeMetadata;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static core.podam.DocumentProvider.*;

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
                case "description":
                    return (T) random(DESCRIPTIONS);
                case "org":
                    return (T) random(ORGS);
                case "date":
                    return (T) randomDate(LocalDate.ofYearDay(2020,1))
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            }
        }
//        attributeMetadata.getPojoClass();
//        // the pojo's field that is built e.g. title, date
//        attributeMetadata.getAttributeName();
//        // the pojo's field type that is build e.g. String.class
//        attributeMetadata.getAttributeType();
        return super.getTypeValue(attributeMetadata, genericTypesArgumentsMap, pojoType);
    }

    private <T> T random(T[] arr) {
        return arr[random.nextInt(arr.length)];
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
