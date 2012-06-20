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
void setup() {
  size(800, 600) ;
  colorMode(RGB, SHADES) ;

  frameRate(30) ;
}

// ------------------------------------------------------------------------------ main methods
void draw() {
  for (int x = 0 ; x < width ; x++) {
      for (int y = 0 ; y < height ; y++) {
          set(x,y, color(
          (x^x+y + delta) % SHADES, 
          (x+y^y + 3*delta) % SHADES, 
          (0.0001*-x*x+0.0001*-y*y + 2*delta) % SHADES, 
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
