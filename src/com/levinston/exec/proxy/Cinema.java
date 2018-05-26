package com.levinston.exec.proxy;

/*
 * Summary :
 *
 * Cinema 是代理类。观众通过Cinema的play()方法来观看电影。
 * RealMovie 是真正被代理的对象。（Real Subject)
 * Movie 是能被代理的接口？？？
 * Cinema 最终通过调用RealMovie的 play（）方法来实现真正的播放
 *
 * 通过扩展代理类， 可以进行功能附加和增加。
 * 代理类和被代理类应当实现同一个接口， 或者是共同继承某个类。
 */
public class Cinema implements Movie {

    private RealMovie movie;

    public Cinema(RealMovie movie) {
        super();
        this.movie = movie;
    }

    @Override
    public void play() {
        playAd(true);
        movie.play();
        playAd(false);
    }

    public void playAd(boolean isStart){
        if ( isStart ) {
            System.out.println("电影马上开始了，爆米花、可乐、口香糖9.8折，快来买啊！");
        } else {
            System.out.println("电影马上结束了，爆米花、可乐、口香糖9.8折，买回家吃吧！");
        }
    }
}
