package dev.malikkurosaki.v3gokorea;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class LayoutActifitas2 extends Fragment {

    private View v;
    private Context context;
    private Activity activity;
    private TextView papanScore;

    private DatabaseReference db;

    LayoutActifitas2 newInstance(){
        return new LayoutActifitas2();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_aktifitas2,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.v = view;
        this.context = v.getContext();
        this.activity = (Activity)context;


        papanScore = v.findViewById(R.id.papanScore);
        db = FirebaseDatabase.getInstance().getReference();

        db.child("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Map<String,Object> ambil = (HashMap<String, Object>)ds.getValue();
                    Map<String,Object> terima = new HashMap<>();
                    for (Map.Entry<String,Object> ent : ambil.entrySet()){
                        terima.put(ent.getKey(),ent.getValue());
                    }
                    String scrr = "Nama :"+terima.get("nama") +"    Score :"+ terima.get("score");
                    papanScore.append(scrr+"\n");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
