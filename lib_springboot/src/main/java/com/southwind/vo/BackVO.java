package com.southwind.vo;

import lombok.Data;

@Data
public class BackVO {
    private Integer id;
    private String userName;
    private String bookName;
    private String author;
    private String publish;
    private String edition;
    private Integer status;
    private String imagePath; // 添加 imagePath 属性
}
