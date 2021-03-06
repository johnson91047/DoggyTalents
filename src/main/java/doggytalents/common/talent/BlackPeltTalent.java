package doggytalents.common.talent;

import java.util.UUID;

import doggytalents.api.inferface.AbstractDogEntity;
import doggytalents.api.registry.Talent;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;

public class BlackPeltTalent extends Talent {

    private static final UUID BLACK_PELT_DAMAGE_ID = UUID.fromString("9abeafa9-3913-4b4c-b46e-0f1548fb19b3");
    private static final UUID BLACK_PELT_CRIT_CHANCE_ID = UUID.fromString("f07b5d39-a8cc-4d32-b458-6efdf1dc6836");
    private static final UUID BLACK_PELT_CRIT_BONUS_ID = UUID.fromString("e19e0d42-6ee3-4ee1-af1c-7519af4354cd");

    @Override
    public void init(AbstractDogEntity dogIn) {
        dogIn.setAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, BLACK_PELT_DAMAGE_ID, this::createPeltModifier);
        dogIn.setAttributeModifier(AbstractDogEntity.CRIT_CHANCE, BLACK_PELT_CRIT_CHANCE_ID, this::createPeltCritChance);
        dogIn.setAttributeModifier(AbstractDogEntity.CRIT_BONUS, BLACK_PELT_CRIT_BONUS_ID, this::createPeltCritBonus);
    }

    @Override
    public void set(AbstractDogEntity dogIn, int level) {
        dogIn.setAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, BLACK_PELT_DAMAGE_ID, this::createPeltModifier);
        dogIn.setAttributeModifier(AbstractDogEntity.CRIT_CHANCE, BLACK_PELT_CRIT_CHANCE_ID, this::createPeltCritChance);
        dogIn.setAttributeModifier(AbstractDogEntity.CRIT_BONUS, BLACK_PELT_CRIT_BONUS_ID, this::createPeltCritBonus);
    }

    @Override
    public void removed(AbstractDogEntity dogIn, int preLevel) {
        dogIn.removeAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, BLACK_PELT_DAMAGE_ID);
        dogIn.removeAttributeModifier(AbstractDogEntity.CRIT_CHANCE, BLACK_PELT_CRIT_CHANCE_ID);
        dogIn.removeAttributeModifier(AbstractDogEntity.CRIT_BONUS, BLACK_PELT_CRIT_BONUS_ID);
    }

    public AttributeModifier createPeltModifier(AbstractDogEntity dogIn, UUID uuidIn) {
        int level = dogIn.getLevel(this);

        if (level > 0) {
            double damageBonus = level;

            if (level >= 5) {
                damageBonus += 2;
            }

            return new AttributeModifier(uuidIn, "Black Pelt", damageBonus, AttributeModifier.Operation.ADDITION).setSaved(false);
        }

        return null;
    }

    public AttributeModifier createPeltCritChance(AbstractDogEntity dogIn, UUID uuidIn) {
        int level = dogIn.getLevel(this);

        if (level <= 0) {
            return null;
        }

        double damageBonus = 0.15D * level;

        if (level >= 5) {
            damageBonus = 1D;
        }

        return new AttributeModifier(uuidIn, "Black Pelt Crit Chance", damageBonus, AttributeModifier.Operation.ADDITION).setSaved(false);
    }

    public AttributeModifier createPeltCritBonus(AbstractDogEntity dogIn, UUID uuidIn) {
        int level = dogIn.getLevel(this);

        if (level <= 0) {
            return null;
        }

        return new AttributeModifier(uuidIn, "Black Pelt Crit Bonus", 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL).setSaved(false);
    }
}
