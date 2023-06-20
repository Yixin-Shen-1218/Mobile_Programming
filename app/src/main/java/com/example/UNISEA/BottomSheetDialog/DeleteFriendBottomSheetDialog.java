package com.example.UNISEA.BottomSheetDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.UNISEA.MainActivity;
import com.example.UNISEA.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteFriendBottomSheetDialog extends BottomSheetDialogFragment {

    String uid;
    String friendId;

    public DeleteFriendBottomSheetDialog(String uid, String friendId) {
        this.uid = uid;
        this.friendId = friendId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.friend_delete_bottomsheet,
                container, false);

        TextView deleteText = v.findViewById(R.id.delete);
        deleteText.setOnClickListener((View view) -> deleteFriend());

        return v;
    }

    private void deleteFriend() {
        FirebaseDatabase.getInstance().getReference("Friends").child(this.uid).child(this.friendId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       deleteOnFriendSide();
                    }
                });
    }

    private void deleteOnFriendSide() {
        FirebaseDatabase.getInstance().getReference("Friends").child(this.friendId).child(this.uid).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Delete success!",Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("id", 2);
                        startActivity(intent);
                    }
                });
    }
}
