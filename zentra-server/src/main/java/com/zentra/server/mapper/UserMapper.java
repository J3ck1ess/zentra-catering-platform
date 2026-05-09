package com.zentra.server.mapper;

import com.zentra.server.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

}
