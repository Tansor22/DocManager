package core.activities.ui.create_doc.adapt.attributes;

import api.clients.middleware.HLFDataAdapter;
import api.clients.middleware.entity.Attributes;
import api.clients.middleware.entity.GraduationThesisTopicsAttributes;
import api.clients.middleware.entity.GraduationThesisTopicsStudent;
import api.clients.middleware.entity.Student;
import core.activities.ui.create_doc.adapt.attributes.model.StudentData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraduationThesisTopicsAttributesRetriever extends AttributesRetriever {
    @Override
    public Attributes retrieveInternal(Map<String, String> data) {
        GraduationThesisTopicsAttributes attrs = new GraduationThesisTopicsAttributes();
        attrs.group(data.get("group"));
        attrs.studyType(HLFDataAdapter.fromUserStudyType(data.get("study_type")));
        attrs.speciality(data.get("speciality"));
        final List<GraduationThesisTopicsStudent> students = data.entrySet().stream()
                .filter(e -> e.getKey().startsWith("_data_student"))
                .map(e -> gson.fromJson(e.getValue(), StudentData.class))
                .map(student -> {
                    GraduationThesisTopicsStudent output = new GraduationThesisTopicsStudent();
                    output.setTopic(student.getThesis());
                    output.setAcademicAdvisorFullName(student.getHeadFullName());
                    Student commonInfo = new Student();
                    commonInfo.setFullName(student.getFullName());
                    output.setCommonInfo(commonInfo);
                    return output;
                })
                .collect(Collectors.toList());
        attrs.students(students);
        return attrs;
    }
}
