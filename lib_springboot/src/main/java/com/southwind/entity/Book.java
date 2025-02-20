package com.southwind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 书籍实体类
 * </p>
 *
 * @author admin
 * @since 2023-03-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("book") // 映射到数据库中的表名
public class Book implements Serializable {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
  private Integer id; // 书籍 ID

  private String name; // 书籍名称

  private Integer sid; // 分类 ID

  private Integer number; // 书籍数量

  private String author; // 作者

  private String publish; // 出版社

  private String edition; // 版本

  private String imagePath; // 图片路径
}
