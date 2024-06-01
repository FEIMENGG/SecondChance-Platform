package com.group3.service;

import com.group3.pojo.UserRelease;
import java.util.List;

/**
 * @author group3
 * @date 2024/04
 */
public interface UserReleaseService {
    int deleteByPrimaryKey(Integer id);

    int insert(UserRelease record);

    int insertSelective(UserRelease record);

    UserRelease selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserRelease record);

    int updateByPrimaryKey(UserRelease record);

    int getCounts(int uid);

    List<UserRelease> selectByUid(int uid, int start);

    List<UserRelease> selectUserProductByUid(int uid);
}
