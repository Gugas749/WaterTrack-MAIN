package com.grupok.watertrack.scripts.localDBCRUD;

import com.grupok.watertrack.database.entities.AvariasContadoresEntity;
import com.grupok.watertrack.database.entities.EnterpriseEntity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.LogsContadoresEntity;
import com.grupok.watertrack.database.entities.MeterTypeEntity;
import com.grupok.watertrack.database.entities.TecnicoInfoEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;

import java.util.List;

public class LocalDBgetAll {
    public List<LogsContadoresEntity> logsContEntityList;
    public List<MeterEntity> contadoresEntityList;
    public List<AvariasContadoresEntity> avariasContadoresEntityList;
    public List<EnterpriseEntity> enterpriseEntityList;
    public List<TecnicoInfoEntity> tecnicoInfoEntityList;
    public List<MeterTypeEntity> meterTypeEntityList;
    public UserInfosEntity userInfo;

    public LocalDBgetAll(List<LogsContadoresEntity> logsContEntityList, List<MeterEntity> contadoresEntityList, List<AvariasContadoresEntity> avariasContadoresEntityList, List<EnterpriseEntity> enterpriseEntityList, List<TecnicoInfoEntity> tecnicoInfoEntityList, List<MeterTypeEntity> meterTypeEntityList, UserInfosEntity userInfo) {
        this.logsContEntityList = logsContEntityList;
        this.contadoresEntityList = contadoresEntityList;
        this.avariasContadoresEntityList = avariasContadoresEntityList;
        this.enterpriseEntityList = enterpriseEntityList;
        this.tecnicoInfoEntityList = tecnicoInfoEntityList;
        this.meterTypeEntityList = meterTypeEntityList;
        this.userInfo = userInfo;
    }
}
