/*    */ package fun.rockstarity.client.modules.player;
/*    */ 
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.events.list.player.EventMotion;
/*    */ import fun.rockstarity.api.helpers.math.MathUtility;
/*    */ import fun.rockstarity.api.helpers.math.aura.AuraUtility;
/*    */ import fun.rockstarity.api.helpers.math.aura.IdealHitUtility;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.client.modules.other.Baritone;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.entity.LivingEntity;
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.util.Hand;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "CreeperFarm", desc = "Автоматический фарм на криперах. Под анархии фт/спуки и т.п.", type = Category.PLAYER)
/*    */ public class AutoCreeperFarm
/*    */   extends Module
/*    */ {
/*    */   private boolean retreating;
/*    */   private long lastHitTime;
/*    */   
/*    */   public void onEvent(Event event) {
/* 42 */     LivingEntity livingEntity = AuraUtility.calculateCreeper(50.0D);
/*    */     
/* 44 */     if (event instanceof EventMotion) { EventMotion e = (EventMotion)event;
/* 45 */       long now = System.currentTimeMillis();
/*    */       
/* 47 */       if (this.retreating) {
/* 48 */         double dx = mc.player.getPosX() - livingEntity.getPosX();
/* 49 */         double dz = mc.player.getPosZ() - livingEntity.getPosZ();
/* 50 */         double dist = Math.sqrt(dx * dx + dz * dz);
/* 51 */         if (dist > 6.0D || now - this.lastHitTime > 300L) {
/* 52 */           this.retreating = false;
/*    */         } else {
/* 54 */           (mc.player.getMotion()).x = dx / dist * 0.3D;
/* 55 */           (mc.player.getMotion()).z = dz / dist * 0.3D;
/*    */           
/*    */           return;
/*    */         } 
/*    */       } 
/* 60 */       boolean canSee = MathUtility.rayTraceWithBlock(3.0D, e
/* 61 */           .getYaw(), e.getPitch(), (Entity)mc.player, (Entity)livingEntity, false);
/*    */       
/* 63 */       boolean inRange = (AuraUtility.distanceTo(AuraUtility.getPoint(livingEntity)) <= 3.0D && mc.player.getDistance((Entity)livingEntity) <= 6.0F);
/*    */       
/* 65 */       if (((!mc.gameSettings.keyBindJump.isKeyDown() && mc.player.isOnGround()) || mc.player.fallDistance > 0.0F) && mc.player
/* 66 */         .getCooledAttackStrength() >= IdealHitUtility.getAICooldown() && inRange && canSee) {
/*    */         
/* 68 */         mc.playerController.attackEntity((PlayerEntity)mc.player, (Entity)livingEntity);
/* 69 */         mc.player.swingArm(Hand.MAIN_HAND);
/* 70 */         this.lastHitTime = now;
/* 71 */         this.retreating = true;
/*    */       }  }
/*    */   
/*    */   }
/*    */ 
/*    */   
/*    */   public void onEnable() {
/* 78 */     ((Baritone)rock.getModules().get(Baritone.class)).set(true);
/* 79 */     mc.player.sendChatMessage("#follow entity creeper");
/* 80 */     mc.player.sendChatMessage("#allowBreak false");
/*    */   }
/*    */ 
/*    */   
/*    */   public void onDisable() {
/* 85 */     mc.player.sendChatMessage("#stop");
/*    */   }
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\AutoCreeperFarm.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */