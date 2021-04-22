package dev.semisol.quasarclient.etc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.semisol.quasarclient.QuasarClient;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.explosion.Explosion;

public class PlayerUtils {

    public static boolean isMoving() {
        return QuasarClient.minecraft.player.input.movementForward != 0.0f || QuasarClient.minecraft.player.input.movementSideways != 0.0f;
    }

    public static double getBaseMoveSpeed() {
        return 0.2873D;
    }

    public static void setMoveSpeed(double speed) {
        double forward = QuasarClient.minecraft.player.forwardSpeed;
        double strafe = QuasarClient.minecraft.player.sidewaysSpeed;
        float yaw = QuasarClient.minecraft.player.yaw;
        if(forward != 0) {
            if(strafe > 0) {
                yaw += ((forward > 0) ? -45 : 45);
            } else if(strafe < 0) {
                yaw += ((forward > 0) ? 45 : -45);
            }
            strafe = 0;
            if(forward > 0) {
                forward = 1;
            }else {
                forward = -1;
            }
        }
        QuasarClient.minecraft.player.setVelocity(forward * speed * Math.cos(Math.toRadians((yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((yaw + 90.0F))), QuasarClient.minecraft.player.getVelocity().getY(), forward * speed * Math.sin(Math.toRadians((yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((yaw + 90.0F))));
    }

    public static void setMoveSpeedRidingEntity(double speed) {
        double forward = QuasarClient.minecraft.player.forwardSpeed;
        double strafe = QuasarClient.minecraft.player.sidewaysSpeed;
        float yaw = QuasarClient.minecraft.player.yaw;
        if(forward != 0) {
            if(strafe > 0) {
                yaw += ((forward > 0) ? -45 : 45);
            } else if(strafe < 0) {
                yaw += ((forward > 0) ? 45 : -45);
            }
            strafe = 0;
            if(forward > 0) {
                forward = 1;
            }else {
                forward = -1;
            }
        }
        QuasarClient.minecraft.player.getVehicle().setVelocity(forward * speed * Math.cos(Math.toRadians((yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((yaw + 90.0F))), QuasarClient.minecraft.player.getVehicle().getVelocity().getY(), forward * speed * Math.sin(Math.toRadians((yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((yaw + 90.0F))));
    }

    public static void setSpeed(double speed)
    {
        QuasarClient.minecraft.player.setVelocity(-MathHelper.sin(getDirection()) * speed, QuasarClient.minecraft.player.getVelocity().getY(), MathHelper.cos(getDirection()) * speed);
    }

    public static float getDirection()
    {
        float yaw = QuasarClient.minecraft.player.yaw;
        float forward = QuasarClient.minecraft.player.forwardSpeed;
        float strafe = QuasarClient.minecraft.player.sidewaysSpeed;
        yaw += (forward < 0.0F ? 180 : 0);
        if (strafe < 0.0F) {
            yaw += (forward == 0.0F ? 90 : forward < 0.0F ? -45 : 45);
        }
        if (strafe > 0.0F) {
            yaw -= (forward == 0.0F ? 90 : forward < 0.0F ? -45 : 45);
        }
        return yaw * 0.017453292F;
    }

    public static float getSpeed()
    {
        return (float)Math.sqrt(QuasarClient.minecraft.player.getVelocity().getX() * QuasarClient.minecraft.player.getVelocity().getX() + QuasarClient.minecraft.player.getVelocity().getZ() * QuasarClient.minecraft.player.getVelocity().getZ());
    }

    public static void setCockSpeed(final double moveSpeed, final float pseudoYaw, final double pseudoStrafe, final double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;

        if (forward == 0.0 && strafe == 0.0) {
            QuasarClient.minecraft.player.setVelocity(0, QuasarClient.minecraft.player.getVelocity().getY(), 0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            Random random = new Random();
            int min = 1;
            int max = 15;
            int delay = random.nextInt((int)max + 1 - (int)min) + (int)min;

            final double cos = Math.cos(Math.toRadians(yaw + 90.0f + delay));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f + delay));

            QuasarClient.minecraft.player.setVelocity(forward * moveSpeed * cos + strafe * moveSpeed * sin, QuasarClient.minecraft.player.getVelocity().getY(), forward * moveSpeed * sin - strafe * moveSpeed * cos);
        }
    }

    public static float getAttackDamage(PlayerEntity attacker, LivingEntity target) {
        float cooldown = attacker.getAttackCooldownProgress(0.5F);

        float damage = (float) attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * (0.2f + cooldown * cooldown * 0.8f);
        float enchDamage = EnchantmentHelper.getAttackDamage(attacker.getMainHandStack(), target.getGroup()) * cooldown;

        if (damage <= 0f && enchDamage <= 0f) {
            return 0f;
        }

        // Crits
        if (cooldown > 0.9
                && attacker.fallDistance > 0.0F
                && !attacker.isOnGround()
                && !attacker.isClimbing()
                && !attacker.isTouchingWater()
                && !attacker.hasStatusEffect(StatusEffects.BLINDNESS)
                && !attacker.hasVehicle()
                && !attacker.isSprinting()
                && target instanceof LivingEntity) {
            damage *= 1.5f;
        }

        damage += enchDamage;

        // Armor
        damage = DamageUtil.getDamageLeft(damage, target.getArmor(),
                (float) target.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));

        // Enchantments
        if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
            int resistance = 25 - (target.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
            float resistance_1 = damage * resistance;
            damage = Math.max(resistance_1 / 25f, 0f);
        }

        if (damage <= 0f) {
            damage = 0f;
        } else {
            int protAmount = EnchantmentHelper.getProtectionAmount(target.getArmorItems(), DamageSource.player(attacker));
            if (protAmount > 0) {
                damage = DamageUtil.getInflictedDamage(damage, protAmount);
            }
        }

        return damage;
    }

    public static float getExplosionDamage(Vec3d explosionPos, float power, LivingEntity target) {
        if (QuasarClient.minecraft.world.getDifficulty() == Difficulty.PEACEFUL)
            return 0f;

        Explosion explosion = new Explosion(QuasarClient.minecraft.world, null, explosionPos.x, explosionPos.y, explosionPos.z, power, false, Explosion.DestructionType.DESTROY);

        double maxDist = power * 2;
        if (!QuasarClient.minecraft.world.getOtherEntities(null, new Box(
                MathHelper.floor(explosionPos.x - maxDist - 1.0),
                MathHelper.floor(explosionPos.y - maxDist - 1.0),
                MathHelper.floor(explosionPos.z - maxDist - 1.0),
                MathHelper.floor(explosionPos.x + maxDist + 1.0),
                MathHelper.floor(explosionPos.y + maxDist + 1.0),
                MathHelper.floor(explosionPos.z + maxDist + 1.0))).contains(target)) {
            return 0f;
        }

        if (!target.isImmuneToExplosion() && !target.isInvulnerable()) {
            double distExposure = MathHelper.sqrt(target.squaredDistanceTo(explosionPos)) / maxDist;
            if (distExposure <= 1.0) {
                double xDiff = target.getX() - explosionPos.x;
                double yDiff = target.getEyeY() - explosionPos.y;
                double zDiff = target.getZ() - explosionPos.z;
                double diff = MathHelper.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
                if (diff != 0.0) {
                    double exposure = Explosion.getExposure(explosionPos, target);
                    double finalExposure = (1.0 - distExposure) * exposure;

                    float toDamage = (float) Math.floor((finalExposure * finalExposure + finalExposure) / 2.0 * 7.0 * maxDist + 1.0);

                    if (target instanceof PlayerEntity) {
                        if (QuasarClient.minecraft.world.getDifficulty() == Difficulty.EASY) {
                            toDamage = Math.min(toDamage / 2f + 1f, toDamage);
                        } else if (QuasarClient.minecraft.world.getDifficulty() == Difficulty.HARD) {
                            toDamage = toDamage * 3f / 2f;
                        }
                    }

                    // Armor
                    toDamage = DamageUtil.getDamageLeft(toDamage, target.getArmor(),
                            (float) target.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());

                    // Enchantments
                    if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
                        int resistance = 25 - (target.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
                        float resistance_1 = toDamage * resistance;
                        toDamage = Math.max(resistance_1 / 25f, 0f);
                    }

                    if (toDamage <= 0f) {
                        toDamage = 0f;
                    } else {
                        int protAmount = EnchantmentHelper.getProtectionAmount(target.getArmorItems(), explosion.getDamageSource());
                        if (protAmount > 0) {
                            toDamage = DamageUtil.getInflictedDamage(toDamage, protAmount);
                        }
                    }

                    return toDamage;
                }
            }
        }

        return 0f;
    }

    public static boolean willExplosionKill(Vec3d explosionPos, float power, LivingEntity target) {
        if (target.getMainHandStack().getItem() == Items.TOTEM_OF_UNDYING || target.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
            return false;
        }

        return getExplosionDamage(explosionPos, power, target) >= target.getHealth() + target.getAbsorptionAmount();
    }

    public static boolean willExplosionPop(Vec3d explosionPos, float power, LivingEntity target) {
        if (target.getMainHandStack().getItem() != Items.TOTEM_OF_UNDYING && target.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
            return false;
        }

        return getExplosionDamage(explosionPos, power, target) >= target.getHealth() + target.getAbsorptionAmount();
    }

    public static boolean willExplosionPopOrKill(Vec3d explosionPos, float power, LivingEntity target) {
        return getExplosionDamage(explosionPos, power, target) >= target.getHealth() + target.getAbsorptionAmount();
    }

    /** Returns the first slot that matches the Predicate **/
    public static int getSlot(boolean offhand, Predicate<Integer> filter) {
        return IntStream.of(getInventorySlots(offhand))
                .boxed()
                .filter(filter)
                .findFirst().orElse(-1);
    }

    public static int getSlot(boolean offhand, boolean reverse, Comparator<Integer> comparator) {
        return IntStream.of(getInventorySlots(offhand))
                .boxed()
                .min(comparator.reversed()).orElse(-1);
    }

    /** Selects the first slot that matches the Predicate and returns the hand it selected **/
    public static Hand selectSlot(boolean offhand, Predicate<Integer> filter) {
        return selectSlot(getSlot(offhand, filter));
    }

    public static Hand selectSlot(boolean offhand, boolean reverse, Comparator<Integer> comparator) {
        return selectSlot(getSlot(offhand, reverse, comparator));
    }

    public static Hand selectSlot(int slot) {
        if (slot >= 0 && slot <= 36) {
            if (slot < 9) {
                if (slot != QuasarClient.minecraft.player.inventory.selectedSlot) {
                    QuasarClient.minecraft.player.inventory.selectedSlot = slot;
                    QuasarClient.minecraft.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                }

                return Hand.MAIN_HAND;
            } else if (QuasarClient.minecraft.player.playerScreenHandler == QuasarClient.minecraft.player.currentScreenHandler) {
                if (QuasarClient.minecraft.player.inventory.getMainHandStack().isEmpty()) {
                    QuasarClient.minecraft.interactionManager.clickSlot(QuasarClient.minecraft.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, QuasarClient.minecraft.player);
                    QuasarClient.minecraft.interactionManager.clickSlot(QuasarClient.minecraft.player.currentScreenHandler.syncId, 36 + QuasarClient.minecraft.player.inventory.selectedSlot, 0, SlotActionType.PICKUP, QuasarClient.minecraft.player);
                    return Hand.MAIN_HAND;
                }

                for (int i = 0; i <= 8; i++) {
                    if (QuasarClient.minecraft.player.inventory.getStack(i).isEmpty()) {
                        QuasarClient.minecraft.interactionManager.clickSlot(QuasarClient.minecraft.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, QuasarClient.minecraft.player);
                        QuasarClient.minecraft.interactionManager.clickSlot(QuasarClient.minecraft.player.currentScreenHandler.syncId, 36 + i, 0, SlotActionType.PICKUP, QuasarClient.minecraft.player);
                        QuasarClient.minecraft.player.inventory.selectedSlot = i;
                        QuasarClient.minecraft.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                        return Hand.MAIN_HAND;
                    }
                }

                QuasarClient.minecraft.interactionManager.clickSlot(QuasarClient.minecraft.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, QuasarClient.minecraft.player);
                QuasarClient.minecraft.interactionManager.clickSlot(QuasarClient.minecraft.player.currentScreenHandler.syncId, 36 + QuasarClient.minecraft.player.inventory.selectedSlot, 0, SlotActionType.PICKUP, QuasarClient.minecraft.player);
                QuasarClient.minecraft.interactionManager.clickSlot(QuasarClient.minecraft.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, QuasarClient.minecraft.player);
                return Hand.MAIN_HAND;
            }
        } else if (slot == 40) {
            return Hand.OFF_HAND;
        }

        return null;
    }

    private static int[] getInventorySlots(boolean offhand) {
        int[] i = new int[offhand ? 38 : 37];

        // Add hand slots first
        i[0] = QuasarClient.minecraft.player.inventory.selectedSlot;
        i[1] = 40;

        for (int j = 0; j < 36; j++) {
            if (j != QuasarClient.minecraft.player.inventory.selectedSlot) {
                i[offhand ? j + 2 : j + 1] = j;
            }
        }

        return i;
    }

    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean thirteen, final boolean specialEntityCheck) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (QuasarClient.minecraft.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && QuasarClient.minecraft.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if ((QuasarClient.minecraft.world.getBlockState(boost).getBlock() != Blocks.AIR || (QuasarClient.minecraft.world.getBlockState(boost2).getBlock() != Blocks.AIR && !thirteen))) {
                return false;
            }
        }
        catch (Exception ignored) {
            return false;
        }
        return true;
    }

    public static List<BlockPos> getSphere(final BlockPos pos, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final List<BlockPos> circleBlocks = new ArrayList<>();
        final int cx = pos.getX();
        final int cy = pos.getY();
        final int cz = pos.getZ();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                for (int y = sphere ? (cy - (int)r) : cy; y < (sphere ? (cy + r) : ((float)(cy + h))); ++y) {
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        final BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }
        return circleBlocks;
    }

    public static BlockPos getPlayerPos(final PlayerEntity player) {
        return new BlockPos(Math.floor(player.getPos().getX()), Math.floor(player.getPos().getY()), Math.floor(player.getPos().getZ()));
    }

    public static List<BlockPos> possiblePlacePositions(final float placeRange, final boolean thirteen, final boolean specialEntityCheck) {
        List<BlockPos> positions = new ArrayList<>();
        positions.addAll(getSphere(getPlayerPos(QuasarClient.minecraft.player), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, thirteen, specialEntityCheck)).collect(Collectors.toList()));
        return positions;
    }

}
