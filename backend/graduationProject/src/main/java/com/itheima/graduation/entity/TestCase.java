package com.itheima.graduation.entity;

public class TestCase {
    private Integer id;
    private Integer questionId;
    private String stdin;
    private String stdout;
    private Integer isExample;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getStdin() {
        return stdin;
    }

    public void setStdin(String stdin) {
        this.stdin = stdin;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public Integer getIsExample() {
        return isExample;
    }

    public void setIsExample(Integer isExample) {
        this.isExample = isExample;
    }
}
