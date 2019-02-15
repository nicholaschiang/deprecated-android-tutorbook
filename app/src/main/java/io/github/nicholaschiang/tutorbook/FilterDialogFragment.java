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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import io.github.nicholaschiang.tutorbook.model.Filters;
import io.github.nicholaschiang.tutorbook.model.User;
import com.google.firebase.firestore.Query;

/**
 * Dialog Fragment containing filter form.
 */
public class FilterDialogFragment extends DialogFragment {

    public static final String TAG = "FilterDialog";

    interface FilterListener {

        void onFilter(Filters filters);

    }

    // Views
    private View mRootView;

    // Spinners
    private Spinner mGradeSpinner;
    private Spinner mSubjectSpinner;
    private Spinner mGenderSpinner;
    private Spinner mTypeSpinner;
    private Spinner mSortSpinner;

    // Buttons
    private Button mButtonSearch;
    private Button mButtonCancel;

    private FilterListener mFilterListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Views
        mRootView = inflater.inflate(R.layout.dialog_filters, container, false);

        // Spinners
        mGradeSpinner = mRootView.findViewById(R.id.spinner_grade);
        mSubjectSpinner = mRootView.findViewById(R.id.spinner_subjects);
        mGenderSpinner = mRootView.findViewById(R.id.spinner_gender);
        mTypeSpinner = mRootView.findViewById(R.id.spinner_type);
        mSortSpinner = mRootView.findViewById(R.id.spinner_sort);

        // Buttons
        mButtonSearch = mRootView.findViewById(R.id.button_search);
        mButtonCancel = mRootView.findViewById(R.id.button_cancel);

        // Set listeners
        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchClicked();
            }
        });
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();
            }
        });

        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }

        dismiss();
    }

    public void onCancelClicked() {
        dismiss();
    }

    @Nullable
    private String getSelectedGrade() {
        String selected = (String) mGradeSpinner.getSelectedItem();
        if (getString(R.string.value_any_grade).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedSubject() {
        String selected = (String) mSubjectSpinner.getSelectedItem();
        if (getString(R.string.value_any_subject).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedGender() {
        String selected = (String) mGenderSpinner.getSelectedItem();
        if (getString(R.string.value_any_gender).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedType() {
        String selected = (String) mTypeSpinner.getSelectedItem();
        if (getString(R.string.value_any_type).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return User.FIELD_AVG_RATING;
        } if (getString(R.string.sort_by_popularity).equals(selected)) {
            return User.FIELD_POPULARITY;
        }

        return null;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return Query.Direction.DESCENDING;
        } if (getString(R.string.sort_by_popularity).equals(selected)) {
            return Query.Direction.DESCENDING;
        }

        return null;
    }

    public void resetFilters() {
        if (mRootView != null) {
            mGradeSpinner.setSelection(0);
            mSubjectSpinner.setSelection(0);
            mTypeSpinner.setSelection(0);
            mGenderSpinner.setSelection(0);
            mSortSpinner.setSelection(0);
        }
    }

    public Filters getFilters() {
        Filters filters = new Filters();

        if (mRootView != null) {
            filters.setGrade(getSelectedGrade());
            filters.setSubject(getSelectedSubject());
            filters.setType(getSelectedType());
            filters.setGender(getSelectedGender());
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(getSortDirection());
        }

        return filters;
    }
}
