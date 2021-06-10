package core.activities.ui.create_doc.adapt;

import api.clients.middleware.HLFDataAdapter;
import api.clients.middleware.entity.Document;
import api.clients.middleware.entity.GraduatedExpellingAttributes;
import api.clients.middleware.entity.GraduatedExpellingStudent;
import com.google.common.collect.ImmutableMap;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import core.activities.ui.shared.forms.JSONModelEx;

import java.util.Map;

public class GraduatedExpellingFormModelAdapter extends FormModelAdapter<GraduatedExpellingAttributes> {
    public GraduatedExpellingFormModelAdapter(Document document) {
        super(document);
    }

    @Override
    protected <T extends JSONModel> void adaptInternal(T model, GraduatedExpellingAttributes attrs) {
        switch (model.getId()) {
            // text edits (multiline and plain)
            case "course":
                DataValueHashMap.put(model.getId(), attrs.course().toString());
                break;
            case "speciality":
                DataValueHashMap.put(model.getId(), attrs.speciality());
                break;
            case "faculty":
                DataValueHashMap.put(model.getId(), attrs.faculty());
                break;
            case "qualification":
                DataValueHashMap.put(model.getId(), attrs.qualification());
                break;

            // data supplier
            case "student": {
                final JSONModelEx modelEx = (JSONModelEx) model;
                for (int i = 0; i < attrs.students().size(); i++) {
                    final GraduatedExpellingStudent student = attrs.students().get(i);
                    Map<String, String> uiData = ImmutableMap.<String, String>builder()
                            .put(getDataSupplierUiKey(modelEx, "fullName"), student.getCommonInfo().getFullName())
                            .put(getDataSupplierUiKey(modelEx, "nationality"), student.getCommonInfo().getNationality())
                            .put(getDataSupplierUiKey(modelEx, "onGovernmentPay"), HLFDataAdapter.toUserOnGovernmentPay(student.getCommonInfo().getOnGovernmentPay()))
                            .put(getDataSupplierUiKey(modelEx, "honoursDegree"), HLFDataAdapter.toUserHonoursDegree(student.getHasHonoursDegree()))
                            .put(getDataSupplierUiKey(modelEx, "examDate"), student.getExamDate())
                            .build();


                    Map<String, String> modelData = ImmutableMap.<String, String>builder()
                            .put("fullName", student.getCommonInfo().getFullName())
                            .put("nationality", student.getCommonInfo().getNationality())
                            .put("onGovernmentPay", student.getCommonInfo().getOnGovernmentPay().toString())
                            .put("honoursDegree", student.getHasHonoursDegree().toString())
                            .put("examDate", student.getExamDate())
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
