package com.cjl.elasticsearch.spring.boot.autoconfig;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.elasticsearch")
public class ElasticSearchProperties {
	/**
	 * Elasticsearch cluster name.
	 */
	private String clusterName = "elasticsearch";

	/**
	 * Comma-separated list of cluster node addresses. If not specified, starts a client
	 * node.
	 */
	private String clusterNodes;

	/**
	 * Additional properties used to configure the client.
	 */
	private Map<String, String> properties = new HashMap<String, String>();

	public String getClusterName() {
		return this.clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getClusterNodes() {
		return this.clusterNodes;
	}

	public void setClusterNodes(String clusterNodes) {
		this.clusterNodes = clusterNodes;
	}

	public Map<String, String> getProperties() {
		return this.properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}
