package Core;

import mnisttools.MnistReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;


public class Image {
    /*
    Image:
    Pour afficher les image d'emnist
        @param windowTitle: Titre de fenetre
        @param image: image buffered
     */
    public static void displayImage(final String windowTitle, final BufferedImage image) {
        new JFrame(windowTitle) {
            {
                final JLabel label = new JLabel("", new ImageIcon(image), SwingConstants.CENTER);
                add(label);
                pack();
                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                setVisible(true);
            }
        };
    }

    private static BufferedImage creat_image(int[][] data) {
        // Créer un image auprès de donnée int[][]
        BufferedImage image_b = new BufferedImage(280,280, BufferedImage.TYPE_BYTE_GRAY);
        for (int k = 0; k < 28 ; k++) {
            for (int j = 0; j < 28 ; j++) {
                int c = data[k][j];
                int rgb = new Color(c,c,c).getRGB();
                for (int l = 0; l < 10 ; l++) {
                    for (int m = 0; m < 10 ; m++) {
                        image_b.setRGB(k*10+l,j*10+m,rgb);
                    }
                }
            }
        }
        return image_b;
    }

    public static BufferedImage creat_image (double[] data) {
        // Créer un image auprès de donnée double[]
        BufferedImage image_b = new BufferedImage(280,280, BufferedImage.TYPE_BYTE_GRAY);
        for (int k = 0; k < 28 ; k++) {
            for (int j = 0; j < 28 ; j++) {
                int c = 0;
                if((int)data[k*28+j] == 1) c = 254;
                int rgb = new Color(c,c,c).getRGB();
                for (int l = 0; l < 10 ; l++) {
                    for (int m = 0; m < 10 ; m++) {
                        image_b.setRGB(k*10+l,j*10+m,rgb);
                    }
                }
            }
        }
        return image_b;
    }


    public static void main (String[] args) throws IOException {
        // Test
        String imageDB = "Data/emnist-byclass-train-images-idx3-ubyte";
        String labelDB = "Data/emnist-byclass-train-labels-idx1-ubyte";
        // Creation de la base de donnees
        MnistReader db = new MnistReader(labelDB, imageDB);
        // Attention la premiere valeur est 1.
        int i = 1;
        while (db.getLabel(i) != 10) {
            i++;
        }
        int test = i;
        int[][] image = db.getImage(test);
        System.out.println(db.getLabel(test));/// On recupere la premiere l'image numero idx
        BufferedImage image_b = creat_image(image);
        File out = new File("./Image/test1.png");
        ImageIO.write(image_b,"png",out);
        displayImage("Image", image_b);
    }
}