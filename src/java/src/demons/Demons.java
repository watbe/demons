package demons;

import processing.core.*;
import processing.xml.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.Image;
import java.util.*;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;

import gifAnimation.Gif;

/**
 * This project is done in the Eclipse IDE and exported via Processing. 
 * As a result, the structure of the code is slightly different.
 * 
 * This code is entirely written by Wayne Tsai - U5027622 for the major assignment
 * as part of the Australian National University course COMP1720.
 * 
 * Acknowledgements and Credits:
 * 
 * Java libraries:
 * 
 * Minim sound library by Damien Di Fede
 * Licensed under the LGPL. The license can be found in the same folder as the JAR file.
 * http://code.compartmental.net/tools/minim/
 * 
 * gifAnimation by extrapixel 2007
 * It is under a GPL license. The license can be found in the same folder as the JAR file. 
 * The GIFEncoder & GIFDecoder classes were written by Kevin Weiner. 
 * Please see the separate copyright notice in the headers of the GifDecoder & GifEncoder classes.
 * http://extrapixel.ch/processing/gifAnimation/
 * 
 * Music:
 * 
 * Ambient A by Music for the Minority
 * Licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) License
 * http://www.jamendo.com/en/album/10663
 * 
 * Ambient Samurai by Ichiro Nakagawa
 * Licensed under the Creative Commons Attribution 3.0 Unported (CC BY 3.0) License
 * http://www.jamendo.com/en/artist/Ambient_Samurai_-_Ichiro_NAKAGAWA
 * 
 * Sound Effects:
 * 
 * Rock Slide
 * Licensed under the Creative Commons Sampling Plus 1.0 License
 * http://soundbible.com/904-Rock-Slide.html
 * 
 * Built with Processing.
 * 
 * See the web page for more details about this project.
 * 
 * This project is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) 
 * 
 * You are free:
 * 		to Share — to copy, distribute and transmit the work
 * 		to Remix — to adapt the work
 * 
 * Under the following conditions:
 * 		Attribution — You must attribute the work in the manner specified by the author or licensor (but not in any way that suggests that they endorse you or your use of the work).
 * 		Noncommercial — You may not use this work for commercial purposes.
 * 		Share Alike — If you alter, transform, or build upon this work, you may distribute the resulting work only under the same or similar license to this one.
 * 
 * For more information and to read the full license text, visit the following URL:
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
public class Demons extends PApplet {

	/**
	 * This class contains the character methods for moving, drawing and colliding the character
	 */
	class Character {

		float x, y;
		float velX, velY;
		float acelX = 0.2f, acelY = 0.3f;
		float maxVel;
		float jumpAcceleration = -8f;
		float upBoost;
		int height = 57, width = 33;

		PImage[] characterSprites = new PImage[6];
		double frame;

		/**
		 * Constructor for a Character takes in an initial (x, y) coordinate
		 * 
		 * @param x
		 *            Start x position
		 * @param y
		 *            Start y position
		 */
		Character(float x, float y) {

			int frame = 0;
			this.x = x;
			this.y = y;
			this.maxVel = 3;

			for (int i = 0; i < 6; i++) {
				characterSprites[i] = loadImage("kid" + i + ".png");
			}

		}

		/**
		 * Method to draw player sprites based on speed and direction.
		 */
		void drawActor() {

			pushMatrix();
			translate((int) x, (int) y);

			if (velX >= 0) {
				image(characterSprites[(int) frame % 6], (int) -width / 2 - 12, (int) -height / 2 - 14, 50, 80);
			} else {
				pushMatrix();
				scale(-1.0f, 1.0f);
				image(characterSprites[(int) frame % 6], (int) -width / 2 - 12, (int) -height / 2 - 14, 50, 80);
				popMatrix();
			}

			popMatrix();

			// Increment Frame
			frame += Math.abs(velX / 15);

		}

		/**
		 * Calculates the velocity of the player based on which buttons are pressed.
		 */
		void move() {

			if (leftPressed) {
				velX -= acelX;
			} else if (velX < 0) {
				velX += acelX;
			}

			if (rightPressed) {
				velX += acelX;
			} else if (velX > 0) {
				velX -= acelX;
			}

			if (velX < 0.02 && velX > -0.02) {
				velX = 0;
			}

			if (velX > maxVel) {
				velX = maxVel;
			} else if (velX < -maxVel) {
				velX = -maxVel;
			}

		}

		/**
		 * Method to handle jumping by detecting whether the player is jumping already or not.
		 */
		void jump() {

			if (!jumping) {
				velY = jumpAcceleration;
				jump = true;
			} else {
				System.out.println("Can't jump in the air!");
			}
		}

		/**
		 * The updating method for the player.
		 */
		void tick() {

			// If we're not colliding
			if (collision((int) x, (int) y) == 1) {
				velY += acelY;

				// Once we are in the air, jump is false, jumping is true;
				if (jump) {
					jump = false;
					jumping = true;
				}

				// can't jump while falling so pretend that we are already jumping
				jumping = true;
			}

			if (!jump) {

				// If we're colliding with the ground, then stop moving
				// downwards, and set jumping to false
				if (collision((int) x, (int) y) == 0) {
					velY = 0;
					jumping = false;
				}

				// if we're underground, move the character up into air
				if (collision((int) x, (int) y) == 2) {
					while (collision((int) x, (int) y) == 2) {
						y--;
					}
					jumping = false;
				}
			}

			// Prevent player from going higher than the level
			while (player.y <= 0) {
				player.y--;
			}

			// update x, y
			x += velX;
			y += velY;
		}

		/**
		 * Checks for collision and returns values based on the result.
		 * 
		 * @param x
		 *            , y Points to check
		 * @return
		 *         0 - collision
		 *         1 - not colliding
		 *         2 - overcolliding
		 */
		Integer collision(int x, int y) {

			Color bottomLeft, bottomRight, underLeft, underRight;
			bottomLeft = getColor(maskImage.pixels[((y + height / 2) * maskImage.width + ((int) x - width / 2))]);
			bottomRight = getColor(maskImage.pixels[((y + height / 2) * maskImage.width + (x + width / 2))]);
			underLeft = getColor(maskImage.pixels[((y + height / 2 + 1) * maskImage.width + (x - width / 2 - 1))]);
			underRight = getColor(maskImage.pixels[((y + height / 2 + 1) * maskImage.width + (x + width / 2 + 1))]);

			ArrayList<Color> commands = new ArrayList<Color>();
			commands.add(bottomLeft);
			commands.add(bottomRight);
			commands.add(underLeft);
			commands.add(underRight);

			// Bottom point is colliding, top point isn't: stop moving
			if ((!(bottomRight.getRed() > 240) && underRight.getRed() > 240) && (!(bottomLeft.getRed() > 240) && underLeft.getRed() > 240)) {
				getCommand(commands);
				return 0;
			}

			// Both points are underground: move up (hills)
			if ((bottomRight.getRed() > 240 && underRight.getRed() > 240) || (bottomLeft.getRed() > 240 && underLeft.getRed() > 240)) {
				return 2;
			}

			// Both points are not colliding, then fall
			if ((!(bottomRight.getRed() > 240) && !(underRight.getRed() > 240)) && (!(bottomLeft.getRed() > 240) && !(underLeft.getRed() > 240))) {
				getCommand(commands);
				return 1;
			}

			// Run colour based commands (see mask image files for examples)
			getCommand(commands);

			// Colliding
			return 0;
		}
	}

	/**
	 * Class for drawing Scenes. Comes with a parallax effect
	 */
	class Scenery {

		String fileName;
		float vX;
		float vY;
		int layer; // Layers less than zero are behind the player's plane, layers greater than zero are above the player plane.
		PImage image;

		Scenery(String fileName, int layer, float vX, float vY) {

			this.fileName = fileName;
			this.image = loadImage(fileName);
			this.layer = layer;
			this.vX = vX;
			this.vY = vY;
		}

	}

	/**
	 * Class for Seeds. Seeds are spawned with a velocity and gets affected by gravity
	 * Upon collision, the plant a new tree.
	 */
	class Seed {

		float x, y;
		PVector vel;
		float accel = -0.08f;
		Boolean moving;

		Seed(float x, float y, PVector vel) {

			this.x = x;
			this.y = y;
			this.vel = vel;
			this.moving = true;

		}

		/**
		 * Updating method for Seed
		 */
		void tick() {

			// If out of bounds, stop moving
			if (x > levelWidth || x < 0 || y > levelHeight || y < 0) {
				moving = false;
			}

			// If moving, check for collision and update position
			if (moving) {

				// If not colliding, then move
				if (!colliding()) {

					vel.y -= accel;
					x += vel.x;
					y += vel.y;
					drawSeed();

					// Otherwise it's colliding, so grow some trees!
				} else {

					Random ran = new Random();

					if (ran.nextBoolean()) {
						trees.add(new Tree(60, new Point((int) (this.x), (int) this.y + 20), -1, 80 - trees.size(), treeCanvasBehind, 1));
					} else {
						trees.add(new Tree(60, new Point((int) (this.x), (int) this.y + 20), -1, 80 - trees.size(), treeCanvasFront, -1));
					}

					this.moving = false;

				}
			}

		}

		// Checks for collisions for the seed
		Boolean colliding() {

			Color point = getColor(maskImage.pixels[((int) y) * maskImage.width + ((int) x)]);

			if (point.getBlue() > 240 && point.getRed() > 240) {
				seeds.remove(this);
			}

			// If on ground, then colliding.
			return point.equals(groundColour);

		}

		/**
		 * Drawing method for the seed
		 */
		void drawSeed() {

			pushMatrix();
			translate((int) x, (int) y);
			fill(255);
			stroke(0);
			ellipseMode(CENTER);
			ellipse(0, 0, 5, 5);
			popMatrix();

		}
	}

	/**
	 * This class contains the methods for the tree, primarily for generation of the primary branch.
	 */
	class Tree {

		Point root;
		Integer windiness; // Sway
		Integer strength; // Size of trunks
		Integer depth; // depth of branches
		Integer speed = 10; // 1 is fastest
		Integer layer;
		PGraphics canvas; // The graphics to draw on
		long seed; // randomness between 0 and 1
		float maxDepth; // the size of the tree

		int tick; // to help with measuring stuff
		int currentIteration; // The current draw cycle
		boolean growing; // Whether it is growing or not

		ArrayList<Branch> branches = new ArrayList<Branch>();
		ArrayList<Branch> branchRemovalQueue = new ArrayList<Branch>();

		Tree(float maxHeight, Point root, Integer windiness, Integer strength, PGraphics canvas, Integer layer) {

			this.root = root;
			this.depth = 0;
			this.windiness = windiness;
			this.strength = strength;

			this.canvas = canvas;
			this.tick = 0;
			this.growing = true;

			this.maxDepth = (float) random(100, 150);

			if (layer == 1) {
				int colour = color(treeBaseColour.getRed() + noise(root.x) * 20, treeBaseColour.getGreen() + noise(root.x) * 20, treeBaseColour.getBlue() + noise(root.x) * 20);
				branches.add(new Branch(this, root, random(70, 80), PI * 0.8, strength, colour));
			} else {
				int colour = color(treeBaseColour.getRed(), treeBaseColour.getGreen(), treeBaseColour.getBlue());
				branches.add(new Branch(this, root, random(70, 80), PI * 0.8, strength, colour));
			}

		}

		/**
		 * Updates tree by updating all the branches in the tree
		 */
		void tick() {

			// Only update if there are branches to update
			if (branches.size() > 0) {

				// Iterator doesn't work here, hence the for loop
				for (int i = 0; i < branches.size(); i++) {

					Branch branch = branches.get(i);

					// If branch is drawing, then update branch, else...

					if (branch.drawing) {

						branch.tick();

					} else {

						// Remove branch from drawing queue
						branches.remove(i);
					}

				}

			}

		}

	}

	/**
	 * The recursive Branch method for drawing the branches.
	 * Draws branches step by step, and once it reaches the end,
	 * it spawns more branches if it hasn't reached the max depth yet.
	 */
	class Branch {

		Tree tree;
		Point start;
		double finalLength;
		double angle;
		double strength;
		Integer colour;

		boolean drawing;
		Point current;
		int currentLength;

		double branchIncrement;

		Branch(Tree tree, Point start, double finalLength, double angle, double strength, Integer colour) {

			this.tree = tree;
			this.start = start;
			this.angle = angle;
			this.strength = strength;
			this.finalLength = finalLength;
			this.colour = colour;

			drawing = true;
			currentLength = 0;
			this.current = new Point();
			branchIncrement = finalLength / strength / 2;

		}

		/**
		 * Updating method for branches.
		 */
		void tick() {

			// If we're still drawing, then update the length of the branch (to
			// get the growing effect)
			if (drawing) {

				// If the current length is less than the final length, we want
				// to keep drawing
				if (currentLength < finalLength) {

					current.x = (int) (start.x + (branchIncrement * Math.sin(angle)));
					current.y = (int) (start.y + (branchIncrement * Math.cos(angle)));

					tree.canvas.beginDraw();
					pushMatrix();

					tree.canvas.fill(colour);
					tree.canvas.noStroke();
					tree.canvas.ellipseMode(CENTER);

					rotate((float) angle);
					tree.canvas.ellipse(start.x, start.y, (float) (strength * 0.9), (float) (strength * 0.9));

					tree.canvas.endDraw();
					popMatrix();

					currentLength = currentLength + (int) (Math.sqrt(Math.pow(Math.abs(current.x - start.x), 2) + Math.pow(Math.abs(current.y - start.y), 2)));

					start.x = current.x;
					start.y = current.y;

					strength -= strength * 0.01;

					// If the current length is greater than the final length,
					// then the branches have finished drawing so generate new
					// branches recursively
				} else {

					// Remove current branch from the drawing loop
					drawing = false;

					if (tree.depth < tree.maxDepth) {

						// Increment depth
						tree.depth++;

						// Add new branches
						tree.branches.add(new Branch(tree, current, finalLength * 0.78, (angle + random(0.4f, 0.8f)), strength, colour));

						tree.branches.add(new Branch(tree, current, finalLength * 0.78, (angle - random(0.4f, 0.8f)), strength, colour));

					}

					tree.branches.remove(this);
				}
			}

		}

	}

	/**
	 * Contains the methods for Grass
	 */
	class Grass {

		float radius = 4;
		float dx = 0.0f;
		float dy = 0.0f;
		int x, y;
		int maxHeight;
		float angle;
		float wind;
		boolean drawn;
		PGraphics canvas;
		Color colorShade;
		boolean hidden;

		Grass(int x, int y, int maxHeight) {

			this.x = x;
			this.y = 1;
			while (!colliding()) {

				this.y++;
				if (this.y >= maskImage.height || this.x >= maskImage.width) {
					break;
				}

			}

			// sets grass to be just below the terrain for a better look.
			this.y -= 5;

			if (!hidden) {

				this.maxHeight = maxHeight;
				this.colorShade = new Color((int) (grassBaseColour.getRed() + noise(x) * 70), (int) (grassBaseColour.getGreen() + noise(x) * 80), (int) (grassBaseColour.getBlue() + noise(x) * 80));
				wind = 0.05f;

			}
		}

		/**
		 * Updating function for the Grass
		 * 
		 * @param layer
		 *            The grass layer to draw on
		 */
		void tick(int layer) {

			if (!hidden) {
				if (layer == 1) {
					canvas = grassCanvasBehind;
				} else {
					canvas = grassCanvasFront;
				}
				wind = 0.05f;

				// Pushes grass away based on the player's distance from the grass
				if (this.x < player.x && Math.abs(this.x - player.x) < 100 && Math.abs(this.x - player.x) != 0 && Math.abs(player.y - this.y) < 50) {
					wind += -0.5f * (1 / Math.abs(this.x - player.x));
				} else if (this.x > player.x && Math.abs(this.x - player.x) < 100 && Math.abs(this.x - player.x) != 0 && Math.abs(player.y - this.y) < 50) {
					wind += 0.5f * (1 / Math.abs(this.x - player.x));
				}

				if (wind > 0.1f) {
					wind = 0.1f;
				} else if (wind < -0.1f) {
					wind = 0.1f;
				}

				canvas.beginDraw();
				canvas.pushMatrix();
				canvas.translate(x, y);
				angle = PI / 2;
				canvas.rotate(angle);
				dx = radius * cos(angle);
				dy = radius * sin(angle);
				canvas.strokeWeight(1);
				canvas.stroke(colorShade.getRed(), colorShade.getGreen(), colorShade.getBlue());
				canvas.line(0, 0, radius, 0);
				canvas.popMatrix();

				for (int a = 2; a < maxHeight; a++) {
					canvas.pushMatrix();
					canvas.translate(x + dx, y + dy);
					angle = (float) (pow(a, 1.5f) * wind * noise((grassInc + x) * 0.01f) + 1.5f * PI);
					canvas.rotate(angle);
					dx += radius * cos(angle);
					dy += radius * sin(angle);
					canvas.line(0, 0, radius, 0);
					canvas.popMatrix();
				}

				canvas.endDraw();

				this.canvas = null;
			}

		}

		/**
		 * Checks for collisions for the grass to the ground.
		 * 
		 * @return Boolean based on whether its colliding.
		 */
		Boolean colliding() {

			Color point = getColor(maskImage.pixels[((int) y) * maskImage.width + ((int) x)]);

			if (point.getRed() > 240 && point.getBlue() > 240) {
				this.hidden = true;
				if (grasses.size() != 0) {

					grasses.remove(grasses.size() - 1);
				}
			}
			// If on ground, then colliding.
			return (point.getRed() > 240);

		}

	}

	/**
	 * Class for screen fades. Uses registerDraw()
	 */
	public class Fader {

		static final int TOWHITE = 1;
		static final int TOBLACK = 0;
		int iterations;
		int type;

		/**
		 * Drawing function for fades. If it is registered to draw, it will draw at the end of the public draw function (On top of everything else).
		 */
		public void draw() {

			if (iterations == 50) {
				if (type == TOBLACK) {
					finishedFadingIn = true;
				} else {
					finishedFadingOut = true;
				}
				app.unregisterDraw(this);
			} else {
				if (type == TOBLACK) {
					fill(0, 0, 0, 20);

				} else {
					fill(255, 255, 255, 20);
				}
				rect(0, 0, screenWidth, screenHeight);
			}

			iterations++;

		}

		Fader(int type) {

			if (type == TOBLACK) {
				finishedFadingIn = false;
			} else {
				finishedFadingOut = false;
			}

			this.type = type;
			app.registerDraw(this);
			iterations = 0;
		}

	}

	/**
	 * Class to simplify drawing text. It draws for a certain duration based on the length of the string.
	 */
	public class Texter {

		String text;
		int length;
		int startTime;
		boolean finished;
		Point point;

		public void draw() {

			fill(255);
			textFont(font);
			textSize(32);
			text(text, point.x, point.y);

			if (millis() > startTime + length) {
				finished = true;
				app.unregisterDraw(this);
			}

		}

		Texter(String text, int length, Point point) {

			this.point = point;

			// Minimum length
			if (length < 2000 && length >= 0) {
				length = 2000;
			}

			this.length = length;
			this.text = text;
			app.registerDraw(this);
			finished = false;
			startTime = millis();
		}

	}

	/**
	 * Class to simplify transitions
	 */
	class Trigger {

		boolean triggered;
		int triggerNumber;
		int min;
		int max;
		boolean loop;

		Trigger(int triggerNumber, int min, int max) {

			this.loop = false;
			this.triggerNumber = triggerNumber;
			this.min = min;
			this.max = max;
			this.triggered = false;
		}

		/**
		 * Secondary constructor for looping transitions
		 * 
		 * @param triggerNumber
		 * @param min
		 * @param max
		 * @param loop
		 */
		Trigger(int triggerNumber, int min, int max, boolean loop) {

			this.loop = loop;
			this.triggerNumber = triggerNumber;
			this.min = min;
			this.max = max;
			this.triggered = false;
		}

		/**
		 * Evaluates whether the conditions for the transition are true.
		 * If so, it activates the transition.
		 */
		void eval() {

			if (!triggered || loop) {
				if (player.x < max && player.x > min) {
					triggered = true;
					activate();
					if (!loop) {
						triggers.remove(this);
					}

				}
			}
		}

		/**
		 * Method to run transitions. Removes itself after it has been run if it's not looping.
		 */
		void activate() {

			runTrigger(triggerNumber);
			if (!loop) {
				triggers.remove(this);
			}

		}
	}

	/*
	 * Demons
	 * The main class that wraps everything. Contains the processing setup() and draw() functions.
	 */
	PApplet app = this; // needed for registerDraw functions for Fader

	PImage foreground;
	PImage maskImage;
	Integer levelWidth;
	Integer levelHeight;
	Minim minim;
	AudioPlayer music;

	Character player;

	PGraphics treeCanvasBehind;
	PGraphics treeCanvasFront;
	ArrayList<Tree> trees;
	double treeDepth;
	Color treeBaseColour;

	ArrayList<Scenery> scenes;

	ArrayList<Seed> seeds;
	Point lastSeedPos;

	PGraphics grassCanvasFront;
	PGraphics grassCanvasBehind;
	ArrayList<Grass> grasses;
	Point lastGrassPos;
	Color grassBaseColour;

	// Define the colours that are used in mask to determine collisions and commands
	final Color groundColour = new Color(255, 0, 0);
	final Color maskColour = new Color(255, 0, 255);
	final Color noRight = new Color(0, 255, 0);
	final Color noLeft = new Color(0, 0, 255);

	Integer offsetX, offsetY; // For scrolling screens

	PFont font;
	boolean mode;
	int targetFrameRate;

	// For jumping
	Boolean jump, jumping;

	Integer currentLevel = 0;

	// Key presses
	boolean leftPressed, rightPressed, spacePressed;

	// Transitions
	Fader fader;
	Texter texter;
	ArrayList<String> transitionText;
	ArrayList<Point> textPoints;
	int transitionType;
	boolean transitioning;
	boolean isFadingIn, isFadingOut;
	boolean finishedFadingIn, finishedFadingOut;
	boolean isTexting;
	boolean finishedTexting;
	int startTime;
	int time;

	boolean secret = false;

	final Integer screenWidth = 900;
	final Integer screenHeight = 555;

	// Grass
	float grassInc = 0.0f;

	float noiseScale = 0.02f;

	int iterations = 0;

	PImage sepia;
	boolean filmGrain;

	ArrayList<Trigger> triggers;
	Gif rockfall1, bridge1, boulder1, bridge2;

	/*
	 * Initialises the variables and loads content based on a defined level.
	 */
	public void setup() {

		filmGrain = false;

		seeds = new ArrayList<Seed>();
		scenes = new ArrayList<Scenery>();
		trees = new ArrayList<Tree>();
		grasses = new ArrayList<Grass>();

		jump = false;
		jumping = false;

		offsetX = 0;
		offsetY = 0;

		lastSeedPos = new Point(0, 0);
		lastGrassPos = new Point(0, 0);

		minim = new Minim(this);

		loadLevel(currentLevel);

		font = loadFont("SueEllenFrancisco.vlw");
		size(900, 555);
		noStroke();

		targetFrameRate = 30;
		frameRate(30);

		treeDepth = 0.0;

		music.loop();

	}

	/*
	 * Main drawing method used by Processing
	 */
	public void draw() {

		// Hides the cursor
		noCursor();

		if (!transitioning) {
			drawGame();
		} else {
			drawTransition();
		}

		// Draw debugging info
		// drawInfo();

	}

	/*
	 * Overrides the default stop functions in PApplet in an attempt to close things properly.
	 */
	public void stop() {

		music.close();
		minim.stop();
		super.stop();

	}

	/*
	 * Methods for key presses
	 */
	public void keyPressed() {

		if (keyCode == KeyEvent.VK_Q) {
			System.out.println("Exit key pressed.");
			System.exit(0);
		} else if (keyCode == KeyEvent.VK_R) {
			resetGame();
		} else if (keyCode == KeyEvent.VK_ENTER) {
			currentLevel++;
			resetGame();
		}

		if (keyCode == KeyEvent.VK_LEFT) {
			leftPressed = true;
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			rightPressed = true;
		}

		if (keyCode == KeyEvent.VK_UP) {
			player.jump();
		}

		if (keyCode == KeyEvent.VK_SHIFT) {
			player.maxVel = 10;
		}

		if (keyCode == KeyEvent.VK_6) {
			currentLevel = 6;
			resetGame();
		}

		if (keyCode == KeyEvent.VK_5) {
			currentLevel = 5;
			resetGame();
		}

		if (keyCode == KeyEvent.VK_4) {
			currentLevel = 4;
			resetGame();
		}

		if (keyCode == KeyEvent.VK_3) {
			currentLevel = 3;
			resetGame();
		}

		if (keyCode == KeyEvent.VK_2) {
			currentLevel = 2;
			resetGame();
		}

		if (keyCode == KeyEvent.VK_1) {
			currentLevel = 1;
			resetGame();
		}

		if (keyCode == KeyEvent.VK_0) {
			currentLevel = 0;
			resetGame();
		}

		if (keyCode == KeyEvent.VK_SPACE) {
			spacePressed = true;
		}

	}

	public void keyReleased() {

		if (keyCode == KeyEvent.VK_LEFT) {
			leftPressed = false;
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			rightPressed = false;
		}

		if (keyCode == KeyEvent.VK_SHIFT) {
			player.maxVel = 3;
		}

		if (keyCode == KeyEvent.VK_SPACE) {
			spacePressed = false;
		}

	}

	/**
	 * Methods for drawing the game
	 */
	public void drawGame() {

		// Calculate offset
		if ((player.x > screenWidth / 2) && (player.x < levelWidth - (screenWidth / 2))) {
			offsetX = (int) Math.abs(player.x - screenWidth / 2);
		} else if ((player.x >= levelWidth - (screenWidth / 2))) {
			offsetX = levelWidth - (screenWidth);
		} else {
			offsetX = 0;
		}

		pushMatrix();

		translate(-offsetX, -offsetY);

		// move the player
		player.move();

		// Iterate through trees
		for (Tree tree : trees) {
			tree.tick();
		}

		Arrays.fill(grassCanvasFront.pixels, 0);
		Arrays.fill(grassCanvasBehind.pixels, 0);

		// Generate a random grass blade
		randomGrass();

		// Generate a random seed
		randomSeed();

		// Iterate through scenes that are behind the player plane and draw them with a parallax effect
		for (Scenery scene : scenes) {
			if (scene.layer > 0) {

			}
			image(scene.image, (float) (offsetX * scene.vX), (float) (offsetY * scene.vY));
		}

		image(treeCanvasBehind, (float) (offsetX * treeDepth), 0);

		// Draw Grass
		image(grassCanvasBehind, 0, 0);
		int layer = 0;
		for (int i = 0; i < grasses.size(); i++) {

			Grass grass = grasses.get(i);

			// Alternates grass layers
			if (layer == 1) {
				layer = 2;
			} else {
				layer = 1;
			}

			// Only draw and update grass that can be seen, give or take 100
			// pixels
			if (grass.x > offsetX - 100 && grass.x < offsetX + screenWidth + 100) {
				grass.tick(layer);
			}

		}

		grassInc--;

		// Draw seeds
		for (int i = 0; i < seeds.size(); i++) {
			Seed seed = seeds.get(i);
			seed.tick();
		}

		// Update player and draw player
		player.tick();
		player.drawActor();

		image(grassCanvasFront, 0, 0);

		// Draw triggers
		drawTriggers();

		// Draws trees in front of the player with a parallax effect
		image(treeCanvasFront, (float) (0 - offsetX * treeDepth), 0);

		// Iterate through scenes that are in front of the player plane and draw
		// them with a parallax effect
		for (Scenery scene : scenes) {
			if (scene.layer <= 0) {
				image(scene.image, (float) (offsetX * scene.vX), (float) (offsetY * scene.vY));
			}
		}

		popMatrix();

		// Draw filmGrain if required
		if (filmGrain) {
			drawFilmGrain();
		}

		// Evaluate triggers
		if (triggers != null && !triggers.isEmpty()) {
			for (int i = 0; i < triggers.size(); i++) {
				triggers.get(i).eval();
			}
		}

		// draw level text
		drawLevelText(currentLevel);

	}

	/**
	 * Draws triggers and earthquakes when required
	 */
	public void drawTriggers() {

		if ((rockfall1 != null && rockfall1.isPlaying()) || (bridge1 != null && bridge1.isPlaying()) || (boulder1 != null && boulder1.isPlaying()) || (bridge2 != null && bridge2.isPlaying())) {
			offsetY = 0;
			offsetY += (int) random(-40, 40);
			offsetX += (int) random(-40, 40);
		} else {
			offsetY = 0;
		}

		if (rockfall1 != null) {

			image(rockfall1, 740, -335);

		}

		if (bridge1 != null) {
			image(bridge1, 2777, 351);
			if (player.x > 2777 && player.x < 2993 && player.y > 500) {
				die();
				resetGame();
			}

		}

		if (bridge2 != null) {
			image(bridge2, 3367, 475);
			if (player.x > 3467 && player.y > 500) {
				currentLevel++;
				resetGame();
			}

		}

		if (boulder1 != null) {
			if (boulder1.isPlaying() && boulder1.currentFrame() > 8) {
				if (player.x > 1210 && player.x < 1427) {
					die();
					resetGame();
				}
			}
			image(boulder1, 1205, -256);
		}
	}

	/**
	 * For debugging, this shows the current player location on the screeen and the framerates.
	 */
	public void drawInfo() {

		fill(255);
		textFont(font);
		textSize(32);
		text("player location: " + (int) player.x + ", " + (int) player.y, 5, height - 70);
		text("target frame rate: " + targetFrameRate, 5, height - 40);
		text("current frame rate: " + round(frameRate), 5, height - 10);
	}

	/**
	 * Draws film grain for the last level
	 */
	public void drawFilmGrain() {

		int offsetG = (int) random(-20, 0);
		int offsetH = (int) random(-20, 0);

		for (int i = 1; i < screenWidth - offsetG; i += 100) {
			for (int j = 1; j < screenHeight - offsetH; j += 100) {
				image(sepia, i + offsetG, j + offsetH);
			}
		}

		for (int i = 0; i < screenWidth; i++) {
			if ((int) random(0, 350) == 1) {
				stroke(255, random(10, 30));
				line(i, 0, i, screenHeight);
			} else if ((int) random(0, 200) == 1) {
				int ran = (int) random(1, screenHeight);
				stroke(255, random(200, 255));
				line(i, ran, i, ran + 1);
			}
		}

		// for wobby y axis (old movie style)

		offsetY = 0;

		if ((int) random(0, 20) == 5) {
			offsetY += (int) random(-20, 20);
		} else {
			offsetY += 0;
		}

	}

	/**
	 * Respawns the player at the start of the same level
	 */
	public void die() {

		transitionText = new ArrayList<String>();
		textPoints = new ArrayList<Point>();
		transitionText.add("Claire was hurt. Try again.");
		textPoints.add(new Point(350, 250));
		transitionType = 0;
		startTransition();

	}

	/**
	 * Wrapper function for resetting the game
	 */
	public void resetGame() {

		setup();

	}

	/**
	 * Generates a random seed to plant trees
	 */
	public void randomSeed() {

		Random ran = new Random();
		if (ran.nextInt(20) == 3 && (player.x - lastSeedPos.x) > 50) {
			seeds.add(new Seed(player.x, player.y, new PVector(random(0, 8), -random(2, 4))));
			lastSeedPos.x = (int) player.x;
		}
	}

	/**
	 * Generates grass
	 */
	public void randomGrass() {

		Random ran = new Random();

		if (player.x - lastGrassPos.x > 1) {
			for (int i = 0; i < Math.abs(player.x - lastGrassPos.x); i++) {
				noiseDetail(2, 0.0f);

				if (noise(player.x + i) > 0.3) {
					grasses.add(new Grass((int) player.x + i - 10, screenHeight, (int) (10 + noise((int) (player.x + i) ^ 2) * 10)));

				}
				// grasses.add(new Grass((int) player.x+i-10, screenHeight,(int)
				// (noise((int) (player.x+i) ^ 2) * 15)));
			}
			lastGrassPos.x = (int) player.x;

		}

	}

	/**
	 * Switch cases for loading levels
	 * 
	 * @param level
	 */
	public void loadLevel(Integer level) {

		switch (level) {
		case 0:
			if (music != null) {
				music.close();
			}
			music = minim.loadFile("neutral.mp3");
			treeBaseColour = new Color(63, 41, 0);
			grassBaseColour = new Color(13, 133, 21);
			player = new Character(110f, 395f);
			scenes.add(new Scenery("level0_5.jpg", 5, 0.5f, 0));
			scenes.add(new Scenery("level0_2.png", 2, 0.9f, 0));
			scenes.add(new Scenery("level0_1.png", 1, 0.2f, 0));
			scenes.add(new Scenery("level0_0.png", 0, 0, 0));
			levelWidth = scenes.get(scenes.size() - 1).image.width;
			levelHeight = scenes.get(scenes.size() - 1).image.height;
			scenes.add(new Scenery("level0_-1.png", -1, 0, 0));
			maskImage = loadImage("level0_mask.png");
			lastGrassPos.x = 650;
			lastSeedPos.x = 650;
			maskImage.loadPixels();
			grassCanvasFront = createGraphics(levelWidth, levelHeight, P2D);
			grassCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);
			treeCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);
			treeCanvasFront = createGraphics(levelWidth, levelHeight, P2D);

			initialiseTriggers();

			triggers.add(new Trigger(5, levelWidth - 50, levelWidth));

			break;

		case 1:

			rockfall1 = null;
			bridge1 = null;
			boulder1 = null;
			bridge2 = null;

			grassBaseColour = new Color(0, 0, 0);
			treeBaseColour = new Color(0, 0, 0);
			scenes.add(new Scenery("city_backdrop.png", 4, 0.5f, 0));
			scenes.add(new Scenery("city_moon.png", 2, 0, 0));
			scenes.add(new Scenery("city1.png", 1, 0, 0));
			scenes.add(new Scenery("city_ground.png", 0, 0, 0));
			levelWidth = scenes.get(scenes.size() - 1).image.width;
			levelHeight = scenes.get(scenes.size() - 1).image.height;
			scenes.add(new Scenery("city_lights.png", -2, 0, 0));
			// scenes.add(new Scenery("city_fog.png", -3, -0.5f, 0));
			maskImage = loadImage("city_mask.png");

			lastGrassPos.x = 6000;
			lastSeedPos.x = 6000;

			grassCanvasFront = createGraphics(levelWidth, levelHeight, P2D);
			grassCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);
			treeCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);
			treeCanvasFront = createGraphics(levelWidth, levelHeight, P2D);

			initialiseTriggers();

			triggers.add(new Trigger(11, 325, 395, true));
			triggers.add(new Trigger(12, 570, 640, true));
			triggers.add(new Trigger(13, 1280, 1350, true));
			triggers.add(new Trigger(14, 1765, 1835, true));
			triggers.add(new Trigger(15, 2010, 2080, true));
			triggers.add(new Trigger(16, 2535, 2605, true));
			triggers.add(new Trigger(17, 3190, 3260, true));
			triggers.add(new Trigger(18, 3790, 3860, true));

			player = new Character(110f, 395f);

			break;

		case 2:
			music.close();
			music = minim.loadFile("mood.mp3");

			grassBaseColour = new Color(0, 0, 0);
			treeBaseColour = new Color(0, 0, 0);

			scenes.add(new Scenery("level1_5.png", 5, 0.5f, 0));
			scenes.add(new Scenery("level1_1.png", 1, 0, 0));
			scenes.add(new Scenery("level1_0.png", 0, 0, 0));
			levelWidth = scenes.get(scenes.size() - 1).image.width;
			levelHeight = scenes.get(scenes.size() - 1).image.height;
			scenes.add(new Scenery("level1_-1.png", -1, 0, 0));
			// scenes.add(new Scenery("level1_-2.png", -2, -0.5f, 0));
			maskImage = loadImage("level1_mask.png");

			lastGrassPos.x = 400;
			lastSeedPos.x = 900;

			rockfall1 = new Gif(this, "rocks.gif");
			bridge1 = new Gif(this, "bridge1.gif");

			grassCanvasFront = createGraphics(levelWidth, levelHeight, P2D);
			grassCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);

			treeCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);
			treeCanvasFront = createGraphics(levelWidth, levelHeight, P2D);

			initialiseTriggers();

			// TODO Add triggers
			triggers.add(new Trigger(0, 965, 1200));
			triggers.add(new Trigger(1, 2840, 3000));
			triggers.add(new Trigger(4, levelWidth - 50, levelWidth));

			preloadScene(200);

			player = new Character(110f, 395f);

			break;
		case 3:
			rockfall1 = null;
			bridge1 = null;
			boulder1 = null;
			bridge2 = null;

			grassBaseColour = new Color(0, 0, 0);
			treeBaseColour = new Color(0, 0, 0);
			scenes.add(new Scenery("city_backdrop.png", 4, 0.5f, 0));
			scenes.add(new Scenery("city_moon.png", 2, 0, 0));
			scenes.add(new Scenery("city2.png", 1, 0, 0));
			scenes.add(new Scenery("city_ground.png", 0, 0, 0));
			levelWidth = scenes.get(scenes.size() - 1).image.width;
			levelHeight = scenes.get(scenes.size() - 1).image.height;
			scenes.add(new Scenery("city_lights.png", -2, 0, 0));
			// scenes.add(new Scenery("city_fog.png", -3, -0.5f, 0));
			maskImage = loadImage("city_mask.png");

			lastGrassPos.x = 6000;
			lastSeedPos.x = 6000;

			grassCanvasFront = createGraphics(levelWidth, levelHeight, P2D);
			grassCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);
			treeCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);
			treeCanvasFront = createGraphics(levelWidth, levelHeight, P2D);

			initialiseTriggers();

			triggers.add(new Trigger(21, 345, 345 + 70, true));
			triggers.add(new Trigger(22, 815, 815 + 70, true));
			triggers.add(new Trigger(23, 1200, 1200 + 70, true));
			triggers.add(new Trigger(24, 1630, 1630 + 70, true));
			triggers.add(new Trigger(25, 1880, 1880 + 70, true));
			triggers.add(new Trigger(26, 2340, 2340 + 70, true));
			triggers.add(new Trigger(27, 2820, 2820 + 70, true));
			triggers.add(new Trigger(28, 3715, 3715 + 70, true));

			player = new Character(110f, 395f);

			break;

		case 4:

			rockfall1 = null;
			bridge1 = null;
			boulder1 = null;

			grassBaseColour = new Color(0, 0, 0);
			treeBaseColour = new Color(0, 0, 0);
			scenes.add(new Scenery("level1_5.png", 5, 0.5f, 0));
			scenes.add(new Scenery("level2_1.png", 1, 0, 0));
			scenes.add(new Scenery("level2_0.png", 0, 0, 0));
			levelWidth = scenes.get(scenes.size() - 1).image.width;
			levelHeight = scenes.get(scenes.size() - 1).image.height;
			scenes.add(new Scenery("level2_-2.png", -2, 0.5f, 0));
			maskImage = loadImage("level2_mask.png");

			lastGrassPos.x = 400;
			lastSeedPos.x = 900;

			boulder1 = new Gif(this, "boulder1.gif");
			bridge2 = new Gif(this, "bridge2.gif");

			grassCanvasFront = createGraphics(levelWidth, levelHeight, P2D);
			grassCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);

			treeCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);
			treeCanvasFront = createGraphics(levelWidth, levelHeight, P2D);

			initialiseTriggers();

			triggers.add(new Trigger(2, 1180, 1300));
			triggers.add(new Trigger(3, 3467, 3600));

			preloadScene(400);

			player = new Character(100f, 170f);

			break;
		case 5:
			rockfall1 = null;
			bridge1 = null;
			boulder1 = null;
			bridge2 = null;

			grassBaseColour = new Color(0, 0, 0);
			treeBaseColour = new Color(0, 0, 0);

			scenes.add(new Scenery("city_backdrop.png", 4, 0.5f, 0));
			scenes.add(new Scenery("city_moon.png", 2, 0, 0));
			scenes.add(new Scenery("city3.png", 1, 0, 0));
			scenes.add(new Scenery("city_ground.png", 0, 0, 0));
			levelWidth = scenes.get(scenes.size() - 1).image.width;
			levelHeight = scenes.get(scenes.size() - 1).image.height;
			scenes.add(new Scenery("city_lights.png", -2, 0, 0));
			scenes.add(new Scenery("city_fog.png", -3, 0.5f, 0));
			maskImage = loadImage("city_mask.png");

			lastGrassPos.x = 6000;
			lastSeedPos.x = 6000;

			grassCanvasFront = createGraphics(levelWidth, levelHeight, P2D);
			grassCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);
			treeCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);
			treeCanvasFront = createGraphics(levelWidth, levelHeight, P2D);

			initialiseTriggers();

			triggers.add(new Trigger(31, 400, 400 + 70, true));
			triggers.add(new Trigger(32, 880, 880 + 70, true));
			triggers.add(new Trigger(33, 1520, 1520 + 70, true));
			triggers.add(new Trigger(34, 1980, 1980 + 70, true));
			triggers.add(new Trigger(35, 2410, 2410 + 70, true));
			triggers.add(new Trigger(36, 2815, 2815 + 140, true));
			triggers.add(new Trigger(37, 3600, 3600 + 70, true));
			triggers.add(new Trigger(38, 400, 400 + 70, true));

			player = new Character(110f, 50f);

			break;
		case 6:
			music.close();
			music = minim.loadFile("neutral.mp3");
			filmGrain = true;
			grassBaseColour = new Color(188, 160, 86);
			treeBaseColour = new Color(50, 50, 50);
			sepia = loadImage("sepia.png");
			scenes.add(new Scenery("level0_5.jpg", 5, 0.5f, 0));
			scenes.add(new Scenery("level0_2.png", 2, 0.9f, 0));
			scenes.add(new Scenery("level0_1.png", 1, 0.2f, 0));
			scenes.add(new Scenery("level0_0.png", 0, 0, 0));
			levelWidth = scenes.get(scenes.size() - 1).image.width;
			levelHeight = scenes.get(scenes.size() - 1).image.height;
			scenes.add(new Scenery("level0_-1.png", -1, 0, 0));
			maskImage = loadImage("level0_mask.png");
			lastGrassPos.x = 650;
			lastSeedPos.x = 650;
			maskImage.loadPixels();

			for (Scenery scene : scenes) {
				scene.image.filter(GRAY);
			}
			grassCanvasFront = createGraphics(levelWidth, levelHeight, P2D);
			grassCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);
			treeCanvasBehind = createGraphics(levelWidth, levelHeight, P2D);
			treeCanvasFront = createGraphics(levelWidth, levelHeight, P2D);

			preloadScene(500);

			initialiseTriggers();
			triggers.add(new Trigger(60, 350, 580, true));

			player = new Character(3900f, 395f);
			break;
		default:
			break;
		}
	}

	/**
	 * Switch cases for drawing text based on levels.
	 * 
	 * @param level
	 */
	public void drawLevelText(Integer level) {

		switch (level) {
		case 0:
			levelText(0, 150, player.x - 50, player.y - 50, "This is Claire.", false);
			levelText(350, 580, 350, 100, "This is the house she grew up in.", true);
			levelText(1000, 1200, 1000, 250, "She's creating memories right now.", true);
			levelText(1500, 1700, 1500, 400, "Unique memories.", true);
			levelText(2000, 2200, 2000, 250, "Memories that only she can see.", true);
			levelText(3000, 3300, 3000, 300, "But soon it will not be the same...", true);
			levelText(3300, 3600, 3300, 400, "...because Claire has Dementia.", true);
			levelText(3800, 4000, player.x - 80, player.y - 50, "This is her journey.", true);
			levelText(3600, 4000, 3500, 200, "This is the beginning of an end.", true);
			break;
		case 1:
			levelText(100, 300, 100, 400, "Press Space to open doors", true);
			break;
		case 2:
			levelText(200, 500, 100, 300, "Dementia affects the memory.", true);
			levelText(500, 700, 500, 250, "Claire's world starts falling apart.", true);
			levelText(1000, 1200, 1000, 400, "Piece by piece.", true);
			levelText(1500, 1700, 1500, 450, "Memory by memory.", true);
			break;
		case 3:
			levelText(100, 300, 100, 400, "Press Space to open doors", true);
			break;
		case 4:
			break;
		case 5:
			levelText(100, 300, 100, 400, "Press Space to open doors", true);
			break;
		case 6:
			levelText(3800, 4000, 3200, 400, "Alzheimer's Disease is a growing problem in aging populations.", true);
			levelText(3400, 3600, 3400, 500, "Dementia is one of its many forms.", true);
			levelText(2900, 3200, 2900, 250, "It is poorly understood by family and friends.", true);
			levelText(2500, 2700, 2500, 300, "Claire is just one journey of many.", true);
			levelText(2100, 2300, 2100, 200, "However there is a redeeming feature of Dementia.", true);
			levelText(1800, 2000, 1800, 400, "The earliest memories are the last to go.", true);
			levelText(1400, 1600, 1400, 300, "Memories that have been locked away for years, surface.", true);
			levelText(1100, 1300, 1100, 250, "For Claire, the end might not be so bad after all.", true);
			levelText(700, 900, 700, 350, "Dedicated to John and Val Scott", true);
			levelText(350, 580, 350, 100, "Press SPACE to start over.", true);
		default:
			break;
		}
	}

	/**
	 * Draws the level text based on player location
	 * 
	 * @param min
	 *            The minimum x distance in order to draw
	 * @param max
	 *            The max
	 * @param x
	 *            coordinate of the x position
	 * @param y
	 *            coordinate of the y position
	 * @param text
	 *            The text to draw
	 * @param isStatic
	 *            Does it move or not?
	 */
	public void levelText(float min, float max, float x, float y, String text, boolean isStatic) {

		if (player.x < max && player.x > min) {
			if (isStatic) {
				texter = new Texter(text, -1, new Point((int) (x - offsetX), (int) (y - offsetY)));
			} else {
				texter = new Texter(text, -1, new Point((int) (x + offsetX), (int) (y + offsetY)));
			}
		}

	}

	/**
	 * Initialises triggers
	 */
	public void initialiseTriggers() {

		if (triggers != null) {
			triggers.clear();
		} else {
			triggers = new ArrayList<Trigger>();
		}
	}

	/**
	 * Switch cases for level triggers
	 * 
	 * @param number
	 */
	public void runTrigger(int number) {

		switch (number) {
		case 0:
			AudioPlayer rockSound = minim.loadFile("rockslide.mp3");
			rockfall1.play();
			maskImage = loadImage("level1_mask1.png");
			rockSound.play();
			break;
		case 1:
			AudioPlayer bridgeSound = minim.loadFile("rockslide.mp3");
			bridge1.play();
			maskImage = loadImage("level1_mask2.png");
			bridgeSound.play();
			break;
		case 2:
			AudioPlayer boulderSound = minim.loadFile("rockslide.mp3");
			boulder1.play();
			boulderSound.play();
			maskImage = loadImage("level2_mask1.png");
			break;
		case 3:
			AudioPlayer dropSound = minim.loadFile("rockslide.mp3");
			bridge2.play();
			dropSound.play();
			maskImage = loadImage("level2_mask2.png");
			break;
		case 4:
			transitionText = new ArrayList<String>();
			textPoints = new ArrayList<Point>();

			transitionText.add("Claire's memory grows more frail with every passing day.");
			textPoints.add(new Point(100, 250));

			transitionType = 0;

			startTransition();

			currentLevel++;

			break;
		case 5:
			transitionText = new ArrayList<String>();
			textPoints = new ArrayList<Point>();

			transitionText.add("80 years later...");
			textPoints.add(new Point(80, 80));

			transitionText.add("Claire's house is the 6th house. Help her find it. ");
			textPoints.add(new Point(60, 200));

			transitionText.add("Maybe visit some neighbours along the way?");
			textPoints.add(new Point(120, 300));

			transitionType = 0;

			startTransition();

			currentLevel++;

			break;
		case 11:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Hi Claire!");
				transitionText.add("Come by again some other time!");
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 12:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Mum, it's the old lady again.");
				transitionText.add("James, be polite!");
				transitionText.add("Here, give her some fresh baking.");
				textPoints.add(new Point(100, 150));
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 13:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Hey you old bag!");
				transitionText.add("Good to see you're still walking!");
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 14:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Good to see you again Claire!");
				transitionText.add("How are your grandchildren doing?");
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 15:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("It's a mighty good evening tonight, isn't it?");
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 16:
			if (spacePressed) {
				transitionType = 0;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Grandma's back everyone!");
				textPoints.add(new Point(100, 200));

				startTransition();

				currentLevel++;

				spacePressed = false;
			}
			break;
		case 17:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Thanks for coming around, good to know you're still breathing.");
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 18:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Hi, we just moved in next door. ");
				transitionText.add("Nice to meet you Claire!");
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 21:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Is everything alright?");
				transitionText.add("Here, warm up with a cuppa.");
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 22:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Mum, Claire's here.");
				transitionText.add("I think she's forgotten where she lives again.");
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 23:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Hi again Claire.");
				transitionText.add("I really don't have time to talk right now.");
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 24:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Still living and breathing I see.");
				textPoints.add(new Point(100, 250));
				startTransition();
				spacePressed = false;
			}
			break;
		case 25:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("For the last time, you live further down the road.");
				textPoints.add(new Point(100, 250));
				startTransition();
				spacePressed = false;
			}
			break;
		case 26:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("For heavens sake, it's 12am, what are you doing out so late Claire?");
				transitionText.add("You'd better get back to your place, your family will be worried sick.");
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 27:
			if (spacePressed) {
				transitionType = 0;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Claire! Where on earth have you been? ");
				transitionText.add("We've been worried sick about you.");
				transitionText.add("I know it's not your fault, but try to let us know next time.");
				textPoints.add(new Point(100, 150));
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));

				startTransition();

				currentLevel++;

				spacePressed = false;
			}
			break;
		case 28:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Who are you?");
				transitionText.add("It's not Halloween anymore.");
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 31:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Oh it's you again.");
				transitionText.add("Your house is further down the road.");
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				secret = true;
				triggers.get(0).loop = false;
				triggers.get(0).triggered = true;
				startTransition();
				spacePressed = false;
			}
			break;
		case 32:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Sorry, you're not welcome here anymore.");
				textPoints.add(new Point(100, 250));
				startTransition();
				spacePressed = false;
			}
			break;
		case 33:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Mum, she's scaring me...");
				transitionText.add("Does she even know who we are?");
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 34:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("For the last time, you don't live here.");
				textPoints.add(new Point(100, 250));
				startTransition();
				spacePressed = false;
			}
			break;
		case 35:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Just go away, do you have any idea what time it is?");
				textPoints.add(new Point(100, 250));
				startTransition();
				spacePressed = false;
			}
			break;
		case 36:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("It's that bloody old woman again.");
				transitionText.add("Just tell her to go away.");
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(400, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 37:
			if (spacePressed) {
				transitionType = 1;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("I'm so sorry about what's happened.");
				transitionText.add("... you don't remember who I am, do you?");
				transitionText.add("Any how, you've already passed your house.");
				textPoints.add(new Point(100, 150));
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				startTransition();
				spacePressed = false;
			}
			break;
		case 38:
			if (spacePressed && secret == true) {
				transitionType = 0;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();

				transitionText.add("Claire, have you been out again?");
				transitionText.add("Thank goodness you made it back.");
				transitionText.add("Claire, this is your Granddaughter, Lisa.");
				transitionText.add("Remember her?");
				transitionText.add("Mum, Grandma scares me...");
				transitionText.add("I know sweetheart.");
				transitionText.add("But she wasn't always like this.");
				transitionText.add("Demons.");
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				textPoints.add(new Point(400, 350));
				textPoints.add(new Point(100, 250));
				textPoints.add(new Point(100, 350));
				textPoints.add(new Point(420, 270));

				startTransition();

				currentLevel++;

				spacePressed = false;
			}
			break;
		case 60:
			if (spacePressed) {
				transitionType = 0;
				transitionText = new ArrayList<String>();
				textPoints = new ArrayList<Point>();
				currentLevel = 0;
				startTransition();
				spacePressed = false;
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Preloads a scene with grass and trees based on a density
	 * 
	 * @param density
	 */
	public void preloadScene(int density) {

		// preload grass
		for (int i = lastGrassPos.x; i < levelWidth; i++) {
			if (density < 300) {
				if (random(0, density) < 2) {
					i += random(20, density / 4);
					lastGrassPos.x = i;
				}
			}
			player.x = i;
			randomGrass();
		}

		// preload trees
		for (int i = lastSeedPos.x; i < levelWidth; i++) {

			Random ran = new Random();

			if (ran.nextInt(density) != 1) {
				continue;
			}

			int y = 350;
			int x = i;

			Color point;
			boolean skip = false;

			do {

				point = getColor(maskImage.pixels[((int) y) * maskImage.width + ((int) x)]);

				y++;

				if (point.getRed() > 240 && point.getBlue() > 240) {
					skip = true;
					break;
				}

			} while ((point.getRed() < 240));

			if (skip) {
				continue;
			}

			if (ran.nextBoolean()) {
				trees.add(new Tree(60, new Point((int) (x), (int) y + 20), -1, 80, treeCanvasBehind, 1));
			} else {
				trees.add(new Tree(60, new Point((int) (x), (int) y + 20), -1, 80, treeCanvasFront, -1));
			}

		}

		lastSeedPos.x = 4000;

		// plant the seeds
		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < seeds.size(); j++) {
				seeds.get(j).tick();
			}
		}

		// grow the trees
		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < trees.size(); j++) {
				trees.get(j).tick();
			}
		}
	}

	/**
	 * Starts a transition
	 */
	public void startTransition() {

		isFadingIn = false;
		isFadingOut = false;
		finishedFadingIn = false;
		finishedFadingOut = false;
		transitioning = true;
	}

	/**
	 * Draws transitions
	 */
	public void drawTransition() {

		if (!isFadingIn && !finishedFadingIn) {
			isFadingIn = true;
			fader = new Fader(Fader.TOBLACK);
		}

		if (finishedFadingIn && !isFadingOut) {
			isFadingIn = false;
			fader = null;
			fill(0, 10);
			rect(0, 0, screenWidth, screenHeight);
		}

		// If it's not fading, it means it's finished fading (by above) thus
		// draw text
		if (!isFadingIn && !transitionText.isEmpty() && !isTexting) {
			isTexting = true;
			texter = new Texter(transitionText.get(0), transitionText.get(0).length() * 100, textPoints.get(0));
		}

		if (isTexting && texter.finished) {
			transitionText.remove(0);
			textPoints.remove(0);
			isTexting = false;
		}

		if (transitionText.isEmpty()) {

			if (!isFadingOut && !finishedFadingOut) {
				if (transitionType == 0) {
					resetGame();
				}

				isFadingOut = true;
				fader = new Fader(Fader.TOWHITE);
			}

			if (finishedFadingOut) {
				isFadingOut = false;
				fader = null;
				transitioning = false;
			}

		}

	}

	/**
	 * * Returns the colour of the pixel at the pixel point. Uses the PImage
	 * 'mask' * @param number * @return
	 */
	public Color getColor(Integer number) {

		if (number > 0) {
			number = -number;
		}
		String hexa = Integer.toHexString(number);
		Color c = new Color(Integer.parseInt("" + hexa.charAt(2) + hexa.charAt(3), 16), // R
				Integer.parseInt("" + hexa.charAt(4) + hexa.charAt(5), 16), // G
				Integer.parseInt("" + hexa.charAt(6) + hexa.charAt(7), 16)); // B
		return c;
	}

	/**
	 * For colour commands based on the mask - look at mask.png!
	 * 
	 * @param commands
	 *            Color based on the command code
	 */
	public void getCommand(ArrayList<Color> commands) {

		Color aCommand = new Color(255, 65, 34);

		for (Color code : commands) {

			if (code.equals(noLeft)) {
				player.velX = 0;
				player.x++;
				jumping = true;
			} else if (code.equals(noRight)) {
				player.velX = 0;
				player.x--;
			}

		}
	}

	static public void main(String args[]) {

		PApplet.main(new String[] { "--bgcolor=#ECE9D8", "Demons" });
	}
}
