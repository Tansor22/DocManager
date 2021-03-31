package podam;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DocumentProvider {
    long ID = 0;
    String[] TITLES = new String[]{
            "Приказ об отчислении",
            "Приказ о переходе на очное обучение",
            "Приках о зачислении",
            "Приказ о прохождение производственной практики"
    };
    String[] DESCRIPTIONS = new String[]{
            "Красивый документ",
            "Срочный",
            "Сверхсрочный",
    };
    String[] ORGS = new String[]{
            "АГТУ",
            "АГУ",
            "АГАУ",
    };
}
