package core.activities.ui.create_doc.adapt;

import api.clients.middleware.HLFDataAdapter;
import api.clients.middleware.entity.Document;
import api.clients.middleware.entity.PracticePermissionAttributes;
import api.clients.middleware.entity.PracticePermissionStudent;
import com.google.common.collect.ImmutableMap;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import core.activities.ui.shared.forms.JSONModelEx;

import java.util.Map;

public class PracticePermissionFormModelAdapter extends FormModelAdapter<PracticePermissionAttributes> {
    public PracticePermissionFormModelAdapter(Document document) {
        super(document);
    }

    @Override
    protected <T extends JSONModel> void adaptInternal(T model, PracticePermissionAttributes attrs) {
        switch (model.getId()) {
            // text edits (multiline and plain)
            case "course":
                DataValueHashMap.put(model.getId(), attrs.course().toString());
                break;
            case "speciality":
                DataValueHashMap.put(model.getId(), attrs.speciality());
                break;
            case "practiceType":
                DataValueHashMap.put(model.getId(), attrs.practiceType());
                break;
            case "studyType":
                DataValueHashMap.put(model.getId(), HLFDataAdapter.toUserStudyType(attrs.studyType()));
                break;
            case "dateFrom":
                DataValueHashMap.put(model.getId(), attrs.dateFrom());
                break;
            case "dateTo":
                DataValueHashMap.put(model.getId(), attrs.dateTo());
                break;

            // data supplier
            case "student": {
                final JSONModelEx modelEx = (JSONModelEx) model;
                for (int i = 0; i < attrs.students().size(); i++) {
                    final PracticePermissionStudent student = attrs.students().get(i);
                    Map<String, String> uiData = ImmutableMap.<String, String>builder()
                            .put(getDataSupplierUiKey(modelEx, "fullName"), student.getCommonInfo().getFullName())
                            .put(getDataSupplierUiKey(modelEx, "group"), student.getCommonInfo().getGroup())
                            .put(getDataSupplierUiKey(modelEx, "onGovernmentPay"), HLFDataAdapter.toUserOnGovernmentPay(student.getCommonInfo().getOnGovernmentPay()))
                            .put(getDataSupplierUiKey(modelEx, "practiceLocation"), student.getPracticeLocation())
                            .put(getDataSupplierUiKey(modelEx, "headFullName"), student.getHeadFullName())
                            .build();


                    Map<String, String> modelData = ImmutableMap.<String, String>builder()
                            .put("fullName", student.getCommonInfo().getFullName())
                            .put("group", student.getCommonInfo().getGroup())
                            .put("onGovernmentPay", HLFDataAdapter.toUserOnGovernmentPay(student.getCommonInfo().getOnGovernmentPay()))
                            .put("practiceLocation", student.getPracticeLocation())
                            .put("headFullName", student.getHeadFullName())
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
