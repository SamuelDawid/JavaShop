package org.javaShop.UniqueIDGen;

import lombok.Setter;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UniqueIdGenerator {
    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();
    @Setter
    private static int idLength = 8;

    public String generateUniqueId(Set<String> existingIds) {
        String newId;
        do{
            newId = generateRandomString(idLength);
        }while (existingIds.contains(newId));
        return newId;
    }
    private String generateRandomString(int length) {
        return random.ints(length,0,ALPHANUMERIC_CHARS.length())
                .mapToObj(ALPHANUMERIC_CHARS::charAt)
                .map(Objects::toString)
                .collect(Collectors.joining());
    }
}
