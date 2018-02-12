package com.example.user.RobotPainter;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


class LSGenerator {


    private static final String DEGREES = ".000 deg,\tP =   0.000 deg,\tR =    90.000 deg\n";
    private static final String WHITE = "ffffffff";
    private static final String BLACK = "ff000000";
    private static final String TAG = "MainActivity";
    private static String z;
    private static int w;
    private static List<Integer> testpoints = new ArrayList<>();
    private static int conf;
    private static int point = 1;
    private static int step = 1;
    //    private static List<Integer> setTest(int count) {
//        List<Integer> color = new ArrayList<>();
//        for (int i = 1; i < count; i++)
//            color.add(i);
//        return color;
//    }
    private static int testY = -6;
    private static int testX = 0;
    private static int smth = 2;

    /**
     * @param colors
     * @param sourceBitmap исходное изображение
     * @param destPath     путь сохранения
     * @param filename     название файла без расширения
     *                     //@param progressListener функциональный интерфейс для отображения прогресса
     */
    static void generateLSfile(Bitmap sourceBitmap, List<String> colors,
                               String destPath, String filename) {

        point = 1;
        step = 1;
        testX = 0;
        testY = -6;
        smth = 2;
        testpoints = new ArrayList<>();
        int photoWidth = sourceBitmap.getWidth();
        int photoHeight = sourceBitmap.getHeight();

        int x = 1;
        int y = 1;

        deleteFiles(destPath);
        List<StringBuilder> blackpointarr = getTest(1, 20);
        List<Integer> blackarr = new ArrayList<>();
        blackarr.addAll(testpoints);
        Log.d(TAG, "generateLSfile: blackarr " + blackarr);

        List<StringBuilder> purppointarr = getTest(2, 20);
        List<Integer> purparr = new ArrayList<>();
        purparr.addAll(testpoints);
        Log.d(TAG, "generateLSfile: purparr " + purparr);

        List<StringBuilder> greenpointarr = getTest(3, 20);
        List<Integer> greenarr = new ArrayList<>();
        greenarr.addAll(testpoints);
        Log.d(TAG, "generateLSfile: greenarr " + greenarr);

        List<StringBuilder> yellpointarr = getTest(4, 20);
        List<Integer> yellowarr = new ArrayList<>();
        yellowarr.addAll(testpoints);
        Log.d(TAG, "generateLSfile: yellowarr " + yellowarr);

        List<StringBuilder> bluepointarr = getTest(5, 20);
        List<Integer> bluearr = new ArrayList<>();
        bluearr.addAll(testpoints);
        Log.d(TAG, "generateLSfile: bluearr " + bluearr);

        List<StringBuilder> redpointarr = getTest(6, 20);
        List<Integer> redarr = new ArrayList<>();
        redarr.addAll(testpoints);
        Log.d(TAG, "generateLSfile: redarr " + redarr);

        int color;
        int count = 1;
        String[][] image = new String[photoWidth][photoHeight];
        for (int i = 0; i < photoWidth; i++) {
            for (int j = 0; j < photoHeight; j++) {
                image[i][j] = Integer.toHexString(sourceBitmap.getPixel(i, j));
            }
        }
        for (int i = 0; i < photoWidth; i++) {
            for (int j = 0; j < photoHeight; j++) {
                // boolean flag = true;
                StringBuilder coordinate;
                StringBuilder coordinate2;
                StringBuilder coordinate3;
                List<Integer> nowPoint = new ArrayList<>();

                if (!image[i][j].equals(WHITE)) {
                    color = colors.indexOf(image[i][j]);
                    if (color == -1) {
                        continue;
                    } else {
                        if (image[i][j].equals(BLACK)) {
                            conf = 1;
                            w = 180;
                            z = "0.600";
                        } else {
                            conf = 0;
                            w = -180;
                            z = "-1.000";
                        }
                        //if (flag) {
                        coordinate = getPoint(point, color, x, y, "15.000", conf, w);
                        nowPoint.add(point);
                        point++;

                        coordinate2 = getPoint(point, color, x, y, z, conf, w);
                        nowPoint.add(point);
                        point++;

                        coordinate3 = getPoint(point, color, x, y, "15.000", conf, w);
                        nowPoint.add(point);
                        point++;
//                        } else {
//                            if (j - 1 < 0) {
//                                coordinate = getPoint(point, color, x, y, "10.000", conf, w);
//
//                                nowPoint.add(point);
//                                point++;
//                                coordinate2 = getPoint(point, color, x, y, "0.000", conf, w);
//                                nowPoint.add(point);
//                                point++;
//                            } else if (!image[i][j - 1].equals(image[i][j])) {
//                                coordinate = getPoint(point, color, x, y, "10.000", conf, w);
//                                nowPoint.add(point);
//                                point++;
//                                coordinate2 = getPoint(point, color, x, y, "0.000", conf, w);
//                                nowPoint.add(point);
//                                point++;
//                            }
//
//                            if (!image[i][j + 1].equals(image[i][j]) || j + 1 >= photoHeight) {
//                                coordinate2 = getPoint(point, color, x, y, "0.000", conf, w);
//                                nowPoint.add(point);
//                                point++;
//                                coordinate3 = getPoint(point, color, x, y, "10.000", conf, w);
//                                nowPoint.add(point);
//                                point++;
//                            }


                        //nowPoint = Arrays.asList(point - 3, point - 2, point - 1);
                        switch (color) {

                            case 1: {
                                blackarr.addAll(nowPoint);
                                blackpointarr.add(coordinate);
                                blackpointarr.add(coordinate2);
                                blackpointarr.add(coordinate3);
                                // Log.d("blackpointarr", blackpointarr.toString());
                                break;
                            }
                            case 2: {
                                purparr.addAll(nowPoint);
                                purppointarr.add(coordinate);
                                purppointarr.add(coordinate2);
                                purppointarr.add(coordinate3);
                                break;
                            }
                            case 3: {
                                greenarr.addAll(nowPoint);
                                greenpointarr.add(coordinate);
                                greenpointarr.add(coordinate2);
                                greenpointarr.add(coordinate3);
                                break;
                            }
                            case 4: {
                                yellowarr.addAll(nowPoint);
                                yellpointarr.add(coordinate);
                                yellpointarr.add(coordinate2);
                                yellpointarr.add(coordinate3);
                                break;
                            }
                            case 5: {
                                bluearr.addAll(nowPoint);
                                bluepointarr.add(coordinate);
                                bluepointarr.add(coordinate2);
                                bluepointarr.add(coordinate3);
                                break;

                            }
                            case 6: {
                                redarr.addAll(nowPoint);
                                redpointarr.add(coordinate);
                                redpointarr.add(coordinate2);
                                redpointarr.add(coordinate3);
                                break;
                            }
                        }
                    }
                }
                x += 3;
                //progressListener.progressChanged(count, photoHeight * photoWidth);
                count++;
            }
            x = 1;
            y += 3;
        }
        writeToFile(getHead(filename), filename + ".ls", destPath);
        generation(blackarr, blackpointarr, 1, filename, destPath);
        generation(purparr, purppointarr, 2, filename, destPath);
        generation(greenarr, greenpointarr, 3, filename, destPath);
        generation(yellowarr, yellpointarr, 4, filename, destPath);
        generation(bluearr, bluepointarr, 5, filename, destPath);
        generation(redarr, redpointarr, 6, filename, destPath);
        writeToFile("\n/END\n", filename + ".ls", destPath);

        Log.d("count", String.valueOf(count));
    }

    private static void generation(List<Integer> color, List<StringBuilder> pointarr, int colornum, String filename, String destPath) {
        StringBuilder stepstr = new StringBuilder();
        StringBuilder pointstr = new StringBuilder();
        String filename2 = filename + colornum;
        Log.d("FILENAME1", filename2);
        if (colornum == 1) {
            conf = 1;
            w = 180;
        } else {
            conf = 0;
            w = -180;
        }
        int main = 2;
        if (color.size() > 5000) {
            for (int i = 0; i <= (color.size() / 5000); i++) {
                stepstr.delete(0, stepstr.length());
                pointstr.delete(0, pointstr.length());
                filename2 = filename + colornum + "_" + i;
                Log.d("FILENAME", filename2);
                // flag = false;
                writeToFile(getHead(filename2), filename2 + ".ls", destPath);
                //flag = true;
                writeToFile(main + ": CALL " + filename2 + ";\n", filename + ".ls", destPath);
                main++;
                setSteps(color.subList(i * 5000, color.size() >= i * 5000 + 5000 ? i * 5000 + 5000 : color.size()), colornum, stepstr);
                stepstr.append(getStep(step, point, "J", "20% ", "CNT100"));
                stepstr.append("/POS");
                setPoints(pointarr.subList(i * 5000, pointarr.size() >= i * 5000 + 5000 ? i * 5000 + 5000 : pointarr.size()), pointstr);
                pointstr.append(getPoint(point, colornum, 145, 300, "150.000", conf, w));
                pointstr.append("\n/END\n");
                writeToFile(stepstr.toString(), filename2 + ".ls", destPath);
                writeToFile(pointstr.toString(), filename2 + ".ls", destPath);
            }
        } else {
            stepstr.delete(0, stepstr.length());
            pointstr.delete(0, pointstr.length());
            // flag = false;
            writeToFile(getHead(filename2), filename2 + ".ls", destPath);
            // flag = true;
            writeToFile(main + ": CALL " + filename2 + ";\n", filename + ".ls", destPath);
            main++;
            setSteps(color, colornum, stepstr);
            stepstr.append(getStep(step, point, "J", "50% ", "CNT100"));
            stepstr.append("/POS");
            setPoints(pointarr, pointstr);
            pointstr.append(getPoint(point, colornum, 145, 300, "100.000", conf, w));
            pointstr.append("\n/END\n");
            writeToFile(stepstr.toString(), filename2 + ".ls", destPath);
            writeToFile(pointstr.toString(), filename2 + ".ls", destPath);
        }

    }

    private static List<StringBuilder> getTest(int color, int count) {
        List<StringBuilder> points = new ArrayList<>();

        testpoints.clear();

        if (color == 1) {
            conf = 0;
            w = -180;
            z = "-1.000";
        } else {
            conf = 0;
            w = -180;
            z = "-2.000";
        }
        for (int i = 0; i < count; i++) {
            if (testX >= 240) {
                testX = 0;
                testY = -3;
            }
            points.add(getPoint(point, color, testX, testY, "15.000", conf, w));
            testpoints.add(point);
            point++;
            points.add(getPoint(point, color, testX, testY, z, conf, w));
            testpoints.add(point);
            point++;
            points.add(getPoint(point, color, testX, testY, "15.000", conf, w));
            testpoints.add(point);
            point++;
            testX += 3;
        }
        testX += 3;
        for (int i = 0; i < 2; i++) {
            if (testX >= 240) {
                testX = 0;
                testY = -3;
            }
            points.add(getPoint(point, color, testX, testY, "15.000", conf, w));
            testpoints.add(point);
            point++;
            points.add(getPoint(point, color, testX, testY, "0.000", conf, w));
            testpoints.add(point);
            point++;
            points.add(getPoint(point, color, testX, testY, "15.000", conf, w));
            testpoints.add(point);
            point++;
            testX += 3;
        }

        return points;
    }

    private static void setSteps(List color, int colornum, StringBuilder steps) {
        step = 2;
        steps.append(step).append(":UTOOL_NUM=").append(colornum).append(";\n");
        step++;
        String var;
        for (int i = 0; i < color.size(); i++) {
            if (smth == 3) {
                var = " FINE";
                smth = 1;
            } else {
                var = " CNT100";
                smth++;
            }
            String type;
            String speed;
            if (i == 0) {
                Log.d(TAG, "check");
                type = "J";
                speed = "10%";
            } else {
                type = "L";
                speed = "250mm/sec";
            }
            steps.append(getStep(step, (Integer) color.get(i), type, speed, var));
            step++;
        }
    }

    private static void setPoints(List pointarr, StringBuilder points) {
        for (int i = 0; i < pointarr.size(); i++) {
            points.append(pointarr.get(i));
        }
    }

    private static StringBuilder getPoint(int point, int color, int x, int y, String z, int conf, int w) {
        return new StringBuilder("\nP[" + point + "]{\n" +
                "   GP1:\n" +
                "\tUF : 1, UT : " + color + ",\t\tCONFIG : 'N U T, 0, 0, " + conf + "',\n" +
                "\tX =  " + x + ".000  mm,\tY =  " + y + ".000  mm,\tZ =   " + z + " mm,\n" + "\tW =" + w +
                DEGREES +
                "};");
    }

    private static String getStep(int step, int point, String type, String speed, String var) {
        return step + ":" + type + " P[" + point + "] " + speed + var + ";\n";
    }

    private static String getHead(String name) {
        return "/PROG " + name + "\n" +
                "/ATTR\n" +
                "OWNER\t\t= NOC_FANU;\n" +
                "COMMENT\t\t= \"NOC_FANUC\";\n" +
                "PROG_SIZE\t= 328;\n" +
                "CREATE\t\t= DATE 97-07-08  TIME 03:05:20;\n" +
                "MODIFIED\t= DATE 17-07-08  TIME 03:05:26;\n" +
                "FILE_NAME\t= ;\n" +
                "VERSION\t\t= 0;\n" +
                "LINE_COUNT\t= 10;\n" +
                "MEMORY_SIZE\t= 692;\n" +
                "PROTECT\t\t= READ_WRITE;\n" +
                "TCD:  STACK_SIZE\t= 0,\n" +
                "      TASK_PRIORITY\t= 0,\n" +
                "      TIME_SLICE\t= 0,\n" +
                "      BUSY_LAMP_OFF\t= 0,\n" +
                "      ABORT_REQUEST\t= 0,\n" +
                "      PAUSE_REQUEST\t= 0;\n" +
                "DEFAULT_GROUP\t= 1,*,*,*,*;\n" +
                "CONTROL_CODE\t= 00000000 00000000;\n" +
                "/APPL\n" +
                "/MN\n" +
                "1:UFRAME_NUM=1 ;\n";
    }

    private static void deleteFiles(String destPath) {
// Lists all files in folder
        File folder = new File(destPath);
        File fList[] = folder.listFiles();
        Log.d(TAG, "delete");
// Searchs .lck
        for (File aFList : fList) {
            String pes = String.valueOf(aFList);
            if (pes.endsWith(".ls") || pes.endsWith(".tp")) {
                // and deletes
                boolean success = (new File(String.valueOf(aFList)).delete());
                Log.d(TAG, String.valueOf(success));
            }
        }
//        final File path = Environment.getExternalStoragePublicDirectory
//                (destPath);
//        final File file = new File(path, filename);
//        file.delete();
    }

    private static void writeToFile(String str, String filename, String destPath) {
        final File path = Environment.getExternalStoragePublicDirectory
                (destPath.substring(20, destPath.length()));
        Log.d(TAG, path.toString());
        if (!path.exists()) {
            path.mkdirs();
        }
        final File file = new File(path, filename);
        try {
            FileOutputStream stream = new FileOutputStream(file, true);
            //flag = true;
            stream.write(str.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
