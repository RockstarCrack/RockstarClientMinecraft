/*     */ package fun.rockstarity.client.modules.player;
/*     */ 
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.EventSpawn;
/*     */ import fun.rockstarity.api.events.list.player.EventMotion;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.client.modules.combat.Aura;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.util.Comparator;
/*     */ import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CAnimateHandPacket;
/*     */ import net.minecraft.network.play.client.CHeldItemChangePacket;
/*     */ import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
/*     */ import net.minecraft.util.Hand;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.BlockRayTraceResult;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.RayTraceContext;
/*     */ import net.minecraft.util.math.RayTraceResult;
/*     */ import net.minecraft.util.math.vector.Vector3d;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "AutoPearl", desc = "Кидает перл за таргетом", type = Category.PLAYER)
/*     */ public class AutoPearl
/*     */   extends Module
/*     */ {
/*     */   private Runnable postSyncAction;
/*     */   private BlockPos targetBlock;
/*     */   private int lastPearlId;
/*     */   private int lastOurPearlId;
/*     */   private float rotationYaw;
/*     */   private float rotationPitch;
/*  45 */   private final TimerUtility delayTimer = new TimerUtility();
/*     */   
/*     */   private int tick;
/*     */   
/*     */   public int getTick() {
/*  50 */     return this.tick;
/*     */   }
/*     */   public AutoPearl() {
/*  53 */     super(11);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  58 */     if (this.tick > 1) {
/*  59 */       Player.look(event, this.rotationYaw, this.rotationPitch, false);
/*     */     }
/*  61 */     if (event instanceof EventSpawn) { EventSpawn e = (EventSpawn)event;
/*  62 */       if (e.getEntity() instanceof net.minecraft.entity.item.EnderPearlEntity)
/*  63 */         mc.world.getPlayers().stream()
/*  64 */           .min(Comparator.comparingDouble(p -> p.getDistanceSq(e.getEntity().getPositionVec())))
/*  65 */           .ifPresent(player -> {
/*     */               if (player.equals(mc.player)) {
/*     */                 this.lastOurPearlId = e.getEntity().getEntityId();
/*     */               }
/*     */             });  }
/*     */     
/*  71 */     if (event instanceof EventMotion) { EventMotion e = (EventMotion)event; if (((Aura)rock.getModules().get(Aura.class)).getPrevTarget() != null) {
/*  72 */         if (this.postSyncAction != null) {
/*  73 */           if (this.tick > 2) {
/*  74 */             this.postSyncAction.run();
/*  75 */             this.postSyncAction = null;
/*  76 */             this.tick = 0;
/*     */           } else {
/*  78 */             this.tick++;
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*  83 */         if (mc.player.getHealth() < 5.0F) {
/*     */           return;
/*     */         }
/*     */         
/*  87 */         if (!this.delayTimer.passed(1000L)) {
/*     */           return;
/*     */         }
/*  90 */         for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity ent = objectIterator.next();
/*  91 */           if (!(ent instanceof net.minecraft.entity.item.EnderPearlEntity) || 
/*  92 */             ent.getEntityId() == this.lastPearlId || ent.getEntityId() == this.lastOurPearlId)
/*  93 */             continue;  mc.world.getPlayers().stream()
/*  94 */             .filter(p -> (p == ((Aura)rock.getModules().get(Aura.class)).getTarget() || p == ((Aura)rock.getModules().get(Aura.class)).getPrevTarget()))
/*  95 */             .min(Comparator.comparingDouble(p -> p.getDistanceSq(ent.getPositionVec())))
/*  96 */             .ifPresent(player -> {
/*     */                 if (!player.equals(mc.player)) {
/*     */                   this.targetBlock = calcTrajectory(ent);
/*     */                   
/*     */                   this.lastPearlId = ent.getEntityId();
/*     */                 } 
/*     */               }); }
/*     */ 
/*     */         
/* 105 */         if (this.targetBlock == null) {
/*     */           return;
/*     */         }
/*     */         
/* 109 */         if (mc.player.getDistance(this.targetBlock.getVec()) < 9.0F) {
/*     */           return;
/*     */         }
/* 112 */         this.rotationPitch = (float)-Math.toDegrees(calcTrajectory(this.targetBlock));
/* 113 */         this.rotationYaw = (float)Math.toDegrees(Math.atan2((this.targetBlock.getZ() + 0.5F) - mc.player.getPosZ(), (this.targetBlock.getX() + 0.5F) - mc.player.getPosX())) - 90.0F;
/* 114 */         BlockPos tracedBP = checkTrajectory(this.rotationYaw, this.rotationPitch);
/*     */         
/* 116 */         if (tracedBP == null || this.targetBlock.distanceSq(tracedBP.getVec()) > 36.0D) {
/*     */           return;
/*     */         }
/* 119 */         this.tick++;
/*     */         
/* 121 */         this.postSyncAction = (() -> {
/*     */             int epSlot = findEPSlot();
/*     */             int originalSlot = mc.player.inventory.currentItem;
/*     */             if (epSlot != -1) {
/*     */               mc.player.inventory.currentItem = epSlot;
/*     */               mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(epSlot));
/*     */               mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
/*     */               mc.player.connection.sendPacket((IPacket)new CAnimateHandPacket(Hand.MAIN_HAND));
/*     */               mc.player.inventory.currentItem = originalSlot;
/*     */               mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(originalSlot));
/*     */             } 
/*     */           });
/* 133 */         this.targetBlock = null;
/* 134 */         this.delayTimer.reset();
/*     */       }  }
/*     */   
/*     */   }
/*     */   private int findEPSlot() {
/* 139 */     int epSlot = -1;
/* 140 */     if (mc.player.getHeldItemMainhand().getItem() == Items.ENDER_PEARL)
/* 141 */       epSlot = mc.player.inventory.currentItem; 
/* 142 */     if (epSlot == -1)
/* 143 */       for (int l = 0; l < 9; l++) {
/* 144 */         if (mc.player.inventory.getStackInSlot(l).getItem() == Items.ENDER_PEARL) {
/* 145 */           epSlot = l; break;
/*     */         } 
/*     */       }  
/* 148 */     return epSlot;
/*     */   }
/*     */   
/*     */   private float calcTrajectory(BlockPos bp) {
/* 152 */     double a = Math.hypot((bp.getX() + 0.5F) - mc.player.getPosX(), (bp.getZ() + 0.5F) - mc.player.getPosZ());
/* 153 */     double y = 6.125D * ((bp.getY() + 1.0F) - mc.player.getPosY() + mc.player.getEyeHeight(mc.player.getPose()));
/* 154 */     y = 0.05000000074505806D * (0.05000000074505806D * a * a + y);
/* 155 */     y = Math.sqrt(9.37890625D - y);
/* 156 */     double d = 3.0625D - y;
/* 157 */     y = Math.atan2(d * d + y, 0.05000000074505806D * a);
/* 158 */     d = Math.atan2(d, 0.05000000074505806D * a);
/* 159 */     return (float)Math.min(y, d);
/*     */   }
/*     */   
/*     */   private BlockPos calcTrajectory(Entity e) {
/* 163 */     return traceTrajectory(e.getPosX(), e.getPosY(), e.getPosZ(), (e.getMotion()).x, (e.getMotion()).y, (e.getMotion()).z);
/*     */   }
/*     */   
/*     */   private BlockPos checkTrajectory(float yaw, float pitch) {
/* 167 */     if (Float.isNaN(pitch))
/* 168 */       return null; 
/* 169 */     float yawRad = yaw / 180.0F * 3.1415927F;
/* 170 */     float pitchRad = pitch / 180.0F * 3.1415927F;
/* 171 */     double x = mc.player.getPosX() - (MathHelper.cos(yawRad) * 0.16F);
/* 172 */     double y = mc.player.getPosY() + mc.player.getEyeHeight(mc.player.getPose()) - 0.1000000014901161D;
/* 173 */     double z = mc.player.getPosZ() - (MathHelper.sin(yawRad) * 0.16F);
/* 174 */     double motionX = (-MathHelper.sin(yawRad) * MathHelper.cos(pitchRad) * 0.4F);
/* 175 */     double motionY = (-MathHelper.sin(pitchRad) * 0.4F);
/* 176 */     double motionZ = (MathHelper.cos(yawRad) * MathHelper.cos(pitchRad) * 0.4F);
/* 177 */     float distance = MathHelper.sqrt((float)(motionX * motionX + motionY * motionY + motionZ * motionZ));
/* 178 */     motionX /= distance;
/* 179 */     motionY /= distance;
/* 180 */     motionZ /= distance;
/* 181 */     motionX *= 1.5D;
/* 182 */     motionY *= 1.5D;
/* 183 */     motionZ *= 1.5D;
/* 184 */     if (!mc.player.isOnGround()) motionY += mc.player.getMotion().getY(); 
/* 185 */     return traceTrajectory(x, y, z, motionX, motionY, motionZ);
/*     */   }
/*     */ 
/*     */   
/*     */   private BlockPos traceTrajectory(double x, double y, double z, double mx, double my, double mz) {
/* 190 */     for (int i = 0; i < 300; i++) {
/* 191 */       Vector3d lastPos = new Vector3d(x, y, z);
/* 192 */       x += mx;
/* 193 */       y += my;
/* 194 */       z += mz;
/* 195 */       mx *= 0.99D;
/* 196 */       my *= 0.99D;
/* 197 */       mz *= 0.99D;
/* 198 */       my -= 0.029999999329447746D;
/* 199 */       Vector3d pos = new Vector3d(x, y, z);
/* 200 */       BlockRayTraceResult bhr = mc.world.rayTraceBlocks(new RayTraceContext(lastPos, pos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, (Entity)mc.player));
/* 201 */       if (bhr != null && bhr.getType() == RayTraceResult.Type.BLOCK) return bhr.getPos();
/*     */       
/* 203 */       for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity ent = objectIterator.next();
/* 204 */         if (!(ent instanceof net.minecraft.entity.projectile.ArrowEntity) && ent != mc.player && !(ent instanceof net.minecraft.entity.item.EnderPearlEntity) && 
/* 205 */           ent.getBoundingBox().intersects(new AxisAlignedBB(x - 0.3D, y - 0.3D, z - 0.3D, x + 0.3D, y + 0.3D, z + 0.2D))) {
/* 206 */           return null;
/*     */         } }
/*     */       
/* 209 */       if (y <= -65.0D)
/*     */         break; 
/* 211 */     }  return null;
/*     */   }
/*     */   
/*     */   public void onEnable() {}
/*     */   
/*     */   public void onDisable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\AutoPearl.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */