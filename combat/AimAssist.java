/*     */ package fun.rockstarity.client.modules.combat;
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.player.EventJump;
/*     */ import fun.rockstarity.api.events.list.player.EventMotion;
/*     */ import fun.rockstarity.api.events.list.player.EventMove;
/*     */ import fun.rockstarity.api.events.list.player.EventTrace;
/*     */ import fun.rockstarity.api.helpers.math.MathUtility;
/*     */ import fun.rockstarity.api.helpers.math.aura.AuraUtility;
/*     */ import fun.rockstarity.api.helpers.math.aura.Rotation;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Select;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import fun.rockstarity.api.render.animation.Animation;
/*     */ import fun.rockstarity.api.render.color.themes.Style;
/*     */ import fun.rockstarity.client.modules.other.Globals;
/*     */ import javafx.animation.Interpolator;
/*     */ import net.minecraft.client.Minecraft;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.LivingEntity;
/*     */ import net.minecraft.util.math.vector.Vector2f;
/*     */ import net.minecraft.util.math.vector.Vector3d;
/*     */ 
/*     */ @Info(name = "AimAssist", desc = "Помогает в аиме при пвп на ближнем оружии", type = Category.COMBAT)
/*     */ public class AimAssist extends Module {
/*     */   private final Select targets;
/*     */   private final Select.Element players;
/*     */   private final Select.Element invisibles;
/*     */   private final Select.Element naked;
/*     */   private final Select.Element bots;
/*     */   private final Select.Element mobs;
/*     */   private final Select.Element rockUser;
/*     */   private final CheckBox saveTarget;
/*     */   private final Slider fov;
/*     */   
/*     */   public AimAssist() {
/*  38 */     this.targets = (new Select((Bindable)this, "Цели")).desc("Цели на которых будет наводиться игрок");
/*     */     
/*  40 */     this.players = (new Select.Element(this.targets, "Игроки")).set(true);
/*  41 */     this.invisibles = (new Select.Element(this.targets, "Невидимые")).set(true).hide(() -> Boolean.valueOf(!this.players.get()));
/*  42 */     this.naked = (new Select.Element(this.targets, "Голые")).set(true).hide(() -> Boolean.valueOf(!this.players.get()));
/*  43 */     this.bots = (new Select.Element(this.targets, "Боты")).hide(() -> Boolean.valueOf(!this.players.get()));
/*  44 */     this.mobs = new Select.Element(this.targets, "Мобы");
/*  45 */     this.rockUser = (new Select.Element(this.targets, "Пользователи " + String.valueOf(ClientInfo.NAME))).hide(() -> Boolean.valueOf(!((Globals)rock.getModules().get(Globals.class)).get()));
/*  46 */     this.saveTarget = (new CheckBox((Bindable)this, "Сохранять цель")).set(true);
/*     */     
/*  48 */     this.fov = (new Slider((Bindable)this, "Поле зрения")).min(1.0F).max(180.0F).set(90.0F).inc(1.0F).desc("Поле зрения на которой будет работать наводка");
/*  49 */     this.fovCircle = (new CheckBox((Bindable)this, "Круг FOV'a")).hide(() -> Boolean.valueOf((this.fov.get() > 90.0F))).desc("Рендерит круг поле зрения");
/*  50 */     this.silent = (new CheckBox((Bindable)this, "Свободный аим")).desc("Делает вашу камеру свободной");
/*  51 */     Objects.requireNonNull(this.silent); this.lock = (new CheckBox((Bindable)this, "Фиксировать")).hide(this.silent::get).desc("Фиксирует камеру на противнике");
/*  52 */     this.rayAim = (new CheckBox((Bindable)this, "Только доводка")).desc("Не наводит прицел на игрока, если уже навелся");
/*     */     
/*  54 */     this.verticalAim = (new CheckBox((Bindable)this, "Наводка по вертикали")).desc("Включает или отключает вертикальную наводку").set(true);
/*     */     
/*  56 */     this.speed = (new Slider((Bindable)this, "Скорость")).min(1.0F).max(10.0F).inc(0.1F).set(2.0F);
/*     */ 
/*     */     
/*  59 */     this.target = null;
/*  60 */     this.anim = (new Animation()).setSpeed(170).setSize(1.0F).setEasing(Easing.EASE_IN_OUT_QUART);
/*     */   } private final CheckBox fovCircle; private final CheckBox silent; private final CheckBox lock; private final CheckBox rayAim; private final CheckBox verticalAim; private final Slider speed; private float yaw; private float pitch; private LivingEntity target; Animation anim; public LivingEntity getTarget() {
/*     */     return this.target;
/*     */   } public void onEvent(Event event) {
/*  64 */     if (event instanceof fun.rockstarity.api.events.list.game.EventTick) {
/*     */       
/*  66 */       LivingEntity target = AuraUtility.calculateTarget(mc.player.getPositionVec(), 4.0D, this.players.get(), this.mobs.get(), this.invisibles.get(), this.naked.get(), this.bots.get(), false, this.rockUser.get(), true, false, false, false, false);
/*  67 */       this.anim.setForward(!MathUtility.rayTraceWithBlock(4.0D, this.yaw, this.pitch, (Entity)mc.player, (Entity)target, false));
/*     */       
/*  69 */       if (((target == null || this.target == null || !mc.world.getAllEntities().contains(this.target) || this.target
/*  70 */         .isDead()) && !this.saveTarget.get()) || (this.target == null && this.saveTarget.get())) {
/*  71 */         this.target = target;
/*     */       }
/*     */       
/*  74 */       if (target == null || Player.getFOV((Entity)target) > this.fov.get()) {
/*  75 */         this.yaw = mc.player.rotationYaw;
/*  76 */         this.pitch = mc.player.rotationPitch;
/*     */         
/*     */         return;
/*     */       } 
/*  80 */       if (!this.silent.get() && !this.lock.get()) {
/*  81 */         this.yaw = mc.player.rotationYaw;
/*  82 */         this.pitch = mc.player.rotationPitch;
/*     */       } 
/*     */       
/*  85 */       Vector3d pos = VectorUtility.getBestVector(target, 0.0F);
/*     */       
/*  87 */       float shortestYawPath = (float)(((Math.toDegrees(Math.atan2(pos.z, pos.x)) - 90.0D - this.yaw) % 360.0D + 540.0D) % 360.0D - 180.0D);
/*  88 */       float targetYaw = this.yaw + shortestYawPath;
/*  89 */       float targetPitch = (float)-Math.toDegrees(Math.atan2(pos.y, Math.hypot(pos.x, pos.z)));
/*     */       
/*  91 */       this.yaw = (AuraUtility.fixDeltaNonVanillaMouse(this.yaw, this.pitch)).x;
/*  92 */       this.pitch = (AuraUtility.fixDeltaNonVanillaMouse(this.yaw, this.pitch)).y;
/*     */       
/*  94 */       this.yaw = (float)Interpolator.LINEAR.interpolate(this.yaw, targetYaw, (getAIRotationSpeed() * (this.rayAim.get() ? this.anim.get() : 1.0F)));
/*  95 */       this.pitch = !this.verticalAim.get() ? mc.player.rotationPitch : (float)Interpolator.LINEAR.interpolate(this.pitch, targetPitch, (
/*  96 */           MathUtility.random(0.04D, 0.05D) / Math.max(Minecraft.debugFPS, 5.0F) * 75.0F * (this.rayAim.get() ? this.anim.get() : 1.0F)));
/*     */       
/*  98 */       Vector2f correctedRotation = Rotation.correctRotation(this.yaw, this.pitch);
/*  99 */       this.yaw = correctedRotation.x;
/* 100 */       this.pitch = correctedRotation.y;
/*     */       
/* 102 */       if (!this.silent.get()) {
/* 103 */         mc.player.rotationYaw = this.yaw;
/* 104 */         mc.player.rotationPitch = this.pitch;
/*     */       } 
/*     */     } 
/*     */     
/* 108 */     Style color = Style.current;
/*     */     
/* 110 */     if (event instanceof fun.rockstarity.api.events.list.render.EventRender2D && this.fovCircle.get() && this.fov.get() <= 90.0F) {
/* 111 */       Style.getCurrent(); Render.drawCircle(sr.getScaledWidth() / 2.0F, sr.getScaledHeight() / 2.0F, 0.0F, 360.0F, this.fov.get() * 2.0F, 1.0F, false, Style.getMain().getRGB());
/*     */     } 
/*     */     
/* 114 */     if (this.silent.get()) {
/* 115 */       if (event instanceof EventTrace) { EventTrace eventTrace = (EventTrace)event;
/* 116 */         eventTrace.setYaw(this.yaw);
/* 117 */         eventTrace.setPitch(this.pitch);
/* 118 */         eventTrace.cancel(); }
/*     */ 
/*     */       
/* 121 */       if (event instanceof EventMotion) { EventMotion eventMotion = (EventMotion)event;
/* 122 */         eventMotion.setYaw(this.yaw);
/* 123 */         eventMotion.setPitch(this.pitch);
/* 124 */         mc.player.rotationYawHead = this.yaw;
/* 125 */         mc.player.rotationPitchHead = this.pitch;
/* 126 */         mc.player.renderYawOffset = AuraUtility.calculateCorrectYawOffset(this.yaw); }
/*     */ 
/*     */ 
/*     */       
/* 130 */       if (event instanceof EventMove) { EventMove eventMoveFix = (EventMove)event;
/* 131 */         eventMoveFix.setYaw(this.yaw);
/* 132 */         eventMoveFix.setPitch(this.pitch); }
/*     */ 
/*     */       
/* 135 */       if (event instanceof EventJump) { EventJump eventJump = (EventJump)event;
/* 136 */         eventJump.setYaw(this.yaw); }
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   private float getAIRotationSpeed() {
/* 142 */     return this.speed.get() / MathUtility.random(28.0D, 35.0D) / Math.max(Minecraft.debugFPS, 5.0F) * 75.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 147 */     this.target = null;
/*     */   }
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\AimAssist.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */