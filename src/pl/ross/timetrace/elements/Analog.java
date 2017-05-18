package pl.ross.timetrace.elements;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.StrokeLineCap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hp on 2017-05-05 10:27.
 */
public class Analog implements Ios {

    private List<Level> levelList;
    private static final double LEVEL_SPACE = 40;
    private static final double BOTTOM_PADDING = 20;
    private static final double TOP_PADDING = -20;

    public static final Level BOTTOM = new Level("BOTTOM",0);
    public static final Level TOP = new Level("TOP",0);


    public static final int TRAJECTORY_CONCAVE = 0;
    public static final int TRAJECTORY_CONVEX = 1;

    private int trajectory;

    private static final int TYPE_NONE = 0;
    private static final int TYPE_STRAIGHT = 1;
    private static final int TYPE_ARC = 2;
    private int type = TYPE_NONE;

    private double yb,ye;
    private double xm = 0.2;



    private Phase currentPhase;
    private List<Phase> phases;

    public Analog(List<Level> levelList) {
        this.levelList = levelList;
        this.phases = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "N";
    }

    public interface PhaseChanges{
        void atPhase(Analog analog);
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

        void change(Analog analog){
            changes.atPhase(analog);
        }
    }

    public Analog at(double time_phase){
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

    @Deprecated
    void drawArcOld(GraphicsContext gc,double x, double y, double xe, double ye){
        double x1 = x, y1 = y + this.yb, x2 = xe, y2 = y + this.yb, x3 = xe, y3 = y + this.ye;
        double radius = 0;
        switch (trajectory) {
            case TRAJECTORY_CONCAVE:
                x2 = xe;
                y2 = y + this.yb;
                x2 -= (xe - x) * xm;
                y2 -= (ye - y) * xm;
                radius = Math.abs(y2 - y3) + Math.abs(x2 - x3);
                break;
            case TRAJECTORY_CONVEX:
                x2 = x;
                y2 = y + this.ye;
                x2 += (xe - x) * xm;
                y2 += (ye - y) * xm;
                radius = Math.abs(y1 - y2) + Math.abs(x1 - x2);
        }
        gc.beginPath();
        gc.moveTo(x1, y1);
        gc.arcTo(x2, y2, x3, y3, radius);
        gc.lineTo(xe, y + this.ye);
        gc.stroke();
    }

    private void drawArc(GraphicsContext gc, double x1, double y1, double x2, double y2){
        double xc = x1 + Math.abs(x2-x1)*xm; y1 += yb;y2 += ye;
        double yc = y1;
        switch(trajectory){
            case TRAJECTORY_CONCAVE:
                yc = y1;
                break;
            case TRAJECTORY_CONVEX:
                yc = y2;
        }
        gc.beginPath();//x1,y1,xc1,yc1,xc2,yc2,x2,y2
        gc.moveTo(x1,y1);
        gc.bezierCurveTo(xc,yc,xc,yc,x2,y2);
        gc.stroke();
   /*     writePoint(gc,x1,y1,"x1y1");
        writePoint(gc,x2,y2,"x2y2");
        writePoint(gc,xc,yc,"xcyc");*/
    }



    void writePoint(GraphicsContext gc,double x, double y,String name){
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineWidth(5);
        gc.strokeLine(x,y,x,y);
        gc.setLineWidth(1);
        gc.strokeText(name,x,y);
        gc.setLineCap(StrokeLineCap.SQUARE);
    }

    @Override
    public void draw(GraphicsContext gc, int x, int y, int xe, int ye) {
        if(phases.isEmpty()) {
      //      double x1 = x, y1 = y + this.yb, x2 = xe, y2 = y + this.yb, x3 = xe, y3 = y + this.ye;

     /*       double radius = 0;
            switch (trajectory) {
                case TRAJECTORY_CONCAVE:
                    x2 = xe;
                    y2 = y + this.yb;
                    x2 -= (xe - x) * xm;
                    y2 -= (ye - y) * xm;
                    radius = Math.abs(y2 - y3) + Math.abs(x2 - x3);
                    break;
                case TRAJECTORY_CONVEX:
                    x2 = x;
                    y2 = y + this.ye;
                    x2 += (xe - x) * xm;
                    y2 += (ye - y) * xm;
                    radius = Math.abs(y1 - y2) + Math.abs(x1 - x2);
            }*/

            switch (type) {
                case TYPE_STRAIGHT:
                    gc.strokeLine(x, y + this.yb, xe, y + this.ye);
                    break;
                case TYPE_ARC:
          /*          gc.beginPath();
                    gc.moveTo(x1, y1);
                    gc.arcTo(x2, y2, x3, y3, radius);
                    gc.lineTo(xe, y + this.ye);
                    gc.stroke();*/
            //   drawArcOld(gc, x, y, xe, ye);
                drawArc(gc,x,y,xe,ye);
                default:
            }
        }else{
            drawPhases(gc,x,y,xe,ye);
        }
    }

    void drawPhases(GraphicsContext gc, float x, float y,float xe, float ye){
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

    private void drawPhase(GraphicsContext gc, double x, double y, double xe, double ye){
   //     double x1 = x, y1 = y + this.yb, x2 = xe, y2 = y + this.yb, x3 = xe, y3 = y + this.ye;

   /*     double radius = 0;
        switch (trajectory) {
            case TRAJECTORY_CONCAVE:
                x2 = xe;
                y2 = y + this.yb;
                x2 -= (xe - x) * xm;
                y2 -= (ye - y) * xm;
                radius = Math.abs(y2 - y3) + Math.abs(x2 - x3);
                break;
            case TRAJECTORY_CONVEX:
                x2 = x;
                y2 = y + this.ye;
                x2 += (xe - x) * xm;
                y2 += (ye - y) * xm;
                radius = Math.abs(y1 - y2) + Math.abs(x1 - x2);
        }*/


        switch (type) {
            case TYPE_STRAIGHT:
                gc.strokeLine(x, y + this.yb, xe, y + this.ye);
                break;
            case TYPE_ARC:
        /*        gc.beginPath();
                gc.moveTo(x1, y1);
                gc.arcTo(x2, y2, x3, y3, radius);
                gc.lineTo(xe, y + this.ye);
                gc.stroke();*/
            //    drawArcOld(gc, x, y, xe, ye);
                drawArc(gc,x,y,xe,ye);
            default:
        }
    }

    @Override
    public void initDraw(GraphicsContext gc, int x, int y) {
        for (Level l: levelList) {
            l.initDraw(gc,x,y);
            y+= LEVEL_SPACE;
        }
    }

    @Override
    public double getWidth() {
        return (levelList.size()-1)*LEVEL_SPACE + BOTTOM_PADDING;
    }

    private void setPoints(Level from,Level to){
        if(from.equals(BOTTOM)){
            yb = (levelList.size()-1)*LEVEL_SPACE + BOTTOM_PADDING;
        }
        if(from.equals((TOP))){
            yb = TOP_PADDING;
        }

        if(levelList.contains(from)){
            int index = levelList.indexOf(from);
            yb = index*LEVEL_SPACE;
        }

        if(to.equals(BOTTOM)){
            ye = (levelList.size()-1)*LEVEL_SPACE + BOTTOM_PADDING;
        }
        if(to.equals((TOP))){
            ye = TOP_PADDING;
        }

        if(levelList.contains(to)){
            int index = levelList.indexOf(to);
            ye = index*LEVEL_SPACE;
        }
    }

    public void setStraightLine(Level from,Level to){
        type = TYPE_STRAIGHT;
        setPoints(from,to);
    }

    public void setStraightLine(Level from,Level to,double btb,double bte){
        type = TYPE_STRAIGHT;
        setPoints(from,to,btb,bte);
    }

    private void setPoints(Level from,Level to,double btb,double bte){
        if(from.equals(BOTTOM)){
            yb = (levelList.size()-1)*LEVEL_SPACE + BOTTOM_PADDING;
        }
        if(from.equals((TOP))){
            yb = TOP_PADDING;
        }

        if(levelList.contains(from)){
            int index = levelList.indexOf(from);
            yb = index*LEVEL_SPACE;
        }

        if(to.equals(BOTTOM)){
            ye = (levelList.size()-1)*LEVEL_SPACE + BOTTOM_PADDING;
        }
        if(to.equals((TOP))){
            ye = TOP_PADDING;
        }

        if(levelList.contains(to)){
            int index = levelList.indexOf(to);
            ye = index*LEVEL_SPACE;
        }
        ye -= (ye-yb)*bte;
        yb += (ye-yb)*btb;
    }


    public void setArcLine(Level from, Level to, double middle, int trajectory){
        type = TYPE_ARC;
        this.trajectory = trajectory;
        xm = middle;
        setPoints(from,to);
    }

    public void setArcLine(Level from, Level to, double middle, int trajectory,double btb,double bte){
        type = TYPE_ARC;
        this.trajectory = trajectory;
        xm = middle;
        setPoints(from,to,btb,bte);
    }
}
