package neu.edu.madcourse.lingzhang.wordGame;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import neu.edu.madcourse.lingzhang.R;
import neu.edu.madcourse.lingzhang.wordGame.models.User;

import static android.content.ContentValues.TAG;

public class MainFragment extends Fragment {

    private AlertDialog mDialog;
    private static String instanceId;
    private DatabaseReference mDatabase;
    private String username = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        View newButton = rootView.findViewById(R.id.new_button);
        View continueButton = rootView.findViewById(R.id.continue_button);
        View scoreButton = rootView.findViewById(R.id.score_button);
        View aboutButton = rootView.findViewById(R.id.about_button);

        // handle new game button
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra(GameActivity.USERNAME_RESTORE, username);
                getActivity().startActivity(intent);
            }
        });

        // handle continue game button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra(GameActivity.KEY_RESTORE, true);
                intent.putExtra(GameActivity.USERNAME_RESTORE, username);
                getActivity().startActivity(intent);
            }
        });

        // handle score board button
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScoreActivity.class);
                getActivity().startActivity(intent);
            }
        });

        // handle about button
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.about_title);
                builder.setMessage(R.string.about_text);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                            }
                        });
                mDialog = builder.show();
            }
        });

        instanceId = FirebaseInstanceId.getInstance().getToken();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final View userLayout = rootView.findViewById(R.id.username_layout);
        final View menu = rootView.findViewById(R.id.menu_select_layout);
        View userButton = rootView.findViewById(R.id.button_username);
        View tryButton = rootView.findViewById(R.id.button_try);
        final TextView usernameText = rootView.findViewById(R.id.text_username);
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (child.getKey().equals(instanceId) && user.getUsername() != null && user.getUsername() != "") {
                        username = user.getUsername();
                        menu.setVisibility(View.VISIBLE);
                        userLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mDatabase.child("users").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        User user = dataSnapshot.getValue(User.class);
                        if (dataSnapshot.getKey().equals(instanceId) && user.getUsername() != null && user.getUsername() != "") {
                            menu.setVisibility(View.VISIBLE);
                            userLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "Failed to read value.", databaseError.toException());
                    }
                }
        );
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameText.getText().toString();
                User newUser = new User(username);
                System.out.println(instanceId);
                mDatabase.child("users").child(instanceId).setValue(newUser);
//                mDatabase.child("record").child(instanceId).child("username").setValue(username);
            }
        });
        tryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.setVisibility(View.VISIBLE);
                userLayout.setVisibility(View.GONE);
            }
        });


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDialog != null)
            mDialog.dismiss();
    }

}
