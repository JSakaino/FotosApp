package pe.edu.ulima.fotosapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sodm on 11/21/2016.
 */

public class FotosActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private String mCurrentPhotoPath;
    ImageView iviFoto;
    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos_thumbnail);

        iviFoto = (ImageView) findViewById(R.id.iviFoto);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //metodo que se ejecuta despues de volver de algun activity
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            redimensionarMostrarFoto();
            new Thread(){
                @Override
                public void run() {
                    subirCloudinary();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FotosActivity.this, "Transferencia finalizada", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }.start();
        }
    }

    private void subirCloudinary(){
        Map config = new HashMap<String,String>();
        config.put("cloud_name","dtwnzaell");
        config.put("api_key","998493122369621");
        config.put("api_secret","aYynCfpGZpOKLgn_7kO3_7Avzx0");

        Cloudinary cloudinary = new Cloudinary(config);
        File photoFile = new File(mCurrentPhotoPath);

        try {
            FileInputStream is = new FileInputStream(photoFile);
            String timestamp = String.valueOf(new Date().getTime());
            //transferencia
            cloudinary.uploader().upload(
                    is,
                    ObjectUtils.asMap("public_id",timestamp)
            );
            Log.i("FotosActivity",cloudinary.url().generate(timestamp));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File crearImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMss_HHmmSS").format(new Date());
        String nombreArchivo = "JPEG_"+timestamp+"_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(nombreArchivo,".jpg",directorio);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void enviarTomarFotoIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = crearImageFile();
            } catch (IOException e) {
                Log.e("FotosActivity","Error creando archivo");
            }
            if (photoFile != null){
                Uri uri = FileProvider.getUriForFile(this,"pe.edu.ulima.fileprovider",photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    public void tomarFotoOnClick(View view){
        enviarTomarFotoIntent();
    }

    private void	redimensionarMostrarFoto()	{
        //	Obtener	las	dimensiones	del	ImageView
        int	targetW	=	iviFoto.getWidth();
        int	targetH	=	iviFoto.getHeight();
        //	Obtener	las	dimensiones	del	bitmap
        BitmapFactory.Options	bmOptions	= new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds	= true;
        BitmapFactory.decodeFile(mCurrentPhotoPath,	bmOptions);
        int	photoW	=	bmOptions.outWidth;
        int	photoH	=	bmOptions.outHeight;
        //	Determinamos	cuanto	debemos	escalar	la	imagen
        int	scaleFactor	= Math.min(photoW/targetW,	photoH/targetH);
        //	Decodificamos	el	archivo	imagen	en	un	bitmap	que	pueda	caber
        //	en	el	ImageView
        bmOptions.inJustDecodeBounds	= false;
        bmOptions.inSampleSize	=	scaleFactor;
        bmOptions.inPurgeable	= true;	//	<=	KITKAT
        Bitmap	bitmap	= BitmapFactory.decodeFile(mCurrentPhotoPath,	bmOptions);
        iviFoto.setImageBitmap(bitmap);
    }
}
