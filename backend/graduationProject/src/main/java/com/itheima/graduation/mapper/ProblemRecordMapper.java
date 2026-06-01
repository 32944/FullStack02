package com.itheima.graduation.mapper;

import com.itheima.graduation.entity.ProblemRecord;
import com.itheima.graduation.vo.UserWrongVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProblemRecordMapper {
    int insert(ProblemRecord record);
    
    int update(ProblemRecord record);
    
    ProblemRecord selectByUserIdAndProblemId(@Param("userId") Long userId, @Param("problemId") Long problemId);
    
    List<Long> selectProblemIdsByUserAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    List<ProblemRecord> selectByUserId(@Param("userId") Long userId);
    
    List<ProblemRecord> selectByUserIdOrderByTime(@Param("userId") Long userId);

    List<UserWrongVO> selectUserWrongDetails(@Param("userId") Long userId);
}
