/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/oauth")
public class OAuthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getOutputStream().write("Here are the parameters you sent me:\n".getBytes());

        Map<String, String[]> parameterMap = req.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            resp.getOutputStream().write(("# " + entry.getKey() + "\n").getBytes());

            for (String value : entry.getValue()) {
                resp.getOutputStream().write(("## " + value + "\n").getBytes());
            }
        }

        resp.getOutputStream().write("Ok Bye!".getBytes());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
