package com.shb;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.shb.pojo.User;
import com.shb.utils.ESconst;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.SearchResult;
import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * es7.6.X高级客户端测试API
 */
@SpringBootTest
class SongEsApiApplicationTests {
    @Autowired
    @Qualifier("restHighLevelClient")//去Spring中取对应的方法名
    private RestHighLevelClient client;

    //测试索引的创建Request PUT song_index
    @Test
    void testCreateIndex() throws IOException {
        //1、创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("song_index");
        //2、执行创建请求，createIndexResponse，请求后获得响应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);

    }

    //测试获取索引，判断是否存在
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest("song_index");
        boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //测试删除索引
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("song_index");
        AcknowledgedResponse delete = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    //测试添加文档
    @Test
    void testAddDocument() throws IOException {
        //创建对象
        User user = new User("宋海波", 3);
        //创建请求
        IndexRequest song_index = new IndexRequest("song_index");
        //规则put /song_index/_doc/1
        song_index.id("1");
        song_index.timeout(TimeValue.timeValueSeconds(1));
        song_index.timeout("1s");
        //将我们的数据放入请求 json
        song_index.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求
        IndexResponse index = client.index(song_index, RequestOptions.DEFAULT);
        System.out.println(index.toString());
        System.out.println(index.status());//成功状态返回CREATED
    }

    //获取文档，判断是否存在 GET /song_index/doc/1
    @Test
    void testIsExists() throws IOException {
        GetRequest song_index = new GetRequest("song_index").id("1");
        //不获取返回的_source 的上下文了 效率更高
        song_index.fetchSourceContext(new FetchSourceContext(false));
        song_index.storedFields("_none_");
        boolean exists = client.exists(song_index, RequestOptions.DEFAULT);
        System.out.println("exists = " + exists);
    }

    //获取文档的信息
    @Test
    void testGetDocument() throws IOException {
        GetRequest song_index = new GetRequest("song_index").id("1");
        GetResponse documentFields = client.get(song_index, RequestOptions.DEFAULT);
        String sourceAsString = documentFields.getSourceAsString();//打印文档内容
        System.out.println("sourceAsString = " + sourceAsString);//返回的全部内容
    }

    //更新文档信息
    @Test
    void testUpdateRequest() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("song_index", "1");
        updateRequest.timeout("1s");
        User user = new User("宋海波Java", 50);
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println("update = " + update.status());
    }

    //删除文档记录
    @Test
    void testDeleteRequest() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("song_index", "1");
        deleteRequest.timeout("1s");
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println("deleteResponse.status() = " + deleteResponse.status());

    }

    //特殊的，真的项目一般都会批量插入数据！
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("songhaibo1", 3));
        userList.add(new User("songhaibo2", 9));
        userList.add(new User("songhaibo3", 2));
        userList.add(new User("songhaibo4", 4));
        userList.add(new User("songhaibo5", 7));
        userList.add(new User("songhaibo6", 45));
        for (int i = 0; i < userList.size(); i++) {
            //批量更新和批量删除，就在这里修改对应的请求就可以了
            bulkRequest.add(
                    new IndexRequest("song_index")
                            .id("" + (i + 1))//不加ID默认随机
                            .source(JSON.toJSONString(userList.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println("bulk.hasFailures() = " + bulk.hasFailures());//是否失败 false就是没有失败
    }
    //批量查询
    // SearchRequest搜索请求
    //SearchSourceBuilder条件构造
    // HighlightBuilder构造高亮
    //TermQueryBuilder 精确查询
    //MatchAllQueryBuilder查询全部
    //xxx QueryBuilder对应kibana命令！例如TermQueryBuilder Term在kibana精确查询
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest(ESconst.ES_INDEX);
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询条件，我们可以使用QueryBuilders工具类实现

        //QueryBuilders.termQuery 精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "songhaibo1");

        // QueryBuilders.matchAllQuery()匹配所有
        //MatchAllQueryBuilder matchQueryBuilder = QueryBuilders.matchAllQuery();

        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("searchResponse.getHits() = " + JSON.toJSONString(searchResponse.getHits()));
        System.out.println("==================");
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }
}
