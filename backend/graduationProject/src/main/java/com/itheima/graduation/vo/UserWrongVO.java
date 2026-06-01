package com.itheima.graduation.vo;

import java.time.LocalDateTime;

public class UserWrongVO {
    private Long problemId;
    private String problemTitle;
    private Integer problemStatus;
    private Integer wrongCount;
    private LocalDateTime lastWrongTime;
    private LocalDateTime createTime;

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getProblemTitle() {
        return problemTitle;
    }

    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    public Integer getProblemStatus() {
        return problemStatus;
    }

    public void setProblemStatus(Integer problemStatus) {
        this.problemStatus = problemStatus;
    }

    public Integer getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }

    public LocalDateTime getLastWrongTime() {
        return lastWrongTime;
    }

    public void setLastWrongTime(LocalDateTime lastWrongTime) {
        this.lastWrongTime = lastWrongTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
