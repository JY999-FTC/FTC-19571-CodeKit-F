package org.firstinspires.ftc.teamcode.JackBurr.Drive;

import com.acmerobotics.dashboard.config.Config;

@Config
public class RobotConstantsV1 {
    //CONFIG=========================================================================================================
    //Port 0

    //GRIPPERS=======================================================================================================
    public double GRIPPERS_OPEN = 0.8;
    public double GRIPPERS_CLOSE = 0.1;
    public double GRIPPERS_GRAB = 0.4;
    public double DELIVERY_GRIPPERS_CLOSE = 0.7;
    public double DELIVERY_GRIPPERS_OPEN = 0.3;
    //===============================================================================================================
    //SERVOS=========================================================================================================
    public double FRONT_LEFT_TRANSFER = 0.9;
    public double FRONT_RIGHT_TRANSFER = 0.9;
    public double FRONT_LEFT_PICKUP = 0.03;
    public double FRONT_RIGHT_PICKUP = 0.03;
    public double FRONT_LEFT_LOW_HOVER = 0.095;
    public double FRONT_RIGHT_LOW_HOVER = 0.095;
    public double FRONT_LEFT_HOVER = 0.16;
    public double FRONT_RIGHT_HOVER = 0.16;
    public double DELIVERY_DOWN = 1;
    public double DELIVERY_GRAB = 0.26;
    public double DELIVERY_LEVEL_ONE_ASCENT = 0.55;
    public double DELIVERY_UP = 0.65;
    public double DELIVERY_DROP = 0.68;
    public double DELIVERY_HIGH_BAR = 0.73;
    public double DELIVERY_WALL_PICKUP = 0.85;
    public double WRIST_CENTER = 0.42;
    //MOTORS=========================================================================================================
    public int INTAKE_MOTOR_IN = 3;
    public int INTAKE_MOTOR_OUT = 850;
    public int INTAKE_MOTOR_ALL_THE_WAY_IN = 0;
    public int LEFT_SLIDE_HIGH_BASKET = -3179;
    public int RIGHT_SLIDE_HIGH_BASKET = 3190;
    public int LEFT_SLIDE_LOW_BASKET = -1416;
    public int RIGHT_SLIDE_LOW_BASKET = 1423;
    public int LEFT_SLIDE_HIGH_BAR = -633;
    public int RIGHT_SLIDE_HIGH_BAR = 644;

}
