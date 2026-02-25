/*    */ package fun.rockstarity.client.modules.render;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ 
/*    */ @Info(name = "AntiInvisible", desc = "Показывает игроков с невидимостью", type = Category.RENDER)
/*    */ public class AntiInvisible extends Module {
/* 12 */   public Slider getAlpha() { return this.alpha; } private final Slider alpha = (new Slider((Bindable)this, "Прозрачность"))
/* 13 */     .min(0.1F).max(1.0F).inc(0.1F).set(0.3F).desc("Выбрать прозрачность моделек игроков");
/*    */   
/*    */   public void onEvent(Event event) {}
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\render\AntiInvisible.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */