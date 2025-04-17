package javaappconverter2;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageConverter {
    
    static class ConvertTask implements Runnable {
        private final File inputFile;
        private final File outputFile;

        public ConvertTask(File inputFile, File outputFile) {
            this.inputFile = inputFile;
            this.outputFile = outputFile;
        }

        @Override
        public void run() {
            try {
                BufferedImage image = ImageIO.read(inputFile);
                ImageIO.write(image, "png", outputFile);
                System.out.println("Converted: " + inputFile.getName() + " to " + outputFile.getName());
            } catch (IOException e) {
                System.err.println("Error converting: " + inputFile.getName());
                e.printStackTrace();
            }
        }
    }
}
