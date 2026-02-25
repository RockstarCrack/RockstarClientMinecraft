/*     */ package fun.rockstarity.client.modules.move;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.player.EventMotion;
/*     */ import fun.rockstarity.api.events.list.player.EventTrace;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import net.minecraft.block.Blocks;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.util.Direction;
/*     */ import net.minecraft.util.Hand;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.BlockRayTraceResult;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.RayTraceResult;
/*     */ import net.minecraft.util.math.vector.Vector3d;
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
/*     */ @Info(name = "Spider", desc = "Позволяет забраться на стены", type = Category.MOVE)
/*     */ public class Spider
/*     */   extends Module
/*     */ {
/*     */   private int oldRightClickTimer;
/*     */   private int oldPitch;
/*     */   private boolean wasPressed;
/*     */   
/*     */   public int getOldRightClickTimer() {
/*  48 */     return this.oldRightClickTimer; } public int getOldPitch() { return this.oldPitch; }
/*  49 */   public void setOldRightClickTimer(int oldRightClickTimer) { this.oldRightClickTimer = oldRightClickTimer; } public void setOldPitch(int oldPitch) { this.oldPitch = oldPitch; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  57 */   private float pitch = 82.0F;
/*  58 */   private final Mode mode = new Mode((Bindable)this, "Выбор");
/*  59 */   private final Mode.Element matrix = new Mode.Element(this.mode, "Matrix");
/*  60 */   private final Mode.Element grim = new Mode.Element(this.mode, "Grim");
/*  61 */   private final Mode.Element ftBlocks = new Mode.Element(this.mode, "FunTime (Неполн. блоки)");
/*  62 */   private final Mode.Element ftTraps = new Mode.Element(this.mode, "FunTime (Трапки)");
/*  63 */   private final Mode.Element spooky = new Mode.Element(this.mode, "Spooky");
/*     */   
/*  65 */   private final Slider speedMatrix = (new Slider((Bindable)this, "Скорость")).min(1.0F).max(20.0F).inc(1.0F).set(6.0F)
/*  66 */     .hide(() -> Boolean.valueOf(!this.mode.is(this.matrix)));
/*     */   
/*  68 */   private final TimerUtility timer = new TimerUtility();
/*     */   
/*     */   private int ticks;
/*     */ 
/*     */   
/*     */   @NativeInclude
/*     */   public void onEvent(Event event) {
/*  75 */     if (this.spooky.get()) {
/*     */       
/*     */       try {
/*     */ 
/*     */         
/*  80 */         boolean useSlime = false;
/*  81 */         if (Player.isLookEvent(event) || event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/*     */           int i;
/*  83 */           for (i = 0; i < 7 && 
/*  84 */             Player.getBlock(1.0D, i, 0.0D) == Blocks.AIR; i++) {
/*  85 */             if (i == 6) {
/*  86 */               useSlime = true;
/*     */             }
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/*  92 */           for (i = 0; i < 7 && 
/*  93 */             Player.getBlock(0.0D, i, 1.0D) == Blocks.AIR; i++) {
/*  94 */             if (i == 6) {
/*  95 */               useSlime = true;
/*     */             }
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 101 */           for (i = 0; i < 7 && 
/* 102 */             Player.getBlock(-1.0D, i, 0.0D) == Blocks.AIR; i++) {
/* 103 */             if (i == 6) {
/* 104 */               useSlime = true;
/*     */             }
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 110 */           for (i = 0; i < 7 && 
/* 111 */             Player.getBlock(0.0D, i, -1.0D) == Blocks.AIR; i++) {
/* 112 */             if (i == 6) {
/* 113 */               useSlime = true;
/*     */             }
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 119 */           Player.look(event, mc.player.rotationYaw, useSlime ? 51.0F : 82.0F, true);
/*     */         } 
/*     */         
/* 122 */         if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) { RayTraceResult rayTraceResult = mc.getObjectMouseOver(); if (rayTraceResult instanceof BlockRayTraceResult) { BlockRayTraceResult obj = (BlockRayTraceResult)rayTraceResult;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 129 */             Direction dir = Direction.UP;
/* 130 */             Direction dir1 = Direction.fromAngle((mc.player.rotationYaw - 180.0F));
/* 131 */             BlockPos pos = obj.getPos();
/*     */             
/* 133 */             if (mc.world.getBlock(pos) != Blocks.SLIME_BLOCK) {
/* 134 */               pos = pos.add(dir1.getDirectionVec());
/*     */             }
/*     */ 
/*     */ 
/*     */             
/* 139 */             if (mc.gameSettings.keyBindJump.isKeyDown() && useSlime) {
/* 140 */               (mc.player.getMotion()).y = 0.6000000238418579D;
/*     */             }
/*     */             
/* 143 */             if (Player.findItem(Items.SLIME_BLOCK) != -1) {
/* 144 */               mc.player.inventory.currentItem = Player.findItem(Items.SLIME_BLOCK);
/*     */             }
/*     */             
/* 147 */             mc.playerController.func_217292_a(mc.player, mc.world, Hand.MAIN_HAND, obj); }
/*     */            }
/*     */       
/* 150 */       } catch (Exception exception) {}
/*     */     }
/*     */ 
/*     */     
/* 154 */     if (this.ftTraps.get() && 
/* 155 */       event instanceof EventMotion) { EventMotion e = (EventMotion)event;
/* 156 */       if (mc.player.collidedHorizontally && (
/* 157 */         mc.player.isOnGround() || this.timer.passed(300L))) {
/* 158 */         (mc.player.getMotion()).y = 0.41999998688697815D;
/* 159 */         if (mc.player.isOnGround()) this.timer.reset();
/*     */       
/*     */       }  }
/*     */ 
/*     */ 
/*     */     
/* 165 */     if (this.mode.is(this.ftBlocks) && 
/* 166 */       event instanceof EventMotion) { EventMotion e = (EventMotion)event;
/* 167 */       Vector3d pos = mc.player.getPositionVec().add(0.0D, 1.5D, 0.0D);
/* 168 */       AxisAlignedBB hitbox = mc.player.getBoundingBox();
/*     */       
/* 170 */       float off = -0.1F;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 175 */       boolean isAir = (mc.world.getBlockState(new BlockPos(hitbox.minX - off, pos.y, hitbox.minZ - off)).getBlock() != Blocks.AIR || mc.world.getBlockState(new BlockPos(hitbox.maxX + off, pos.y, hitbox.minZ - off)).getBlock() != Blocks.AIR || mc.world.getBlockState(new BlockPos(hitbox.minX - off, pos.y, hitbox.maxZ + off)).getBlock() != Blocks.AIR || mc.world.getBlockState(new BlockPos(hitbox.maxX + off, pos.y, hitbox.maxZ + off)).getBlock() != Blocks.AIR);
/*     */       
/* 177 */       if (isAir && !mc.player.isInWater()) {
/* 178 */         (mc.getGameSettings()).keyBindSneak.setPressed(false);
/* 179 */         if ((mc.getGameSettings()).keyBindJump.isKeyDown()) {
/* 180 */           (mc.player.getMotion()).y = 0.6000000238418579D;
/*     */         }
/* 182 */         e.setGround(true);
/*     */       }  }
/*     */ 
/*     */ 
/*     */     
/* 187 */     if (this.mode.is(this.grim)) {
/* 188 */       if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && Player.getBlock(0.0D, 2.0D, 0.0D) == Blocks.AIR) {
/* 189 */         mc.rightClickMouse();
/*     */         
/* 191 */         if (mc.player.isOnGround()) {
/* 192 */           mc.player.jump();
/*     */         }
/*     */       } 
/*     */       
/* 196 */       if (event instanceof EventMotion) { EventMotion e = (EventMotion)event;
/* 197 */         e.setPitch(this.pitch);
/* 198 */         mc.player.rotationPitchHead = this.pitch; }
/*     */ 
/*     */       
/* 201 */       if (event instanceof EventTrace) { EventTrace e = (EventTrace)event;
/* 202 */         e.setPitch(this.pitch);
/* 203 */         e.cancel(); }
/*     */     
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 210 */     if (this.mode.is(this.matrix) && event instanceof EventMotion) { EventMotion e = (EventMotion)event;
/* 211 */       if (!mc.player.collidedHorizontally) {
/*     */         return;
/*     */       }
/*     */       
/* 215 */       if (this.timer.passed(MathHelper.clamp(500L - (long)this.speedMatrix.get() / 2L * 100L, 0L, 1000L))) {
/*     */         
/* 217 */         mc.player.jump();
/* 218 */         (mc.player.getMotion()).y = 0.41999998688697815D;
/* 219 */         mc.player.collidedVertically = true;
/* 220 */         mc.player.collidedHorizontally = true;
/* 221 */         this.timer.reset();
/* 222 */         e.setGround(true);
/*     */       }  }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 230 */     if (this.mode.is(this.grim)) {
/* 231 */       mc.setRightClickDelayTimer(getOldRightClickTimer());
/* 232 */       (mc.getGameSettings()).keyBindUseItem.setPressed(this.wasPressed);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/* 238 */     if (this.mode.is(this.grim)) {
/* 239 */       setOldRightClickTimer(mc.getRightClickDelayTimer());
/* 240 */       this.wasPressed = (mc.getGameSettings()).keyBindUseItem.isKeyDown();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\Spider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */