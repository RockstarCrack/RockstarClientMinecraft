/*    */ package fun.rockstarity.client.modules.move;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.player.EventCollision;
/*    */ import fun.rockstarity.api.helpers.player.Move;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.util.math.vector.Vector3d;
/*    */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "NoClip", desc = "Ходит сквозь стены", type = Category.MOVE)
/*    */ public class NoClip
/*    */   extends Module
/*    */ {
/* 35 */   private final CheckBox colliBox = new CheckBox((Bindable)this, "Только в блоках");
/* 36 */   private final CheckBox custom = new CheckBox((Bindable)this, "Кастомная скорость");
/* 37 */   private final Slider speedCustom = (new Slider((Bindable)this, "Скорость")).min(0.5F).max(5.0F).inc(0.5F).set(1.0F).hide(() -> Boolean.valueOf(!this.custom.get()));
/*    */   
/*    */   private boolean prevCollision;
/*    */   private boolean collision;
/*    */   
/*    */   public void onEvent(Event event) {
/* 43 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 44 */       this.prevCollision = mc.world.getCollisionShapes((Entity)mc.player, mc.player.getBoundingBox()).toList().isEmpty();
/* 45 */       this.collision = (mc.world.getCollisionShapes((Entity)mc.player, mc.player.getBoundingBox().shrink(0.0625D)).toList().isEmpty() && this.prevCollision);
/* 46 */       mc.player.noClip = true;
/*    */       
/* 48 */       if (this.custom.get() && !this.collision) {
/* 49 */         Move.setSpeed((this.speedCustom.get() / 10.0F));
/*    */       }
/*    */     } 
/* 52 */     if (event instanceof EventCollision) { EventCollision e = (EventCollision)event;
/*    */       
/* 54 */       Vector3d backUp = new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());
/* 55 */       mc.player.setPosition((mc.player.getPositionVec()).x, (mc.player.getPositionVec()).y, (mc.player.getPositionVec()).z);
/* 56 */       mc.player.setPosition(backUp.x, backUp.y, backUp.z);
/*    */       
/* 58 */       if (this.colliBox.get() && this.collision)
/*    */         return; 
/* 60 */       if (mc.player.isSneaking() || (!mc.player.isOnGround() && mc.player.fallDistance == 0.0F) || e.getBlockPos().getY() >= mc.player.getPosY()) {
/* 61 */         event.cancel();
/*    */       } }
/*    */   
/*    */   }
/*    */ 
/*    */   
/*    */   @NativeInclude
/*    */   public void onDisable() {
/* 69 */     mc.player.noClip = false;
/*    */   }
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\NoClip.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */