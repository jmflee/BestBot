

import robocode.*;
import robocode.util.Utils;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import robocode.WinEvent;

import java.awt.*;

//Modification of "Crazy" and "Spinbot"
public class BestBot extends AdvancedRobot {
  boolean movingForward;
  boolean inWall;
  
  public void run() {
    // Set colors
    setBodyColor(Color.blue);
    setGunColor(Color.red);
    setRadarColor(Color.blue);
    setScanColor(Color.cyan);
    setBulletColor(Color.red);
    
    setAhead(7500);
    setTurnRight(7500);
    setTurnRadarRight(Double.POSITIVE_INFINITY); //Scans infinitly
    movingForward = true;
    
    //Checks if robot is next to a wall
    if (getX() <= 75 || getY() <= 75 || getBattleFieldWidth() - getX() <= 75 || getBattleFieldHeight() - getY() <= 75) {
      inWall = true;
    } else {
      inWall = false;
    }
    
    // Loop forever
    while (true) {
      //If the bot is near a wall by 75 pixels or less then it will reverse it's movement
      if (getX() > 75 && getY() > 75 && getBattleFieldWidth() - getX() > 75 && getBattleFieldHeight() - getY() > 75 && inWall == true) {
        inWall = false;
      }
      if (getX() <= 75 || getY() <= 75 || getBattleFieldWidth() - getX() <= 75 || getBattleFieldHeight() - getY() <= 75 ) {
        if (inWall == false){
          reverseDirection();
          inWall = true;
        }
      }
      execute(); //Execute all commands
      // If the radar stopped turning, take a scan of the whole field until we find a new enemy
      if (getRadarTurnRemaining() == 0.0){
        setTurnRadarRight(Double.POSITIVE_INFINITY);
      }
    }
  }
  
  /**
   * onHitWall:  Handle collision with wall.
   */
  public void onHitWall(HitWallEvent e) {
    // Bounce off!
    reverseDirection();
  }
  
  /**
   * reverseDirection:  Switch from ahead to back & vice versa
   * Reverses the way the tanks is turning aswell
   */
  public void reverseDirection() {
    if (movingForward == true) {
      setBack(7500);
      setTurnLeft(7500);
      movingForward = false;
    } else {
      setAhead(7500);
      setTurnRight(7500);
      movingForward = true;
    }
  }
  
  /**
   * onScannedRobot:  Fire!
   */
  public void onScannedRobot(ScannedRobotEvent e) {
     //If the bot is near a wall by 75 pixels or less then it will reverse it's movement
      if (getX() > 75 && getY() > 75 && getBattleFieldWidth() - getX() > 75 && getBattleFieldHeight() - getY() > 75 && inWall == true) {
        inWall = false;
      }
      if (getX() <= 75 || getY() <= 75 || getBattleFieldWidth() - getX() <= 75 || getBattleFieldHeight() - getY() <= 75 ) {
        if (inWall == false){
          reverseDirection();
          inWall = true;
        }
      }
    //Finds where the enemy was previously and subtracts it with the new position to find his current position
    double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
    setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
    double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
    setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians()));
    //Spins around the enemy, everytime it makes a rotation it will move back and forth by 90 and 110 degrees
    if (movingForward){
      setTurnRight(normalRelativeAngleDegrees(e.getBearing() + 80));
    } else {
      setTurnRight(normalRelativeAngleDegrees(e.getBearing() + 100));
    }
    double distance = e.getDistance(); //Gets distance of enemy relative to tank
    if(getEnergy() > 2) {
      //Fire strength depends on the distance apart
      if (distance>0 && distance<75){
        fire(2);
      }
      else if(distance<200){
        fire(1.5);
      }
      else if(distance<750)
      {
        fire(1);
      }
      else if(distance<800)
      {
        fire(0.5);
      }
      else
      {     
        fire(0.2);
      }
    }
    scan();
  }
  
  /**
   * onHitRobot:  Back up!
   */
  public void onHitRobot(HitRobotEvent e) {
    // If we're moving the other robot, reverse!
    reverseDirection();
  }
  //Spins when he wins
  public void onWin(WinEvent e) {
    setTurnGunRight(Double.POSITIVE_INFINITY);
    setTurnRadarLeft(Double.POSITIVE_INFINITY);
    setTurnLeft(Double.POSITIVE_INFINITY);
  }
}
