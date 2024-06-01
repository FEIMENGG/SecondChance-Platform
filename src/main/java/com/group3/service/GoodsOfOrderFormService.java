package com.group3.service;

import com.group3.pojo.GoodsOfOrderForm;

import java.util.List;

/**
 * @author group3
 * @date 2024/04
 */
public interface GoodsOfOrderFormService {
    int deleteByPrimaryKey(Integer id);

    int insert(GoodsOfOrderForm record);

    int insertSelective(GoodsOfOrderForm record);

    GoodsOfOrderForm selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GoodsOfOrderForm record);

    int updateByPrimaryKey(GoodsOfOrderForm record);

    List<GoodsOfOrderForm> selectByOFid(int ofid);
}
