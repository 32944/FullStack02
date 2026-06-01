package com.itheima.graduation.mapper;

import com.itheima.graduation.entity.CodeQuestion;
import com.itheima.graduation.entity.TestCase;
import com.itheima.graduation.entity.UserRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CodeQuestionMapper {

    CodeQuestion selectById(@Param("id") Long id);

    List<CodeQuestion> selectAll();

    List<TestCase> selectTestCasesByQuestionId(@Param("questionId") Integer questionId);

    int insertUserRecord(UserRecord userRecord);

    List<UserRecord> selectUserRecordsByOpenidAndQuestionId(@Param("openid") String openid, @Param("questionId") Integer questionId);

    UserRecord selectLatestUserRecord(@Param("openid") String openid, @Param("questionId") Integer questionId);
}
