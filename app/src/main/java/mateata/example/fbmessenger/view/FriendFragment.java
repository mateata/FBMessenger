package mateata.example.fbmessenger.view;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import mateata.example.fbmessenger.R;
import mateata.example.fbmessenger.adapter.FriendListAdapter;
import mateata.example.fbmessenger.adapter.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    LinearLayout mSearchArea;
    EditText edtEmail;
    RecyclerView mRecyclerView;
    Button findBtn;

    private FirebaseUser mFirebaseUser;

    private FirebaseAuth mFirebaseAuth;

    private FirebaseDatabase mFirebaseDb;

    private DatabaseReference mFriendsDBRef;
    private DatabaseReference mUserDBRef;

    private FriendListAdapter friendListAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View friendView = inflater.inflate(R.layout.fragment_friends, container, false);

        mSearchArea = friendView.findViewById(R.id.search_area);
        edtEmail = friendView.findViewById(R.id.edtContent);
        mRecyclerView = friendView.findViewById(R.id.friendRecyclerView);
        findBtn = friendView.findViewById(R.id.findBtn);
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDb = FirebaseDatabase.getInstance();

        mFriendsDBRef = mFirebaseDb.getReference("users").child(mFirebaseUser.getUid()).child("friends");
        mUserDBRef = mFirebaseDb.getReference("users");

        addFriendListener();
        friendListAdapter = new FriendListAdapter();
        mRecyclerView.setAdapter(friendListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return friendView;
    }

    public void toggleSearchBar(){
        mSearchArea.setVisibility( mSearchArea.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE );
    }

    public void addFriend(){

        final String inputEmail = edtEmail.getText().toString();
        if ( inputEmail.isEmpty()) {
            Snackbar.make(mSearchArea, getString(R.string.prompt_email), Snackbar.LENGTH_LONG).show();
            return;
        }
        // 3. 자기 자신을 친구로 등록할 수 없기때문에 FirebaseUser의 email이 입력한 이메일과 같다면, 자기자신은 등록 불가 메세지를 띄워줍니다.
        if ( inputEmail.equals(mFirebaseUser.getEmail())) {
            Snackbar.make(mSearchArea, "자기자신은 친구로 등록할 수 없습니다. ", Snackbar.LENGTH_LONG).show();
            return;
        }
        // 3. 이메일이 정상이라면 나의 정보를 조회하여 이미등록된 친구인지를 판단하고
        mFriendsDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> friendsIteratble = dataSnapshot.getChildren();
                Iterator<DataSnapshot> friendsIterator = friendsIteratble.iterator();


                while ( friendsIterator.hasNext()) {
                    User user = friendsIterator.next().getValue(User.class);

                    if ( user.getEmail().equals(inputEmail)) {
                        Snackbar.make(mSearchArea, "이미 등록된 친구입니다.", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }

                // 4. users db에 존재 하지 않는 이메일이라면, 가입하지 않는 친구라는 메세지를 띄워주고
                mUserDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> userIterator = dataSnapshot.getChildren().iterator();
                        int userCount = (int)dataSnapshot.getChildrenCount();
                        int loopCount = 1;


                        while (userIterator.hasNext()) {
                            final User currentUser = userIterator.next().getValue(User.class);
                            if ( inputEmail.equals(currentUser.getEmail())) {
                                // 친구 등록 로직
                                // 5. users/{myuid}/friends/{someone_uid}/firebasePush/상대 정보를 등록하고     //  저의 친구에 상대방을 등록
                                mFriendsDBRef.push().setValue(currentUser, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(final DatabaseError databaseError, DatabaseReference databaseReference) {
                                        // 6. users/{someone_uid}/friends/{myuid}/상대 정보를 등록하고

                                        // 나의 정보를 가져온다
                                        mUserDBRef.child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                mUserDBRef.child(currentUser.getUid()).child("friends").push().setValue(user);
                                                Snackbar.make(mSearchArea, "친구등록이 완료되었습니다. ", Snackbar.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            } else {
                                if ( loopCount++ >= userCount ) {
                                    Snackbar.make(mSearchArea, "가입을 하지 않은 친구입니다.", Snackbar.LENGTH_LONG).show();
                                    return;
                                }
                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addFriendListener(){

        mFriendsDBRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User friend = dataSnapshot.getValue(User.class);
                drawUI(friend);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void drawUI(User friend){
        friendListAdapter.addItem(friend);

    }

}
