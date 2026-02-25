/*    */ package fun.rockstarity.client.modules.move;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.game.packet.EventSendPacket;
/*    */ import fun.rockstarity.api.helpers.player.Player;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.Select;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ import net.minecraft.block.Blocks;
/*    */ import net.minecraft.network.IPacket;
/*    */ import net.minecraft.network.play.client.CConfirmTeleportPacket;
/*    */ import net.minecraft.network.play.server.SPlayerPositionLookPacket;
/*    */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "WaterLeave", desc = "Высоко прыгает в воде", type = Category.MOVE)
/*    */ public class WaterLeave
/*    */   extends Module
/*    */ {
/* 26 */   private final Select util = new Select((Bindable)this, "Выбор");
/* 27 */   private final Select.Element hightJump = (new Select.Element(this.util, "Высокий прыжок")).set(true);
/* 28 */   private final Select.Element lily = new Select.Element(this.util, "Работать с кувшинкой");
/* 29 */   private final Slider motion = (new Slider((Bindable)this, "Высота")).min(1.0F).max(10.0F).inc(0.5F).set(1.0F)
/* 30 */     .hide(() -> Boolean.valueOf(!this.hightJump.get()));
/*    */ 
/*    */   
/*    */   @NativeInclude
/*    */   public void onEvent(Event event) {
/* 35 */     if (event instanceof EventSendPacket) { EventSendPacket e = (EventSendPacket)event; if (!this.lily.get()) { IPacket iPacket = e.getPacket(); if (iPacket instanceof SPlayerPositionLookPacket) { SPlayerPositionLookPacket p = (SPlayerPositionLookPacket)iPacket;
/* 36 */           mc.player.setPosition(p.getX(), p.getY(), p.getZ());
/* 37 */           mc.player.connection.sendPacket((IPacket)new CConfirmTeleportPacket(p.getTeleportId()));
/* 38 */           e.cancel(); }
/*    */          }
/*    */        }
/*    */ 
/*    */     
/* 43 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 44 */       if (this.lily.get() && Player.getBlock() == Blocks.LILY_PAD && mc.player.ticksExisted % 7 == 1 && mc.player.isCrouching()) {
/* 45 */         (mc.player.getMotion()).y = 0.20000000298023224D;
/*    */       }
/*    */       
/* 48 */       if (this.hightJump.get() && mc.player.isInWater() && mc.player.fallDistance > 0.0F)
/* 49 */         (mc.player.getMotion()).y = this.motion.get(); 
/*    */     } 
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\WaterLeave.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */