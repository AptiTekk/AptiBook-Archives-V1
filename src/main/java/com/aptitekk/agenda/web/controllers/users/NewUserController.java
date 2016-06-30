package com.aptitekk.agenda.web.controllers.users;

import com.aptitekk.agenda.core.UserService;
import com.aptitekk.agenda.core.entity.User;
import com.aptitekk.agenda.core.entity.UserGroup;
import com.aptitekk.agenda.core.utilities.Sha256Helper;
import org.primefaces.model.TreeNode;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "NewUserController")
@RequestScoped
public class NewUserController {

    @Inject
    UserService userService;

    @ManagedProperty(value = "#{UserEditController}")
    private UserEditController userEditController;

    public void setUserEditController(UserEditController userEditController) {
        this.userEditController = userEditController;
    }

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[A-Za-z0-9_-]+", message = "This may only contain letters, numbers, underscores, and hyphens.")
    private String username;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String firstName;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String lastName;

    @Size(max = 64, message = "This may only be 64 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String email;

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String phoneNumber;

    @Size(max = 256, message = "This may only be 256 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    private String location;

    @Size(max = 32, message = "This may only be 32 characters long.")
    private String password;

    @Size(max = 32, message = "This may only be 32 characters long.")
    private String confirmPassword;

    private TreeNode[] userGroupNodes;

    public void addUser() {
        try {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setEmail(email);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setLocation(location);
            newUser.setPassword(Sha256Helper.rawToSha(password));
            newUser.setEnabled(true);

            if (userGroupNodes != null) {
                List<UserGroup> selectedUserGroups = new ArrayList<>();
                List<TreeNode> userGroupNodesList = Arrays.asList(userGroupNodes);
                for (TreeNode node : userGroupNodesList) {

                    //Check to see if any parents are selected, and skip if they are.
                    //No need to select both parent and child.
                    TreeNode parent = node;
                    boolean skip = false;
                    while ((parent = parent.getParent()) != null) {
                        if (userGroupNodesList.contains(parent)) {
                            skip = true;
                            break;
                        }
                    }
                    if (skip)
                        continue;

                    selectedUserGroups.add((UserGroup) node.getData());
                }

                newUser.setUserGroups(selectedUserGroups);
            }

            userService.insert(newUser);

            if (userService.get(newUser.getId()) != null) {
                FacesContext.getCurrentInstance().addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_INFO, null, "User '" + newUser.getUsername() + "' Added!"));
                if (userEditController != null) {
                    userEditController.refreshUserList();
                    userEditController.setSelectedUser(newUser);
                }
            } else {
                throw new Exception("User not found!");
            }

            resetFields();
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage("userEditForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Error While Adding User!"));
        }
    }

    private void resetFields() {
        username = null;
        firstName = null;
        lastName = null;
        email = null;
        phoneNumber = null;
        location = null;
        password = null;
        confirmPassword = null;
        userGroupNodes = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public TreeNode[] getUserGroupNodes() {
        return userGroupNodes;
    }

    public void setUserGroupNodes(TreeNode[] userGroupNodes) {
        this.userGroupNodes = userGroupNodes;
    }
}
