package com.group3.service;

import com.group3.pojo.BoughtShop;

import java.util.List;

/**
 * @author group3
 * @date 2024/04
 */
public interface BoughtShopService {
    int deleteByPrimaryKey(Integer id);

    int insert(BoughtShop record);

    int insertSelective(BoughtShop record);

    BoughtShop selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BoughtShop record);

    int updateByPrimaryKey(BoughtShop record);

    int getCounts(int uid);

    List<BoughtShop> selectByUid(int uid, int start);
}
