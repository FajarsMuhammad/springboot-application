/* Copyright (C) 2014 ASYX International B.V. All rights reserved. */
package com.example.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.example.settings.IndicesSettings;

/**
 * @author fajars
 * @version 1.0, Mar 13, 2017
 * @since
 */
@Configuration
@EnableElasticsearchRepositories({ "com.example.repository" })
public class ElasticsearchConfig {
    private static final Logger log = LoggerFactory
            .getLogger(ElasticsearchConfig.class);

    public static final String INDEX_NAME = "example";
    
    @Autowired
    private Environment env;

    @Autowired
    private IndicesSettings indices;

    /**
     * @param props
     * @return the settings for the Elasticsearch node client
     */
    public static Settings getNodeClientSettings(final PropertyResolver props) {
        String multicastEnabled = props
                .getProperty("elasticsearch.discovery.zen.ping.multicast.enabled");
        String unicastHosts = props
                .getProperty("elasticsearch.discovery.zen.ping.unicast.hosts");
        String clusterName = props.getProperty("elasticsearch.cluster.name");
        Settings.Builder settings = Settings.builder();
        settings.put("discovery.zen.ping.multicast.enabled", multicastEnabled);
        settings.put("discovery.zen.ping.unicast.hosts", unicastHosts);
        settings.put("cluster.name", clusterName);
        return settings.build();
    }

    /**
     * @param props
     * @return the settings for the Elasticsearch transport client
     */
    public static Settings getTransportClientSettings(
            final PropertyResolver props) {
        boolean sniff = props.getProperty(
                "elasticsearch.client.transport.sniff", boolean.class);
        boolean ignoreClusterName = props.getProperty(
                "elasticsearch.client.transport.ignore_cluster_name",
                boolean.class);
        String pingTimeout = props
                .getProperty("elasticsearch.client.transport.ping_timeout");
        String nodesSamplerInterval = props
                .getProperty("elasticsearch.client.transport.nodes_sampler_interval");
        String clusterName = props.getProperty("elasticsearch.cluster.name");
        Settings.Builder settings = Settings.builder();
        settings.put("client.transport.sniff", sniff);
        settings.put("client.transport.ignore_cluster_name", ignoreClusterName);
        settings.put("client.transport.ping_timeout", pingTimeout);
        settings.put("client.transport.nodes_sampler_interval",
                nodesSamplerInterval).put("cluster.name", clusterName);
        return settings.build();
    }

    /**
     * @param props
     * @return the parsed transport addresses
     */
    public static TransportAddress[] parseTransportAdresses(
            PropertyResolver props) {

        String prop = props
                .getProperty("elasticsearch.client.transport.addresses");
        if (prop == null || prop.isEmpty()) {
            return new TransportAddress[0];
        }
        ArrayList<TransportAddress> addresses = new ArrayList<>();
        String[] vals = prop.split(",");
        for (String val : vals) {
            try {
                addresses.add(new InetSocketTransportAddress(InetAddress
                        .getByName(val.split(":")[0]), Integer.parseInt(val
                                .split(":")[1])));
            } catch (NumberFormatException | UnknownHostException e) {
                log.warn("Unknown host: " + val.split(":")[0], e);
            }
        }
        return addresses.toArray(new TransportAddress[addresses.size()]);
    }

    @Bean
    public String documentIndex() {
        return indices.getIndexPerDays(new Date());
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        Client client;
        boolean local = env.getProperty("elasticsearch.local", boolean.class);
        if (local) {
            Node node = NodeBuilder.nodeBuilder().client(false).local(true)
                    .node();
            client = node.client();
        } else {
            boolean transportClientEnabled = env.getProperty(
                    "elasticsearch.client.transport.enabled", boolean.class);
            if (transportClientEnabled) {
                Settings settings = getTransportClientSettings(env);
                TransportClient transportClient = TransportClient.builder()
                        .settings(settings).build();
                TransportAddress[] addresses = parseTransportAdresses(env);
                transportClient.addTransportAddresses(addresses);
                client = transportClient;
            } else {
                Settings settings = getNodeClientSettings(env);
                Node node = NodeBuilder.nodeBuilder().settings(settings)
                        .client(true).local(false).node();
                client = node.client();
            }
        }
        return new ElasticsearchTemplate(client);
    }

}
