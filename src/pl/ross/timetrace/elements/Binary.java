package pl.ross.timetrace.elements;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Hp on 2017-05-04 20:20.
 */
public class Binary implements Ios {
    public static double TRACE_LOW = 30;
    private String name;
    private boolean high;
    private boolean changed;
    private String label = "";

    public void setLabel(String label) {
        this.label = label;
    }
    //  private double size;

    public interface PhaseChanges{
        void atPhase(Binary binary);
    }

    class Phase{


        PhaseChanges changes;
        double from;

        Phase(double from) {
            this.from = from;
        }

        double getFrom() {
            return from;
        }

        void setChanges(PhaseChanges changes) {
            this.changes = changes;
        }

        void change(Binary binary){
            changes.atPhase(binary);
        }
    }

    private Phase currentPhase;

    private List<Phase> phases;

    public Binary at(double time_phase){
        currentPhase = new Phase(time_phase);
        return this;
    }

    public void doThis(PhaseChanges changes){
        if(currentPhase == null)
            return;
        currentPhase.setChanges(changes);
        phases.add(currentPhase);
        currentPhase = null;
    }

    public Binary(String name) {
        this.name = name;
        this.high = false;
        this.changed = false;
        phases = new ArrayList<>();
    }

    public boolean isHigh() {
        return high;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setInitialHigh(boolean high){
        changed = false;
        this.high = high;
    }

    public void setHigh(boolean high) {
        changed = high != this.high;
        this.high = high;
    }

    private void drawLabel(GraphicsContext gc, int x, int y, int xe, int ye){
        /*
        double size = gc.getFont().getSize();
        double height = text.getLayoutBounds().getHeight();
        double h = y-(TRACE_LOW/2)+(height/2);
        gc.strokeText(name,x,h);*/
        Text text = new Text(label);
        text.setFont(gc.getFont());
        double height = text.getLayoutBounds().getHeight();
        double width = text.getLayoutBounds().getWidth();
        double h = y-(TRACE_LOW/2)+(height/2);
        double w = x+(xe-x)/2 -width/2;
        gc.strokeText(label,w,h);
        label = "";

    }

    @Override
    public String getName() {
        return name;
    }

    public void draw(GraphicsContext gc, int x, int y, int xe, int ye){
        if(phases.isEmpty()){
            if(isChanged()){
                gc.strokeLine(x,y,x,y-TRACE_LOW);
                changed = false;
            }
            if(isHigh()){
                gc.strokeLine(x,y-TRACE_LOW,xe,y-TRACE_LOW);
            }else{
                gc.strokeLine(x,y,xe,y);
            }
        }else{
            drawPhases(gc,x,y, xe,ye);
        }
        if(!Objects.equals(label, "")){
            drawLabel(gc,x,y,xe,ye);
        }
    }

    private void drawPhases(GraphicsContext gc, int x, int y, int xe, int ye) {
        double before = phases.get(0).getFrom();
        double x_before = x+(xe-x)*before;
        drawPhase(gc,x,y,x_before,ye);
        for (int i = 0; i < phases.size(); i++) {
            if(i != (phases.size()-1)){
                phases.get(i).change(this);
                double x_next = x+(xe-x)*phases.get(i+1).getFrom();
                drawPhase(gc,x_before,y,x_next,ye);
                x_before = x_next;
            }else{
                phases.get(i).change(this);
                drawPhase(gc,x_before,y,xe,ye);
            }
        }
        phases.clear();
    }

    private void drawPhase(GraphicsContext gc, double x, double y, double xe, double ye) {
        if(isChanged()){
            gc.strokeLine(x,y,x,y-TRACE_LOW);
        }
        if(isHigh()){
            gc.strokeLine(x,y-TRACE_LOW,xe,y-TRACE_LOW);
        }else{
            gc.strokeLine(x,y,xe,y);
        }
    }


    @Override
    public void initDraw(GraphicsContext gc, int x, int y) {
        Text text = new Text(name);
        double size = gc.getFont().getSize();
        double height = text.getLayoutBounds().getHeight();
        double h = y-(TRACE_LOW/2)+(height/2);
        gc.strokeText(name,x,h);
    }

    @Override
    public double getWidth() {
     //   return size;
        return TRACE_LOW;
    }

}
