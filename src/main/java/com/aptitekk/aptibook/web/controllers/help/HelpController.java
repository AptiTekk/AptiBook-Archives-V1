/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.help;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

@Named
@ViewScoped
public class HelpController implements Serializable {

    public enum Topic {
        FRONT_PAGE(null),

        RESERVATION_MANAGEMENT_PENDING(null),
        RESERVATION_MANAGEMENT_APPROVED(null),
        RESERVATION_MANAGEMENT_REJECTED(null),
        RESERVATION_MANAGEMENT_CALENDAR(null),

        USER_SETTINGS(null),
        USER_MY_RESERVATIONS(null),
        USER_NOTIFICATIONS(null),

        SETTINGS_ASSETS(null),
        SETTINGS_ASSET_CATEGORIES(new String[]{"How To Add New Asset Categories", "add-new-asset-categories"}),
        SETTINGS_USER_GROUPS(null),
        SETTINGS_USERS(null),
        SETTINGS_PERMISSIONS(null),
        SETTINGS_PROPERTIES(null);

        /**
         * An array containing the name and Slug of the popular topics, in that order. For example:
         * ["Name 1", "Slug 1", "Name 2", "Slug 2", ...]
         * <p>
         * The Slug is found in the URL of the article.
         * For example: https://support.aptitekk.com/article/add-new-asset-categories has a slug of "add-new-asset-categories"
         */
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
     * Sets the current Topic. Should be called from the page's controller.
     *
     * @param topic The Topic.
     */
    public void setCurrentTopic(Topic topic) {
        if (topic == null || topic.getPopularTopics() == null || topic.getPopularTopics().length % 2 != 0) {
            popularTopics = null;
            return;
        }

        popularTopics = new HashMap<>();
        for (int i = 0; i < topic.getPopularTopics().length; i += 2) {
            popularTopics.put(topic.getPopularTopics()[i], topic.getPopularTopics()[i + 1]);
        }
    }

    public Set<String> getPopularTopicTitles() {
        if (popularTopics != null)
            return popularTopics.keySet();
        return null;
    }

    public String getPopularTopicURL(String title) {
        if (popularTopics != null)
            return "https://support.aptitekk.com/article/" + popularTopics.get(title);
        return null;
    }

}
