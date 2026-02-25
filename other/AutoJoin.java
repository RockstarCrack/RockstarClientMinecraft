/*     */ package fun.rockstarity.client.modules.other;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*     */ import fun.rockstarity.api.helpers.game.Chat;
/*     */ import fun.rockstarity.api.helpers.game.Server;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.Input;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import net.minecraft.inventory.container.ClickType;
/*     */ import net.minecraft.inventory.container.Slot;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CClickWindowPacket;
/*     */ import net.minecraft.network.play.client.CCloseWindowPacket;
/*     */ import net.minecraft.network.play.client.CHeldItemChangePacket;
/*     */ import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
/*     */ import net.minecraft.network.play.server.SChatPacket;
/*     */ import net.minecraft.network.play.server.SOpenWindowPacket;
/*     */ import net.minecraft.util.Hand;
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
/*     */ @NativeInclude
/*     */ @Info(name = "AutoJoin", desc = "Автоматически заходит на гриф", type = Category.OTHER)
/*     */ public class AutoJoin
/*     */   extends Module
/*     */ {
/*  42 */   protected final TimerUtility timerSystem = new TimerUtility();
/*     */   
/*  44 */   Mode mode = new Mode((Bindable)this, "Режим");
/*     */   
/*  46 */   Mode.Element rw = new Mode.Element(this.mode, "ReallyWorld/FunTime");
/*  47 */   Mode.Element st = new Mode.Element(this.mode, "Spooky Дуэли");
/*     */   
/*  49 */   private final Input grief = (new Input((Bindable)this, "Гриф")).set(true).hide(() -> Boolean.valueOf(!this.mode.is(this.rw)));
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  53 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/*  54 */       joinerGrief();
/*  55 */       checkGriefContainer();
/*     */     } 
/*  57 */     if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event; joinSuccessful(e); }
/*  58 */      if (event instanceof fun.rockstarity.api.events.list.game.EventWorldChange && !this.st.get()) {
/*  59 */       toggle();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  65 */     if (this.rw.get() && Server.isRW()) {
/*  66 */       joinerItem();
/*  67 */       mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
/*     */     } 
/*     */   }
/*     */   
/*     */   private void joinSuccessful(EventReceivePacket event) {
/*  72 */     if (this.st.get() && Server.is("spooky"))
/*  73 */       if (event.getPacket() instanceof SChatPacket)
/*  74 */       { joinerItem();
/*  75 */         mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND)); }
/*  76 */       else { IPacket iPacket = event.getPacket(); if (iPacket instanceof SOpenWindowPacket) { SOpenWindowPacket packet = (SOpenWindowPacket)iPacket;
/*  77 */           if (packet.getTitle().getString().contains("Выберите режим")) {
/*  78 */             mc.player.connection.sendPacket((IPacket)new CClickWindowPacket(packet.getWindowId(), 14, 0, ClickType.PICKUP, mc.player.openContainer
/*     */                   
/*  80 */                   .getSlot(14).getStack(), mc.player.openContainer
/*  81 */                   .getNextTransactionID(mc.player.inventory)));
/*  82 */             event.cancel();
/*     */           }  }
/*     */          }
/*     */        
/*  86 */     if (this.rw.get() && Server.isRW()) {
/*  87 */       if (event.getPacket() instanceof net.minecraft.network.play.server.SJoinGamePacket)
/*  88 */       { if (mc.ingameGUI.getTabList().getHeader() == null)
/*  89 */           return;  Chat.msg("Вход на " + this.grief.get() + " гриф успешен");
/*  90 */         toggle(); }
/*  91 */       else if (event.getPacket() instanceof SChatPacket)
/*  92 */       { joinerItem();
/*  93 */         mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND)); }
/*  94 */       else { IPacket iPacket = event.getPacket(); if (iPacket instanceof SOpenWindowPacket) { SOpenWindowPacket packet = (SOpenWindowPacket)iPacket;
/*  95 */           if (packet.getTitle().getString().contains("Выбор сервера"))
/*  96 */           { mc.player.connection.sendPacket((IPacket)new CClickWindowPacket(packet.getWindowId(), 21, 0, ClickType.PICKUP, mc.player.openContainer
/*     */                   
/*  98 */                   .getSlot(21).getStack(), mc.player.openContainer
/*  99 */                   .getNextTransactionID(mc.player.inventory)));
/* 100 */             event.cancel(); }  }
/*     */          }
/*     */     
/* 103 */     } else if (this.rw.get() && Server.isFT()) {
/* 104 */       if (event.getPacket() instanceof net.minecraft.network.play.server.SJoinGamePacket)
/* 105 */       { if (mc.ingameGUI.getTabList().getHeader() == null)
/* 106 */           return;  Chat.msg("Вход на " + this.grief.get() + " анархию успешен");
/* 107 */         toggle(); }
/* 108 */       else { IPacket iPacket = event.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket p = (SChatPacket)iPacket;
/* 109 */           String m = TextFormatting.getTextWithoutFormattingCodes(p.getChatComponent().getString());
/* 110 */           if (m.contains("Вы уже подключены"))
/* 111 */             toggle();  }
/*     */          }
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   private void joinerGrief() {
/* 118 */     if (this.rw.get() && Server.isFT()) {
/* 119 */       mc.player.sendChatMessage("/an" + String.valueOf(this.grief));
/*     */     }
/*     */     
/* 122 */     if (this.st.get() && Server.is("spooky")) {
/* 123 */       joinerItem();
/* 124 */       mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
/*     */       
/* 126 */       if (Player.findItem(Items.DIAMOND_SWORD) != -1) {
/* 127 */         Chat.msg("Вход успешен");
/* 128 */         toggle();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void joinerItem() {
/* 134 */     int slot = Player.findItem(Items.COMPASS);
/* 135 */     if (slot == -1)
/* 136 */       return;  mc.player.inventory.currentItem = slot;
/* 137 */     mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(slot));
/*     */   }
/*     */   
/*     */   private void checkGriefContainer() {
/* 141 */     if (mc.player.openContainer == null)
/*     */       return; 
/* 143 */     for (int slot = 0; slot < mc.player.openContainer.inventorySlots.size(); slot++) {
/* 144 */       Slot containerSlot = mc.player.openContainer.getSlot(slot);
/* 145 */       if (containerSlot != null && containerSlot.getHasStack()) {
/* 146 */         ItemStack stack = containerSlot.getStack();
/* 147 */         String itemName = stack.getDisplayName().getString();
/*     */         
/* 149 */         if (itemName.contains("ГРИФ #" + this.grief.get() + " (1.16.5+)")) {
/* 150 */           mc.player.connection.sendPacket((IPacket)new CClickWindowPacket(mc.player.openContainer.windowId, slot, 0, ClickType.PICKUP, stack, mc.player.openContainer
/*     */ 
/*     */                 
/* 153 */                 .getNextTransactionID(mc.player.inventory)));
/*     */           
/* 155 */           mc.player.connection.sendPacket((IPacket)new CCloseWindowPacket(mc.player.openContainer.windowId));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\AutoJoin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */