/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.help;

import com.aptitekk.aptibook.core.util.LogManager;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

@Named
@ViewScoped
public class HelpController implements Serializable {

    public enum Topic {
        ASSET_CATEGORIES(new String[]{"How To Add New Asset Categories", "https://support.aptitekk.com/article/add-new-asset-categories/"});

        private String[] popularTopics;

        Topic(String[] topics) {
            this.popularTopics = topics;
        }

        public String[] getPopularTopics() {
            return popularTopics;
        }
    }

    /**
     * A Map of the popular popularTopics for the current page.
     * Key: Topic Title
     * Value: Topic URL
     */
    private HashMap<String, String> popularTopics;

    /**
     * Sets the current Page ID. Should be called from the page itself on load.
     *
     * @param pageId The Page ID. This is the name of the topic enum value. Case-insensitive.
     */
    public void setCurrentPageId(String pageId) {
        if (pageId == null) {
            popularTopics = null;
        } else {
            try {
                Topic topic = Topic.valueOf(pageId.toUpperCase());

                if (topic.getPopularTopics().length % 2 != 0) {
                    setCurrentPageId(null);
                } else {
                    popularTopics = new HashMap<>();
                    for (int i = 0; i < topic.getPopularTopics().length; i += 2) {
                        popularTopics.put(topic.getPopularTopics()[i], topic.getPopularTopics()[i + 1]);
                    }
                }

            } catch (IllegalArgumentException e) {
                setCurrentPageId(null);
            }
        }
    }

    public Set<String> getPopularTopicTitles() {
        if (popularTopics != null)
            return popularTopics.keySet();
        return null;
    }

    public String getPopularTopicURL(String title) {
        if (popularTopics != null)
            return popularTopics.get(title);
        return null;
    }

}
