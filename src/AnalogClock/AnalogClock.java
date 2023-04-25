package AnalogClock;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

public class AnalogClock extends Application {

	private Label clockLabel;
	private Timer stopwatchTimer;
	private boolean paused = false;
	private int mode = 0;
	private int minuteHand_len = 70;
	private int hourHand_len = 50;
	private int secondHand_len = 80;
	
    private Timeline timeline;
    Line hourHand, minuteHand, secondHand;    
    
    @Override
    public void start(Stage stage) {
    	 stage.setTitle("Clock / Stopwatch");
    	 Circle face = new Circle(100, 100, 90);
         face.setFill(Color.WHITE);

         hourHand = new Line(0, 0, 0, hourHand_len);
         hourHand.setStrokeWidth(5);
         hourHand.setStroke(Color.BLACK);

         minuteHand = new Line(0, 0, 0, minuteHand_len);
         minuteHand.setStrokeWidth(3);
         minuteHand.setStroke(Color.BLACK);

         secondHand = new Line(0, 0, 0, secondHand_len);
         secondHand.setStrokeWidth(1);
         secondHand.setStroke(Color.RED);
         
         clockLabel = new Label("0:00");
         clockLabel.setTranslateY(100);
        StackPane root = new StackPane();
        root.getChildren().addAll(face, hourHand, minuteHand, secondHand, clockLabel);

        Button pauseResumeButton = new Button("Pause");
	        pauseResumeButton.setOnAction(event -> {
	            if (!this.paused) {
	                timeline.pause();
	                this.paused = true;
	                pauseResumeButton.setText(mode == 0 ? "Resume" : "Start");
	            } else {
	            	timeline.play();
	            	this.paused = false;	                
	                pauseResumeButton.setText("Pause");
	            }
	        });

        ToggleGroup modeToggle = new ToggleGroup();
        RadioButton toggleButtonClock = new RadioButton("Clock");
        RadioButton toggleButtonWatch = new RadioButton("Stopwatch");
        toggleButtonClock.setToggleGroup(modeToggle);
        toggleButtonWatch.setToggleGroup(modeToggle);     
        modeToggle.selectToggle(toggleButtonClock);
        toggleButtonClock.setPrefWidth(100);
        toggleButtonWatch.setPrefWidth(100);
        
        
       modeToggle.selectedToggleProperty().addListener((e,o,n)->{
    	   if(n.equals(toggleButtonWatch)) {	
    		   this.start_time = LocalTime.MIN;
    		   this.mode = 1;    	
    		   this.timeline.playFromStart();
    		   this.paused = true;
    		   pauseResumeButton.setText("Start");
    		   this.stopwatchTimer = new Timer();
    		   
    		   TimerTask task = new TimerTask()
    		   {    	    			
    		       public void run()
    		       {
    		    	   if(!paused) {
    		          start_time = start_time.plusSeconds(1);
    		    	   }
    		       }
    		   };    		 
    		   
    		   stopwatchTimer.scheduleAtFixedRate(task, 0,1000);
    	   }
    	   else {
    		   this.start_time = null;
    		   this.mode = 0;    	
    		   this.timeline.playFromStart();
    		   this.paused = false;
    		   pauseResumeButton.setText("Pause");
    		   stopwatchTimer.cancel();
    	   }
       });
        
        
        HBox tbox = new HBox(toggleButtonClock, toggleButtonWatch);
	    tbox.alignmentProperty().set(Pos.CENTER);
        VBox vbox = new VBox(root, pauseResumeButton, tbox);        
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);

        Scene scene = new Scene(vbox, 220, 280);
        stage.setScene(scene);
        stage.show();
        
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> {   
                	LocalTime time = get_time();
                	set_hands(time);
                                  }),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    
    public void set_hands(LocalTime time) {
    	 int hour = time.getHour() % 12;
         int minute = time.getMinute();
         int second = time.getSecond();

         float hourRot = (hour * 30 + minute / 2);
         float minuteRot = minute * 6;
         float secondRot = second * 6;
         hourHand.setRotate(hourRot);
         minuteHand.setRotate(minuteRot);
         secondHand.setRotate(secondRot);
         
         minuteHand.setTranslateX(Math.sin(Math.toRadians(minuteRot))*(minuteHand_len/2));
         minuteHand.setTranslateY(-Math.cos(Math.toRadians(minuteRot))*(minuteHand_len/2));
         
         hourHand.setTranslateX(Math.sin(Math.toRadians(hourRot))*(hourHand_len/2));
         hourHand.setTranslateY(-Math.cos(Math.toRadians(hourRot))*(hourHand_len/2));

         secondHand.setTranslateX(Math.sin(Math.toRadians(secondRot))*(secondHand_len/2));
         secondHand.setTranslateY(-Math.cos(Math.toRadians(secondRot))*(secondHand_len/2));
    }
    LocalTime start_time = null;    
    public LocalTime get_time() {
    	if(mode == 0) {
    		var t = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
    		this.clockLabel.setText(t.format(DateTimeFormatter.ISO_TIME));
    		return t;
    	}
    	this.clockLabel.setText(start_time.format(DateTimeFormatter.ISO_TIME));
    	return start_time;
    }
     

    public static void main(String[] args) {
        launch();
    }
}