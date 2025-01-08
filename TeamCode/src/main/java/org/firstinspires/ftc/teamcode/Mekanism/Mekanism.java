// Copyright (c) 2024-2025 FTC 13532
// All rights reserved.

package org.firstinspires.ftc.teamcode.Mekanism;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_TO_POSITION;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.STOP_AND_RESET_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Mekanism {
  private final DcMotorEx pivot;
  private final DcMotorEx slide;
  private final DcMotorEx slide2;

  private final DigitalChannel limitSwitch;

  private final Servo intakeServo;
  private final Servo wrist;

  private final Servo ramp1;
  private final Servo ramp2;

  public final double limitSlide = 4200;
  public final double limitPivot = 2750;
  private final double countsPerDegree = 30; // TODO: This needs to be found

  public double slideTarget = 0;

  private final Telemetry telemetry;

  ElapsedTime pivotTimer = new ElapsedTime();

  public Mekanism(LinearOpMode opMode) {
    // Init slaw, claw, and pivot
    pivot = (DcMotorEx) opMode.hardwareMap.dcMotor.get("pivot");
    slide = (DcMotorEx) opMode.hardwareMap.dcMotor.get("slide");
    slide2 = (DcMotorEx) opMode.hardwareMap.get(DcMotor.class, "slide 2");

    pivot.setMode(STOP_AND_RESET_ENCODER);
    slide.setMode(STOP_AND_RESET_ENCODER);
    slide2.setMode(STOP_AND_RESET_ENCODER);

    pivot.setTargetPosition(0);
    slide.setTargetPosition(0);
    slide2.setTargetPosition(0);

    pivot.setMode(RUN_TO_POSITION);
    slide.setMode(RUN_TO_POSITION);
    slide2.setMode(RUN_TO_POSITION);

    pivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    slide2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    pivot.setDirection(FORWARD);
    slide.setDirection(FORWARD);
    slide2.setDirection(FORWARD);

    pivot.setPower(1);
    slide.setPower(1);
    slide2.setPower(1);


    limitSwitch = opMode.hardwareMap.get(DigitalChannel.class, "limit switch");


    // Servos for intake
    intakeServo = opMode.hardwareMap.get(Servo.class, "intake");
    wrist = opMode.hardwareMap.get(Servo.class, "wrist");

    intakeServo.setDirection(Servo.Direction.REVERSE);
    wrist.setDirection(Servo.Direction.REVERSE);

    wrist.setPosition(0.65);


    // Servos for clipper
    ramp1 = opMode.hardwareMap.get(Servo.class, "ramp 1");
    ramp2 = opMode.hardwareMap.get(Servo.class, "ramp 2");

    ramp1.setDirection(Servo.Direction.FORWARD);
    ramp2.setDirection(Servo.Direction.FORWARD);

    unclamp();

    ramp1.scaleRange(0, 1);
    ramp2.scaleRange(0, 1);

    telemetry = opMode.telemetry;
  }

  // To extend arm, input from game pad 2 straight in
  public void setSlide(int x) {

    telemetry.addData("slide current pos", slide.getCurrentPosition());

    slide.setTargetPosition(x);
    slide2.setTargetPosition(x);
  }

  public void setPivot(double x, boolean raiseLimit) {
    if (pivot.getCurrentPosition() >= (raiseLimit ? limitPivot : limitPivot + 500) && x > 0) {
      x = 0;
    } else if (pivot.getCurrentPosition() <= 0 && x < 0) {
      x = 0;
    }
    telemetry.addData("pivot current pos", pivot.getCurrentPosition());

    x *= .5;
    pivot.setPower(x);
  }

  public void homeArm() {
    pivotTimer.reset();
    while (limitSwitch.getState() && pivotTimer.milliseconds() < 2500) {
      pivot.setPower(-.75);
      slide.setPower(-0.5);
    }

    pivot.setPower(.00);
    slide.setPower(.00);
    try {
      Thread.sleep(250);
    } catch (final InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    pivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    pivot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    slide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    slide2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    pivot.setPower(.00);
  }

  public void runIntake(boolean intake, boolean outtake) {
    if (outtake) {
      intakeServo.setPosition(1);
    } else if (intake) {
      intakeServo.setPosition(0);
    } else {
      intakeServo.setPosition(.5);
    }
  }

  public void intake() {
    intakeServo.setPosition(1);
  }

  public void outtake() {}

  public void clamp() {
    ramp1.setPosition(0);
    ramp2.setPosition(.15);
  }

  public void unclamp() {
    ramp1.setPosition(.15);
    ramp2.setPosition(0);
  }

  public void autoClip() {
    telemetry.addData("pivot current pos", pivot.getCurrentPosition());
    telemetry.addData("slide current pos", slide.getCurrentPosition());
    if (slide.getCurrentPosition() > 150) {
      unclamp();
      slide.setPower(-1);
      wrist.setPosition(0.1);
    } else {
      clamp();
      slide.setPower(0);
      if (pivot.getCurrentPosition() < 2200) {
        wrist.setPosition(0.1);
        pivot.setPower(1);
      } else {
        wrist.setPosition(0);
        slide.setPower(-1);
        if (pivot.getCurrentPosition() < 2500) {
          pivot.setPower(1);
        } else {
          pivot.setPower(0);
        }
      }
    }
    wristMoved = true;
  }

  private boolean wristMoved = false;

  public void toggleWrist() {
    if (!wristMoved) {
      wrist.setPosition(0.1);
      wristMoved = true;
    } else {
      wrist.setPosition(0.65);
      wristMoved = false;
    }
  }

  public void topSpecimenRung() {
    // setPivot();
    // setSlide();
  }
}
