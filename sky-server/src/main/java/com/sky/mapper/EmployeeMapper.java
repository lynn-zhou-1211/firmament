package com.sky.mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.sky.entity.Employee;

@Mapper
public interface EmployeeMapper {

    Employee getByUsername(@Param("username") String username);

    int insertSelective(Employee employee);

    Page<Employee> selectByNameWithPage(EmployeePageQueryDTO employeePageQueryDTO);

    int updateById(Employee employee);


}