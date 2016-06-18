package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.UserService;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.utilities.AgendaLogger;
import com.aptitekk.agenda.core.utilities.BanHelper;
import com.aptitekk.agenda.core.utilities.FacesSessionHelper;
import com.aptitekk.agenda.core.utilities.Sha256Helper;
import com.aptitekk.agenda.web.AuthenticationFilter;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;

@ManagedBean(name = "AccountController")
@ViewScoped
public class AccountController {

    private final static String BAN_NAME = "Login";

    @Inject
    private UserService userService;

    private String username;
    private String password;

    /**
     * Whether or not the user is currently banned (for attempting to login too
     * many times)
     */
    private boolean isBanned = BanHelper.isUserBanned(BAN_NAME);

    private User user;

    private String editableFirstName;
    private String editableLastName;
    private String editableEmail;
    private String editablePhoneNumber;
    private String editableLocation;
    private String newPassword;
    private String confirmPassword;

    private boolean editSuccess;

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        this.username = externalContext.getRequestHeaderMap().get("REMOTE_USER");

        String loggedInUser
                = FacesSessionHelper.getSessionVariableAsString(UserService.SESSION_VAR_USERNAME);
        if (loggedInUser != null) {
            this.setUser(userService.findByName(loggedInUser));
        }

        resetSettings();
    }

    /**
     * Attempts to log the user in with the credentials they have input.
     *
     * @return The outcome page.
     */
    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        AgendaLogger.logVerbose("Logging In - User: " + username);

        User authenticatedUser = userService.correctCredentials(username, password);

        username = null;
        password = null;

        if (authenticatedUser == null) // Invalid Credentials
        {
            context.addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Login Failed: Incorrect Credentials."));
            BanHelper.recordFailedAttempt(BAN_NAME);
            if (BanHelper.getNumberFailedAttempts(BAN_NAME) >= 3) {
                BanHelper.banUser(BAN_NAME, 6);
                setBanned(true);
            }
            return null;
        } else {
            AgendaLogger.logVerbose("Login Successful");
            FacesSessionHelper.setSessionVariable(UserService.SESSION_VAR_USERNAME,
                    authenticatedUser.getUsername());
            BanHelper.unBanUser(BAN_NAME);
            BanHelper.clearFailedAttempts(BAN_NAME);
            String originalUrl
                    = FacesSessionHelper.getSessionVariableAsString(AuthenticationFilter.SESSION_ORIGINAL_URL);
            if (originalUrl != null) {
                FacesSessionHelper.removeSessionVariable(AuthenticationFilter.SESSION_ORIGINAL_URL);
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(originalUrl);
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "index";
    }

    public String logout() {
        AgendaLogger.logVerbose("Logging Out");

        FacesSessionHelper.removeSessionVariable(UserService.SESSION_VAR_USERNAME);
        return "index";
    }

    public void redirectIfLoggedIn() throws IOException {
        if (user != null) {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

            externalContext.redirect(externalContext.getRequestContextPath() + "/secure/index.xhtml");
        }
    }

    public void resetSettings() {
        if (this.user != null) {
            setEditableFirstName(user.getFirstName());
            setEditableLastName(user.getLastName());
            setEditableEmail(user.getEmail());
            setEditablePhoneNumber(user.getPhoneNumber());
            setEditableLocation(user.getLocation());

            setNewPassword("");
            setConfirmPassword("");
        }
    }

    public void updateSettings() {
        editSuccess = false;
        if (!getEditableFirstName().isEmpty() && !getEditableFirstName().matches("[A-Za-z'-]+")) {
            FacesContext.getCurrentInstance().addMessage("accountSettingsForm",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "First Name may only contain A-Z, a-z, apostrophe, and dash."));
        }

        if (!getEditableLastName().isEmpty() && !getEditableLastName().matches("[A-Za-z'-]+")) {
            FacesContext.getCurrentInstance().addMessage("accountSettingsForm",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Last Name may only contain A-Z, a-z, apostrophe, and dash."));
        }

        if (!getEditableEmail().isEmpty() && !getEditableEmail().matches("[A-Za-z\\.\\+0-9@]+")) {
            FacesContext.getCurrentInstance().addMessage("accountSettingsForm",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Invalid Email Format."));
        }

        if (!getEditablePhoneNumber().isEmpty()
                && !getEditablePhoneNumber().matches("[0-9\\- \\(\\)]+")) {
            FacesContext.getCurrentInstance().addMessage("accountSettingsForm",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Invalid Phone Number Format."));
        }

        if (!getNewPassword().isEmpty()) {
            if (!getConfirmPassword().equals(getNewPassword())) {
                FacesContext.getCurrentInstance().addMessage("accountSettingsForm",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Passwords do not match."));
                setNewPassword("");
                setConfirmPassword("");
            }
        }

        if (FacesContext.getCurrentInstance().getMessageList("accountSettingsForm").isEmpty()) {
            user.setFirstName(editableFirstName);
            user.setLastName(editableLastName);
            user.setEmail(editableEmail);
            user.setPhoneNumber(getEditablePhoneNumber());
            user.setLocation(editableLocation);

            FacesContext.getCurrentInstance().addMessage("accountSettingsForm",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Account Settings Updated."));

            if (!getNewPassword().isEmpty()) {
                user.setPassword(Sha256Helper.rawToSha(getNewPassword()));
                FacesContext.getCurrentInstance().addMessage("accountSettingsForm",
                        new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Password Changed Successfully."));
            }
            try {
                user = userService.merge(user);
            } catch (Exception e) {
                e.printStackTrace();
            }

            editSuccess = true;
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        resetSettings();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getBanned() {
        return isBanned;
    }

    public void setBanned(boolean isBanned) {
        this.isBanned = isBanned;
    }

    public String getEditableFirstName() {
        return editableFirstName;
    }

    public void setEditableFirstName(String editableFirstName) {
        this.editableFirstName = editableFirstName;
    }

    public String getEditableLastName() {
        return editableLastName;
    }

    public void setEditableLastName(String editableLastName) {
        this.editableLastName = editableLastName;
    }

    public String getEditableEmail() {
        return editableEmail;
    }

    public void setEditableEmail(String editableEmail) {
        this.editableEmail = editableEmail;
    }

    public String getEditablePhoneNumber() {
        return editablePhoneNumber;
    }

    public void setEditablePhoneNumber(String editablePhoneNumber) {
        this.editablePhoneNumber = editablePhoneNumber;
    }

    public String getEditableLocation() {
        return editableLocation;
    }

    public void setEditableLocation(String editableLocation) {
        this.editableLocation = editableLocation;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isEditSuccess() {
        return editSuccess;
    }

}
