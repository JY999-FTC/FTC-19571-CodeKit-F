package org.firstinspires.ftc.teamcode.bots;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.pedroPathing.follower.Follower;
import org.firstinspires.ftc.teamcode.pedroPathing.localization.Pose;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.BezierLine;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.Path;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.PathChain;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.Point;
import org.firstinspires.ftc.teamcode.pedroPathing.util.Timer;

public class PedroPathingBot extends GyroBot{

     private Follower follower;
     private Timer pathTimer, actionTimer, opmodeTimer;

     /** This is the variable where we store the state of our auto.
      * It is used by the pathUpdate method. */
     private int pathState;

     private final Pose startPose = new Pose(7.698663426488457, 53.890643985419196, 0);// starting position of robot
     private final Pose scoreSpecimen = new Pose(40, 66, Math.toRadians(180));// position where specimen is scored on submersible, robot is aligned to submerisble with back facing it

     //    private final Pose sample1 = new Pose(35, 23,0); //these three poses are just behind the samples
     private final Pose samplePivot = new Pose(35, 12.7,0); //pivot from one point to grab all 3 samples
//    private final Pose sample3 = new Pose(35, 6,0);

     /** Park Pose for our robot, after we do all of the scoring. */
     private final Pose parkPose = new Pose(60, 46, Math.toRadians(90));

     /** coordinate to control bezier curve for parking, to go around the submersible must use bezier curve, this is mid point.*/
     private final Pose parkControl = new Pose (37, 25, 0);

     private Path scorePreload, park;

     private PathChain pickup1, pickup2, pickup3, score1, score2, score3;

     public void buildPaths(){
          scorePreload = new Path(new BezierLine(new Point(startPose), new Point(scoreSpecimen)));
          scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), scoreSpecimen.getHeading());

          pickup1 = follower.pathBuilder()
                  .addPath(new BezierLine(new));

     }

     public PedroPathingBot(LinearOpMode opMode) {
          super(opMode);
     }

     public void init(HardwareMap ahwMap) {
          super.init(ahwMap);
          buildPaths();
     }

     protected void onTick() {
          super.onTick();
          // These loop the movements of the robot
          follower.update();
          autonomousPathUpdate();

          // Feedback to Driver Hub
          telemetry.addData("path state", pathState);
          telemetry.addData("x", follower.getPose().getX());
          telemetry.addData("y", follower.getPose().getY());
          telemetry.addData("heading", follower.getPose().getHeading());
          telemetry.update();
     }
     public void autonomousPathUpdate() {
          switch (pathState) {
               case 0:
                    follower.followPath(scorePreload);
                    setPathState(1);
                    break;
               case 1:

                /* You could check for
                - Follower State: "if(!follower.isBusy() {}"
                - Time: "if(pathTimer.getElapsedTimeSeconds() > 1) {}"
                - Robot Position: "if(follower.getPose().getX() > 36) {}"
                */

                    /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                    if(!follower.isBusy()) {
                         /* Score Preload */

                         /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
//                         follower.followPath(grabPickup1,true);
                         triggerEvent(EVENT_SAMPLE_PICKED_UP, 1);
                         setPathState(2);
                    }
                    break;
               case 8:
                    /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                    if(!follower.isBusy()) {
                         /* Level 1 Ascent */

                         /* Set the state to a Case we won't use or define, so it just stops running an new paths */
                         setPathState(-1);
                    }
                    break;
          }
     }
     /** These change the states of the paths and actions
      * It will also reset the timers of the individual switches **/
     public void setPathState(int pState) {
          pathState = pState;
          pathTimer.resetTimer();
     }

     protected void onEvent(int event, int data) {
          super.onEvent(event, data);
          if (event == EVENT_SAMPLE_PICKED_UP) {
              // Do something when the sample is picked up
              telemetry.addData("Sample picked up - Pedro Pathing", data);
          }
          telemetry.update();
     }
}
