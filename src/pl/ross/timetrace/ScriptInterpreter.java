package pl.ross.timetrace;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.StrokeLineCap;
import pl.ross.timetrace.elements.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hp on 2017-05-06 13:10.
 */
public class ScriptInterpreter {

    private static final int PHASE_INIT = 0;
    private static final int PHASE_GET_TRACES = 1;
    private static final int PHASE_DRAW_STATES = 2;



    private int phase;
    private TimeStatus status;

    private Map<String,Binary> discreteMap = new HashMap<>();

    private Map<String,String> mapValues(String line){
        String[] elements = line.split(" ");
        Map<String,String> values = new HashMap<>();
        for (String s : elements) {
            if(s.contains("=")){
                String[] pair = s.split("=");
                values.put(pair[0],pair[1]);
            }
        }
        return values;
    }

    private Map<String,String> current_map;

    private double state_len;

    private double x=50,y=50,width=1000,height=600,space=50, length=500;


    private GraphicsContext gc;
    private TraceContainer traceContainer;
    private boolean first_state;

    private double setValue(double val, String key){
        if(current_map.containsKey(key)){
            val = Double.parseDouble(current_map.get(key));
        }
        return val;
    }

    private void fieldSetup(String line){
        current_map = mapValues(line);
        width = setValue(width,"width");
        height = setValue(height,"height");
        x = setValue(x,"x");
        y = setValue(y,"y");
        space = setValue(space,"space");
        length = setValue(length,"len");
    }

    private void fieldInit(Canvas canvas){
        canvas.setHeight(height);
        canvas.setWidth(width);
        gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,width,height);
        traceContainer = new TraceContainer(x,y,space);
        levelMap.put("top",Analog.TOP);
        levelMap.put("bot",Analog.BOTTOM);
        trajectories.put("ccav",Analog.TRAJECTORY_CONCAVE);
        trajectories.put("cvex",Analog.TRAJECTORY_CONVEX);
    }

    private boolean no_number=false, no_line=false;

    private void setStatus(String line){
        current_map = mapValues(line);
        state_len = setValue(state_len,"len");
        no_number = line.contains("nonumber");
        no_line = line.contains("noline");
        String number_string = line.split(" ")[0];
        number_string = number_string.replace("#","");
        status.setNumber(Integer.parseInt(number_string));
    }

    private void drawStates(){
        status.draw(gc,(int)state_len, no_number);
        if(!no_line){
            status.drawLine(gc);
        }
    }

    private void addBinaryTrace(String line){
        String[] fields = line.split("\\s+");
        String name = fields[1];
        boolean highState = fields[2].equals("1");
        Binary binary = new Binary(name);
        binary.setInitialHigh(highState);
        discreteMap.put(name, binary);
        traceContainer.addIos(binary);
    }

    private void setDiscreteValue(String line){
        String[] words = line.split("\\s+");
        if(line.contains("at")){
            discreteMap.get(words[0]).at(Double.parseDouble(words[3])).doThis(binary -> binary.setHigh(words[1].equals("1")));
        }else{
            discreteMap.get(words[0]).setHigh(words[1].equals("1"));
        }
    }

    private Map<String,Analog> analogMap = new HashMap<>();
    private Map<String,Level> levelMap = new HashMap<>();

    private void addAnalogTrace(String line){
        String[] fields = line.split("\\s+");
        String name = fields[1];
        List<Level> levels = new ArrayList<>();
        for (int i = 2; i < fields.length; i++) {
            if(fields[i].equals("lv") && (i+1<fields.length)){
                Level level = new Level(fields[i+1],length);
                levels.add(level);
                levelMap.put(fields[i+1],level);
            }
        }
        Analog analog = new Analog(levels);
        analogMap.put(name,analog);
        traceContainer.addIos(analog);
    }

    private Map<String,Integer> trajectories = new HashMap<>();


    private void setAnalogValue(String line){
        String[] fields = line.split("\\s+");
        if(!(levelMap.containsKey(fields[2]) && levelMap.containsKey(fields[3]))){
            return;
        }
        String type = fields[1];
        double mid;
        switch(type){
            case "str":
                if(!line.contains("at")){
                    if(!line.contains("rng")){
                        analogMap.get(fields[0]).setStraightLine(levelMap.get(fields[2]),levelMap.get(fields[3]));
                    }else{
                        analogMap.get(fields[0]).setStraightLine(levelMap.get(fields[2]),levelMap.get(fields[3]),Double.parseDouble(fields[5]),Double.parseDouble(fields[6]));
                    }
                }else{
                    if(!line.contains("rng")){
                        analogMap.get(fields[0]).at(Double.parseDouble(fields[5])).doThis(analog -> analog.setStraightLine(levelMap.get(fields[2]),levelMap.get(fields[3])));
                    }else{
                        analogMap.get(fields[0]).at(Double.parseDouble(fields[8])).doThis(analog -> analog.setStraightLine(levelMap.get(fields[2]),levelMap.get(fields[3]),Double.parseDouble(fields[5]),Double.parseDouble(fields[6])));
                    }//poz str X2 X1 rng 0.5 0 at 0.5
                }
                break;
            case "arc":
                mid = Double.parseDouble(fields[4]);
                if(!line.contains("at")){
                    if(!line.contains("rng")){
                        analogMap.get(fields[0]).setArcLine(levelMap.get(fields[2]),levelMap.get(fields[3]),mid,trajectories.get(fields[5]));
                    }else{
                        analogMap.get(fields[0]).setArcLine(levelMap.get(fields[2]),levelMap.get(fields[3]),mid,trajectories.get(fields[5]),Double.parseDouble(fields[7]),Double.parseDouble(fields[8]));
                    }
                }else{
                    if(!line.contains("rng")){
                        analogMap.get(fields[0]).at(Double.parseDouble(fields[7])).doThis(analog -> analog.setArcLine(levelMap.get(fields[2]),levelMap.get(fields[3]), mid,trajectories.get(fields[5])));
                    }else{
                        analogMap.get(fields[0]).at(Double.parseDouble(fields[10])).doThis(analog -> analog.setArcLine(levelMap.get(fields[2]),levelMap.get(fields[3]), mid,trajectories.get(fields[5]),Double.parseDouble(fields[7]),Double.parseDouble(fields[8])));

                    }//poz arc X2 X1 0.5 cvex rng 0 0.5 at 0.5
                }
            default:
        }
    }
    //label KL <3s

    private void setLabel(String line){
        String[] fields = line.split("\\s+",3);
        discreteMap.get(fields[1]).setLabel(fields[2]);
    }



    /*
    public void bezierCurveTo(double xc1,
                          double yc1,
                          double xc2,
                          double yc2,
                          double x1,
                          double y1)
    *                 gc.beginPath();
                gc.moveTo(x1, y1);
                gc.arcTo(x2, y2, x3, y3, radius);
                gc.lineTo(xe, y + this.ye);
                gc.stroke();*/

    void drawArc(double x1, double y1, double x2, double y2, double xm, String trajectory){
        gc.beginPath();//x1,y1,xc1,yc1,xc2,yc2,x2,y2
        gc.moveTo(x1,y1);

        double xc = x1 + Math.abs(x2-x1)*xm;
        double yc = y1;
        switch(trajectory){
            case "cvex":
                yc = y2;
                break;
            case "ccav":
                yc = y1;
        }
        //double yc = y1;
        gc.bezierCurveTo(xc,yc,xc,yc,x2,y2);
        gc.stroke();
  /*      writePoint(gc,x1,y1,"x1y1");
        writePoint(gc,x2,y2,"x2y2");
        writePoint(gc,xc,yc,"xcyc");*/
    }

    void arcTests(String line){
        Map<String,String> values = mapValues(line);
     //   gc.beginPath();//x1,y1,xc1,yc1,xc2,yc2,x2,y2
        double x1 = Double.parseDouble(values.get("x1"));
        double y1 = Double.parseDouble(values.get("y1"));
        double x2 = Double.parseDouble(values.get("x2"));
        double y2 = Double.parseDouble(values.get("y2"));
        double xm = Double.parseDouble(values.get("xm"));
  /*      gc.moveTo(x1,y1);
        gc.bezierCurveTo(xc,yc,xc,yc,x2,y2);
        writePoint(gc,x1,y1,"x1y1");
        writePoint(gc,x2,y2,"x2y2");
        writePoint(gc,xc,yc,"xcyc");
        gc.stroke();*/
        drawArc(x1,y1,x2,y2,xm,values.get("traj"));
    }

    void writePoint(GraphicsContext gc,double x, double y,String name){
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineWidth(5);
        gc.strokeLine(x,y,x,y);
        gc.setLineWidth(1);
        gc.strokeText(name,x,y);
        gc.setLineCap(StrokeLineCap.SQUARE);
    }

    void runScript(Canvas canvas, String script){
        String lines[] = script.split("\n");

        phase = PHASE_INIT;
      //  gc = canvas.getGraphicsContext2D();
     //   fieldInit();
     /*   fieldInit(canvas);
            line = line.trim();
            if(line.equals("")){
                continue;
            }
            if(line.contains("arc")){
                arcTests(line);
            }*/
        for (String line : lines) {
            switch(phase){
                case PHASE_INIT:
                    if(line.contains("field")){
                        fieldSetup(line);
                        fieldInit(canvas);
                        phase = PHASE_GET_TRACES;
                        break;
                    }
                    fieldInit(canvas);
                    phase = PHASE_GET_TRACES;
                case PHASE_GET_TRACES:
                    if(!line.startsWith("#")){
                        if(line.startsWith("binary")){
                            addBinaryTrace(line);
                        }
                        if(line.startsWith("analog")){
                            addAnalogTrace(line);
                        }
                        break;
                    } else{
                        traceContainer.initDraw(gc);
                        status = new TimeStatus(0,traceContainer.getElements());
                        phase = PHASE_DRAW_STATES;
                        first_state = true;
                    }
                case PHASE_DRAW_STATES:
                    if(line.startsWith("#")){
                        if(first_state){
                            setStatus(line);
                            first_state = false;
                        }else{
                           drawStates();
                           setStatus(line);
                        }
                    }else if(line.startsWith("label")){
                        String name = line.split("\\s+")[1];
                        if(discreteMap.containsKey(name)){
                            setLabel(line);
                        }
                    }else{
                        String name = line.split("\\s+")[0];
                        if(discreteMap.containsKey(name)){
                            setDiscreteValue(line);
                        }
                        if(analogMap.containsKey(name)){
                           setAnalogValue(line);
                        }
                    }
            }

        }if(!first_state){
            drawStates();
        }
    }

}
