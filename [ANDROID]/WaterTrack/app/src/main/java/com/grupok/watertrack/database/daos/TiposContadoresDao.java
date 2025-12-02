package com.grupok.watertrack.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.grupok.watertrack.database.entities.MeterTypeEntity;

import java.util.List;

@Dao
public interface TiposContadoresDao {

    @Insert
    void insert(MeterTypeEntity tipoContador);

    @Insert
    void insertList(List<MeterTypeEntity> tiposContadores);

    @Update
    void update(MeterTypeEntity tipoContador);

    @Query("SELECT * FROM MeterTypes")
    List<MeterTypeEntity> getTiposContadores();

    @Query("DELETE FROM MeterTypes")
    void clearAllEntries();
}
