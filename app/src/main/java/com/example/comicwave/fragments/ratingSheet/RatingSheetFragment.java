package com.example.comicwave.fragments.ratingSheet;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.comicwave.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class RatingSheetFragment extends BottomSheetDialogFragment {

    private TextView ratingSheetTitle;
    private ImageView ratingSheetImage;
    private RatingBar ratingSheetRatingBar;
    private double rating;
    private Button ratingSheetButton;
    private RatingSheetViewModel mViewModel;
    public interface RatingSheetListener {
        void onRatingSubmitted(double rating);
    }
    private RatingSheetListener mListener;
    public static RatingSheetFragment newInstance() {
        return new RatingSheetFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof RatingSheetListener) {
            mListener = (RatingSheetListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement RatingSheetListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rating_sheet, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ratingSheetImage = view.findViewById(R.id.ratingSheetImage);
        ratingSheetTitle = view.findViewById(R.id.ratingSheetTitle);
        ratingSheetRatingBar = view.findViewById(R.id.ratingSheetRatingBar);
        ratingSheetButton = view.findViewById(R.id.ratingSheetButton);

        if (getArguments() != null) {
            String comicTitle = getArguments().getString("comicTitle");
            String comicImage = getArguments().getString("comicImage");
            rating = getArguments().getDouble("userRating");

            ratingSheetTitle.setText(String.format("How would you like %s?", comicTitle));
            Glide.with(view).load(comicImage)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                    .into(ratingSheetImage);
            ratingSheetRatingBar.setRating((float) rating);

        }

        ratingSheetButton.setOnClickListener(v -> {
            if (mListener != null || ratingSheetRatingBar.getRating() != 0.0) {
                ratingSheetRatingBar.setRating(ratingSheetRatingBar.getRating());
                mListener.onRatingSubmitted(ratingSheetRatingBar.getRating());
            }
            dismiss();
        });

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}