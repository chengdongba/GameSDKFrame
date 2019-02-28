package com.creative.gamesdk.build;

/**
 * Created by Administrator on 2019/2/28.
 */

public class Main {
    public static void main(String[] args){
        new Thread(new BuildJarTask()).start();
    }
}
