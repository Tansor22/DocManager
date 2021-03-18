package api.clients.middleware.entity;

import lombok.Data;

import java.util.List;
@Data
public class Document {
    String documentId;
    String org;
    String date;
    String content;
    String status;
    List<String> signsRequired;
    List<String> signedBy;
}
