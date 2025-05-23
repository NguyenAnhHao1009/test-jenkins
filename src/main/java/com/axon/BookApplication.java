package com.axon;

import io.quarkus.runtime.Quarkus;

@io.quarkus.runtime.annotations.QuarkusMain
public class BookApplication {
    public static void main(String[] args) {
        Quarkus.run(args);
    }
    
}
