package pe.edu.ulima.fotosapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by sodm on 11/21/2016.
 */

public class FotosThumbnailActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    ImageView iviFoto;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos_thumbnail);
        iviFoto = (ImageView) findViewById(R.id.iviFoto);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //metodo que se ejecuta despues de volver de algun activity
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bitmap imageThumbnailBitmap = (Bitmap) data.getExtras().get("data");
            iviFoto.setImageBitmap(imageThumbnailBitmap);
        }
    }

    public void tomarFotoOnClick(View view){
        enviarTomarFotoIntent();
    }

    private void enviarTomarFotoIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        }
    }
}
