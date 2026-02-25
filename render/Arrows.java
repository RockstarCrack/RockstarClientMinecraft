/*     */ package fun.rockstarity.client.modules.render;
/*     */ 
/*     */ import com.mojang.blaze3d.platform.GlStateManager;
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.connection.globals.ClientAPI;
/*     */ import fun.rockstarity.api.connection.globals.GlobalsColors;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.EventType;
/*     */ import fun.rockstarity.api.events.list.player.EventUpdate;
/*     */ import fun.rockstarity.api.events.list.render.EventRender2D;
/*     */ import fun.rockstarity.api.events.list.render.EventRender3D;
/*     */ import fun.rockstarity.api.helpers.player.Move;
/*     */ import fun.rockstarity.api.helpers.render.PositionTracker;
/*     */ import fun.rockstarity.api.helpers.render.Render;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Select;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import fun.rockstarity.api.render.animation.infinity.InfinityAnimation;
/*     */ import fun.rockstarity.api.render.color.FixColor;
/*     */ import fun.rockstarity.api.render.color.themes.Style;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.stream.Collectors;
/*     */ import net.minecraft.client.gui.screen.Screen;
/*     */ import net.minecraft.client.gui.screen.inventory.ContainerScreen;
/*     */ import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
/*     */ import net.minecraft.client.settings.PointOfView;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.vector.Vector3d;
/*     */ import org.lwjgl.opengl.GL11;
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
/*     */ @Info(name = "Arrows", desc = "Отображает стрелочки до игроков", type = Category.RENDER)
/*     */ public class Arrows
/*     */   extends Module
/*     */ {
/*  57 */   protected final InfinityAnimation radius = new InfinityAnimation();
/*  58 */   protected final InfinityAnimation angle = new InfinityAnimation();
/*     */   
/*  60 */   protected final Mode mode = new Mode((Bindable)this, "Мод");
/*     */   
/*  62 */   protected final Mode.Element arrows = new Mode.Element(this.mode, "Стрелки");
/*  63 */   protected final Mode.Element tracer = new Mode.Element(this.mode, "Линии");
/*     */   
/*  65 */   protected final Slider size = (new Slider((Bindable)this, "Размер")).min(0.6F).max(1.0F).inc(0.05F).set(0.7F)
/*  66 */     .hide(() -> Boolean.valueOf(this.mode.is(this.tracer)));
/*  67 */   protected final Slider dist = (new Slider((Bindable)this, "Растояние")).min(5.0F).max(150.0F).inc(1.0F).set(10.0F)
/*  68 */     .hide(() -> Boolean.valueOf(this.mode.is(this.tracer)));
/*     */   
/*  70 */   protected final Select targets = (new Select((Bindable)this, "Сущности")).min(1);
/*     */   
/*  72 */   protected final Select.Element friend = (new Select.Element(this.targets, "Друзей")).set(true);
/*  73 */   protected final Select.Element players = (new Select.Element(this.targets, "Игроков")).set(true);
/*  74 */   protected final Select.Element nakedPlayers = (new Select.Element(this.targets, "Голых игроков")).hide(() -> Boolean.valueOf(!this.players.get()));
/*  75 */   protected final Select.Element mobs = new Select.Element(this.targets, "Мобов");
/*  76 */   protected final Select.Element bot = new Select.Element(this.targets, "Ботов");
/*  77 */   protected final Select.Element items = new Select.Element(this.targets, "Предметы");
/*  78 */   protected final Select.Element backward = (new Select.Element(this.targets, "За спиной")).set(true);
/*     */   
/*  80 */   protected final CheckBox animations = (new CheckBox((Bindable)this, "Анимации")).set(true);
/*  81 */   protected final CheckBox thirdperson = new CheckBox((Bindable)this, "Игнорировать 3-е лицо");
/*     */   
/*  83 */   protected final Slider width = (new Slider((Bindable)this, "Ширина линий")).min(0.5F).max(5.0F).inc(0.5F).set(0.5F)
/*  84 */     .hide(() -> Boolean.valueOf(!this.mode.is(this.tracer)));
/*     */   
/*  86 */   protected final Set<Entity> entities = new HashSet<>();
/*     */ 
/*     */ 
/*     */   
/*     */   @EventType({EventRender2D.class, EventRender3D.class, EventUpdate.class})
/*     */   public void onEvent(Event event) {
/*  92 */     if (event instanceof EventUpdate && (
/*  93 */       this.mode.is(this.arrows) || this.mode.is(this.tracer))) {
/*  94 */       this.entities.addAll((Collection<? extends Entity>)mc.world.getAllEntities().stream().filter(this::isValid).collect(Collectors.toSet()));
/*     */     }
/*     */ 
/*     */     
/*  98 */     if (this.mode.is(this.arrows)) {
/*  99 */       if (event instanceof EventRender2D) { EventRender2D e = (EventRender2D)event;
/*     */         
/* 101 */         float size = 0.0F, xOffset = mc.getMainWindow().getScaledWidth() / 2.0F - 24.5F;
/* 102 */         float yOffset = mc.getMainWindow().getScaledHeight() / 2.0F - 25.2F;
/*     */         
/* 104 */         float sizeValue = 0.0F;
/* 105 */         if (Move.isMoving())
/* 106 */           sizeValue = (float)(sizeValue + Move.getSpeed() * 100.0D); 
/* 107 */         if (mc.player.isSneaking())
/* 108 */           sizeValue -= 20.0F; 
/* 109 */         if ((mc.currentScreen instanceof ContainerScreen || mc.currentScreen instanceof net.minecraft.client.gui.screen.IngameMenuScreen) && this.dist
/* 110 */           .get() < 86.0F) {
/* 111 */           sizeValue += 110.0F;
/*     */         }
/*     */         
/* 114 */         float shortestYawPath = ((mc.player.rotationYaw - this.angle.get()) % 360.0F + 540.0F) % 360.0F - 180.0F;
/* 115 */         this.angle.animate(mc.player.rotationYaw + shortestYawPath, 100);
/*     */         
/* 117 */         sizeValue += Math.abs(mc.player.rotationYaw - this.angle.get()) / 10.0F;
/*     */ 
/*     */         
/* 120 */         this.radius.animate(this.animations.get() ? sizeValue : 0.0F, 100);
/*     */         
/* 122 */         size += this.radius.get() + this.dist.get();
/*     */         
/* 124 */         GL11.glPushMatrix();
/*     */         
/* 126 */         float value = Math.max(0.1F, mc.player.rotationPitch / 90.0F);
/*     */         
/* 128 */         Entity toRemove = null;
/*     */         
/* 130 */         for (Entity ent : this.entities) {
/* 131 */           ent.getArrowHide().setForward((mc.world.getAllEntities().contains(ent) && (!this.thirdperson.get() || mc.getGameSettings().getPointOfView() == PointOfView.FIRST_PERSON) && isValid(ent)));
/* 132 */           ent.getArrowHide().setSpeed(this.animations.get() ? 300 : 1);
/*     */           
/* 134 */           if (ent.getArrowHide().finished(false) && !ent.getArrowHide().isForward()) {
/* 135 */             toRemove = ent;
/*     */           }
/* 137 */           if (ent.ticksExisted < 10 && !ent.getArrowHide().isForward()) {
/*     */             continue;
/*     */           }
/*     */           
/* 141 */           GlStateManager.pushMatrix();
/* 142 */           GlStateManager.disableBlend();
/*     */ 
/*     */ 
/*     */           
/* 146 */           double x = ent.lastTickPosX + (ent.getPosX() - ent.lastTickPosX) * mc.getRenderPartialTicks() - (mc.getRenderManager()).info.getProjectedView().getX();
/*     */           
/* 148 */           double z = ent.lastTickPosZ + (ent.getPosZ() - ent.lastTickPosZ) * mc.getRenderPartialTicks() - (mc.getRenderManager()).info.getProjectedView().getZ();
/*     */           
/* 150 */           int i = 2;
/* 151 */           double cos = Math.cos(mc.player.rotationYaw * Math.PI * i / 360.0D);
/* 152 */           double sizes = 0.8D;
/* 153 */           double sin = Math.sin(mc.player.rotationYaw * Math.PI * i / 360.0D);
/* 154 */           double rotY = -(z * cos - x * sin);
/* 155 */           double rotX = -(x * cos + z * sin);
/*     */           
/* 157 */           float angle = (float)(Math.atan2(rotY, rotX) * 180.0D / Math.PI) + mc.player.rotationYaw - this.angle.get();
/* 158 */           double valX = (sizes + size) * Math.cos(Math.toRadians(angle));
/* 159 */           double valY = (sizes + size) * Math.sin(Math.toRadians(angle));
/* 160 */           Screen screen1 = mc.currentScreen; ContainerScreen con = (ContainerScreen)screen1;
/*     */           
/* 162 */           double circleX = (screen1 instanceof ContainerScreen) ? MathHelper.clamp(valX, (-con.xSize / 2.0F + 20.0F), (con.xSize / 2.0F - 20.0F)) : valX;
/* 163 */           Screen screen2 = mc.currentScreen; ContainerScreen containerScreen1 = (ContainerScreen)screen2;
/*     */           
/* 165 */           double circleY = (screen2 instanceof ContainerScreen) ? MathHelper.clamp(valY, (-containerScreen1.ySize / 2.0F + 20.0F), (containerScreen1.ySize / 2.0F - 20.0F)) : valY;
/* 166 */           double xPos = (xOffset + (50 / i)) + circleX;
/* 167 */           double y = (yOffset + (50 / i)) + circleY;
/* 168 */           GlStateManager.translated(xPos, y, 0.0D);
/*     */ 
/*     */           
/* 171 */           GlStateManager.rotatef(angle, 0.0F, 0.0F, 1.0F);
/* 172 */           GlStateManager.disableBlend();
/* 173 */           GlStateManager.translatef(51.0F, 0.0F, 0.0F);
/*     */           
/* 175 */           GlStateManager.scaled(2.0D, 2.0D, 2.0D);
/* 176 */           GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
/* 177 */           GlStateManager.translatef(-5.0F, 0.0F, 0.0F);
/* 178 */           Render.drawImage(e.getMatrixStack(), "masks/arrow.png", 0.0D, 0.0D, 0.0D, (this.size.get() * 15.0F), (this.size.get() * 15.0F), getColor(ent, angle).alpha(ent.getArrowHide().get()));
/* 179 */           GlStateManager.popMatrix();
/*     */         } 
/*     */         
/* 182 */         if (toRemove != null) {
/* 183 */           this.entities.remove(toRemove);
/*     */         }
/* 185 */         GL11.glPopMatrix(); }
/*     */     
/*     */     } else {
/* 188 */       if (event instanceof EventRender3D) { EventRender3D e = (EventRender3D)event;
/*     */         
/* 190 */         GL11.glRotated(e.getRenderInfo().getPitch(), 1.0D, 0.0D, 0.0D);
/* 191 */         GL11.glRotated((e.getRenderInfo().getYaw() + 180.0F), 0.0D, 1.0D, 0.0D);
/* 192 */         GL11.glPushMatrix();
/* 193 */         GL11.glEnable(3042);
/* 194 */         GL11.glEnable(2884);
/* 195 */         GL11.glLineWidth(this.width.get());
/* 196 */         GL11.glDisable(3553);
/* 197 */         GL11.glDisable(2929);
/* 198 */         GL11.glDepthMask(false);
/* 199 */         GL11.glEnable(2848);
/* 200 */         GL11.glHint(3154, 4354);
/*     */         
/* 202 */         Vector3d vec = (new Vector3d(0.0D, 0.0D, 150.0D)).rotatePitch((float)-Math.toRadians(mc.player.rotationPitch)).rotateYaw((float)-Math.toRadians(mc.player.rotationYaw));
/* 203 */         double partialTicks = mc.getRenderPartialTicks();
/* 204 */         Vector3d projectedView = (mc.getRenderManager()).info.getProjectedView();
/* 205 */         Entity toRemove = null;
/* 206 */         for (Entity entity : this.entities) {
/* 207 */           entity.getArrowHide().setForward((mc.world.getAllEntities().contains(entity) && (!this.thirdperson.get() || mc.getGameSettings().getPointOfView() == PointOfView.FIRST_PERSON) && isValid(entity)));
/* 208 */           entity.getArrowHide()
/* 209 */             .setForward(mc.world.getAllEntities().contains(entity) ? isValid(entity) : false);
/* 210 */           if (entity.ticksExisted < 10 && !entity.getArrowHide().isForward()) {
/*     */             continue;
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 216 */           FixColor color = rock.getFriendsHandler().isFriend(entity.getName().getString()) ? FixColor.GREEN : (rock.getTargetHandler().isTarget(entity.getName().getString()) ? FixColor.RED : FixColor.WHITE);
/* 217 */           double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * partialTicks - projectedView.getX();
/* 218 */           double y = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * partialTicks - projectedView.getY();
/* 219 */           double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * partialTicks - projectedView.getZ();
/* 220 */           Render.setColor(color.alpha(entity.getArrowHide().get()).getRGB());
/* 221 */           BUILDER.begin(3, DefaultVertexFormats.POSITION);
/* 222 */           BUILDER.pos(vec.x, vec.y, vec.z).endVertex();
/* 223 */           BUILDER.pos(x, y, z).endVertex();
/* 224 */           TESSELLATOR.draw();
/* 225 */           if (entity.getArrowHide().finished(false) && !entity.getArrowHide().isForward()) {
/* 226 */             toRemove = entity;
/*     */           }
/*     */         } 
/* 229 */         GL11.glHint(3154, 4352);
/* 230 */         GL11.glDisable(2848);
/* 231 */         GL11.glEnable(3553);
/* 232 */         GL11.glEnable(2929);
/* 233 */         GL11.glDepthMask(true);
/* 234 */         GL11.glDisable(3042);
/* 235 */         GL11.glDisable(2884);
/* 236 */         if (toRemove != null)
/* 237 */           this.entities.remove(toRemove); 
/* 238 */         GL11.glPopMatrix(); }
/*     */ 
/*     */       
/* 241 */       if (event instanceof EventRender3D) { EventRender3D e = (EventRender3D)event;
/* 242 */         if (this.mode.is(this.tracer)) {
/* 243 */           GL11.glRotated((e.getRenderInfo().getYaw() + 180.0F), 0.0D, -1.0D, 0.0D);
/* 244 */           GL11.glRotated(e.getRenderInfo().getPitch(), -1.0D, 0.0D, 0.0D);
/*     */         }  }
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   private FixColor getColor(Entity entity, float angle) {
/* 251 */     if (rock.getFriendsHandler().isFriend(entity))
/* 252 */       return FixColor.GREEN; 
/* 253 */     if (rock.getTargetHandler().isTarget(entity))
/* 254 */       return FixColor.RED; 
/* 255 */     String client = ClientAPI.getClient(entity.getName().getString());
/*     */     
/* 257 */     if (client != null) {
/* 258 */       return GlobalsColors.getColor(client);
/*     */     }
/* 260 */     return Style.getPoint((int)angle);
/*     */   }
/*     */   
/*     */   private boolean isValid(Entity entity) {
/* 264 */     if (entity == mc.player || entity instanceof net.minecraft.client.entity.player.ClientPlayerEntity) {
/* 265 */       return false;
/*     */     }
/* 267 */     if (this.backward.get() || PositionTracker.isInView(entity)) {
/* 268 */       if (entity instanceof net.minecraft.entity.item.ItemEntity && this.items.get())
/* 269 */         return true; 
/* 270 */       if ((entity instanceof net.minecraft.entity.MobEntity || entity instanceof net.minecraft.entity.passive.AnimalEntity) && this.mobs.get())
/* 271 */         return true; 
/* 272 */       if (entity instanceof PlayerEntity) { PlayerEntity player = (PlayerEntity)entity; if (this.players.get() && (player.getTotalArmorValue() != 0 || this.nakedPlayers.get()))
/* 273 */           return true;  }
/* 274 */        if (entity instanceof PlayerEntity) { PlayerEntity player = (PlayerEntity)entity; if (rock.getFriendsHandler().isFriend((Entity)player) && this.friend.get()) return true;  }
/*     */       
/* 276 */       if (entity.getUniqueID().equals(PlayerEntity.getOfflineUUID(entity.getName().getString())) && this.bot.get()) return true;
/*     */     
/*     */     } 
/* 279 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 284 */     this.entities.clear();
/*     */   }
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\render\Arrows.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */