/* Copyright (C) 2014 ASYX International B.V. All rights reserved. */
package com.example.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

/**
 * @author frank
 * @version 1.0, Feb 8, 2014
 * @since 3.0.0
 */
@org.springframework.data.elasticsearch.annotations.Document(indexName = "#{@documentIndex}")
@Mapping(mappingPath = "/common_elastic/document_mapping.json")
public class Document {

    @Id
    private String uuid;

    @Field(type = FieldType.Nested)
    Map<String, UUID> associateDocstores;

    @Field(type = FieldType.String, analyzer = "keyword")
    UUID ownerDocstore;

    @Field(type = FieldType.String, analyzer = "keyword")
    private String businessKey;

    @Field(type = FieldType.Date)
    private Date dateReceived;

    @Field(type = FieldType.String, analyzer = "keyword")
    private String reference;

    @Field(type = FieldType.Nested)
    List<String> tags;

    @Field(type = FieldType.String, analyzer = "keyword")
    private String type;

    @Field(type = FieldType.Double)
    private BigDecimal totalAmount;

    @Field(type = FieldType.String)
    private String originalCurrency;

    @Field(type = FieldType.Date)
    private Date paymentDate;

    public Document() {
        super();
    }

    public Document(String uuid) {
        super();
        this.uuid = uuid;
    }

    public Document(UUID uuid) {
        super();
        this.uuid = uuid.toString();
    }

    public void addTag(String tag) {
        List<String> newTags = new ArrayList<>(this.tags);
        if (newTags.add(tag)) {
            this.tags = newTags;
        }
    }

    public Map<String, UUID> getAssociateDocstores() {
        return associateDocstores;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public String getOriginalCurrency() {
        return originalCurrency;
    }

    public UUID getOwnerDocstore() {
        return ownerDocstore;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public String getReference() {
        return reference;
    }

    public List<String> getTags() {
        return tags;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getType() {
        return type;
    }

    public UUID getUuid() {
        return UUID.fromString(uuid);
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public boolean removeTag(String tag) {
        if (!tags.contains(tag)) {
            return false;
        }
        List<String> newTags = new ArrayList<>(this.tags);
        if (newTags.remove(tag)) {
            this.tags = newTags;
            return true;
        }
        return false;
    }

    public void setAssociateDocstores(Map<String, UUID> associateDocstores) {
        this.associateDocstores = associateDocstores;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public void setOriginalCurrency(String originalCurrency) {
        this.originalCurrency = originalCurrency;
    }

    public void setOwnerDocstore(UUID ownerDocstore) {
        this.ownerDocstore = ownerDocstore;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid.toString();
    }

    @Override
    public String toString() {
        return "Document [uuid=" + uuid + ", associateDocstores="
                + associateDocstores + ", ownerDocstore=" + ownerDocstore
                + ", businessKey=" + businessKey + ", dateReceived="
                + dateReceived + ", reference=" + reference + ", tags=" + tags
                + ", type=" + type + ", totalAmount=" + totalAmount
                + ", originalCurrency=" + originalCurrency + ", paymentDate="
                + paymentDate + "]";
    }

}
