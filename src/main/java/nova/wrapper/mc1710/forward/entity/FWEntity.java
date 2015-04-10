package nova.wrapper.mc1710.forward.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import nova.core.entity.Entity;
import nova.core.entity.EntityFactory;
import nova.core.entity.EntityWrapper;
import nova.core.retention.Data;
import nova.core.retention.Storable;
import nova.core.util.components.Updater;
import nova.core.util.transform.Quaternion;
import nova.core.util.transform.Vector3d;
import nova.wrapper.mc1710.backward.world.BWWorld;
import nova.wrapper.mc1710.util.DataUtility;

/**
 * Entity wrapper
 * 
 * @author Calclavia
 */
public class FWEntity extends net.minecraft.entity.Entity implements EntityWrapper {

	public final Entity wrapped;
	private final MCRigidBody rigidBody = new MCRigidBody(this);

	public FWEntity(World world, EntityFactory factory) {
		super(world);
		this.wrapped = factory.makeEntity(this, rigidBody);
		entityInit();
	}

	public FWEntity(World world, Entity wrapped) {
		super(world);
		this.wrapped = wrapped;
		entityInit();
	}

	@Override
	protected void entityInit() {
		// Sadly, Minecraft wants to wake us up before we're done wrapping...
		if (wrapped != null)
			wrapped.awake();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		double deltaTime = 0.05;

		if (wrapped instanceof Updater) {
			((Updater) wrapped).update(deltaTime);
		}

		rigidBody.update(deltaTime);

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		if (wrapped instanceof Storable) {
			((Storable) wrapped).load(DataUtility.nbtToData(nbt));
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {

		if (wrapped instanceof Storable) {
			Data data = new Data();
			((Storable) wrapped).save(data);
			DataUtility.dataToNBT(nbt, data);
		}
	}

	/**
	 * Entity Wrapper Methods
	 * 
	 * @return
	 */
	@Override
	public boolean isValid() {
		return !isDead;
	}

	@Override
	public nova.core.world.World world() {
		return new BWWorld(worldObj);
	}

	@Override
	public Vector3d position() {
		return new Vector3d(posX, posY, posZ);
	}

	@Override
	public Quaternion rotation() {
		return Quaternion.fromEuler(Math.toRadians(rotationYaw), Math.toRadians(rotationPitch), 0);
	}

	@Override
	public void setWorld(nova.core.world.World world) {
		// TODO: Change entity's world
	}

	@Override
	public void setPosition(Vector3d position) {
		setPosition(position.x, position.y, position.z);
	}

	@Override
	public void setRotation(Quaternion rotation) {
		Vector3d euler = rotation.toEuler();
		setRotation((float) Math.toDegrees(euler.x), (float) Math.toDegrees(euler.y));
	}
}
