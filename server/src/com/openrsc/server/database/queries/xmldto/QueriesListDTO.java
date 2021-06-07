package com.openrsc.server.database.queries.xmldto;

import java.util.List;

public class QueriesListDTO {
    private List<QueryDTO> queries;

    public QueriesListDTO(List<QueryDTO> queries) {
        this.queries = queries;
    }

    public List<QueryDTO> getQueries() {
        return queries;
    }

    public void setQueries(List<QueryDTO> queries) {
        this.queries = queries;
    }
}
