package pl.ross.timetrace.elements;

import javafx.scene.canvas.GraphicsContext;

/**
 * Created by Hp on 2017-05-05 10:29.
 */
public class Level {
    private String name;
    private double width;
    public Level(String name,double width) {
        this.width = width;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void initDraw(GraphicsContext gc,int x, int y){
        gc.strokeText(name,x,y);
        x += Element.PADDING_FROM_TEXT;
        gc.setLineDashes(15,10);
        gc.strokeLine(x,y,x+width,y);
        gc.setLineDashes((double[]) null);
    }

}
