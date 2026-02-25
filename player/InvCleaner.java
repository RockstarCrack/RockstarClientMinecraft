/*    */ package fun.rockstarity.client.modules.player;
/*    */ 
/*    */ import fun.rockstarity.api.autobuy.logic.items.MinecraftItem;
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*    */ import fun.rockstarity.api.helpers.player.Player;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.ItemSelect;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.inventory.container.ClickType;
/*    */ 
/*    */ @Info(name = "InvCleaner", desc = "Выкидывает ненужные предметы", type = Category.PLAYER)
/*    */ public class InvCleaner extends Module {
/* 18 */   ItemSelect itemSelect = new ItemSelect((Bindable)this, "Предметы");
/*    */   
/* 20 */   Slider speed = (new Slider((Bindable)this, "Задержка")).set(100.0F).min(0.0F).max(1000.0F).inc(50.0F);
/*    */   
/* 22 */   TimerUtility timer = new TimerUtility();
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 26 */     if (event instanceof fun.rockstarity.api.events.list.game.EventTick && this.timer.passed(this.speed.get()))
/* 27 */       for (MinecraftItem item : this.itemSelect.getItems()) {
/* 28 */         int slot = Player.findItem(45, item.getItem());
/* 29 */         if (slot != -1) {
/* 30 */           mc.playerController.windowClick(0, slot, 1, ClickType.THROW, (PlayerEntity)mc.player);
/* 31 */           this.timer.reset();
/*    */           break;
/*    */         } 
/*    */       }  
/*    */   }
/*    */   
/*    */   public void onEnable() {}
/*    */   
/*    */   public void onDisable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\InvCleaner.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */