package com.grupok.watertrack.scripts.localDBCRUD;

import com.grupok.watertrack.database.entities.AvariasContadoresEntity;
import com.grupok.watertrack.database.entities.EnterpriseEntity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.MeterReadingEntity;
import com.grupok.watertrack.database.entities.MeterTypeEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;

import java.util.List;

public class LocalDBgetAll {
    public List<MeterReadingEntity> logsContEntityList;
    public List<MeterEntity> contadoresEntityList;
    public List<AvariasContadoresEntity> avariasContadoresEntityList;
    public List<EnterpriseEntity> enterpriseEntityList;
    public List<MeterTypeEntity> meterTypeEntityList;
    public UserInfosEntity userInfo;

    public LocalDBgetAll(List<MeterReadingEntity> logsContEntityList, List<MeterEntity> contadoresEntityList, List<AvariasContadoresEntity> avariasContadoresEntityList, List<EnterpriseEntity> enterpriseEntityList, List<MeterTypeEntity> meterTypeEntityList, UserInfosEntity userInfo) {
        this.logsContEntityList = logsContEntityList;
        this.contadoresEntityList = contadoresEntityList;
        this.avariasContadoresEntityList = avariasContadoresEntityList;
        this.enterpriseEntityList = enterpriseEntityList;
        this.meterTypeEntityList = meterTypeEntityList;
        this.userInfo = userInfo;
    }
}
