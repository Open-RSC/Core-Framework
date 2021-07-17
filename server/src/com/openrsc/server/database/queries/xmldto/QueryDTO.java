package com.openrsc.server.database.queries.xmldto;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class QueryDTO {
    @XStreamAsAttribute
    private String key;
    private String value;

    public QueryDTO(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
