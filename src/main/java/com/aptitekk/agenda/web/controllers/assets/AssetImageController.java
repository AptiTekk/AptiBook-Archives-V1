/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.assets;

import com.aptitekk.agenda.core.entity.Asset;
import com.aptitekk.agenda.core.services.AssetService;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Pasha on 7/7/2016.
 */



@Named
@RequestScoped
public class AssetImageController implements Serializable {

    @Inject
    private AssetService assetService;

    public StreamedContent getImage(){
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        }
        else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            String assetId = context.getExternalContext().getRequestParameterMap().get("assetId");
            int assetIdInt = Integer.parseInt(assetId);
            return new DefaultStreamedContent(new ByteArrayInputStream(assetService.get(assetIdInt).getPhoto()));
        }

    }



}
