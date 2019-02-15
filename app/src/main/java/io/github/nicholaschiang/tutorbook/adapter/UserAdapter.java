package io.github.nicholaschiang.tutorbook.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import com.bumptech.glide.Glide;

import io.github.nicholaschiang.tutorbook.R;
import io.github.nicholaschiang.tutorbook.adapter.FirestoreAdapter;
import io.github.nicholaschiang.tutorbook.model.User;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a list of Users.
 */
public class UserAdapter extends FirestoreAdapter<UserAdapter.ViewHolder> {

    public interface OnUserSelectedListener {

        void onUserSelected(DocumentSnapshot user);

    }

    private OnUserSelectedListener mListener;

    public UserAdapter(Query query, OnUserSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        // Views
        ImageView imageView;
        TextView nameView;
        MaterialRatingBar ratingBar;
        TextView numRatingsView;
        TextView typeView;
        TextView gradeView;
        TextView subjectsView;

        public ViewHolder(View itemView) {
            super(itemView);

            // Views
            imageView = itemView.findViewById(R.id.user_item_image);
            nameView = itemView.findViewById(R.id.user_item_name);
            ratingBar = itemView.findViewById(R.id.user_item_rating);
            numRatingsView = itemView.findViewById(R.id.user_item_num_ratings);
            typeView = itemView.findViewById(R.id.user_item_type);
            gradeView = itemView.findViewById(R.id.user_item_grade);
            subjectsView = itemView.findViewById(R.id.user_item_subjects);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnUserSelectedListener listener) {

            User user = snapshot.toObject(User.class);
            Resources resources = itemView.getResources();

            // Load image
            Glide.with(imageView.getContext())
                    .load(user.getPhoto())
                    .into(imageView);

            // Set basic info (i.e. username, ratings, grade, type)
            nameView.setText(user.getName());
            ratingBar.setRating((float) user.getAvgRating());
            typeView.setText(user.getType());
            gradeView.setText(user.getGradeString());

            // Show appropriate subject data
            if (user.getType().equals("TUTOR")) {
                subjectsView.setText(user.getProficientStudies());
            }
            else if (user.getType().equals("PUPIL")) {
                subjectsView.setText(user.getProficientStudies());
            }
            else {
                // If there is no specific type, show only needed data
                // TODO: what do we want our default to be? TUTOR or PUPIL?
                user.setType("PUPIL");
                subjectsView.setText(user.getNeededStudies());
            }
            numRatingsView.setText(resources.getString(R.string.fmt_num_ratings,
                    user.getNumRatings()));

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onUserSelected(snapshot);
                    }
                }
            });
        }

    }
}
