package com.example.user.RobotPainter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SecondA extends AppCompatActivity {

    String codepath;
    private static Context context;
    private View.OnClickListener backToMainPage = v -> onBackPressed();

    private View.OnClickListener saveInPhone = (View v) -> { //save txt(but now img) file
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(Intent.createChooser(i, "Choose directory"), 110);
    };

    public static String getPath(final Context context, final Uri uri) { //get real path of file by uri, works only with external storage and downloads folder. Crash with sd-card

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // TODO: 16.01.2018 repair to correct path in many ways
            else if (isDownloadsDocument(uri)) {
                return "/storage/emulated/0/Download/";
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {

            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_second);
        SecondA.context = getApplicationContext();
       // Button botton_robot = findViewById(R.id.button_robot);
        Button button_phone = findViewById(R.id.button_phone);
        findViewById(R.id.button_back).setOnClickListener(backToMainPage);
        button_phone.setOnClickListener(saveInPhone);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 110 && resultCode == RESULT_OK && data != null) { //check if file successful saved
            Uri originalUri = data.getData();
            String path = getTreePath(originalUri);
            codepath = path;
            Intent intent = getIntent();
            Bitmap filteredImg = intent.getParcelableExtra("filteredImg");
            String[] colors = intent.getStringArrayExtra("colors");
            List<String> colorList = Arrays.asList(colors);
            LSGenerator.generateLSfile(filteredImg, colorList, path, "CODE");
            try {
                serverConnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        if (requestCode == 110 && resultCode == RESULT_OK && data != null) { //check if file successful saved
            //get Path by Uri
//            FileOutputStream out = null;
//
//            Bitmap filteredImg = (Bitmap) intent.getParcelableExtra("filteredImg");
//            try { // TODO: 16.01.2018 make there saving a code, but now saving image
//                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                String filename = path + "/FILTERED_IMG_" + timeStamp + ".jpg";
//                Log.d("filename", filename);
//                out = new FileOutputStream(filename);
//                filteredImg.compress(Bitmap.CompressFormat.PNG, 100, out); //save img to choosed path
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (out != null) {
//                        out.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }


    public String getTreePath(Uri uri) {
        Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
        String path = getPath(this, docUri);
        return path;
    }

    private void serverConnect() throws IOException { //save txt(but now img) file
        TextView textip = findViewById(R.id.textip1);
        String ip = (String) textip.getText().toString();
        TextView textport = findViewById(R.id.textip2);
        Integer port = Integer.parseInt(textport.getText().toString());
        Log.d("Files", "Path: " + codepath);
        File directory = new File(codepath + "/");
        File[] files = directory.listFiles();
        Log.d("ip", ip);
        Log.d("port", port.toString());
        for (int i = 0; i < files.length; i++) {
            String codepathSend = codepath + "/" + files[i].getName();
            String codepathGet = codepath + "/" + (files[i].getName()).substring(0, (files[i].getName()).length() - 3) + ".tp";
            Log.d("Files", "FileNames:" + codepathSend + " " + codepathGet);
            serverSendGet(ip, port, codepathSend); //send ls -> get tp
        }
        serverEnd(ip,port);
        Log.d("serveSend", "ls sent");
    }

    ;

    protected byte[] fileToByte(String path) {
        File file = new File(path);
//init array with file length
        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fis.read(bytesArray); //read file into bytes[]
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytesArray;
    }

    protected void serverSendGet(String ip, Integer port, String filePathout) {
        new Thread(() -> {
            Socket sock = null;
            try {
                sock = new Socket(ip, port);
            } catch (IOException e) {
                Log.d("sock", sock.toString());
                e.printStackTrace();
            }
            Log.d("ip", ip + " " + port);
            FileOutputStream fos = null;
            try {
                fos = (FileOutputStream) sock.getOutputStream(); //pathout
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] data = fileToByte(filePathout);
            // TODO: 19.01.2018  file to byte -> fos.write();
            try {
                fos.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    protected void serverEnd(String ip, Integer port) {
        new Thread(() -> {
            Socket sock = null;
            try {
                sock = new Socket(ip, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream fos = null;
            try {
                fos = (FileOutputStream) sock.getOutputStream(); //pathout
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] data = "Sent".getBytes();;
            // TODO: 19.01.2018  file to byte -> fos.write();
            try {
                fos.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}