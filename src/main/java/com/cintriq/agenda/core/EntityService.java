package com.cintriq.agenda.core;

import javax.ejb.Local;
import java.util.List;

@Local
public interface EntityService<T> {
  
  T get(int id);
  List<T> getAll();

  void insert(T o) throws Exception;

  void update(T newData, int id) throws Exception;

  void delete(int id) throws Exception;

  T merge(T entity) throws Exception;

}
