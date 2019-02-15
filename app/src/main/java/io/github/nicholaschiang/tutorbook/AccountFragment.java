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
import java.util.HashMap;
import java.util.Map;


public class AccountFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AccountFragment";

    // TODO: Use the annotation BindView syntax for views
    // Buttons
    private Button mUpdateButton;

    // Views
    private EditText mProfileDescription;
    private EditText mProficientSubjects;
    private EditText mNeededSubjects;

    // Firebase instance variables
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
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        // Buttons
        mUpdateButton = v.findViewById(R.id.updateProfileDescription);

        // Views
        mProfileDescription = v.findViewById(R.id.profileDescription);
        mNeededSubjects = v.findViewById(R.id.neededStudies);
        mProficientSubjects = v.findViewById(R.id.proficientStudies);

        // Set click listeners
        mUpdateButton.setOnClickListener(this);

        // Set welcome message and profile pic
        setWelcomeMessage(v);

        // Restore previously written profile info
        if (mProfileDescription.getText().toString().matches(""))
            setProfile(v);

        // Return the final view for use by MainActivity
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateProfileDescription:
                updateProfile();
                break;
        }
    }

    private void updateProfile() {
        // Get the text in profile boxes (if a box is left empty, pass "No Data")
        String description = mProfileDescription.getText().toString();
        if (description.equals(""))
            description = "No Data";
        String needed_subjects = mNeededSubjects.getText().toString();
        if (needed_subjects.equals(""))
            needed_subjects = "No Data";
        String proficient_subjects = mProficientSubjects.getText().toString();
        if (proficient_subjects.equals(""))
            proficient_subjects = "No Data";

        // Add document data using the user's email as the id with a hashmap
        Map<String, Object> data = new HashMap<>();
        data.put("email", mFirebaseUser.getEmail().toString());
        data.put("username", mFirebaseUser.getDisplayName().toString());
        data.put("profile", description);
        data.put("neededStudies", needed_subjects);
        data.put("proficientStudies", proficient_subjects);

        // Write data to Firestore database
        mFirestore.collection("users").document(mFirebaseUser.getEmail().toString())
            .set(data, SetOptions.merge())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");

                    // Show success message and hide keyboard
                    hideKeyboard();
                    Snackbar.make(getView().findViewById(R.id.updateProfileDescription), "Profile updated",
                            Snackbar.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);

                    // Show failure message
                    Snackbar.make(getView().findViewById(R.id.updateProfileDescription), "Failed to update profile",
                            Snackbar.LENGTH_SHORT).show();
                }
            });
    }

    private void setProfile(View v) {
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
                            if (document.get("neededStudies") != null)
                                mNeededSubjects.setText(document.get("neededStudies").toString());
                            if (document.get("proficientStudies") != null)
                                mProficientSubjects.setText(document.get("proficientStudies").toString());

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

    private void setWelcomeMessage(View v) {
        // Set welcome text to reflect current user's username
        TextView welcomeText = v.findViewById(R.id.welcomeText);
        String welcomeMessage = mUsername;
        welcomeText.setText(welcomeMessage);

        // Set profile picture to reflect current user's profile
        ImageView profileImage = v.findViewById(R.id.profileImage);
        if (mFirebaseUser.getPhotoUrl() != null) {
            // Profile image
            Glide.with(profileImage.getContext())
                    .load(mFirebaseUser.getPhotoUrl().toString())
                    .into(profileImage);
        }
        else
            profileImage.setColorFilter(getResources().getColor(R.color.colorPrimary));

    }

    private void hideKeyboard() {
        View view = (View) getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}