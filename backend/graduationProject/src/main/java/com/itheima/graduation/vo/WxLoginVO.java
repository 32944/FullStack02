package com.itheima.graduation.vo;

import lombok.Data;

@Data
public class WxLoginVO {
    private Long userId;
    private String openid;
    private String nickname;
    private String avatar;
    private String token;
    private Integer isNew;
}
