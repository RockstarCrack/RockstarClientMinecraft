/*     */ package fun.rockstarity.client.modules.move;
/*     */ 
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.player.EventUpdate;
/*     */ import fun.rockstarity.api.helpers.game.Chat;
/*     */ import fun.rockstarity.api.helpers.math.MathUtility;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.secure.Debugger;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.Objects;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.LivingEntity;
/*     */ import net.minecraft.entity.projectile.FishingBobberEntity;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
/*     */ import net.minecraft.util.Hand;
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
/*     */ @Info(name = "AutoUP", desc = "Подхватывает людей удочкой на /fly", type = Category.MOVE)
/*     */ public class AutoUP
/*     */   extends Module
/*     */ {
/*     */   private Vector3d packetPos;
/*     */   private LivingEntity target;
/*  43 */   private TimerUtility timer = new TimerUtility();
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  47 */     if (event instanceof EventUpdate) { EventUpdate e = (EventUpdate)event;
/*  48 */       boolean elytra = (Player.find(38).getItem() == Items.ELYTRA);
/*  49 */       if (!elytra) {
/*  50 */         Chat.msg("Элитра не надета. Не будем лутать вещи.");
/*  51 */         set(false);
/*     */       } else {
/*  53 */         if (this.target == null || !mc.world.getPlayers().contains(this.target)) {
/*  54 */           this.target = findTarget();
/*     */         }
/*     */         
/*  57 */         boolean containsBobber = false;
/*     */ 
/*     */         
/*  60 */         for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity entity = objectIterator.next();
/*  61 */           if (entity instanceof FishingBobberEntity) { FishingBobberEntity bob = (FishingBobberEntity)entity;
/*  62 */             containsBobber = true; }
/*     */            }
/*     */ 
/*     */         
/*  66 */         if (this.target != null && mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.FishingRodItem) {
/*     */ 
/*     */           
/*  69 */           Vector3d pos = containsBobber ? this.target.getPositionVec().add(0.0D, 25.0D, 0.0D) : this.target.getPositionVec().add(0.0D, 1.0D, 0.0D);
/*     */           
/*  71 */           Debugger.overlay(this.target.getDisplayName().getString() + " - " + this.target.getDisplayName().getString());
/*     */           
/*  73 */           if (this.timer.passed(300L) && mc.player.getDistance(pos) < 1.1F) {
/*  74 */             mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
/*  75 */             mc.player.swingArm(Hand.MAIN_HAND);
/*  76 */             this.timer.reset();
/*     */           } 
/*  78 */           mc.player.setPosition(MathUtility.step((float)(mc.player.getPositionVec()).x, (float)pos.x, 9.0F), MathUtility.step((float)(mc.player.getPositionVec()).y, (float)pos.y, 9.0F), MathUtility.step((float)(mc.player.getPositionVec()).z, (float)pos.z, 9.0F));
/*     */         } 
/*     */       }  }
/*     */   
/*     */   }
/*     */   
/*     */   @NativeInclude
/*     */   private LivingEntity findTarget() {
/*  86 */     ArrayList<LivingEntity> targets = new ArrayList<>();
/*     */     
/*  88 */     for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity entity = objectIterator.next();
/*  89 */       if (entity instanceof LivingEntity) { LivingEntity e = (LivingEntity)entity; if (!(e instanceof net.minecraft.client.entity.player.ClientPlayerEntity) && MathUtility.canSeen(entity.getPositionVec()))
/*  90 */           targets.add(e);  }
/*     */        }
/*     */     
/*  93 */     Objects.requireNonNull(mc.player); targets.sort(Comparator.comparingDouble(mc.player::getDistance));
/*  94 */     return (targets.size() == 0) ? null : targets.get(0);
/*     */   }
/*     */ 
/*     */   
/*     */   @NativeInclude
/*     */   public void onEnable() {
/* 100 */     boolean elytra = (Player.find(38).getItem() == Items.ELYTRA);
/* 101 */     if (!elytra) {
/* 102 */       Chat.msg("Элитра не надета. Не будем лутать вещи.");
/* 103 */       set(false);
/*     */     } 
/*     */     
/* 106 */     mc.player.sendChatMessage("/fly enable");
/* 107 */     this.packetPos = null;
/*     */   }
/*     */ 
/*     */   
/*     */   @NativeInclude
/*     */   public void onDisable() {
/* 113 */     this.target = null;
/*     */   }
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\AutoUP.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */