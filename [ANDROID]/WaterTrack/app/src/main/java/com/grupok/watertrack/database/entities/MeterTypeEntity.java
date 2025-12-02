package com.grupok.watertrack.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MeterTypes")
public class MeterTypeEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "description")
    public String description;

    public MeterTypeEntity(String description) {
        this.description = description;
    }
    public void setId(int id){
        this.id= id;
    }
}
