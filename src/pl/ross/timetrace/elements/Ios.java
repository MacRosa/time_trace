package pl.ross.timetrace.elements;

import javafx.scene.canvas.GraphicsContext;

/**
 * Created by Hp on 2017-05-04 20:46.
 */
public interface Ios {
     String getName();
     
     void draw(GraphicsContext gc,int x, int y,int xe,int ye);

     void initDraw(GraphicsContext gc, int x, int y);

     double getWidth();

}
