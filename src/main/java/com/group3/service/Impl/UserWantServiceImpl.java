package com.group3.service.Impl;

import com.group3.dao.UserWantMapper;
import com.group3.pojo.UserWant;
import com.group3.service.UserWantService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author group3
 * @date 2024/04
 */
@Service
public class UserWantServiceImpl implements UserWantService {
    @Resource
    private UserWantMapper userWantMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return 0;
    }

    @Override
    public int insert(UserWant record) {
        return userWantMapper.insert(record);
    }

    @Override
    public int insertSelective(UserWant record) {
        return userWantMapper.insertSelective(record);
    }

    @Override
    public UserWant selectByPrimaryKey(Integer id) {
        return userWantMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(UserWant record) {
        return userWantMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(UserWant record) {
        return userWantMapper.updateByPrimaryKey(record);
    }

    @Override
    public int getCounts(int uid) {
        return userWantMapper.getCounts(uid);
    }

    @Override
    public List<UserWant> selectByUid(int uid, int start) {
        return userWantMapper.selectByUid(uid, start);
    }

    @Override
    public List<UserWant> selectMineByUid(int id) {
        return userWantMapper.selectMineByUid(id);
    }

    @Override
    public List<UserWant> selectAll() {
        return userWantMapper.selectAll();
    }
}
