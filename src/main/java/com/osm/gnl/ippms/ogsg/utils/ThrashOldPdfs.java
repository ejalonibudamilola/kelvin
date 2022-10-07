package com.osm.gnl.ippms.ogsg.utils;

import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ThrashOldPdfs {

    public static void checkToDeleteOldPdfsOld(HttpServletRequest request) {
        LocalDate currentDay = LocalDate.now();

        if (currentDay.getDayOfWeek() != DayOfWeek.SATURDAY) {
            try {
               // File currDir = new File( ThrashOldPdfs.class.getClass().getResource(".").getFile());

                File currDir = new File(request.getServletContext().getRealPath(File.separator));
                String path = currDir.getPath();

                List<String> files = findFiles(Paths.get(path), "pdf");
                if (IppmsUtils.isNotNullOrEmpty(files)) {
                    files.forEach(x -> deleteFile(x));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return;
    }
    public static void checkToDeleteOldPdfs(List<String> fileList) {
        LocalDate currentDay = LocalDate.now();

        if (currentDay.getDayOfWeek() != DayOfWeek.SATURDAY) {
            // File currDir = new File( ThrashOldPdfs.class.getClass().getResource(".").getFile());


            if (IppmsUtils.isNotNullOrEmpty(fileList)) {
                fileList.forEach(x -> deleteFile(x));
            }
        }
        return;
    }
    public static void CheckToDeleteOldZippedFiles(HttpServletRequest request) {
        LocalDate currentDay = LocalDate.now();

        if (currentDay.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
            try {
                //File currDir = new File( ThrashOldPdfs.class.getClassLoader().getResource(".").getFile());
                File currDir = new File(request.getServletContext().getRealPath(File.separator));
                String path = currDir.getPath();


                List<String> files = findFiles(Paths.get(path), "zip");
                if (IppmsUtils.isNotNullOrEmpty(files)) {
                    files.forEach(x -> deleteFile(x));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return;
    }


    private static void deleteFile(String x) {
        try {
            File file = new File(x);             //creates a file instance
            FileUtils.forceDelete(file);
            return; //deletes the file instantly
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> findFiles(Path path, String fileExtension) throws IOException {
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }

        List<String> result = null;

        try (Stream<Path> walk = Files.walk(path)) {
            result = walk
                    .filter(p -> !Files.isDirectory(p))
                    // this is a path, not string,
                    // this only test if path end with a certain path
                    //.filter(p -> p.endsWith(fileExtension))
                    // convert path to string first
                    .map(p -> p.toString())
                    .filter(f -> f.endsWith(fileExtension))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
