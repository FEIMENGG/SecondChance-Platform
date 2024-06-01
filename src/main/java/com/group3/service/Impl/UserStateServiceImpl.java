package com.group3.service.Impl;

import com.group3.dao.UserStateMapper;
import com.group3.pojo.UserState;
import com.group3.service.UserStateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author group3
 * @date 2024/04
 */
@Service
public class UserStateServiceImpl implements UserStateService {
    @Resource
    private UserStateMapper userStateMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return 0;
    }

    @Override
    public int insert(UserState record) {
        return userStateMapper.insert(record);
    }

    @Override
    public int insertSelective(UserState record) {
        return userStateMapper.insertSelective(record);
    }

    @Override
    public UserState selectByPrimaryKey(Integer id) {
        return userStateMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(UserState record) {
        return userStateMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(UserState record) {
        return userStateMapper.updateByPrimaryKey(record);
    }

    @Override
    public UserState selectByUid(int uid) {
        return userStateMapper.selectByUid(uid);
    }
}
