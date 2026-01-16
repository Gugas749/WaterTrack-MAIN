package com.grupok.watertrack.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Reports")
public class ReportsEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "meterID")
    public int meterID;

    @ColumnInfo(name = "userID")
    public int userID;

    @ColumnInfo(name = "tecnicoID")
    public int tecnicoID;

    @ColumnInfo(name = "problemState")
    public int problemState;

    @ColumnInfo(name = "description")
    public String description;

    public ReportsEntity(int meterID, int userID, int tecnicoID, int problemState, String description) {
        this.meterID = meterID;
        this.userID = userID;
        this.tecnicoID = tecnicoID;
        this.problemState = problemState;
        this.description = description;
    }

    public void setId(int id){
        this.id = id;
    }
}
