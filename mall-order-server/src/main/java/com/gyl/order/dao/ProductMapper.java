package com.gyl.order.dao;

import com.gyl.order.dto.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> getList(@Param("orderBy") String orderBy,
                          @Param("categoryId") Integer categoryId,
                          @Param("keyword") String keyword,
                          @Param("offset") Integer offset,
                          @Param("pageSize") Integer pageSize);

    int batchUpdate(@Param("list") List<Integer> list, @Param("sellStatus") Integer sellStatus);

    List<Product> selectByPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Integer selectCount();
}