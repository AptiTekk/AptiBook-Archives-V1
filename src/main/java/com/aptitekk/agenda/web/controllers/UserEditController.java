package com.aptitekk.agenda.web.controllers;

import com.aptitekk.agenda.core.UserService;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.utilities.Sha256Helper;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.List;

@ManagedBean(name = "UserEditController")
@ViewScoped
public class UserEditController {

    @Inject
    private UserService userService;

    private User selectedUser;
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
        users = userService.getAll();
    }

    public void resetSettings() {
        if (this.selectedUser != null) {
            setEditableUsername(selectedUser.getUsername());
            setEditableFirstName(selectedUser.getFirstName());
            setEditableLastName(selectedUser.getLastName());
            setEditableEmail(selectedUser.getEmail());
            setEditablePhoneNumber(selectedUser.getPhoneNumber());
            setEditableLocation(selectedUser.getLocation());

            setNewPassword("");
            setConfirmPassword("");
        }
    }

    public void updateSettings() {
        editSuccess = false;

        if (!getNewPassword().isEmpty()) {
            if (!getConfirmPassword().equals(getNewPassword())) {
                FacesContext.getCurrentInstance().addMessage("userEditForm:passwordEdit",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Passwords do not match."));
                setNewPassword("");
                setConfirmPassword("");
            }
        }

        if (FacesContext.getCurrentInstance().getMessageList("userEditForm").isEmpty()) {
            selectedUser.setUsername(editableUsername);
            selectedUser.setFirstName(editableFirstName);
            selectedUser.setLastName(editableLastName);
            selectedUser.setEmail(editableEmail);
            selectedUser.setPhoneNumber(getEditablePhoneNumber());
            selectedUser.setLocation(editableLocation);

            FacesContext.getCurrentInstance().addMessage("userEditForm",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Personal Information Updated."));

            if (!getNewPassword().isEmpty() && FacesContext.getCurrentInstance().getMessageList("userEditForm:passwordEdit").isEmpty()) {
                selectedUser.setPassword(Sha256Helper.rawToSha(getNewPassword()));
                FacesContext.getCurrentInstance().addMessage("userEditForm:passwordEdit",
                        new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Password Changed Successfully."));
            }
            try {
                selectedUser = userService.merge(selectedUser);
                refreshUserList();
                editSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage("userEditForm",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error while updating User Settings: " + e.getMessage()));
            }

        }
    }

    public void addNewUser() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            User newUser = new User();
            newUser.setUsername("new_user");
            userService.insert(newUser);

            if (userService.get(newUser.getId()) != null) {
                context.addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "New User Added!"));
                setSelectedUser(newUser);
            } else {
                throw new Exception("User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error While Adding User!"));
        }

        refreshUserList();
    }

    public void deleteSelectedUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (userService.get(getSelectedUser().getId()) != null) {
                context.addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User Deleted!"));
                userService.delete(getSelectedUser().getId());
                setSelectedUser(null);
            } else {
                throw new Exception("User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error While Deleting User!"));
        }

        refreshUserList();
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
        resetSettings();
    }

    public List<User> getUsers() {
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
