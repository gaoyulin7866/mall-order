package com.gyl.order.dao;

import com.gyl.order.dto.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNo(@Param("orderNo") String orderNo, @Param("userId") Integer userId);
    List<Order> selectByPage(@Param("offset") Integer offset,@Param("pageSize") Integer pageSize , @Param("userId") Integer userId);

    List<Order> selectByAdminPage(@Param("offset") Integer offset,@Param("pageSize") Integer pageSize);

    Order selectByAdmin(@Param("orderNo") String orderNo);
}