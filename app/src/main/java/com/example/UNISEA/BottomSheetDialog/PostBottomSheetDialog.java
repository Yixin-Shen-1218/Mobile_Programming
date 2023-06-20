package com.example.UNISEA.BottomSheetDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.UNISEA.ActivityPostActivity;
import com.example.UNISEA.MomentsPostActivity;
import com.example.UNISEA.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PostBottomSheetDialog extends BottomSheetDialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout,
                container, false);

        TextView activityText = v.findViewById(R.id.activity);
        TextView momentsText = v.findViewById(R.id.moments);


        activityText.setOnClickListener((View view) -> {
            Log.d("test", "activity text");
            Intent intent = new Intent(getActivity(), ActivityPostActivity.class);
            startActivity(intent);
            dismiss();
        });

        momentsText.setOnClickListener((View view) -> {
            Log.d("test", "moments text");
            Intent intent = new Intent(getActivity(), MomentsPostActivity.class);
            startActivity(intent);
            dismiss();
        });

        return v;
    }
}
