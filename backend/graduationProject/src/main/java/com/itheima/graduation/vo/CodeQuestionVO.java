package com.itheima.graduation.vo;

import java.util.List;

public class CodeQuestionVO {
    private Integer id;
    private String title;
    private String content;
    private String codeTemplate;
    private Integer difficulty;
    private List<TestCaseVO> testCases;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCodeTemplate() {
        return codeTemplate;
    }

    public void setCodeTemplate(String codeTemplate) {
        this.codeTemplate = codeTemplate;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public List<TestCaseVO> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCaseVO> testCases) {
        this.testCases = testCases;
    }

    public static class TestCaseVO {
        private Integer id;
        private String stdin;
        private String stdout;
        private Integer isExample;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
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
}
