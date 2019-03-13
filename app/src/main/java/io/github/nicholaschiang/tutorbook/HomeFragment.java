package io.github.nicholaschiang.tutorbook;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class HomeFragment extends Fragment {

    // Set default username to anonymous
    private String mUsername = "Anonymous";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        // Get the current user and set mUsername to their DisplayName
        if (mFirebaseAuth.getCurrentUser() != null)
            mUsername = mFirebaseAuth.getCurrentUser().getDisplayName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // Set welcome text to reflect current user's username
        TextView welcomeText = v.findViewById(R.id.welcomeText);
        String welcomeMessage = "Welcome " + mUsername + "!";
        welcomeText.setText(welcomeMessage);

        // Return the view with the updated welcome text
        return v;
    }
}
