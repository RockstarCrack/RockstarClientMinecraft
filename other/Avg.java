/*    */ package fun.rockstarity.client.modules.other;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.helpers.game.Chat;
/*    */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import net.minecraft.client.Minecraft;
/*    */ 
/*    */ @Info(name = "Avg", desc = "Средний FPS", type = Category.OTHER)
/*    */ public class Avg
/*    */   extends Module {
/* 19 */   private final CheckBox auto = (new CheckBox((Bindable)this, "Авто выключение")).set(true);
/*    */   
/* 21 */   private final TimerUtility timer = new TimerUtility();
/* 22 */   private final TimerUtility timerOff = new TimerUtility();
/* 23 */   private final List<Integer> fpsValues = new ArrayList<>();
/* 24 */   private int minFPS = Integer.MAX_VALUE;
/* 25 */   private int maxFPS = Integer.MIN_VALUE;
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 29 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 30 */       int currentFPS = Minecraft.debugFPS;
/* 31 */       this.fpsValues.add(Integer.valueOf(currentFPS));
/*    */       
/* 33 */       if (currentFPS < this.minFPS) {
/* 34 */         this.minFPS = currentFPS;
/*    */       }
/* 36 */       if (currentFPS > this.maxFPS) {
/* 37 */         this.maxFPS = currentFPS;
/*    */       }
/*    */       
/* 40 */       if (this.timer.passed(1200L)) {
/* 41 */         int avgFPS = calculateAverageFPS();
/*    */         
/* 43 */         this.timer.reset();
/*    */       } 
/*    */       
/* 46 */       if (this.auto.get() && 
/* 47 */         this.timerOff.passed(5000L)) {
/* 48 */         onDisable();
/* 49 */         set(false);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   private int calculateAverageFPS() {
/* 56 */     if (this.fpsValues.isEmpty()) {
/* 57 */       return 0;
/*    */     }
/* 59 */     int sum = 0;
/* 60 */     for (Iterator<Integer> iterator = this.fpsValues.iterator(); iterator.hasNext(); ) { int fps = ((Integer)iterator.next()).intValue();
/* 61 */       sum += fps; }
/*    */     
/* 63 */     return sum / this.fpsValues.size();
/*    */   }
/*    */ 
/*    */   
/*    */   public void onEnable() {
/* 68 */     this.timerOff.reset();
/* 69 */     this.fpsValues.clear();
/* 70 */     this.minFPS = Integer.MAX_VALUE;
/* 71 */     this.maxFPS = Integer.MIN_VALUE;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onDisable() {
/* 76 */     if (!this.fpsValues.isEmpty()) {
/* 77 */       int avgFPS = calculateAverageFPS();
/* 78 */       Chat.msg("Финальная статистика FPS: Средний: " + avgFPS + " | Минимальный: " + this.minFPS + " | Максимальный: " + this.maxFPS);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\Avg.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */