/*    */ package fun.rockstarity.client.modules.other;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.helpers.game.Chat;
/*    */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*    */ import fun.rockstarity.api.helpers.player.Player;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.PremiumModule;
/*    */ import fun.rockstarity.api.modules.settings.list.Input;
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.inventory.container.ClickType;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.item.Items;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "AutoDupe", desc = "Автоматически фармит предметы на BravoHVH", type = Category.OTHER)
/*    */ public class AutoDupe
/*    */   extends PremiumModule
/*    */ {
/* 26 */   private final TimerUtility kitTimer = new TimerUtility();
/* 27 */   private final TimerUtility cleanTimer = new TimerUtility();
/* 28 */   private final Input kit = (new Input((Bindable)this, "Кит")).set("free");
/*    */   
/*    */   private boolean storeKit;
/*    */   
/*    */   public void onEvent(Event event) {
/* 33 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 34 */       if (invEmpty() && this.kitTimer.passed(61000L)) {
/* 35 */         mc.player.sendChatMessage("/kit " + this.kit.get());
/* 36 */         this.kitTimer.reset();
/* 37 */         this.storeKit = true;
/*    */       } 
/*    */       
/* 40 */       if (!invEmpty()) {
/* 41 */         if (haveGApple() && this.storeKit && this.cleanTimer.passed(1000L)) {
/* 42 */           if (mc.currentScreen instanceof net.minecraft.client.gui.screen.inventory.ContainerScreen) {
/* 43 */             int startIndex = mc.player.openContainer.getInventory().size() - 36;
/* 44 */             int endIndex = mc.player.openContainer.getInventory().size() - 1;
/*    */             
/* 46 */             for (int i = startIndex; i <= endIndex; i++) {
/* 47 */               ItemStack itemstack = mc.player.openContainer.getSlot(i).getStack();
/* 48 */               if (itemstack.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
/* 49 */                 Chat.debug(itemstack.getItem());
/*    */                 
/* 51 */                 mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.QUICK_MOVE, (PlayerEntity)mc.player);
/*    */               } 
/*    */             } 
/*    */             
/* 55 */             this.storeKit = false;
/* 56 */             mc.player.closeScreen();
/*    */           } else {
/* 58 */             mc.player.sendChatMessage("/ec");
/*    */           } 
/* 60 */           this.cleanTimer.reset();
/*    */         } 
/*    */         
/* 63 */         if (!haveGApple() && this.cleanTimer.passed(1000L)) {
/* 64 */           mc.player.sendChatMessage("/clear -confirmed");
/* 65 */           this.cleanTimer.reset();
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   private boolean haveGApple() {
/* 72 */     for (int i = 0; i < mc.player.container.getInventory().size(); i++) {
/* 73 */       if (!Player.find(i).isEmpty() && 
/* 74 */         Player.find(i).getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
/* 75 */         return true;
/*    */       }
/*    */     } 
/*    */     
/* 79 */     return false;
/*    */   }
/*    */   
/*    */   private boolean invEmpty() {
/* 83 */     for (int i = 0; i < mc.player.container.getInventory().size(); i++) {
/* 84 */       if (!Player.find(i).isEmpty()) return false;
/*    */     
/*    */     } 
/* 87 */     return true;
/*    */   }
/*    */   
/*    */   public void onEnable() {}
/*    */   
/*    */   public void onDisable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\AutoDupe.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */