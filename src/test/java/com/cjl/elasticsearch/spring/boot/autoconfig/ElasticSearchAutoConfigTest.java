package com.cjl.elasticsearch.spring.boot.autoconfig;

import static org.junit.Assert.*;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= {ElasticSearchAutoConfig.class})
public class ElasticSearchAutoConfigTest {
	@Autowired
	private Client client;
	
	//创建索引
	@Test
	public void testAdminCreateIndex() {
		CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate("test").get();
		assertEquals("test",createIndexResponse.index());
	}
}
