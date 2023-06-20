//package com.example.UNISEA.BottomSheetDialog;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//
//import com.example.UNISEA.ActivityPostActivity;
//import com.example.UNISEA.MomentsPostActivity;
//import com.example.UNISEA.R;
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
//
//public class ImageSelect extends BottomSheetDialogFragment {
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable
//            ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.image_select_dialog,
//                container, false);
//
//        TextView gallery = v.findViewById(R.id.gallery);
//        TextView camera = v.findViewById(R.id.camera);
//
//
//        gallery.setOnClickListener((View view) -> {
//            Intent intent = new Intent(getActivity(), ActivityPostActivity.class);
//            startActivity(intent);
//            dismiss();
//        });
//
//        camera.setOnClickListener((View view) -> {
//            Log.d("INPUT","NULL");
//            Intent intent = new Intent(Intent.ACTION_PICK);  //跳转到 ACTION_IMAGE_CAPTURE
//            intent.setType("image/*");
//            startActivityForResult(intent, 2);
//            dismiss();
//        });
//
//        return v;
//    }
//}
