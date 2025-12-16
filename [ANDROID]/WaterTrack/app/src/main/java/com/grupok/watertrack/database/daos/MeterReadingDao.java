package com.grupok.watertrack.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.grupok.watertrack.database.entities.MeterReadingEntity;

import java.util.List;

@Dao
public interface MeterReadingDao {
    @Insert
    void insert(MeterReadingEntity logsContador);

    @Update
    void update(MeterReadingEntity logsContador);
    @Insert
    void insertList(List<MeterReadingEntity> logsContadores);

    @Query("SELECT * FROM MeterReadings")
    List<MeterReadingEntity> getLogsContadores();

    @Query("DELETE FROM MeterReadings")
    void clearAllEntries();
}

