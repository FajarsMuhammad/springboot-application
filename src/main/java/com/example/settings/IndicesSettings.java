/* Copyright (C) 2016 ASYX International B.V. All rights reserved. */
package com.example.settings;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.query.AliasBuilder;
import org.springframework.data.elasticsearch.core.query.AliasQuery;
import org.springframework.stereotype.Component;

/**
 * @author fajars
 * @version 1.0, Jun 11, 2016
 * @since
 */
@Component
public class IndicesSettings {

    private static final String DOCUMENT_MAPPING = "/common_elastic/document_mapping.json";
    private static final String DEFAULT_DOCUMENT_INDEX = "example";
    private static final String DOCUMENT_TYPE = "document";
    private static final String DATE_FORMAT = "yyyy.MM.dd";

    @Autowired
    private Environment environment;


    public String documentSearchIndex() {
        return environment.getProperty("elasticsearch.index.document_search",
                DEFAULT_DOCUMENT_INDEX);
    }


    public String getDefaultDocumentIndex() {
        return DEFAULT_DOCUMENT_INDEX;
    }

    public AliasQuery getDocumentAlias(String indexName) {
        String aliasName = environment
                .getProperty("elasticsearch.index.document_alias");
        if (StringUtils.isNotBlank(aliasName))
            return new AliasBuilder().withIndexName(indexName)
                    .withAliasName(aliasName).build();
        return null;
    }

    public String getFormatIndex(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(environment.getProperty(
                "elasticsearch.index.date_format", DATE_FORMAT));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return sdf.format(cal.getTime());
    }

    public String getIndexPerDays(Date date) {
        String indexName = environment
                .getProperty("elasticsearch.index.document_perdays");
        if (StringUtils.isNotBlank(indexName))
            return indexName + getFormatIndex(date);

        return DEFAULT_DOCUMENT_INDEX;
    }


    public String getMappingDocuments() throws IOException {
        return IOUtils.toString(
                this.getClass().getResourceAsStream(DOCUMENT_MAPPING), "utf8");
    }

    public String getTypeOfDocument() {
        return DOCUMENT_TYPE;
    }

}
