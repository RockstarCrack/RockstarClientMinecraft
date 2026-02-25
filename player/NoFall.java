/*    */ package fun.rockstarity.client.modules.player;
/*    */ 
/*    */ import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.player.EventMotion;
/*    */ import fun.rockstarity.api.helpers.game.Server;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.Mode;
/*    */ import fun.rockstarity.api.render.ui.alerts.AlertType;
/*    */ import fun.rockstarity.api.render.ui.alerts.Tooltip;
/*    */ import fun.rockstarity.api.via.ViaLoadingBase;
/*    */ import net.minecraft.network.IPacket;
/*    */ import net.minecraft.network.play.client.CPlayerPacket;
/*    */ import net.minecraft.util.math.vector.Vector3d;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "NoFall", desc = "Убирает урон от падения", type = Category.PLAYER)
/*    */ public class NoFall
/*    */   extends Module
/*    */ {
/* 35 */   Mode mode = new Mode((Bindable)this, "Режим");
/*    */   
/* 37 */   Mode.Element vanilla = new Mode.Element(this.mode, "Обычный");
/* 38 */   Mode.Element grimOld = new Mode.Element(this.mode, "Grim Old");
/* 39 */   Mode.Element funtime = new Mode.Element(this.mode, "FunTime");
/* 40 */   Mode.Element holyworld = new Mode.Element(this.mode, "HolyWorld");
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 44 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && mc.player.ticksExisted > 100)
/*    */     {
/* 46 */       if (Server.isHW() && this.mode.is(this.holyworld) && !ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_17)) {
/* 47 */         rock.getAlertHandler().alert(Tooltip.create("Этот NoFall работает только с VIA 1.17+"), AlertType.INFO);
/*    */       }
/*    */     }
/*    */     
/* 51 */     if (event instanceof EventMotion) { EventMotion e = (EventMotion)event;
/* 52 */       if (this.mode.is(this.vanilla)) {
/* 53 */         e.setGround(true);
/*    */       } }
/*    */ 
/*    */     
/* 57 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 58 */       if (this.mode.is(this.funtime)) {
/* 59 */         if (mc.player.fallDistance > 2.0F) {
/* 60 */           (mc.player.getMotion()).y = 0.005D;
/*    */         }
/* 62 */       } else if (this.mode.is(this.holyworld)) {
/* 63 */         if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_17) && 
/* 64 */           mc.player.fallDistance > 2.5D) {
/* 65 */           Vector3d pos = mc.player.getPositionVec();
/* 66 */           EventMotion eventMotion = (new EventMotion(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.isOnGround())).hook();
/* 67 */           mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y + 1.0E-7D, pos.z, eventMotion.getYaw(), eventMotion.getPitch(), false));
/* 68 */           mc.player.fallDistance = 0.0F;
/*    */         }
/*    */       
/* 71 */       } else if (this.mode.is(this.grimOld) && 
/* 72 */         mc.player.fallDistance > 2.5D) {
/* 73 */         mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionPacket(mc.player
/* 74 */               .getPosX(), mc.player.getPosY(), mc.player.getPosZ(), true));
/* 75 */         (mc.player.getMotion()).y -= 0.91231425D;
/*    */       } 
/*    */     }
/*    */ 
/*    */     
/* 80 */     if (event instanceof fun.rockstarity.api.events.list.game.EventWorldChange && this.mode.is(this.funtime))
/* 81 */       set(false); 
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\NoFall.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */