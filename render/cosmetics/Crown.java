/*    */ package fun.rockstarity.client.modules.render.cosmetics;
/*    */ 
/*    */ import com.mojang.blaze3d.matrix.MatrixStack;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.helpers.render.Render;
/*    */ import fun.rockstarity.api.modules.settings.list.Select;
/*    */ import fun.rockstarity.api.render.color.themes.Style;
/*    */ import fun.rockstarity.client.modules.render.Cosmetics;
/*    */ import fun.rockstarity.client.modules.render.FreeLook;
/*    */ import net.minecraft.client.entity.player.ClientPlayerEntity;
/*    */ import net.minecraft.client.settings.PointOfView;
/*    */ import net.minecraft.entity.LivingEntity;
/*    */ import net.minecraft.util.math.vector.Vector3f;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Crown
/*    */   extends Cosmetics.Cosmetic
/*    */ {
/*    */   public Crown(Cosmetics ui, Select select) {
/* 24 */     super(select, "Корона");
/*    */   }
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {}
/*    */ 
/*    */   
/*    */   public void render(MatrixStack ms) {
/* 32 */     if (this.showing.finished(false))
/*    */       return; 
/* 34 */     Render.startFlatRender();
/*    */     
/* 36 */     ClientPlayerEntity clientPlayerEntity = mc.player;
/*    */     
/* 38 */     double x = ((LivingEntity)clientPlayerEntity).lastTickPosX + (clientPlayerEntity.getPosX() - ((LivingEntity)clientPlayerEntity).lastTickPosX) * mc.getRenderPartialTicks() - (mc.getRenderManager()).info.getProjectedView().getX();
/* 39 */     double y = ((LivingEntity)clientPlayerEntity).lastTickPosY + (clientPlayerEntity.getPosY() - ((LivingEntity)clientPlayerEntity).lastTickPosY) * mc.getRenderPartialTicks() - (mc.getRenderManager()).info.getProjectedView().getY() + mc.player.getEyeHeight() + 0.1D;
/* 40 */     double z = ((LivingEntity)clientPlayerEntity).lastTickPosZ + (clientPlayerEntity.getPosZ() - ((LivingEntity)clientPlayerEntity).lastTickPosZ) * mc.getRenderPartialTicks() - (mc.getRenderManager()).info.getProjectedView().getZ();
/*    */     
/* 42 */     float miniSize = 0.08F;
/*    */     
/* 44 */     FreeLook freelook = (FreeLook)rock.getModules().get(FreeLook.class);
/*    */     
/* 46 */     for (int i = 0; i < 360; i++) {
/* 47 */       float sin = (float)Math.sin(Math.toRadians(i)) * 0.5F;
/* 48 */       float cos = (float)Math.cos(Math.toRadians(i)) * 0.5F;
/*    */       
/* 50 */       ms.push();
/* 51 */       ms.translate(sin, 0.4000000059604645D, cos);
/* 52 */       if (mc.getGameSettings().getPointOfView() == PointOfView.THIRD_PERSON_BACK) {
/* 53 */         ms.rotate(Vector3f.XP.rotationDegrees(freelook.get() ? mc.player.rotationYaw : 180.0F));
/*    */       }
/* 55 */       Render.flatImage(ms, "masks/glow.png", (-miniSize / 2.0F), (-miniSize / 2.0F), (-miniSize / 2.0F), miniSize, miniSize, Style.getPoint(i * 3).alpha(this.showing.get()));
/*    */       
/* 57 */       ms.pop();
/*    */       
/* 59 */       for (int i1 = 0; i1 < 10; i1++) {
/* 60 */         ms.push();
/* 61 */         ms.translate(sin, 0.5D + Math.sin(Math.toRadians((i * 8))) * (0.1F - i1 * 0.01F) - (i1 * 0.02F), cos);
/* 62 */         if (mc.getGameSettings().getPointOfView() == PointOfView.THIRD_PERSON_BACK) {
/* 63 */           ms.rotate(Vector3f.XP.rotationDegrees(freelook.get() ? mc.player.rotationYaw : 180.0F));
/*    */         }
/* 65 */         Render.flatImage(ms, "masks/glow.png", (-miniSize / 2.0F), (-miniSize / 2.0F), (-miniSize / 2.0F), miniSize, miniSize, Style.getPoint(i * 3).alpha(this.showing.get()));
/*    */         
/* 67 */         ms.pop();
/*    */       } 
/*    */     } 
/*    */     
/* 71 */     Render.endFlatRender();
/*    */   }
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\render\cosmetics\Crown.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */