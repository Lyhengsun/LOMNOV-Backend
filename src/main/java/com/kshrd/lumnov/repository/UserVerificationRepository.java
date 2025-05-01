package com.kshrd.lumnov.repository;

import com.kshrd.lumnov.model.entity.UserVerification;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserVerificationRepository {

    @Select("SELECT * FROM user_verifications WHERE user_id = #{userId}")
    @Results(id="emailMapper",value = {
            @Result(property = "expireDateTime",column = "expiry_date_time"),
            @Result(property = "verification",column = "verified_code"),
            @Result(property = "userId",column = "user_id")
    })
    public UserVerification getUserVerifyById(Integer userId);

    @Insert("INSERT INTO user_verifications(expiry_date_time,verified_code,user_id) VALUES(#{e.expireDateTime},#{e.verification},#{e.userId})")
    public void insertUserVerify(@Param("e") UserVerification userVerification);

    @Delete("DELETE FROM user_verifications WHERE user_id = #{userId}")
    void deleteOTPCodeById(Integer userId);
}
