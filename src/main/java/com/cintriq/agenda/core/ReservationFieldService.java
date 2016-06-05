package com.cintriq.agenda.core;

import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.ReservationField;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ReservationFieldService extends EntityService<ReservationField> {

    List<ReservationField> getByType(AssetType type);

}
