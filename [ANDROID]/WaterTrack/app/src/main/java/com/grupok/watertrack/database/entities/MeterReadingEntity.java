package com.grupok.watertrack.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MeterReadings")
public class MeterReadingEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "tecnicoID")
    public int tecnicoID;

    @ColumnInfo(name = "meterID")
    public int meterID;

    @ColumnInfo(name = "reading")
    public String reading;

    @ColumnInfo(name = "accumulatedConsumption")
    public String accumulatedConsumption;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "waterPressure")
    public String waterPressure;

    public MeterReadingEntity(int tecnicoID, int meterID, String reading, String accumulatedConsumption, String date, String waterPressure) {
        this.tecnicoID = tecnicoID;
        this.meterID = meterID;
        this.reading = reading;
        this.accumulatedConsumption = accumulatedConsumption;
        this.date = date;
        this.waterPressure = waterPressure;
    }

    public void setId(int id) {
        this.id = id;
    }
}
