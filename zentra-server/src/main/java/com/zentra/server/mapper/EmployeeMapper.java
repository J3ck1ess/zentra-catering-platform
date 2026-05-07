package com.zentra.server.mapper;

import com.zentra.server.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Mapper interface for Employee table operations
 */
@Mapper
public interface EmployeeMapper {

    /**
     * Insert employee
     */
    int insert(Employee employee);

    /**
     * Query employee list with pagination
     */
    List<Employee> findPage(
            @Param("username") String username,
            @Param("status") Integer status,
            @Param("merchantId") Long merchantId,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    /**
     * Query employee by id with tenant isolation
     */
    Employee findById(
            @Param("id") Long id,
            @Param("merchantId") Long merchantId
    );

    /**
     * Query employee by username with tenant isolation
     */
    Employee findByUsername(
            @Param("username") String username,
            @Param("merchantId") Long merchantId
    );

    /**
     * Query employee by username for login
     */
    Employee findByUsernameOnly(
            @Param("username") String username
    );

    /**
     * Count employees
     */
    Long count(
            @Param("username") String username,
            @Param("status") Integer status,
            @Param("merchantId") Long merchantId
    );

    /**
     * Update employee
     */
    int update(Employee employee);

    /**
     * Delete employee by id
     */
    int deleteById(
            @Param("id") Long id,
            @Param("merchantId") Long merchantId
    );
}
