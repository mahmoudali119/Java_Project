package javaappconverter2;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javaappconverter2.ImageConverter.ConvertTask;

public class JavaAppConverter2 {

    public static void main(String[] args) {
        

        File[] images = {
            new File("E:/College/Parallel Computing/ImageConverterApp/1.jpg"),
            new File("E:/College/Parallel Computing/ImageConverterApp/2.jpg"),
            new File("E:/College/Parallel Computing/ImageConverterApp/3.jpg")
        };
        
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (File image : images) {
            // تحديد ملف الإخراج (بصيغة PNG)
            File outputFile = new File("C:/Users/user/Pictures/Screenshots/" + image.getName().replace(".jpg", ".png"));
            
            // إرسال المهمة إلى الخيوط
            executor.execute(new ConvertTask(image, outputFile));
        }
        
        // إغلاق الـ Executor بعد انتهاء جميع المهام
        executor.shutdown();
    }
}
