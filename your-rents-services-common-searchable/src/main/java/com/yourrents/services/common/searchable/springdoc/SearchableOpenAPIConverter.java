package com.yourrents.services.common.searchable.springdoc;

import java.util.Iterator;
import com.fasterxml.jackson.databind.JavaType;
import com.yourrents.services.common.searchable.springdoc.model.Searchable;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.providers.ObjectMapperProvider;

/**
 * The Searchable Type models converter.
 * 
 * @author Lucio Benfante
 */
public class SearchableOpenAPIConverter implements ModelConverter {

    private static final String SEARCHABLE_TO_REPLACE = "com.yourrents.services.common.searchable.Searchable";

    private static final String FILTER_CRITERIA_TO_REPLACE = "com.yourrents.services.common.searchable.FilterCriteria";

    private static final AnnotatedType SEARCHABLE = new AnnotatedType(Searchable.class).resolveAsRef(true);

    /**
     * The Spring doc object mapper.
     */
    private final ObjectMapperProvider springDocObjectMapper;

    /**
     * Instantiates a new Searchable open api converter.
     *
     * @param springDocObjectMapper the spring doc object mapper
     */
    public SearchableOpenAPIConverter(ObjectMapperProvider springDocObjectMapper) {
        this.springDocObjectMapper = springDocObjectMapper;
    }

    /**
     * Resolve schema.
     *
     * @param type    the type
     * @param context the context
     * @param chain   the chain
     * @return the schema
     */
    @Override
    public Schema<?> resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        JavaType javaType = springDocObjectMapper.jsonMapper().constructType(type.getType());
        if (javaType != null) {
            Class<?> cls = javaType.getRawClass();
            if (SEARCHABLE_TO_REPLACE.equals(cls.getCanonicalName())
                    || FILTER_CRITERIA_TO_REPLACE.equals(cls.getCanonicalName())) {
                if (!type.isSchemaProperty())
                    type = SEARCHABLE;
                else
                    type.name(cls.getSimpleName() + StringUtils.capitalize(type.getParent().getType()));
           }
        }
        return (chain.hasNext()) ? chain.next().resolve(type, context, chain) : null;
    }

}