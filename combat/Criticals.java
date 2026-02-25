/*     */ package fun.rockstarity.client.modules.combat;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.EventType;
/*     */ import fun.rockstarity.api.events.list.player.EventAttack;
/*     */ import fun.rockstarity.api.events.list.player.EventMotion;
/*     */ import fun.rockstarity.api.helpers.player.Bypass;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.render.ui.alerts.AlertType;
/*     */ import fun.rockstarity.api.render.ui.alerts.Tooltip;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CPlayerPacket;
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
/*     */ @Info(name = "Criticals", desc = "Бьет критами на земле", type = Category.COMBAT)
/*     */ public class Criticals
/*     */   extends Module
/*     */ {
/*  50 */   private final Mode mode = new Mode((Bindable)this, "Режим"); public Mode getMode() { return this.mode; }
/*     */   
/*  52 */   private final Mode.Element vanila = new Mode.Element(this.mode, "Обычные"); public Mode.Element getVanila() { return this.vanila; }
/*  53 */    private final Mode.Element ncp = new Mode.Element(this.mode, "NCP"); public Mode.Element getNcp() { return this.ncp; }
/*  54 */    private final Mode.Element funtime = new Mode.Element(this.mode, "RWCollision"); public Mode.Element getFuntime() { return this.funtime; }
/*  55 */    private final Mode.Element grim = new Mode.Element(this.mode, "Grim"); public Mode.Element getGrim() { return this.grim; }
/*  56 */    private final Mode.Element spooky = new Mode.Element(this.mode, "SpookyTime"); public Mode.Element getSpooky() { return this.spooky; }
/*     */ 
/*     */   
/*     */   @EventType({EventAttack.class})
/*     */   public void onEvent(Event event) {
/*  61 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && mc.player.ticksExisted > 100 && (
/*  62 */       this.mode.is(this.grim) || this.mode.is(this.spooky)) && !Bypass.via()) {
/*  63 */       rock.getAlertHandler().alert(Tooltip.create("Этот Criticals работает только с VIA 1.17+"), AlertType.INFO);
/*     */     }
/*     */ 
/*     */     
/*  67 */     if (event instanceof EventAttack) { EventAttack e = (EventAttack)event;
/*  68 */       EventMotion eventMotion = (new EventMotion(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.isOnGround())).hook();
/*  69 */       Vector3d pos = mc.player.getPositionVec();
/*     */       
/*  71 */       if (!canCritical())
/*     */         return; 
/*  73 */       if (this.mode.is(this.grim))
/*  74 */       { if (!mc.player.isOnGround() && 
/*  75 */           Bypass.via()) {
/*  76 */           mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y - 1.0E-6D, pos.z, eventMotion.getYaw(), eventMotion.getPitch(), false));
/*  77 */           mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y + 1.0E-6D, pos.z, eventMotion.getYaw(), eventMotion.getPitch(), false));
/*  78 */           mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y, pos.z, eventMotion.getYaw(), eventMotion.getPitch(), false));
/*     */         }
/*     */          }
/*  81 */       else if (this.mode.is(this.funtime))
/*  82 */       { if (mc.player.hurtTime == 0) {
/*  83 */           mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y + 1.0E-6D, pos.z, eventMotion.getYaw(), eventMotion.getPitch(), true));
/*  84 */           mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y, pos.z, eventMotion.getYaw(), eventMotion.getPitch(), false));
/*     */         }  }
/*  86 */       else { if (this.mode.is(this.spooky)) {
/*  87 */           if (Bypass.via())
/*  88 */             if (!mc.player.isOnGround()) {
/*  89 */               mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y - 1.0E-6D, pos.z, eventMotion.getYaw(), eventMotion.getPitch(), false));
/*     */             } else {
/*     */               
/*  92 */               mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y + 1.0E-6D, pos.z, eventMotion.getYaw(), eventMotion.getPitch(), false));
/*  93 */               mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y, pos.z, eventMotion.getYaw(), eventMotion.getPitch(), false));
/*     */             }  
/*     */           return;
/*     */         } 
/*  97 */         if (this.mode.is(this.vanila)) {
/*  98 */           if (!mc.player.isOnGround()) {
/*  99 */             mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y - 1.0E-6D, pos.z, eventMotion.getYaw(), eventMotion.getPitch(), false));
/*     */           } else {
/* 101 */             mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y + 1.0E-6D, pos.z, eventMotion.getYaw(), eventMotion.getPitch(), true));
/* 102 */             mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y, pos.z, eventMotion.getYaw(), eventMotion.getPitch(), false));
/*     */           } 
/*     */           return;
/*     */         } 
/* 106 */         mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y + 2.71875E-7D, pos.z, eventMotion
/* 107 */               .getYaw(), eventMotion.getPitch(), false));
/* 108 */         mc.player.connection.sendPacket((IPacket)new CPlayerPacket.PositionRotationPacket(pos.x, pos.y, pos.z, eventMotion
/* 109 */               .getYaw(), eventMotion.getPitch(), false));
/*     */         
/*     */         return; }
/*     */       
/* 113 */       mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND)); }
/*     */   
/*     */   }
/*     */   
/*     */   public boolean canCritical() {
/* 118 */     return (get() && ((this.mode.is(this.funtime) && mc.player.isOnGround()) || this.mode.is(this.vanila) || (Bypass.via() && this.mode.is(this.spooky)) || (!mc.player.isOnGround() && this.mode.is(this.grim) && Bypass.via())));
/*     */   }
/*     */   
/*     */   @NativeInclude
/*     */   public void onDisable() {}
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\Criticals.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */