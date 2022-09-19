package tech.tresearchgroup.palila.controller.search;

import java.util.List;

public interface SearchFunctionality {
    List search(String query) throws Exception;

    void createDocument(Object object) throws Exception;

    void updateDocument(Object object) throws Exception;

    void deleteDocument(long id) throws Exception;

    void deleteAllDocuments() throws Exception;

    void reindex(int maxResultsSize) throws Exception;
}
