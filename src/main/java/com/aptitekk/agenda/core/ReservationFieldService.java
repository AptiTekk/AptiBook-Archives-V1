package com.aptitekk.agenda.core;

import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.ReservationField;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ReservationFieldService extends EntityService<ReservationField> {

    List<ReservationField> getByType(AssetType type);

}
