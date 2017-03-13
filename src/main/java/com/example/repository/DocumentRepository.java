/* Copyright (C) 2014 ASYX International B.V. All rights reserved. */
package com.example.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.model.Document;

/**
 * @author fajars
 * @version 1.0, Mar 13, 2017
 * @since
 */
public interface DocumentRepository extends
        ElasticsearchRepository<Document, String> {

    List<Document> findByReference(String reference);

}
