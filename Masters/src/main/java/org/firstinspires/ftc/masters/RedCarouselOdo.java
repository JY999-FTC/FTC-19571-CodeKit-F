package org.firstinspires.ftc.masters;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.masters.drive.DriveConstants;
import org.firstinspires.ftc.masters.drive.SampleMecanumDrive;
import org.firstinspires.ftc.masters.trajectorySequence.TrajectorySequence;

import java.util.Date;

@Autonomous(name = "Red - Carousel (STATE)", group = "competition")
public class RedCarouselOdo extends LinearOpMode {

    final int SERVO_DROP_PAUSE = 900;
    Pose2d position;
    SampleMecanumDrive drive;
    ElapsedTime elapsedTime;

    @Override
    public void runOpMode() throws InterruptedException {

        drive = new SampleMecanumDrive(hardwareMap, this, telemetry);

        drive.openCVInnitShenanigans("red");
        MultipleCameraCV.ShippingElementDeterminationPipeline.ElementPosition freightLocation = drive.analyze();

        Pose2d startPose = new Pose2d(new Vector2d(-35, -63), Math.toRadians(90));

        drive.setPoseEstimate(startPose);
        drive.linearSlideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drive.linearSlideServo.setPosition(FreightFrenzyConstants.DUMP_SERVO_LIFT);

        waitForStart();
//        Mecha Knight Changes: ALERT
        drive.pause(1000);

        elapsedTime = new ElapsedTime();

        long startTime = new Date().getTime();
        long time = 0;

        while (time < 200 && opModeIsActive()) {
            time = new Date().getTime() - startTime;
            freightLocation = drive.analyze();

            telemetry.addData("Position", freightLocation);
            telemetry.update();
        }
        switch (freightLocation) {
            case LEFT:
                drive.linearSlideMotor.setTargetPosition(FreightFrenzyConstants.SLIDE_LOW);
                break;
            case MIDDLE:
                drive.linearSlideMotor.setTargetPosition(FreightFrenzyConstants.SLIDE_MIDDLE);
                break;
            case RIGHT:
                drive.linearSlideMotor.setTargetPosition(FreightFrenzyConstants.SLIDE_TOP);
                break;
            default:
                drive.linearSlideMotor.setTargetPosition(FreightFrenzyConstants.SLIDE_TOP);
        }
        drive.linearSlideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        drive.linearSlideMotor.setPower(.8);

        if (isStopRequested()) return;

//      Deposit initial freight
        Pose2d hubPosition = new Pose2d(new Vector2d(-23, -38), Math.toRadians(45));
        TrajectorySequence toHubHigh = drive.trajectorySequenceBuilder(startPose)
                .lineToLinearHeading(hubPosition)
                .build();
        TrajectorySequence toHubLow = drive.trajectorySequenceBuilder(startPose)
                .lineToLinearHeading(new Pose2d(new Vector2d(-22.3, -37.3), Math.toRadians(45)))
                .build();

        switch (freightLocation) {
            case LEFT:
                drive.followTrajectorySequence(toHubLow);
                break;
            case MIDDLE:
                drive.followTrajectorySequence(toHubHigh);
                break;
            case RIGHT:
                drive.followTrajectorySequence(toHubHigh);
                break;
        }
        drive.linearSlideServo.setPosition(FreightFrenzyConstants.DUMP_SERVO_DROP);
        drive.pause(SERVO_DROP_PAUSE);
        drive.linearSlideServo.setPosition(FreightFrenzyConstants.DUMP_SERVO_BOTTOM);
        if (freightLocation == MultipleCameraCV.ShippingElementDeterminationPipeline.ElementPosition.LEFT) {
            drive.pause(300);
        }
        drive.stopShippingElementCamera();
        drive.retract();

//        To spin duck
        position = drive.getLocalizer().getPoseEstimate();

        TrajectorySequence toCarousel = drive.trajectorySequenceBuilder(position)
                .lineToLinearHeading(new Pose2d(new Vector2d(-61.5, -56.5), Math.toRadians(90)))
                .build();
        drive.followTrajectorySequence(toCarousel);

        drive.intakeMotor.setPower(1);
        drive.jevilTurnCarousel(.57, 4); //can we go faster?


        TrajectorySequence getOffCarousel = drive.trajectorySequenceBuilder(drive.getLocalizer().getPoseEstimate())
                .strafeTo(new Vector2d(-59.5, -50), SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH), SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();
        drive.followTrajectorySequence(getOffCarousel);


        drive.findDuckRed();

//        TrajectorySequence acquireDuck = drive.trajectorySequenceBuilder(drive.getLocalizer().getPoseEstimate())
//                .lineTo(new Vector2d(-55,-64))
//                .build();
//        drive.followTrajectorySequence(acquireDuck);

//        boolean hasDuck = drive

        drive.CV.duckWebcam.stopStreaming();

        drive.pause(250);
        drive.linearSlideServo.setPosition(FreightFrenzyConstants.DUMP_SERVO_LIFT);
        drive.pause(200);
        drive.linearSlideMotor.setTargetPosition(FreightFrenzyConstants.SLIDE_TOP);
        drive.linearSlideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        drive.linearSlideMotor.setPower(.8);

        position = drive.getLocalizer().getPoseEstimate();
        TrajectorySequence trajSeq6 = drive.trajectorySequenceBuilder(position)
                .lineToLinearHeading(hubPosition)
                .build();
        drive.followTrajectorySequence(trajSeq6);

        drive.linearSlideServo.setPosition(FreightFrenzyConstants.DUMP_SERVO_DROP);
        drive.pause(SERVO_DROP_PAUSE);
        drive.linearSlideServo.setPosition(FreightFrenzyConstants.DUMP_SERVO_BOTTOM);
        if (freightLocation == MultipleCameraCV.ShippingElementDeterminationPipeline.ElementPosition.LEFT) {
            drive.pause(300);
        }
        drive.retract();


        gotToPark();

    }

    protected void gotToPark() {
        position = drive.getLocalizer().getPoseEstimate();
        Pose2d parkPosition = new Pose2d(new Vector2d(-62, -35), Math.toRadians(0));
        TrajectorySequence trajSeq7 = drive.trajectorySequenceBuilder(position)
                .lineToLinearHeading(parkPosition)
                .build();
        drive.followTrajectorySequence(trajSeq7);
    }


}
