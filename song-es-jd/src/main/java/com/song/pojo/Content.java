package com.song.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author songhaibo
 * @create 2020-09-23 6:31 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Content {
    private String title;
    private String price;
    private String img;
    //可以自己添加属性
}
