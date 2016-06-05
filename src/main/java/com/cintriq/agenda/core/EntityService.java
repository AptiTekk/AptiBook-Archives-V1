package com.cintriq.agenda.core;

import java.util.List;
import javax.ejb.Local;

@Local
public interface EntityService<T> {
  
  T get(int id);
  List<T> getAll();
  void insert(T o);
  void update(T newData, int id);
  void delete(int id);
  T merge(T entity);

}
