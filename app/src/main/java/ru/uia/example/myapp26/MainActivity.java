package ru.uia.example.myapp26;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EditText mPathEditText;
    private Button mDownloadButton;
    private ImageView mImageView;
    private Button mShowButton;

    private DownloadManager downloadManager;
    private long refid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPathEditText = findViewById(R.id.et_file_path);
        mDownloadButton = findViewById(R.id.btn_download);
        mImageView = findViewById(R.id.iv_file);
        mShowButton = findViewById(R.id.btn_show);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStoragePermissionGranted()) {

                String uriString = mPathEditText.getText().toString();
                if(TextUtils.isEmpty(uriString)) {
                    showErrorToast("Введен пустой url!");
                    return;
                } else {
                    String fileExt = "";
                    int dotPosition = uriString.lastIndexOf(".");
                    if(dotPosition > 0)
                        fileExt = uriString.substring(dotPosition + 1);
                    if(!fileExt.equals("png") && !fileExt.equals("bmp") && !fileExt.equals("jpg")) {
                        showErrorToast("Необходимо ввести корректное расширение: png, bmp, jpg!");
                        return;
                    }
                    if(!URLUtil.isValidUrl(uriString)) {
                        showErrorToast("Необходимо ввести корректный url!");
                        return;
                    }
                }

                try {
                    Uri downloadUri = Uri.parse(uriString);
                    DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                    request.setAllowedOverRoaming(false);
                    request.setTitle("GadgetSaint Downloading " + "Sample" + ".png");
                    request.setDescription("Downloading " + "Sample" + ".png");
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/MyApp26/" + "/" + "myFile" + ".png");


                    refid = downloadManager.enqueue(request);
                    if (refid > 0) {
                        mShowButton.setEnabled(true);
                    }
                    else
                        showErrorToast("Не удалось загрузить файл!");
                } catch (Exception ex) {
                    showErrorToast("Что-то пошло не так: " + ex.getMessage());
                }

            }
            }
        });

        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(refid > 0) {
                    File imgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/MyApp26/"  + "/" + "myFile" + ".png");
                    if(imgFile.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        mImageView.setImageBitmap(myBitmap);
                        //mShowButton.setEnabled(false);
                        //imgFile.delete();
                    } else showErrorToast("Не удалось загрузить файл!");
                }
            }
        });
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    private void showErrorToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
    }
}
