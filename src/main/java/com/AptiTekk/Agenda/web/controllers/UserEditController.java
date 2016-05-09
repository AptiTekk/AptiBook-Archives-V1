package com.AptiTekk.Agenda.web.controllers;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import com.AptiTekk.Agenda.core.UserService;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.utilities.Sha256Helper;
import java.util.List;

@ManagedBean
@ViewScoped
public class UserEditController {

    @Inject
    private UserService userService;

    private User user;
    private List<User> users;

    private String editableUsername;
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
        refreshUserList();
        resetSettings();
    }
    
    public void refreshUserList() {
        users = (List) userService.getAll();
    }

    public void resetSettings() {
        if (this.user != null) {
            setEditableUsername(user.getUsername());
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
        if(getEditableUsername().isEmpty())
        {
            FacesContext.getCurrentInstance().addMessage("userEditForm", new FacesMessage("Username cannot be empty."));
        }
        
        //TODO: Check if username already exists, check regex
        
        if (!getEditableFirstName().isEmpty() && !getEditableFirstName().matches("[A-Za-z'-]+")) {
            FacesContext.getCurrentInstance().addMessage("userEditForm",
                    new FacesMessage("First Name may only contain A-Z, a-z, apostrophe, and dash."));
        }

        if (!getEditableLastName().isEmpty() && !getEditableLastName().matches("[A-Za-z'-]+")) {
            FacesContext.getCurrentInstance().addMessage("userEditForm",
                    new FacesMessage("Last Name may only contain A-Z, a-z, apostrophe, and dash."));
        }

        if (!getEditableEmail().isEmpty() && !getEditableEmail().matches("[A-Za-z\\.\\+0-9@]+")) {
            FacesContext.getCurrentInstance().addMessage("userEditForm",
                    new FacesMessage("Invalid Email Format."));
        }

        if (!getEditablePhoneNumber().isEmpty()
                && !getEditablePhoneNumber().matches("[0-9\\- \\(\\)]+")) {
            FacesContext.getCurrentInstance().addMessage("userEditForm",
                    new FacesMessage("Invalid Phone Number Format."));
        }

        if (!getNewPassword().isEmpty()) {
            if (!getConfirmPassword().equals(getNewPassword())) {
                FacesContext.getCurrentInstance().addMessage("userEditForm",
                        new FacesMessage("Passwords do not match."));
                setNewPassword("");
                setConfirmPassword("");
            }
        }

        if (FacesContext.getCurrentInstance().getMessageList("userEditForm").isEmpty()) {
            user.setUsername(editableUsername);
            user.setFirstName(editableFirstName);
            user.setLastName(editableLastName);
            user.setEmail(editableEmail);
            user.setPhoneNumber(getEditablePhoneNumber());
            user.setLocation(editableLocation);

            FacesContext.getCurrentInstance().addMessage("userEditForm",
                    new FacesMessage("Account Settings Updated."));

            if (!getNewPassword().isEmpty()) {
                user.setPassword(Sha256Helper.rawToSha(getNewPassword()));
                FacesContext.getCurrentInstance().addMessage("userEditForm",
                        new FacesMessage("Password Changed Successfully."));
            }

            user = userService.merge(user);
            refreshUserList();

            editSuccess = true;
        }
    }
    
    public void addNewUser()
    {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            User newUser = new User();
            userService.insert(newUser);

            if (userService.get(newUser.getId()) != null) {
                context.addMessage("accountSettingsForm", new FacesMessage("Successful", "New User Added!"));
                setUser(newUser);
            } else {
                throw new Exception("User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage("accountSettingsForm", new FacesMessage("Failure", "Error While Adding User!"));
        }
        
        refreshUserList();
    }
    
    public void deleteSelectedUser()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (userService.get(getUser().getId()) != null) {
                context.addMessage("accountSettingsForm", new FacesMessage("Successful", "User Deleted!"));
                userService.delete(getUser().getId());
                setUser(null);
            } else {
                throw new Exception("User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage("accountSettingsForm", new FacesMessage("Failure", "Error While Deleting User!"));
        }
        
        refreshUserList();
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        resetSettings();
    }
    
    public List<User> getUsers()
    {
        return users;
    }
    
    public String getEditableUsername() {
        return editableUsername;
    }

    public void setEditableUsername(String editableUsername) {
        this.editableUsername = editableUsername;
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
