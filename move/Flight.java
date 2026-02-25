/*     */ package fun.rockstarity.client.modules.move;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*     */ import fun.rockstarity.api.helpers.game.Chat;
/*     */ import fun.rockstarity.api.helpers.game.Server;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Bypass;
/*     */ import fun.rockstarity.api.helpers.player.Inventory;
/*     */ import fun.rockstarity.api.helpers.player.Move;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import fun.rockstarity.api.render.ui.alerts.AlertType;
/*     */ import io.netty.util.internal.ConcurrentSet;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Set;
/*     */ import net.minecraft.enchantment.EnchantmentHelper;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.inventory.container.ClickType;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CEntityActionPacket;
/*     */ import net.minecraft.network.play.client.CPlayerPacket;
/*     */ import net.minecraft.network.play.server.SChatPacket;
/*     */ import net.minecraft.util.math.vector.Vector3d;
/*     */ import net.minecraft.util.text.TextFormatting;
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
/*     */ 
/*     */ @NativeInclude
/*     */ @Info(name = "Flight", desc = "Симулятор птички", type = Category.MOVE)
/*     */ public class Flight
/*     */   extends Module
/*     */ {
/*     */   private boolean elytras;
/*     */   private ItemStack old;
/*     */   private boolean jump;
/*  69 */   private Set<CPlayerPacket> packetFlyPackets = (Set<CPlayerPacket>)new ConcurrentSet();
/*  70 */   private final Mode mode = new Mode((Bindable)this, "Режим"); public Mode getMode() { return this.mode; }
/*     */   
/*  72 */   private final Mode.Element elytraY = new Mode.Element(this.mode, "Spooky Y");
/*  73 */   private final Mode.Element glide = new Mode.Element(this.mode, "Парение");
/*  74 */   private final Mode.Element trident = new Mode.Element(this.mode, "Трезубец");
/*  75 */   private final Mode.Element packet = new Mode.Element(this.mode, "Packet");
/*     */ 
/*     */   
/*  78 */   private final Slider speed = (new Slider((Bindable)this, "Скорость")).min(0.5F).max(5.0F).inc(0.5F).set(1.0F)
/*  79 */     .hide(() -> Boolean.valueOf(!this.mode.is(this.glide)));
/*     */ 
/*     */ 
/*     */   
/*  83 */   private final ArrayList<IPacket> packets = new ArrayList<>();
/*  84 */   private final TimerUtility timer = new TimerUtility();
/*  85 */   private final TimerUtility placeTimer = new TimerUtility();
/*     */   int ticks;
/*     */   private int oldSlot;
/*  88 */   private final TimerUtility timers = new TimerUtility();
/*     */ 
/*     */   
/*     */   public void onAllEvent(Event event) {
/*  92 */     if (this.elytraY.get() && event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/*  93 */       IPacket iPacket = e.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket;
/*  94 */         String m = packet.getChatComponent().getString();
/*  95 */         if (m.contains("Вы не можете надевать элитры в PvP")) {
/*  96 */           e.cancel();
/*     */         } }
/*     */        }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/* 104 */     if (!this.mode.is(this.elytraY) || !(event instanceof fun.rockstarity.api.events.list.player.EventUpdate) || mc.player.isElytraFlying());
/*     */ 
/*     */ 
/*     */     
/* 108 */     if (this.elytraY.get()) {
/* 109 */       if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 110 */         if (!Server.hasCT()) {
/* 111 */           rock.getAlertHandler().alert("Нужно находиться в PVP!", AlertType.ERROR);
/* 112 */           set(false);
/*     */           
/*     */           return;
/*     */         } 
/* 116 */         this.ticks = 0;
/* 117 */         while (this.ticks < 9) {
/* 118 */           if (Spider.mc.player.inventory.getStackInSlot(this.ticks).getItem() == Items.ELYTRA && !Spider.mc.player.isOnGround() && !mc.player.isInWater() && mc.gameSettings.keyBindJump.isKeyDown() && Spider.mc.player.fallDistance == 0.0F) {
/* 119 */             Spider.mc.playerController.windowClick(0, 6, this.ticks, ClickType.SWAP, (PlayerEntity)Spider.mc.player);
/* 120 */             Spider.mc.player.connection.sendPacket((IPacket)new CEntityActionPacket((Entity)Spider.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
/* 121 */             (Spider.mc.player.getMotion()).y = 0.366D;
/* 122 */             Spider.mc.playerController.windowClick(0, 6, this.ticks, ClickType.SWAP, (PlayerEntity)Spider.mc.player);
/* 123 */             this.oldSlot = this.ticks;
/*     */           } 
/* 125 */           this.ticks++;
/*     */         } 
/*     */       } 
/*     */       
/* 129 */       Player.look(event, mc.player.rotationYaw, 0.0F, true);
/*     */     } 
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
/* 226 */     if (event instanceof fun.rockstarity.api.events.list.player.EventMotion) {
/* 227 */       if (this.mode.is(this.glide)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 235 */         mc.player.setPosition(mc.player.getPosX(), mc.player.getPosY() + 0.029999999329447746D, mc.player.getPosZ());
/* 236 */         Bypass.send(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), mc.player.rotationYaw, mc.player.rotationPitch, false);
/*     */       }
/* 238 */       else if (this.mode.is(this.packet)) {
/*     */         
/* 240 */         double speed = (mc.player.movementInput.jump && !Move.isMoving()) ? 0.062D : (mc.player.movementInput.sneaking ? -0.82D : (mc.player.movementInput.jump ? 1.0D : 0.0D));
/* 241 */         double[] strafing = Move.getSpeed(0.17299999296665192D);
/* 242 */         for (int i = 1; i < 2; i++) {
/* 243 */           mc.player.setVelocity(strafing[0] * i * 1.5D, speed * i, strafing[1] * i * 1.5D);
/* 244 */           sendPackets((mc.player.getMotion()).x, (mc.player.getMotion()).y, (mc.player.getMotion()).z);
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 252 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && 
/* 253 */       this.mode.is(this.trident)) {
/* 254 */       if (Player.findItem(41, Items.TRIDENT) == -1) {
/* 255 */         Chat.msg(String.valueOf(TextFormatting.RED) + "Для использования этого флая нужен трезубец!");
/* 256 */         toggle();
/*     */         return;
/*     */       } 
/* 259 */       if (mc.player.isWet() && (
/* 260 */         EnchantmentHelper.getRiptideModifier(mc.player.getHeldItemMainhand()) > 0 || 
/* 261 */         EnchantmentHelper.getRiptideModifier(mc.player.getHeldItemOffhand()) > 0)) {
/* 262 */         (mc.getGameSettings()).keyBindUseItem.setPressed((mc.player.ticksExisted % 20 < 15));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 270 */     mc.timer.reset();
/*     */     
/* 272 */     for (IPacket p : this.packets) {
/* 273 */       mc.player.connection.sendPacketSilent(p);
/*     */     }
/* 275 */     this.packets.clear();
/* 276 */     mc.world.removeEntityFromWorld(-1337);
/* 277 */     this.timer.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/* 282 */     this.timer.reset();
/*     */   }
/*     */   
/*     */   private void equip() {
/* 286 */     if (this.elytras) {
/* 287 */       int item = Inventory.getChestplate();
/* 288 */       Player.moveItem((item < 46) ? item : 6, 6, true);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void unequip() {
/* 293 */     if (!this.elytras) {
/* 294 */       this.old = Inventory.getItem(37);
/* 295 */       int item = Player.findItem(45, Items.ELYTRA);
/* 296 */       Player.moveItem((item < 46) ? item : 6, 6, true);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void sendPackets(double x, double y, double z) {
/* 301 */     Vector3d position = mc.player.getPositionVec().add(x, y, z);
/* 302 */     Vector3d outOfBoundsPosition = position.add(new Vector3d(0.0D, 1377.0D, 0.0D));
/* 303 */     packetSender((CPlayerPacket)new CPlayerPacket.PositionPacket(position.x, position.y, position.z, true));
/* 304 */     packetSender((CPlayerPacket)new CPlayerPacket.PositionPacket(outOfBoundsPosition.x, outOfBoundsPosition.y, outOfBoundsPosition.z, true));
/*     */   }
/*     */ 
/*     */   
/*     */   private void packetSender(CPlayerPacket packet) {
/* 309 */     this.packetFlyPackets.add(packet);
/* 310 */     mc.player.connection.sendPacket((IPacket)packet);
/*     */   }
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\move\Flight.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */