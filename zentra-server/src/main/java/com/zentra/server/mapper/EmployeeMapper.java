package com.zentra.server.mapper;

import com.zentra.server.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Mapper interface for Employee table operations
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

    /**
     * Query employee by id
     * @param id employee id
     * @return Employee entity
     */
    @Select("SELECT * FROM employee WHERE id = #{id}")
    Employee findById(Long id);

    /**
     * Query all employees
     * @return Array of Employee entities
     */
    @Select("SELECT * FROM employee")
    List<Employee> findAll();

    /**
     * Insert employee
     * @param employee employee entity
     */
    @Insert("INSERT INTO employee (merchant_id, username, password, name, role, status) " +
            "VALUES (#{merchantId}, #{username}, #{password}, #{name}, #{role}, #{status})")
    void insert(Employee employee);
}
