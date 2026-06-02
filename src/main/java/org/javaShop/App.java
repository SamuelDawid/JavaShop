package org.javaShop;

import org.javaShop.UniqueIDGen.UniqueIdGenerator;
import org.javaShop.models.Product;

import java.util.HashSet;
import java.util.Set;

public class App
{
    public static void main(String[] args) {
        System.out.println("Hello");
        Set<String> UniqueIds = new HashSet<>();
        UniqueIdGenerator uniqueIdGenerator = new UniqueIdGenerator();
        Product product = Product.builder().Id(uniqueIdGenerator.generateUniqueId(UniqueIds)).build();
        System.out.println(product.getId());
    }
}
