package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.util.ElapsedTime;

public class PIDController {
    private float p;
    private float i;
    private float d;
    private float integralSummation;
    private float lastError;
    private ElapsedTime timer;
    private float previousReference = 0;

    public void init(float Xp, float Xi, float Xd) {
        p = Xp;
        i = Xi;
        d = Xd;
        timer = new ElapsedTime();
    }
    public float getOutput(float state, float reference, TelemetryPacket packet, String name) {
        if (reference != previousReference) {
            integralSummation = 0;
        }
        float error = reference - state;
        integralSummation += (float) (error * timer.seconds());
        float derivative = (float) ((error - lastError) / timer.seconds());
        lastError = error;
        previousReference = reference;
//        packet.put(name + "Integral Summation", integralSummation);
        return (error * p) + (derivative * d) + (integralSummation * i);
    }
}