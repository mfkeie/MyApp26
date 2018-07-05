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
    private String fileName = "";

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
                mImageView.setImageDrawable(null);
                if(isStoragePermissionGranted()) {
                    String filePath = mPathEditText.getText().toString();
                    fileName = filePath.substring( filePath.lastIndexOf('/')+1, filePath.length() );
                    if (validateUri(filePath)) {
                        try {

                            Uri uri = Uri.parse(filePath);
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                            request.setAllowedOverRoaming(false);
                            request.setTitle("Downloading " + fileName);
                            request.setDescription("Downloading " + fileName);
                            request.setVisibleInDownloadsUi(true);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/" + fileName);
                            refid = downloadManager.enqueue(request);
                            if (refid > 0) {
                                //File imgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName);
                               // if(imgFile.exists())
                                    mShowButton.setEnabled(true);
                                //else
                                 //   showErrorToast("Не удалось загрузить файл!");
                            } else
                                showToast("Не удалось загрузить файл!");
                        } catch (Exception ex) {
                            showToast("Что-то пошло не так: " + ex.getMessage());
                        }

                    }
                }
            }
        });

        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(refid > 0 && !fileName.isEmpty()) {
                    File imgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName);
                    if(imgFile.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        mImageView.setImageBitmap(myBitmap);
                        imgFile.delete();
                    } else showToast("Не удалось загрузить файл!");
                    mShowButton.setEnabled(false);
                }
            }
        });
    }

    private boolean validateUri(String filePath) {
        if(TextUtils.isEmpty(filePath)) {
            showToast("Введен пустой url!");
            return false;
        } else {
            String fileExt = "";
            int dotPosition = filePath.lastIndexOf(".");
            if(dotPosition > 0)
                fileExt = filePath.substring(dotPosition + 1);
            if(!fileExt.equals("png") && !fileExt.equals("bmp") && !fileExt.equals("jpg")) {
                showToast("Необходимо ввести корректное расширение: png, bmp, jpg!");
                return false;
            }
            if(!URLUtil.isValidUrl(filePath)) {
                showToast("Необходимо ввести корректный url!");
                return false;
            }
            if(filePath.substring( filePath.lastIndexOf('/')+1, filePath.length()).isEmpty()) {
                showToast("Необходимо ввести корректный url!");
                return false;
            }
        }
        return true;
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

    private void showToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
    }
}
