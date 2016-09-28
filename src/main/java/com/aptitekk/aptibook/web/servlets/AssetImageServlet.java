/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.servlets;

import com.aptitekk.aptibook.core.domain.entities.Asset;
import com.aptitekk.aptibook.core.domain.services.AssetService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;

@WebServlet("/images/assets/*")
public class AssetImageServlet extends HttpServlet {

    @Inject
    private AssetService assetService;

    private File imageNotFoundFile;

    @Override
    public void init() {
        String imageNotFoundPath = "/resources/images/notFound.jpg";
        try {
            imageNotFoundFile = new File(getServletContext().getResource(imageNotFoundPath).toURI());
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String assetIdString = request.getPathInfo().substring(1);

        try {
            int assetId = Integer.parseInt(assetIdString);
            Asset asset = assetService.get(assetId);
            respondWithAssetImage(response, asset);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            respondWithAssetImage(response, null);
        }
    }

    private void respondWithAssetImage(HttpServletResponse response, Asset asset) throws IOException {
        if (response == null)
            return;

        if (asset == null || asset.getImage() == null) {
            response.setHeader("Content-Type", "image/jpeg");
            response.setHeader("Content-Length", String.valueOf(imageNotFoundFile.length()));
            response.setHeader("Content-Disposition", "inline; filename=\"notfound.jpg\"");
            Files.copy(imageNotFoundFile.toPath(), response.getOutputStream());
        } else {
            response.setHeader("Content-Type", "image/jpeg");
            response.setHeader("Content-Length", String.valueOf(asset.getImage().getData().length));
            response.setHeader("Content-Disposition", "inline; filename=\"" + asset.getId() + ".jpg\"");
            response.getOutputStream().write(asset.getImage().getData());
        }
    }

}