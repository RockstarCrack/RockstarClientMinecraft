/*    */ package fun.rockstarity.client.modules.player;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.player.EventMotion;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.Select;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ import net.minecraft.item.Items;
/*    */ import net.minecraft.network.IPacket;
/*    */ import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
/*    */ import net.minecraft.util.Hand;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "NoDelay", desc = "Убирает задежку между кликами и прыжками", type = Category.PLAYER)
/*    */ public class NoDelay
/*    */   extends Module
/*    */ {
/* 27 */   private final Select utils = new Select((Bindable)this, "Выбор");
/*    */   
/* 29 */   private final Select.Element jumps = (new Select.Element(this.utils, "Прыжок")).set(true);
/* 30 */   private final Select.Element rightClicks = new Select.Element(this.utils, "ПКМ"); public Select.Element getRightClicks() { return this.rightClicks; }
/* 31 */    private final Select.Element leftClicks = new Select.Element(this.utils, "ЛКМ");
/* 32 */   private final Select.Element hit = new Select.Element(this.utils, "Ломание");
/* 33 */   private final Select.Element fastEXP = new Select.Element(this.utils, "Бутыльки опыта");
/*    */   
/* 35 */   private final Slider delay = (new Slider((Bindable)this, "Задержка ПКМ")).min(0.0F).max(4.0F).inc(1.0F).set(0.0F).hide(() -> Boolean.valueOf(!this.rightClicks.get())); public Slider getDelay() { return this.delay; }
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 39 */     if (event instanceof EventMotion) { EventMotion e = (EventMotion)event;
/* 40 */       if (this.jumps.get()) mc.player.setJumpTicks(0); 
/* 41 */       if (this.leftClicks.get() && (mc.getGameSettings()).keyBindAttack.isKeyDown()) mc.clickMouse(); 
/* 42 */       if (this.hit.get()) mc.playerController.setBlockHitDelay(0);
/*    */        }
/*    */     
/* 45 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && 
/* 46 */       (mc.getGameSettings()).keyBindUseItem.isKeyDown() && this.fastEXP.get() && mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE)
/* 47 */       for (int i = 0; i < 8; i++) {
/* 48 */         mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
/* 49 */         mc.player.swingArm(Hand.MAIN_HAND);
/*    */       }  
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\NoDelay.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */