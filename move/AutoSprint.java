/*    */ package fun.rockstarity.client.modules.move;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.EventType;
/*    */ import fun.rockstarity.api.events.list.player.EventKeepSprint;
/*    */ import fun.rockstarity.api.events.list.player.EventUpdate;
/*    */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*    */ import fun.rockstarity.client.modules.combat.Aura;
/*    */ import fun.rockstarity.client.modules.player.FreeCam;
/*    */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @NativeInclude
/*    */ @Info(name = "AutoSprint", desc = "Автоматически спринтится", type = Category.MOVE)
/*    */ public class AutoSprint
/*    */   extends Module
/*    */ {
/*    */   private boolean canSprint = true;
/*    */   
/*    */   public boolean isCanSprint() {
/* 28 */     return this.canSprint; } public void setCanSprint(boolean canSprint) { this.canSprint = canSprint; }
/*    */ 
/*    */   
/* 31 */   public CheckBox getKeepSprint() { return this.keepSprint; } private final CheckBox keepSprint = (new CheckBox((Bindable)this, "Сохранять спринт"))
/* 32 */     .set(true);
/* 33 */   private final CheckBox hunger = new CheckBox((Bindable)this, "Игнорировать голод"); public CheckBox getHunger() { return this.hunger; }
/*    */   
/* 35 */   TimerUtility timerUtility = new TimerUtility();
/*    */ 
/*    */   
/*    */   @EventType({EventUpdate.class, EventKeepSprint.class})
/*    */   public void onEvent(Event event) {
/* 40 */     if (event instanceof EventUpdate && !((FreeCam)rock.getModules().get(FreeCam.class)).get()) {
/* 41 */       Aura auraNew = (Aura)rock.getModules().get(Aura.class);
/* 42 */       if (mc.player.isSprinting()) this.timerUtility.reset();
/*    */       
/* 44 */       (mc.getGameSettings()).keyBindSprint.setPressed(this.canSprint);
/*    */     } 
/*    */ 
/*    */     
/* 48 */     if (this.keepSprint.get() && event instanceof EventKeepSprint)
/* 49 */       event.cancel(); 
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\AutoSprint.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */