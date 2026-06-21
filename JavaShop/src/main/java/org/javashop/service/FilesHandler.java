package org.javashop.service;

import lombok.NonNull;
import org.javashop.interfaces.Savable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The type Files handler.
 */
public class FilesHandler {
    /**
     * The constant SAVED_ORDERS_DIRECTORY_PATH.
     */
    public static final Path SAVED_ORDERS_DIRECTORY_PATH = Path.of("data", "ORDERS");

    /**
     * Saves SAVABLE object to file
     *
     * @param fileToSave the file to save
     * @param path       the path
     * @throws IOException the io exception
     */
    public static void saveToFile(@NonNull Savable fileToSave, @NonNull Path path) throws IOException {
        Files.createDirectories(path);
        String fileName = fileToSave.fileName();
        Path file = path.resolve(fileName);
        Files.writeString(file, fileToSave.content());
    }
}
