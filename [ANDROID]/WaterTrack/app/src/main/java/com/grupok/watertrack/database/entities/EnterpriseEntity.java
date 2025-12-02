package com.grupok.watertrack.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Enterprises")
public class EnterpriseEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "contactNumber")
    public String contactNumber;

    @ColumnInfo(name = "contactEmail")
    public String contactEmail;

    @ColumnInfo(name = "website")
    public String website;

    public EnterpriseEntity(String name, String address, String contactNumber, String contactEmail, String website) {
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.contactEmail = contactEmail;
        this.website = website;
    }

    public void setId(int id){
        this.id = id;
    }
}
