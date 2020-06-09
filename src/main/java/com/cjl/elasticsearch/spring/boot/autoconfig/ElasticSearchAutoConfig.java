package com.cjl.elasticsearch.spring.boot.autoconfig;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.lease.Releasable;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnClass({ Client.class, TransportClient.class, NodeClient.class })
@EnableConfigurationProperties(ElasticSearchProperties.class)
public class ElasticSearchAutoConfig implements DisposableBean {
	private static final Map<String, String> DEFAULTS;

	static {
		Map<String, String> defaults = new LinkedHashMap<String, String>();
		defaults.put("http.enabled", String.valueOf(false));
		defaults.put("node.local", String.valueOf(true));
		defaults.put("path.home", System.getProperty("user.dir"));
		DEFAULTS = Collections.unmodifiableMap(defaults);
	}

	private static final Log logger = LogFactory.getLog(ElasticSearchAutoConfig.class);

	private final ElasticSearchProperties properties;

	private Releasable releasable;

	public ElasticSearchAutoConfig(ElasticSearchProperties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean
	public Client elasticsearchClient() {
		try {
			return createClient();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	private Client createClient() throws Exception {
		if (StringUtils.hasLength(this.properties.getClusterNodes())) {
			return createTransportClient();
		}
		return createNodeClient();
	}

	/**
	 * 这个方法是用来创建本地连接的，就是当配置文件没有任何配置的时候，就默认访问本地的一个节点 默认“127.0.0.1:9300”
	 */
	private Client createNodeClient() throws Exception {
		// 我们参考的是es-2.4.6的代码，由于现在用的是6.2.4版本，有些类的方法直接被删除了，
		// 我们要找相同作用的方法进行重写
		/* Settings.Builder settings = Settings.settingsBuilder(); */
		// 新的方法名叫builder()
		Settings.Builder settings = Settings.builder();

		for (Map.Entry<String, String> entry : DEFAULTS.entrySet()) {
			if (!this.properties.getProperties().containsKey(entry.getKey())) {
				settings.put(entry.getKey(), entry.getValue());
			}
		}
		//put方法已经没有了，要换个写法，读取属性文件，用后面的方法循环读取
		/* settings.put(this.properties.getProperties()); */
		
		// 加载属性配置
		for (Map.Entry<String, String> entry : this.properties.getProperties().entrySet()) {
			settings.put(entry.getKey(), entry.getValue());
		}
		
		//2.4.6支持
		/*Node node = new NodeBuilder().settings(settings).clusterName(this.properties.getClusterName()).node();*/
		//6.2.4支持
		/*Node node = new Node(settings.build());*/

		/*this.releasable = (Releasable)node;
		return node.client();*/
		
		
		//6.5.4支持
		NodeClient client = new NodeClient(settings.build(), null);
		this.releasable = (Releasable)client;
		return client;
	}

	private Client createTransportClient() throws Exception {
		TransportClientFactoryBean factory = new TransportClientFactoryBean();
		factory.setClusterNodes(this.properties.getClusterNodes());
		factory.setProperties(createProperties());
		factory.afterPropertiesSet();
		TransportClient client = factory.getObject();
		this.releasable = client;
		return client;
	}

	private Properties createProperties() {
		Properties properties = new Properties();
		properties.put("cluster.name", this.properties.getClusterName());
		properties.putAll(this.properties.getProperties());
		return properties;
	}

	@Override
	public void destroy() throws Exception {
		if (this.releasable != null) {
			try {
				if (logger.isInfoEnabled()) {
					logger.info("Closing Elasticsearch client");
				}
				try {
					this.releasable.close();
				} catch (NoSuchMethodError ex) {
					// Earlier versions of Elasticsearch had a different method name
					ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(Releasable.class, "release"),
							this.releasable);
				}
			} catch (final Exception ex) {
				if (logger.isErrorEnabled()) {
					logger.error("Error closing Elasticsearch client: ", ex);
				}
			}
		}
	}
}
