/*     */ package fun.rockstarity.client.modules.player;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventSendPacket;
/*     */ import fun.rockstarity.api.helpers.game.Server;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Move;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Select;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import fun.rockstarity.client.modules.combat.Aura;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import net.minecraft.client.settings.KeyBinding;
/*     */ import net.minecraft.client.util.InputMappings;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.inventory.container.ClickType;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CHeldItemChangePacket;
/*     */ import net.minecraft.network.play.server.SHeldItemChangePacket;
/*     */ import net.minecraft.world.Difficulty;
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
/*     */ @Info(name = "InvUtils", desc = "Утилиты для инвентаря", type = Category.PLAYER, module = {"GuiMove", "InventoryMove", "InvMove", "GuiWalk", "XCarry", "ItemScroller"})
/*     */ public class InvUtils
/*     */   extends Module
/*     */ {
/*     */   private boolean clicked;
/*     */   
/*     */   public boolean isClicked() {
/*  50 */     return this.clicked; } public void setClicked(boolean clicked) { this.clicked = clicked; }
/*     */   
/*  52 */   private final List<IPacket> queue = new ArrayList<>(); public List<IPacket> getQueue() { return this.queue; }
/*     */   
/*  54 */   private final List<IPacket> mainQueue = new ArrayList<>(); public List<IPacket> getMainQueue() { return this.mainQueue; }
/*     */   
/*  56 */   private final TimerUtility timer = new TimerUtility(); public TimerUtility getTimer() { return this.timer; }
/*     */ 
/*     */   
/*  59 */   private final Select utils = new Select((Bindable)this, "Выбор");
/*     */   
/*  61 */   private final Select.Element buttonDrop = (new Select.Element(this.utils, "Кнопка выброса")).set(true); public Select.Element getButtonDrop() { return this.buttonDrop; }
/*  62 */    private final Select.Element itemScroll = new Select.Element(this.utils, "Скрол предметов"); public Select.Element getItemScroll() { return this.itemScroll; }
/*  63 */    private final Select.Element guiMove = new Select.Element(this.utils, "GuiMove");
/*  64 */   private final Select.Element itemSwapFix = new Select.Element(this.utils, "Фикс свапа предметов");
/*  65 */   private final CheckBox noClickIfMove = (new CheckBox((Bindable)this.guiMove, "Не двигать")).desc("Не дает двигать предметы, если вы бежите с открытым инвентарем"); public CheckBox getNoClickIfMove() { return this.noClickIfMove; }
/*  66 */    private final Select.Element glow = new Select.Element(this.utils, "Подсвечивать дебафф"); public Select.Element getGlow() { return this.glow; }
/*  67 */    private final Select.Element xCarry = new Select.Element(this.utils, "XCarry");
/*  68 */   private final Select.Element slotProtection = new Select.Element(this.utils, "Защита слотов"); public Select.Element getSlotProtection() { return this.slotProtection; }
/*  69 */    private final Select procet = new Select((Bindable)this.slotProtection, "Выбор");
/*  70 */   private final Select.Element one = new Select.Element(this.procet, "1");
/*  71 */   private final Select.Element two = new Select.Element(this.procet, "2");
/*  72 */   private final Select.Element three = new Select.Element(this.procet, "3");
/*  73 */   private final Select.Element four = new Select.Element(this.procet, "4");
/*  74 */   private final Select.Element five = new Select.Element(this.procet, "5");
/*  75 */   private final Select.Element six = new Select.Element(this.procet, "6");
/*  76 */   private final Select.Element seven = new Select.Element(this.procet, "7");
/*  77 */   private final Select.Element eight = new Select.Element(this.procet, "8");
/*  78 */   private final Select.Element nine = new Select.Element(this.procet, "9");
/*  79 */   private final CheckBox ctrq = new CheckBox((Bindable)this.slotProtection, "Работать с контролом"); public CheckBox getCtrq() { return this.ctrq; }
/*     */   
/*  81 */   private final CheckBox sneak = (new CheckBox((Bindable)this, "Присяд")).hide(() -> Boolean.valueOf(!this.guiMove.get()));
/*  82 */   public CheckBox getFt() { return this.ft; } private final CheckBox ft = (new CheckBox((Bindable)this, "Обход FunTime"))
/*  83 */     .desc("Мод для работы GuiMove на FunTime").hide(() -> Boolean.valueOf(!this.guiMove.get()));
/*     */   
/*  85 */   private final CheckBox slow = (new CheckBox((Bindable)this, "Медленный выброс")).hide(() -> Boolean.valueOf(!this.buttonDrop.get()));
/*     */   
/*  87 */   private final CheckBox noslow = (new CheckBox((Bindable)this, "Без замедления")).hide(() -> Boolean.valueOf(!this.ft.get()));
/*  88 */   private final Slider delayFt = (new Slider((Bindable)this, "Задержка свапа")).min(100.0F).max(400.0F).set(200.0F).inc(5.0F).desc("Задержка у инвентори-мува").hide(() -> Boolean.valueOf((!this.noslow.get() && !this.ft.get())));
/*  89 */   public Slider getDelay() { return this.delay; } private final Slider delay = (new Slider((Bindable)this, "Задержка"))
/*  90 */     .min(1.0F).max(400.0F).set(1.0F).inc(10.0F).desc("Задержка между перемещением предметов").hide(() -> Boolean.valueOf(!this.itemScroll.get()));
/*     */   private boolean taskActive;
/*     */   
/*  93 */   public boolean isTaskActive() { return this.taskActive; } public void setTaskActive(boolean taskActive) { this.taskActive = taskActive; }
/*     */   
/*  95 */   private final TimerUtility dropTimer = new TimerUtility();
/*     */   private boolean stop; private boolean protect;
/*  97 */   public boolean isStop() { return this.stop; } public void setStop(boolean stop) { this.stop = stop; }
/*     */   
/*     */   public boolean isProtect() {
/* 100 */     return this.protect;
/*     */   }
/*     */   
/*     */   public void onAllEvent(Event event) {
/* 104 */     if (mc.currentScreen == rock.getClickGui() && !this.stop) {
/* 105 */       handleGuiWalk(event);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onEvent(Event event) {
/* 110 */     if (this.guiMove.get()) {
/* 111 */       handleGuiWalk(event);
/*     */     }
/* 113 */     if (this.buttonDrop.get()) {
/* 114 */       handleActiveTask(event);
/*     */     }
/* 116 */     if (this.xCarry.get() && event instanceof EventSendPacket) { EventSendPacket e = (EventSendPacket)event; if (e.getPacket() instanceof net.minecraft.network.play.client.CCloseWindowPacket)
/* 117 */         event.cancel();  }
/*     */     
/* 119 */     if (this.itemSwapFix.get()) {
/* 120 */       if (mc.player == null || mc.player.ticksExisted % 40 != 0)
/*     */         return; 
/* 122 */       if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/* 123 */         IPacket iPacket = e.getPacket(); if (iPacket instanceof SHeldItemChangePacket) { SHeldItemChangePacket wrapper = (SHeldItemChangePacket)iPacket;
/* 124 */           int serverSlot = wrapper.getHeldItemHotbarIndex();
/* 125 */           if (serverSlot != mc.player.inventory.currentItem) {
/* 126 */             mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(Math.max(mc.player.inventory.currentItem - 1, 0)));
/* 127 */             mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(mc.player.inventory.currentItem));
/* 128 */             e.cancel();
/*     */           }  }
/*     */          }
/*     */     
/*     */     } 
/*     */ 
/*     */     
/* 135 */     if (this.slotProtection.get() && event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 136 */       int currentSlot = mc.player.inventory.currentItem;
/*     */       
/* 138 */       this
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 147 */         .protect = ((currentSlot == 0 && this.one.get()) || (currentSlot == 1 && this.two.get()) || (currentSlot == 2 && this.three.get()) || (currentSlot == 3 && this.four.get()) || (currentSlot == 4 && this.five.get()) || (currentSlot == 5 && this.six.get()) || (currentSlot == 6 && this.seven.get()) || (currentSlot == 7 && this.eight.get()) || (currentSlot == 8 && this.nine.get()));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleActiveTask(Event event) {
/* 153 */     if (!this.taskActive)
/*     */       return; 
/* 155 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate)
/* 156 */       for (int i = 0; i < mc.player.container.getInventory().size(); i++) {
/* 157 */         if (i == mc.player.container.getInventory().size() - 1) this.taskActive = false; 
/* 158 */         if (!Player.find(i).isEmpty()) {
/*     */           
/* 160 */           mc.playerController.windowClick(0, (i == 40) ? 45 : ((i < 9) ? (36 + i) : ((i >= 36) ? (8 - i - 36) : i)), 0, ClickType.PICKUP, (PlayerEntity)mc.player);
/* 161 */           mc.playerController.windowClick(0, -999, 0, ClickType.PICKUP, (PlayerEntity)mc.player);
/*     */           
/* 163 */           this.dropTimer.reset();
/*     */           
/* 165 */           if (this.slow.get() || i % 9 == 0)
/*     */             break; 
/*     */         } 
/*     */       }  
/*     */   }
/*     */   
/*     */   private void handleGuiWalk(Event event) {
/* 172 */     if (event instanceof fun.rockstarity.api.events.list.player.EventMotion && !(mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen)) {
/* 173 */       List<KeyBinding> keys = new ArrayList<>(Arrays.asList(new KeyBinding[] {
/* 174 */               (mc.getGameSettings()).keyBindForward, 
/* 175 */               (mc.getGameSettings()).keyBindBack, 
/* 176 */               (mc.getGameSettings()).keyBindLeft, 
/* 177 */               (mc.getGameSettings()).keyBindRight, 
/* 178 */               (mc.getGameSettings()).keyBindJump
/*     */             }));
/*     */       
/* 181 */       if (this.sneak.get()) keys.add((mc.getGameSettings()).keyBindSneak); 
/* 182 */       if (this.ft.get() && mc.currentScreen instanceof net.minecraft.client.gui.screen.inventory.ChestScreen && mc.world.getDifficulty() != Difficulty.EASY) {
/*     */         return;
/*     */       }
/*     */       
/* 186 */       if (mc.player.fallDistance <= 0.0F && this.noslow.get() && !mc.player.isOnGround() && mc.currentScreen instanceof net.minecraft.client.gui.screen.inventory.InventoryScreen) this.timer.reset();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 191 */       boolean canMove = ((this.timer.passed(this.delayFt.get()) && (rock.getClickGui().getWindow().getInput() == null || !rock.getClickGui().getWindow().getInput().isFocused())) || (this.noClickIfMove.get() && Move.getSpeed() < Move.getSpeed() / 3.0D + 0.05D));
/*     */ 
/*     */       
/* 194 */       keys.forEach(keyBinding -> keyBinding.setPressed(((!canMove && (mc.player.fallDistance > 0.0F || !this.noslow.get() || (mc.player.isOnGround() && !this.mainQueue.isEmpty()))) || (rock.getClickGui().getWindow().getInput() != null && rock.getClickGui().getWindow().getInput().isFocused())) ? false : InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode())));
/* 195 */       if (Server.isFS() && !canMove && mc.player.isInWater()) {
/* 196 */         Move.setSpeed(0.0D);
/* 197 */         (mc.player.getMotion()).y = 0.0D;
/*     */       } 
/*     */       
/* 200 */       if (canMove && (!Server.isFS() || (((Aura)rock.getModules().get(Aura.class)).getAttackTimer().passed(200L) && (mc.player.fallDistance > 0.0F || !this.noslow.get() || mc.player.isOnGround())))) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 205 */         ((InvUtils)rock.getModules().get(InvUtils.class)).getMainQueue().stream().forEach(packet -> mc.player.connection.sendPacket(packet));
/* 206 */         this.mainQueue.clear();
/*     */       } 
/*     */     } 
/*     */     
/* 210 */     if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event; if (this.ft.get())
/*     */       {
/* 212 */         if (e.getPacket() instanceof net.minecraft.network.play.server.SCloseWindowPacket)
/*     */         {
/*     */           
/* 215 */           event.cancel();
/*     */         }
/*     */       } }
/*     */   
/*     */   }
/*     */   
/*     */   public void onDisable() {
/* 222 */     this.mainQueue.clear();
/*     */   }
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\InvUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */