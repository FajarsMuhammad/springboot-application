/* Copyright (C) 2014 ASYX International B.V. All rights reserved. */
package com.example.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.model.Document;


/**
 * @author frank
 * @version 1.0, Feb 8, 2014
 * @since 3.0.0
 */
public interface DocumentRepository extends
        ElasticsearchRepository<Document, String> {

    List<Document> findByReference(String reference);
    
}
