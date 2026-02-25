/*     */ package fun.rockstarity.client.modules.combat;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.inputs.EventInput;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*     */ import fun.rockstarity.api.helpers.game.Chat;
/*     */ import fun.rockstarity.api.helpers.math.MathUtility;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import fun.rockstarity.api.render.ui.alerts.AlertType;
/*     */ import net.minecraft.client.settings.KeyBinding;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.item.ArmorItem;
/*     */ import net.minecraft.item.ArmorMaterial;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CEntityActionPacket;
/*     */ import net.minecraft.network.play.server.SEntityVelocityPacket;
/*     */ import net.minecraft.util.DamageSource;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.vector.Vector3d;
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
/*     */ @Info(name = "Velocity", desc = "Убирает откидывание", type = Category.COMBAT)
/*     */ public class Velocity
/*     */   extends Module
/*     */ {
/*  42 */   private final Mode mode = (new Mode((Bindable)this, "Режим")).desc("Позволяет замедлить/отклонить отбрасывание после удара по вам.");
/*     */   
/*  44 */   private final Mode.Element cancel = new Mode.Element(this.mode, "Обычный");
/*  45 */   private final Mode.Element compensation = new Mode.Element(this.mode, "Компенсация");
/*  46 */   private final Mode.Element intave = new Mode.Element(this.mode, "Intave");
/*  47 */   private final Mode.Element universalGrim = new Mode.Element(this.mode, "Grim");
/*     */   
/*  49 */   private final Mode grimMode = new Mode((Bindable)this.universalGrim, "Режим");
/*  50 */   private final Mode.Element old = new Mode.Element(this.grimMode, "Old");
/*  51 */   private final Mode.Element latest = new Mode.Element(this.grimMode, "Latest");
/*     */   
/*  53 */   private final Slider countTo = (new Slider((Bindable)this.intave, "Сколько замедлять?")).min(1.0F).max(6.0F).inc(1.0F).set(4.0F)
/*  54 */     .desc("Выбирает, сколько отбрасываний можно замедлить");
/*  55 */   private final Slider countPost = (new Slider((Bindable)this.intave, "Сколько пропускать?")).min(2.0F).max(10.0F).inc(1.0F).set(4.0F)
/*  56 */     .desc("Выбирает, сколько отбрасываний нужно пропустить");
/*  57 */   private final CheckBox packet = new CheckBox((Bindable)this.intave, "Пакетно замедлять");
/*  58 */   private final Slider slow = (new Slider((Bindable)this.packet, "Сила замедления (в -%)")).min(0.01F).max(0.1F).inc(0.001F).set(0.03F);
/*  59 */   private final CheckBox targetFromJump = (new CheckBox((Bindable)this.intave, "Ускориться")).desc("Позволит ускориться после получения урона на земле, и соответственно втаргетится в противника");
/*     */   
/*  61 */   private final CheckBox logging = (new CheckBox((Bindable)this.intave, "Логировать")).desc("Позволяет логировать все отталкивания");
/*  62 */   private final Mode logMode = new Mode((Bindable)this.logging, "Кого логировать?");
/*  63 */   private final Mode.Element packetLog = new Mode.Element(this.logMode, "Пакетное замедление");
/*  64 */   private final Mode.Element defLog = new Mode.Element(this.logMode, "Обычное замедление");
/*  65 */   private final Slider speedable = (new Slider((Bindable)this.targetFromJump, "Сила")).min(0.1F).max(2.0F).inc(0.1F).set(0.5F);
/*     */   
/*  67 */   private final CheckBox offPostFlagged = new CheckBox((Bindable)this, "Вырубить после флага");
/*  68 */   private final Mode compMode = (new Mode((Bindable)this, "Тип компенсации")).hide(() -> Boolean.valueOf(!this.mode.is(this.compensation)));
/*  69 */   private final Mode.Element defaultComp = new Mode.Element(this.compMode, "Обычная");
/*  70 */   private final CheckBox jumper = new CheckBox((Bindable)this.defaultComp, "Прыгать на земле");
/*  71 */   private final Mode.Element damageAngle = new Mode.Element(this.compMode, "По направлению урона");
/*  72 */   private final CheckBox onlyPlayer = (new CheckBox((Bindable)this, "Только от игроков")).hide(() -> Boolean.valueOf((!this.mode.is(this.compensation) && !this.compMode.is(this.damageAngle))));
/*  73 */   private final CheckBox noNether = (new CheckBox((Bindable)this, "Не работать если незерка")).hide(() -> Boolean.valueOf((!this.mode.is(this.compensation) && !this.compMode.is(this.damageAngle))));
/*     */   
/*  75 */   private Vector3d lastMotion = Vector3d.ZERO;
/*     */   private boolean packetReceived;
/*  77 */   private final TimerUtility flagTimer = new TimerUtility();
/*  78 */   private int logic = 0;
/*     */   private boolean gotVelo;
/*     */   private boolean prev;
/*  81 */   float count = 0.0F;
/*  82 */   float countLog = 0.0F;
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  86 */     if (this.noNether.get() && isNether()) {
/*     */       return;
/*     */     }
/*     */     
/*  90 */     if (this.mode.is(this.cancel)) {
/*  91 */       if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/*  92 */         IPacket<?> packet = e.getPacket();
/*  93 */         if (packet instanceof SEntityVelocityPacket) { SEntityVelocityPacket velocityPacket = (SEntityVelocityPacket)packet;
/*  94 */           if (velocityPacket.getEntityID() == mc.player.getEntityId()) {
/*  95 */             event.cancel();
/*     */           } }
/*     */          }
/*     */     
/*  99 */     } else if (this.mode.is(this.compensation) && this.compMode.is(this.damageAngle)) {
/* 100 */       if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/* 101 */         IPacket iPacket = e.getPacket(); if (iPacket instanceof SEntityVelocityPacket) { SEntityVelocityPacket packet = (SEntityVelocityPacket)iPacket;
/* 102 */           if (packet.getEntityID() == mc.player.getEntityId() && (
/* 103 */             !this.onlyPlayer.get() || isDamagePlayer())) {
/* 104 */             this.lastMotion = new Vector3d(packet.motionX, packet.motionY, packet.motionZ);
/* 105 */             applyVelocityAction();
/*     */           }
/*     */            }
/* 108 */         else if (e.getPacket() instanceof net.minecraft.network.play.server.SExplosionPacket && (
/* 109 */           !this.onlyPlayer.get() || isDamagePlayer()))
/* 110 */         { applyVelocityAction(); }
/*     */          }
/*     */ 
/*     */ 
/*     */       
/* 115 */       if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) transform();
/*     */     
/*     */     } 
/* 118 */     if (event instanceof EventInput) { EventInput e = (EventInput)event; if (this.mode.is(this.compensation) && (!this.onlyPlayer.get() || isDamagePlayer())) {
/* 119 */         if (this.compMode.is(this.damageAngle)) {
/* 120 */           if (this.prev) {
/* 121 */             float direction = MathHelper.wrapDegrees(mc.player.rotationYaw - (MathUtility.calculate(mc.player.getPositionVec().add(this.lastMotion))).x);
/* 122 */             if (mc.player.isOnGround()) e.setJump(true); 
/* 123 */             if (direction > 120.0F || direction < -120.0F) e.setForward(1.0F); 
/* 124 */             if (direction > -150.0F && direction < -60.0F) e.setStrafe(1.0F); 
/* 125 */             if (direction > -60.0F && direction < 60.0F) e.setForward(-1.0F); 
/* 126 */             if (direction > 60.0F && direction < 150.0F) e.setStrafe(-1.0F); 
/*     */           } 
/*     */         } else {
/* 129 */           if (mc.player.hurtTime >= 9) this.count++; 
/* 130 */           if (this.count > 4.0F || (mc.player.isOnGround() && !mc.gameSettings.keyBindJump.isKeyDown())) this.count = 0.0F;
/*     */           
/* 132 */           if (canCancelVelocity())
/* 133 */           { float forwardFunc = 1.0F;
/*     */             
/* 135 */             if (!mc.player.isOnGround()) e.setForward(forwardFunc); 
/* 136 */             if (MathUtility.isBlockUnder(0.01F) && mc.player.isOnGround()) e.setForward(1.0F);
/*     */             
/* 138 */             if (mc.player.isOnGround()) {
/* 139 */               if (mc.player.hurtTime >= 9 && !mc.gameSettings.keyBindJump.isKeyDown()) {
/* 140 */                 if (this.jumper.get()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getDefault(), true); 
/* 141 */                 e.setForward(forwardFunc);
/*     */               } 
/*     */               
/* 144 */               if (mc.player.hurtTime == 8) e.setSneak(true);
/*     */             
/*     */             } 
/* 147 */             if (mc.player.hurtTime < 7) e.setSneak(false);
/*     */              }
/* 149 */           else if (!mc.gameSettings.keyBindSneak.isKeyDown()) { e.setSneak(false); }
/*     */         
/*     */         } 
/*     */         
/* 153 */         if (mc.gameSettings.keyBindSneak.isKeyDown()) {
/* 154 */           KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getDefault(), true);
/*     */         }
/*     */       }  }
/*     */     
/* 158 */     if (this.mode.is(this.universalGrim) && canCancelVelocity() && 
/* 159 */       event instanceof EventInput) { EventInput e = (EventInput)event;
/* 160 */       if (!mc.player.isOnGround()) e.setForward(1.0F); 
/* 161 */       if (MathUtility.isBlockUnder(0.01F) && mc.player.isOnGround()) e.setForward(1.0F);
/*     */       
/* 163 */       if (((mc.player.getMotion()).y > 0.0D && (mc.player.getMotion()).y < 0.10000000149011612D) || (mc.player.fallDistance > 0.3F && mc.player.fallDistance < 0.4F)) {
/*     */ 
/*     */         
/* 166 */         float value = this.grimMode.is(this.old) ? 0.8F : (isNether() ? 0.87F : 0.96F);
/* 167 */         if (!this.grimMode.is(this.latest) || mc.player.ticksExisted % 3 == 0) {
/* 168 */           (mc.player.getMotion()).x *= value;
/* 169 */           (mc.player.getMotion()).z *= value;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 174 */       float factorValue = this.grimMode.is(this.old) ? (0.8F + mc.player.hurtTime / 40.0F) : (isNether() ? (0.85F + mc.player.hurtTime / 75.0F) : 1.0F);
/* 175 */       mc.player.jumpMovementFactor *= MathHelper.clamp(factorValue, 0.8F, 1.0F);
/*     */       
/* 177 */       if (mc.player.isOnGround()) {
/* 178 */         KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getDefault(), true);
/* 179 */         e.setSneak(true);
/* 180 */         if (mc.player.hurtTime < 7) e.setSneak(false);
/*     */       
/*     */       }  }
/*     */ 
/*     */     
/* 185 */     if (this.mode.is(this.intave) && canCancelVelocity() && 
/* 186 */       event instanceof EventInput) { EventInput e = (EventInput)event;
/* 187 */       if (mc.player.hurtTime == 9) this.count++; 
/* 188 */       if (this.count >= 0.0F && this.count <= this.countTo.get()) {
/* 189 */         if (!mc.player.isOnGround()) e.setForward(1.0F); 
/* 190 */         if (MathUtility.isBlockUnder(0.01F) && mc.player.isOnGround()) e.setForward(1.0F);
/*     */         
/* 192 */         if (this.packet.get()) {
/* 193 */           for (int i = 0; i < 10; i++) {
/* 194 */             mc.player.connection.sendPacket((IPacket)new CEntityActionPacket((Entity)mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
/*     */           }
/* 196 */           mc.player.connection.sendPacket((IPacket)new CEntityActionPacket((Entity)mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
/*     */           
/* 198 */           (mc.player.getMotion()).x *= (1.0F - this.slow.get());
/* 199 */           (mc.player.getMotion()).z *= (1.0F - this.slow.get());
/*     */           
/* 201 */           if (this.count <= this.countTo.get() && canCancelVelocity() && mc.player.ticksExisted % 8 == 0 && this.logging.get() && this.logMode.is(this.packetLog)) {
/* 202 */             Chat.msg("" + this.count + " velocity count packet slowed");
/*     */           }
/*     */         } 
/* 205 */         if (((mc.player.getMotion()).y > 0.0D && (mc.player.getMotion()).y < 0.10000000149011612D) || (mc.player.fallDistance > 0.3F && mc.player.fallDistance < 0.4F)) {
/*     */           
/* 207 */           (mc.player.getMotion()).x *= 0.8299999833106995D;
/* 208 */           (mc.player.getMotion()).z *= 0.8299999833106995D;
/*     */         } 
/*     */         
/* 211 */         if (mc.player.isOnGround()) {
/* 212 */           KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getDefault(), true);
/* 213 */           if (mc.player.hurtTime < 7) e.setSneak(false);
/*     */         
/*     */         } 
/* 216 */         if (this.targetFromJump.get() && mc.gameSettings.keyBindJump.isKeyDown()) {
/* 217 */           mc.player.jumpMovementFactor *= 1.0F + this.speedable.get();
/*     */         }
/*     */       } 
/*     */       
/* 221 */       if (this.logging.get()) {
/* 222 */         if (this.count > this.countTo.get() && this.count < this.countTo.get() + 2.0F) this.count = this.countTo.get() + 2.0F; 
/* 223 */         if (this.count > this.countTo.get() + this.countPost.get()) this.count = 0.0F;
/*     */         
/* 225 */         if (this.count <= this.countTo.get() && canCancelVelocity() && mc.player.ticksExisted % 8 == 0 && this.logging.get() && this.logMode.is(this.defLog)) {
/* 226 */           Chat.msg("" + this.count + " velocity count slowed");
/*     */         }
/* 228 */         if (this.count >= this.countTo.get() + 2.0F && mc.player.ticksExisted % 8 == 0 && this.logging.get()) {
/* 229 */           Chat.msg("number " + this.count + " velocity skipped");
/*     */         }
/*     */       }  }
/*     */ 
/*     */     
/* 234 */     if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event; if (this.offPostFlagged.get()) {
/* 235 */         if (mc.player.hurtTime == 9) this.countLog++;
/*     */         
/* 237 */         if (e.getPacket() instanceof net.minecraft.network.play.server.SPlayerPositionLookPacket) {
/* 238 */           rock.getAlertHandler().alert("Обнаружен флаг. Выключаю велосити" + (rock.isDebugging() ? (", last velocity registered: " + (int)this.countLog) : ""), AlertType.ERROR);
/* 239 */           set(false);
/* 240 */           onDisable();
/*     */         } 
/*     */       }  }
/*     */   
/*     */   }
/*     */   @NativeInclude
/*     */   public void transform() {
/* 247 */     if (mc.player.hurtTime > 0 || this.gotVelo) {
/* 248 */       this.prev = true;
/*     */     }
/* 250 */     if (mc.player.hurtTime == 0) {
/* 251 */       this.gotVelo = false;
/* 252 */       if (this.prev) {
/* 253 */         this.prev = false;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   @NativeInclude
/*     */   private void applyVelocityAction() {
/* 260 */     mc.player.jump();
/* 261 */     this.prev = true;
/* 262 */     this.gotVelo = true;
/*     */   }
/*     */   
/*     */   private boolean isDamagePlayer() {
/* 266 */     DamageSource damageSource = mc.player.getLastDamageSource();
/*     */     
/* 268 */     if (damageSource != null) {
/* 269 */       return damageSource.getTrueSource() instanceof net.minecraft.entity.player.PlayerEntity;
/*     */     }
/*     */     
/* 272 */     return false;
/*     */   }
/*     */   
/*     */   private boolean isNether() {
/* 276 */     for (ItemStack armor : mc.player.getArmorInventoryList()) {
/* 277 */       if (armor != null && armor.getItem() instanceof ArmorItem) {
/* 278 */         ArmorItem armorItem = (ArmorItem)armor.getItem();
/* 279 */         if (armorItem.getArmorMaterial() == ArmorMaterial.NETHERITE) {
/* 280 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 284 */     return false;
/*     */   }
/*     */   
/*     */   private boolean canCancelVelocity() {
/* 288 */     return (mc.player.hurtTime > 0 && !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.isElytraFlying());
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 293 */     this.countLog = 0.0F;
/*     */   }
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\Velocity.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */