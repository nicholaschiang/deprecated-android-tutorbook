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
package io.github.nicholaschiang.tutorbook.util;

import android.content.Context;

import io.github.nicholaschiang.tutorbook.R;
import io.github.nicholaschiang.tutorbook.model.User;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Utilities for Restaurants.
 */
public class UserUtil {

    private static final String TAG = "UserUtil";

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private static final String RESTAURANT_URL_FMT = "https://storage.googleapis.com/firestorequickstarts.appspot.com/food_%d.png";

    private static final int MAX_IMAGE_NUM = 22;

    private static final String[] NAME_FIRST_WORDS = {
            "Nicholas",
            "Brandon",
            "Alex",
            "Jack",
            "Catherine",
            "Sophia",
            "Jacquelyn",
            "Katy",
            "Julia",
    };

    private static final String[] NAME_SECOND_WORDS = {
            "Chiang",
            "Xu",
            "Jacobsen",
            "Chang",
            "Ballantyne",
            "Fester",
            "Cox",
            "Samson",
    };

    // Top 10 email providers according to https://www.ecloudbuzz.com/best-free-email-service-providers/
    private static final String[] EMAILS = {
            "gmail.com",
            "yahoo.com",
            "funmail.org",
            "government.gov",
            "outlook.com",
            "aol.mail",
            "zoho.mail",
            "mail.com",
            "yandex.com",
            "protonmail.com",
            "gmx.com",
            "iCloud.com"
    };


    /**
     * Create a random Restaurant POJO.
     */
    public static User getRandom(Context context) {
        User user = new User();
        Random random = new Random();

        // Grades (first element is 'Any')
        String[] grades = context.getResources().getStringArray(R.array.grades);
        grades = Arrays.copyOfRange(grades, 1, grades.length);

        // Subjects (first element is 'Any')
        String[] subjects = context.getResources().getStringArray(R.array.subjects);
        subjects = Arrays.copyOfRange(subjects, 1, subjects.length);

        // Genders (first element is 'Any')
        String[] genders = context.getResources().getStringArray(R.array.genders);
        genders = Arrays.copyOfRange(genders, 1, genders.length);

        // User types (first element is 'Any')
        String[] types = context.getResources().getStringArray(R.array.types);
        types = Arrays.copyOfRange(types, 1, types.length);

        user.setName(getRandomName(random));
        user.setGrade(getRandomGrade(9, 12, random));
        user.setNeededStudies(getRandomString(subjects, random));
        user.setProficientStudies(getRandomString(subjects, random));
        user.setGender(getRandomString(genders, random));
        user.setType(getRandomString(types, random));
        user.setPhoto(getRandomImageUrl(random));
        user.setAvgRating(getRandomRating(random));
        user.setNumRatings(random.nextInt(20));
        user.setEmail(getRandomEmail(random));
        user.setPhone("(650)324-9189");

        return user;
    }


    /**
     * Get a random image.
     */
    private static String getRandomImageUrl(Random random) {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        int id = random.nextInt(MAX_IMAGE_NUM) + 1;

        return String.format(Locale.getDefault(), RESTAURANT_URL_FMT, id);
    }

    private static int getRandomGrade(int min, int max, Random random) {
        // Taken from https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
        return random.nextInt((max - min) + 1) + min;
    }

    private static double getRandomRating(Random random) {
        double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }

    private static String getRandomName(Random random) {
        return getRandomString(NAME_FIRST_WORDS, random) + " "
                + getRandomString(NAME_SECOND_WORDS, random);
    }

    private static String getRandomEmail(Random random) {
        return getRandomString(NAME_FIRST_WORDS, random).toLowerCase() + "."
                + getRandomString(NAME_SECOND_WORDS, random).toLowerCase() + "@"
                + getRandomString(EMAILS, random);
    }

    private static String getRandomString(String[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    private static int getRandomInt(int[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

}
