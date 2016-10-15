/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.resources;

import com.aptitekk.aptibook.core.domain.entities.UserGroup;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

import javax.servlet.http.Part;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ResourceFieldSupplier {

    @Size(max = 32, message = "This may only be 32 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    String resourceName;

    boolean reservationApprovalRequired;

    UserGroup resourceOwnerGroup;
    TreeNode tree;

    Part image;
    String fileName;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public boolean isReservationApprovalRequired() {
        return reservationApprovalRequired;
    }

    public void setReservationApprovalRequired(boolean reservationApprovalRequired) {
        this.reservationApprovalRequired = reservationApprovalRequired;
    }

    public TreeNode getTree() {
        return tree;
    }

    public void setTree(TreeNode tree) {
        this.tree = tree;
    }

    public void onOwnerSelected(NodeSelectEvent event) {
        if (event.getTreeNode() != null && event.getTreeNode().getData() != null && event.getTreeNode().getData() instanceof UserGroup)
            this.resourceOwnerGroup = (UserGroup) event.getTreeNode().getData();
    }

    public Part getImage() {
        return image;
    }

    public void setImage(Part image) {
        if (image == null)
            return;
        this.image = image;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Called upon an image file being chosen by the user.
     */
    public void onFileChosen() {
        if (image != null)
            this.fileName = image.getSubmittedFileName();
    }

}
