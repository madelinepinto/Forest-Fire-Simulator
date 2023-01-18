import java.util.*;

/*Author: Madeline Pinto  Date: Oct 10 2020
 * Desc: Simulates a Forest Fire*/

class ForestFireSimulator {
  public static void main(String [] args) {
   double humidity, burnability;
  int windD,windS,treeT,forestL,forestW,fX,fY;
  int[] wind= new int[2];
    Scanner userInput = new Scanner(System.in);
    
    //Getting information from user
    System.out.println("This is a Forest Fire Simulator.");
    System.out.println("Enter the humidity (%) of the area:");
    humidity=userInput.nextDouble()/100;
    while(humidity<0||humidity>100){
      System.out.println("You entered an invaild value-Try again.");
      humidity=userInput.nextDouble()/100;
    }
    System.out.println("Please choose the wind direction");
    System.out.printf("0-North \n1-North-East \n2-East \n3-South-East \n");
    System.out.printf("4-South \n5-South-West \n6-West \n7-North-West\n");
    while(!userInput.hasNextInt()){
      System.out.println("You entered an invaild value-Try again.");
      userInput.next();
    }
    windD=userInput.nextInt();
    while(windD<0||windD>7){
      System.out.println("You entered an invaild value-Try again.");
      windD=userInput.nextInt();
  }
    wind[0]=windD;
    System.out.println("Enter the wind speed (km/h):");
    while(!userInput.hasNextInt()){
      System.out.println("You entered an invaild value-Try again.");
      userInput.next();
    }
    windS=userInput.nextInt();
    while(windS<0){
      System.out.println("You entered an invaild value-Try again.");
      windS=userInput.nextInt();
  }
    wind[1]=windS;
    System.out.printf("0-Douglas-fir \n1-Loblolly Pine \n2-Ponderosa Pine \n3-Red Maple \n4-Western Hemlock \n");
    System.out.printf("5-Lodgepole Pine\n6-White Oak \n7-Sugar Maple \n8-Yellow-Poplar \n9-Northern Red Oak\n");
    
     while(!userInput.hasNextInt()){
      System.out.println("You entered an invaild value-Try again.");
      userInput.next();
    }
    treeT=userInput.nextInt();
    while(treeT<0||treeT>9){
      System.out.println("You entered an invaild value-Try again.");
      treeT=userInput.nextInt();
  }
    
switch(treeT) {
  case 0:
        burnability=26.5;
        break;
      case 1:
        burnability=17.1;
        break;
      case 2:
        burnability=21.7;
        break;
      case 3:
       burnability=18.1;
       break;
      case 4:
        burnability=24.4; 
        break;
      case 5:
        burnability=22.3; 
        break;
      case 6:
        burnability=24.0;
        break;
      case 7:
        burnability=24.0;
        break;
      case 8:
        burnability=16.0;
        break;
      case 9:
        burnability=24.0;
        break;
      default:
        burnability=20;
        break;}
    System.out.print("Please enter the length of the forest:\n");
    while(!userInput.hasNextInt()){
      System.out.println("You entered an invaild value-Try again.");
      userInput.next();
    }
    forestL=userInput.nextInt();
    while(forestL<0){
      System.out.println("You entered an invaild value-Try again.");
      forestL=userInput.nextInt();
  }
    System.out.print("Please enter the width of the forest:\n");
    while(!userInput.hasNextInt()){
      System.out.println("You entered an invaild value-Try again.");
      userInput.next();
    }
    forestW=userInput.nextInt();
    while(forestW<0){
      System.out.println("You entered an invaild value-Try again.");
      forestW=userInput.nextInt();
  }
    double[][]forest= new double[forestL][forestW];
    System.out.println("Please enter the position of the start point of the fire:");
    System.out.println("example: x y");
    userInput.nextLine();
    String position = userInput.nextLine();
    Scanner fire = new Scanner(position);
    fX=fire.nextInt();
    fY=fire.nextInt();
    forest[fY][fX]=0.5;
    
    //creating the simulation
    ForestSpace sim = new ForestSpace(humidity, forest, wind, burnability);
    System.out.println("\n\n" + sim.drawForest());
    System.out.print("\n" + sim.reachSides());
    
    System.out.println("How many days would you like to jump forward?");
    int repeat = userInput.nextInt();
    
    for (int i=0; i<repeat;i++) sim.advanceDay();
    
    while (true) {
      System.out.println(sim.drawForest());
    
      System.out.println("How many days would you like to jump forward?");
      repeat = userInput.nextInt();
      
      if (repeat == 0) break;
      for (int i=0; i<repeat;i++) sim.advanceDay();
    }
  }
}  

/*Author: Madeline Pinto Date: Oct 10 2020
 * Desc: Acts as the forest that the fire is in
 * Usable public Methods:
 * advanceDay()
 * drawForest()
 * reachSides()
 * getForest()
 * getDays()
 * setWind()*/
class ForestSpace {
  private double humidity; //Goes from 0-1 (0-100%)
/*The value in each cell is like the probability/intensity of the fire there (0-1). 
  Each square is 1km.
  over 0.3  and less than 0.9 means it is burning and can spread.
  less than 0.3 means the fire is kindling and more than 0.9 means it is burning out */
  private double[][] forest; 
  private int[] wind; //First value is the wind direction from 0-7: 0 is north, 1 is north-east, etc. Second is wind speed in km/h
  private double burnability; //Different types of tree
  private int days; //number of days since fire started
  private int[] daysToReachSide;
  
  //Constants to finetune the simulation
  private final double W_SCALE = 1; // controls how fast the wind spreads fire
  private final double B_SCALE = 1; //controls how fast the wood burns
  private final double H_SCALE = 2; //controls how humid it is
  private final double R_SCALE = 7; //controls how small the radius of fire spread is
  private final double S_SCALE = 20; //controls how small the the trees initially catch on fire
  
  ForestSpace() {humidity = 0.5; forest = new double[100][100]; wind = new int[2]; burnability = 20; days =0;daysToReachSide = new int[4];}
  ForestSpace(double humidity, double[][] forest, int[] wind, double burnability) {this.humidity = humidity;daysToReachSide = new int[4];
    this.forest = new double[forest.length][];
    for(int i = 0; i < forest.length; i++)
      this.forest[i] = forest[i].clone(); this.wind = Arrays.copyOf(wind, wind.length); this.burnability = burnability; days = 0;}
  
  //get/set methods
  public double[][] getForest() {forest = new double[this.forest.length][];
    for(int i = 0; i < this.forest.length; i++)
      forest[i] = this.forest[i].clone(); return forest;}
  public int getDays() {return days;}
  protected int[] getDaysToReachSide() {return Arrays.copyOf(daysToReachSide, daysToReachSide.length);}
  
  
  public void setWind(int[] wind) {this.wind = Arrays.copyOf(wind, 2);}
  
  /* param: n/a return: void
   * Desc: Simulates a day of forest fire*/
  public void advanceDay() {
    days++;
    
    // Stage 1, Spreading
    for (int i=0; i<forest.length; i++) {
      for (int j=0; j<forest[0].length; j++) {
        if (forest[i][j] < 0.3 || forest[i][j] >1) continue;
        else {
          spread(i,j);
        }
      }
    }
    
    //Stage 2: Burning the forest
    for (int k=0; k<forest.length; k++) {
      for (int h=0; h<forest[0].length; h++) {
       if (forest[k][h] >1) continue;
       burn(k,h);
      }
    }
  }
  
  /* param: two ints return: void
   * Desc: spreads the fire*/
  private void spread(int i, int j) {
    double range = forest[i][j]*burnability/R_SCALE;
    //loops 4 times for the different directions
      
    for(int k=0;k<7; k++) {
      // figures out where the wind would be pointing if this was north
      int rDirection = (wind[0] +8-k) %8;
      //how far the range stretches based on wind
      double aRange = range;
      //figures out how much to stretch/squish by
      if (rDirection == 0) aRange *= 1+W_SCALE*wind[1]/20;
      else if (rDirection % 6 == 1) aRange *= Math.sqrt(1+W_SCALE*wind[1]/20);
      else if (rDirection == 3 || rDirection == 5) aRange /= Math.sqrt(1+W_SCALE*wind[1]/20);
      else if (rDirection == 4) aRange /= 1+W_SCALE*wind[1]/20;
      
      aRange = Math.floor(aRange);
      
      //north
      if (k==0) {
        for(int n=1;n<=aRange;n++) {
          if (exists(i-n,j)) {
            if (forest[i-n][j] <0.3) forest[i-n][j] = forest[i][j]/(n*humidity*S_SCALE);
          }
        }
      }
            
      //east
      if (k==2) {
        for(int m=1;m<=aRange;m++) {
          if (exists(i,j+m)) {
            if (forest[i][j+m] <0.3) forest[i][j+m] = forest[i][j]/(m*humidity*S_SCALE);
          }
        }
      }
            
      //south
      if (k==4) {
        for(int s=1;s<=aRange;s++) {
          if (exists(i+s,j)) {
            if (forest[i+s][j] <0.3) forest[i+s][j] = forest[i][j]/(s*humidity*S_SCALE);
          }
        }
      }
            
      //west
      if (k==6) {
        for(int z=1;z<=aRange;z++) {
          if (exists(i,j-z)) {
            if (forest[i][j-z] <0.3) forest[i][j-z] = forest[i][j]/(z*humidity*S_SCALE);
          }
        }
      }
    }
  }
  
  /* param: two ints return: void
   * Desc: changes the fire value based on the user input*/
  private void burn(int i, int j) {
    forest[i][j] += (burnability*B_SCALE/20 - humidity*H_SCALE)*(forest[i][j]);
  }
  
  /* param: two ints return: boolean
   * Desc: returns true if the square exists in the array, false if not*/
  private boolean exists(int i, int j) {
    if (i<0 || i>=forest.length || j<0 || j>=forest[0].length) return false;
    else return true;
  }
  
  /* param: n/a return: String
   * Desc: returns the current state of the forest*/
  public String drawForest() {
    String output = "";
    
    for (int i=0; i<forest.length; i++) {
      for (int j=0; j<forest[0].length; j++) {
       output += character(i,j);
      }
      output += "\n";
    }
    
    return output;
  }
  
  /* param: two ints return: String
   * Desc: returns the character that corresponds to each fire value*/
  private String character(int i, int j) {
    double value = forest[i][j];
    if (value == 0) return ".";
    else if (value < 0.3) return "/";
    else if (value >1) return "-";
    else if (value <= 1 && value >= 0.3) return "#";
    else return "?";
  }
  
  /* param: void return: String
   * Desc: returns how long it takes the fire to reach each side*/
  public String reachSides() {
    ForestSpace sides = new ForestSpace(humidity, forest, wind, burnability);
    int[] timeToSides;
    
    while(true) {
      sides.advanceDay();
      if (sides.checkSides()) {
        timeToSides = sides.getDaysToReachSide();
        break;
      }
    }
    
    //Formats the output
    String output = "";
    String[] directions = {"north","east","south","west"};
    
    for (int i=0; i<4; i++) {
      if (timeToSides[i] == 0) output += "The fire never reaches the " + directions[i] + ".\n";
      else output += "The fire reaches the " + directions[i] + " in " + timeToSides[i] + " days.\n";
    }
    
    return output;
  }
  
  /* param: n/a return: boolean
   * Desc: logs how long it takes the fire to reach each side and returns true once it has fully spread*/
  protected boolean checkSides() {
    //checks if fire is on east and west
    for (int i=0; i<forest.length;i++) {
      if (forest[i][0] >0 && daysToReachSide[3] ==0) daysToReachSide[3] = days;
      if (forest[i][forest[0].length-1] >0 && daysToReachSide[1] ==0) daysToReachSide[1] = days;
    }
    
    //checks if fire is on north and south
    for (int j=0; j<forest[0].length;j++) {
      if (forest[0][j] >0 && daysToReachSide[0] ==0) daysToReachSide[0] = days;
      if (forest[forest.length-1][j] >0 && daysToReachSide[2] ==0) daysToReachSide[2] = days;
    }
    
    //checks to see if fire has burnt out
    boolean burntOut = true;
    
    flame:
    for (int k=0; k<forest.length; k++) {
      for (int h=0; h<forest[0].length; h++) {
        if(forest[k][h] <1 && forest[k][h] >0) {
          burntOut = false;
          break flame;
        }
      }
    }
    if (burntOut) return burntOut;
    
    //checks if fire is on all sides, if so return true
    boolean full = true;
    for (int w=0; w<daysToReachSide.length;w++) if (daysToReachSide[w] == 0) full = false;
    return full;
  }
}