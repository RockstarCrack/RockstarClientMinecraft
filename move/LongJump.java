/*     */ package fun.rockstarity.client.modules.move;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.helpers.player.Move;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import net.minecraft.block.Blocks;
/*     */ import net.minecraft.entity.Entity;
/*     */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "LongJump", desc = "Прыжки на дальние дистанции", type = Category.MOVE)
/*     */ public class LongJump
/*     */   extends Module
/*     */ {
/*     */   private double pol;
/*     */   private boolean boost;
/*     */   
/*     */   public double getPol() {
/*  25 */     return this.pol; } public boolean isBoost() {
/*  26 */     return this.boost;
/*     */   }
/*  28 */   private final Mode mode = new Mode((Bindable)this, "Режим"); public Mode getMode() { return this.mode; }
/*     */   
/*  30 */   private final Mode.Element ft = new Mode.Element(this.mode, "FunTime"); public Mode.Element getFt() { return this.ft; }
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  34 */     if (this.mode.is(this.ft) && 
/*  35 */       event instanceof fun.rockstarity.api.events.list.player.EventUpdate && Move.isMoving()) {
/*  36 */       if (mc.player.isOnGround()) {
/*  37 */         this.boost = false;
/*     */       }
/*     */       
/*  40 */       if (mc.player.isOnGround() && Player.getBlock(0.0D, -1.0D, 0.0D) == Blocks.AIR && (
/*  41 */         mc.player.isOnGround() || (mc.player.fallDistance < mc.player.stepHeight && !mc.world.hasNoCollisions((Entity)mc.player, mc.player.getBoundingBox().offset(0.0D, (mc.player.fallDistance - mc.player.stepHeight), 0.0D))))) {
/*  42 */         mc.player.jump();
/*     */         
/*  44 */         this.boost = true;
/*     */       } 
/*     */ 
/*     */       
/*  48 */       if (this.boost && mc.player.fallDistance < 0.5F)
/*     */       {
/*  50 */         mc.player.setMotion(mc.player.getMotion().mul(1.0499999523162842D, 1.0D, 1.0499999523162842D));
/*     */       }
/*     */     } 
/*     */   }
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
/*     */   @NativeInclude
/*     */   public void onEnable() {
/* 108 */     this.pol = mc.player.rotationPitch;
/*     */   }
/*     */   
/*     */   @NativeInclude
/*     */   public void onDisable() {
/* 113 */     (mc.getGameSettings()).keyBindSneak.setPressed(false);
/*     */   }
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\LongJump.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */