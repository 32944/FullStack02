package com.itheima.graduation.service.impl;

import com.itheima.graduation.entity.Notice;
import com.itheima.graduation.mapper.NoticeMapper;
import com.itheima.graduation.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public List<Notice> getActiveNotices() {
        return noticeMapper.selectByStatus(1);
    }
}
