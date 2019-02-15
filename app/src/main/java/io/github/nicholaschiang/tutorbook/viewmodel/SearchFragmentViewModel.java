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
package io.github.nicholaschiang.tutorbook.viewmodel;

import android.arch.lifecycle.ViewModel;

import io.github.nicholaschiang.tutorbook.model.Filters;

/**
 * ViewModel for {@link io.github.nicholaschiang.tutorbook.SearchFragment}.
 */

public class SearchFragmentViewModel extends ViewModel {

    private Filters mFilters;

    public SearchFragmentViewModel() {
        mFilters = Filters.getDefault();
    }

    public Filters getFilters() {
        return mFilters;
    }

    public void setFilters(Filters mFilters) {
        this.mFilters = mFilters;
    }
}
