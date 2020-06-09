# elasticsearch-spring-boot-starter

#### 项目介绍
由于elasticsearch版本更新太频繁，导致java客户端的api升级也较为频繁<br>
而且各版本之间变动很大，做不到很好的兼容性，因此spring官网提供的<br>
spring-data-elasticsearch默认依赖的客户端对新版本支持不够友好。<br>
目前比较好的使用方式，还是使用原生的客户端，这样不但灵活，也易于<br>
学习，但spring官方并未提供其相应的boot starter实现，这就是此项目<br>
的由来。

#### 使用说明
1)依赖  
```
<dependency>
    <groupId>com.cjl</groupId>  
    <artifactId>elasticsearch-spring-boot-starter</artifactId>  
    <version>1.2.0.RELEASE</version>  
</dependency> 
```
2)application.yml  
```
spring:  
  elasticsearch:  
    cluster-nodes:  192.168.19.131:9300
```
3)代码
```java
package com.es.controller;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EsClientController {
	@Autowired
	private Client client;
	
	@GetMapping("/index")
	public Object index() {
		GetResponse response = client.prepareGet("bank", "_doc", "1").get();
		return response;
	}
}
```