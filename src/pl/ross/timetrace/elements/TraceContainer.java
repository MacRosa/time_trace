package pl.ross.timetrace.elements;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hp on 2017-05-06 07:36.
 */
public class TraceContainer {

    private List<Element> elements;

    private double x;
    private double space;

    private double current_y;

    public TraceContainer(double x, double y,double space) {
        this.x = x;
        this.space = space;
        this.elements = new ArrayList<>();
        current_y = y;
    }

    public void addIos(Ios ios){
        elements.add(new Element((int)x,(int)current_y,ios));
        current_y += space + ios.getWidth();
    }

    public List<Element> getElements() {
        return elements;
    }

    public void initDraw(GraphicsContext gc){
        for (Element e :
                elements) {
            e.initDraw(gc);
        }
    }
}
