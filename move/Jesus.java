/*     */ package fun.rockstarity.client.modules.move;
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.helpers.player.Move;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CEntityActionPacket;
/*     */ import net.minecraft.potion.EffectInstance;
/*     */ import net.minecraft.potion.Effects;
/*     */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*     */ 
/*     */ @Info(name = "Jesus", desc = "Позваоляет ходить по поверхностям жидкостей", type = Category.MOVE)
/*     */ public class Jesus extends Module {
/*  20 */   Mode jesusMode = new Mode((Bindable)this, "Режим");
/*  21 */   Mode.Element def = new Mode.Element(this.jesusMode, "Обычный");
/*  22 */   Mode.Element grimOld = new Mode.Element(this.jesusMode, "Grim Old");
/*  23 */   Slider speed = (new Slider((Bindable)this.grimOld, "Скорость")).min(0.5F).max(1.5F).set(1.0F).inc(0.05F);
/*  24 */   Mode.Element meta = new Mode.Element(this.jesusMode, "MetaHvH/AnACI");
/*     */ 
/*     */   
/*     */   @NativeInclude
/*     */   public void onEvent(Event event) {
/*  29 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/*  30 */       if (this.jesusMode.is(this.def)) {
/*  31 */         if (mc.player.isInWater() || mc.player.isInLava()) {
/*  32 */           (mc.player.getMotion()).y = 0.011D;
/*     */           
/*  34 */           if (Move.isMoving() && 
/*  35 */             Move.getSpeed() < 1.0D) {
/*  36 */             (mc.player.getMotion()).x *= 1.218D;
/*  37 */             (mc.player.getMotion()).z *= 1.218D;
/*     */           }
/*     */         
/*     */         } 
/*  41 */       } else if (this.jesusMode.is(this.meta)) {
/*  42 */         if (mc.player.isInWater() || mc.player.isInLava()) {
/*  43 */           EffectInstance speedEffect = mc.player.getActivePotionEffect(Effects.SPEED);
/*  44 */           EffectInstance DeEffect = mc.player.getActivePotionEffect(Effects.SLOWNESS);
/*  45 */           float appliedSpeed = getAppliedSpeed(speedEffect, DeEffect);
/*     */           
/*  47 */           Move.setSpeed(appliedSpeed);
/*     */ 
/*     */           
/*  50 */           boolean isMoving = (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown());
/*     */           
/*  52 */           if (!isMoving) {
/*  53 */             (mc.player.getMotion()).x = 0.0D;
/*  54 */             (mc.player.getMotion()).z = 0.0D;
/*     */           } 
/*     */           
/*  57 */           (mc.player.getMotion()).y = mc.gameSettings.keyBindJump.isKeyDown() ? 0.019D : 0.003D;
/*     */         } 
/*     */       } else {
/*  60 */         mc.player.setVelocity(
/*  61 */             (mc.player.getMotion()).x * this.speed.get(), 0.01D, 
/*     */             
/*  63 */             (mc.player.getMotion()).z * this.speed.get());
/*     */ 
/*     */         
/*  66 */         mc.player.setVelocity(
/*  67 */             (mc.player.getMotion()).x * this.speed.get(), 0.0D, 
/*     */             
/*  69 */             (mc.player.getMotion()).z * this.speed.get());
/*     */ 
/*     */         
/*  72 */         mc.player.setVelocity(
/*  73 */             (mc.player.getMotion()).x * this.speed.get(), -0.1D, 
/*     */             
/*  75 */             (mc.player.getMotion()).z * this.speed.get());
/*     */ 
/*     */         
/*  78 */         mc.player.setVelocity(
/*  79 */             (mc.player.getMotion()).x * this.speed.get(), 0.035D, 
/*     */             
/*  81 */             (mc.player.getMotion()).z * this.speed.get());
/*     */ 
/*     */         
/*  84 */         mc.player.connection.sendPacket((IPacket)new CEntityActionPacket((Entity)mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
/*  85 */         mc.player.connection.sendPacket((IPacket)new CEntityActionPacket((Entity)mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static float getAppliedSpeed(EffectInstance speedEffect, EffectInstance DeEffect) {
/*  91 */     ItemStack offHandItem = mc.player.getHeldItemOffhand();
/*  92 */     String itemName = offHandItem.getDisplayName().getString();
/*  93 */     float appliedSpeed = 0.0F;
/*     */     
/*  95 */     if (itemName.contains("Ломтик Дыни") && speedEffect != null && speedEffect.getAmplifier() == 2)
/*  96 */     { appliedSpeed = 0.49254498F; }
/*     */     
/*  98 */     else if (speedEffect != null)
/*  99 */     { if (speedEffect.getAmplifier() == 2) { appliedSpeed = 0.506F; }
/* 100 */       else if (speedEffect.getAmplifier() == 1) { appliedSpeed = 0.44F; }  }
/* 101 */     else { appliedSpeed = 0.2992F; }
/*     */ 
/*     */     
/* 104 */     if (DeEffect != null) appliedSpeed *= 0.85F;
/*     */     
/* 106 */     return appliedSpeed;
/*     */   }
/*     */   
/*     */   public void onEnable() {}
/*     */   
/*     */   public void onDisable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\Jesus.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */