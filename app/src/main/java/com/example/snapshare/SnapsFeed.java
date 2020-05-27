package com.example.snapshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SnapsFeed extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ListView snapsList;
    ArrayList<String> snapsfrom = new ArrayList<>();
    ArrayList<DataSnapshot> snaps = new ArrayList<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sharesnap, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.createsnap)
        {
            Intent intent = new Intent(this,createSnap.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.logout)
        {
            mAuth.signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps_feed);

        mAuth = FirebaseAuth.getInstance();
        snapsList = (ListView) findViewById(R.id.snapsList);
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,snapsfrom);
        snapsList.setAdapter(arrayAdapter);

        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                snapsfrom.add("You recieved a snap from "+ dataSnapshot.child("from").getValue().toString());
                snaps.add(dataSnapshot);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index=0;
                for(int i=0 ; i<snaps.size() ; ++i)
                {
                    DataSnapshot snap = snaps.get(i);
                    if(snap.getKey().equals(dataSnapshot.getKey()))
                    {
                        snaps.remove(index);
                        snapsfrom.remove(index);

                    }
                    index++;
                }
                arrayAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        snapsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataSnapshot snapshot = snaps.get(position);
                Intent intent = new Intent(SnapsFeed.this,ViewSnap.class);
                intent.putExtra("imageName",snapshot.child("imageName").getValue().toString());
                intent.putExtra("imageUrl",snapshot.child("imageUrl").getValue().toString());
                intent.putExtra("message",snapshot.child("Message").getValue().toString());
                intent.putExtra("snapKey",snapshot.getKey());
                startActivity(intent);

            }
        });

    }

}
