/*    */ package fun.rockstarity.client.modules.move;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
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
/*    */ @NativeInclude
/*    */ @Info(name = "HighJump", desc = "Высокий прыжок", type = Category.MOVE)
/*    */ public class HighJump
/*    */   extends Module
/*    */ {
/* 25 */   private final Mode mode = new Mode((Bindable)this, "Мод");
/* 26 */   private final Mode.Element matrix = new Mode.Element(this.mode, "Matrix");
/* 27 */   private final Mode.Element infinity = new Mode.Element(this.mode, "Effect");
/*    */   
/* 29 */   private final Slider motion = (new Slider((Bindable)this, "Высота")).min(1.0F).max(80.0F).inc(1.0F).set(6.0F);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 36 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 37 */       if (this.mode.is(this.matrix)) {
/*    */         
/* 39 */         if ((mc.getGameSettings()).keyBindJump.isPressed()) (mc.player.getMotion()).y += this.motion.get(); 
/* 40 */       } else if (this.mode.is(this.infinity)) {
/*    */         
/* 42 */         EffectInstance effect = new EffectInstance(Effects.JUMP_BOOST, 1, (int)this.motion.get());
/* 43 */         effect.setPotionDurationMax(true);
/* 44 */         mc.player.addPotionEffect(effect);
/* 45 */         if (mc.player.isInWater() && (mc.getGameSettings()).keyBindJump.isKeyDown()) mc.player.jump();
/*    */       
/*    */       } 
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onDisable() {
/* 53 */     mc.player.removePotionEffect(Effects.JUMP_BOOST);
/*    */   }
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\HighJump.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */