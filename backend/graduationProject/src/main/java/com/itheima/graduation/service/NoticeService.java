package com.itheima.graduation.service;

import com.itheima.graduation.entity.Notice;

import java.util.List;

public interface NoticeService {
    List<Notice> getActiveNotices();
}
