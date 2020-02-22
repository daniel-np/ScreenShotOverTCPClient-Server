package service.screenShot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;

public class Screenshot {

    public static BufferedImage captureWholeScreen() {
        try {
            Thread.sleep(120);
            Robot robot = new Robot();

            Rectangle captureField = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage image = robot.createScreenCapture(captureField);

            return image;
        } catch (AWTException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void saveImage(String path, BufferedImage image) {
        try {
            ImageIO.write(image, "png", new File(path));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int[][] deconstructImageToArray(BufferedImage image) {
        int[][] rgbArray = new int[image.getWidth()][image.getHeight()];

        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                rgbArray[i][j] = image.getRGB(i, j);
            }
        }


        return rgbArray;
    }



    public static boolean streamOutImage(BufferedImage image, DataOutputStream stream) {
        boolean success = true;

        try {
            stream.writeInt(image.getWidth());
            stream.writeInt(image.getHeight());
            Transaction:for(int i = 0; i < image.getWidth(); i++) {
                for(int j = 0; j < image.getHeight(); j++) {
                    try {
                        stream.writeInt(image.getRGB(i, j));
                    } catch (SocketException e) {
                        e.printStackTrace();
                        success = false;
                        break Transaction;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }

    public static BufferedImage streamInImage(DataInputStream stream,int width, int height,  int type) throws IOException {
        BufferedImage image = new BufferedImage(width, height, type);
        for(var i = 0; i < image.getWidth(); i++) {
            for(var j = 0; j < image.getHeight(); j++) {
                image.setRGB(i, j, stream.readInt());
            }
        }

        return image;
    }

    public static BufferedImage constructImage(int[][] rgbArray, int type) {
        BufferedImage image = new BufferedImage(rgbArray.length, rgbArray[0].length,type);
        for(var i = 0; i < image.getWidth(); i++) {
            for(var j = 0; j < image.getHeight(); j++) {
                image.setRGB(i, j, rgbArray[i][j]);
            }
        }

        return image;
    }
}
