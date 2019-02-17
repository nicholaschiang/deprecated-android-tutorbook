/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.nicholaschiang.tutorbook;

import android.content.Context;
import android.text.TextUtils;

import io.github.nicholaschiang.tutorbook.model.User;
import com.google.firebase.firestore.Query;

/**
 * Object for passing filters around.
 */
public class Filters {

    private String grade = null;
    private String subject = null;
    private String gender = null;
    private String type = null;
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public Filters() {}

    public static Filters getDefault() {
        Filters filters = new Filters();
        filters.setSortBy(User.FIELD_AVG_RATING);
        filters.setSortDirection(Query.Direction.DESCENDING);

        return filters;
    }

    public boolean hasGrade() {
        return !(TextUtils.isEmpty(grade));
    }

    public boolean hasSubjects() {
        return !(TextUtils.isEmpty(subject));
    }

    public boolean hasGender() {
        return !(TextUtils.isEmpty(gender));
    }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public boolean hasType() {
        return !(TextUtils.isEmpty(type));
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (grade == null && subject == null && gender == null) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_users));
            desc.append("</b>");
        }

        if (grade != null) {
            desc.append("<b>");
            desc.append(grade);
            desc.append("</b>");
        }

        if (grade != null && subject != null && gender != null) {
            desc.append(" in ");
        }

        if (subject != null) {
            desc.append("<b>");
            desc.append(subject);
            desc.append("</b>");
        }

        if (gender != null) {
            desc.append("<b>");
            desc.append(gender);
            desc.append("</b>");
        }

        return desc.toString();
    }

    public String getOrderDescription(Context context) {
        if (User.FIELD_POPULARITY.equals(sortBy)) {
            return context.getString(R.string.sorted_by_popularity);
        } else {
            return context.getString(R.string.sorted_by_rating);
        }
    }
}
