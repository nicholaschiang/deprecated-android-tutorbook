package io.github.nicholaschiang.tutorbook;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import io.github.nicholaschiang.tutorbook.model.User;
import io.github.nicholaschiang.tutorbook.model.Rating;
import io.github.nicholaschiang.tutorbook.adapter.RatingAdapter;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class UserDetailActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot>, RatingDialogFragment.RatingListener {

    private static final String TAG = "UserDetail";

    public static final String KEY_USER_ID = "key_user_id";

    // Views
    ImageView mImageView;
    TextView mNameView;
    MaterialRatingBar mRatingIndicator;
    TextView mNumRatingsView;
    TextView mSubjectsView;
    TextView mGradeView;
    TextView mTypeView;

    // Profile views
    TextView mEmailView;
    TextView mGenderView;
    TextView mPhoneView;

    // Empty view and ratings recycler
    ViewGroup mEmptyView;
    RecyclerView mRatingsRecycler;

    // Buttons
    ImageView mButtonBack;
    ImageView mButtonMakeRating;

    private RatingDialogFragment mRatingDialog;

    private FirebaseFirestore mFirestore;
    private DocumentReference mUserRef;
    private ListenerRegistration mUserRegistration;

    private RatingAdapter mRatingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        // Views
        mImageView = findViewById(R.id.user_image);
        mNameView = findViewById(R.id.user_name);
        mRatingIndicator = findViewById(R.id.user_rating);
        mNumRatingsView = findViewById(R.id.user_num_ratings);
        mSubjectsView = findViewById(R.id.user_subjects);
        mGradeView = findViewById(R.id.user_grade);
        mTypeView = findViewById(R.id.user_type);

        // Profile views
        mEmailView = findViewById(R.id.user_email);
        mPhoneView = findViewById(R.id.user_phone);
        mGenderView = findViewById(R.id.user_gender);

        // Empty view and ratings recycler
        mEmptyView = findViewById(R.id.view_empty_ratings);
        mRatingsRecycler = findViewById(R.id.recycler_ratings);

        // Buttons
        mButtonBack = findViewById(R.id.user_button_back);
        mButtonMakeRating = findViewById(R.id.fab_show_rating_dialog);

        // Set listeners
        mButtonBack.setClickable(true);
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackArrowClicked(v);
            }
        });
        mButtonMakeRating.setClickable(true);
        mButtonMakeRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddRatingClicked(v);
            }
        });

        // Get user ID from extras
        String userId = getIntent().getExtras().getString(KEY_USER_ID);
        if (userId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_USER_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mUserRef = mFirestore.collection("users").document(userId);

        // Get 50 most recent ratings
        Query ratingsQuery = mUserRef
                .collection("ratings")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);

        // RecyclerView
        mRatingAdapter = new RatingAdapter(ratingsQuery) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mRatingsRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRatingsRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };

        mRatingsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRatingsRecycler.setAdapter(mRatingAdapter);

        mRatingDialog = new RatingDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        mRatingAdapter.startListening();
        mUserRegistration = mUserRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mRatingAdapter.stopListening();

        if (mUserRegistration != null) {
            mUserRegistration.remove();
            mUserRegistration = null;
        }
    }

    private Task<Void> addRating(final DocumentReference restaurantRef, final Rating rating) {
        // TODO(developer): Implement
        return Tasks.forException(new Exception("not yet implemented"));
    }

    /**
     * Listener for the Restaurant document ({@link #mUserRegistration}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        onUserLoaded(snapshot.toObject(User.class));
    }

    private void onUserLoaded(User user) {

        // Set basic info (i.e. username, ratings, grade, type)
        mNameView.setText(user.getName());
        mRatingIndicator.setRating((float) user.getAvgRating());
        mNumRatingsView.setText(user.getType());
        mGradeView.setText(user.getGradeString());

        // Set profile info (i.e. email, phone number, gender)
        mEmailView.setText(user.getEmail());
        mPhoneView.setText(user.getPhone());
        mGenderView.setText(user.getGender());

        // Show appropriate subject data
        if (user.getType().equals("TUTOR")) {
            mSubjectsView.setText(user.getProficientStudies());
        }
        else if (user.getType().equals("PUPIL")) {
            mSubjectsView.setText(user.getProficientStudies());
        }
        else {
            // If there is no specific type, show only needed data
            // TODO: what do we want our default to be? TUTOR or PUPIL?
            user.setType("PUPIL");
            mSubjectsView.setText(user.getNeededStudies());
        }
        mNumRatingsView.setText(getString(R.string.fmt_num_ratings,
                user.getNumRatings()));

        // Background image
        Glide.with(mImageView.getContext())
                .load(user.getPhoto())
                .into(mImageView);
    }

    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    public void onAddRatingClicked(View view) {
        mRatingDialog.show(getSupportFragmentManager(), RatingDialogFragment.TAG);
    }

    @Override
    public void onRating(Rating rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(mUserRef, rating)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Rating added");

                        // Hide keyboard and scroll to top
                        hideKeyboard();
                        mRatingsRecycler.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add rating failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(findViewById(android.R.id.content), "Failed to add rating",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}