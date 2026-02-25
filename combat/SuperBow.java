/*    */ package fun.rockstarity.client.modules.combat;
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.game.packet.EventSendPacket;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.item.Items;
/*    */ import net.minecraft.network.IPacket;
/*    */ import net.minecraft.network.play.client.CEntityActionPacket;
/*    */ import net.minecraft.network.play.client.CPlayerDiggingPacket;
/*    */ import net.minecraft.network.play.client.CPlayerPacket;
/*    */ 
/*    */ @Info(name = "SuperBow", desc = "Усиливает силу лука", type = Category.COMBAT)
/*    */ public class SuperBow extends Module {
/* 18 */   private final Slider pow = (new Slider((Bindable)this, "Сила")).min(10.0F).max(50.0F).inc(1.0F).set(10.0F);
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 22 */     if (event instanceof EventSendPacket) { EventSendPacket e = (EventSendPacket)event; IPacket iPacket = e.getPacket(); if (iPacket instanceof CPlayerDiggingPacket) { CPlayerDiggingPacket packet = (CPlayerDiggingPacket)iPacket;
/* 23 */         if (packet.getAction() == CPlayerDiggingPacket.Action.RELEASE_USE_ITEM && mc.player.getActiveItemStack().getItem() == Items.BOW) {
/* 24 */           mc.player.connection.sendPacket((IPacket)new CEntityActionPacket((Entity)mc.player, CEntityActionPacket.Action.START_SPRINTING));
/*    */           
/* 26 */           for (int i = 0; i < this.pow.get(); i++) {
/* 27 */             posRot(mc.player.getPosY() + 1.0E-10D, false);
/* 28 */             posRot(mc.player.getPosY() - 1.0E-10D, true);
/*    */           } 
/*    */         }  }
/*    */        }
/*    */   
/*    */   }
/*    */   protected void posRot(double y, boolean ground) {
/* 35 */     mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(mc.player.getPosX(), y, mc.player.getPosZ(), mc.player.rotationYaw, mc.player.rotationPitch, ground));
/*    */   }
/*    */   
/*    */   public void onEnable() {}
/*    */   
/*    */   public void onDisable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\SuperBow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */