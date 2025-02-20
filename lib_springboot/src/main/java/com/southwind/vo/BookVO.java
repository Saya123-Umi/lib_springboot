package com.southwind.vo;

import lombok.Data;

@Data
public class BookVO {
    private Integer id;
    private String name;
    private String sname;
    private Integer number;
    private String author;
    private String publish;
    private String edition;
    private String imagePath; // 添加 imagePath 属性
}
