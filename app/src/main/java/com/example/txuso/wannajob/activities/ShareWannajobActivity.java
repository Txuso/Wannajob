package com.example.txuso.wannajob.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.txuso.wannajob.R;
import com.facebook.Request;
import com.facebook.Session;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareWannajobActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_wannajob);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try{


            Bitmap adv = BitmapFactory.decodeResource(getResources(), R.drawable.job);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            adv.compress(Bitmap.CompressFormat.JPEG, 75, bytes);
            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "temporary_file.jpg");
            try {
                f.createNewFile();
                new FileOutputStream(f).write(bytes.toByteArray());
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            share.putExtra(Intent.EXTRA_TEXT, "Putuuuuuuuuuuuunnnn");

            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg"));

             if(isPackageInstalled("com.whatsapp",this)){
            share.setPackage("com.whatsapp");
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(share, "Share Image"));

            }else{

                 Toast.makeText(getApplicationContext(), "Please Install Whatsapp", Toast.LENGTH_LONG).show();
            }

/*
  Intent i = new Intent(Intent.ACTION_SEND);
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.profileimage);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), b, "Wannajob lo otroooo", null);
            Uri imageUri = Uri.parse(path);
            i.putExtra(Intent.EXTRA_STREAM, imageUri);
            String sAux = getString(R.string.share_wannajob_message);
            sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
            i.setPackage("com.whatsapp");
            i.setType("image/jpeg");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(i, "Choose an application to share Wannajob"));

            String sAux = getString(R.string.share_wannajob_message);
            sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            i.setPackage("com.facebook.katana");
            startActivity(Intent.createChooser(i, "Choose an application to share Wannajob"));
            */
            finish();
        }
        catch (Exception e){

        }
    }

    private boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                // NavUtils.navigateUpFromSameTask(this);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
