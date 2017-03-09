/* Copyright (C) 2016 ASYX International B.V. All rights reserved. */
package com.example.repository;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;

import com.example.model.Document;
import com.example.settings.IndicesSettings;


/**
 * @author fajars
 * @version 1.0, Jun 13, 2016
 * @since
 */
@Component
public class DocumentPerdaysRepository {

    @Resource
    private DocumentRepository documentRepository;
    @Autowired
    private IndicesSettings indices;
    @Autowired
    private Environment environment;

    public List<Document> findByReference(String reference) {
        String indexPerDays = environment
                .getProperty("elasticsearch.index.document_perdays");
        if (StringUtils.isNotBlank(indexPerDays))
            return findByReferenceWithIndices(indices.documentSearchIndex(),
                    reference);
        else
            return documentRepository.findByReference(reference);
    }

    public List<Document> findByReferenceWithIndices(String indices,
            String reference) {
        QueryBuilder query = QueryBuilders.termQuery("reference", reference);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indices).withQuery(query).build();
        Page<Document> documents = documentRepository.search(searchQuery);
        return documents.getContent();
    }

    public Document findOne(String uuid) {
        String indexPerDays = environment
                .getProperty("elasticsearch.index.document_perdays");
        if (StringUtils.isNotBlank(indexPerDays))
            return findOneWithIndices(indices.documentSearchIndex(), uuid);
        else
            return documentRepository.findOne(uuid);
    }

    public Document findOneWithIndices(String indices, String uuid) {
        QueryBuilder query = QueryBuilders.termQuery("uuid", uuid);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indices).withQuery(query).build();
        Page<Document> documents = documentRepository.search(searchQuery);
        return !documents.getContent().isEmpty() ? documents.getContent()
                .get(0) : null;
    }
}
