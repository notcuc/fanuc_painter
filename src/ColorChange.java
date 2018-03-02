import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ColorChange {

    private static int[] RED = {255, 0, 0};
    private static int[] GREEN = {0, 180, 0};
    private static int[] BLUE = {10, 180, 240};
    private static int[] YELLOW = {244, 216, 11};
    private static int[] WHITE = {255, 255, 255};
    private static int[] BLACK = {0, 0, 0};
    private static int[] PURPLE = {130, 0, 255};

    public static void main(String[] args) {
        List<int[]> colorsList = new ArrayList<>();
        colorsList.add(WHITE);
        colorsList.add(BLACK);
        colorsList.add(YELLOW);
        colorsList.add(RED);
        colorsList.add(PURPLE);
        colorsList.add(GREEN);
        colorsList.add(BLUE);
        int[][] array = new int[colorsList.size()][3];
        colorsList.toArray(array);
        BufferedImage img = null;

        try {
            img = ImageIO.read(new File("C:\\Users\\Tata2\\Pictures\\picture.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int photoWidth = img.getWidth();
        int photoHeight = img.getHeight();
        BufferedImage img2 = new BufferedImage(photoWidth, photoHeight,
                BufferedImage.TYPE_INT_RGB);
        BufferedImage newImg = new BufferedImage(photoWidth, photoHeight,
                BufferedImage.TYPE_INT_RGB);
        newImg.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
        JFrame frame = new JFrame();
        JLabel label = new JLabel(new ImageIcon(Objects.requireNonNull(newImg)));
        frame.add(label);
        frame.setDefaultCloseOperation
                (WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        img2 = getPixels(newImg, array, img2);
        JLabel label2 = new JLabel(new ImageIcon(Objects.requireNonNull(img2)));
        LSGenerator.generateLSfile(img2, convertToHex(array), "C:\\Users\\Tata2\\Pictures", "CODE");
        frame.add(label2);
        frame.setDefaultCloseOperation
                (WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private static BufferedImage getPixels(BufferedImage image, int[][] array, BufferedImage image2) {
        int photoWidth = image.getWidth();
        int photoHeight = image.getHeight();
        int[][] color = new int[photoWidth][photoHeight];
        for (int i = 0; i < photoWidth; i++) {
            for (int j = 0; j < photoHeight; j++) {
                color[i][j] = image.getRGB(i, j);
                color[i][j] = changeColor(color[i][j], array).getRGB();
                image2.setRGB(i, j, color[i][j]);
            }
        }
        return image2;
    }

    private static Color changeColor(int argb, int[][] array) {
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
        return new Color(cColor[0], cColor[1], cColor[2]);
    }

    private static List<Integer> convertToHex(int[][] color) {
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < color[0].length; i++) {
            colors.add(new Color(color[i][0], color[i][1], color[i][2]).getRGB());
        }
        return colors;
    }
}
