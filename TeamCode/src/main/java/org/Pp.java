package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import java.util.Map;

class Ppbot{
    public DcMotor BLeft = null;
    public DcMotor BRight = null;
    public DcMotor FLeft = null;
    public DcMotor FRight = null;
    public DcMotor Slider = null;
    public Servo Take = null;

    HardwareMap map = null;
    public void init(HardwareMap maps) {
        map = maps;
        BLeft = maps.dcMotor.get("bl");
        BRight = maps.dcMotor.get("br");
        FLeft = maps.dcMotor.get("fl");
        FRight = maps.dcMotor.get("fr");
        //Take = maps.servo.get("take");
        //Slider = maps.dcMotor.get("s");

        BLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        BRight.setDirection(DcMotorSimple.Direction.FORWARD);
        FLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        FRight.setDirection(DcMotorSimple.Direction.FORWARD);
        //Slider.setDirection(DcMotorSimple.Direction.FORWARD);

        BLeft.setPower(0.0);
        BRight.setPower(0.0);
        FLeft.setPower(0.0);
        FRight.setPower(0.0);
        //Slider.setPower(0.0);
        //Take.setPosition(1.0);

        BLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //Slider.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }
}

@TeleOp (name = "PowerPlaybot", group = "pp")

public class Pp extends LinearOpMode{
    Ppbot robot = new Ppbot();
    double x;
    double y;
    double rx;
    double Armpos = 1.0;
    double Slidepos = 0.0;
    final double Armspeed = 0.1;
    final double Slidespeed = 0.5;
    final double rotationScalar = 0.5;
    final double speedScalar = 0.8;

    @Override

    public void runOpMode(){
        robot.init(hardwareMap);
        telemetry.addData("Say", "Hello");
        telemetry.update();

        waitForStart();
        
        //while we balling
        while(opModeIsActive()){
            // y = forward/back x = left strafe/right strafe, rx = rotation
            y = -gamepad1.left_stick_y;
            x = gamepad1.left_stick_x;
            rx = gamepad1.right_stick_x;

            //great fun math

            //if not turning, do a little driving.
            if (gamepad1.y) {
                y = 0.2;
            }
            if (Math.abs(rx) > 0.03){
                robot.BLeft.setPower(rotationScalar * -rx);
                robot.BRight.setPower(rotationScalar * -rx);
                robot.FLeft.setPower(rotationScalar * rx);
                robot.FRight.setPower(rotationScalar * rx);
            }
            // if we want to go foward/back do a little motor powering
            else if (Math.abs(y) >= Math.abs(x) && Math.abs(y) > 0.03){
                robot.BLeft.setPower(speedScalar * -y * 0.9); //-
                robot.BRight.setPower(speedScalar * y);
                robot.FLeft.setPower(speedScalar * y); //-
                robot.FRight.setPower(speedScalar * y * 1.1); //0

            }
            //if we want to go strafing, set a little moter powerfing for strafing
            else if (Math.abs(x) > (Math.abs(y)) && Math.abs(x) > 0.03){
                robot.BLeft.setPower(speedScalar * x);
                robot.BRight.setPower(speedScalar * x * 0.8);
                robot.FLeft.setPower(speedScalar * x);
                robot.FRight.setPower(speedScalar * -x);
            //if we dont want to move make sure we dont move
            } else {
                robot.BLeft.setPower(0);
                robot.BRight.setPower(0);
                robot.FLeft.setPower(0);
                robot.FRight.setPower(0);
            }
            //uppy downy.
            Slidepos = 0.0;
            if (gamepad1.right_bumper)
                Slidepos += Slidespeed;
            if (Math.abs(gamepad1.right_trigger) > 0.0)
                Slidepos -= Slidespeed;
            //open close :)
            if (gamepad1.x)
                Armpos -= Armspeed;
            if (gamepad1.b)
                Armpos += Armspeed;
            //set power and position for grabby and shit
            //robot.Slider.setPower(Slidepos);

            Armpos = Range.clip(Armpos, 0.0, 1.0);
            //robot.Take.setPosition(Armpos);

            //telemetry :nerd_emoji:
            telemetry.addData("x","%.2f", x);
            telemetry.addData("y","%.2f", y);
            telemetry.update();

            sleep(50);
        }
    }
}