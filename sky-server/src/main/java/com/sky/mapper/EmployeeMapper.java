package com.sky.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.sky.entity.Employee;

@Mapper
public interface EmployeeMapper {

    Employee getByUsername(@Param("username")String username);

    int insertSelective(Employee employee);





}