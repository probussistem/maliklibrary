package dev.malikkurosaki.v3gokorea;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapzen.speakerbox.Speakerbox;
import com.tooltip.Tooltip;
import com.xd.commander.translatetool.GoogleTrans;
import com.xd.commander.translatetool.OnTransSuccess;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import dev.malikkurosaki.malikkurosakilibrary.MalikKurosaki;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private ImageView mulai;
    private Speakerbox speakerbox;
    private String nama = "";

    private EditText namaKenalan;
    private Button simpanKenalan;
    private DatabaseReference db;
    private BottomSheetDialog kenalan;
    private FirebaseFirestore dbf;


    private TextView papanScr1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speakerbox = new Speakerbox(this.getApplication());
        db = FirebaseDatabase.getInstance().getReference();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},500);
        }

        mulai = findViewById(R.id.mulai);
        papanScr1 = findViewById(R.id.papanScr1);

        dbf = FirebaseFirestore.getInstance();

        new MalikKurosaki(this)
                .mintaIjin(300)
                .db(dbf,"data")
                .namaFolderBaru("malikoutput")
                .build();


        Tooltip tooltip = new Tooltip.Builder(mulai)
                .setDismissOnClick(true)
                .setText("clikc sini untuk mulai")
                .setDrawablePadding(16)
                .setTextSize(24f)
                .setTextColor(getResources().getColor(R.color.colorOrange1))
                .setBackgroundColor(getResources().getColor(R.color.colorPutih))
                .setCornerRadius(20f)
                .show();

        Paper.init(this);
        nama = Paper.book().read("nama");

        kenalan = new BottomSheetDialog(this);
        View wadah = View.inflate(this,R.layout.layout_kenalan,null);
        kenalan.setContentView(wadah);
        namaKenalan = wadah.findViewById(R.id.namaKenalan);
        simpanKenalan = wadah.findViewById(R.id.simpanKenalan);

        mulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nama == null){
                    kenalan.show();
                    return;
                }

                Intent kemana = new Intent(MainActivity.this,Main2Activity.class);
                Map<String,Object> halaman = new HashMap<>();
                halaman.put("halaman","aktifitas1");
                kemana.putExtra("halaman", (Serializable) halaman);
                startActivity(kemana);

            }
        });


        simpanKenalan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nm = namaKenalan.getText().toString().trim();
                if (TextUtils.isEmpty(nm)){
                    Toast.makeText(getApplicationContext(),"isi namanya dong",Toast.LENGTH_LONG).show();
                    return;
                }

                Random acak = new Random();
                int nilaiAcak = acak.nextInt(1000);
                final String fxNama = nm+"-"+String.valueOf(nilaiAcak);

                Map<String,Object> daftar = new HashMap<>();
                daftar.put("nama",fxNama);
                daftar.put("score","");

                db.child("score").child(fxNama).setValue(daftar).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Paper.book().write("nama",fxNama);
                            MainActivity.this.recreate();
                            kenalan.dismiss();
                            Toast.makeText(getApplicationContext(),"pendaftaran selesai silahkan lanjutkan ",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        db.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Map<String,Object> ambil = (HashMap<String, Object>)ds.getValue();
                    Map<String,Object> terima = new HashMap<>();
                    for (Map.Entry<String,Object> ent : ambil.entrySet()){
                        terima.put(ent.getKey(),ent.getValue());
                    }
                    String scrr = "Name :"+terima.get("nama") +"    Score :"+ terima.get("score");
                    papanScr1.append(scrr+"\n");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 500){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),"ijin telah diberikan silahkan melanjutkan",Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == 300){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),"membaca external ijin diberikan",Toast.LENGTH_LONG).show();
            }
        }
    }
}
