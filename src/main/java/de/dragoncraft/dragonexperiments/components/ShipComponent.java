package de.dragoncraft.dragonexperiments.components;

import de.dragoncraft.dragonexperiments.DragonExperiments;
import de.dragoncraft.dragonexperiments.gamerules.ModGamerules;
import de.dragoncraft.dragonexperiments.solarsystem.CelestialBody;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ShipComponent implements ShipComponentInterface {
    private final World provider;
    private Vec3d shipPos = new Vec3d(13000, 0, 0);

    private Vec3d shipOrigin = new Vec3d(0,0,0);

    private Quaternionf shipRotation = new Quaternionf();
    private Vec3d shipAngularVelocity = new Vec3d(0,0,0);
    private Vec3d shipVelocity = new Vec3d(0, 0, 0);

    private String referenceFrame = "";
    private CelestialBody referenceFrameBody = null;

    private CelestialBody nearestBody = null;
    public ShipComponent(World provider) {
        this.provider = provider;
    }

    public void setShipPos(Vec3d shipPos) {
        this.shipPos = shipPos;
        ModComponents.SHIP_COMPONENT.sync(this.provider);
    }
    public void setShipOrigin(Vec3d shipOrigin) {
        this.shipOrigin = shipOrigin;
        ModComponents.SHIP_COMPONENT.sync(this.provider);
    }


    public void setShipVelocity(Vec3d shipVelocity) {
        this.shipVelocity = shipVelocity;
        ModComponents.SHIP_COMPONENT.sync(this.provider);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.shipPos = new Vec3d(tag.getDouble("posX"), tag.getDouble("posY"), tag.getDouble("posZ"));
        this.shipOrigin = new Vec3d(tag.getDouble("originX"), tag.getDouble("originY"), tag.getDouble("originZ"));
        this.shipRotation = new Quaternionf(tag.getDouble("rotationX"), tag.getDouble("rotationY"), tag.getDouble("rotationZ"), tag.getDouble("rotationW"));
        this.shipVelocity = new Vec3d(tag.getDouble("velocityX"), tag.getDouble("velocityY"), tag.getDouble("velocityZ"));
        this.shipAngularVelocity = new Vec3d(tag.getDouble("angularVelocityX"), tag.getDouble("angularVelocityY"), tag.getDouble("angularVelocityZ"));
        this.referenceFrame = tag.getString("referenceFrame");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putDouble("posX", this.shipPos.x);
        tag.putDouble("posY", this.shipPos.y);
        tag.putDouble("posZ", this.shipPos.z);

        tag.putDouble("originX", this.shipOrigin.x);
        tag.putDouble("originY", this.shipOrigin.y);
        tag.putDouble("originZ", this.shipOrigin.z);

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
        tag.putString("referenceFrame",referenceFrame);
    }

    @Override
    public Vec3d getShipPosition() {
        return shipPos;
    }

    @Override
    public Vec3d getShipOrigin() {
        return shipOrigin;
    }

    @Override
    public Quaternionf getShipRotation() {
        return this.shipRotation;
    }

    @Override
    public Quaternionf getShipAngularVelocity() {
        return this.shipRotation;
    }

    @Override
    public CelestialBody getReferenceFrameBody() {
        return this.referenceFrameBody;
    }
    @Override
    public String getReferenceFrame() {
        return this.referenceFrame;
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

    public void brakeShip(double brakeSpeed) {
        setShipVelocity(getShipVelocity().normalize().multiply(Math.max(0,getShipVelocity().length() - brakeSpeed)));
    }
    @Override
    public void serverTick() {
        Vec3d lastPos = shipPos.add(0,0,0);
        applyRotationTick(shipAngularVelocity.toVector3f());
        addAngularDrag(0.175f);
        tickGravity();
        checkReferenceFrame();
        if (Double.isNaN(shipPos.x) ||Double.isNaN(shipPos.y) ||Double.isNaN(shipPos.z)) {
            shipPos = lastPos;
        }

        setShipPos(shipPos.add(shipVelocity.add(0,0,0)));
    }
    private double getReferenceFrameSize(CelestialBody body) {
        return body.getRadius() * 5;
    }

    private void tickGravity() {
        if (!this.provider.getGameRules().getBoolean(ModGamerules.ENABLE_GRAVITY)) {
            return;
        }
        DragonExperiments.universe.getAllBodies().forEach(this::calculateGravity);
    }

    private void calculateGravity(CelestialBody body) {
        if (body == null) {
            return;
        }


        double G = 0.0001;
        double mass = 1.33333 * Math.PI * Math.pow(body.getRadius(),3);

        double distanceToReference = shipPos.squaredDistanceTo(body.getCurrentPosition());
        distanceToReference = Math.max(10,distanceToReference);
        double strength = G * (mass / distanceToReference);

        Vec3d vector = body.getCurrentPosition().subtract(shipPos).normalize();
        vector = vector.multiply(strength);
        setShipVelocity(shipVelocity.add(vector));
    }


    private void exitReferenceFrame() {
        if (referenceFrame.isEmpty()) {
            return;
        }
        System.out.println("Leaving Reference Frame of " + referenceFrame);
        if (referenceFrameBody != null) {
            setShipVelocity(shipVelocity.add(referenceFrameBody.getPlanetVelocity()));
        }
        referenceFrame = "";
        referenceFrameBody = null;
    }
    private void enterReferenceFrame(CelestialBody newFrame) {
        System.out.println("Entering Reference Frame of " + newFrame.getBodyName());
        referenceFrame = newFrame.getBodyName();
        referenceFrameBody = newFrame;
        setShipVelocity(shipVelocity.subtract(referenceFrameBody.getPlanetVelocity()));
    }

    private void switchReferenceFrame(CelestialBody newFrame) {
        if (!referenceFrame.isEmpty() && referenceFrameBody == null) {
            referenceFrameBody = DragonExperiments.universe.getCelestialBody(referenceFrame);
        }


        if (referenceFrameBody == newFrame || referenceFrame.equals(newFrame.getBodyName())) {
            return;
        }
        if (referenceFrame.isEmpty()) {
            enterReferenceFrame(newFrame);
            ModComponents.SHIP_COMPONENT.sync(this.provider);
            return;
        }
        exitReferenceFrame();
        enterReferenceFrame(newFrame);
        ModComponents.SHIP_COMPONENT.sync(this.provider);
    }

    private void tickReferenceFrame() {
        if (referenceFrameBody == null) {
            return;
        }
        setShipPos(shipPos.add(referenceFrameBody.getPlanetVelocity()));
        double distanceToReference = shipPos.distanceTo(referenceFrameBody.getCurrentPosition());
        if (distanceToReference < referenceFrameBody.getRadius() + 10 && referenceFrameBody.isHasCollision()) {
            Vec3d relativePos = shipPos.subtract(referenceFrameBody.getCurrentPosition()).normalize().multiply(referenceFrameBody.getRadius()+ 10.1 );
            shipPos = referenceFrameBody.getCurrentPosition().add(relativePos);

            setShipVelocity(new Vec3d(0,0,0));
        }
    }

    public void checkReferenceFrame() {
        if (DragonExperiments.universe == null) {
            return;
        }
        tickReferenceFrame();
        CelestialBody nearest = DragonExperiments.universe.getNearestBody(shipPos);
        nearestBody = nearest;
        if (nearest.getCurrentPosition().distanceTo(shipPos) > getReferenceFrameSize(nearest)) {
            exitReferenceFrame();
            ModComponents.SHIP_COMPONENT.sync(this.provider);
        } else {
            switchReferenceFrame(nearest);
        }

    }
}
