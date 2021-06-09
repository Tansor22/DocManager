package core.activities.ui.create_doc.adapt.attributes;

import api.clients.middleware.HLFDataAdapter;
import api.clients.middleware.entity.Attributes;
import api.clients.middleware.entity.PracticePermissionAttributes;
import api.clients.middleware.entity.PracticePermissionStudent;
import api.clients.middleware.entity.Student;
import core.activities.ui.create_doc.adapt.attributes.model.StudentData;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PracticePermissionAttributesRetriever extends AttributesRetriever{
    @Override
    public Attributes retrieveInternal(Map<String, String> data) {
        PracticePermissionAttributes attrs = new PracticePermissionAttributes();
        attrs.course(Integer.parseInt(Objects.requireNonNull(data.get("course"))));
        attrs.studyType(HLFDataAdapter.fromUserStudyType(data.get("studyType")));
        attrs.speciality(data.get("speciality"));
        attrs.practiceType(data.get("practiceType"));
        attrs.dateFrom(data.get("dateFrom"));
        attrs.dateTo(data.get("dateTo"));
        final List<PracticePermissionStudent> students = data.entrySet().stream()
                .filter(e -> e.getKey().startsWith("_data_student"))
                .map(e -> gson.fromJson(e.getValue(), StudentData.class))
                .map(student -> {
                    PracticePermissionStudent output = new PracticePermissionStudent();
                    output.setPracticeLocation(student.getPracticeLocation());
                    output.setHeadFullName(student.getHeadFullName());
                    Student commonInfo = new Student();
                    commonInfo.setFullName(student.getFullName());
                    commonInfo.setOnGovernmentPay("бюджет".equalsIgnoreCase(student.getOnGovernmentPay()));
                    commonInfo.setNationality(student.getNationality());
                    commonInfo.setGroup(student.getGroup());
                    output.setCommonInfo(commonInfo);
                    return output;
                })
                .collect(Collectors.toList());
        attrs.students(students);
        return attrs;
    }
}
