/*    */ package fun.rockstarity.client.modules.player;
/*    */ 
/*    */ import com.mojang.blaze3d.matrix.MatrixStack;
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.game.packet.EventSendPacket;
/*    */ import fun.rockstarity.api.events.list.render.world.EventRenderWorldEntities;
/*    */ import fun.rockstarity.api.helpers.game.Server;
/*    */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*    */ import fun.rockstarity.api.helpers.render.Render;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ import fun.rockstarity.api.render.ui.alerts.AlertType;
/*    */ import fun.rockstarity.api.render.ui.alerts.Tooltip;
/*    */ import java.util.ArrayList;
/*    */ import net.minecraft.client.settings.PointOfView;
/*    */ import net.minecraft.entity.LivingEntity;
/*    */ import net.minecraft.network.IPacket;
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
/*    */ @Info(name = "Blink", desc = "Замораживает вас для сервера", type = Category.PLAYER)
/*    */ public class Blink
/*    */   extends Module
/*    */ {
/* 36 */   private final ArrayList<IPacket<?>> packets = new ArrayList<>();
/* 37 */   private final TimerUtility timer = new TimerUtility();
/*    */   
/* 39 */   private final CheckBox pulse = new CheckBox((Bindable)this, "Пульсация");
/* 40 */   private final Slider pulseTimer = (new Slider((Bindable)this, "Время")).min(1.0F).max(20.0F).set(5.0F).inc(1.0F).hide(() -> Boolean.valueOf(!this.pulse.get()));
/* 41 */   private final CheckBox entity = new CheckBox((Bindable)this, "Отображение");
/* 42 */   private final CheckBox svo = (new CheckBox((Bindable)this, "Не показывать от первого")).hide(() -> Boolean.valueOf(!this.entity.get()));
/*    */   
/*    */   private MatrixStack matrixStack;
/*    */   private Vector3d lastPos;
/*    */   
/*    */   public void onEvent(Event event) {
/* 48 */     if (event instanceof EventSendPacket) { EventSendPacket e = (EventSendPacket)event;
/*    */       
/* 50 */       this.packets.add(e.getPacket());
/* 51 */       event.cancel();
/*    */       
/* 53 */       if (this.timer.passed((long)this.pulseTimer.get() * 110L) && this.pulse.get()) {
/* 54 */         onDisable();
/* 55 */         onEnable();
/* 56 */         this.timer.reset();
/*    */       }  }
/*    */ 
/*    */     
/* 60 */     if (event instanceof EventRenderWorldEntities) { EventRenderWorldEntities e = (EventRenderWorldEntities)event; if (this.entity.get() && this.lastPos != null && (mc.getGameSettings().getPointOfView() != PointOfView.FIRST_PERSON || !this.svo.get())) {
/* 61 */         Render.drawEntity3D(e.getMatrix(), (LivingEntity)mc.player, this.lastPos, 1.0F);
/*    */       } }
/*    */   
/*    */   }
/*    */   
/*    */   public void onDisable() {
/* 67 */     if (!mc.isSingleplayer()) {
/* 68 */       for (IPacket<?> p : this.packets) {
/* 69 */         mc.player.connection.sendPacketSilent(p);
/*    */       }
/*    */     }
/* 72 */     this.packets.clear();
/* 73 */     this.lastPos = null;
/*    */   }
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
/*    */   public void onEnable() {
/* 87 */     this.lastPos = mc.player.getPositionVec();
/* 88 */     if (Server.isFT() && this.pulse.get() && this.pulseTimer.get() > 6.0F)
/* 89 */       rock.getAlertHandler().alert(Tooltip.create("Оптимальное значение под FunTime - 6"), AlertType.INFO); 
/*    */   }
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\Blink.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */