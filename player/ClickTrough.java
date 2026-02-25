/*    */ package fun.rockstarity.client.modules.player;
/*    */ 
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.util.Direction;
/*    */ import net.minecraft.util.Hand;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.util.math.BlockRayTraceResult;
/*    */ import net.minecraft.util.math.MathHelper;
/*    */ import net.minecraft.util.math.vector.Vector3d;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "ClickTrough", desc = "Позволяет взаимодействовать с контейнерами через стены", type = Category.PLAYER, module = {"GhostHand", "OpenWalls"})
/*    */ public class ClickTrough
/*    */   extends Module
/*    */ {
/* 25 */   private final Set<BlockPos> checkedPositions = new HashSet<>();
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 29 */     if (mc.player == null || mc.world == null || mc.playerController == null)
/* 30 */       return;  if (!mc.gameSettings.keyBindUseItem.isPressed() || mc.player.isSneaking())
/*    */       return; 
/* 32 */     if (event instanceof fun.rockstarity.api.events.list.game.EventTick) {
/*    */ 
/*    */       
/* 35 */       Vector3d offset = (new Vector3d(0.0D, 0.0D, 0.1D)).rotatePitch(-((float)Math.toRadians(mc.player.rotationPitch))).rotateYaw(-((float)Math.toRadians(mc.player.rotationYaw)));
/*    */       
/* 37 */       this.checkedPositions.clear();
/*    */ 
/*    */       
/* 40 */       for (int i = 1; i < 50; i++) {
/* 41 */         BlockPos targetPos = new BlockPos(getCameraPosVec(mc.getRenderPartialTicks()).add(offset.mul(i)));
/*    */ 
/*    */         
/* 44 */         if (!this.checkedPositions.contains(targetPos)) {
/*    */ 
/*    */ 
/*    */           
/* 48 */           this.checkedPositions.add(targetPos);
/*    */ 
/*    */           
/* 51 */           for (TileEntity blockEntity : mc.world.loadedTileEntityList) {
/* 52 */             if (blockEntity.getPos().equals(targetPos)) {
/*    */               
/* 54 */               mc.playerController.func_217292_a(mc.player, mc.world, Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(targetPos.getX() + 0.5D, targetPos.getY() + 1.0D, targetPos.getZ() + 0.5D), Direction.UP, targetPos, true));
/*    */               return;
/*    */             } 
/*    */           } 
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Vector3d getCameraPosVec(float partialTicks) {
/* 67 */     if (mc.player == null) return Vector3d.ZERO;
/*    */     
/* 69 */     double x = MathHelper.lerp(partialTicks, mc.player.prevPosX, mc.player.getPosX());
/* 70 */     double y = MathHelper.lerp(partialTicks, mc.player.prevPosY, mc.player.getPosY()) + mc.player.getEyeHeight(mc.player.getPose());
/* 71 */     double z = MathHelper.lerp(partialTicks, mc.player.prevPosZ, mc.player.getPosZ());
/* 72 */     return new Vector3d(x, y, z);
/*    */   }
/*    */   
/*    */   public void onEnable() {}
/*    */   
/*    */   public void onDisable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\ClickTrough.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */