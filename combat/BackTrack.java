/*     */ package fun.rockstarity.client.modules.combat;
/*     */ 
/*     */ import com.mojang.blaze3d.matrix.MatrixStack;
/*     */ import com.mojang.blaze3d.platform.GlStateManager;
/*     */ import fun.rockstarity.api.Reacher;
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.render.EventRender3D;
/*     */ import fun.rockstarity.api.helpers.render.Render;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import fun.rockstarity.api.render.color.themes.Style;
/*     */ import fun.rockstarity.client.modules.render.GlowESP;
/*     */ import net.minecraft.client.renderer.ActiveRenderInfo;
/*     */ import net.minecraft.client.renderer.IRenderTypeBuffer;
/*     */ import net.minecraft.client.renderer.entity.EntityRendererManager;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.vector.Vector3d;
/*     */ import org.lwjgl.opengl.GL11;
/*     */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "BackTrack", desc = "Задерживает хитбокс", type = Category.COMBAT)
/*     */ public class BackTrack
/*     */   extends Module
/*     */ {
/*  51 */   private final Mode mode = (new Mode((Bindable)this, "Отображать..")).desc("Позволяет выбрать режим отображения");
/*     */   
/*  53 */   private final Mode.Element nothing = new Mode.Element(this.mode, "Ничего");
/*  54 */   private final Mode.Element entity = new Mode.Element(this.mode, "Модель");
/*  55 */   private final Mode.Element box = new Mode.Element(this.mode, "Хитбокс");
/*     */   
/*  57 */   private final Slider time = (new Slider((Bindable)this, "Время")).min(50.0F).max(1000.0F).inc(50.0F).set(100.0F).desc("Позволяет выбрать время, через которое след за игроком удаляться");
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  61 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/*  62 */       for (PlayerEntity player : mc.world.getPlayers()) {
/*  63 */         if (player != mc.player && !rock.getFriendsHandler().isFriend(player.getName().getString()) && 
/*  64 */           player.getBacktrack().size() > 2) {
/*  65 */           player.getBacktrack().remove(0);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*  70 */     if (event instanceof fun.rockstarity.api.events.list.render.world.EventRenderWorld && this.mode.is(this.entity)) {
/*  71 */       MatrixStack matrixStack = new MatrixStack();
/*  72 */       ActiveRenderInfo activeRenderInfo = mc.gameRenderer.getActiveRenderInfo();
/*     */       
/*  74 */       float time = this.time.get();
/*     */       
/*  76 */       GlowESP.SILENT_RENDERING = true;
/*     */       
/*  78 */       GL11.glPushMatrix();
/*     */       
/*  80 */       GlStateManager.enableBlend();
/*  81 */       GlStateManager.depthMask(false);
/*  82 */       GlStateManager.disableTexture();
/*  83 */       GL11.glShadeModel(7425);
/*  84 */       GlStateManager.disableCull();
/*  85 */       GlStateManager.enableDepthTest();
/*  86 */       GlStateManager.blendFunc(770, 772);
/*  87 */       GlStateManager.glBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE.param, GlStateManager.SourceFactor.ZERO.param, GlStateManager.DestFactor.ONE.param);
/*  88 */       GL11.glEnable(2929);
/*     */ 
/*     */       
/*  91 */       for (PlayerEntity player : mc.world.getPlayers()) {
/*  92 */         if (player == mc.player || rock.getFriendsHandler().isFriend(player.getName().getString()))
/*  93 */           continue;  for (Position position : player.getBacktrack()) {
/*  94 */           Vector3d cameraPos = (mc.getRenderManager()).info.getProjectedView();
/*     */           try {
/*  96 */             Vector3d pos = position.getPos();
/*  97 */             double x = pos.x - (mc.getRenderManager()).info.getProjectedView().getX();
/*  98 */             double y = pos.y - (mc.getRenderManager()).info.getProjectedView().getY();
/*  99 */             double z = pos.z - (mc.getRenderManager()).info.getProjectedView().getZ();
/*     */             
/* 101 */             EntityRendererManager renderManager = mc.getRenderManager();
/*     */             
/* 103 */             if (renderManager == null) {
/*     */               continue;
/*     */             }
/* 106 */             float partialTicks = mc.getRenderPartialTicks();
/*     */             
/* 108 */             Vector3d camera = activeRenderInfo.getProjectedView();
/*     */             
/* 110 */             Reacher.ENTITY_ALPHA = (1.0F - (float)(System.currentTimeMillis() - position.getTime()) / time) * 0.5F;
/* 111 */             Reacher.SILENT = true;
/*     */             
/* 113 */             renderManager.renderEntityStaticSilent((Entity)player, x, y, z, 
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 118 */                 MathHelper.lerp(partialTicks, player.prevRotationYaw, player.rotationYaw), partialTicks, matrixStack, (IRenderTypeBuffer)mc
/*     */                 
/* 120 */                 .getRenderTypeBuffers().getBufferSource(), renderManager
/* 121 */                 .getPackedLight((Entity)player, partialTicks));
/*     */ 
/*     */             
/* 124 */             Reacher.ENTITY_ALPHA = 1.0F;
/* 125 */             Reacher.SILENT = false;
/*     */             
/* 127 */             if ((float)(System.currentTimeMillis() - position.getTime()) > time) {
/* 128 */               player.getBacktrack().remove(position);
/*     */               break;
/*     */             } 
/* 131 */           } catch (Exception e1) {
/* 132 */             e1.printStackTrace();
/*     */           } 
/*     */         } 
/*     */       } 
/* 136 */       GlStateManager.blendFunc(770, 771);
/* 137 */       GlStateManager.shadeModel(7424);
/* 138 */       GlStateManager.disableBlend();
/* 139 */       GlStateManager.enableTexture();
/* 140 */       GlStateManager.depthMask(true);
/* 141 */       GlStateManager.enableCull();
/* 142 */       GlStateManager.popMatrix();
/* 143 */       GlowESP.SILENT_RENDERING = false;
/*     */     } 
/*     */ 
/*     */     
/* 147 */     if (event instanceof EventRender3D) { EventRender3D e = (EventRender3D)event; if (this.mode.is(this.box))
/*     */       {
/* 149 */         for (PlayerEntity player : mc.world.getPlayers()) {
/* 150 */           if (player == mc.player || rock.getFriendsHandler().isFriend(player.getName().getString()))
/* 151 */             continue;  for (Position position : player.getBacktrack()) {
/* 152 */             Vector3d cameraPos = (mc.getRenderManager()).info.getProjectedView();
/*     */             try {
/* 154 */               Vector3d pos = position.getPos();
/* 155 */               GL11.glRotated(e.getRenderInfo().getPitch(), 1.0D, 0.0D, 0.0D);
/* 156 */               GL11.glRotated((e.getRenderInfo().getYaw() + 180.0F), 0.0D, 1.0D, 0.0D);
/* 157 */               double x = pos.x - (mc.getRenderManager()).info.getProjectedView().getX();
/* 158 */               double y = pos.y - (mc.getRenderManager()).info.getProjectedView().getY();
/* 159 */               double z = pos.z - (mc.getRenderManager()).info.getProjectedView().getZ();
/* 160 */               GL11.glPushMatrix();
/* 161 */               GL11.glEnable(3042);
/* 162 */               GL11.glLineWidth(2.0F);
/* 163 */               GL11.glDisable(3553);
/* 164 */               GL11.glDisable(2929);
/*     */               
/* 166 */               Render.setColor(Style.getMain().alpha((1.0F - (float)(System.currentTimeMillis() - position.getTime()) / this.time.get())).getRGB());
/* 167 */               Render.drawBoxing(new AxisAlignedBB(x - 0.30000001192092896D, y, z - 0.30000001192092896D, x + 0.30000001192092896D, y + player.getHeight(), z + 0.30000001192092896D));
/* 168 */               GL11.glLineWidth(9.5F);
/* 169 */               GL11.glEnable(3553);
/* 170 */               GL11.glEnable(2929);
/* 171 */               GL11.glDisable(3042);
/* 172 */               Render.resetColor();
/* 173 */               GL11.glPopMatrix();
/*     */               
/* 175 */               GL11.glRotated((e.getRenderInfo().getYaw() + 180.0F), 0.0D, -1.0D, 0.0D);
/* 176 */               GL11.glRotated(e.getRenderInfo().getPitch(), -1.0D, 0.0D, 0.0D);
/*     */ 
/*     */               
/* 179 */               if ((float)(System.currentTimeMillis() - position.getTime()) > this.time.get()) {
/* 180 */                 player.getBacktrack().remove(position);
/*     */                 break;
/*     */               } 
/* 183 */             } catch (Exception e1) {
/* 184 */               e1.printStackTrace();
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   @NativeInclude
/*     */   public void onDisable() {}
/*     */   
/*     */   public void onEnable() {}
/*     */   
/*     */   public static class Position
/*     */   {
/*     */     private final Vector3d pos;
/*     */     private final long time;
/*     */     
/*     */     public Position(Vector3d pos, long time) {
/* 204 */       this.pos = pos; this.time = time;
/*     */     }
/* 206 */     public Vector3d getPos() { return this.pos; } public long getTime() {
/* 207 */       return this.time;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\BackTrack.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */