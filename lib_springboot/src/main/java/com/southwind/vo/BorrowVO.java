package com.southwind.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowVO {
    private Integer id;
    private String bookName;
    private String sortName;
    private String author;
    private String publish;
    private String edition;
    private LocalDateTime startTime;
    private Integer status = 0;
    private String imagePath; // 添加 imagePath 属性
}
