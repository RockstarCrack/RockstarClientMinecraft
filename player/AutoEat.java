/*    */ package fun.rockstarity.client.modules.player;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.helpers.player.Player;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ import fun.rockstarity.client.modules.combat.Aura;
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.inventory.container.ClickType;
/*    */ import net.minecraft.item.UseAction;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "AutoEat", desc = "Автоматически ест еду из инвентаря", type = Category.PLAYER)
/*    */ public class AutoEat
/*    */   extends Module
/*    */ {
/* 25 */   private final Slider feedLevel = (new Slider((Bindable)this, "Голод")).min(1.0F).max(19.0F).set(15.0F).inc(0.5F);
/* 26 */   private final CheckBox swap = new CheckBox((Bindable)this, "Брать в руку");
/* 27 */   private final CheckBox auraNo = (new CheckBox((Bindable)this, "Не использовать с Aura")).desc("Не будет использоваться если есть таргет в Aura");
/*    */   
/*    */   private boolean isUse;
/*    */   
/*    */   public void onEvent(Event event) {
/* 32 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && shouldEat()) {
/* 33 */       Aura aura = (Aura)rock.getModules().get(Aura.class);
/* 34 */       if (aura.get() && aura.getTarget() != null && this.auraNo.get())
/* 35 */         return;  (mc.getGameSettings()).keyBindUseItem.setPressed(this.isUse);
/*    */       
/* 37 */       if (mc.player.getFoodStats().getFoodLevel() < this.feedLevel.get()) {
/* 38 */         handleEating();
/* 39 */         this.isUse = true;
/*    */       } else {
/* 41 */         this.isUse = mc.player.getFoodStats().needFood();
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   private boolean shouldEat() {
/* 47 */     return (mc.player.getHeldItemOffhand().getUseAction() == UseAction.EAT || mc.player.getHeldItemMainhand().getUseAction() == UseAction.EAT || this.swap.get());
/*    */   }
/*    */   
/*    */   private void handleEating() {
/* 51 */     int hotbarSlot = Player.findEatInHotbar();
/* 52 */     if (hotbarSlot != -1) {
/* 53 */       mc.player.inventory.currentItem = hotbarSlot;
/*    */     } else {
/* 55 */       int inventorySlot = Player.findEatInInventory();
/* 56 */       if (inventorySlot != -1) {
/* 57 */         swapItemToOffhand(inventorySlot);
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   private void swapItemToOffhand(int inventorySlot) {
/* 63 */     mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, (PlayerEntity)mc.player);
/* 64 */     mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (PlayerEntity)mc.player);
/* 65 */     if (!(mc.player.getHeldItemOffhand().getItem() instanceof net.minecraft.item.AirItem))
/* 66 */       mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, (PlayerEntity)mc.player); 
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\AutoEat.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */