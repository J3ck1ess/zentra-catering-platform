package com.zentra.server.mapper;

import com.zentra.server.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Mapper for Employee table operations
 */
@Mapper
public interface EmployeeMapper {

    /**
     * Query employee by username
     * @param username login username
     * @return Employee entity
     */
    @Select("SELECT * FROM employee WHERE username = #{username}")
    Employee findByUsername(String username);
}
