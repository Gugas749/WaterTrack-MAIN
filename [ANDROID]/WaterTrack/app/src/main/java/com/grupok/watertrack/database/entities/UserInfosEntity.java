package com.grupok.watertrack.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


import java.util.Date;
import java.util.Locale;

@Entity(tableName = "User_Infos")
public class UserInfosEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "userId")
    public int userId;
    @ColumnInfo(name = "username")
    public String username;
    @ColumnInfo(name = "email")
    public String email;
    @ColumnInfo(name = "status")
    public int status;
    // PROFILE INFO
    @ColumnInfo(name = "birthDate")
    public String birthDate;
    @ColumnInfo(name = "address")
    public String address;
    //TECH INFO
    @ColumnInfo(name = "enterpriseID")
    public int enterpriseID;
    @ColumnInfo(name = "profissionalCertificateNumber")
    public String certificationNumber;
    // ANDROID EXTRAS
    @ColumnInfo(name = "theme")
    public String Theme;
    @ColumnInfo(name = "language")
    public String Language;
    @ColumnInfo(name = "cargo")
    public int cargo;

    public UserInfosEntity(int userId, String username, String email, int status) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.status = status;
    }

    public void setProfileInfo(String birthDate, String address){
        this.birthDate = birthDate;
        this.address = address;
    }
    public void setTechInfo(int enterpriseID, String certificationNumber){
        this.enterpriseID = enterpriseID;
        this.certificationNumber = certificationNumber;
    }
}