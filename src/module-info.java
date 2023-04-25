module AnalogClock {
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires java.desktop;
	
	opens AnalogClock to javafx.graphics, javafx.base;
}