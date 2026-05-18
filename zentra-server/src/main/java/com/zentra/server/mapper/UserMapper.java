package com.zentra.server.mapper;

import com.zentra.server.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface fo user operations
 */
@Mapper
public interface UserMapper {

    /**
     * Insert user
     */
    int insert(User user);

    /**
     * Find user by username
     */
    User findByUsername(
            @Param("username") String username,
            @Param("merchantId") Long merchantId
    );

    /**
     * Find user by id
     */
    User findById(
            @Param("id") Long id,
            @Param("merchantId") Long merchantId
    );

    /**
     * Find user by username
     */
    User findByUsernameOnly(
            @Param("username") String username
    );

    /**
     * Query user list with pagination
     */
    List<User> findPage(

            @Param("username") String username,
            @Param("status") Integer status,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    /**
     * Count users
     */
    Long count(

            @Param("username") String username,
            @Param("status") Integer status
    );

    /**
     * Find user by id for admin management
     */
    User findByIdOnly(
            @Param("id") Long id
    );

    /**
     * Update user
     */
    int update(User user);

}
