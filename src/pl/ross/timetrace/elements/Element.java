package pl.ross.timetrace.elements;

import javafx.scene.canvas.GraphicsContext;

/**
 * Created by Hp on 2017-05-04 21:00.
 */
public class Element {
    public static final double PADDING_FROM_TEXT = 40;
    private int x,y;
    private Ios ios;

    public Element(int x, int y, Ios ios) {
        this.x = x;
        this.y = y;
        this.ios = ios;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public double getWidth() {return ios.getWidth();}

    public void setY(int y) {
        this.y = y;
    }

    public Ios getIos() {
        return ios;
    }

    public void setIos(Ios ios) {
        this.ios = ios;
    }

    public void initDraw(GraphicsContext gc){
        ios.initDraw(gc,x,y);
        x+=PADDING_FROM_TEXT;
    }

    public void draw(GraphicsContext gc,int width){

        ios.draw(gc,x,y,x+width,y);
        x+=width;
    }
}
