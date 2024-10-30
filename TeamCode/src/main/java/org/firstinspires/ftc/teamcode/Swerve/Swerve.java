// Copyright (c) 2024-2025 FTC 13532
// All rights reserved.

package org.firstinspires.ftc.teamcode.Swerve;

import static org.firstinspires.ftc.teamcode.ODO.GoBildaPinpointDriver.EncoderDirection.FORWARD;
import static org.firstinspires.ftc.teamcode.ODO.GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.teamcode.ODO.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.Swerve.wpilib.MathUtil;
import org.firstinspires.ftc.teamcode.Swerve.wpilib.geometry.Pose2d;
import org.firstinspires.ftc.teamcode.Swerve.wpilib.geometry.Rotation2d;
import org.firstinspires.ftc.teamcode.Swerve.wpilib.geometry.Translation2d;
import org.firstinspires.ftc.teamcode.Swerve.wpilib.kinematics.ChassisSpeeds;
import org.firstinspires.ftc.teamcode.Swerve.wpilib.kinematics.SwerveModuleState;
import org.firstinspires.ftc.teamcode.Swerve.wpilib.math.controller.PIDController;
import org.firstinspires.ftc.teamcode.Swerve.wpilib.math.controller.SimpleMotorFeedforward;
import org.firstinspires.ftc.teamcode.Utils;

public class Swerve {

  private final GoBildaPinpointDriver odometry;

  private final SwerveSetpointGenerator setpointGenerator;
  private SwerveSetpointGenerator.SwerveSetpoint lastSetpoint;
  private final double drivebaseRadius;

  private final Module[] modules = new Module[4];

  public Swerve(OpMode opMode) {
    odometry = opMode.hardwareMap.get(GoBildaPinpointDriver.class, "odo");
    odometry.setOffsets(0, 0);
    odometry.setEncoderResolution(goBILDA_4_BAR_POD);
    odometry.setEncoderDirections(FORWARD, FORWARD);

    double trackLengthMeters = .31;
    double trackWidthMeters = .38;
    setpointGenerator =
        new SwerveSetpointGenerator(
            new Translation2d[] {
              new Translation2d(trackLengthMeters / 2, trackWidthMeters / 2),
              new Translation2d(trackLengthMeters / 2, -trackWidthMeters / 2),
              new Translation2d(-trackLengthMeters / 2, trackWidthMeters / 2),
              new Translation2d(-trackLengthMeters / 2, -trackWidthMeters / 2)
            },
            new SwerveSetpointGenerator.ModuleLimits(
                Module.maxDriveSpeedMetersPerSec,
                Module.maxDriveSpeedMetersPerSec / .25,
                Module.maxSteerSpeedRadPerSec));
    drivebaseRadius = Math.hypot(trackLengthMeters / 2, trackWidthMeters / 2);

    SwerveModuleState[] initStates = new SwerveModuleState[4];
    for (int i = 0; i < 4; i++) {
      modules[i] = new Module(opMode, i);
      initStates[i] = new SwerveModuleState(0, modules[i].getServoPos());
    }

    lastSetpoint =
        new SwerveSetpointGenerator.SwerveSetpoint(new ChassisSpeeds(), initStates, new double[4]);
  }

  public void drive(ChassisSpeeds speeds, double dt) {
    var setpoint = setpointGenerator.generateSetpoint(lastSetpoint, speeds, dt);

    for (int i = 0; i < 4; i++) {
      modules[i].run(setpoint.moduleStates()[i], setpoint.steerFeedforwards()[i]);
    }

    lastSetpoint = setpoint;
  }

  public void teleopDrive(double xInput, double yInput, double yawInput, double dt) {
    var translationalMagnitude = Math.hypot(xInput, yInput);
    if (translationalMagnitude > 1) {
      xInput /= translationalMagnitude;
      yInput /= translationalMagnitude;
    }

    drive(
        new ChassisSpeeds(
            xInput * Module.maxDriveSpeedMetersPerSec,
            yInput * Module.maxDriveSpeedMetersPerSec,
            yawInput * (Module.maxDriveSpeedMetersPerSec / drivebaseRadius)),
        dt);
  }

  public Pose2d getPose() {
    return new Pose2d(
        odometry.getPosX() / 1000.0,
        odometry.getPosY() / 1000.0,
        new Rotation2d(odometry.getHeading()));
  }

  public void periodic() {
    odometry.update();
  }

  private static final class Module {
    private static final double conversionFactor;
    static final double maxDriveSpeedMetersPerSec;
    static final double maxSteerSpeedRadPerSec;

    static {
      double countsPerRevolution = 537.7;
      double gearRatio = 1.7;
      double wheelCircumferenceMeters = (96.0 / 1000.0) * Math.PI;
      double maxMotorVelocity = 3.12;

      conversionFactor = countsPerRevolution * gearRatio / wheelCircumferenceMeters;
      maxDriveSpeedMetersPerSec = ((maxMotorVelocity / gearRatio)) * wheelCircumferenceMeters;

      double maxSpeedSecondsPer60Degrees = .115;
      maxSteerSpeedRadPerSec = (2 * Math.PI) / (maxSpeedSecondsPer60Degrees * 6);
    }

    final DcMotorEx driveMotor;
    final CRServo steerServo;
    final AnalogInput steerEncoder;

    final PIDController drivePID;
    final SimpleMotorFeedforward driveFeedforward;

    final PIDController steerPID;

    Module(OpMode opMode, int id) {
      String pos;
      switch (id) {
        case 0 -> pos = "FL";
        case 1 -> pos = "FR";
        case 2 -> pos = "BL";
        case 3 -> pos = "BR";
        default -> throw new IllegalArgumentException("Module ID is out of range 0-3!");
      }

      driveMotor = (DcMotorEx) opMode.hardwareMap.dcMotor.get(pos + "Motor");
      steerServo = opMode.hardwareMap.crservo.get(pos + "Servo");
      steerEncoder = opMode.hardwareMap.analogInput.get(pos + "Encoder");

      if (pos.equals("FL") || pos.equals("FR")) {
        driveMotor.setDirection(DcMotorSimple.Direction.REVERSE);
      }

      drivePID = new PIDController(2 / maxDriveSpeedMetersPerSec, 0, 0);
      driveFeedforward = new SimpleMotorFeedforward(0, 1 / maxDriveSpeedMetersPerSec);

      steerPID = new PIDController(5, 0, 0);
      steerPID.enableContinuousInput(-Math.PI, Math.PI);
    }

    void run(SwerveModuleState state, double steerFeedforward) {
      var servoPos = getServoPos();
      state.optimize(servoPos);
      state.cosineScale(servoPos);

      driveMotor.setPower(
          driveFeedforward.calculate(state.speedMetersPerSecond)
              + drivePID.calculate(getDriveVelocity(), state.speedMetersPerSecond));

      runServoVel(
          steerFeedforward
              + steerPID.calculate(getServoPos().getRadians(), state.angle.getRadians()));
    }

    public double getDrivePosition() {
      return driveMotor.getCurrentPosition() / conversionFactor;
    }

    private double lastPos;
    private double lastTime = -1;

    // We calculate motor velocity ourselves because REV sucks and only calculates velocity at 20
    // hz.
    public double getDriveVelocity() {
      if (lastTime == -1) {
        lastPos = getDrivePosition();
        lastTime = Utils.getTimeSeconds();
        return 0;
      }
      var currentPos = getDrivePosition();
      var currentTime = Utils.getTimeSeconds();
      var velocity = (currentPos - lastPos) / (currentTime - lastTime);
      lastPos = currentPos;
      lastTime = currentTime;
      return velocity;
    }

    public Rotation2d getServoPos() {
      return Rotation2d.fromDegrees(
          MathUtil.interpolate(0, 360, steerEncoder.getVoltage() / steerEncoder.getMaxVoltage()));
    }

    private void runServoVel(double velRadPerSec) {
      steerServo.setPower((velRadPerSec / maxSteerSpeedRadPerSec) * 2 - 1);
    }
  }
}
