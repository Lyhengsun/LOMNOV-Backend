package com.kshrd.lumnov.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.kshrd.lumnov.model.dto.request.AppUserRequest;
import com.kshrd.lumnov.model.entity.AppUser;

@Mapper
public interface AppUserRepository {
  @Results(id = "appUserMapper", value = {
      @Result(property = "appUserId", column = "app_user_id"),
      @Result(property = "fullName", column = "full_name"),
      @Result(property = "dateOfBirth", column = "date_of_birth"),
      @Result(property = "phoneNumber", column = "phone_number"),
      @Result(property = "isVerified", column = "is_verified"),
      @Result(property = "avatarUrl", column = "avatar_url"),
      @Result(property = "deviceToken", column = "device_token"),
      @Result(property = "role", column = "role_id", one = @One(select = "getRoleById"))
  })
  @Select("""
    SELECT * FROM app_users WHERE email = #{email};
    """)
  public AppUser getUserByEmail(@Param("email") String email);

  @Select("""
      SELECT name FROM roles WHERE role_id = #{role_id};
      """)
  public String getRoleById(@Param("role_id") Long roleId);

  @ResultMap("appUserMapper")
  @Select("""
      INSERT INTO app_users (
        full_name,
        gender,
        date_of_birth,
        occupation,
        phone_number,
        email,
        password,
        avatar_url,
        emergency_contact,
        device_token,
        role_id
      ) VALUES
      (
        #{req.fullName},
        #{req.gender},
        #{req.dateOfBirth},
        #{req.occupation},
        #{req.phoneNumber},
        #{req.email},
        #{req.password},
        #{req.avatarUrl},
        #{req.emergencyContact},
        #{req.deviceToken},
        #{req.roleId}
      )
      RETURNING *;
      """)
  public AppUser registerUser(@Param("req") AppUserRequest request);
}