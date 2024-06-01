package com.group3.service;

import com.group3.pojo.UserWant;
import java.util.List;

/**
 * @author group3
 * @date 2024/04
 */
public interface UserWantService {
    int deleteByPrimaryKey(Integer id);

    int insert(UserWant record);

    int insertSelective(UserWant record);

    UserWant selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserWant record);

    int updateByPrimaryKey(UserWant record);

    int getCounts(int uid);

    List<UserWant> selectByUid(int uid, int start);

    List<UserWant> selectMineByUid(int id);

    List<UserWant> selectAll();
}
