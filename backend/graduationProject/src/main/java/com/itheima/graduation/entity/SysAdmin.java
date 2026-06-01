package com.itheima.graduation.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysAdmin extends BaseEntity {
    private Integer id;
    private String username;
    private String password;
    private Integer status;
}
