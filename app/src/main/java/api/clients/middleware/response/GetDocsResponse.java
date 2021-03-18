package api.clients.middleware.response;

import api.clients.middleware.entity.Document;
import lombok.Data;

import java.util.List;
@Data
public class GetDocsResponse {
    List<Document> documents;
}
