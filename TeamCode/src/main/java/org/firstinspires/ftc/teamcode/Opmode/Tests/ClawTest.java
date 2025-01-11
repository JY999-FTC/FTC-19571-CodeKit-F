package org.firstinspires.ftc.teamcode.Opmode.Tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Claw;

@TeleOp
@Config
// closed: 0.5,opened: 0.8,
public class ClawTest extends LinearOpMode {
    public static Boolean open=false;
    public static double c;
    public void runOpMode(){
        //Servo claw = hardwareMap.get(Servo.class, "claw");
        Claw claw = new Claw(hardwareMap);
        waitForStart();
        claw.setPosition(0.5);
        while(opModeIsActive()&&!isStopRequested()){
            if(gamepad1.left_bumper) {
                claw.setPosition(0.5);
            }if(gamepad1.right_bumper) {
                claw.setPosition(0.8);
            }
            if (isStopRequested()){
                claw.close();
                break;
            }
        }

    }
}
