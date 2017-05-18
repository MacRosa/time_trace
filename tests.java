private void scriptTest() {
        String simpleBinaryScript = "field width=1000 height=600 space=50 x=50 y=100\n" +
                "\n" +
                "binary KL 0\n" +
                "binary LD 0\n" +
                "\n" +
                "#1 len=100\n" +
                "\n" +
                "#2 len=100\n" +
                "KL 1\n" +
                "\n" +
                "#3 len=100\n" +
                "KL 0\n" +
                "LD 1\n" +
                "\n" +
                "#4 len=100\n" +
                "KL 1\n" +
                "LD 1\n" +
                "\n" +
                "#1 len=100 noline\n" +
                "KL 0\n" +
                "LD 0";
        String complexDiscrete = "field width=1000 height=600 space=50 x=50 y=50\n" +
                "\n" +
                "binary KL 0\n" +
                "binary LD 0\n" +
                "\n" +
                "#1 len=100\n" +
                "LD 1 at 0.2\n" +
                "LD 0 at 0.5\n" +
                "\n" +
                "#2 len=100\n" +
                "KL 1 \n" +
                "LD 1 at 0.5\n" +
                "\n";

       // String simpleBinaryStringFromFile = loadFromFile("example_simple_discrete.txt");
      //  String simpleBinaryStringFromFile2 = loadFromFile("example_simple_discrete2.txt");
   //     String complexBinaryStringFromFile = loadFromFile("example_complex_discrete.txt");
   //     String simpleAnalogFromFile = loadFromFile("example_simple_analog.txt");
     //   String simpleAnalogFromFile2 = loadFromFile("example_simple_analog2.txt");
      //  String simpleAnalogFromFile3 = loadFromFile("example_simple_analog3.txt");
  //      String complexAnalogFromFile = loadFromFile("example_complex_analog.txt");
     //   String complexAnalogFromFile2 = loadFromFile("example_complex_analog2.txt");
        String bothFromFile = loadFromFile("example_both.txt");
        ScriptInterpreter interpreter = new ScriptInterpreter();
        interpreter.runScript(canvas,bothFromFile);
    }

    private void analogComplexTest() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        List<Level> levels = new ArrayList<>();
        Level x1 = new Level("X1",500),x2 = new Level("X2",500);
        levels.add(x1);
        levels.add(x2);
        Analog analog = new Analog(levels);

        List<Element> elements = new ArrayList<>();
        elements.add(new Element(50,50,analog));

        elements.get(0).initDraw(gc);
        TimeStatus status = new TimeStatus(1,elements);
       // analog.setStraightLine(x2,x1,0.5,0);
        analog.at(0.0).doThis(analog1 -> analog1.setArcLine(x2,x1,0.25,Analog.TRAJECTORY_CONVEX,0,0));
        analog.at(0.2).doThis(analog1 -> analog1.setArcLine(x1,x2,0.2,Analog.TRAJECTORY_CONCAVE,0,0.75));
        analog.at(0.5).doThis(analog1 -> analog1.setStraightLine(x1,x2,0.25,0));
        status.draw(gc,100);
        status.drawLine(gc);
   /*     */
    }

    private void discreteComplexTest() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);

        Binary x1 = new Binary("X1");

        List<Element> elements = new ArrayList<>();
        elements.add(new Element(50,100,x1));

        for (Element e: elements) {
            e.initDraw(gc);
        }

        x1.setInitialHigh(false);

        TimeStatus status = new TimeStatus(1,elements);

        x1.at(0.2).doThis(binary -> binary.setHigh(true));
        x1.at(0.5).doThis(binary -> binary.setHigh(false));
        x1.at(0.75).doThis(binary -> binary.setHigh(true));
        x1.at(0.85).doThis(binary -> binary.setHigh(false));
        status.draw(gc,100);
        status.drawLine(gc);




    }

    void bothTest(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);

        Level x1 = new Level("X1",500);
        Level x2 = new Level("X2",500);

        List<Level> levels = new ArrayList<>();
        levels.add(x1);
        levels.add(x2);
        Analog poziom = new Analog(levels);

        Binary z1 = new Binary("Z1");
        Binary z2 = new Binary("Z2");
        Binary z3 = new Binary("Z3");

        TraceContainer traceContainer = new TraceContainer(50,50,50);
        traceContainer.addIos(poziom);
        traceContainer.addIos(z1);
        traceContainer.addIos(z2);
        traceContainer.addIos(z3);

        traceContainer.initDraw(gc);


        TimeStatus status = new TimeStatus(1,traceContainer.getElements());

        poziom.at(0.0).doThis(analog -> analog.setArcLine(Analog.BOTTOM,x2,0.5,Analog.TRAJECTORY_CONCAVE));
        poziom.at(0.5).doThis(analog -> analog.setStraightLine(x2,x1));
        z1.setInitialHigh(true);
        z2.setInitialHigh(false);
        z3.setInitialHigh(false);
        status.draw(gc,100);
        status.drawLine(gc);

        status.setNumber(2);
        poziom.at(0.0).doThis(analog -> analog.setArcLine(x1,Analog.TOP,0.55,Analog.TRAJECTORY_CONVEX));
        poziom.at(0.3).doThis(analog -> analog.setArcLine(Analog.TOP,x1,0.55,Analog.TRAJECTORY_CONCAVE));
        poziom.at(0.6).doThis(analog -> analog.setStraightLine(x1,x2));
        z1.setHigh(false);
        z3.setHigh(true);
        z3.setLabel("5s");
        status.draw(gc,100);
        status.drawLine(gc);

        status.setNumber(3);
        poziom.at(0.0).doThis(analog -> analog.setArcLine(x2,Analog.BOTTOM,0.55,Analog.TRAJECTORY_CONVEX));
        poziom.at(0.3).doThis(analog -> analog.setArcLine(Analog.BOTTOM,x2,0.55,Analog.TRAJECTORY_CONCAVE));
        poziom.at(0.6).doThis(analog -> analog.setStraightLine(x2,x1));
        z2.setHigh(true);
        z2.setLabel("<3s");
        z3.setHigh(false);
        status.draw(gc,100);
        status.drawLine(gc);

        status.setNumber(4);
        poziom.at(0.0).doThis(analog -> analog.setArcLine(x1,Analog.TOP,0.55,Analog.TRAJECTORY_CONVEX));
        poziom.at(0.3).doThis(analog -> analog.setArcLine(Analog.TOP,x1,0.55,Analog.TRAJECTORY_CONCAVE));
        poziom.at(0.6).doThis(analog -> analog.setStraightLine(x1,x2));
        z2.setHigh(false);
        z3.setHigh(true);
        status.draw(gc,100);
        status.drawLine(gc);

        status.setNumber(1);
        poziom.setArcLine(x2,Analog.BOTTOM,0.5,Analog.TRAJECTORY_CONVEX);
        z1.setHigh(true);
        z3.setHigh(false);
        status.draw(gc,100);
    }

    void writePoint(GraphicsContext gc,double x, double y,String name){
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineWidth(5);
        gc.strokeLine(x,y,x,y);
        gc.setLineWidth(1);
        gc.strokeText(name,x,y);
        gc.setLineCap(StrokeLineCap.SQUARE);
    }

    void arcTest(){
        double x1=50,y1=200,x2=50,y2=100,x3=200,y3=100;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.moveTo(x1,y1);
        gc.arcTo(x2,y2,x3,y3,Math.abs(y2-y3)+Math.abs(x2-x3));
        gc.lineTo(x3,y3);
        gc.stroke();



        gc.setLineCap(StrokeLineCap.ROUND);

        gc.setLineWidth(5);
        gc.strokeLine(x1,y1,x1,y1);
        gc.setLineWidth(1);
        gc.strokeText("X1",x1,y1);

        gc.setLineWidth(5);
        gc.strokeLine(x2,y2,x2,y2);
        gc.setLineWidth(1);
        gc.strokeText("X2",x2,y2);

        gc.setLineWidth(5);
        gc.strokeLine(x3,y3,x3,y3);
        gc.setLineWidth(1);
        gc.strokeText("X3",x3,y3);
    }

    void analogTest(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        List<Level> levels = new ArrayList<>();
        Level x1 = new Level("X1",500),x2 = new Level("X2",500);
        levels.add(x1);
        levels.add(x2);
        Analog analog = new Analog(levels);

        List<Element> elements = new ArrayList<>();
        elements.add(new Element(50,50,analog));

        elements.get(0).initDraw(gc);
        TimeStatus status = new TimeStatus(1,elements);
        analog.setArcLine(Analog.BOTTOM,x2,0.5,Analog.TRAJECTORY_CONCAVE);
    //    analog.setStraightLine(Analog.BOTTOM,x2,0.5,0);
        status.draw(gc,100);
        status.drawLine(gc);

        TimeStatus status2 = new TimeStatus(2,elements);
        analog.setStraightLine(x2,x1);
        status2.draw(gc,100);
        status2.drawLine(gc);

        TimeStatus status3 = new TimeStatus(3,elements);
        analog.setArcLine(x1,Analog.TOP,0.5,Analog.TRAJECTORY_CONVEX);
        status3.draw(gc,100);
        status3.drawLine(gc);
    }


    void discreteTest(){
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.BLUE);
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
   //     Binary.TRACE_LOW = gc.getFont().getSize();

        Binary kl = new Binary("KL");
        Binary ld = new Binary("LD");

        List<Element> elements = new ArrayList<>();
        elements.add(new Element(50,100,kl));
        elements.add(new Element(50,200,ld));

        kl.setInitialHigh(false);
        ld.setInitialHigh(false);

        for (Element e: elements) {
            e.initDraw(gc);
        }

        TimeStatus s1 = new TimeStatus(1,elements),
                s2 = new TimeStatus(2,elements),
                s3= new TimeStatus(3,elements),
                s4 = new TimeStatus(4,elements);

        s1.draw(gc,100);
        s1.drawLine(gc);
        kl.setHigh(true);
        s2.draw(gc,100);
        s2.drawLine(gc);
        kl.setHigh(false);
        ld.setHigh(true);
        s3.draw(gc,100);
        s3.drawLine(gc);
        kl.setHigh(true);
        s4.draw(gc,100);
        s4.drawLine(gc);
        kl.setHigh(false);
        ld.setHigh(false);
        s1.draw(gc,100);
        s1.drawLine(gc);
        kl.setHigh(true);
        s2.draw(gc,50,true);


 /*       Binary z1 = new Binary("Z1");
        Binary z2 = new Binary("Z2");
        Binary z3 = new Binary("Z3");


        List<Element> elements = new ArrayList<>();
        elements.add(new Element(50,50,z1));
        elements.add(new Element(50,100,z2));
        elements.add(new Element(50,150,z3));
        z1.setHigh(true);
        z1.setHigh(true);
        z2.setHigh(true);
        z2.setHigh(true);


        TimeStatus status = new TimeStatus(1,elements);
        status.draw(gc,100);
        status.drawLine(gc);

        TimeStatus status2 = new TimeStatus(2,elements);
        z2.setHigh(false);
        status2.draw(gc,100);
        status2.drawLine(gc);*/
        //..
    }