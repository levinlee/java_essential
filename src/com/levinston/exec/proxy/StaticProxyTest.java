package com.levinston.exec.proxy;

public class StaticProxyTest {

    public static void main(String[] args) {
        RealMovie rm = new RealMovie();
        Movie movie = new Cinema(rm);
        movie.play();
    }
}
