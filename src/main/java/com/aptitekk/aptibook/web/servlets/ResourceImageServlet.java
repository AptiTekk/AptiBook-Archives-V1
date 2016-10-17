/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.servlets;

import com.aptitekk.aptibook.core.domain.entities.Resource;
import com.aptitekk.aptibook.core.domain.services.ResourceService;

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

@WebServlet("/images/resources/*")
public class ResourceImageServlet extends HttpServlet {

    @Inject
    private ResourceService resourceService;

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
        String resourceIdString = request.getPathInfo().substring(1);

        try {
            int resourceId = Integer.parseInt(resourceIdString);
            Resource resource = resourceService.get(resourceId);
            respondWithResourceImage(response, resource);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            respondWithResourceImage(response, null);
        }
    }

    private void respondWithResourceImage(HttpServletResponse response, Resource resource) throws IOException {
        if (response == null)
            return;

        if (resource == null || resource.getImage() == null) {
            response.setHeader("Content-Type", "image/jpeg");
            response.setHeader("Content-Length", String.valueOf(imageNotFoundFile.length()));
            response.setHeader("Content-Disposition", "inline; filename=\"notfound.jpg\"");
            Files.copy(imageNotFoundFile.toPath(), response.getOutputStream());
        } else {
            response.setHeader("Content-Type", "image/jpeg");
            response.setHeader("Content-Length", String.valueOf(resource.getImage().getData().length));
            response.setHeader("Content-Disposition", "inline; filename=\"" + resource.getId() + ".jpg\"");
            response.getOutputStream().write(resource.getImage().getData());
        }
    }

}