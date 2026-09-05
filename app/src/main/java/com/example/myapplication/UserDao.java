package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM users WHERE parentName = :name LIMIT 1")
    User getUserByName(String name);

    @Query("SELECT * FROM users WHERE parentPhone = :phone LIMIT 1")
    User getUserByPhone(String phone);

    @Query("SELECT * FROM users ORDER BY id DESC LIMIT 1")
    User getFirstUser();
}
