package com.aptitekk.agenda.core.services;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.store.DataStoreFactory;

import javax.ejb.Local;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Local
public interface GoogleService {

    String APP_NAME = "Agenda";

    String INITIAL_PATH = "/CalendarServlet";
    String CALLBACK_PATH = "/CalendarCallbackServlet";

    String ACCESS_TOKEN_PROPERTY = "";
    String CLIENT_ID_PROPERTY = "457451349060-uo39jdu88fbm4ib0r7l6jrba3k3uflem.apps.googleusercontent.com";
    String CLIENT_SECRET_PROPERTY = "YNNu0ompxxjoZRZVRvK3GPhY";

    Credential authorize(String userId) throws IOException;

    AuthorizationCodeFlow initializeCalendarFlow() throws IOException;

    String getCalendarRedirectUri(HttpServletRequest req) throws ServletException, IOException;

    String getCalendarUserId(HttpServletRequest req) throws ServletException, IOException;

    DataStoreFactory getDataStoreFactory() throws IOException;

}
