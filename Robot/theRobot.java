import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


// This class draws the probability map and value iteration map that you create to the window
// You need only call updateProbs() and updateValues() from your theRobot class to update these maps
class mySmartMap extends JComponent implements KeyListener {
   public static final int NORTH = 0;
   public static final int SOUTH = 1;
   public static final int EAST = 2;
   public static final int WEST = 3;
   public static final int STAY = 4;

   int currentKey;

   int winWidth, winHeight;
   double sqrWdth, sqrHght;
   Color gris = new Color(170, 170, 170);
   Color myWhite = new Color(220, 220, 220);
   World mundo;

   int gameStatus;

   double[][] probs;
   double[][] vals;

   public mySmartMap(int w, int h, World wld) {
      mundo = wld;
      probs = new double[mundo.width][mundo.height];
      vals = new double[mundo.width][mundo.height];
      winWidth = w;
      winHeight = h;

      sqrWdth = (double) w / mundo.width;
      sqrHght = (double) h / mundo.height;
      currentKey = -1;

      addKeyListener(this);

      gameStatus = 0;
   }

   public void addNotify() {
      super.addNotify();
      requestFocus();
   }

   public void setWin() {
      gameStatus = 1;
      repaint();
   }

   public void setLoss() {
      gameStatus = 2;
      repaint();
   }

   public void updateProbs(double[][] _probs) {
      for (int y = 0; y < mundo.height; y++) {
         for (int x = 0; x < mundo.width; x++) {
            probs[x][y] = _probs[x][y];
         }
      }

      repaint();
   }

   public void updateValues(double[][] _vals) {
      for (int y = 0; y < mundo.height; y++) {
         for (int x = 0; x < mundo.width; x++) {
            vals[x][y] = _vals[x][y];
         }
      }

      repaint();
   }

   public void paint(Graphics g) {
      paintProbs(g);
      //paintValues(g);
   }

   public void paintProbs(Graphics g) {
      double maxProbs = 0.0;
      int mx = 0, my = 0;
      for (int y = 0; y < mundo.height; y++) {
         for (int x = 0; x < mundo.width; x++) {
            if (probs[x][y] > maxProbs) {
               maxProbs = probs[x][y];
               mx = x;
               my = y;
            }
            if (mundo.grid[x][y] == 1) {
               g.setColor(Color.black);
               g.fillRect((int) (x * sqrWdth), (int) (y * sqrHght), (int) sqrWdth, (int) sqrHght);
            } else if (mundo.grid[x][y] == 0) {
               //g.setColor(myWhite);

               int col = (int) (255 * Math.sqrt(probs[x][y]));
               if (col > 255)
                  col = 255;
               g.setColor(new Color(255 - col, 255 - col, 255));
               g.fillRect((int) (x * sqrWdth), (int) (y * sqrHght), (int) sqrWdth, (int) sqrHght);
            } else if (mundo.grid[x][y] == 2) {
               g.setColor(Color.red);
               g.fillRect((int) (x * sqrWdth), (int) (y * sqrHght), (int) sqrWdth, (int) sqrHght);
            } else if (mundo.grid[x][y] == 3) {
               g.setColor(Color.green);
               g.fillRect((int) (x * sqrWdth), (int) (y * sqrHght), (int) sqrWdth, (int) sqrHght);
            }

         }
         if (y != 0) {
            g.setColor(gris);
            g.drawLine(0, (int) (y * sqrHght), (int) winWidth, (int) (y * sqrHght));
         }
      }
      for (int x = 0; x < mundo.width; x++) {
         g.setColor(gris);
         g.drawLine((int) (x * sqrWdth), 0, (int) (x * sqrWdth), (int) winHeight);
      }

      //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);

      g.setColor(Color.green);
      g.drawOval((int) (mx * sqrWdth) + 1, (int) (my * sqrHght) + 1, (int) (sqrWdth - 1.4), (int) (sqrHght - 1.4));

      if (gameStatus == 1) {
         g.setColor(Color.green);
         g.drawString("You Won!", 8, 25);
      } else if (gameStatus == 2) {
         g.setColor(Color.red);
         g.drawString("You're a Loser!", 8, 25);
      }
   }

   public void paintValues(Graphics g) {
      double maxVal = -99999, minVal = 99999;
      int mx = 0, my = 0;

      for (int y = 0; y < mundo.height; y++) {
         for (int x = 0; x < mundo.width; x++) {
            if (mundo.grid[x][y] != 0)
               continue;

            if (vals[x][y] > maxVal)
               maxVal = vals[x][y];
            if (vals[x][y] < minVal)
               minVal = vals[x][y];
         }
      }
      if (minVal == maxVal) {
         maxVal = minVal + 1;
      }

      int offset = winWidth + 20;
      for (int y = 0; y < mundo.height; y++) {
         for (int x = 0; x < mundo.width; x++) {
            if (mundo.grid[x][y] == 1) {
               g.setColor(Color.black);
               g.fillRect((int) (x * sqrWdth) + offset, (int) (y * sqrHght), (int) sqrWdth, (int) sqrHght);
            } else if (mundo.grid[x][y] == 0) {
               //g.setColor(myWhite);

               //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
               int col = (int) (255 * (vals[x][y] - minVal) / (maxVal - minVal));
               if (col > 255)
                  col = 255;
               g.setColor(new Color(255 - col, 255 - col, 255));
               g.fillRect((int) (x * sqrWdth) + offset, (int) (y * sqrHght), (int) sqrWdth, (int) sqrHght);
            } else if (mundo.grid[x][y] == 2) {
               g.setColor(Color.red);
               g.fillRect((int) (x * sqrWdth) + offset, (int) (y * sqrHght), (int) sqrWdth, (int) sqrHght);
            } else if (mundo.grid[x][y] == 3) {
               g.setColor(Color.green);
               g.fillRect((int) (x * sqrWdth) + offset, (int) (y * sqrHght), (int) sqrWdth, (int) sqrHght);
            }

         }
         if (y != 0) {
            g.setColor(gris);
            g.drawLine(offset, (int) (y * sqrHght), (int) winWidth + offset, (int) (y * sqrHght));
         }
      }
      for (int x = 0; x < mundo.width; x++) {
         g.setColor(gris);
         g.drawLine((int) (x * sqrWdth) + offset, 0, (int) (x * sqrWdth) + offset, (int) winHeight);
      }
   }


   public void keyPressed(KeyEvent e) {
      //System.out.println("keyPressed");
   }

   public void keyReleased(KeyEvent e) {
      //System.out.println("keyReleased");
   }

   public void keyTyped(KeyEvent e) {
      char key = e.getKeyChar();
      //System.out.println(key);

      switch (key) {
         case 'i':
            currentKey = NORTH;
            break;
         case ',':
            currentKey = SOUTH;
            break;
         case 'j':
            currentKey = WEST;
            break;
         case 'l':
            currentKey = EAST;
            break;
         case 'k':
            currentKey = STAY;
            break;
      }
   }
}


// This is the main class that you will add to in order to complete the lab
public class theRobot extends JFrame {
   // Mapping of actions to integers
   public static final int NORTH = 0;
   public static final int SOUTH = 1;
   public static final int EAST = 2;
   public static final int WEST = 3;
   public static final int STAY = 4;

   public static int[][] directions = {
         {0, -1, SOUTH},     // up
         {0, 1, NORTH},      // down
         {1, 0, WEST},      // right
         {-1, 0, EAST},     // left
         {0, 0, STAY}       // stay
   };

   Color bkgroundColor = new Color(230, 230, 230);

   static mySmartMap myMaps; // instance of the class that draw everything to the GUI
   String mundoName;

   World mundo; // mundo contains all the information about the world.  See World.java
   double moveProb, sensorAccuracy;  // stores probabilies that the robot moves in the intended direction
   // and the probability that a sonar reading is correct, respectively

   // variables to communicate with the Server via sockets
   public Socket s;
   public BufferedReader sin;
   public PrintWriter sout;

   // variables to store information entered through the command-line about the current scenario
   boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
   boolean knownPosition = false;
   int startX = -1, startY = -1;
   int decisionDelay = 250;

   // store your probability map (for position of the robot in this array
   double[][] probs;

   // store your computed value of being in each state (x, y)
   double[][] Vs;

   public theRobot(String _manual, int _decisionDelay) {
      // initialize variables as specified from the command-line
      isManual = !_manual.equals("automatic");
      decisionDelay = _decisionDelay;

      // get a connection to the server and get initial information about the world
      initClient();

      // Read in the world
      mundo = new World(mundoName);

      // set up the GUI that displays the information you compute
      int width = 500;
      int height = 500;
      int bar = 20;
      setSize(width, height + bar);
      getContentPane().setBackground(bkgroundColor);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(0, 0, width, height + bar);
      myMaps = new mySmartMap(width, height, mundo);
      getContentPane().add(myMaps);

      setVisible(true);
      setTitle("Probability and Value Maps");

      doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
   }

   // this function establishes a connection with the server and learns
   //   1 -- which world it is in
   //   2 -- it's transition model (specified by moveProb)
   //   3 -- it's sensor model (specified by sensorAccuracy)
   //   4 -- whether it's initial position is known.  if known, its position is stored in (startX, startY)
   public void initClient() {
      int portNumber = 3333;
      String host = "localhost";

      try {
         s = new Socket(host, portNumber);
         sout = new PrintWriter(s.getOutputStream(), true);
         sin = new BufferedReader(new InputStreamReader(s.getInputStream()));

         mundoName = sin.readLine();
         moveProb = Double.parseDouble(sin.readLine());
         sensorAccuracy = Double.parseDouble(sin.readLine());
         System.out.println("Need to open the mundo: " + mundoName);
         System.out.println("moveProb: " + moveProb);
         System.out.println("sensorAccuracy: " + sensorAccuracy);

         // find out of the robots position is know
         String _known = sin.readLine();
         if (_known.equals("known")) {
            knownPosition = true;
            startX = Integer.parseInt(sin.readLine());
            startY = Integer.parseInt(sin.readLine());
            System.out.println("Robot's initial position is known: " + startX + ", " + startY);
         } else {
            System.out.println("Robot's initial position is unknown");
         }
      } catch (IOException e) {
         System.err.println("Caught IOException: " + e.getMessage());
      }
   }

   // function that gets human-specified actions
   // 'i' specifies the movement up
   // ',' specifies the movement down
   // 'l' specifies the movement right
   // 'j' specifies the movement left
   // 'k' specifies the movement stay
   int getHumanAction() {
      System.out.println("Reading the action selected by the user");
      while (myMaps.currentKey < 0) {
         try {
            Thread.sleep(50);
         } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
         }
      }
      int a = myMaps.currentKey;
      myMaps.currentKey = -1;

      System.out.println("Action: " + a);

      return a;
   }

   // initializes the probabilities of where the AI is
   void initializeProbabilities() {
      probs = new double[mundo.width][mundo.height];
      // if the robot's initial position is known, reflect that in the probability map
      if (knownPosition) {
         for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
               if ((x == startX) && (y == startY))
                  probs[x][y] = 1.0;
               else
                  probs[x][y] = 0.0;
            }
         }
      } else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
         int count = 0;

         for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
               if (mundo.grid[x][y] == 0)
                  count++;
            }
         }

         for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
               if (mundo.grid[x][y] == 0)
                  probs[x][y] = 1.0 / count;
               else
                  probs[x][y] = 0;
            }
         }
      }

      myMaps.updateProbs(probs);
   }

   // Iterate over utility values in the maze to find the optimal values
   void valueIteration() {
      double gamma = 0.9;
      double epsilon = 1e-10;
      double delta;

      Vs = new double[mundo.width][mundo.height];

      printMundoArray(Vs);
      do {
         delta = 0.0;
         double[][] newVs = new double[mundo.width][mundo.height];

         for (int y = 0; y < mundo.height; ++y) {
            for (int x = 0; x < mundo.width; ++x) {
               int cellType = mundo.grid[x][y];

               double Rs = getRewardValue(cellType);

               if (cellType != 0) {
                  newVs[x][y] = Rs;
                  continue;
               }

               double maxActionValue = Double.NEGATIVE_INFINITY;

               for (int action = 0; action < 5; action++) {
                  double actionValue = computeExpectedUtility(x, y, action);

                  if (actionValue > maxActionValue) {
                     maxActionValue = actionValue;
                  }
               }

               double VsUpdated = Rs + gamma * maxActionValue;
               // Keep the maximum change that we see over all iterations. This will determine if we have converged.
               delta = Math.max(delta, Math.abs(VsUpdated - Vs[x][y]));
               newVs[x][y] = VsUpdated;
            }
         }
         Vs = newVs;
      } while (delta > epsilon);
   }

   double computeExpectedUtility(int x, int y, int action) {
      double utility = 0.0;

      // Define possible movements and their probabilities
      for (int dir = 0; dir < 5; dir++) {
         int dx = directions[dir][0];
         int dy = directions[dir][1];
         int intendedAction = directions[dir][2];

         int xPrime = x + dx;
         int yPrime = y + dy;

         // Check for boundaries and walls
         if (xPrime < 0 || xPrime >= mundo.width || yPrime < 0 || yPrime >= mundo.height || mundo.grid[xPrime][yPrime] == 1) {
            xPrime = x;
            yPrime = y;
         }

         double prob = (intendedAction == action) ? moveProb : (1 - moveProb) / 4.0;

         utility += prob * Vs[xPrime][yPrime];
      }
      return utility;
   }

   double getRewardValue(int cellType) {
      double stairReward = -1.0;
      double goalReward = 1000.0;
      double emptyReward = -0.01;

      return switch (cellType) {
         case 1 -> 0.0;
         case 2 -> stairReward;
         case 3 -> goalReward;
         default -> emptyReward;
      };
   }

   // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
   //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
   void updateProbabilities(int action, String sonars) {
      // your code
      double[][] newProbs = new double[mundo.width][mundo.height];

      for (int y = 0; y < mundo.height; ++y) {
         for (int x = 0; x < mundo.width; ++x) {
            double sensorModel = sensorModel(x, y, sonars);
            double predictionModel = predictionModel(x, y, action);
            newProbs[x][y] = sensorModel * predictionModel;
         }
      }

      double sum = 0.0;

      for (int y = 0; y < mundo.height; ++y) {
         for (int x = 0; x < mundo.width; ++x) {
            sum += newProbs[x][y];
         }
      }

      double alpha = 1 / sum;

      for (int y = 0; y < mundo.height; ++y) {
         for (int x = 0; x < mundo.width; ++x) {
            newProbs[x][y] *= alpha;
         }
      }

      probs = newProbs;
      myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
      //  new probabilities will show up in the probability map on the GUI
   }

   double predictionModel(int x, int y, int action) {
      double prob = 0.0;

      if (mundo.grid[x][y] == 1) {
         return prob;
      }

      for (int i = 0; i < 4; ++i) {
         int moveX = x + directions[i][0];
         int moveY = y + directions[i][1];

         int direction = directions[i][2];

         if (direction == action) {
            prob += probs[moveX][moveY] * moveProb;
         } else {
            // Multiply by complement and probability of actually moving in this direction.
            prob += probs[moveX][moveY] * (1 - moveProb) * 0.25;
         }
      }

      prob += getStayProb(x, y, action);

      return prob;
   }

   double getStayProb(int x, int y, int action) {
      double moveComp = (1 - moveProb) * 0.25;

      double prob = (action == STAY ? probs[x][y] * moveProb : probs[x][y] * moveComp);

      for (int i = 0; i < 4; ++i) {
         int checkX = x - directions[i][0];
         int checkY = y - directions[i][1];

         boolean isWall = mundo.grid[checkX][checkY] == 1;

         int direction = directions[i][2];

         if (direction == action && isWall) {
            prob += probs[x][y] * moveProb;
         } else if (isWall) {
            prob += probs[x][y] * moveComp;
         }
      }
      return prob;
   }

   double sensorModel(int x, int y, String sonars) {
      if (mundo.grid[x][y] == 1 || mundo.grid[x][y] == 2) {
         return 0.0;
      }

      double numMatch = 0.0;

      for (int i = 0; i < 4; ++i) {
         int moveX = x + directions[i][0];
         int moveY = y + directions[i][1];

         if (mundo.grid[moveX][moveY] == sonars.charAt(i) - '0' ||
               mundo.grid[moveX][moveY] != 1 && sonars.charAt(i) == '0') {
            ++numMatch;
         }
      }

      return Math.pow(sensorAccuracy, numMatch) * Math.pow(1 - sensorAccuracy, 4 - numMatch);
   }

   // This is the function you'd need to write to make the robot move using your AI;
   // You do NOT need to write this function for this lab; it can remain as is
   int automaticAction() {
      int bestAction = 0;

      double bestEV = Double.NEGATIVE_INFINITY;

      // Try each action and find the expected value, EV(a), of each action and choose the best one.
      for (int action = 0; action < 5; ++action) {

         double EV = getEV(action);

         if (EV > bestEV) {
            bestEV = EV;
            bestAction = action;
         }

         if (EV == bestEV) {
            if (Math.random() < 0.5)
               bestAction = action;
         }
      }
      return bestAction;
   }

   int autoExploreAction(int consecutiveStayActions, double epsilon) {
      int bestAction = 0;
      int maxStays = 3;

      double bestEV = Double.NEGATIVE_INFINITY;
      for (int action = 0; action < 5; ++action) {
         // Prevent infinite STAY actions
         if (action == STAY && consecutiveStayActions >= maxStays)
            continue;

         // Random chance to move in a less optimal direction.
         if (Math.random() < epsilon) {
            bestAction = (int) (Math.random() * 4);
         }

         double EV = getEV(action);

         if (EV > bestEV) {
            bestEV = EV;
            bestAction = action;
         }

         if (EV == bestEV) {
            if (Math.random() < 0.5)
               bestAction = action;
         }
      }
      return bestAction;
   }

   private double getEV(int action) {
      double EV = 0.0;

      for (int y = 0; y < mundo.height; ++y) {
         for (int x = 0; x < mundo.width; ++x) {
            int xPrime = x + directions[action][0];
            int yPrime = y + directions[action][1];

            // Check for walls and ensure within bounds
            if (xPrime < 0 || xPrime >= mundo.width ||
                  yPrime < 0 || yPrime >= mundo.height ||
                  mundo.grid[xPrime][yPrime] == 1 || mundo.grid[x][y] == 1) {
               continue;
            }

            double Q = moveProb * Vs[xPrime][yPrime];

            EV += probs[x][y] * Q;
         }
      }
      return EV;
   }

   void doStuff() {
      int action;
      int numStay = 0;
      double epsilon = 0.9;

      valueIteration();
      initializeProbabilities();  // Initializes the location (probability) map

      printMundoArray(Vs);

      while (true) {
         try {
            if (isManual)
               action = getHumanAction();  // get the action selected by the user (from the keyboard)
            else {
               // Favor exploration in the early actions to localize.
               action = autoExploreAction(numStay, epsilon);
               epsilon = Math.max(epsilon * epsilon, 0.01);

               // Count the number of consecutive STAY actions
               if (action == STAY) numStay += 1;
               else numStay = 0;
            }
            // you'll need to write this function for part III

            sout.println(action); // send the action to the Server

            // get sonar readings after the robot moves
            String sonars = sin.readLine();
            //System.out.println("Sonars: " + sonars);

            updateProbabilities(action, sonars);

            if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
               if (sonars.charAt(4) == 'w') {
                  System.out.println("I won!");
                  myMaps.setWin();
                  break;
               } else if (sonars.charAt(4) == 'l') {
                  System.out.println("I lost!");
                  myMaps.setLoss();
                  break;
               }
            } else {
               // here, you'll want to update the position probabilities
               // since you know that the result of the move as that the robot
               // was not at the goal or in a stairwell
            }
            Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
            // decisionDelay is specified by the send command-line argument, which is given in milliseconds
         } catch (IOException e) {
            System.out.println(e);
         } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
         }
      }
   }

   void printMundoArray(double[][] array) {
      final String RESET = "\u001B[0m";
      final String BLUE = "\u001B[34m";
      final String RED = "\u001B[31m";
      final String GREEN = "\u001B[32m";

      System.out.println("----------------------------------------------------------------------------------------");
      for (int y = 0; y < mundo.height; ++y) {
         for (int x = 0; x < mundo.width; ++x) {
            double val = array[x][y];
            String color = RESET;

            if (mundo.grid[x][y] == 1) color = BLUE;
            else if (mundo.grid[x][y] == 2) color = RED;
            else if (mundo.grid[x][y] == 3) color = GREEN;

            System.out.print(color);
            System.out.printf("[%6.2f]", val);
            System.out.print(RESET);
         }
         System.out.println();
      }
      System.out.println("----------------------------------------------------------------------------------------");
   }

   // java theRobot [manual/automatic] [delay]
   public static void main(String[] args) {
      theRobot robot = new theRobot(args[0], Integer.parseInt(args[1]));  // starts up the robot
   }
}