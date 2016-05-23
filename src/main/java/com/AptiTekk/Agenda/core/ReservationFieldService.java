package com.AptiTekk.Agenda.core;

import com.AptiTekk.Agenda.core.entity.ReservableType;
import com.AptiTekk.Agenda.core.entity.ReservationField;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by kevint on 5/18/2016.
 */
@Local
public interface ReservationFieldService extends EntityService<ReservationField> {

    List<ReservationField> getByType(ReservableType type);

}
