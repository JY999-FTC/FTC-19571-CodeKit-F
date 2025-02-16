package org.firstinspires.ftc.teamcode.subsystems.complex;

import static android.os.SystemClock.sleep;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.claw.ClawActions;
import org.firstinspires.ftc.teamcode.subsystems.lift.LiftActions;
import org.firstinspires.ftc.teamcode.subsystems.slide.SlideActions;

public class ComplexActions {
    private LiftActions liftActions;
    private ClawActions clawActions;
    private SlideActions slideActions;

    private Servo claw;
    private Servo rotator;

    public ComplexActions(HardwareMap hardwareMap) {
        liftActions = new LiftActions(hardwareMap);
        clawActions = new ClawActions(hardwareMap);

        claw = hardwareMap.servo.get("claw");
        rotator = hardwareMap.servo.get("rotator");
    }

    public class grabCubeFromTray implements Action {
        private double clawPosition = 0.2;
        private double rotatorPosition = 0.2;
        private double slidePosition = 0;
        boolean slideStatus = true;
        boolean liftStatus = true;
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {

            claw.setPosition(clawPosition);
            sleep(250);
            if (claw.getPosition() == clawPosition){
                if (slideStatus) {
                    slideStatus = slideActions.SlideUp().run(packet);
                }
                if (liftStatus) {
                    liftStatus = liftActions.liftUp().run(packet);
                }
                else rotator.setPosition(rotatorPosition);
            }
            return rotator.getPosition() == rotatorPosition && claw.getPosition() == clawPosition && !liftStatus;
        }
    }

    public Action grabCubeFromTray() {
        return new grabCubeFromTray();
    }

    public class returnToTray implements Action {
        private double clawPosition = 1;
        private double rotatorPosition = 0.85;
//        boolean clawStatus = true;
        boolean liftStatus = true;
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
//            if (clawStatus) {
//                clawStatus = clawActions.releaseCube().run(packet);
//            }
            claw.setPosition(clawPosition);
            sleep(250);
            if (claw.getPosition() == clawPosition) {
                if (liftStatus) {
                    liftStatus = liftActions.liftDown().run(packet);
                }
            }
            return rotator.getPosition() == rotatorPosition && claw.getPosition() == clawPosition && !liftStatus;
        }

    }

    public Action returnToTray() {
        return new returnToTray();
    }
}