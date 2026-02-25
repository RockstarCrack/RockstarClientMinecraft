/*    */ package fun.rockstarity.client.modules.render.cosmetics;
/*    */ 
/*    */ import com.mojang.blaze3d.matrix.MatrixStack;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.render.EventRender3D;
/*    */ import fun.rockstarity.api.helpers.render.PositionTracker;
/*    */ import fun.rockstarity.api.helpers.render.Render;
/*    */ import fun.rockstarity.api.modules.settings.list.Select;
/*    */ import fun.rockstarity.api.render.color.themes.Style;
/*    */ import fun.rockstarity.client.modules.render.Cosmetics;
/*    */ import java.util.ArrayList;
/*    */ import net.minecraft.util.math.vector.Vector3d;
/*    */ import net.minecraft.util.math.vector.Vector3f;
/*    */ import net.minecraft.util.math.vector.Vector4f;
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
/*    */ public class Tail
/*    */   extends Cosmetics.Cosmetic
/*    */ {
/* 28 */   private final ArrayList<Vector4f> tail = new ArrayList<>();
/*    */   
/*    */   public Tail(Cosmetics ui, Select select) {
/* 31 */     super(select, "Хвостик");
/*    */   }
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 36 */     if (event instanceof EventRender3D) { EventRender3D e = (EventRender3D)event;
/* 37 */       MatrixStack ms = e.getMatrixStack();
/*    */       
/* 39 */       float bigSize = 0.7F;
/* 40 */       float size = 0.3F;
/*    */       
/* 42 */       ArrayList<Vector4f> toRemove = new ArrayList<>();
/*    */       
/* 44 */       Render.startFlatRender();
/*    */       
/* 46 */       int lineSize = this.tail.size();
/* 47 */       for (Vector4f pos : this.tail) {
/* 48 */         int i = this.tail.indexOf(pos);
/* 49 */         float start = Math.min(i / 10.0F * 2.0F, 1.0F);
/*    */ 
/*    */ 
/*    */         
/* 53 */         double posX = pos.x - ((mc.getRenderManager()).info.getProjectedView()).x;
/* 54 */         double posY = pos.y - ((mc.getRenderManager()).info.getProjectedView()).y;
/* 55 */         double posZ = pos.z - ((mc.getRenderManager()).info.getProjectedView()).z;
/*    */         
/* 57 */         if (PositionTracker.isInView(new Vector3d(pos.x, pos.y, pos.z))) {
/* 58 */           ms.push();
/* 59 */           ms.translate(posX, posY, posZ);
/* 60 */           ms.rotate(Vector3f.XP.rotationDegrees(90.0F));
/*    */           
/* 62 */           Render.flatImage(ms, "masks/glow.png", (-bigSize / 2.0F), (-bigSize / 2.0F), -0.009999999776482582D, bigSize, bigSize, Style.getPoint(i * 10).alpha(this.showing.get()));
/* 63 */           Render.flatImage(ms, "masks/glow.png", (-size / 2.0F), (-size / 2.0F), -0.009999999776482582D, size, size, Style.getPoint(i * 10).alpha((0.2F * this.showing.get())));
/*    */           
/* 65 */           float ySize = 50.0F * start;
/* 66 */           for (int y = 1; y < ySize; y++) {
/* 67 */             float val = 1.0F - y / ySize;
/* 68 */             float lightSize = 0.3F;
/* 69 */             Render.flatImage(ms, "masks/glow.png", (-lightSize / 2.0F), (-lightSize / 2.0F), -0.009999999776482582D - y * 0.015D, lightSize, lightSize, Style.getPoint(i * 10).alpha((val * 0.2F * this.showing.get())));
/*    */           } 
/*    */           
/* 72 */           ms.pop();
/*    */         } 
/*    */         
/* 75 */         pos.w -= 0.01F;
/*    */         
/* 77 */         if (pos.w < 0.0F) toRemove.add(pos);
/*    */       
/*    */       } 
/* 80 */       Render.endFlatRender();
/*    */       
/* 82 */       Vector3d playerPos = mc.player.getPositionVec();
/*    */       
/* 84 */       if (mc.player.prevPosX != mc.player.getPosX() || mc.player.prevPosZ != mc.player.getPosZ()) {
/* 85 */         float x = (float)(mc.player.lastTickPosX + (mc.player.getPosX() - mc.player.lastTickPosX) * mc.getRenderPartialTicks());
/* 86 */         float y = (float)(mc.player.lastTickPosY + (mc.player.getPosY() - mc.player.lastTickPosY) * mc.getRenderPartialTicks());
/* 87 */         float z = (float)(mc.player.lastTickPosZ + (mc.player.getPosZ() - mc.player.lastTickPosZ) * mc.getRenderPartialTicks());
/*    */         
/* 89 */         this.tail.add(new Vector4f(x, y, z, 1.0F));
/*    */       } 
/*    */       
/* 92 */       this.tail.removeAll(toRemove); }
/*    */   
/*    */   }
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\render\cosmetics\Tail.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */