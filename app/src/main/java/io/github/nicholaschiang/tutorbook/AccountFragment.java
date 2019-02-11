package io.github.nicholaschiang.tutorbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class AccountFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TempTag";

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

    // Only load the image once
    private boolean loadProfilePic = true;

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
        View v = inflater.inflate(R.layout.account_fragment, container, false);

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
        data.put("needed_studies", needed_subjects);
        data.put("proficient_studies", proficient_subjects);

        // Write data to Firestore database
        mFirestore.collection("users").document(mFirebaseUser.getEmail().toString())
            .set(data)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
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
            if (loadProfilePic)
                new DownloadImageTask(profileImage)
                        .execute(mFirebaseUser.getPhotoUrl().toString());
            loadProfilePic = false;
        }
        else
            profileImage.setColorFilter(getResources().getColor(R.color.colorPrimary));

    }

    // Class to set a given ImageView from given image URL (i.e. profile picture here)
    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        private DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}