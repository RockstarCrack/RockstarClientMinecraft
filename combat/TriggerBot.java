/*     */ package fun.rockstarity.client.modules.combat;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.helpers.game.Server;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.math.aura.AuraUtility;
/*     */ import fun.rockstarity.api.helpers.math.aura.BotUtility;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Select;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.LivingEntity;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.item.UseAction;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CEntityActionPacket;
/*     */ import net.minecraft.network.play.client.CPlayerDiggingPacket;
/*     */ import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
/*     */ import net.minecraft.util.Direction;
/*     */ import net.minecraft.util.Hand;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.EntityRayTraceResult;
/*     */ import net.minecraft.util.math.RayTraceResult;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "TriggerBot", desc = "При наводке на энтити, автоматически бьет ее", type = Category.COMBAT, module = {"TapeMouse", "AutoClicker"})
/*     */ public class TriggerBot
/*     */   extends Module
/*     */ {
/*  39 */   private final Mode mode = new Mode((Bindable)this, "Режим");
/*  40 */   private final Mode.Element defaults = new Mode.Element(this.mode, "Обычный");
/*  41 */   private final Mode.Element hides = new Mode.Element(this.mode, "Скрытный");
/*     */   
/*  43 */   private final CheckBox crit = (new CheckBox((Bindable)this, "Только криты")).set(true)
/*  44 */     .desc("TriggerBot будет бить только тогда, когда может нанести критический удар").hide(() -> Boolean.valueOf(!this.mode.is(this.defaults)));
/*  45 */   private final CheckBox breaks = (new CheckBox((Bindable)this, "Ломать щит")).desc("Ломает щит противнику").hide(() -> Boolean.valueOf(!this.mode.is(this.defaults)));
/*     */   
/*  47 */   private final Select targets = (new Select((Bindable)this, "Цели")).desc("Сущности, которых будет бить TriggerBot").hide(() -> Boolean.valueOf(!this.mode.is(this.defaults)));
/*     */   
/*  49 */   private final Select.Element players = (new Select.Element(this.targets, "Игроки")).set(true);
/*  50 */   private final Select.Element invisibles = (new Select.Element(this.targets, "Невидимые")).set(true)
/*  51 */     .hide(() -> Boolean.valueOf(!this.players.get()));
/*  52 */   private final Select.Element bots = (new Select.Element(this.targets, "Боты")).hide(() -> Boolean.valueOf(!this.players.get()));
/*  53 */   private final Select.Element mobs = new Select.Element(this.targets, "Мобы");
/*  54 */   private final Select.Element friend = new Select.Element(this.targets, "Друзья");
/*     */   
/*  56 */   private final Slider delay = (new Slider((Bindable)this, "Задержка")).min(100.0F).max(5000.0F).inc(100.0F).set(500.0F).hide(() -> Boolean.valueOf(!this.mode.is(this.hides)));
/*     */   
/*  58 */   private final TimerUtility timer = new TimerUtility();
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  62 */     if (event instanceof fun.rockstarity.api.events.list.player.EventMotion && this.mode.is(this.defaults)) {
/*     */       
/*  64 */       RayTraceResult traceResult = mc.objectMouseOver;
/*     */       
/*  66 */       if (traceResult == null || traceResult.getType() != RayTraceResult.Type.ENTITY || 
/*  67 */         (mc.getGameSettings()).keyBindUseItem.isKeyDown()) {
/*     */         return;
/*     */       }
/*     */       
/*  71 */       Entity entity = ((EntityRayTraceResult)traceResult).getEntity();
/*     */       
/*  73 */       if (!isValid(entity))
/*     */         return; 
/*  75 */       if ((this.crit.get() && mc.player.fallDistance < 0.1D) || !this.timer.passed(470L)) {
/*     */         return;
/*     */       }
/*  78 */       if (mc.player.getCooledAttackStrength(0.5F) >= (this.crit.get() ? 0.95F : 1.0F)) {
/*     */         
/*  80 */         if (mc.player.isSprinting() || (mc.player.serverSprintState && Server.is("spooky"))) {
/*  81 */           mc.player.connection.sendPacket((IPacket)new CEntityActionPacket((Entity)mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
/*  82 */           mc.player.serverSprintState = false;
/*     */         } 
/*     */         
/*  85 */         boolean blocking = (mc.player.isHandActive() && mc.player.getActiveItemStack().getItem().getUseAction(mc.player.getActiveItemStack()) == UseAction.BLOCK);
/*  86 */         if (blocking) {
/*  87 */           mc.player.connection.sendPacket((IPacket)new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
/*     */         }
/*     */         
/*  90 */         mc.playerController.attackEntity((PlayerEntity)mc.player, entity);
/*  91 */         mc.player.swingArm(Hand.MAIN_HAND);
/*  92 */         this.timer.reset();
/*     */         
/*  94 */         if (blocking) {
/*  95 */           mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(mc.player.getActiveHand()));
/*     */         }
/*     */         
/*  98 */         if (this.breaks.get()) {
/*  99 */           AuraUtility.tryBreakShield();
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 104 */     if (this.mode.is(this.hides) && event instanceof fun.rockstarity.api.events.list.player.EventUpdate && this.timer.passed((long)this.delay.get() + 300L)) {
/* 105 */       mc.setLeftClickCounter(0);
/* 106 */       mc.clickMouse();
/* 107 */       this.timer.reset();
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isValid(Entity entity) {
/* 112 */     if (entity == null || entity == mc.player) return false;
/*     */     
/* 114 */     if (entity instanceof PlayerEntity && this.players.get()) {
/* 115 */       if (((LivingEntity)entity).getHealth() <= 0.0F) return false; 
/* 116 */     } else if ((entity instanceof net.minecraft.entity.MobEntity || entity instanceof net.minecraft.entity.passive.AnimalEntity) && this.mobs.get()) {
/* 117 */       if (((LivingEntity)entity).getHealth() <= 0.0F) return false; 
/*     */     } else {
/* 119 */       return false;
/*     */     } 
/*     */     
/* 122 */     if (entity.isInvisible() && !this.invisibles.get()) return false;
/*     */     
/* 124 */     if (rock.getFriendsHandler().isFriend(entity) && !this.friend.get()) return false;
/*     */     
/* 126 */     if (Server.isRW() && !this.bots.get() && BotUtility.isRWBot((LivingEntity)entity)) return false;
/*     */     
/* 128 */     return true;
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\TriggerBot.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */