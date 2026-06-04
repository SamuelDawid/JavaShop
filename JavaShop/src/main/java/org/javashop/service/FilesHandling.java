package org.javashop.service;

import lombok.NonNull;
import org.javashop.interfaces.Savable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesHandling {
    private static final Path SAVED_ORDERS_DIRECTORY_PATH = Path.of("data","ORDERS");

    public static void saveOrderToFile(@NonNull Savable fileToSave) throws IOException {
        Files.createDirectories(SAVED_ORDERS_DIRECTORY_PATH);
        String fileName =fileToSave.fileName();
        Path file = SAVED_ORDERS_DIRECTORY_PATH.resolve(fileName);
        Files.writeString(file,fileToSave.content());
    }
}
