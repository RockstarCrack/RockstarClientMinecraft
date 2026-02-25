/*    */ package fun.rockstarity.client.modules.combat;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*    */ import fun.rockstarity.api.helpers.player.Player;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.inventory.container.ClickType;
/*    */ import net.minecraft.item.Items;
/*    */ import net.minecraft.network.IPacket;
/*    */ import net.minecraft.network.play.client.CCloseWindowPacket;
/*    */ import net.minecraft.network.play.client.CHeldItemChangePacket;
/*    */ import net.minecraft.network.play.client.CPlayerDiggingPacket;
/*    */ import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
/*    */ import net.minecraft.util.Direction;
/*    */ import net.minecraft.util.Hand;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "AutoSoup", desc = "Автоматически ест супы", type = Category.COMBAT)
/*    */ public class AutoSoup
/*    */   extends Module
/*    */ {
/* 33 */   private final TimerUtility eatTimer = new TimerUtility();
/*    */   
/* 35 */   private final Slider health = (new Slider((Bindable)this, "Здоровье")).min(1.0F).max(20.0F).inc(0.5F).set(19.0F).desc("Здоровье при котором будет использоваться суп");
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 39 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && 
/* 40 */       mc.player.getHealth() + mc.player.getAbsorptionAmount() <= this.health.get() && this.eatTimer.passed(1200L)) {
/* 41 */       int i = Player.findItem(46, Items.MUSHROOM_STEW);
/*    */       
/* 43 */       if (i != -1)
/*    */       {
/* 45 */         if (i == 40) {
/*    */           
/* 47 */           mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.OFF_HAND));
/*    */         }
/* 49 */         else if (i >= 36) {
/* 50 */           mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(i - 36));
/*    */           
/* 52 */           mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
/* 53 */           mc.player.connection.sendPacket((IPacket)new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.DROP_ITEM, BlockPos.ZERO, Direction.DOWN));
/*    */           
/* 55 */           mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(mc.player.inventory.currentItem));
/* 56 */           mc.player.connection.sendPacket((IPacket)new CCloseWindowPacket());
/*    */           
/* 58 */           this.eatTimer.reset();
/*    */         } else {
/*    */           
/* 61 */           mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, (PlayerEntity)mc.player);
/* 62 */           mc.playerController.windowClick(0, 44, 0, ClickType.PICKUP, (PlayerEntity)mc.player);
/*    */         } 
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   @NativeInclude
/*    */   private void privet() {}
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\AutoSoup.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */