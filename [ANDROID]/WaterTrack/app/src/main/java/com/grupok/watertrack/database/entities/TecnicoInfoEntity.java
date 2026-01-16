package com.grupok.watertrack.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Tecnico_Info")
public class TecnicoInfoEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "userID")
    public int userID;

    @ColumnInfo(name = "enterpriseID")
    public int enterpriseID;

    @ColumnInfo(name = "profissionalCertificateNumber")
    public String profissionalCertificateNumber;

    public TecnicoInfoEntity(int userID, int enterpriseID, String profissionalCertificateNumber) {
        this.userID = userID;
        this.enterpriseID = enterpriseID;
        this.profissionalCertificateNumber = profissionalCertificateNumber;
    }
}
