package com.itheima.graduation.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BaseVO {
    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
