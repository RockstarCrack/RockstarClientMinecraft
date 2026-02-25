/*     */ package fun.rockstarity.client.modules.move;
/*     */ 
/*     */ import com.mojang.blaze3d.matrix.MatrixStack;
/*     */ import com.mojang.blaze3d.platform.GlStateManager;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.inputs.EventInput;
/*     */ import fun.rockstarity.api.events.list.player.EventMotionMove;
/*     */ import fun.rockstarity.api.events.list.player.EventMove;
/*     */ import fun.rockstarity.api.events.list.render.EventRender3D;
/*     */ import fun.rockstarity.api.helpers.math.aura.AuraUtility;
/*     */ import fun.rockstarity.api.helpers.math.aura.RotationMode;
/*     */ import fun.rockstarity.api.helpers.render.Render;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.render.animation.Animation;
/*     */ import fun.rockstarity.api.render.animation.Easing;
/*     */ import fun.rockstarity.api.render.color.FixColor;
/*     */ import fun.rockstarity.client.modules.combat.Aura;
/*     */ import fun.rockstarity.client.modules.render.Interface;
/*     */ import net.minecraft.block.Blocks;
/*     */ import net.minecraft.entity.LivingEntity;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.vector.Vector3d;
/*     */ import net.minecraft.util.math.vector.Vector3f;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "PeekAssist", desc = "Помогает при пике", type = Category.MOVE)
/*     */ public class PeekAssist
/*     */   extends Module
/*     */ {
/*  42 */   private final Animation alpha = (new Animation()).setEasing(Easing.BOTH_SINE).setSpeed(300);
/*  43 */   private final Animation target = (new Animation()).setEasing(Easing.BOTH_SINE).setSpeed(300);
/*  44 */   private Vector3d peekPos = Vector3d.ZERO;
/*  45 */   private Vector3d targetPos = Vector3d.ZERO;
/*     */   private boolean peek;
/*     */   
/*     */   public PeekAssist() {
/*  49 */     super(11);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  54 */     if (event instanceof EventRender3D) { EventRender3D e = (EventRender3D)event;
/*  55 */       MatrixStack ms = e.getMatrixStack();
/*     */       
/*  57 */       float size = 3.0F;
/*     */       
/*  59 */       ms.push();
/*  60 */       GlStateManager.depthMask(false);
/*  61 */       ms.translate(this.peekPos.sub(Render.cameraPos().add(0.0D, -0.49000000953674316D, 0.0D)));
/*  62 */       ms.rotate(Vector3f.XP.rotationDegrees(90.0F));
/*     */       
/*  64 */       FixColor[] circle = Interface.getCircle(this.alpha.get());
/*     */       
/*  66 */       Render.drawImage(ms, "masks/glow.png", (-size / 2.0F), (-size / 2.0F), (-size / 2.0F), size, size, circle[0], circle[1], circle[2], circle[3]);
/*  67 */       GlStateManager.depthMask(true);
/*  68 */       ms.pop();
/*     */ 
/*     */       
/*  71 */       this.target.setForward((target() != null));
/*  72 */       ms.push();
/*  73 */       GlStateManager.depthMask(false);
/*  74 */       GlStateManager.disableDepthTest();
/*  75 */       ms.translate(this.targetPos.sub(Render.cameraPos().add(0.0D, -0.49000000953674316D, 0.0D)));
/*  76 */       ms.rotate(Vector3f.XP.rotationDegrees(90.0F));
/*  77 */       Render.drawImage(ms, "masks/glow.png", (-size / 2.0F), (-size / 2.0F), (-size / 2.0F), size, size, FixColor.RED.alpha(this.target.get()));
/*  78 */       GlStateManager.enableDepthTest();
/*  79 */       GlStateManager.depthMask(true);
/*  80 */       ms.pop(); }
/*     */ 
/*     */     
/*  83 */     if (target() != null) {
/*  84 */       this.peek = (mc.player.fallDistance > 0.0F && (target()).fallDistance <= 0.0F);
/*     */       
/*  86 */       if (event instanceof EventMove) { EventMove e = (EventMove)event;
/*  87 */         RotationMode rot = ((Aura)rock.getModules().get(Aura.class)).rotation();
/*  88 */         e.setYaw(rot.getYaw());
/*  89 */         e.setPitch(rot.getPitch()); }
/*     */ 
/*     */       
/*  92 */       if (event instanceof EventInput) { EventInput e = (EventInput)event;
/*  93 */         e.setStrafe(0.0F);
/*     */         
/*  95 */         if (AuraUtility.getBestPoint(target().getEyePosition(0.0F), (LivingEntity)mc.player, true).distanceTo(target().getEyePosition(0.0F)) < 3.0D) {
/*  96 */           e.setForward(-1.0F);
/*     */         }
/*     */         
/*  99 */         if (this.peek) {
/* 100 */           e.setForward(1.0F);
/*     */         } }
/*     */ 
/*     */       
/* 104 */       if (AuraUtility.getBestPoint(target().getEyePosition(0.0F), (LivingEntity)mc.player, true).distanceTo(target().getEyePosition(0.0F)) > 3.0D && !this.peek) {
/* 105 */         if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 106 */           update(mc.player.getPositionVec());
/*     */         }
/*     */         
/* 109 */         if (event instanceof EventMotionMove) EventMotionMove eventMotionMove = (EventMotionMove)event;
/*     */       
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 119 */     this.alpha.setForward(false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/* 124 */     update(mc.player.getPositionVec());
/* 125 */     this.alpha.setForward(true);
/*     */   }
/*     */   private void update(Vector3d pos) {
/*     */     int i;
/* 129 */     for (i = 0; i < 15; i++) {
/* 130 */       if (mc.world.getBlock((new BlockPos(pos)).add(0, -i, 0)) != Blocks.AIR) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 137 */         this.peekPos = new Vector3d(pos.x, ((((new BlockPos(pos)).add(0, -i, 0)).y - 1) + 0.05F), pos.z);
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 142 */     if (target() == null) {
/*     */       return;
/*     */     }
/*     */     
/* 146 */     pos = target().getPositionVec();
/*     */     
/* 148 */     for (i = 0; i < 15; i++) {
/* 149 */       if (mc.world.getBlock((new BlockPos(pos)).add(0, -i, 0)) != Blocks.AIR) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 156 */         this.targetPos = new Vector3d(pos.x, ((((new BlockPos(pos)).add(0, -i, 0)).y - 1) + 0.05F), pos.z);
/*     */         break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private LivingEntity target() {
/* 163 */     return ((Aura)rock.getModules().get(Aura.class)).getTarget();
/*     */   }
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\PeekAssist.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */