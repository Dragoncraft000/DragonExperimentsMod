package de.dragoncraft.dragonexperiments.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.*;

import java.lang.Math;

public class ShipComponent implements ShipComponentInterface {
    private final World provider;
    private Vec3d shipPos = new Vec3d(13000, 0, 0);

    private Quaternionf shipRotation = new Quaternionf();
    private Vec3d shipAngularVelocity = new Vec3d(0,0,0);
    private Vec3d shipVelocity = new Vec3d(0, 0, 0);
    public ShipComponent(World provider) {
        this.provider = provider;
    }

    public void setShipPos(Vec3d shipPos) {
        this.shipPos = shipPos;
        ModComponents.SHIP_COMPONENT.sync(this.provider);
    }


    public void setShipVelocity(Vec3d shipVelocity) {
        this.shipVelocity = shipVelocity;
        ModComponents.SHIP_COMPONENT.sync(this.provider);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.shipPos = new Vec3d(tag.getDouble("posX"), tag.getDouble("posY"), tag.getDouble("posZ"));
        this.shipRotation = new Quaternionf(tag.getDouble("rotationX"), tag.getDouble("rotationY"), tag.getDouble("rotationZ"), tag.getDouble("rotationW"));
        this.shipVelocity = new Vec3d(tag.getDouble("velocityX"), tag.getDouble("velocityY"), tag.getDouble("velocityZ"));
        this.shipAngularVelocity = new Vec3d(tag.getDouble("angularVelocityX"), tag.getDouble("angularVelocityY"), tag.getDouble("angularVelocityZ"));
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putDouble("posX", this.shipPos.x);
        tag.putDouble("posY", this.shipPos.y);
        tag.putDouble("posZ", this.shipPos.z);

        tag.putDouble("rotationX", this.shipRotation.x);
        tag.putDouble("rotationY", this.shipRotation.y);
        tag.putDouble("rotationZ", this.shipRotation.z);
        tag.putDouble("rotationW", this.shipRotation.w);

        tag.putDouble("velocityX", this.shipVelocity.x);
        tag.putDouble("velocityY", this.shipVelocity.y);
        tag.putDouble("velocityZ", this.shipVelocity.z);

        tag.putDouble("angularVelocityX", this.shipAngularVelocity.x);
        tag.putDouble("angularVelocityY", this.shipAngularVelocity.y);
        tag.putDouble("angularVelocityZ", this.shipAngularVelocity.z);
    }

    @Override
    public Vec3d getShipPosition() {
        return shipPos;
    }

    @Override
    public Quaternionf getShipRotation() {
        return this.shipRotation;
    }

    @Override
    public Quaternionf getShipAngularVelocity() {
        return this.shipRotation;
    }

    public void addAngularMomentum(Vector3f inputDegrees) {
        shipAngularVelocity = shipAngularVelocity.add(new Vec3d(inputDegrees.x, inputDegrees.y, inputDegrees.z));
    }
    public void addAngularDrag(float strength) {
        shipAngularVelocity = shipAngularVelocity.multiply(1 - strength);
    }

    public void applyRotationTick(Vector3f inputDegrees) {
        float yaw = (float) Math.toRadians(inputDegrees.y);
        float pitch = (float) Math.toRadians(inputDegrees.x);
        float roll = (float) Math.toRadians(inputDegrees.z);

        Quaternionf deltaRot = new Quaternionf().rotateYXZ(yaw, pitch, roll);

        shipRotation.mul(deltaRot).normalize();
    }
    @Override
    public Vec3d getShipVelocity() {
        return shipVelocity;
    }

    public void moveLocal(Vector3f steering) {
        steering = steering.rotate(shipRotation);
        setShipPos(getShipPosition().add(new Vec3d(steering.x,steering.y,steering.z)));
    }
    public void accelerateLocal(Vector3f steering) {
        steering = steering.rotate(shipRotation);
        setShipVelocity(getShipVelocity().add(new Vec3d(steering.x,steering.y,steering.z)));
    }

    public void brakeShip() {
        setShipVelocity(getShipVelocity().normalize().multiply(Math.max(0,getShipVelocity().length() - 2)));
    }
    @Override
    public void serverTick() {
        applyRotationTick(shipAngularVelocity.toVector3f());
        addAngularDrag(0.1f);
        setShipPos(shipPos.add(shipVelocity.add(0,0,0)));
    }



}
