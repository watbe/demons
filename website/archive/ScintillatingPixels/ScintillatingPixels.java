import processing.core.*; 
import processing.xml.*; 

import java.applet.*; 
import java.awt.*; 
import java.awt.image.*; 
import java.awt.event.*; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class ScintillatingPixels extends PApplet {

/* ------------------------------------------------------------------- scintillatingPixels.pde
Intent
~~~~~~
This program is the starting point for the Scintillating Pixels contest.
The idea is to get to the most interesting moving pixel pattern with the simplest code.

Designed
~~~~~~~~
Dr Tim Brook  February 2007

Modified
~~~~~~~~

*/
// -------------------------------------------------------------------------------- initialise
// Global (i.e. PApplet-wide) constants and variables
final int SHADES = 256 ;   // No. of shades of grey (and No. of levels of red, green or blue)

int delta = 0 ;

// -------------------------------------------------
public void setup() {
  size(800, 600) ;
  colorMode(RGB, SHADES) ;

  frameRate(30) ;
}

// ------------------------------------------------------------------------------ main methods
public void draw() {
  for (int x = 0 ; x < width ; x++) {
      for (int y = 0 ; y < height ; y++) {
          set(x,y, color(
          (x^x+y + delta) % SHADES, 
          (x+y^y + 3*delta) % SHADES, 
          (0.0001f*-x*x+0.0001f*-y*y + 2*delta) % SHADES, 
          delta/10 % SHADES+1000)
          // --------------------- Modifications below this line only
               //((x+y<100)?((x>10)?128:0):255),
              // 255, //Green
              // 255, //Blue
              // 255  //Alpha
          // --------------------- Modifications above this line only
               );
      }
  }
  delta++ ;
  print(delta + "\n");
}
// -------------------------------------------------------------------------------------------

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#DFDFDF", "ScintillatingPixels" });
  }
}
