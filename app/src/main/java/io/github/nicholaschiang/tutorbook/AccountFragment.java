package io.github.nicholaschiang.tutorbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;


public class AccountFragment extends Fragment {

    // Firebase instance variables
    private FirebaseUser mFirebaseUser;
    private String mUsername;

    // Only load the image once
    private boolean loadProfilePic = true;

    private static final String ANONYMOUS = "anonymous";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.account_fragment, container, false);

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

        // Add setup view for new users
        pushFragment(new SetupFragment());

        // Return the final view for use by MainActivity
        return v;
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

    /**
     * Method to push any fragment into given id.
     *
     * @param fragment An instance of Fragment to show into the given id.
     */
    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getChildFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.rootLayout, fragment);
                ft.commit();
            }
        }
    }


}