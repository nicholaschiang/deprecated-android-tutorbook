package io.github.nicholaschiang.tutorbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AccountFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AccountFragment";

    // Buttons
    private Button mUpdateButton;

    // Views
    private EditText mProfileDescription;
    private EditText mPhoneView;
    private EditText mInputEmailView;
    private View mRootView;
    private TextView mHeaderView;
    private TextView mEmailView;

    // Spinners (TODO: Right now we only support two spinners for each subject)
    private Spinner mNeededSubjects1;
    private Spinner mNeededSubjects2;
    private Spinner mProficientSubjects1;
    private Spinner mProficientSubjects2;
    private Spinner mType;
    private Spinner mGrade;
    private Spinner mGender;

    // Firebase
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private FirebaseFirestore mFirestore;

    private static final String ANONYMOUS = "anonymous";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase variables
        initFirebaseAuth();
        initFirestore();

    }

    private void initFirebaseAuth() {
        // Get the current user and set mUsername to their DisplayName
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            mUsername = mFirebaseUser.getDisplayName();
        }
        else {
            mUsername = ANONYMOUS;
        }
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_account, container, false);

        // Buttons
        mUpdateButton = mRootView.findViewById(R.id.action_update_profile);

        // Views
        mHeaderView = mRootView.findViewById(R.id.account_title);
        mEmailView = mRootView.findViewById(R.id.account_email_text);

        // Editable Views
        mProfileDescription = mRootView.findViewById(R.id.account_description);
        mPhoneView = mRootView.findViewById(R.id.account_phone);
        mInputEmailView = mRootView.findViewById(R.id.account_email);

        // Subject spinners
        mNeededSubjects1 = mRootView.findViewById(R.id.account_spinner_needed_subjects1);
        mNeededSubjects2 = mRootView.findViewById(R.id.account_spinner_needed_subjects2);
        mProficientSubjects1 = mRootView.findViewById(R.id.account_spinner_proficient_subjects1);
        mProficientSubjects2 = mRootView.findViewById(R.id.account_spinner_proficient_subjects2);

        // Basic info spinners
        mType = mRootView.findViewById(R.id.account_spinner_type);
        mGrade = mRootView.findViewById(R.id.account_spinner_grade);
        mGender = mRootView.findViewById(R.id.account_spinner_gender);

        // Set click listeners
        mUpdateButton.setOnClickListener(this);

        // Restore previous data
        setProfile(mRootView);

        // Return the final view for use by MainActivity
        return mRootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_update_profile:
                updateProfile();
                break;
        }
    }

    private void updateProfile() {

        // Views
        String description = mProfileDescription.getText().toString();
        String phone = mPhoneView.getText().toString();
        String custom_email = mInputEmailView.getText().toString();

        // Spinners
        // TODO: Right now we're only using the first spinners for input
        String needed_subjects = mNeededSubjects1.getSelectedItem().toString();
        String proficient_subjects = mProficientSubjects1.getSelectedItem().toString();
        String type = mType.getSelectedItem().toString();
        String gradeString = mGrade.getSelectedItem().toString();
        String gender = mGender.getSelectedItem().toString();

        // Convert gradeString to int grade
        int grade = 9; // TODO: What should are default grade be?
        switch (gradeString) {
            case "Freshman":
                grade = 9;
                break;
            case "Sophomore":
                grade = 10;
                break;
            case "Junior":
                grade = 11;
                break;
            case "Senior":
                grade = 12;
                break;
        }

        // Add document data with a hashmap (allow user to customize email)
        Map<String, Object> data = new HashMap<>();
        data.put("email", custom_email);
        data.put("name", mFirebaseUser.getDisplayName().toString());
        data.put("profile", description);
        data.put("neededStudies", needed_subjects);
        data.put("proficientStudies", proficient_subjects);
        data.put("gradeString", gradeString);
        data.put("grade", grade);
        data.put("type", type);
        data.put("gender", gender);
        data.put("phone", phone);

        // Write data to Firestore database (with user's email as document id)
        mFirestore.collection("users").document(mFirebaseUser.getEmail().toString())
            .set(data, SetOptions.merge())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");

                    // Show success message and hide keyboard
                    hideKeyboard();
                    Toast.makeText(getContext(), "Profile updated",
                            Toast.LENGTH_SHORT).show();

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);

                    // Show failure message
                    Toast.makeText(getContext(), "Failed to update profile",
                            Toast.LENGTH_SHORT).show();

                }
            });
    }

    private void setProfile(View v) {
        // Set email view from user's email (the one that they made the account with)
        if (mFirebaseUser.getEmail() != null)
            mEmailView.setText(mFirebaseUser.getEmail().toString());

        // Get the info from the user's document in Firestore database
        DocumentReference docRef = mFirestore.collection("users").document(mFirebaseUser.getEmail().toString());
        docRef
            .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            // If document exists, set the profile boxes to already written data
                            if (document.get("profile") != null)
                                mProfileDescription.setText(document.get("profile").toString());

                            // Set username and email header
                            if (document.get("name") != null)
                                mHeaderView.setText(document.get("name").toString());

                            // Set phone number and email preferences
                            if (document.get("email") != null)
                                mInputEmailView.setText(document.get("email").toString());
                            if (document.get("phone") != null)
                                mPhoneView.setText(document.get("phone").toString());


                            // Set profile picture to reflect current user's profile
                            ImageView profileImage = getView().findViewById(R.id.account_profile_image);
                            if (mFirebaseUser.getPhotoUrl() != null) {
                                // Use Google profile image
                                Glide.with(profileImage.getContext())
                                        .load(mFirebaseUser.getPhotoUrl().toString())
                                        .into(profileImage);
                            }
                            else if (document.get("photo") != null) {
                                // Use manually set profile image
                                Glide.with(profileImage.getContext())
                                        .load(document.get("photo").toString())
                                        .into(profileImage);
                            }

                            // Set correct selections for subjects
                            if (document.get("neededStudies") != null) {
                                String[] subjects = getResources().getStringArray(R.array.subjects);
                                int selection = Arrays.asList(subjects).indexOf(document.get("neededStudies").toString());
                                mNeededSubjects1.setSelection(selection);
                            }
                            if (document.get("proficientStudies") != null) {
                                String[] subjects = getResources().getStringArray(R.array.subjects);
                                int selection = Arrays.asList(subjects).indexOf(document.get("proficientStudies").toString());
                                mProficientSubjects1.setSelection(selection);
                            }

                            // Set correct selections for basic info
                            if (document.get("gradeString") != null) {
                                String[] grades = getResources().getStringArray(R.array.grades);
                                int selection = Arrays.asList(grades).indexOf(document.get("gradeString").toString());
                                mGrade.setSelection(selection);
                            }
                            if (document.get("gender") != null) {
                                String[] genders = getResources().getStringArray(R.array.genders);
                                int selection = Arrays.asList(genders).indexOf(document.get("gender").toString());
                                mGender.setSelection(selection);
                            }

                            // Set correct user type selection
                            if (document.get("type") != null) {
                                String[] types = getResources().getStringArray(R.array.types);
                                int selection = Arrays.asList(types).indexOf(document.get("type").toString());
                                mType.setSelection(selection);
                            }

                        } else {

                            // If document does not exist, log an error
                            Log.d(TAG, "No such document");

                            // Tell user sync failed
                            Toast.makeText(getActivity(), "Failed to sync profile",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        // Getting that document failed
                        Log.d(TAG, "get failed with ", task.getException());

                        // Tell user sync failed
                        Toast.makeText(getActivity(), "Failed to sync profile",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void hideKeyboard() {
        View view = (View) getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}