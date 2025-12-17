package com.grupok.watertrack.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MeterReadings")
public class MeterReadingEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "userID")
    public int userID;

    @ColumnInfo(name = "meterID")
    public int meterID;

    @ColumnInfo(name = "problemID")
    public int problemID;

    @ColumnInfo(name = "reading")
    public String reading;

    @ColumnInfo(name = "accumulatedConsumption")
    public String accumulatedConsumption;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "waterPressure")
    public String waterPressure;

    @ColumnInfo(name = "desc")
    public String desc;

    @ColumnInfo(name = "readingType")
    public int readingType;

    @ColumnInfo(name = "problemState")
    public int problemState;

    public MeterReadingEntity(int userID, int meterID, int problemID, String reading, String accumulatedConsumption, String date, String waterPressure, String desc, int readingType, int problemState) {
        this.userID = userID;
        this.meterID = meterID;
        this.problemID = problemID;
        this.reading = reading;
        this.accumulatedConsumption = accumulatedConsumption;
        this.date = date;
        this.waterPressure = waterPressure;
        this.desc = desc;
        this.readingType = readingType;
        this.problemState = problemState;
    }

    public void setId(int id) {this.id = id;}
}
