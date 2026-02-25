/*     */ package fun.rockstarity.client.modules.move;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.inputs.EventInput;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*     */ import fun.rockstarity.api.events.list.player.EventAction;
/*     */ import fun.rockstarity.api.events.list.player.EventDamageReceive;
/*     */ import fun.rockstarity.api.events.list.player.EventJump;
/*     */ import fun.rockstarity.api.events.list.player.EventMotion;
/*     */ import fun.rockstarity.api.events.list.player.EventMotionMove;
/*     */ import fun.rockstarity.api.events.list.player.EventMove;
/*     */ import fun.rockstarity.api.events.list.player.EventPostMotionMove;
/*     */ import fun.rockstarity.api.events.list.player.EventTrace;
/*     */ import fun.rockstarity.api.helpers.math.aura.Rotation;
/*     */ import fun.rockstarity.api.helpers.player.DamagePlayerUtil;
/*     */ import fun.rockstarity.api.helpers.player.MoveUtils;
/*     */ import fun.rockstarity.api.helpers.player.StrafeMovement;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.block.Blocks;
/*     */ import net.minecraft.block.material.Material;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CEntityActionPacket;
/*     */ import net.minecraft.network.play.server.SPlayerPositionLookPacket;
/*     */ import net.minecraft.potion.Effects;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "Strafe", desc = "Позволяет двигаться во все стороны с одинаковой скоростью", type = Category.MOVE)
/*     */ public class Strafe
/*     */   extends Module
/*     */ {
/*  47 */   private final Mode mode = new Mode((Bindable)this, "Режим");
/*  48 */   private final Mode.Element matrix = new Mode.Element(this.mode, "Matrix");
/*  49 */   private final Mode.Element univer = new Mode.Element(this.mode, "Универсальные");
/*     */   
/*  51 */   private final CheckBox damageBoost = (new CheckBox((Bindable)this, "Ускорение от урона")).set(true).hide(() -> Boolean.valueOf(!this.mode.is(this.matrix)));
/*  52 */   private final Slider boost = (new Slider((Bindable)this, "Сила ускорения")).min(0.1F).max(5.0F).inc(0.1F).set(0.7F).desc("Сила ускорения при получении урона").hide(() -> Boolean.valueOf(!this.damageBoost.get()));
/*     */   
/*  54 */   private final DamagePlayerUtil damageUtil = new DamagePlayerUtil();
/*  55 */   private final StrafeMovement strafeMovement = new StrafeMovement();
/*     */   public float getYaw() {
/*  57 */     return this.yaw;
/*     */   }
/*     */   private float yaw;
/*     */   public Strafe() {
/*  61 */     super(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  66 */     if (this.mode.is(this.univer)) {
/*  67 */       handlePackets(event);
/*     */       
/*  69 */       if (event instanceof EventInput) { EventInput e = (EventInput)event;
/*     */ 
/*     */         
/*  72 */         if (this.yaw == Rotation.getDirection()) {
/*  73 */           e.setForward((mc.player.movementInput.moveForward != 0.0F || mc.player.movementInput.moveStrafe != 0.0F) ? 1.0F : 0.0F);
/*  74 */           e.setStrafe(0.0F);
/*     */         } else {
/*  76 */           e.setYaw(this.yaw + mc.player.rotationYaw);
/*     */         }  }
/*     */ 
/*     */ 
/*     */       
/*  81 */       if (event instanceof EventMove) { EventMove e = (EventMove)event;
/*  82 */         e.setYaw(this.yaw + mc.player.rotationYaw); }
/*     */ 
/*     */ 
/*     */       
/*  86 */       if (event instanceof EventJump) { EventJump e = (EventJump)event;
/*  87 */         e.setYaw(this.yaw + mc.player.rotationYaw); }
/*     */ 
/*     */       
/*  90 */       if (event instanceof EventTrace) { EventTrace e = (EventTrace)event;
/*  91 */         e.setYaw(this.yaw + mc.player.rotationYaw);
/*  92 */         e.cancel(); }
/*     */ 
/*     */       
/*  95 */       if (event instanceof EventMotion) { EventMotion e = (EventMotion)event;
/*     */ 
/*     */         
/*  98 */         this.yaw = Rotation.getDirection() - mc.player.rotationYaw;
/*     */         
/* 100 */         e.setYaw(this.yaw + mc.player.rotationYaw);
/*     */          }
/*     */ 
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 107 */       if (event instanceof EventAction) { EventAction e = (EventAction)event; handleEventAction(e); }
/* 108 */        if (event instanceof EventMotionMove) { EventMotionMove e = (EventMotionMove)event; handleEventMove(e); }
/* 109 */        if (event instanceof EventPostMotionMove) { EventPostMotionMove e = (EventPostMotionMove)event; handleEventPostMove(e); }
/* 110 */        if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event; handleEventPacket(e); }
/* 111 */        if (event instanceof EventDamageReceive) { EventDamageReceive e = (EventDamageReceive)event; handleDamageEvent(e); }
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   @NativeInclude
/*     */   private void handlePackets(Event event) {
/* 118 */     if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/* 119 */       IPacket iPacket = e.getPacket(); if (iPacket instanceof SPlayerPositionLookPacket) { SPlayerPositionLookPacket packet = (SPlayerPositionLookPacket)iPacket;
/* 120 */         packet.setYaw(mc.player.rotationYaw);
/* 121 */         packet.setPitch(mc.player.rotationPitch); }
/*     */        }
/*     */   
/*     */   }
/*     */   
/*     */   private void handleDamageEvent(EventDamageReceive damage) {
/* 127 */     if (this.damageBoost.get()) {
/* 128 */       this.damageUtil.processDamage(damage);
/*     */     }
/*     */   }
/*     */   
/*     */   private void handleEventAction(EventAction action) {
/* 133 */     if (strafes()) handleStrafesEventAction(action); 
/* 134 */     if (this.strafeMovement.isNeedSwap()) handleNeedSwapEventAction(action); 
/*     */   }
/*     */   
/*     */   private void handleEventMove(EventMotionMove eventMove) {
/* 138 */     if (strafes()) {
/* 139 */       handleStrafesEventMove(eventMove);
/*     */     } else {
/* 141 */       this.strafeMovement.setOldSpeed(0.0D);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleEventPostMove(EventPostMotionMove eventPostMove) {
/* 146 */     this.strafeMovement.postMove(eventPostMove.getSpeed());
/*     */   }
/*     */   
/*     */   private void handleEventPacket(EventReceivePacket packet) {
/* 150 */     if (this.damageBoost.get()) this.damageUtil.onPacketEvent(packet); 
/* 151 */     handleReceivePacketEventPacket(packet);
/*     */   }
/*     */   
/*     */   private void handleStrafesEventAction(EventAction action) {
/* 155 */     if (CEntityActionPacket.lastUpdatedSprint != this.strafeMovement.isNeedSprintState()) {
/* 156 */       action.setSprintState(!CEntityActionPacket.lastUpdatedSprint);
/*     */     }
/*     */   }
/*     */   
/*     */   private void handleStrafesEventMove(EventMotionMove eventMove) {
/* 161 */     if (this.damageBoost.get()) this.damageUtil.time(700L);
/*     */     
/* 163 */     float damageSpeed = this.boost.get() / 10.0F;
/* 164 */     double speed = this.strafeMovement.calculateSpeed(eventMove, this.damageBoost.get(), this.damageUtil.isNormalDamage(), false, damageSpeed);
/*     */     
/* 166 */     MoveUtils.MoveEvent.setMoveMotion(eventMove, speed);
/*     */   }
/*     */   
/*     */   private void handleNeedSwapEventAction(EventAction action) {
/* 170 */     action.setSprintState(!mc.player.serverSprintState);
/* 171 */     this.strafeMovement.setNeedSwap(false);
/*     */   }
/*     */   
/*     */   private void handleReceivePacketEventPacket(EventReceivePacket packet) {
/* 175 */     if (packet.getPacket() instanceof SPlayerPositionLookPacket) this.strafeMovement.setOldSpeed(0.0D); 
/*     */   }
/*     */   
/*     */   public boolean strafes() {
/* 179 */     if (isInvalidPlayerState()) return false; 
/* 180 */     BlockPos playerPosition = new BlockPos(mc.player.getPositionVec());
/* 181 */     BlockPos abovePosition = playerPosition.up();
/* 182 */     BlockPos belowPosition = playerPosition.down();
/* 183 */     if (isSurfaceLiquid(abovePosition, belowPosition)) return false; 
/* 184 */     if (isPlayerInWebOrSoulSand(playerPosition)) return false; 
/* 185 */     return isPlayerAbleToStrafe();
/*     */   }
/*     */   
/*     */   private boolean isInvalidPlayerState() {
/* 189 */     return (mc.player == null || mc.world == null || mc.player
/* 190 */       .isSneaking() || mc.player
/* 191 */       .isElytraFlying() || mc.player
/* 192 */       .isInWater() || mc.player
/* 193 */       .isInLava());
/*     */   }
/*     */   
/*     */   private boolean isSurfaceLiquid(BlockPos abovePosition, BlockPos belowPosition) {
/* 197 */     Block aboveBlock = mc.world.getBlockState(abovePosition).getBlock();
/* 198 */     Block belowBlock = mc.world.getBlockState(belowPosition).getBlock();
/* 199 */     return (aboveBlock instanceof net.minecraft.block.AirBlock && belowBlock == Blocks.WATER);
/*     */   }
/*     */   
/*     */   private boolean isPlayerInWebOrSoulSand(BlockPos playerPosition) {
/* 203 */     Material playerMaterial = mc.world.getBlockState(playerPosition).getMaterial();
/* 204 */     Block oneBelowBlock = mc.world.getBlockState(playerPosition.down()).getBlock();
/* 205 */     return (playerMaterial == Material.WEB || oneBelowBlock instanceof net.minecraft.block.SoulSandBlock);
/*     */   }
/*     */   
/*     */   private boolean isPlayerAbleToStrafe() {
/* 209 */     return (!mc.player.abilities.isFlying && !mc.player.isPotionActive(Effects.LEVITATION));
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/* 214 */     this.strafeMovement.setOldSpeed(0.0D);
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\Strafe.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */