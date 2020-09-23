package com.shb.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author songhaibo
 * @create 2020-09-23 1:53 下午
 */
//找对象
    //放到Spring中待用
    //如果是springboot就先分析员吗
    //xxxxConfiguration xxxProerties
@Configuration
public class ElasticSearchConfig {
  //<bean id='RestHighLevelClient' class='restHighLevelClient'>
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        return client;
    }
}
