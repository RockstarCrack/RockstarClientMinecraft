/*     */ package fun.rockstarity.client.modules.other;
/*     */ 
/*     */ import com.google.common.collect.Lists;
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.inputs.EventInput;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*     */ import fun.rockstarity.api.events.list.player.EventFinishEat;
/*     */ import fun.rockstarity.api.events.list.render.EventRender2D;
/*     */ import fun.rockstarity.api.helpers.game.Server;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Input;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Select;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import fun.rockstarity.api.render.color.FixColor;
/*     */ import fun.rockstarity.api.secure.Debugger;
/*     */ import fun.rockstarity.client.modules.combat.Aura;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
/*     */ import net.minecraft.client.util.ITooltipFlag;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.inventory.container.ClickType;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CClickWindowPacket;
/*     */ import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
/*     */ import net.minecraft.network.play.server.SChatPacket;
/*     */ import net.minecraft.network.play.server.SCloseWindowPacket;
/*     */ import net.minecraft.network.play.server.SOpenWindowPacket;
/*     */ import net.minecraft.network.play.server.SSetSlotPacket;
/*     */ import net.minecraft.potion.Effects;
/*     */ import net.minecraft.util.Hand;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import net.minecraft.world.World;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "AutoDuels", desc = "Автоматически кидает дуэли", type = Category.OTHER)
/*     */ public class AutoDuels
/*     */   extends Module
/*     */ {
/*  52 */   private final List<String> sent = Lists.newArrayList();
/*  53 */   private final TimerUtility counter = new TimerUtility();
/*     */   
/*  55 */   private final Mode mode1 = new Mode((Bindable)this, "Режим");
/*  56 */   private final Mode.Element reallyworld = new Mode.Element(this.mode1, "ReallyWorld");
/*  57 */   private final Mode.Element spooky = new Mode.Element(this.mode1, "Spooky Auto");
/*     */   
/*  59 */   private final Slider kit = (new Slider((Bindable)this, "Кит")).min(1.0F).max(12.0F).inc(1.0F).set(10.0F).hide(() -> Boolean.valueOf(this.reallyworld.get()));
/*     */   
/*  61 */   private final Select mode = (new Select((Bindable)this, "Киты")).min(1).hide(() -> Boolean.valueOf(this.spooky.get()));
/*     */   
/*  63 */   private final Select.Element shield = new Select.Element(this.mode, "Щиты");
/*  64 */   private final Select.Element shipi = new Select.Element(this.mode, "Шипы 3");
/*  65 */   private final Select.Element bow = new Select.Element(this.mode, "Лук");
/*  66 */   private final Select.Element totem = new Select.Element(this.mode, "Тотем");
/*  67 */   private final Select.Element noDebaff = new Select.Element(this.mode, "НоуДебаф");
/*  68 */   private final Select.Element balls = new Select.Element(this.mode, "Шары");
/*  69 */   private final Select.Element classik = new Select.Element(this.mode, "Классик");
/*  70 */   private final Select.Element cheats = new Select.Element(this.mode, "Читерский рай");
/*  71 */   private final Select.Element nezer = new Select.Element(this.mode, "Незер");
/*     */   
/*  73 */   private final CheckBox stavkaAll = (new CheckBox((Bindable)this, "Ставить все")).hide(() -> Boolean.valueOf(this.spooky.get()));
/*     */   
/*  75 */   private final Mode sort = (new Mode((Bindable)this, "Предпочитать...")).hide(() -> Boolean.valueOf(this.spooky.get()));
/*  76 */   private final Mode.Element soft = new Mode.Element(this.sort, "Софтеров");
/*  77 */   private final Mode.Element noSoft = new Mode.Element(this.sort, "Ансофтеров");
/*  78 */   private final Mode.Element random = new Mode.Element(this.sort, "Рандом");
/*     */   
/*  80 */   private final Input stavk = (new Input((Bindable)this, "Ставка")).set("0").set(true).hide(() -> Boolean.valueOf((this.stavkaAll.get() || this.spooky.get())));
/*     */   
/*  82 */   private String lastTarget = "";
/*     */   private int balance;
/*     */   private boolean eating;
/*     */   private int wins;
/*     */   private int loses;
/*  87 */   private final TimerUtility charka = new TimerUtility();
/*  88 */   private final TimerUtility healka = new TimerUtility();
/*     */ 
/*     */ 
/*     */   
/*     */   public void onAllEvent(Event event) {
/*  93 */     if (this.reallyworld.get() && 
/*  94 */       event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event; IPacket iPacket = e.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket; if (Server.isRW()) {
/*  95 */           String m = packet.getChatComponent().getString();
/*     */ 
/*     */           
/*  98 */           if (m.contains("выиграли") || m.contains("Баланс")) {
/*     */             try {
/* 100 */               this.balance = Integer.parseInt(m.split("\\$")[1].replace("!", ""));
/* 101 */             } catch (Exception e1) {
/* 102 */               Debugger.print(e1);
/*     */             } 
/*     */           }
/*     */         }  }
/*     */        }
/*     */   
/*     */   }
/*     */   
/*     */   public void onEvent(Event event) {
/* 111 */     if (this.reallyworld.get()) {
/* 112 */       if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/*     */         
/* 114 */         List<String> list = PlayerTabOverlayGui.getPlayersNames();
/*     */         
/* 116 */         if (this.sort.is(this.random)) {
/* 117 */           Collections.shuffle(list);
/* 118 */         } else if (this.sort.is(this.soft)) {
/* 119 */           Collections.reverse(list);
/*     */         } 
/* 121 */         for (String player : list) {
/* 122 */           if (this.counter.passed(900L) && !this.sent.contains(player) && !player.equals(mc.player.getNameClear()) && !player.contains("§")) {
/* 123 */             this.lastTarget = player;
/* 124 */             mc.player.sendChatMessage(String.format("/duel %s %s", new Object[] { player, this.stavkaAll.get() ? ("" + this.balance) : this.stavk.get() }));
/* 125 */             this.sent.add(player);
/* 126 */             this.counter.reset();
/*     */           } 
/*     */         } 
/*     */       } 
/* 130 */       if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/* 131 */         IPacket iPacket1 = e.getPacket(); if (iPacket1 instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket1;
/* 132 */           String m = packet.getChatComponent().getString();
/* 133 */           if ((m.contains("принял") && !m.contains("не принял")) || m.contains("команды")) {
/* 134 */             this.sent.clear();
/* 135 */             set(false);
/*     */           } 
/*     */           
/* 138 */           if (m.contains("Баланс") || m.contains("отключил запросы")) {
/* 139 */             e.cancel();
/*     */           } }
/*     */ 
/*     */         
/* 143 */         String stavka = this.stavkaAll.get() ? ("" + this.balance) : this.stavk.get();
/*     */         
/* 145 */         IPacket iPacket2 = e.getPacket(); if (iPacket2 instanceof SCloseWindowPacket) { SCloseWindowPacket packet = (SCloseWindowPacket)iPacket2;
/* 146 */           e.cancel(); }
/*     */ 
/*     */         
/* 149 */         iPacket2 = e.getPacket(); if (iPacket2 instanceof SOpenWindowPacket) { SOpenWindowPacket packet = (SOpenWindowPacket)iPacket2;
/* 150 */           if (packet.getTitle().getString().contains("Выбор набора")) {
/* 151 */             mc.player.connection.sendPacket((IPacket)new CClickWindowPacket(packet
/* 152 */                   .getWindowId(), this.mode.getElements().indexOf(this.mode.getRandomEnabledElement()), 0, ClickType.PICKUP, mc.player.openContainer
/*     */                   
/* 154 */                   .getSlot(this.mode.getElements().indexOf(this.mode.getRandomEnabledElement())).getStack(), mc.player.openContainer
/* 155 */                   .getNextTransactionID(mc.player.inventory)));
/* 156 */             e.cancel();
/* 157 */           } else if (packet.getTitle().getString().contains("Настройка поединка")) {
/* 158 */             if (stavka.isEmpty() || stavka.equals("0")) {
/* 159 */               mc.player.connection.sendPacket((IPacket)new CClickWindowPacket(packet.getWindowId(), 0, 0, ClickType.PICKUP, mc.player.openContainer
/* 160 */                     .getSlot(0).getStack(), mc.player.openContainer
/* 161 */                     .getNextTransactionID(mc.player.inventory)));
/*     */             }
/* 163 */             e.cancel();
/*     */           }  }
/*     */ 
/*     */         
/* 167 */         iPacket2 = e.getPacket(); if (iPacket2 instanceof SSetSlotPacket) { SSetSlotPacket packet = (SSetSlotPacket)iPacket2; if (!stavka.equals("0")) {
/* 168 */             ItemStack stack = packet.getStack();
/*     */             
/* 170 */             if (stack.getDisplayName().getString().contains("Информация")) {
/* 171 */               List<ITextComponent> itemTooltip = stack.getTooltip(null, (ITooltipFlag)ITooltipFlag.TooltipFlags.NORMAL);
/* 172 */               for (ITextComponent str : itemTooltip) {
/*     */                 
/* 174 */                 if (str.getString().contains("Ставка:")) {
/* 175 */                   if (str.getString().contains("Ставка: 0")) {
/* 176 */                     mc.player.sendChatMessage("/duel " + this.lastTarget + " " + stavka + " " + stavka); continue;
/*     */                   } 
/* 178 */                   mc.player.connection.sendPacket((IPacket)new CClickWindowPacket(packet.getWindowId(), 0, 0, ClickType.PICKUP, mc.player.openContainer
/* 179 */                         .getSlot(0).getStack(), mc.player.openContainer
/* 180 */                         .getNextTransactionID(mc.player.inventory)));
/*     */                 } 
/*     */               } 
/*     */             } 
/*     */           }  }
/*     */          }
/*     */     
/*     */     } else {
/* 188 */       if (event instanceof EventFinishEat) { EventFinishEat e = (EventFinishEat)event; if (e.getEntity() == mc.player) {
/* 189 */           mc.player.inventory.currentItem = 0;
/* 190 */           mc.playerController.onStoppedUsingItem((PlayerEntity)mc.player);
/* 191 */           mc.gameSettings.keyBindUseItem.setPressed(false);
/* 192 */           if (e.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
/* 193 */             this.charka.reset();
/*     */           }
/* 195 */           if (e.getItem() == Items.POTION)
/* 196 */             this.healka.reset(); 
/* 197 */           this.eating = false;
/*     */         }  }
/*     */       
/* 200 */       if (event instanceof fun.rockstarity.api.events.list.game.EventWorldChange) {
/* 201 */         mc.player.setAbsorptionAmount(0.0F);
/* 202 */         this.eating = false;
/*     */       } 
/*     */       
/* 205 */       if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 206 */         if (mc.player.getHeldItemMainhand().getDisplayName().getString().contains("Вход в очередь")) {
/* 207 */           mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
/*     */         }
/*     */         
/* 210 */         if (mc.player.inventory.currentItem == 1) {
/* 211 */           mc.player.inventory.currentItem = 0;
/*     */         }
/*     */         
/* 214 */         if (!mc.player.getCooldownTracker().hasCooldown(Items.ENCHANTED_GOLDEN_APPLE) && Player.findItem(Items.ENCHANTED_GOLDEN_APPLE) != -1 && mc.player.getAbsorptionAmount() <= 0.0F) {
/* 215 */           mc.player.inventory.currentItem = Player.findItem(Items.ENCHANTED_GOLDEN_APPLE);
/* 216 */           if (mc.currentScreen == null) {
/* 217 */             mc.gameSettings.keyBindUseItem.setPressed(true);
/*     */           } else {
/* 219 */             mc.playerController.processRightClick((PlayerEntity)mc.player, (World)mc.world, Hand.MAIN_HAND);
/*     */           } 
/* 221 */           this.eating = true;
/*     */           
/*     */           return;
/*     */         } 
/* 225 */         if (Player.findItem(Items.POTION) != -1 && mc.player.getHealth() + mc.player.getAbsorptionAmount() < 7.0F && this.healka.passed(13000L)) {
/* 226 */           mc.player.inventory.currentItem = 7;
/* 227 */           if (mc.currentScreen == null) {
/* 228 */             mc.gameSettings.keyBindUseItem.setPressed(true);
/*     */           } else {
/* 230 */             mc.playerController.processRightClick((PlayerEntity)mc.player, (World)mc.world, Hand.MAIN_HAND);
/*     */           } 
/* 232 */           this.eating = true;
/*     */           
/*     */           return;
/*     */         } 
/* 236 */         if (!mc.player.getCooldownTracker().hasCooldown(Items.GOLDEN_APPLE) && Player.findItem(Items.GOLDEN_APPLE) != -1 && mc.player.getHealth() + mc.player.getAbsorptionAmount() < 7.0F) {
/* 237 */           mc.player.inventory.currentItem = Player.findItem(Items.GOLDEN_APPLE);
/* 238 */           if (mc.currentScreen == null) {
/* 239 */             mc.gameSettings.keyBindUseItem.setPressed(true);
/*     */           } else {
/* 241 */             mc.playerController.processRightClick((PlayerEntity)mc.player, (World)mc.world, Hand.MAIN_HAND);
/*     */           } 
/* 243 */           this.eating = true;
/*     */           
/*     */           return;
/*     */         } 
/* 247 */         if (!mc.player.isPotionActive(Effects.STRENGTH) && Player.findItem(Items.POTION) != -1) {
/* 248 */           mc.player.inventory.currentItem = 8;
/* 249 */           if (mc.currentScreen == null) {
/* 250 */             mc.gameSettings.keyBindUseItem.setPressed(true);
/*     */           } else {
/* 252 */             mc.playerController.processRightClick((PlayerEntity)mc.player, (World)mc.world, Hand.MAIN_HAND);
/*     */           } 
/* 254 */           this.eating = true;
/*     */           
/*     */           return;
/*     */         } 
/*     */       } 
/* 259 */       if (event instanceof EventInput) { EventInput e = (EventInput)event; if (((Aura)rock.getModules().get(Aura.class)).getTarget() != null) {
/* 260 */           e.setForward(1.0F);
/* 261 */           e.setJump(true);
/*     */         }  }
/*     */       
/* 264 */       if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/* 265 */         IPacket iPacket = e.getPacket(); if (iPacket instanceof SOpenWindowPacket) { SOpenWindowPacket packet = (SOpenWindowPacket)iPacket;
/* 266 */           mc.player.connection.sendPacket((IPacket)new CClickWindowPacket(packet.getWindowId(), 
/* 267 */                 (int)(this.kit.get() - 1.0F), 0, ClickType.PICKUP, mc.player.openContainer
/* 268 */                 .getSlot((int)(this.kit.get() - 1.0F)).getStack(), mc.player.openContainer
/* 269 */                 .getNextTransactionID(mc.player.inventory))); }
/*     */          }
/*     */ 
/*     */       
/* 273 */       if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event; IPacket iPacket = e.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket;
/* 274 */           String message = packet.getChatComponent().getString().toLowerCase();
/*     */           
/* 276 */           if (message.contains("победитель") && message.contains(mc.player.getNameClear().toLowerCase())) {
/* 277 */             this.wins++;
/*     */           }
/*     */           
/* 280 */           if (message.contains("проигрывший") && message.contains(mc.player.getNameClear().toLowerCase())) {
/* 281 */             this.loses++;
/*     */           } }
/*     */          }
/*     */       
/* 285 */       if (event instanceof EventRender2D) { EventRender2D e = (EventRender2D)event;
/* 286 */         bold.get(16).draw(e.getMatrixStack(), "Всего игр: " + this.wins + this.loses, sr.getScaledWidth() / 2.0F - bold.get(16).getWidth("Всего игр: " + this.wins + this.loses) / 2.0F, sr.getScaledHeight() / 2.0F + 10.0F, FixColor.WHITE);
/* 287 */         bold.get(16).draw(e.getMatrixStack(), "Побед: " + this.wins, sr.getScaledWidth() / 2.0F - bold.get(16).getWidth("Побед: " + this.wins) / 2.0F, sr.getScaledHeight() / 2.0F + 20.0F, FixColor.WHITE);
/* 288 */         bold.get(16).draw(e.getMatrixStack(), "Лузов: " + this.loses, sr.getScaledWidth() / 2.0F - bold.get(16).getWidth("Лузов: " + this.loses) / 2.0F, sr.getScaledHeight() / 2.0F + 30.0F, FixColor.WHITE); }
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/* 295 */     this.wins = 0;
/* 296 */     this.loses = 0;
/*     */     
/* 298 */     if (this.spooky.get()) {
/* 299 */       ((Baritone)rock.getModules().get(Baritone.class)).set(true);
/* 300 */       mc.player.sendChatMessage("#follow players");
/* 301 */       mc.player.sendChatMessage("#allowBreak false");
/*     */     } else {
/* 303 */       mc.player.sendChatMessage("/bal");
/* 304 */       this.counter.reset();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 310 */     if (this.spooky.get()) {
/* 311 */       mc.player.sendChatMessage("#stop");
/* 312 */       mc.player.sendChatMessage("#allowBreak true");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\AutoDuels.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */