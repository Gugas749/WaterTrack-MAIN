package com.grupok.watertrack.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.grupok.watertrack.database.entities.MeterEntity;

import java.util.List;

@Dao
public interface MeterDao {
    @Insert
    void insert(MeterEntity contador);

    @Update
    void update(MeterEntity contador);
    @Insert
    void insertList(List<MeterEntity> contadores);

    @Query("SELECT * FROM Meters")
    List<MeterEntity> getMeters();

    @Query("DELETE FROM Meters")
    void clearAllEntries();
}

