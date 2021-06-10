package core.activities.ui.create_doc.adapt;

import api.clients.middleware.HLFDataAdapter;
import api.clients.middleware.entity.Document;
import api.clients.middleware.entity.GraduationThesisTopicsAttributes;
import api.clients.middleware.entity.GraduationThesisTopicsStudent;
import com.google.common.collect.ImmutableMap;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import core.activities.ui.shared.forms.JSONModelEx;

import java.util.Map;

public class GraduationThesisTopicsFormModelAdapter extends FormModelAdapter<GraduationThesisTopicsAttributes> {
    public GraduationThesisTopicsFormModelAdapter(Document document) {
        super(document);
    }

    @Override
    protected <T extends JSONModel> void adaptInternal(T model, GraduationThesisTopicsAttributes attrs) {
        switch (model.getId()) {
            // text edits (multiline and plain)
            case "group":
                DataValueHashMap.put(model.getId(), attrs.group());
                break;
            case "speciality":
                DataValueHashMap.put(model.getId(), attrs.speciality());
                break;
            // radio
            case "study_type": {
                final String studyType = attrs.studyType();
                model.getList().stream()
                        .filter(listModel -> listModel.getIndexText().equals(HLFDataAdapter.toUserStudyType(studyType)))
                        .findFirst().ifPresent(listModel -> model.setSelectedValue(listModel.getIndex().toString()));
                break;
            }
            // data supplier
            case "student": {
                final JSONModelEx modelEx = (JSONModelEx) model;
                for (int i = 0; i < attrs.students().size(); i++) {
                    final GraduationThesisTopicsStudent student = attrs.students().get(i);
                    Map<String, String> uiData = ImmutableMap.<String, String>builder()
                            .put(getDataSupplierUiKey(modelEx, "fullName"), student.getCommonInfo().getFullName())
                            .put(getDataSupplierUiKey(modelEx, "thesis"), student.getTopic())
                            .put(getDataSupplierUiKey(modelEx, "headFullName"), student.getAcademicAdvisorFullName())
                            .build();


                    Map<String, String> modelData = ImmutableMap.<String, String>builder()
                            .put("fullName", student.getCommonInfo().getFullName())
                            .put("thesis", student.getTopic())
                            .put("headFullName", student.getAcademicAdvisorFullName())
                            .build();

                    final JSONModel dataModel = JSONModelEx.picturedTextView(
                            "_data_" + modelEx.getId() + "_" + i,
                            getDataSupplierUiRepresentation(uiData));
                    modelEx.getForm().add(dataModel);
                    DataValueHashMap.dataValueHashMap.put(dataModel.getId(), getDataSupplierModelRepresentation(modelData));
                }
                break;
            }
        }
    }
}
