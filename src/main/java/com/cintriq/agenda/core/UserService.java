package com.cintriq.agenda.core;

import javax.ejb.Local;

import com.cintriq.agenda.core.entity.User;

@Local
public interface UserService extends EntityService<User> {
  
  public final static String ADMIN_USERNAME = "admin";
  public final static String DEFAULT_ADMIN_PASSWORD = "admin";
  
  public final static String SESSION_VAR_USERNAME = "username";
  
  /**
   * Finds User Entity by its username
   * 
   * @param username
   * 
   * @return User where table.username = username
   */
  User findByName(String username);
  
  /**
   * Determines if the credentials are correct or not.
   * 
   * @param username The username of the user to check.
   * @param password The password of the user to check (raw).
   * @return The User if the credentials are correct, or null if they are not.
   */
  User correctCredentials(String username, String password);
  
}
