// Copyright (c) 2024-2025 FTC 13532
// All rights reserved.

package org.firstinspires.ftc.teamcode.Auto;

import static org.firstinspires.ftc.teamcode.ODO.GoBildaPinpointDriver.EncoderDirection.FORWARD;
import static org.firstinspires.ftc.teamcode.ODO.GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.ODO.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.Swerve.wpilib.geometry.Rotation2d;

@Autonomous(name = "Comp Bot Auto", preselectTeleOp = "Blue Bot Teleop")
public class CompBotAuto extends LinearOpMode {

  DcMotor slide, slide2, pivot;
  Servo intake, wrist;
  int limitSlide, limitPivot;

  double pubLength = 0;

  double encoderCountsPerInch = 100; // needs adjusting

  double encoderCountsPerDegree = 30;

  boolean test = false;

  DigitalChannel limitSwitch;

  @Override
  public void runOpMode() throws InterruptedException {
    initRobot();
    waitForStart();
    // initSlide();
    // Home arm
    while (limitSwitch.getState()) {
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
    pivot.setPower(.00);

    slide.setPower(.5);
    slide2.setPower(.5);
    slide.setTargetPosition(4200);
    slide2.setTargetPosition(4200);
    pivot.setTargetPosition(600);
    wrist.setPosition(.35);
    telem();

    sleep(4500);

    intake.setPosition(0);

    sleep(1000);
    intake.setPosition(0.5);
    pivot.setTargetPosition(0);
    slide.setTargetPosition(0);
    slide2.setTargetPosition(0);
    telem();
    sleep(4500);
  }

  public void telem(){
    telemetry.addData("slide current: ",slide.getCurrentPosition());
    telemetry.addData("slide goal: ",slide.getTargetPosition());
    telemetry.addData("pivot current: ",pivot.getCurrentPosition());
    telemetry.addData("pivot goal: ",pivot.getTargetPosition());
    telemetry.addData("wrist current: ",wrist.getPosition());
    telemetry.update();
  }

  public void initRobot() {
    slide = hardwareMap.get(DcMotor.class, "slide");
    slide2 = hardwareMap.get(DcMotor.class, "slide 2");
    pivot = hardwareMap.get(DcMotor.class, "pivot");

    slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    slide2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    pivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    slide.setTargetPosition(0);
    slide2.setTargetPosition(0);
    pivot.setTargetPosition(0);

    slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    slide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    pivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    slide.setPower(1);
    slide2.setPower(1);
    pivot.setPower(.5);

    slide.setDirection(DcMotorSimple.Direction.FORWARD);
    pivot.setDirection(DcMotorSimple.Direction.FORWARD);

    limitSlide = 4750;
    limitPivot = 3200;

    // servos
    intake = hardwareMap.get(Servo.class, "intake");
    wrist = hardwareMap.get(Servo.class, "wrist");

    intake.setDirection(Servo.Direction.REVERSE);
    wrist.setDirection(Servo.Direction.FORWARD);

    // limit switch and brings pivot back
    limitSwitch = hardwareMap.get(DigitalChannel.class, "limit switch");

    var odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
    odo.setOffsets(110, 30);
    odo.setEncoderResolution(goBILDA_4_BAR_POD);
    odo.setEncoderDirections(FORWARD, FORWARD);
    odo.resetPosAndIMU();
    try {
      Thread.sleep((long) (.25 * 1e3));
    } catch (final InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    odo.resetHeading(Rotation2d.fromDegrees(120));
  }

  public void initSlide() {
    double i = 0.75;
    while (limitSwitch.getState()) {
      pivot.setPower(i);
      slide.setPower(-.01);
    }

    pivot.setPower(.00);
    slide.setPower(.00);
    sleep(250);

    pivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    pivot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    slide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    pivot.setPower(.00);
  }
}
