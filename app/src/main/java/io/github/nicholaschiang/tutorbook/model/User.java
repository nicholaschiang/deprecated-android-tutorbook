package io.github.nicholaschiang.tutorbook.model;

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

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * User POJO.
 */
@IgnoreExtraProperties
public class User {

    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PROFICIENT_STUDIES = "proficientStudies";
    public static final String FIELD_NEEDED_STUDIES = "neededStudies";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PHONE = "phoneNumber";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_GRADE = "grade";
    public static final String FIELD_GENDER = "gender";
    public static final String FIELD_POPULARITY = "numRatings";
    public static final String FIELD_AVG_RATING = "avgRating";

    private String username;
    private String proficient_studies;
    private String needed_studies;
    private String email;
    private String phone;
    private String photo;
    private String type;
    private String gender;
    private int grade;
    private int numRatings;
    private double avgRating;

    public User() {}

    public User(String username, String type, int grade, String proficient_studies, String needed_studies, String photo,
                      String phone, String email, String gender, int numRatings, double avgRating) {
        this.username = username;
        this.proficient_studies = proficient_studies;
        this.needed_studies = needed_studies;
        this.email = email;
        this.phone = phone;
        this.photo = photo;
        this.type = type;
        this.grade = grade;
        this.gender = gender;
        this.numRatings = numRatings;
        this.avgRating = avgRating;
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public String getProficientStudies() {
        return proficient_studies;
    }

    public void setProficientStudies(String proficient_studies) {
        this.proficient_studies = proficient_studies;
    }

    public String getNeededStudies() {
        return needed_studies;
    }

    public void setNeededStudies(String needed_studies) {
        this.needed_studies = needed_studies;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getGrade() {
        return grade;
    }

    public String getGradeString() {
        if (grade == 9)
            return "Freshman";
        else if (grade == 10)
            return "Sophomore";
        else if (grade == 11)
            return "Junior";
        else if (grade == 12)
            return "Senior";
        else {
            return "Unsupported Grade";
        }
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }
}
