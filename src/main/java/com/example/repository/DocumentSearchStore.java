/* Copyright (C) 2014 ASYX International B.V. All rights reserved. */
package com.example.repository;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Component;

import com.example.model.Document;
import com.example.settings.IndicesSettings;

/**
 * @author fajars
 * @version 1.0, Mar 13, 2017
 * @since
 */
@Component
public class DocumentSearchStore {

    private static Logger log = Logger.getLogger(DocumentSearchStore.class);

    private static final String INDEX_TYPE = "document";

    @Autowired
    private ElasticsearchOperations elasticsearchTemplate;
    @Autowired
    private Environment environment;
    @Autowired
    private IndicesSettings indices;

    public boolean delete(Document document) {
        try {
            String indexName = indices.getIndexPerDays(document
                    .getDateReceived());

            elasticsearchTemplate.delete(indexName, INDEX_TYPE, document
                    .getUuid().toString());

            return true;
        } catch (Exception e) {
            log.warn("Elasticsearch exception during delete", e);
            return false;
        }
    }

    public boolean index(Document document) {
        try {
            IndexQuery query = new IndexQuery();
            query.setId(document.getUuid().toString());
            query.setObject(document);
            elasticsearchTemplate.index(query);
            return true;
        } catch (Exception e) {
            log.warn("Elasticsearch exception during index", e);
            return false;
        }
    }

    public boolean index(Document document, String source, String indexName) {
        try {
            IndexQuery qry = new IndexQuery();
            qry.setId(document.getUuid().toString());
            qry.setIndexName(indexName);
            qry.setType(INDEX_TYPE);
            qry.setSource(source);
            elasticsearchTemplate.index(qry);
            return true;
        } catch (Exception e) {
            log.warn("Elasticsearch exception during index", e);
            return false;
        }
    }

    public List<Document> queryForList(String query) {
        try {
            return elasticsearchTemplate.queryForList(new StringQuery(query),
                    Document.class);
        } catch (Exception e) {
            log.warn("Elasticsearch exception during query", e);
            return Collections.emptyList();
        }
    }
}
