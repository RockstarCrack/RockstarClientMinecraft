/*    */ package fun.rockstarity.client.modules.move;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*    */ import fun.rockstarity.api.helpers.player.Move;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*    */ import fun.rockstarity.api.modules.settings.list.Mode;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ import net.minecraft.potion.EffectInstance;
/*    */ import net.minecraft.potion.Effects;
/*    */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @NativeInclude
/*    */ @Info(name = "WaterSpeed", desc = "Ускоряет в воде", type = Category.MOVE)
/*    */ public class WaterSpeed
/*    */   extends Module
/*    */ {
/* 37 */   private final Mode mode = new Mode((Bindable)this, "Режим"); public Mode getMode() { return this.mode; }
/*    */ 
/*    */   
/* 40 */   private final Mode.Element defaults = new Mode.Element(this.mode, "Matrix");
/* 41 */   private final Mode.Element grim = new Mode.Element(this.mode, "Grim"); public Mode.Element getGrim() { return this.grim; }
/* 42 */    private final Mode.Element funtime = new Mode.Element(this.mode, "FunTime");
/* 43 */   private final Mode.Element intave = new Mode.Element(this.mode, "Intave");
/*    */   
/* 45 */   private final Slider speed = (new Slider((Bindable)this, "Скорость")).min(0.1F).max(5.0F).inc(0.1F).set(1.0F).hide(() -> Boolean.valueOf(!this.mode.is(this.defaults)));
/*    */   
/* 47 */   private final CheckBox potion = (new CheckBox((Bindable)this, "Работать с зельем")).hide(() -> Boolean.valueOf(!this.mode.is(this.defaults)));
/*    */   
/* 49 */   private final TimerUtility debug = new TimerUtility();
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 53 */     if (event instanceof fun.rockstarity.api.events.list.player.EventMotion) {
/* 54 */       if (this.mode.is(this.defaults)) handleDefaultMode(); 
/* 55 */       if (this.mode.is(this.intave) && mc.player.hurtTime > 0 && mc.player.isInWater()) {
/* 56 */         mc.player.setMotion(mc.player.getMotion().mul(1.02D, 1.0D, 1.02D));
/*    */       }
/*    */       
/* 59 */       if (this.mode.is(this.funtime)) {
/* 60 */         if (mc.player.hurtTime > 0 || !mc.player.isInWater())
/* 61 */           return;  mc.player.setMotion(mc.player.getMotion().mul(1.011D, 1.0D, 1.011D));
/*    */         
/* 63 */         EffectInstance speedEffect = mc.player.getActivePotionEffect(Effects.SPEED);
/* 64 */         if (speedEffect != null) {
/* 65 */           mc.player.setMotion(mc.player.getMotion().mul(1.1270499974489212D, 1.0D, 1.1270499974489212D));
/*    */         }
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   private void handleDefaultMode() {
/* 72 */     if ((!mc.player.isPotionActive(Effects.SPEED) && this.potion.get()) || !mc.player.isInWater())
/* 73 */       return;  Move.setSpeed(this.speed.get());
/* 74 */     (mc.player.getMotion()).y += (mc.player.ticksExisted % 2 == 0) ? -0.001D : 0.01D;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onDisable() {
/* 79 */     this.debug.reset();
/*    */   }
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\WaterSpeed.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */