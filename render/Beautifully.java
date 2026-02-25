/*     */ package fun.rockstarity.client.modules.render;
/*     */ 
/*     */ import com.mojang.blaze3d.matrix.MatrixStack;
/*     */ import com.mojang.blaze3d.systems.RenderSystem;
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.render.EventRender2D;
/*     */ import fun.rockstarity.api.events.list.render.EventRender3D;
/*     */ import fun.rockstarity.api.events.list.render.ui.EventHotbarSlot;
/*     */ import fun.rockstarity.api.events.list.render.ui.shaders.EventBlur;
/*     */ import fun.rockstarity.api.events.list.render.world.EventCamera;
/*     */ import fun.rockstarity.api.events.list.render.world.EventCameraPosition;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.helpers.render.Render;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Position;
/*     */ import fun.rockstarity.api.modules.settings.list.Select;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import fun.rockstarity.api.render.animation.Animation;
/*     */ import fun.rockstarity.api.render.animation.Easing;
/*     */ import fun.rockstarity.api.render.animation.infinity.InfinityAnimation;
/*     */ import fun.rockstarity.api.render.animation.infinity.RotationAnimation;
/*     */ import fun.rockstarity.api.render.color.FixColor;
/*     */ import fun.rockstarity.api.render.color.themes.Style;
/*     */ import fun.rockstarity.api.render.shaders.list.Glass;
/*     */ import fun.rockstarity.api.render.shaders.list.Round;
/*     */ import fun.rockstarity.api.render.ui.rect.Rect;
/*     */ import fun.rockstarity.client.modules.player.FreeCam;
/*     */ import java.awt.Color;
/*     */ import java.util.Arrays;
/*     */ import net.minecraft.client.gui.AbstractGui;
/*     */ import net.minecraft.client.settings.AttackIndicatorStatus;
/*     */ import net.minecraft.entity.LivingEntity;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.util.HandSide;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.vector.Vector2f;
/*     */ import net.minecraft.util.math.vector.Vector3d;
/*     */ import net.minecraft.util.math.vector.Vector3f;
/*     */ import net.optifine.CustomItems;
/*     */ import net.optifine.shaders.Shaders;
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
/*     */ @Info(name = "Beautifully", desc = "Визуально улучшает игру", type = Category.RENDER)
/*     */ public class Beautifully
/*     */   extends Module
/*     */ {
/*  65 */   private final Select utils = new Select((Bindable)this, "Анимация"); public Select getUtils() { return this.utils; }
/*  66 */    private final Select.Element zoom = (new Select.Element(this.utils, "Зум")).set(true); public Select.Element getZoom() { return this.zoom; }
/*  67 */    private final Select.Element tab = (new Select.Element(this.utils, "Таб")).set(true); public Select.Element getTab() { return this.tab; }
/*  68 */    private final Select.Element inventory = (new Select.Element(this.utils, "Инвентарь")).set(true); public Select.Element getInventory() { return this.inventory; }
/*  69 */    private final Select.Element f5 = (new Select.Element(this.utils, "Третье лицо")).set(true); public Select.Element getF5() { return this.f5; }
/*  70 */    private final Select.Element hotBar = (new Select.Element(this.utils, "Хотбар")).set(true); public Select.Element getHotBar() { return this.hotBar; }
/*  71 */    private final Select.Element chat = (new Select.Element(this.utils, "Чат")).set(true); public Select.Element getChat() { return this.chat; }
/*  72 */    private final Select.Element changeWorld = (new Select.Element(this.utils, "Переход между мирами")).set(true); public Select.Element getChangeWorld() { return this.changeWorld; }
/*  73 */    private final Select.Element damage = (new Select.Element(this.utils, "Урон")).set(false); public Select.Element getDamage() { return this.damage; }
/*     */   
/*  75 */   private final Slider zoomSize = (new Slider((Bindable)this, "Сила приближения")).min(1.0F).max(4.0F).inc(0.1F).set(2.0F).hide(() -> Boolean.valueOf(!this.zoom.get())).text(2.0F, "Обыч"); public Slider getZoomSize() { return this.zoomSize; }
/*  76 */    public final CheckBox customF3 = (new CheckBox((Bindable)this, "Кастомный F5")).hide(() -> Boolean.valueOf(!this.f5.get())); public CheckBox getCustomF3() { return this.customF3; }
/*  77 */    public final Position positionF5 = new Position((Bindable)this.customF3, "Позиция"); public Position getPositionF5() { return this.positionF5; }
/*  78 */    public final Slider distance = (new Slider((Bindable)this.customF3, "Дистанция камеры")).min(-3.0F).max(6.0F).inc(0.1F).set(4.0F); public Slider getDistance() { return this.distance; }
/*     */   
/*  80 */   private final Select replace = new Select((Bindable)this, "Заменять"); public Select getReplace() { return this.replace; }
/*  81 */    private final Select.Element crosshair = new Select.Element(this.replace, "Прицел"); public Select.Element getCrosshair() { return this.crosshair; }
/*  82 */    private final Select.Element hotbar = new Select.Element(this.replace, "Хотбар");
/*  83 */   private final Select.Element f3 = new Select.Element(this.replace, "F3"); public Select.Element getF3() { return this.f3; }
/*  84 */    private final Select.Element bars = (new Select.Element(this.replace, "Полоски")).hide(() -> Boolean.valueOf(!this.hotbar.get())); public Select.Element getBars() { return this.bars; }
/*  85 */    private final CheckBox animCross = (new CheckBox((Bindable)this, "Анимировать")).hide(() -> Boolean.valueOf(!this.crosshair.get())); public CheckBox getAnimCross() { return this.animCross; }
/*     */   
/*  87 */   private final Select realistic = new Select((Bindable)this, "Реалистичность"); public Select getRealistic() { return this.realistic; }
/*  88 */    private final Select.Element realCamera = new Select.Element(this.realistic, "Камера"); public Select.Element getRealCamera() { return this.realCamera; }
/*  89 */    private final Select.Element itemPhysics = new Select.Element(this.realistic, "Физика предметов"); public Select.Element getItemPhysics() { return this.itemPhysics; }
/*  90 */    private final Select.Element realBlur = (new Select.Element(this.realistic, "Размытие в движении")).hide(() -> Boolean.valueOf(!((Interface)rock.getModules().get(Interface.class)).getBlur().get())).hide(() -> Boolean.valueOf(true)); public Select.Element getRealBlur() { return this.realBlur; }
/*  91 */    private final Select.Element position = (new Select.Element(this.realistic, ":3")).hide(() -> Boolean.valueOf(!rock.isDebugging())); public Select.Element getPosition() { return this.position; }
/*     */   
/*  93 */   private final Select client = (new Select((Bindable)this, "Клиент")).hide(() -> Boolean.valueOf(!Shaders.shaderPackLoaded)); public Select getClient() { return this.client; }
/*  94 */    private final Select.Element bloom = new Select.Element(this.client, "Свечение 3д визуалов"); public Select.Element getBloom() { return this.bloom; }
/*     */   
/*  96 */   private final Slider aspectRatio = (new Slider((Bindable)this, "Соотношение сторон")).min(0.5F).max(2.0F).inc(0.1F).set(1.0F).desc("Изменяет соотношение сторон").text(1.0F, "Выкл"); public Slider getAspectRatio() { return this.aspectRatio; }
/*     */   
/*  98 */   private final RotationAnimation camera = new RotationAnimation(); public RotationAnimation getCamera() { return this.camera; }
/*     */ 
/*     */ 
/*     */   
/* 102 */   private final Animation[] items = new Animation[9]; public Animation[] getItems() { return this.items; }
/* 103 */    private final InfinityAnimation slot = new InfinityAnimation(); public InfinityAnimation getSlot() { return this.slot; }
/*     */   
/* 105 */   private final InfinityAnimation healthAnim = new InfinityAnimation(); public InfinityAnimation getHealthAnim() { return this.healthAnim; }
/* 106 */    private final InfinityAnimation goldenAnim = new InfinityAnimation(); public InfinityAnimation getGoldenAnim() { return this.goldenAnim; }
/* 107 */    private final InfinityAnimation foodAnim = new InfinityAnimation(); public InfinityAnimation getFoodAnim() { return this.foodAnim; }
/* 108 */    private final InfinityAnimation armorAnim = new InfinityAnimation(); public InfinityAnimation getArmorAnim() { return this.armorAnim; }
/* 109 */    private final InfinityAnimation waterAnim = new InfinityAnimation(); public InfinityAnimation getWaterAnim() { return this.waterAnim; }
/* 110 */    private final InfinityAnimation saturationAnim = new InfinityAnimation(); public InfinityAnimation getSaturationAnim() { return this.saturationAnim; }
/*     */   
/* 112 */   private final InfinityAnimation x = new InfinityAnimation(); public InfinityAnimation getX() { return this.x; }
/* 113 */    private final InfinityAnimation y = new InfinityAnimation(); public InfinityAnimation getY() { return this.y; }
/* 114 */    private final InfinityAnimation z = new InfinityAnimation(); public InfinityAnimation getZ() { return this.z; }
/* 115 */    private final InfinityAnimation yaw = new InfinityAnimation(); public InfinityAnimation getYaw() { return this.yaw; }
/* 116 */    private final InfinityAnimation pitch = new InfinityAnimation(); public InfinityAnimation getPitch() { return this.pitch; }
/*     */   
/* 118 */   private final Animation changeWorldAnim = (new Animation()).setEasing(Easing.EASE_OUT_CIRC).setSpeed(300); public Animation getChangeWorldAnim() { return this.changeWorldAnim; }
/*     */   
/* 120 */   private final Animation hideUI = (new Animation()).setEasing(Easing.BOTH_SINE).setSpeed(300); public Animation getHideUI() { return this.hideUI; }
/*     */ 
/*     */   
/*     */   public Beautifully() {
/* 124 */     set(true);
/* 125 */     Arrays.setAll(this.items, item -> (new Animation()).setEasing(Easing.EASE_OUT_CIRC).setSpeed(300));
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/* 130 */     if (event instanceof EventRender3D) { EventRender3D e = (EventRender3D)event; if (this.bloom.get()) {
/* 131 */         Glass.draw(FixColor.WHITE, 0.0F, 0.0F, 0.0F);
/* 132 */         Glass.end();
/*     */       }  }
/*     */ 
/*     */     
/* 136 */     if (this.changeWorld.get()) {
/* 137 */       if (event instanceof EventCameraPosition) { EventCameraPosition e = (EventCameraPosition)event; if (Player.isInGame()) {
/* 138 */           this.changeWorldAnim.setEasing(Easing.BOTH_CIRC);
/* 139 */           this.changeWorldAnim.setSpeed(300);
/* 140 */           this.changeWorldAnim.setForward(mc.isGameFocused());
/* 141 */           Vector2f rot = e.getRotation();
/* 142 */           e.setRotation(new Vector2f(rot.x, rot.y + (90.0F - rot.y) * (1.0F - this.changeWorldAnim
/*     */                 
/* 144 */                 .get())));
/*     */         }  }
/*     */ 
/*     */       
/* 148 */       if (event instanceof fun.rockstarity.api.events.list.game.EventWorldChange) {
/* 149 */         this.changeWorldAnim.setForward(false);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 154 */     if (event instanceof EventCamera) { EventCamera e = (EventCamera)event;
/* 155 */       MatrixStack ms = e.getStack();
/* 156 */       this.camera.animate(new Vector2f(mc.player.rotationYaw, -mc.player.movementInput.moveStrafe * 20.0F), 60, 450);
/*     */       
/* 158 */       if (this.realCamera.get()) {
/* 159 */         ms.rotate(Vector3f.ZP.rotationDegrees(MathHelper.clamp(this.camera.getPitch(), -20.0F, 20.0F) + MathHelper.clamp((mc.player.rotationYaw - this.camera.getYaw()) / 2.0F, -10.0F, 10.0F)));
/*     */       } }
/*     */ 
/*     */ 
/*     */     
/* 164 */     if (event instanceof EventBlur) { EventBlur e = (EventBlur)event; if (!this.realBlur.get() || !((FreeCam)rock.getModules().get(FreeCam.class)).get()); }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 169 */     if (this.hotbar.get() && (event instanceof EventBlur || event instanceof EventRender2D)) {
/* 170 */       this.hideUI.setForward((!((Interface)rock.getModules().get(Interface.class)).getHideUi().get() || mc.isGameFocused()));
/*     */       
/* 172 */       GL11.glPushMatrix();
/* 173 */       GL11.glTranslated(0.0D, (50.0F - 50.0F * this.hideUI.get()), 0.0D);
/* 174 */       if (event instanceof EventBlur) { EventBlur e = (EventBlur)event;
/* 175 */         renderHotbar(e.getPartialTicks(), e.getMatrixStack(), true); }
/*     */ 
/*     */       
/* 178 */       if (event instanceof EventRender2D) { EventRender2D e = (EventRender2D)event;
/* 179 */         renderHotbar(e.getPartialTicks(), e.getMatrixStack(), false); }
/*     */       
/* 181 */       GL11.glPopMatrix();
/*     */     } 
/*     */ 
/*     */     
/* 185 */     if (event instanceof EventCameraPosition) { EventCameraPosition e = (EventCameraPosition)event; if (this.position.get()) {
/* 186 */         int speed = 1;
/* 187 */         Vector3d pos = e.getPosition();
/* 188 */         e.setPosition(new Vector3d(this.x
/* 189 */               .animate((float)pos.x, speed), this.y
/* 190 */               .animate((float)pos.y, speed), this.z
/* 191 */               .animate((float)pos.z, speed)));
/*     */ 
/*     */         
/* 194 */         speed = 1;
/* 195 */         Vector2f rot = e.getRotation();
/* 196 */         e.setRotation(new Vector2f(this.yaw
/* 197 */               .animate(rot.x, speed), this.pitch
/* 198 */               .animate(rot.y, speed)));
/*     */       }  }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   protected void renderHotbar(float partialTicks, MatrixStack matrixStack, boolean blur) {
/* 205 */     this; this; PlayerEntity playerentity = !(mc.getRenderViewEntity() instanceof PlayerEntity) ? null : (PlayerEntity)mc.getRenderViewEntity();
/* 206 */     if (playerentity != null && !(mc.getGameSettings()).hideGUI) {
/*     */       
/* 208 */       RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 209 */       ItemStack itemstack = playerentity.getHeldItemOffhand();
/* 210 */       HandSide handside = playerentity.getPrimaryHand().opposite();
/* 211 */       Beautifully beautifully = (Beautifully)rock.getModules().get(Beautifully.class);
/* 212 */       mc.ingameGUI.getOpening().setForward((mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen && !rock.isPanic() && beautifully.get() && beautifully.getHotBar().get()));
/* 213 */       int i = sr.getScaledWidth() / 2;
/* 214 */       int j = mc.ingameGUI.getBlitOffset();
/* 215 */       int k = 182;
/* 216 */       int l = 91;
/* 217 */       mc.ingameGUI.setBlitOffset(-90);
/*     */ 
/*     */ 
/*     */       
/* 221 */       float hpRound = 2.0F;
/*     */       
/* 223 */       if (blur) {
/* 224 */         Round.draw(matrixStack, new Rect((i - 91), sr.getScaledHeight() - 26.5F - 14.0F * mc.ingameGUI.getOpening().get(), 182.0F, 21.0F), 3.0F, rock.getThemes().getSecondColor());
/*     */         
/* 226 */         if (mc.playerController.shouldDrawHUD() && this.bars.get()) {
/* 227 */           Round.draw(matrixStack, new Rect((i - 91), (sr.getScaledHeight() - 36) - 14.0F * mc.ingameGUI.getOpening().get(), 85.0F, 7.0F), hpRound, FixColor.RED.move((Color)FixColor.WHITE, 0.5F).move((Color)FixColor.BLACK, 0.7F));
/*     */           
/* 229 */           Round.draw(matrixStack, new Rect((i + 6), (sr.getScaledHeight() - 36) - 14.0F * mc.ingameGUI.getOpening().get(), 85.0F, 7.0F), hpRound, FixColor.YELLOW.move((Color)FixColor.ORANGE, 0.7F).move((Color)FixColor.WHITE, 0.2F).move((Color)FixColor.BLACK, 0.5F));
/*     */         } 
/*     */       } else {
/* 232 */         Render.glow(matrixStack, new Rect((i - 91), sr.getScaledHeight() - 27.5F - 14.0F * mc.ingameGUI.getOpening().get(), 182.0F, 22.0F), 1.0F);
/*     */         
/* 234 */         float alpha = 1.0F;
/*     */         
/* 236 */         if (((Interface)rock.getModules().get(Interface.class)).getBlur().get()) {
/* 237 */           Round.draw(matrixStack, new Rect((i - 91), (sr.getScaledHeight() - 27) - 14.0F * mc.ingameGUI.getOpening().get(), 182.0F, 22.0F), 3.0F, rock.getThemes().getFirstColor().alpha(0.5D));
/*     */           
/* 239 */           Round.draw(matrixStack, new Rect((i - 91 - 1 + 3) + this.slot.animate((playerentity.inventory.currentItem * 20), 50), (sr.getScaledHeight() - 25) - 14.0F * mc.ingameGUI.getOpening().get(), 18.0F, 18.0F), 2.0F, rock.getThemes().getSecondColor().alpha(0.5D));
/*     */           
/* 241 */           if (mc.playerController.shouldDrawHUD() && this.bars.get()) {
/* 242 */             Round.draw(matrixStack, new Rect((i - 91), (sr.getScaledHeight() - 36) - 14.0F * mc.ingameGUI.getOpening().get(), 85.0F, 7.0F), hpRound, FixColor.RED.move((Color)FixColor.WHITE, 0.2F).move((Color)FixColor.BLACK, 0.5F).alpha(0.5D));
/*     */             
/* 244 */             Round.draw(matrixStack, new Rect((i + 6), (sr.getScaledHeight() - 36) - 14.0F * mc.ingameGUI.getOpening().get(), 85.0F, 7.0F), hpRound, FixColor.YELLOW.move((Color)FixColor.ORANGE, 0.7F).move((Color)FixColor.WHITE, 0.2F).move((Color)FixColor.BLACK, 0.5F).alpha(0.5D));
/*     */           } 
/*     */         } else {
/* 247 */           FixColor upLeft = Style.getPoint(0).alpha(alpha);
/* 248 */           FixColor upRight = Style.getPoint(90).alpha(alpha);
/* 249 */           FixColor downLeft = Style.getPoint(180).alpha(alpha);
/* 250 */           FixColor downRight = Style.getPoint(270).alpha(alpha);
/*     */           
/* 252 */           Round.draw(matrixStack, new Rect((i - 91 - 1 + 3) + this.slot.animate((playerentity.inventory.currentItem * 20), 50), (sr.getScaledHeight() - 25) - 14.0F * mc.ingameGUI.getOpening().get(), 18.0F, 18.0F), 2.0F, upLeft, upRight, downLeft, downRight);
/*     */         } 
/*     */         
/* 255 */         Render.outline(matrixStack, new Rect((i - 91), (sr.getScaledHeight() - 27) - 14.0F * mc.ingameGUI.getOpening().get(), 182.0F, 22.0F), 1.0F);
/*     */         
/* 257 */         if (mc.playerController.shouldDrawHUD()) {
/* 258 */           if (this.bars.get()) {
/* 259 */             float healthCoff = this.healthAnim.animate(mc.player.getHealth() / mc.player.getMaxHealth(), 50);
/* 260 */             Round.draw(matrixStack, new Rect((i - 91), (sr.getScaledHeight() - 36) - 14.0F * mc.ingameGUI.getOpening().get(), 85.0F * healthCoff, 7.0F), hpRound, FixColor.RED.move((Color)FixColor.WHITE, 0.2F));
/*     */             
/* 262 */             float goldenCoff = this.goldenAnim.animate(mc.player.getAbsorptionAmount() / 20.0F, 50);
/* 263 */             Round.draw(matrixStack, new Rect((i - 91), (sr.getScaledHeight() - 36) - 14.0F * mc.ingameGUI.getOpening().get(), 85.0F * goldenCoff, 7.0F), hpRound, FixColor.YELLOW.move((Color)FixColor.WHITE, 0.2F));
/*     */             
/* 265 */             float armorCoff = this.armorAnim.animate(playerentity.getTotalArmorValue() / 20.0F, 50);
/* 266 */             Round.draw(matrixStack, new Rect((i - 91), (sr.getScaledHeight() - 45) - 14.0F * mc.ingameGUI.getOpening().get(), 85.0F * armorCoff, 7.0F), hpRound, FixColor.WHITE.move((Color)FixColor.GRAY, 0.2F));
/*     */ 
/*     */             
/* 269 */             float foodCoff = this.foodAnim.animate(mc.player.getFoodStats().getFoodLevel() / 20.0F, 50);
/* 270 */             Round.draw(matrixStack, new Rect((i + 91) - 85.0F * foodCoff, (sr.getScaledHeight() - 36) - 14.0F * mc.ingameGUI.getOpening().get(), foodCoff * 85.0F, 7.0F), hpRound, FixColor.ORANGE);
/*     */             
/* 272 */             float saturationCoff = this.saturationAnim.animate(mc.player.getFoodStats().getSaturationLevel() / 20.0F, 50);
/* 273 */             Round.draw(matrixStack, new Rect((i + 91) - 85.0F * saturationCoff, (sr.getScaledHeight() - 36) - 14.0F * mc.ingameGUI.getOpening().get(), saturationCoff * 85.0F, 7.0F), hpRound, FixColor.YELLOW);
/*     */             
/* 275 */             float waterCoff = this.waterAnim.animate((playerentity.getAir() < playerentity.getMaxAir()) ? (Math.min(playerentity.getAir(), playerentity.getMaxAir()) / playerentity.getMaxAir()) : 0.0F, 50);
/* 276 */             Round.draw(matrixStack, new Rect((i + 91) - 85.0F * waterCoff, (sr.getScaledHeight() - 45) - 14.0F * mc.ingameGUI.getOpening().get(), waterCoff * 85.0F, 7.0F), hpRound, FixColor.BLUE.move((Color)FixColor.WHITE, 0.7F));
/*     */           } 
/*     */           
/* 279 */           this; String exp = "" + mc.player.experienceLevel;
/* 280 */           bold.get(14).draw(matrixStack, exp, sr.getScaledWidth() / 2.0F - bold.get(14).getWidth(exp) / 2.0F - 0.5F, (sr.getScaledHeight() - 37) - 14.0F * mc.ingameGUI.getOpening().get(), FixColor.GREEN.alpha(alpha));
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 286 */       if (!itemstack.isEmpty())
/*     */       {
/* 288 */         if (handside == HandSide.LEFT) {
/*     */           
/* 290 */           if (!blur) {
/* 291 */             Render.glow(matrixStack, new Rect((i - 91 - 28), sr.getScaledHeight() - 27.5F - 14.0F * mc.ingameGUI.getOpening().get(), 22.0F, 22.0F), 1.0F);
/*     */           }
/*     */           
/* 294 */           if (blur) {
/* 295 */             Round.draw(matrixStack, new Rect((i - 91 - 28), sr.getScaledHeight() - 27.5F - 14.0F * mc.ingameGUI.getOpening().get(), 22.0F, 22.0F), 3.0F, rock.getThemes().getSecondColor());
/* 296 */           } else if (((Interface)rock.getModules().get(Interface.class)).getBlur().get()) {
/* 297 */             Round.draw(matrixStack, new Rect((i - 91 - 28), (sr.getScaledHeight() - 27) - 14.0F * mc.ingameGUI.getOpening().get(), 22.0F, 22.0F), 3.0F, rock.getThemes().getFirstColor().alpha(0.5D));
/*     */           } 
/*     */           
/* 300 */           if (!blur) {
/* 301 */             Render.outline(matrixStack, new Rect((i - 91 - 28), (sr.getScaledHeight() - 27) - 14.0F * mc.ingameGUI.getOpening().get() + 50.0F - 50.0F * this.hideUI.get(), 22.0F, 22.0F), 1.0F);
/*     */           
/*     */           }
/*     */         }
/*     */         else {
/*     */           
/* 307 */           if (!blur) {
/* 308 */             Render.glow(matrixStack, new Rect((i + 91 + 6), sr.getScaledHeight() - 27.5F - 14.0F * mc.ingameGUI.getOpening().get(), 22.0F, 22.0F), 1.0F);
/*     */           }
/*     */           
/* 311 */           if (blur) {
/* 312 */             Round.draw(matrixStack, new Rect((i + 91 + 6), sr.getScaledHeight() - 27.5F - 14.0F * mc.ingameGUI.getOpening().get(), 22.0F, 22.0F), 3.0F, rock.getThemes().getSecondColor());
/* 313 */           } else if (((Interface)rock.getModules().get(Interface.class)).getBlur().get()) {
/* 314 */             Round.draw(matrixStack, new Rect((i + 91 + 6), (sr.getScaledHeight() - 27) - 14.0F * mc.ingameGUI.getOpening().get(), 22.0F, 22.0F), 3.0F, rock.getThemes().getFirstColor().alpha(0.5D));
/*     */           } 
/*     */           
/* 317 */           if (!blur) {
/* 318 */             Render.outline(matrixStack, new Rect((i + 91 + 6), (sr.getScaledHeight() - 27) - 14.0F * mc.ingameGUI.getOpening().get() + 50.0F - 50.0F * this.hideUI.get(), 22.0F, 22.0F), 1.0F);
/*     */           }
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/* 324 */       if (blur)
/*     */         return; 
/* 326 */       mc.ingameGUI.setBlitOffset(j);
/* 327 */       RenderSystem.enableRescaleNormal();
/* 328 */       RenderSystem.enableBlend();
/* 329 */       RenderSystem.defaultBlendFunc();
/* 330 */       CustomItems.setRenderOffHand(false);
/* 331 */       int k1 = sr.getScaledHeight() - 16 - 3 - 5;
/* 332 */       for (int i1 = 0; i1 < 9; i1++) {
/*     */         
/* 334 */         int j1 = i - 90 + i1 * 20 + 2;
/* 335 */         (new EventHotbarSlot(matrixStack, partialTicks, j1, (int)(k1 - 14.0F * mc.ingameGUI.getOpening().get()), i1)).hook();
/*     */         
/* 337 */         this.items[i1].setForward((mc.player.inventory.currentItem == i1));
/* 338 */         Render.scale((j1 + 8), ((int)(k1 - 14.0F * mc.ingameGUI.getOpening().get()) + 8), 1.0F - this.items[i1].get() / 5.0F);
/* 339 */         renderHotbarItem(j1, (int)(k1 - 14.0F * mc.ingameGUI.getOpening().get()), partialTicks, playerentity, (ItemStack)playerentity.inventory.mainInventory.get(i1));
/* 340 */         Render.end();
/*     */       } 
/*     */       
/* 343 */       if (!itemstack.isEmpty()) {
/*     */         
/* 345 */         CustomItems.setRenderOffHand(true);
/*     */         
/* 347 */         if (handside == HandSide.LEFT) {
/*     */           
/* 349 */           Rect rect = new Rect((i - 91 - 26), (int)((sr.getScaledHeight() - 25) - 14.0F * mc.ingameGUI.getOpening().get()), 19.0F, 19.0F);
/* 350 */           renderHotbarItem((int)(rect.getX() + (rect.getWidth() - 16.0F) / 2.0F), (int)(rect.getY() + (rect.getHeight() - 16.0F) / 2.0F), partialTicks, playerentity, itemstack);
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 355 */           Rect rect = new Rect((i + 91 + 8), (int)((sr.getScaledHeight() - 25) - 14.0F * mc.ingameGUI.getOpening().get()), 19.0F, 19.0F);
/* 356 */           renderHotbarItem((int)(rect.getX() + (rect.getWidth() - 16.0F) / 2.0F), (int)(rect.getY() + (rect.getHeight() - 16.0F) / 2.0F), partialTicks, playerentity, itemstack);
/*     */         } 
/*     */         
/* 359 */         CustomItems.setRenderOffHand(false);
/*     */       } 
/*     */       
/* 362 */       this; if ((mc.getGameSettings()).attackIndicator == AttackIndicatorStatus.HOTBAR) {
/*     */         
/* 364 */         this; float f = mc.player.getCooledAttackStrength(0.0F);
/*     */         
/* 366 */         if (f < 1.0F) {
/*     */           
/* 368 */           int j2 = sr.getScaledHeight() - 20;
/* 369 */           int k2 = i + 91 + 6;
/*     */           
/* 371 */           if (handside == HandSide.RIGHT)
/*     */           {
/* 373 */             k2 = i - 91 - 22;
/*     */           }
/*     */           
/* 376 */           this; mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
/* 377 */           int l1 = (int)(f * 19.0F);
/* 378 */           RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 384 */       RenderSystem.disableRescaleNormal();
/* 385 */       RenderSystem.disableBlend();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void renderHotbarItem(int x, int y, float partialTicks, PlayerEntity player, ItemStack stack) {
/* 391 */     if (stack.getItem() == Items.SLIME_BLOCK);
/*     */ 
/*     */ 
/*     */     
/* 395 */     if (!stack.isEmpty()) {
/*     */       
/* 397 */       float f = stack.getAnimationsToGo() - partialTicks;
/*     */       
/* 399 */       if (f > 0.0F) {
/*     */         
/* 401 */         RenderSystem.pushMatrix();
/* 402 */         float f1 = 1.0F + f / 5.0F;
/* 403 */         RenderSystem.translatef((x + 8), (y + 12), 0.0F);
/* 404 */         RenderSystem.scalef(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
/* 405 */         RenderSystem.translatef(-(x + 8), -(y + 12), 0.0F);
/*     */       } 
/*     */       
/* 408 */       mc.getItemRenderer().renderItemAndEffectIntoGUI((LivingEntity)player, stack, x, y);
/*     */       
/* 410 */       if (f > 0.0F)
/*     */       {
/* 412 */         RenderSystem.popMatrix();
/*     */       }
/*     */       
/* 415 */       this; mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, stack, x, y);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean hotbar() {
/* 420 */     return (get() && this.hotbar.get());
/*     */   }
/*     */   
/*     */   public boolean bars() {
/* 424 */     return (get() && this.hotbar.get() && this.bars.get());
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\render\Beautifully.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */