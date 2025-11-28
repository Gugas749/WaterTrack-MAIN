package com.grupok.watertrack.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.grupok.watertrack.database.entities.UserInfosEntity;

import java.util.List;

@Dao
public interface UserInfosDao {
    @Insert
    long insert(UserInfosEntity user);

    @Update
    void update(UserInfosEntity user);
    @Insert
    void insertList(List<UserInfosEntity> users);

    @Query("SELECT * FROM User_Infos")
    List<UserInfosEntity> getUserInfos();

    @Query("SELECT * FROM User_Infos WHERE email = :userEmail")
    UserInfosEntity getUser(String userEmail);

    @Query("DELETE FROM User_Infos")
    void clearAllEntries();
}

