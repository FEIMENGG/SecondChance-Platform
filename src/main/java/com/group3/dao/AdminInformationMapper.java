package com.group3.dao;

import com.group3.pojo.AdminInformation;

public interface AdminInformationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AdminInformation record);

    int insertSelective(AdminInformation record);

    AdminInformation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AdminInformation record);

    int updateByPrimaryKey(AdminInformation record);

    AdminInformation selectByNo(int ano);
}