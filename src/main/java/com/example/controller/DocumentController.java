package com.example.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.model.Document;
import com.example.repository.DocumentRepository;
import com.example.repository.DocumentSearchStore;

@Controller
public class DocumentController {

    @Autowired
    private DocumentSearchStore repository;
    @Resource
    private DocumentRepository documentRepository;

    @RequestMapping("/documents")
    public String documents(ModelMap model) {

        Page<Document> documents = getDocuments();
        model.put("documents", documents);

        return "documents";
    }

    private Page<Document> getDocuments() {
        BoolQueryBuilder theQuery = QueryBuilders.boolQuery().must(
                QueryBuilders.matchAllQuery());

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("xchange3").withTypes("document")
                .withQuery(theQuery).build();

        return documentRepository.search(searchQuery);
    }

    @RequestMapping("/document/add")
    public String save() {
        Document document = new Document();
        document.setUuid(UUID.randomUUID());
        document.setReference("SE00000001");
        document.setType("SettlementExtensionRequest");
        document.setTags(Arrays.asList("financed"));
        document.setAssociateDocstores(populateAssociateDocstores());
        document.setTotalAmount(new BigDecimal("22500000.00"));
        document.setPaymentDate(parseCalendar("2014-05-20").getTime());
        document.setOriginalCurrency("IDR");
        document.setDateReceived(new Date());

        repository.index(document);
        return "redirect:/documents";
    }

    @RequestMapping("/document/{uuid}/delete")
    public String delete(@PathVariable("uuid") String uuid,
            RedirectAttributes redirect, ModelMap model) {

        Document document = documentRepository.findOne(uuid);

        repository.delete(document);

        model.put("documents", getDocuments());

        return "documents";

    }

    public static GregorianCalendar parseCalendar(String dateStr) {
        int y = Integer.parseInt(dateStr.substring(0, 4));
        int m = Integer.parseInt(dateStr.substring(5, 7));
        int d = Integer.parseInt(dateStr.substring(8, 10));
        GregorianCalendar result = new GregorianCalendar(y, m - 1, d, 0, 0, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result;
    }

    public static Map<String, UUID> populateAssociateDocstores() {
        Map<String, UUID> associateDocstores = new HashMap<>();
        associateDocstores.put("bank",
                UUID.fromString("d992d9b1-b55a-45af-b11d-aecd5d6b19df"));
        associateDocstores.put("buyer",
                UUID.fromString("d992d9b1-b55a-45af-b11d-aecd5d6b19df"));
        associateDocstores.put("supplier",
                UUID.fromString("d992d9b1-b55a-45af-b11d-aecd5d6b19df"));
        return associateDocstores;
    }

}
