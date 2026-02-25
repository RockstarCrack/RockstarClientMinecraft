/*     */ package fun.rockstarity.client.modules.player;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.inputs.EventInput;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.math.aura.Rotation;
/*     */ import fun.rockstarity.api.helpers.player.InvUtility;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.helpers.render.Render;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.render.color.themes.Style;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.projectile.PotionEntity;
/*     */ import net.minecraft.entity.projectile.ThrowableEntity;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.server.SChatPacket;
/*     */ import net.minecraft.potion.Effect;
/*     */ import net.minecraft.potion.EffectInstance;
/*     */ import net.minecraft.potion.EffectType;
/*     */ import net.minecraft.potion.Effects;
/*     */ import net.minecraft.potion.PotionUtils;
/*     */ import net.minecraft.util.math.BlockRayTraceResult;
/*     */ import net.minecraft.util.math.RayTraceContext;
/*     */ import net.minecraft.util.math.RayTraceResult;
/*     */ import net.minecraft.util.math.vector.Vector3d;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "AutoDodge", desc = "Автоматически уворачивается от плохих зелий", type = Category.PLAYER)
/*     */ public class AutoDodge
/*     */   extends Module
/*     */ {
/*  46 */   Mode mode = new Mode((Bindable)this, "Режим");
/*  47 */   Mode.Element auto = new Mode.Element(this.mode, "Авто");
/*  48 */   Mode.Element dodge = new Mode.Element(this.mode, "Уворот");
/*  49 */   Mode.Element plast = new Mode.Element(this.mode, "Пласт");
/*     */   
/*  51 */   private final Map<Entity, Vector3d> potions = new HashMap<>();
/*  52 */   private final TimerUtility checkTimer = new TimerUtility();
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  56 */     if (Player.isLookEvent(event) || event instanceof fun.rockstarity.api.events.list.player.EventPostMotion) {
/*  57 */       for (Map.Entry<Entity, Vector3d> potion : this.potions.entrySet()) {
/*  58 */         if (this.auto.get()) {
/*  59 */           if (mc.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP) || !this.checkTimer.passed(2000L) || mc.player.getDistance(((Entity)potion.getKey()).getPositionVec()) > 7.0F) {
/*  60 */             if (mc.player.getDistance(potion.getValue()) > 6.0F)
/*     */               continue; 
/*  62 */             if (event instanceof EventInput) { EventInput e = (EventInput)event;
/*  63 */               e.setForward(1.0F);
/*  64 */               e.setJump((!mc.player.isPotionActive(Effects.SPEED) || mc.player.collidedHorizontally));
/*     */               
/*  66 */               if (e.isJump() && mc.player.isPotionActive(Effects.SLOWNESS) && mc.player.getActivePotionEffect(Effects.SLOWNESS).getAmplifier() > mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier()) {
/*  67 */                 e.setJump(true);
/*     */               } }
/*     */ 
/*     */             
/*  71 */             Player.look(event, (Rotation.get((Vector3d)potion.getValue())).x - 180.0F, mc.player.rotationPitch, true); break;
/*     */           } 
/*  73 */           if (mc.player.getDistance(potion.getValue()) > 6.0F)
/*     */             continue; 
/*  75 */           Player.look(event, (Rotation.get(((Entity)potion.getKey()).getPositionVec())).x, (Rotation.get(((Entity)potion.getKey()).getPositionVec())).y, true);
/*     */           
/*  77 */           if (event instanceof fun.rockstarity.api.events.list.player.EventPostMotion && !mc.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP) && mc.player.getDistance(((Entity)potion.getKey()).getPositionVec()) > 3.0F && ((Entity)potion.getKey()).ticksExisted > 1) {
/*  78 */             InvUtility.use(Items.DRIED_KELP);
/*     */           }
/*     */           break;
/*     */         } 
/*  82 */         if (this.dodge.get()) {
/*  83 */           if (mc.player.getDistance(potion.getValue()) > 6.0F)
/*     */             continue; 
/*  85 */           if (event instanceof EventInput) { EventInput e = (EventInput)event;
/*  86 */             e.setForward(1.0F);
/*  87 */             e.setJump((!mc.player.isPotionActive(Effects.SPEED) || mc.player.collidedHorizontally)); }
/*     */ 
/*     */           
/*  90 */           Player.look(event, (Rotation.get((Vector3d)potion.getValue())).x - 180.0F, mc.player.rotationPitch, true);
/*     */         } 
/*     */         
/*  93 */         if (this.plast.get()) {
/*  94 */           if (mc.player.getDistance(potion.getValue()) > 6.0F)
/*     */             continue; 
/*  96 */           Player.look(event, (Rotation.get(((Entity)potion.getKey()).getPositionVec())).x, (Rotation.get(((Entity)potion.getKey()).getPositionVec())).y, true);
/*     */           
/*  98 */           if (event instanceof fun.rockstarity.api.events.list.player.EventPostMotion && !mc.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP) && mc.player.getDistance(((Entity)potion.getKey()).getPositionVec()) > 3.0F && ((Entity)potion.getKey()).ticksExisted > 1) {
/*  99 */             InvUtility.use(Items.DRIED_KELP);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 108 */     if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event; IPacket iPacket = e.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket;
/* 109 */         String message = packet.getChatComponent().getString();
/*     */         
/* 111 */         if (message.contains("Здесь уже стоит трапка/пласт")) {
/* 112 */           this.checkTimer.reset();
/*     */         } }
/*     */        }
/*     */     
/* 116 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 117 */       this.potions.clear();
/*     */       
/* 119 */       for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity ent = objectIterator.next();
/* 120 */         if (ent instanceof PotionEntity) { PotionEntity pot = (PotionEntity)ent;
/* 121 */           BUILDER.begin(1, DefaultVertexFormats.POSITION);
/* 122 */           Vector3d motion = ent.getMotion();
/* 123 */           Vector3d pos = ent.getPositionVec();
/*     */           
/* 125 */           Vector3d startPos = null;
/* 126 */           double distance = 0.0D;
/* 127 */           double ticks = 0.0D;
/*     */           
/* 129 */           for (int i = 0; i < 150; i++) {
/* 130 */             Vector3d prevPos = pos;
/* 131 */             pos = pos.add(motion);
/*     */ 
/*     */ 
/*     */             
/* 135 */             distance += prevPos.distanceTo(pos);
/*     */ 
/*     */             
/* 138 */             Vector3d motion1 = ent.getMotion();
/*     */             
/* 140 */             double speed = Math.hypot(motion1.x, motion1.z);
/* 141 */             ent.getSpeeds().add(Double.valueOf(speed));
/*     */             
/* 143 */             motion = calculateNext(ent, motion);
/*     */             
/* 145 */             if (startPos == null) startPos = prevPos;
/*     */             
/* 147 */             Render.color(Style.getPoint(i * 15).getRGB());
/*     */             
/* 149 */             BUILDER.pos(prevPos.x, prevPos.y, prevPos.z).endVertex();
/*     */ 
/*     */             
/* 152 */             RayTraceContext rayTraceContext = new RayTraceContext(prevPos, pos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, ent);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 161 */             BlockRayTraceResult blockHitResult = mc.world.rayTraceBlocks(rayTraceContext);
/*     */ 
/*     */             
/* 164 */             boolean isLast = (blockHitResult.getType() == RayTraceResult.Type.BLOCK);
/*     */             
/* 166 */             if (isLast) {
/* 167 */               for (EffectInstance eff : PotionUtils.getEffectsFromStack(pot.getItem())) {
/* 168 */                 Effect effect = eff.getPotion();
/* 169 */                 EffectInstance inst = new EffectInstance(effect, i, eff.getAmplifier(), eff.isAmbient(), eff.doesShowParticles());
/*     */                 
/* 171 */                 if (effect.getEffectType() == EffectType.HARMFUL || effect == Effects.JUMP_BOOST) {
/* 172 */                   this.potions.put(ent, blockHitResult.getHitVec());
/*     */                   break;
/*     */                 } 
/*     */               } 
/* 176 */               this.potions.put(ent, blockHitResult.getHitVec());
/*     */               
/* 178 */               pos = blockHitResult.getHitVec();
/*     */             } 
/*     */             
/* 181 */             if (isLast || pos.y < 0.0D)
/* 182 */               break;  ticks++;
/* 183 */             BUILDER.pos(pos.x, pos.y, pos.z).endVertex();
/*     */           } 
/*     */           
/* 186 */           TESSELLATOR.draw(); }
/*     */          }
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Vector3d calculateNext(Entity ent, Vector3d motion) {
/* 195 */     motion = motion.scale(0.99D);
/*     */     
/* 197 */     if (!ent.hasNoGravity()) {
/*     */       
/* 199 */       ThrowableEntity th = (ThrowableEntity)ent; motion.y -= ((ent instanceof ThrowableEntity) ? th.getGravityVelocity() : 0.05F);
/*     */     } 
/*     */     
/* 202 */     return motion;
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\AutoDodge.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */