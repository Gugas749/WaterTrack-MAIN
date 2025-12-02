package com.grupok.watertrack.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.grupok.watertrack.database.entities.EnterpriseEntity;

import java.util.List;

@Dao
public interface EmpresasDao {

    @Insert
    void insert(EnterpriseEntity empresa);

    @Insert
    void insertList(List<EnterpriseEntity> empresas);

    @Update
    void update(EnterpriseEntity empresa);

    @Query("SELECT * FROM Enterprises")
    List<EnterpriseEntity> getEmpresas();

    @Query("DELETE FROM Enterprises")
    void clearAllEntries();
}
