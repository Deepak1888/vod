/**
 * 
 */
package com.globant.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author mangesh.pendhare
 *
 */

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.globant.repository")
public class ElasticSearchConfig {

	@Value("${elasticsearch.host}")
	private String esHost;

	@Value("${elasticsearch.port}")
	private int esPort;

	/*
	 * @Bean public Client client() throws Exception { Settings settings =
	 * Settings.builder().put("cluster.name", esClusterName).build();
	 * TransportClient client = new PreBuiltTransportClient(settings);
	 * client.addTransportAddress(new
	 * TransportAddress(InetAddress.getByName(esHost), esPort)); return client; }
	 */

//	@Bean
//	public ElasticsearchOperations elasticsearchTemplate() throws Exception {
//		return new ElasticsearchTemplate(client());
//	}

	@Bean
	RestHighLevelClient elasticsearchClient() {
		final ClientConfiguration configuration = ClientConfiguration.builder().connectedTo(esHost + ":" + esPort)
				.build();
		RestHighLevelClient client = RestClients.create(configuration).rest();
		return client;
	}

	@Bean
	ElasticsearchRestTemplate elasticsearchTemplate() {
		return new ElasticsearchRestTemplate(elasticsearchClient());
	}

}