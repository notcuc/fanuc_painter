package com.example.user.RobotPainter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.graphics.Color.argb;
import static java.lang.Math.round;


public class MainA extends AppCompatActivity {
    //private static final String TAG = "MainA";
    //private static final int STORAGE_REQUEST_CODE = 3;
    public static Bitmap resizedBitImage;
    public static int[] RED = {255, 0, 0};
    public static int[] GREEN = {0, 180, 0};
    public static int[] BLUE = {10, 180, 240};
    public static int[] YELLOW = {244, 216, 11};
    public static int[] WHITE = {255, 255, 255};
    public static int[] BLACK = {0, 0, 0};
    public static int[] PURPLE = {130, 0, 255};
//    public static int[] PINK = {250, 105, 105};
//    public static int[] GREY = {100, 100, 100};
//    public static int[] LBLUE = {0, 100, 255};
//    public static int[] LGREEN = {80, 185, 80};
//    public static int[] LORANGE = {255, 200, 140};
    static Uri fileUri;
    boolean imageIsLoaded = false;
    Bitmap orig_loaded_img;
    String loadedfile_path;
    Bitmap filteredImg;
    private Button takePictureButton;
    private ImageView imageView;
    private View.OnClickListener savePicturePage = (View v) -> {

        Intent intent = new Intent(MainA.this, SecondA.class);
        intent.putExtra("filteredImg", filteredImg);
        String[] colors = {convertToHex(WHITE), convertToHex(BLACK), convertToHex(PURPLE), convertToHex(GREEN),
                convertToHex(YELLOW), convertToHex(BLUE), convertToHex(RED)}; //convertToHex(PINK), convertToHex(GREY),
               // convertToHex(LBLUE), convertToHex(LGREEN), convertToHex(LORANGE)};
        intent.putExtra("colors", colors);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_enter, R.anim.push_left_exit);

    };
    private View.OnClickListener loadPicturePage = v -> {
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 150);
    };

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static Bitmap checkForRotateImage(Bitmap bitmap, boolean imageIsLoaded) throws IOException {
        if (!imageIsLoaded) {
            ExifInterface ei = new ExifInterface(fileUri.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap rotatedBitmap;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            return rotatedBitmap;
        } else {
            Bitmap rotatedBitmap;
            int byteSize = bitmap.getRowBytes() * bitmap.getHeight();
            ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
            bitmap.copyPixelsToBuffer(byteBuffer);
            byte[] byteArray = byteBuffer.array();
            ByteArrayInputStream bs = new ByteArrayInputStream(byteArray);
            ExifInterface ei = new ExifInterface(bs); // TODO: 17.01.2018 make change orintation from bitmap
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }

            return rotatedBitmap;
        }
    }

    protected static int[] getWidthHeight(float w, float h) {
        float k = Math.min(w, h) / Math.max(w, h);
        float ww = 0;
        float hh = 0;
        int size = 80;
        while (true) {

            if (w > h) {
                if (ww + 5 > size || (ww + 5) * k > size) break;
                ww += 5;
                hh = ww * k;
            } else {
                if (hh + 5 > size || (hh + 5) * k > size) break;
                hh += 5;
                ww = hh * k;
            }
        }
        return new int[]{round(ww), round(hh)};
    }

    private static File getOutputMediaFile() { //return good dir
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "OLD_IMG_" + timeStamp + ".jpg");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        takePictureButton = findViewById(R.id.button_image);
        imageView = findViewById(R.id.ivImage);
        Button button = findViewById(R.id.button_continue);
        button.setOnClickListener(savePicturePage);
        Button button2 = findViewById(R.id.button_load);
        button2.setOnClickListener(loadPicturePage);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }

    public void makeImageToPixel() { //create palette and render n-color image
        List<int[]> colorsList = new ArrayList<>();
        colorsList.add(WHITE);
        colorsList.add(BLACK);
        colorsList.add(YELLOW);
        colorsList.add(RED);
        colorsList.add(PURPLE);
        colorsList.add(GREEN);
        colorsList.add(BLUE);
//        colorsList.add(PINK);
//        colorsList.add(GREY);
//        colorsList.add(LBLUE);
//        colorsList.add(LGREEN);
//        colorsList.add(LORANGE);
        if (!((CheckBox) findViewById(R.id.cbBlack)).isChecked()) {
            colorsList.remove(BLACK);
            //colorsList.remove(GREY);
        }
        if (!((CheckBox) findViewById(R.id.cbYellow)).isChecked()) {
            colorsList.remove(YELLOW);
        }
        if (!((CheckBox) findViewById(R.id.cbRed)).isChecked()) {
            colorsList.remove(RED);
            //colorsList.remove(PINK);
        }
        if (!((CheckBox) findViewById(R.id.cbPurple)).isChecked()) {
            colorsList.remove(PURPLE);
           // colorsList.remove(LORANGE);
        }
        if (!((CheckBox) findViewById(R.id.cbGreen)).isChecked()) {
            colorsList.remove(GREEN);
           // colorsList.remove(LGREEN);
        }
        if (!((CheckBox) findViewById(R.id.cbBlue)).isChecked()) {
            colorsList.remove(BLUE);
           // colorsList.remove(LBLUE);
        }
        int[][] array = new int[colorsList.size()][3];
        colorsList.toArray(array);
        new Thread(() -> {
            try {
                Bitmap newMapImage = ImageFilter(array);
                filteredImg = checkForRotateImage(newMapImage, imageIsLoaded);
                renderResultImg(newMapImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void renderResultImg(final Bitmap newMapImage) {
        runOnUiThread(() -> {
            ImageView tv1 = findViewById(R.id.ivResult);

            try {
                tv1.setImageBitmap(checkForRotateImage(newMapImage, imageIsLoaded));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 150 && resultCode == RESULT_OK && data != null) { //if image is loaded
            if (null == data) return;
            imageIsLoaded = true;
            Uri originalUri = data.getData();
            try {
                Log.d("height of uri bitmap", String.valueOf(MediaStore.Images.Media.getBitmap(getContentResolver(), originalUri)));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap loadedimage_bitmap = null;
            try {
                loadedimage_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), originalUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (loadedimage_bitmap.equals("")) {
                loadedfile_path = "stop it";
            } else {
                int[] wh = getWidthHeight(loadedimage_bitmap.getWidth(), loadedimage_bitmap.getHeight());
                resizedBitImage = Bitmap.createScaledBitmap(loadedimage_bitmap, wh[0], wh[1], true); //orig resized loaded image
                orig_loaded_img = resizedBitImage;
                imageView = findViewById(R.id.ivImage);
                imageView.setImageBitmap(resizedBitImage); //render loaded img and rotate if required
                findViewById(R.id.button_continue).setVisibility(View.VISIBLE);//render continue button
                makeImageToPixel();
            }
        }
        super.onActivityResult(requestCode, resultCode, data); //if image is photographed
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Bitmap BitImage = null;
                try {
                    BitImage = getBitmapFromAssets();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int[] wh = getWidthHeight(BitImage.getWidth(), BitImage.getHeight());
                resizedBitImage = Bitmap.createScaledBitmap(BitImage, wh[0], wh[1], true); //make image optimal sizes
                Log.d("new_width", String.valueOf(wh[0]));
                Log.d("new_height", String.valueOf(wh[1]));
                try {
                    imageView.setImageBitmap(checkForRotateImage(resizedBitImage, imageIsLoaded)); //render and rotate if required
                    findViewById(R.id.button_continue).setVisibility(View.VISIBLE);//render continue button
                } catch (IOException e) {
                    e.printStackTrace();
                }
                makeImageToPixel();

            }
        }
    }

//    public String getImagePath(Uri uri) {
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        String document_id = cursor.getString(0);
//        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
//        cursor.close();
//
//        cursor = getContentResolver().query(
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//        cursor.moveToFirst();
//        String path;
//        if (document_id.substring(0, Math.min(document_id.length(), document_id.lastIndexOf("/") + 1)).equals("/storage/emulated/0/Download/")) {
//            path = document_id;
//        } else {
//            if (document_id.contains("/")) {
//                path = "/storage/emulated/0/" + document_id;
//            } else {
//                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            }
//        }
//        cursor.close();
//
//        return path;
//    }

    private Bitmap ImageFilter(int[][] array) throws IOException { //return n-color img(bitmap)
        ImageView imageView = new ImageView(this);
        if (!imageIsLoaded) {
            imageView.setImageBitmap(checkForRotateImage(getBitmapFromAssets(), imageIsLoaded)); //render orig img
        } else {
            imageView.setImageBitmap(orig_loaded_img); //else render loaded orig img
        }
        Bitmap bitmap = resizedBitImage;
        int photoWidth = bitmap.getWidth();
        int photoHeight = bitmap.getHeight();
        Integer[][] image = new Integer[photoWidth][photoHeight];

        for (int i = 0; i < photoWidth; i++) {
            for (int j = 0; j < photoHeight; j++) {
                image[i][j] = bitmap.getPixel(i, j);
                image[i][j] = filterRGB(image[i][j], array);
            }
        }
        Bitmap bitmap2 = Bitmap.createBitmap(photoWidth, photoHeight, Bitmap.Config.ARGB_8888);
        int[] x = new int[photoWidth * photoHeight];
        int k = 0;

        for (int j = 0; j < photoHeight; j++) {
            for (int i = 0; i < photoWidth; i++) {
                x[k++] = image[i][j];
            }
        }
        bitmap2.setPixels(x, 0, photoWidth, 0, 0, photoWidth, photoHeight);
        return bitmap2;
    }

    private Bitmap getBitmapFromAssets() throws IOException { //return bitmap of orig img
        return MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
    }

    public int filterRGB(int argb, int[][] array) { //make one pixel to palette
        int nR = (argb >> 16) & 0xff;
        int nG = (argb >> 8) & 0xff;
        int nB = argb & 0xff;
        int[] cColor = {0, 0, 0};
        int cMin = 999999999;
        for (int[] anArray : array) {
            int deltaR = anArray[0] - nR;
            int deltaG = anArray[1] - nG;
            int deltaB = anArray[2] - nB;
            int distance = (deltaR * deltaR) + (deltaG * deltaG) + (deltaB * deltaB);
            if (distance < cMin) {
                cMin = distance;
                cColor[0] = anArray[0];
                cColor[1] = anArray[1];
                cColor[2] = anArray[2];
            }
        }
        return argb(255, cColor[0], cColor[1], cColor[2]);
    }

    public void takePicture(View view) { //wait for photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, 100);
    }

    public String convertToHex(int[] color) {
        String hexcolor = "ff";
        for (int i = 0; i < 3; i++) {
            String colors = Integer.toHexString(color[i]);
            if (colors.length() == 1) {
                hexcolor = hexcolor + "0" + colors;
            } else {
                hexcolor += colors;

            }
            Log.d("colors", hexcolor);
        }
        //colors[colornum]= hexcolor;
        return hexcolor;
    }

}

