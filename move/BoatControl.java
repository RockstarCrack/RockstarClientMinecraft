/*    */ package fun.rockstarity.client.modules.move;
/*    */ 
/*    */ import com.mojang.blaze3d.matrix.MatrixStack;
/*    */ import com.mojang.blaze3d.platform.GlStateManager;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.player.EventAttack;
/*    */ import fun.rockstarity.api.events.list.player.EventMotionMove;
/*    */ import fun.rockstarity.api.events.list.player.EventUpdate;
/*    */ import fun.rockstarity.api.events.list.render.EventRender3D;
/*    */ import fun.rockstarity.api.helpers.player.Move;
/*    */ import fun.rockstarity.api.helpers.render.Render;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.render.color.themes.Style;
/*    */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.entity.item.BoatEntity;
/*    */ import net.minecraft.network.IPacket;
/*    */ import net.minecraft.network.play.client.CPlayerPacket;
/*    */ import net.minecraft.util.math.vector.Vector3d;
/*    */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @NativeInclude
/*    */ @Info(name = "BoatControl", desc = "Выключает античит возле лодки", type = Category.MOVE)
/*    */ public class BoatControl
/*    */   extends Module
/*    */ {
/*    */   private BoatEntity target;
/*    */   
/*    */   public void onEvent(Event event) {
/* 35 */     if (event instanceof EventUpdate) { EventUpdate e = (EventUpdate)event;
/* 36 */       this.target = null;
/* 37 */       for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity ent = objectIterator.next();
/* 38 */         if (ent instanceof BoatEntity) { BoatEntity entity = (BoatEntity)ent; if (mc.player.getDistance((Entity)entity) < 3.0F) {
/* 39 */             this.target = entity;
/*    */             break;
/*    */           }  }
/*    */          }
/*    */       
/* 44 */       if (this.target != null) {
/* 45 */         mc.player.fallDistance = 1.0F;
/*    */       } }
/*    */ 
/*    */     
/* 49 */     if (event instanceof EventRender3D) { EventRender3D e = (EventRender3D)event;
/* 50 */       MatrixStack ms = e.getMatrixStack();
/* 51 */       if (this.target != null) {
/* 52 */         int len = 361;
/* 53 */         double size = 0.5D;
/* 54 */         int start = (int)(mc.getRenderManager()).info.getPitch();
/* 55 */         double x = this.target.lastTickPosX + (this.target.getPosX() - this.target.lastTickPosX) * ((mc.currentScreen == null) ? mc.getRenderPartialTicks() : 0.0F) - (mc.getRenderManager()).info.getProjectedView().getX();
/* 56 */         double y = this.target.lastTickPosY + (this.target.getPosY() - this.target.lastTickPosY) * ((mc.currentScreen == null) ? mc.getRenderPartialTicks() : 0.0F) - (mc.getRenderManager()).info.getProjectedView().getY();
/* 57 */         double z = this.target.lastTickPosZ + (this.target.getPosZ() - this.target.lastTickPosZ) * ((mc.currentScreen == null) ? mc.getRenderPartialTicks() : 0.0F) - (mc.getRenderManager()).info.getProjectedView().getZ();
/*    */         
/* 59 */         for (int i = start; i <= start + len - 1; i++) {
/* 60 */           double s = 0.5D;
/* 61 */           double cos = Math.cos(Math.toRadians(i)) * 4.0D * s;
/* 62 */           double sin = Math.sin(Math.toRadians(i)) * 4.0D * s;
/* 63 */           double posX = x + cos;
/* 64 */           double posY = y;
/* 65 */           double posZ = z + sin;
/*    */           
/* 67 */           ms.push();
/* 68 */           GlStateManager.depthMask(false);
/* 69 */           ms.translate(posX, posY, posZ);
/* 70 */           ms.rotate((mc.getRenderManager()).info.getRotation());
/*    */           
/* 72 */           Render.drawImage(ms, "masks/glow.png", -size / 2.0D, -size / 2.0D, -size / 2.0D, size, size, Style.getPoint(i * 10));
/* 73 */           GlStateManager.depthMask(true);
/* 74 */           ms.pop();
/*    */         } 
/*    */       }  }
/*    */ 
/*    */     
/* 79 */     if (event instanceof EventMotionMove) { EventMotionMove e = (EventMotionMove)event;
/* 80 */       if (this.target != null) {
/* 81 */         Move.setSpeed(1.0D);
/* 82 */         if (mc.player.fallDistance > 0.1F);
/*    */ 
/*    */         
/* 85 */         if (mc.player.getDistance((Entity)this.target) > 2.5F && 
/* 86 */           mc.player.getPositionVec().add(mc.player.getMotion()).distanceTo(this.target.getPositionVec()) > mc.player.getDistance((Entity)this.target)) {
/* 87 */           e.setMotion(Vector3d.ZERO);
/*    */         }
/*    */       }  }
/*    */ 
/*    */ 
/*    */     
/* 93 */     if (event instanceof EventAttack) { EventAttack e = (EventAttack)event;
/* 94 */       if (this.target != null) {
/* 95 */         Vector3d pos = mc.player.getPositionVec();
/* 96 */         mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y + 0.10000000149011612D, pos.z, mc.player.rotationYaw, mc.player.rotationPitch, false));
/* 97 */         mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y, pos.z, mc.player.rotationYaw, mc.player.rotationPitch, false));
/*    */       }  }
/*    */   
/*    */   }
/*    */   
/*    */   public void onDisable() {}
/*    */   
/*    */   public void onEnable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\BoatControl.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */