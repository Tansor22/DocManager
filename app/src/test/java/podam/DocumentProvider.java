package podam;

import lombok.experimental.UtilityClass;


public class DocumentProvider {
    static long ID = 0;
    static String[] TITLES = new String[]{
            "Приказ о допуске на практику студентов",
            "Представление-отчисление",
            "Приках о темах выпускных квалификационных работ студентов",
            "Приказ о навправлении на практику студентов"
    };
    static String[] OWNERS = new String[]{
            "С.М. Старолетов",
            "С.А. Кантор",
            "А.С. Авдеев",
            "Л.И. Сучкова",
            "А.Г. Якунин",
    };
    static String[] ORGS = new String[]{
            "Administration",
    };
    static String[] TYPES = new String[]{
            "GraduatedExpelling",
            "PracticePermission",
            "GraduationThesisTopics",
    };
    static String[] CONTENTS = new String[]{
            "GraduatedExpelling",
            "PracticePermission",
            "GraduationThesisTopics",
    };
    static String[] STATUSES = new String[]{
            "PROCESSING",
            "APPROVED",
            "CLOSED",
            "REJECTED",
    };
}
