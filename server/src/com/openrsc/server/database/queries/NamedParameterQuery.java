package com.openrsc.server.database.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NamedParameterQuery {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String PARAM_PREFIX = "{";
    private static final String PARAM_SUFFIX = "}";
    private final String template;

    static {
    }

    public NamedParameterQuery(String template) {
        this.template = template;
    }

    public String fillParameter(String parameter, Object obj) {
        return fillParameters(Pair.of(parameter, obj));
    }

    public String fillArrayParameter(String parameter, List<?> objects) {
        List<String> asStrings = objects.stream().map(this::resolveValue).collect(Collectors.toList());
        String joined = String.join(",", asStrings);
        return fillFromValue(parameter, joined);
    }

    @SafeVarargs
    public final String fillParameters(Pair<String, Object>... pairs) {
        return fill(
                Arrays.stream(pairs).collect(Collectors.toMap(Pair::getKey, Pair::getValue))
        );
    }

    @SuppressWarnings("unchecked")
    public String fill(Object struct) {
        Map<String, Object> objectMap = OBJECT_MAPPER.convertValue(struct, Map.class);
        Map<String, String> parameterMap = objectMap.entrySet()
                .stream()
                .map(entry -> {
                            String resolvedValue = resolveValue(entry.getValue());
                            return Pair.of(
                                    String.valueOf(entry.getKey()),
                                    resolvedValue
                            );
                        }
                )
                .collect(Collectors.toMap(
                        Pair::getKey,
                        Pair::getValue
                ));

        return StrSubstitutor.replace(template, parameterMap, PARAM_PREFIX, PARAM_SUFFIX);
    }

    private String resolveValue(Object value) {
        if (value instanceof Collection) {
            return ((Collection<?>) value).stream()
                    .map(this::resolveValue)
                    .collect(Collectors.joining(","));
        } else if (value instanceof String) {
            return "'" + StringEscapeUtils.escapeSql((String) value) + "'";
        } else {
            return StringEscapeUtils.escapeSql(String.valueOf(value));
        }
    }

    private String fillFromValue(String parameter, String value) {
        Map<String, String> parameterMap = Maps.newHashMap();
        parameterMap.put(parameter, String.valueOf(value));

        return StrSubstitutor.replace(template, parameterMap, PARAM_PREFIX, PARAM_SUFFIX);
    }

    public String get() {
        return template;
    }
}
