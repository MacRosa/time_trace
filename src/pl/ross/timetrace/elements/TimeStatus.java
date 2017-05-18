package pl.ross.timetrace.elements;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Created by Hp on 2017-05-04 20:27.
 */
public class TimeStatus {
    private static final double STATUS_NUMBER_PADDING = 20;
    private static final double STATUS_LINE_UP_MARGIN = 20;
    private static final double STATUS_LINE_DOWN_MARGIN = 30;
    private static final double STATUS_OVAL_SIZE = 25;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    private int number;
    private List<Element> elementList;

    public TimeStatus(int number, List<Element> elements) {
        this.number = number;
        this.elementList = elements;
    }

    public void draw(GraphicsContext gc, int width){
        double x = elementList.get(elementList.size()-1).getX();
        for (Element e: elementList) {
            e.draw(gc,width);
        }
        double y = elementList.get(elementList.size()-1).getY() + elementList.get(elementList.size()-1).getWidth();
        y += STATUS_NUMBER_PADDING;
        x += width/2 - STATUS_OVAL_SIZE/2;
        gc.strokeOval(x,y,STATUS_OVAL_SIZE,STATUS_OVAL_SIZE);
        Text text = new Text(Integer.toString(number));
        text.setFont(gc.getFont());
        double textWidth = (text.getLayoutBounds().getWidth());
        double textHeight = (text.getLayoutBounds().getHeight());
        double size = gc.getFont().getSize();
        x+=size;
        y+=size;
        gc.strokeText(Integer.toString(number),x-(textWidth/2),y+12.5-textHeight/2);
    }

    public void draw(GraphicsContext gc, int width,boolean last){
        if(!last){
            draw(gc,width);
            return;
        }
        double x = elementList.get(elementList.size()-1).getX();
        for (Element e: elementList) {
            e.draw(gc,width);
        }
    }

    public void drawLine(GraphicsContext gc){
        double x = elementList.get(0).getX();
        double yb = elementList.get(0).getY()- Binary.TRACE_LOW - STATUS_LINE_UP_MARGIN;
        double ye = elementList.get(elementList.size()-1).getY() + STATUS_LINE_DOWN_MARGIN*2 + elementList.get(elementList.size()-1).getWidth();
        gc.setLineDashes(15,10);
        gc.strokeLine(x,yb,x,ye);
        gc.setLineDashes((double[]) null);
    }

}
