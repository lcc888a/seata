package com.teamer.servicetcc.dao;

import com.teamer.servicetcc.entity.Tcc;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Component;

import java.util.Map;

@Mapper
@Component
public interface TccDAO {

    @Insert("insert into service_tcc (bName)  value (#{name})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    int insert(Tcc tcc);

    @Delete("delete from service_tcc where id=#{id}")
    int delete(Integer id);



}
