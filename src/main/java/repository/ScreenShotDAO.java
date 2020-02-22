package repository;

import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ScreenShotDAO {
    private ArrayList<String> imagePaths;
    private String dirPath;

    public ScreenShotDAO(String dirPath) {
        this.dirPath = dirPath;
        init();
    }

    private void init() {
        findFiles("screenShot", new File(dirPath));
    }

    private void findFiles(String name, File file) {
        File[] list = file.listFiles();

        if(list!=null) {
            Arrays.stream(list)
                    .map(f->{
                        System.out.println(f.getName());
                        if(f.getName().contains(name))
                            imagePaths.add(f.getName());
                        return f;
                    });
        }


    }

    public void printImagePaths() {
        System.out.println(dirPath);
        imagePaths.forEach(System.out::println);
    }
}
