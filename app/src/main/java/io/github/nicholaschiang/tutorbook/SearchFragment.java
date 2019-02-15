package io.github.nicholaschiang.tutorbook;

import android.content.Intent;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Collections;

import io.github.nicholaschiang.tutorbook.adapter.UserAdapter;
import io.github.nicholaschiang.tutorbook.model.Filters;
import io.github.nicholaschiang.tutorbook.model.User;
import io.github.nicholaschiang.tutorbook.util.UserUtil;
import io.github.nicholaschiang.tutorbook.viewmodel.SearchFragmentViewModel;


public class SearchFragment extends Fragment implements
        FilterDialogFragment.FilterListener,
        UserAdapter.OnUserSelectedListener {

    // Finals
    private static final String TAG = "SearchFragment";
    private static final int RC_SIGN_IN = 9001;
    private static final int LIMIT = 50;

    // Views
    private View mRootView;
    private TextView mCurrentSearchView;
    private TextView mCurrentSortByView;
    private RecyclerView mUsersRecycler;
    private ViewGroup mEmptyView;

    // Buttons
    private CardView mFilterBar;
    private AppCompatImageView mButtonClearFilter;

    // Firebase
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    // Other
    private FilterDialogFragment mFilterDialog;
    private UserAdapter mAdapter;
    private SearchFragmentViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initial view
        mRootView = inflater.inflate(R.layout.fragment_search, container, false);

        // Views
        mUsersRecycler = mRootView.findViewById(R.id.recycler_users);
        mCurrentSearchView = mRootView.findViewById(R.id.text_current_search);
        mCurrentSortByView = mRootView.findViewById(R.id.text_current_sort_by);
        mEmptyView = mRootView.findViewById(R.id.view_empty);

        // Buttons
        mFilterBar = mRootView.findViewById(R.id.filter_bar);
        mButtonClearFilter = mRootView.findViewById(R.id.button_clear_filter);

        // View model
        mViewModel = ViewModelProviders.of(this).get(SearchFragmentViewModel.class);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Initialize Firestore and the main RecyclerView
        initFirestore();
        initRecyclerView();

        // Filter Dialog
        mFilterDialog = new FilterDialogFragment();

        // Set listeners
        mFilterBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.filter_bar)
                    onFilterClicked();
            }
        });
        mButtonClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button_clear_filter)
                    onClearFilterClicked();
            }
        });

        // Return final view
        return mRootView;
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();

        // Get the 50 highest rated users
        mQuery = mFirestore.collection("users")
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new UserAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mUsersRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mUsersRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(mRootView.findViewById(R.id.filter_bar_container),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mUsersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsersRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Apply filters
        onFilter(mViewModel.getFilters());

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onFilter(Filters filters) {
        // Construct query basic query
        Query query = mFirestore.collection("users");

        // Subjects (equality filter)
        if (filters.hasSubjects()) {
            query = query.whereEqualTo("subject", filters.getSubject());
        }

        // Grade (equality filter)
        if (filters.hasGrade()) {
            query = query.whereEqualTo("grade", filters.getGrade());
        }

        // Gender (equality filter)
        if (filters.hasGender()) {
            query = query.whereEqualTo("gender", filters.getGender());
        }

        // User type (equality filter)
        if (filters.hasType()) {
            query = query.whereEqualTo("type", filters.getType());
        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        // Limit items
        query = query.limit(LIMIT);

        // Update the query
        mQuery = query;
        mAdapter.setQuery(query);

        // Set header
        mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(getContext())));
        mCurrentSortByView.setText(filters.getOrderDescription(getContext()));

        // Save filters
        mViewModel.setFilters(filters);
    }

    public void onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog.show(getChildFragmentManager(), FilterDialogFragment.TAG);
    }

    public void onClearFilterClicked() {
        mFilterDialog.resetFilters();

        onFilter(Filters.getDefault());
    }

    @Override
    public void onUserSelected(DocumentSnapshot user) {
        // Go to the details page for the selected user
        Intent intent = new Intent(getContext(), UserDetailActivity.class);
        intent.putExtra(UserDetailActivity.KEY_USER_ID, user.getId());

        startActivity(intent);
    }
}

