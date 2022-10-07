/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.filestorage;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FileStorageServiceImpl implements FileStorageService{

    private final Path root = Paths.get("uploads");
    @Override
    public void init(){
        try{
            Files.createDirectories(root);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void save(MultipartFile file) {
        try{
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        }catch (Exception e){
            throw new RuntimeException("Could not store file. Error: "+e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try{
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if(resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new RuntimeException("Could not read the file!");
            }

        }catch (MalformedURLException e){
            throw new RuntimeException("Error: "+e.getMessage());
        }

    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try{
            return  Files.walk(this.root, -1).filter(path -> !path.equals(this.root));
        }catch(IOException e){
            throw new RuntimeException("Could not load the file!");
        }

    }
}
