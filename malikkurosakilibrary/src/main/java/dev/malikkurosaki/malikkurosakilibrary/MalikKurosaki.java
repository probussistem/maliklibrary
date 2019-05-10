package dev.malikkurosaki.malikkurosakilibrary;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MalikKurosaki {

    private Context context;
    private Activity activity;
    private FirebaseFirestore db;
    private String TAG = "-->";
    private String col;
    private String namaFoldernya;

    public MalikKurosaki(Context context1){
        this.context = context1;
        this.activity = (Activity)context;

    }

    public MalikKurosaki mintaIjin(int codePermisi){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},codePermisi);
        }
        return this;
    }

    public MalikKurosaki db(FirebaseFirestore db1,String namaCollection){
        this.db = db1;
        this.col = namaCollection;
        return this;
    }

    public MalikKurosaki namaFolderBaru(String namaFolder){
        this.namaFoldernya = namaFolder;
        return this;
    }

    public void build(){
        final File folder = new File(Environment.getExternalStorageDirectory()
                + "/"+namaFoldernya);
        final String filename = folder.toString() + "/" + "Test.csv";

       db.collection(col).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               List<String[]> bungkus = new ArrayList<>();
               List<String> bungkusJason = new ArrayList<>();
               for (QueryDocumentSnapshot snapshot : task.getResult()){
                   Map<String,Object> ambil = (HashMap<String,Object>)snapshot.getData();
                   List<String> terima = new ArrayList<>();
                   for (Map.Entry<String,Object> ent : ambil.entrySet()){
                        terima.add(ent.getValue().toString());
                   }
                   String[] nn = terima.toArray(new String[0]);
                   bungkus.add(nn);
               }

               if (folder.exists()){
                   try {
                       CSVWriter csvWriter = new CSVWriter(new FileWriter(filename));
                       csvWriter.writeAll(bungkus);
                       csvWriter.close();
                       Toast.makeText(context,"sudah selesai ",Toast.LENGTH_LONG).show();
                   } catch (IOException e) {
                       e.printStackTrace();
                       Toast.makeText(context,"gagal "+e,Toast.LENGTH_LONG).show();
                   }
               }else {
                   boolean apakah = folder.mkdirs();
                   if (apakah){
                       try {
                           CSVWriter csvWriter = new CSVWriter(new FileWriter(filename));
                           csvWriter.writeAll(bungkus);
                           csvWriter.close();
                           Toast.makeText(context,"sudah selesai ",Toast.LENGTH_LONG).show();
                       } catch (IOException e) {
                           e.printStackTrace();
                           Toast.makeText(context,"gagal"+e,Toast.LENGTH_LONG).show();
                       }
                   }
               }


           }
       });




    }

    void nulis(String namaFile,List<String> list){

    }
}

