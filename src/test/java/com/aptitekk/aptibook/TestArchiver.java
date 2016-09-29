/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.File;

public class TestArchiver {
    private static final File RESOURCES = new File("src/main/resources");
    private static final File WEB = new File("web");
    private static final File WEB_INF = new File(WEB, "WEB-INF");

    public static WARArchive buildArchive() {
        try {
            // ---------------------------- ShrinkWrap WAR Generation ----------------------------
            WARArchive deployment = ShrinkWrap.create(WARArchive.class);

            //Add all classes
            deployment.addPackages(true, "com.aptitekk.aptibook");

            //Add all resources
            recursiveAddAsClassesResource(deployment, RESOURCES);

            //Add all web files
            recursiveAddAsWebResource(deployment, WEB);

            //Add all Maven dependencies
            deployment.addAllDependencies();
            // ---------------------------- END ShrinkWrap WAR Generation ------------------------

            return deployment;
        } catch (Exception e) {
            return null;
        }
    }

    private static void recursiveAddAsClassesResource(WARArchive deployment, File root) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory())
                    recursiveAddAsClassesResource(deployment, file);
                else {
                    if (file.getPath().startsWith(RESOURCES.getPath())) {
                        String filePath = file.getPath().substring(RESOURCES.getPath().length() + 1).replaceAll("\\\\", "/");
                        deployment.addAsWebInfResource(new ClassLoaderAsset(filePath, Main.class.getClassLoader()), "classes/" + filePath);
                    }
                }
            }
        }
    }

    private static void recursiveAddAsWebResource(WARArchive deployment, File root) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory())
                    recursiveAddAsWebResource(deployment, file);
                else {
                    if (file.getPath().startsWith(WEB_INF.getPath())) {
                        if (file.getName().equals("web.xml")) {
                            deployment.setWebXML(file);
                        } else {
                            String filePath = file.getPath().substring(WEB_INF.getPath().length() + 1).replaceAll("\\\\", "/");
                            deployment.addAsWebInfResource(file, filePath);
                        }
                    } else if (file.getPath().startsWith(WEB.getPath())) {
                        String filePath = file.getPath().substring(WEB.getPath().length() + 1).replaceAll("\\\\", "/");
                        deployment.addAsWebResource(file, filePath);
                    }
                }
            }
        }
    }

}
