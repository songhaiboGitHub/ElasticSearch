package com.song.utils;

import com.song.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author songhaibo
 * @create 2020-09-23 6:06 下午
 */
@Component
public class HemlParseUtil {
    public static void main(String[] args) throws IOException {
        new HemlParseUtil().parseJD("码出高效").forEach(System.out::println);
    }

    public ArrayList<Content> parseJD(String keywords) throws IOException {
        //前提需要联网，ajax不能获取到！模拟浏览器才能获取到
        //获取请求 https://search.jd.com/Search?keyword=java
        //支持中文转义&enc=utf-8
        String url = "https://search.jd.com/Search?keyword=" + keywords+"&enc=utf-8";
        //解析网页.(Jsoup返回Document就是js页面对象)
        Document parse = Jsoup.parse(new URL(url), 30000);
        //所有在js中的方法，这里都能用
        Element element = parse.getElementById("J_goodsList");
        //System.out.println("element.html() = " + element.html());
        //获取所有的li元素
        Elements li = element.getElementsByTag("li");
        ArrayList<Content> goodList = new ArrayList<>();
        //获取元素中的内容,这里的el 就是每个element1标签了
        for (Element element1 : li) {
            //关于图片特别多的网站，所有的图片都是延迟加载的
            String img = element1.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = element1.getElementsByClass("p-price").eq(0).text();
            String title = element1.getElementsByClass("p-name").eq(0).text();
            /*System.out.println("=============");
            System.out.println("img = " + img);
            System.out.println("price = " + price);
            System.out.println("title = " + title);*/
            //爬下来的数据保存到自己的对象里
            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(title);
            goodList.add(content);
        }
        return goodList;
    }
}
