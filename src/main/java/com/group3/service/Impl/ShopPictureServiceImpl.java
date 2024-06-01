package com.group3.service.Impl;

import com.group3.dao.ShopPictureMapper;
import com.group3.pojo.ShopPicture;
import com.group3.service.ShopPictureService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author group3
 * @date 2024/04
 */
@Service
public class ShopPictureServiceImpl implements ShopPictureService {
    @Resource
    private ShopPictureMapper shopPictureMapper;
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return 0;
    }

    @Override
    public int insert(ShopPicture record) {
        return shopPictureMapper.insert(record);
    }

    @Override
    public int insertSelective(ShopPicture record) {
        return shopPictureMapper.insertSelective(record);
    }

    @Override
    public ShopPicture selectByPrimaryKey(Integer id) {
        return shopPictureMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(ShopPicture record) {
        return shopPictureMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(ShopPicture record) {
        return shopPictureMapper.updateByPrimaryKey(record);
    }

    @Override
    public ShopPicture selectBySidOnlyOne(Integer sid) {
        return shopPictureMapper.selectBySidOnlyOne(sid);
    }

    @Override
    public List<ShopPicture> selectBySid(Integer sid) {
        return null;
    }
}
