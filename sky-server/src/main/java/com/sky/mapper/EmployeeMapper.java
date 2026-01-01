package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.sky.entity.Employee;

@Mapper
public interface EmployeeMapper {

    Employee getByUsername(@Param("username") String username);

    @AutoFill(value = OperationType.INSERT)
    int insert(Employee employee);

    Page<Employee> selectByNameWithPage(EmployeePageQueryDTO employeePageQueryDTO);

    @AutoFill(value = OperationType.UPDATE)
    int update(Employee employee);

    Employee selectById(@Param("id")Long id);

}