package com.cbitts.taskmanager;

import java.util.Random;

public class RandGenerate {

    public String randgenerate() {

        int i=0;
        final String characters = "qwertyuiopasdfghjklzxcvbnm1234567890";
        StringBuilder result = new StringBuilder();
        while (i<20){
            Random rand = new Random();
            result.append(characters.charAt(rand.nextInt(characters.length())));
            i++;
        }
        return result.toString();
    }
}
