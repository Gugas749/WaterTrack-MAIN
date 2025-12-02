package com.grupok.watertrack.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Meters")
public class MeterEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nome")
    public String nome;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "userID")
    public int userID;

    @ColumnInfo(name = "meterTypeID")
    public int meterTypeID;

    @ColumnInfo(name = "enterpriseID")
    public int enterpriseID;

    @ColumnInfo(name = "classe")
    public String classe;

    @ColumnInfo(name = "instalationDate")
    public String instalationDate;

    @ColumnInfo(name = "shutdownDate")
    public String shutdownDate;

    @ColumnInfo(name = "maxCapacity")
    public String maxCapacity;

    @ColumnInfo(name = "measureUnity")
    public String measureUnity;

    @ColumnInfo(name = "supportedTemperature")
    public String supportedTemperature;
    @ColumnInfo(name = "state")
    public int state;

    public MeterEntity(String nome, String address, int userID, int meterTypeID, int enterpriseID, String classe, String instalationDate, String shutdownDate, String maxCapacity, String measureUnity, String supportedTemperature, int state) {
        this.nome = nome;
        this.address = address;
        this.userID = userID;
        this.meterTypeID = meterTypeID;
        this.enterpriseID = enterpriseID;
        this.classe = classe;
        this.instalationDate = instalationDate;
        this.shutdownDate = shutdownDate;
        this.maxCapacity = maxCapacity;
        this.measureUnity = measureUnity;
        this.supportedTemperature = supportedTemperature;
        this.state = state;
    }
    public void setId(int id){
        this.id = id;
    }
}
