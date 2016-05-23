package com.AptiTekk.Agenda.core;

import com.AptiTekk.Agenda.core.entity.AssetType;
import com.AptiTekk.Agenda.core.entity.ReservationField;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ReservationFieldService extends EntityService<ReservationField> {

    List<ReservationField> getByType(AssetType type);

}
