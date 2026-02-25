/*     */ package fun.rockstarity.client.modules.move;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*     */ import fun.rockstarity.api.events.list.player.EventMotion;
/*     */ import fun.rockstarity.api.helpers.game.Server;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Bypass;
/*     */ import fun.rockstarity.api.helpers.player.Inventory;
/*     */ import fun.rockstarity.api.helpers.player.Move;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import fun.rockstarity.api.render.ui.alerts.AlertType;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import javafx.animation.Interpolator;
/*     */ import net.minecraft.block.BlockState;
/*     */ import net.minecraft.block.Blocks;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.LivingEntity;
/*     */ import net.minecraft.entity.item.ArmorStandEntity;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CConfirmTeleportPacket;
/*     */ import net.minecraft.network.play.client.CEntityActionPacket;
/*     */ import net.minecraft.network.play.client.CHeldItemChangePacket;
/*     */ import net.minecraft.network.play.client.CPlayerDiggingPacket;
/*     */ import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
/*     */ import net.minecraft.network.play.server.SPlayerPositionLookPacket;
/*     */ import net.minecraft.potion.EffectInstance;
/*     */ import net.minecraft.potion.Effects;
/*     */ import net.minecraft.util.Direction;
/*     */ import net.minecraft.util.Hand;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.BlockRayTraceResult;
/*     */ import net.minecraft.util.math.MathHelper;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "Speed", desc = "Ускоряет игрока", type = Category.MOVE)
/*     */ public class Speed
/*     */   extends Module
/*     */ {
/*     */   private boolean wasTimer;
/*     */   private double pol;
/*     */   private boolean alreadyUsed;
/*     */   
/*     */   public boolean isWasTimer() {
/*  68 */     return this.wasTimer;
/*  69 */   } public double getPol() { return this.pol; } public boolean isAlreadyUsed() {
/*  70 */     return this.alreadyUsed;
/*  71 */   } private int lastSlot = -1; public int getLastSlot() { return this.lastSlot; }
/*  72 */    private final TimerUtility timers = new TimerUtility(); public TimerUtility getTimers() { return this.timers; }
/*     */   
/*  74 */   private final Mode mode = new Mode((Bindable)this, "Режим"); public Mode getMode() { return this.mode; }
/*  75 */    private final Mode.Element vanila = new Mode.Element(this.mode, "Vanila"); public Mode.Element getVanila() { return this.vanila; }
/*  76 */    private final Mode.Element airtick = new Mode.Element(this.mode, "Intave"); public Mode.Element getAirtick() { return this.airtick; }
/*  77 */    private final Mode.Element ft = new Mode.Element(this.mode, "FunTime"); public Mode.Element getFt() { return this.ft; }
/*  78 */    private final Mode.Element ft2 = new Mode.Element(this.mode, "Коллизия"); public Mode.Element getFt2() { return this.ft2; }
/*  79 */    private final Mode modeCollision = (new Mode((Bindable)this.ft2, "Режим коллизии")).hide(() -> Boolean.valueOf(!this.mode.is(this.ft2))); public Mode getModeCollision() { return this.modeCollision; }
/*  80 */    private final Mode.Element oldCollision = new Mode.Element(this.modeCollision, "Старый"); public Mode.Element getOldCollision() { return this.oldCollision; }
/*  81 */    private final Mode.Element newCollision = new Mode.Element(this.modeCollision, "Новый"); public Mode.Element getNewCollision() { return this.newCollision; }
/*  82 */    private final Mode.Element spookySilent = new Mode.Element(this.modeCollision, "Spooky тихий"); public Mode.Element getSpookySilent() { return this.spookySilent; }
/*  83 */    private final Mode.Element custom = new Mode.Element(this.modeCollision, "Кастом"); public Mode.Element getCustom() { return this.custom; }
/*  84 */    private final CheckBox onGround = new CheckBox((Bindable)this.ft2, "Работать на земле"); public CheckBox getOnGround() { return this.onGround; }
/*  85 */    private final Mode speedModification = new Mode((Bindable)this.custom, "Режим модификации"); public Mode getSpeedModification() { return this.speedModification; }
/*  86 */    private final Mode.Element target = new Mode.Element(this.speedModification, "Таргет"); public Mode.Element getTarget() { return this.target; }
/*  87 */    private final Mode.Element accelerate = new Mode.Element(this.speedModification, "Ускорение"); public Mode.Element getAccelerate() { return this.accelerate; }
/*  88 */    private final Slider speed2 = (new Slider((Bindable)this.custom, "Скорость")).min(1.0F).max(3.0F).inc(0.05F).set(1.5F); public Slider getSpeed2() { return this.speed2; }
/*  89 */    private final Slider distance = (new Slider((Bindable)this.custom, "Дистанция")).min(0.5F).max(2.0F).inc(0.1F).set(0.5F); public Slider getDistance() { return this.distance; }
/*  90 */    private final CheckBox check = (new CheckBox((Bindable)this.custom, "Проверять на кулдаун")).desc("Если вы получаете урон, или у вас кулдаун руки, то вы не ускоряетесь"); public CheckBox getCheck() { return this.check; }
/*     */   
/*  92 */   private final Mode.Element hw = new Mode.Element(this.mode, "HolyWorld"); public Mode.Element getHw() { return this.hw; }
/*  93 */    private final Mode hwMode = new Mode((Bindable)this.hw, "Режим"); public Mode getHwMode() { return this.hwMode; }
/*  94 */    private final Mode.Element hwFast = new Mode.Element(this.hwMode, "Быстрый"); public Mode.Element getHwFast() { return this.hwFast; }
/*  95 */    private final Mode.Element hwSlow = new Mode.Element(this.hwMode, "Пассивный медленный"); public Mode.Element getHwSlow() { return this.hwSlow; }
/*  96 */    private final Slider delay = (new Slider((Bindable)this.hwSlow, "Задержка")).min(10.0F).max(150.0F).inc(2.0F).set(50.0F); public Slider getDelay() { return this.delay; }
/*  97 */    private final Slider speed1 = (new Slider((Bindable)this.hwSlow, "Скорость")).min(1.0F).max(1.2F).inc(0.01F).set(1.05F); public Slider getSpeed1() { return this.speed1; }
/*  98 */    private final Mode.Element st = new Mode.Element(this.mode, "Spooky"); public Mode.Element getSt() { return this.st; }
/*  99 */    private final Mode.Element stTimer = new Mode.Element(this.mode, "Spooky Timer"); public Mode.Element getStTimer() { return this.stTimer; }
/* 100 */    private final Mode.Element slow = new Mode.Element(this.mode, "TimerSlow"); public Mode.Element getSlow() { return this.slow; }
/* 101 */    private final Mode.Element timer = new Mode.Element(this.mode, "TimerFast"); public Mode.Element getTimer() { return this.timer; }
/* 102 */    private final Mode.Element matrixFlag = new Mode.Element(this.mode, "Matrix flag"); public Mode.Element getMatrixFlag() { return this.matrixFlag; }
/* 103 */    private final Mode.Element longHop = new Mode.Element(this.mode, "LongHop"); public Mode.Element getLongHop() { return this.longHop; }
/* 104 */    private final Mode.Element meta = new Mode.Element(this.mode, "MetaHvH/AnACI"); public Mode.Element getMeta() { return this.meta; }
/* 105 */    private final Mode.Element elytra = new Mode.Element(this.mode, "Элитры"); public Mode.Element getElytra() { return this.elytra; }
/*     */   
/* 107 */   private BlockPos pos; private BlockState state; private final Slider speed = (new Slider((Bindable)this.ft, "Скорость")).min(1.0F).max(15.0F).inc(1.0F).set(5.0F).hide(() -> Boolean.valueOf(!this.mode.is(this.ft))); private BlockPos startBlock; public Slider getSpeed() { return this.speed; }
/*     */   
/* 109 */   public BlockPos getPos() { return this.pos; } public BlockState getState() {
/* 110 */     return this.state;
/*     */   } public BlockPos getStartBlock() {
/* 112 */     return this.startBlock;
/* 113 */   } private final TimerUtility timerUtility = new TimerUtility(); public TimerUtility getTimerUtility() { return this.timerUtility; }
/* 114 */    private final TimerUtility timerUtil = new TimerUtility(); private boolean boosting; private int jumps; public TimerUtility getTimerUtil() { return this.timerUtil; } public boolean isBoosting() {
/* 115 */     return this.boosting;
/*     */   } public int getJumps() {
/* 117 */     return this.jumps;
/*     */   }
/*     */   
/*     */   public void onEvent(Event event) {
/* 121 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && this.stTimer.get()) {
/* 122 */       if (this.timerUtil.passed(1100L)) {
/* 123 */         this.boosting = true;
/*     */       }
/* 125 */       if (this.timerUtil.passed(7000L)) {
/* 126 */         this.boosting = false;
/* 127 */         this.timerUtil.reset();
/*     */       } 
/* 129 */       if (this.boosting) {
/* 130 */         if (mc.player.isOnGround() && !mc.gameSettings.keyBindJump.isPressed()) {
/* 131 */           mc.player.jump();
/*     */         }
/* 133 */         mc.timer.timerSpeed = (mc.player.ticksExisted % 2 == 0) ? 1.5F : 1.2F;
/*     */       } else {
/* 135 */         mc.timer.timerSpeed = 0.05F;
/*     */       } 
/*     */     } 
/*     */     
/* 139 */     if (this.ft.get() && 
/* 140 */       event instanceof EventMotion && 
/* 141 */       Player.getBlock(0.0D, -0.05000000074505806D, 0.0D) != Blocks.AIR) {
/* 142 */       (mc.player.getMotion()).x *= (1.0F + 0.01F * this.speed.get());
/* 143 */       (mc.player.getMotion()).z *= (1.0F + 0.01F * this.speed.get());
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 148 */     if (this.longHop.get() && 
/* 149 */       mc.player.fallDistance >= 0.04F && Move.isMoving()) {
/* 150 */       float f = mc.player.rotationYawHead;
/* 151 */       float f2 = mc.player.rotationPitch;
/* 152 */       double d3 = -Math.sin(f / 180.0D * Math.PI) * Math.cos(f2 / 180.0D * Math.PI);
/* 153 */       double d2 = Math.cos(f / 180.0D * Math.PI) * Math.cos(f2 / 180.0D * Math.PI);
/* 154 */       mc.player.setVelocity(d3, -0.6D, d2);
/*     */     } 
/*     */ 
/*     */     
/* 158 */     if (this.airtick.get()) {
/* 159 */       if (event instanceof fun.rockstarity.api.events.list.player.EventJump) {
/* 160 */         this.jumps++;
/*     */         
/* 162 */         if (this.jumps >= 32) {
/* 163 */           rock.getAlertHandler().alert("Отключаю спиды чтобы избежать флага", AlertType.ERROR);
/* 164 */           set(false);
/* 165 */           onDisable();
/*     */         } 
/*     */       } 
/* 168 */       if (event instanceof EventMotion) {
/* 169 */         for (int i = 0; i < 10; i++) {
/* 170 */           mc.player.connection.sendPacket((IPacket)new CEntityActionPacket((Entity)mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
/*     */         }
/* 172 */         mc.player.connection.sendPacket((IPacket)new CEntityActionPacket((Entity)mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
/*     */       } 
/* 174 */       if (event instanceof EventMotion && ((
/* 175 */         (mc.player.getMotion()).y > 0.0D && (mc.player.getMotion()).y < 0.10000000149011612D) || (mc.player.fallDistance > 0.3F && mc.player.fallDistance < 0.4F))) {
/*     */ 
/*     */         
/* 178 */         (mc.player.getMotion()).x *= 1.0700000524520874D;
/* 179 */         (mc.player.getMotion()).z *= 1.0700000524520874D;
/*     */       } 
/* 181 */       if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/* 182 */         if (e.getPacket() instanceof SPlayerPositionLookPacket) {
/* 183 */           rock.getAlertHandler().alert("Обнаружен флаг. Выключаю спиды" + (rock.isDebugging() ? (" " + this.jumps) : ""), AlertType.ERROR);
/* 184 */           set(false);
/* 185 */           onDisable();
/*     */         }  }
/*     */     
/*     */     } 
/*     */     
/* 190 */     if (this.mode.is(this.meta) && 
/* 191 */       event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 192 */       ItemStack offHandItem = mc.player.getHeldItemOffhand();
/* 193 */       EffectInstance speedEffect = mc.player.getActivePotionEffect(Effects.SPEED);
/* 194 */       EffectInstance DeEffect = mc.player.getActivePotionEffect(Effects.SLOWNESS);
/* 195 */       float appliedSpeed = getAppliedSpeed(offHandItem, speedEffect, DeEffect);
/*     */       
/* 197 */       Move.setSpeed(appliedSpeed);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 202 */     if (this.mode.is(this.st) && 
/* 203 */       event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 204 */       if (!Bypass.via() || Inventory.findItemNoChanges(44, Items.ICE) == -1) {
/* 205 */         rock.getAlertHandler().alert("Для использования этого режима необходим лёд в хотбаре и версия 1.17.1", AlertType.ERROR);
/* 206 */         set(false);
/*     */         
/*     */         return;
/*     */       } 
/* 210 */       BlockPos pos = mc.player.getPosition().add(0, -1, 0);
/* 211 */       if (mc.world.isAirBlock(pos) || mc.world.getBlockState(pos).getBlock().canSpawnInBlock() || (!mc.world.isAirBlock(pos.up()) && mc.world.getBlockState(pos.up()).getBlock().canSpawnInBlock()) || !(mc.getGameSettings()).keyBindJump.isKeyDown() || !mc.player.isOnGround()) {
/*     */         return;
/*     */       }
/* 214 */       int i = Inventory.findItemNoChanges(44, Items.ICE);
/* 215 */       boolean inHotbar = (i <= 8);
/* 216 */       if (i != -1 && inHotbar) {
/* 217 */         Bypass.send(mc.player.rotationYaw, 90.0F);
/*     */         
/* 219 */         if (inHotbar) {
/* 220 */           mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(i));
/* 221 */           mc.player.inventory.currentItem = i;
/*     */         } else {
/* 223 */           mc.playerController.pickItem(i);
/*     */         } 
/*     */         
/* 226 */         this.pos = pos;
/* 227 */         this.state = mc.world.getBlockState(pos);
/*     */ 
/*     */         
/* 230 */         mc.player.connection.sendPacket((IPacket)new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
/* 231 */         mc.player.connection.sendPacket((IPacket)new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
/* 232 */         mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, new BlockRayTraceResult(pos.down().getVec().add(0.5D, 1.0D, 0.5D), Direction.UP, pos.down(), true)));
/* 233 */         mc.world.setBlockState(pos, Blocks.ICE.getDefaultState());
/*     */ 
/*     */         
/* 236 */         if (!inHotbar) {
/* 237 */           mc.playerController.pickItem(i);
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 245 */         rock.getAlertHandler().alert("Этот режим работает только с блоками льда в хотбаре", AlertType.INFO);
/* 246 */         set(false);
/* 247 */         onDisable();
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 257 */     if (this.mode.is(this.hw)) {
/* 258 */       if (this.hwMode.is(this.hwFast)) {
/* 259 */         if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 260 */           BlockPos pos = mc.player.getPosition().add(0, -1, 0);
/* 261 */           if (mc.world.isAirBlock(pos) || mc.world.getBlockState(pos).getBlock().canSpawnInBlock() || (!mc.world.isAirBlock(pos.up()) && mc.world.getBlockState(pos.up()).getBlock().canSpawnInBlock()) || !(mc.getGameSettings()).keyBindJump.isKeyDown() || !mc.player.isOnGround()) {
/*     */             return;
/*     */           }
/* 264 */           int i = Inventory.findItemNoChanges(44, Items.ICE);
/* 265 */           boolean inHotbar = (i <= 8);
/* 266 */           if (i != -1 && inHotbar) {
/* 267 */             if (Bypass.via()) {
/* 268 */               Bypass.send(mc.player.rotationYaw, 90.0F);
/*     */             }
/*     */             
/* 271 */             mc.player.inventory.currentItem = i;
/*     */             
/* 273 */             this.pos = pos;
/* 274 */             this.state = mc.world.getBlockState(pos);
/*     */ 
/*     */             
/* 277 */             mc.player.connection.sendPacket((IPacket)new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
/* 278 */             mc.player.connection.sendPacket((IPacket)new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
/* 279 */             mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, new BlockRayTraceResult(pos.down().getVec().add(0.5D, 1.0D, 0.5D), Direction.UP, pos.down(), true)));
/* 280 */             mc.world.setBlockState(pos, Blocks.ICE.getDefaultState());
/*     */           
/*     */           }
/*     */           else {
/*     */ 
/*     */             
/* 286 */             rock.getAlertHandler().alert("Этот режим работает только с блоками льда в хотбаре", AlertType.INFO);
/* 287 */             set(false);
/* 288 */             onDisable();
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 296 */         if (event instanceof EventMotion) { EventMotion e = (EventMotion)event; if (!Bypass.via()) {
/* 297 */             e.setPitch(90.0F);
/*     */           } }
/*     */       
/* 300 */       } else if (event instanceof EventMotion && 
/* 301 */         this.timerUtility.passed(this.delay.get()) && mc.player.isOnGround() && !mc.gameSettings.keyBindJump.isKeyDown()) {
/* 302 */         (mc.player.getMotion()).x *= this.speed1.get();
/* 303 */         (mc.player.getMotion()).z *= this.speed1.get();
/* 304 */         this.timerUtility.reset();
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 310 */     if (this.mode.is(this.elytra)) {
/* 311 */       if (event instanceof EventMotion) { EventMotion e = (EventMotion)event;
/* 312 */         if (!mc.player.isElytraFlying() && 
/* 313 */           mc.player.isOnGround()) {
/* 314 */           mc.player.startFallFlying();
/* 315 */           mc.player.connection.sendPacket((IPacket)new CEntityActionPacket((Entity)mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
/* 316 */           (mc.player.getMotion()).y = -0.36D;
/*     */         }  }
/*     */ 
/*     */ 
/*     */       
/* 321 */       Player.look(event, mc.player.rotationYaw, (float)(this.pol = Interpolator.LINEAR.interpolate(this.pol, 90.0D, 0.699999988079071D)), true);
/*     */     } 
/*     */     
/* 324 */     if (this.mode.is(this.ft2) && 
/* 325 */       event instanceof EventMotion) {
/* 326 */       if (this.modeCollision.is(this.oldCollision)) {
/* 327 */         for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity entity = objectIterator.next();
/* 328 */           if (!(entity instanceof LivingEntity))
/*     */             continue; 
/* 330 */           double speed = Math.hypot(Math.abs(entity.prevPosX - entity.getPosX()), Math.abs(entity.prevPosZ - entity.getPosZ()));
/*     */           
/* 332 */           if (Server.isFT()) {
/* 333 */             if (mc.player.getDistance(entity) < 1.5F && speed < 0.10000000149011612D) {
/* 334 */               float p = mc.world.getBlockState(mc.player.getPosition().add((mc.player.getMotion()).x, (mc.player.getMotion()).y, (mc.player.getMotion()).z)).getBlock().getSlipperiness();
/* 335 */               float f = mc.player.isOnGround() ? (p * 0.21F) : 0.61F;
/* 336 */               float f2 = mc.player.isOnGround() ? p : 0.81F;
/* 337 */               mc.player.setVelocity(mc.player.getMotion().getX() / f * f2, mc.player.getMotion().getY(), mc.player.getMotion().getZ() / f * f2); break;
/*     */             } 
/*     */             continue;
/*     */           } 
/* 341 */           if (mc.player.getDistance(entity) < 1.5F && speed < 0.10000000149011612D) {
/* 342 */             float p = mc.world.getBlockState(mc.player.getPosition().add((mc.player.getMotion()).x, (mc.player.getMotion()).y, (mc.player.getMotion()).z)).getBlock().getSlipperiness();
/* 343 */             float f = mc.player.isOnGround() ? (p * 0.91F) : 0.81F;
/* 344 */             float f2 = mc.player.isOnGround() ? p : 0.99F;
/* 345 */             mc.player.setVelocity(mc.player.getMotion().getX() / f * f2, mc.player.getMotion().getY(), mc.player.getMotion().getZ() / f * f2);
/*     */             
/*     */             break;
/*     */           }  }
/*     */       
/* 350 */       } else if (this.modeCollision.is(this.newCollision)) {
/* 351 */         if (canBoostFromEntity()) mc.player.jumpMovementFactor *= 2.5F; 
/* 352 */       } else if (this.modeCollision.is(this.custom)) {
/* 353 */         if (canBoostFromEntityCustom()) {
/* 354 */           if (this.speedModification.is(this.target)) {
/* 355 */             mc.player.jumpMovementFactor += mc.player.jumpMovementFactor * this.speed2.get();
/*     */             
/* 357 */             if (Server.isServerForHPFix()) mc.player.jumpMovementFactor -= mc.player.jumpMovementFactor * 0.5F;
/*     */             
/* 359 */             mc.player.jumpMovementFactor = (float)MathHelper.clamp(mc.player.jumpMovementFactor, 0.026D, mc.player.jumpMovementFactor);
/*     */           } else {
/*     */             
/* 362 */             float value = (this.speed2.get() == 1.0F) ? 0.02F : (this.speed2.get() / (Server.isServerForHPFix() ? 20 : 10));
/* 363 */             mc.player.setMotion(mc.player.getMotion().mul((1.0F + value), 1.0D, (1.0F + value)));
/*     */           }
/*     */         
/*     */         }
/* 367 */       } else if (canBoostFromEntity()) {
/* 368 */         float value = 0.105F;
/* 369 */         mc.player.jumpMovementFactor *= 1.05F;
/* 370 */         mc.player.setMotion(mc.player.getMotion().mul((1.0F + value), 1.0D, (1.0F + value)));
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 460 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate)
/*     */     {
/* 462 */       if (this.mode.is(this.timer)) {
/* 463 */         if (this.timers.passed(1150L)) {
/* 464 */           if (mc.player.isOnGround() && !(mc.getGameSettings()).keyBindJump.isPressed()) {
/* 465 */             mc.player.jump();
/*     */           }
/* 467 */           mc.timer.timerSpeed = (mc.player.ticksExisted % 2 == 0) ? 1.5F : 1.2F;
/* 468 */         } else if (this.timers.passed(7000L)) {
/* 469 */           mc.timer.timerSpeed = 0.05F;
/* 470 */           this.timers.reset();
/*     */         } 
/* 472 */       } else if (this.mode.is(this.slow)) {
/*     */         
/* 474 */         (mc.getGameSettings()).keyBindJump.setPressed(false);
/*     */         
/* 476 */         if (mc.player.isOnGround()) mc.player.jump();
/*     */         
/* 478 */         if (mc.player.fallDistance < 0.01D) {
/* 479 */           mc.timer.timerSpeed = 0.1F;
/*     */         } else {
/* 481 */           mc.timer.timerSpeed = 3.0F;
/*     */         } 
/* 483 */       } else if (this.mode.is(this.matrixFlag)) {
/* 484 */         if ((mc.player.getMotion()).y != -0.0784000015258789D) {
/* 485 */           this.timers.reset();
/*     */         }
/* 487 */         if (this.timers.passed(100L)) {
/* 488 */           (mc.player.getMotion()).y = 0.4229D;
/* 489 */           Move.setSpeed(1.953D);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 494 */     if (this.mode.is(this.vanila) && 
/* 495 */       event instanceof fun.rockstarity.api.events.list.player.EventUpdate && 
/* 496 */       Move.isMoving()) {
/* 497 */       if (mc.player.isOnGround()) {
/* 498 */         mc.gameSettings.keyBindJump.setPressed(false);
/* 499 */         mc.timer.reset();
/* 500 */         mc.player.jump();
/*     */       } 
/*     */       
/* 503 */       if ((mc.player.getMotion()).y > 0.003D) {
/* 504 */         (mc.player.getMotion()).x *= 1.0011D;
/* 505 */         (mc.player.getMotion()).z *= 1.0011D;
/* 506 */         mc.timer.timerSpeed = 1.03F;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 512 */     if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/*     */       
/* 514 */       IPacket packet = e.getPacket();
/* 515 */       if (packet instanceof SPlayerPositionLookPacket) { SPlayerPositionLookPacket tpPacket = (SPlayerPositionLookPacket)packet; if (this.mode.is(this.matrixFlag)) {
/* 516 */           mc.player.setPosition(tpPacket.getX(), tpPacket.getY(), tpPacket.getZ());
/* 517 */           mc.player.connection.sendPacket((IPacket)new CConfirmTeleportPacket(tpPacket.getTeleportId()));
/* 518 */           (mc.player.getMotion()).y = 0.4229D;
/* 519 */           Move.setSpeed(1.953D);
/* 520 */           event.cancel();
/*     */         }  }
/*     */        }
/* 523 */      if (this.mode.is(this.matrixFlag) && 
/* 524 */       event instanceof fun.rockstarity.api.events.list.game.EventWorldChange) {
/* 525 */       toggle();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static float getAppliedSpeed(ItemStack offHandItem, EffectInstance speedEffect, EffectInstance DeEffect) {
/* 531 */     String itemName = offHandItem.getDisplayName().getString();
/* 532 */     float appliedSpeed = 0.0F;
/* 533 */     if (speedEffect != null)
/* 534 */     { if (speedEffect.getAmplifier() == 2)
/* 535 */       { appliedSpeed = 0.3927F;
/* 536 */         if (itemName.contains("Ломтик Дыни")) {
/* 537 */           if (speedEffect.getAmplifier() == 2) { appliedSpeed = 0.41755F; }
/* 538 */           else { appliedSpeed = 0.217126F; }
/*     */ 
/*     */         
/*     */         } }
/* 542 */       else if (speedEffect.getAmplifier() == 1) { appliedSpeed = 0.35F; }
/*     */        }
/* 544 */     else { appliedSpeed = 0.23120001F; }
/* 545 */      if (DeEffect != null) appliedSpeed *= 0.835F; 
/* 546 */     if (!mc.player.isOnGround()) appliedSpeed *= 1.435F;
/*     */     
/* 548 */     return appliedSpeed;
/*     */   }
/*     */   
/*     */   private boolean canBoostFromEntity() {
/* 552 */     AxisAlignedBB aabb = mc.player.getBoundingBox().grow(0.5D);
/* 553 */     int armorstans = mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb).size();
/* 554 */     boolean canBoost = (armorstans > 1 || mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb).size() > 1);
/*     */     
/* 556 */     AxisAlignedBB aabb1 = mc.player.getBoundingBox().grow(-0.25D);
/* 557 */     int armorstans1 = mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb1).size();
/* 558 */     boolean canBoost1 = (armorstans1 > 1 || mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb1).size() > 1);
/*     */     
/* 560 */     return (canBoost && !canBoost1 && (mc.player.swingProgress < 0.3D || mc.player.hurtTime < 5) && (this.onGround
/* 561 */       .get() || !mc.player.isOnGround()) && 
/* 562 */       !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.isOnLadder() && 
/* 563 */       Move.isMoving() && (mc.player.fallDistance <= 1.0F || mc.player.fallDistance >= 1.14D));
/*     */   }
/*     */   
/*     */   private boolean canBoostFromEntityCustom() {
/* 567 */     AxisAlignedBB aabb = mc.player.getBoundingBox().grow(this.distance.get() - 0.5D);
/* 568 */     int armorstans = mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb).size();
/* 569 */     boolean canBoost = (armorstans > 1 || mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb).size() > 1);
/*     */     
/* 571 */     AxisAlignedBB aabb1 = mc.player.getBoundingBox().grow(-0.25D);
/* 572 */     int armorstans1 = mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb1).size();
/* 573 */     boolean canBoost1 = (armorstans1 > 1 || mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb1).size() > 1);
/*     */     
/* 575 */     return (canBoost && !canBoost1 && (mc.player.swingProgress < 0.3D || mc.player.hurtTime < 5) && (this.onGround
/* 576 */       .get() || !mc.player.isOnGround()) && 
/* 577 */       !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.isOnLadder() && 
/* 578 */       Move.isMoving() && (mc.player.fallDistance <= 1.0F || mc.player.fallDistance >= 1.14D));
/*     */   }
/*     */ 
/*     */   
/*     */   @NativeInclude
/*     */   public void onEnable() {
/* 584 */     this.pol = Move.getSpeed();
/* 585 */     this.startBlock = mc.player.getPosition();
/*     */ 
/*     */     
/* 588 */     this.jumps = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NativeInclude
/*     */   public void onDisable() {
/* 598 */     mc.timer.reset();
/* 599 */     this.pol = Move.getSpeed();
/* 600 */     this.wasTimer = true;
/* 601 */     this.alreadyUsed = false;
/* 602 */     (mc.getGameSettings()).keyBindJump.setPressed(false);
/*     */   }
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\Speed.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */