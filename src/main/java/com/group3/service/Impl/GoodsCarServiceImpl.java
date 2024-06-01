package com.group3.service.Impl;

import com.group3.dao.GoodsCarMapper;
import com.group3.pojo.GoodsCar;
import com.group3.service.GoodsCarService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author group3
 * @date 2024/04
 */
@Service
public class GoodsCarServiceImpl implements GoodsCarService {
    @Resource
    private GoodsCarMapper goodsCarMapper;
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return 0;
    }

    @Override
    public int insert(GoodsCar record) {
        return goodsCarMapper.insert(record);
    }

    @Override
    public int insertSelective(GoodsCar record) {
        return goodsCarMapper.insertSelective(record);
    }

    @Override
    public GoodsCar selectByPrimaryKey(Integer id) {
        return goodsCarMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(GoodsCar record) {
        return goodsCarMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(GoodsCar record) {
        return goodsCarMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<GoodsCar> selectByUid(int scid) {
        return goodsCarMapper.selectByUid(scid);
    }
}
