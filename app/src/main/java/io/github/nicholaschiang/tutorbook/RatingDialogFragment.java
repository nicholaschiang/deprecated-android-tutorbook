package io.github.nicholaschiang.tutorbook;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import io.github.nicholaschiang.tutorbook.model.Rating;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Dialog Fragment containing rating form.
 */
public class RatingDialogFragment extends DialogFragment {

    public static final String TAG = "RatingDialog";

    // Views
    MaterialRatingBar mRatingBar;
    EditText mRatingText;

    // Buttons
    Button mSubmitButton;
    Button mCancelButton;

    interface RatingListener {

        void onRating(Rating rating);

    }

    private RatingListener mRatingListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_rating, container, false);

        // Views
        mRatingBar = view.findViewById(R.id.restaurant_form_rating);
        mRatingText = view.findViewById(R.id.restaurant_form_text);

        // Buttons
        mSubmitButton = view.findViewById(R.id.restaurant_form_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClicked(v);
            }
        });
        mCancelButton = view.findViewById(R.id.restaurant_form_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked(v);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof RatingListener) {
            mRatingListener = (RatingListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    public void onSubmitClicked(View view) {
        Rating rating = new Rating(
                FirebaseAuth.getInstance().getCurrentUser(),
                mRatingBar.getRating(),
                mRatingText.getText().toString());

        if (mRatingListener != null) {
            mRatingListener.onRating(rating);
        }

        dismiss();
    }

    public void onCancelClicked(View view) {
        dismiss();
    }
}
