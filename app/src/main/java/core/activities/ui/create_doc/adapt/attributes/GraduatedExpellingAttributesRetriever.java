package core.activities.ui.create_doc.adapt.attributes;

import api.clients.middleware.entity.*;
import core.activities.ui.create_doc.adapt.attributes.model.StudentData;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GraduatedExpellingAttributesRetriever extends AttributesRetriever {
    @Override
    public Attributes retrieveInternal(Map<String, String> data) {
        GraduatedExpellingAttributes attrs = new GraduatedExpellingAttributes();
        attrs.course(Integer.parseInt(Objects.requireNonNull(data.get("course"))));
        attrs.faculty(data.get("faculty"));
        attrs.speciality(data.get("speciality"));
        attrs.qualification(data.get("qualification"));
        final List<GraduatedExpellingStudent> students = data.entrySet().stream()
                .filter(e -> e.getKey().startsWith("_data_student"))
                .map(e -> gson.fromJson(e.getValue(), StudentData.class))
                .map(student -> {
                    GraduatedExpellingStudent output = new GraduatedExpellingStudent();
                    output.setExamDate(student.getExamDate());
                    output.setHasHonoursDegree(BooleanUtils.toBoolean(student.getHonoursDegree()));
                    Student commonInfo = new Student();
                    commonInfo.setFullName(student.getFullName());
                    commonInfo.setOnGovernmentPay("бюджет".equalsIgnoreCase(student.getOnGovernmentPay()));
                    commonInfo.setNationality(student.getNationality());
                    output.setCommonInfo(commonInfo);
                    return output;
                })
                .collect(Collectors.toList());
        attrs.students(students);
        return attrs;
    }
}
