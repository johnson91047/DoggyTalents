package doggytalents.common.talent;

import java.util.List;
import java.util.function.Predicate;

import doggytalents.DoggyTags;
import doggytalents.DoggyTalents;
import doggytalents.api.feature.DataKey;
import doggytalents.api.inferface.AbstractDogEntity;
import doggytalents.api.registry.Talent;
import doggytalents.common.inventory.PackPuppyItemHandler;
import doggytalents.common.util.InventoryUtil;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class PackPuppyTalent extends Talent {

    @CapabilityInject(PackPuppyItemHandler.class)
    public static Capability<PackPuppyItemHandler> PACK_PUPPY_CAPABILITY = null;

    public static DataKey<PackPuppyItemHandler> PACK_PUPPY_HANDLER = DataKey.makeFinal();
    public static DataKey<LazyOptional<?>> LAZY_PACK_PUPPY_HANDLER = DataKey.makeFinal();

    public static Predicate<ItemEntity> SHOULD_PICKUP_ENTITY_ITEM = (entity) -> {
        return entity.isAlive() && !entity.cannotPickup() && !entity.getItem().getItem().isIn(DoggyTags.PACK_PUPPY_BLACKLIST);// && !EntityAIFetch.BONE_PREDICATE.test(entity.getItem());
    };

    @Override
    public void tick(AbstractDogEntity dogIn) {
        if (dogIn.isAlive() && !dogIn.world.isRemote && dogIn.getLevel(this) >= 5) {
            List<ItemEntity> list = dogIn.world.getEntitiesWithinAABB(ItemEntity.class, dogIn.getBoundingBox().grow(2.5D, 1D, 2.5D), SHOULD_PICKUP_ENTITY_ITEM);

            if (!list.isEmpty()) {
                IItemHandler inventory = dogIn.getData(PACK_PUPPY_HANDLER);

                for (ItemEntity entityItem : list) {
                    ItemStack remaining = InventoryUtil.addItem(inventory, entityItem.getItem());

                    if (!remaining.isEmpty()) {
                        entityItem.setItem(remaining);
                    } else {
                        entityItem.remove();
                        dogIn.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.25F, ((dogIn.world.rand.nextFloat() - dogIn.world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    }
                }
            }
        }
    }

    @Override
    public void init(AbstractDogEntity dog) {
        PackPuppyItemHandler handler = new PackPuppyItemHandler(dog);
        dog.setDataIfEmpty(PACK_PUPPY_HANDLER, handler);
        dog.setDataIfEmpty(LAZY_PACK_PUPPY_HANDLER, LazyOptional.of(() -> handler));
    }

    @Override
    public ActionResultType processInteract(AbstractDogEntity dogIn, World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        int level = dogIn.getLevel(this);

        if (dogIn.isTamed() && level > 0) { // Dog requirements
            if (playerIn.isSneaking() && stack.isEmpty()) { // Player requirements

                if (dogIn.canInteract(playerIn)) {

                    if (!playerIn.world.isRemote) {
                        playerIn.sendStatusMessage(new TranslationTextComponent("talent.doggytalents.pack_puppy.version_migration"), false);
                    }
                    return ActionResultType.SUCCESS;
                }
            }
        }

        return ActionResultType.PASS;
    }

    @Override
    public void removed(AbstractDogEntity dog, int preLevel) {
        // No need to drop anything if dog didn't have pack puppy
        if (preLevel > 0) {
            this.dropInventory(dog);
        }
    }

    @Override
    public void dropInventory(AbstractDogEntity dogIn) {
        //TODO either drop inventory or save to respawn data, currently does both
        // No need to drop anything if dog didn't have pack puppy
        PackPuppyItemHandler inventory = dogIn.getData(PACK_PUPPY_HANDLER);
        for (int i = 0; i < inventory.getSlots(); ++i) {
            InventoryHelper.spawnItemStack(dogIn.world, dogIn.getPosX(), dogIn.getPosY(), dogIn.getPosZ(), inventory.getStackInSlot(i));
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void write(AbstractDogEntity dogIn, CompoundNBT compound) {
        PackPuppyItemHandler inventory = dogIn.getData(PACK_PUPPY_HANDLER);
        compound.merge(inventory.serializeNBT());
    }

    @Override
    public void read(AbstractDogEntity dogIn, CompoundNBT compound) {
        PackPuppyItemHandler inventory = dogIn.getData(PACK_PUPPY_HANDLER);
        inventory.deserializeNBT(compound);
    }

    @Override
    public <T> LazyOptional<T> getCapability(AbstractDogEntity dogIn, Capability<T> cap, Direction side) {
        if (cap == PackPuppyTalent.PACK_PUPPY_CAPABILITY) {
            return (LazyOptional<T>) dogIn.getData(LAZY_PACK_PUPPY_HANDLER);
        }
        return null;
    }

    @Override
    public void invalidateCapabilities(AbstractDogEntity dogIn) {
        dogIn.getData(LAZY_PACK_PUPPY_HANDLER).invalidate();
    }

    @Override
    public boolean hasRenderer() {
        return true;
    }

    public static boolean hasInventory(AbstractDogEntity dogIn) {
        return dogIn.isAlive() && dogIn.getData(PackPuppyTalent.PACK_PUPPY_HANDLER) != null && dogIn.getLevel(DoggyTalents.PACK_PUPPY) > 0;
    }
}
