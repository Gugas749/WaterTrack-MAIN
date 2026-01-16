package com.grupok.watertrack.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.grupok.watertrack.database.entities.EnterpriseEntity;
import com.grupok.watertrack.database.entities.ReportsEntity;

import java.util.List;

@Dao
public interface ReportsDao {

    @Insert
    void insert(ReportsEntity report);

    @Insert
    void insertList(List<ReportsEntity> reportsList);

    @Update
    void update(ReportsEntity report);

    @Query("SELECT * FROM Reports")
    List<ReportsEntity> getReports();

    @Query("DELETE FROM Reports")
    void clearAllEntries();
}
