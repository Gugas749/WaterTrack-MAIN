package com.grupok.watertrack.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.grupok.watertrack.database.daos.AvariasContadoresDao;
import com.grupok.watertrack.database.daos.MeterDao;
import com.grupok.watertrack.database.daos.EmpresasDao;
import com.grupok.watertrack.database.daos.MeterReadingDao;
import com.grupok.watertrack.database.daos.TiposContadoresDao;
import com.grupok.watertrack.database.daos.UserInfosDao;
import com.grupok.watertrack.database.entities.AvariasContadoresEntity;
import com.grupok.watertrack.database.entities.EnterpriseEntity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.MeterReadingEntity;
import com.grupok.watertrack.database.entities.MeterTypeEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;

@Database(entities = {UserInfosEntity.class, MeterEntity.class, MeterReadingEntity.class, AvariasContadoresEntity.class, EnterpriseEntity.class, MeterTypeEntity.class}, version = 2)
public abstract class LocalDataBase extends RoomDatabase {
    public abstract UserInfosDao userInfosDao();
    public abstract MeterDao contadoresDao();
    public abstract MeterReadingDao logsContadoresDao();
    public abstract AvariasContadoresDao avariasContadoresDao();
    public abstract EmpresasDao empresasDao();
    public abstract TiposContadoresDao tiposContadoresDao();
    public static LocalDataBase INSTANCE;
    public static LocalDataBase getInstance(Context context){
        if(INSTANCE==null)
        {
            INSTANCE= Room.databaseBuilder(context, LocalDataBase.class,"WaterTrackLocalDB")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return INSTANCE;
    }
}
