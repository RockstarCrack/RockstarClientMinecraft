/*    */ package fun.rockstarity.client.modules.combat;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.helpers.math.MathUtility;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.Select;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ import net.minecraft.item.Item;
/*    */ import net.minecraft.item.Items;
/*    */ import net.minecraft.network.IPacket;
/*    */ import net.minecraft.network.play.client.CPlayerDiggingPacket;
/*    */ import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
/*    */ import net.minecraft.util.Hand;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*    */ 
/*    */ @Info(name = "FastBow", desc = "Автоматически спамит луком или арбалетом", type = Category.COMBAT)
/*    */ public class FastBow
/*    */   extends Module {
/* 23 */   private final Select targets = (new Select((Bindable)this, "Предмет")).desc("Предметы на которые будет действовать модуль");
/*    */   
/* 25 */   private final Select.Element bow = (new Select.Element(this.targets, "Лук")).set(true);
/* 26 */   private final Select.Element crossbow = (new Select.Element(this.targets, "Арбалет")).set(true);
/*    */   
/* 28 */   private final Slider range = (new Slider((Bindable)this, "Задержка")).min(4.0F).max(30.0F).inc(1.0F).set(4.0F).desc("Задержка выстрела");
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 32 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 33 */       if (this.bow.get()) {
/* 34 */         useItem(Items.BOW);
/*    */       }
/* 36 */       if (this.crossbow.get()) {
/* 37 */         useItem(Items.CROSSBOW);
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   private void useItem(Item items) {
/* 43 */     int ticks = (int)(this.range.get() + MathUtility.random(-2.0D, 2.0D));
/* 44 */     if (mc.player.getActiveItemStack().getItem() == items && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= ticks) {
/* 45 */       mc.player.connection.sendPacket((IPacket)new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, mc.player.getHorizontalFacing()));
/* 46 */       mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
/* 47 */       mc.player.stopActiveHand();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   @NativeInclude
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\FastBow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */