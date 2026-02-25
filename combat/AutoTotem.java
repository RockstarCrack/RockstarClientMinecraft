/*     */ package fun.rockstarity.client.modules.combat;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.EventType;
/*     */ import fun.rockstarity.api.events.list.game.EventTotemBreak;
/*     */ import fun.rockstarity.api.events.list.player.EventUpdate;
/*     */ import fun.rockstarity.api.events.list.render.EventRender2D;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Inventory;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.helpers.render.Render;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Select;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import fun.rockstarity.api.render.animation.Animation;
/*     */ import fun.rockstarity.api.render.animation.Easing;
/*     */ import fun.rockstarity.api.render.color.FixColor;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.inventory.EquipmentSlotType;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.Items;
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
/*     */ @Info(name = "AutoTotem", desc = "Автоматически ставит тотем в левую руку", type = Category.COMBAT)
/*     */ public class AutoTotem
/*     */   extends Module
/*     */ {
/*  44 */   private final Mode mode = new Mode((Bindable)this, "Брать первую очередь");
/*  45 */   private final Mode.Element noSort = new Mode.Element(this.mode, "Без сортировки");
/*  46 */   private final Mode.Element sortNon = new Mode.Element(this.mode, "Не зачарованные");
/*  47 */   private final Mode.Element sortOn = new Mode.Element(this.mode, "Зачарованные");
/*     */   
/*  49 */   private final Slider health = (new Slider((Bindable)this, "Здоровье")).min(1.0F).max(20.0F).inc(0.5F).set(4.0F).desc("Здоровье при котором будет браться тотем");
/*  50 */   private final Slider healthwithelytra = (new Slider((Bindable)this, "Здоровье c элитрой")).min(1.0F).max(20.0F).inc(0.5F).set(4.0F).desc("Здоровье с элитрой при котором будет браться тотем");
/*  51 */   private final CheckBox swapBack = (new CheckBox((Bindable)this, "Возвращать обратно")).set(true).desc("Возвращает предмет обратно");
/*  52 */   private final CheckBox display = (new CheckBox((Bindable)this, "Отображение количества")).set(true).desc("Отображает количество тотемов");
/*     */   
/*  54 */   private final CheckBox eat = new CheckBox((Bindable)this, "Не менять если ешь");
/*     */   
/*  56 */   private final Select utils = new Select((Bindable)this, "Брать при...");
/*     */   
/*  58 */   private final Select.Element crys = (new Select.Element(this.utils, "Кристалле")).set(true);
/*  59 */   private final Select.Element minecart = (new Select.Element(this.utils, "Вагонетке с динамитом")).set(true);
/*  60 */   private final Select.Element fall = new Select.Element(this.utils, "Падении");
/*     */   
/*  62 */   private final Slider crysDist = (new Slider((Bindable)this, "Дистанция кристалла")).min(2.0F).max(20.0F).inc(1.0F).set(7.0F).hide(() -> Boolean.valueOf(!this.crys.get()));
/*  63 */   private final Slider minecartDist = (new Slider((Bindable)this, "Дистанция вагонетки")).min(2.0F).max(20.0F).inc(1.0F).set(7.0F).hide(() -> Boolean.valueOf(!this.minecart.get()));
/*  64 */   private final Slider fallDist = (new Slider((Bindable)this, "Дистанция до земли")).min(4.0F).max(52.0F).inc(2.0F).set(6.0F).hide(() -> Boolean.valueOf(!this.fall.get()));
/*     */   
/*     */   private boolean needSwap;
/*  67 */   private int swapItem = -1;
/*     */   private Item last;
/*  69 */   protected final Animation showing = (new Animation()).setEasing(Easing.BOTH_SINE).setSpeed(300);
/*     */   
/*  71 */   private final TimerUtility lastUse = new TimerUtility();
/*     */   
/*  73 */   private final Slider cooldown = (new Slider((Bindable)this, "Задержка на взятие")).min(0.0F).max(5000.0F).inc(250.0F).set(1000.0F).desc("Задержка в миллисекундах на взятие нового тотема после сноса старого");
/*     */   
/*     */   @NativeInclude
/*     */   public AutoTotem() {
/*  77 */     super(0);
/*     */   }
/*     */ 
/*     */   
/*     */   @EventType({EventRender2D.class, EventUpdate.class, EventTotemBreak.class})
/*     */   public void onEvent(Event event) {
/*  83 */     if (event instanceof EventRender2D) { EventRender2D e = (EventRender2D)event;
/*  84 */       renderTotem(e); }
/*     */ 
/*     */     
/*  87 */     if (event instanceof fun.rockstarity.api.events.list.game.EventPickupItem) {
/*  88 */       this.lastUse.reset();
/*     */     }
/*     */     
/*  91 */     if (event instanceof EventUpdate) {
/*  92 */       float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
/*  93 */       ItemStack leftStack = mc.player.getHeldItemOffhand();
/*  94 */       Item left = leftStack.getItem();
/*  95 */       ItemStack rightStack = mc.player.getHeldItemMainhand();
/*  96 */       Item right = rightStack.getItem();
/*     */       
/*  98 */       int preferredTotem = -1;
/*  99 */       int fallbackTotem = -1;
/*     */       
/* 101 */       if (this.mode.is(this.sortNon)) {
/* 102 */         preferredTotem = findTotemInInventoryNon();
/* 103 */         fallbackTotem = findTotemInInventoryOn();
/*     */       }
/* 105 */       else if (this.mode.is(this.sortOn)) {
/* 106 */         preferredTotem = findTotemInInventoryOn();
/* 107 */         fallbackTotem = findTotemInInventoryNon();
/*     */       } else {
/*     */         
/* 110 */         preferredTotem = findAnyTotemInInventory();
/*     */       } 
/*     */       
/* 113 */       float required = Player.isElytraEquiped() ? this.healthwithelytra.get() : this.health.get();
/*     */ 
/*     */       
/* 116 */       boolean shouldActivate = ((!this.eat.get() || !mc.player.isHandActive()) && (health <= required || checkCrystal() || checkMinecart() || checkFall()) && this.lastUse.passed(this.cooldown.get()));
/*     */       
/* 118 */       if (shouldActivate) {
/* 119 */         int totemToUse = -1;
/*     */         
/* 121 */         if (this.mode.is(this.sortNon)) {
/* 122 */           if (preferredTotem != -1) {
/* 123 */             totemToUse = preferredTotem;
/* 124 */           } else if (fallbackTotem != -1 && left != Items.TOTEM_OF_UNDYING) {
/* 125 */             totemToUse = fallbackTotem;
/*     */           }
/*     */         
/* 128 */         } else if (this.mode.is(this.sortOn)) {
/* 129 */           if (preferredTotem != -1) {
/* 130 */             totemToUse = preferredTotem;
/* 131 */           } else if (fallbackTotem != -1 && left != Items.TOTEM_OF_UNDYING) {
/* 132 */             totemToUse = fallbackTotem;
/*     */           }
/*     */         
/*     */         }
/* 136 */         else if (preferredTotem != -1) {
/* 137 */           totemToUse = preferredTotem;
/*     */         } 
/*     */ 
/*     */         
/* 141 */         if (totemToUse != -1) {
/* 142 */           moveItem(totemToUse);
/*     */         
/*     */         }
/*     */       }
/* 146 */       else if ((this.swapItem >= 0 || this.last != null) && this.needSwap && this.swapBack.get()) {
/* 147 */         swap();
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 152 */     if (event instanceof EventTotemBreak) { EventTotemBreak e = (EventTotemBreak)event; if (e.getEntity() == mc.player) {
/* 153 */         this.needSwap = true;
/* 154 */         this.lastUse.reset();
/*     */       }  }
/*     */     
/* 157 */     if (event instanceof fun.rockstarity.api.events.list.game.EventWorldChange) {
/* 158 */       this.needSwap = false;
/*     */     }
/*     */   }
/*     */   
/*     */   private void swap() {
/* 163 */     Item item = mc.player.getItemStackFromSlot(EquipmentSlotType.OFFHAND).getItem();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 168 */     if (this.last != null) {
/* 169 */       Inventory.moveItem(Inventory.findItem(44, this.last), 45, true);
/* 170 */       this.swapItem = -1;
/* 171 */       this.needSwap = false;
/*     */       return;
/*     */     } 
/* 174 */     Inventory.moveItem(this.swapItem, 45, true);
/* 175 */     this.swapItem = -1;
/* 176 */     this.needSwap = false;
/*     */   }
/*     */   
/*     */   private void renderTotem(EventRender2D e) {
/* 180 */     this.showing.setForward(((mc.getGameSettings()).hideGUI || (mc.getGameSettings()).showDebugInfo || !this.display.get()));
/* 181 */     int heigt = sr.getScaledHeight();
/* 182 */     int width = sr.getScaledWidth();
/* 183 */     float x = (width / 2 + 103);
/* 184 */     float y = heigt - 24.5F - 14.0F * mc.ingameGUI.getOpening().get() + 17.0F * this.showing.get();
/*     */     
/* 186 */     if (!this.showing.finished() && 
/* 187 */       foundTotemCount() > 0) {
/* 188 */       bold.get(12).draw(e.getMatrixStack(), String.valueOf(foundTotemCount()), x - bold
/* 189 */           .get(12).getWidth(String.valueOf(foundTotemCount())) / 2.0F + 8.1F, y + 15.3F, (foundTotemCount() > 2) ? FixColor.WHITE : FixColor.RED);
/*     */ 
/*     */       
/* 192 */       Render.drawStackOld(Items.TOTEM_OF_UNDYING.getDefaultInstance(), ((int)x - 8), ((int)y + 7), 1.0F);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean checkFall() {
/* 198 */     if (!this.fall.get()) return false; 
/* 199 */     if (mc.player.isElytraFlying()) return false;
/*     */     
/* 201 */     return (mc.player.fallDistance >= this.fallDist.get());
/*     */   }
/*     */   
/*     */   private boolean checkCrystal() {
/* 205 */     if (!this.crys.get()) return false;
/*     */     
/* 207 */     for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity entity = objectIterator.next();
/* 208 */       if (!(entity instanceof net.minecraft.entity.item.EnderCrystalEntity) || mc.player.getDistance(entity) > this.crysDist.get())
/* 209 */         continue;  return true; }
/*     */     
/* 211 */     return false;
/*     */   }
/*     */   
/*     */   private boolean checkMinecart() {
/* 215 */     if (!this.minecart.get()) return false;
/*     */     
/* 217 */     for (ObjectIterator<Entity> objectIterator = mc.world.getAllEntities().iterator(); objectIterator.hasNext(); ) { Entity entity = objectIterator.next();
/* 218 */       if (!(entity instanceof net.minecraft.entity.item.minecart.TNTMinecartEntity) || mc.player.getDistance(entity) > this.minecartDist.get())
/* 219 */         continue;  return true; }
/*     */     
/* 221 */     return false;
/*     */   }
/*     */   
/*     */   private int foundTotemCount() {
/* 225 */     return 
/*     */ 
/*     */       
/* 228 */       (int)mc.player.inventory.mainInventory.stream().filter(stack -> (stack.getItem() == Items.TOTEM_OF_UNDYING)).count();
/*     */   }
/*     */   
/*     */   private int findAnyTotemInInventory() {
/* 232 */     for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
/* 233 */       ItemStack stack = (ItemStack)mc.player.inventory.mainInventory.get(i);
/* 234 */       if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
/* 235 */         return i;
/*     */       }
/*     */     } 
/* 238 */     return -1;
/*     */   }
/*     */   
/*     */   private int findTotemInInventoryNon() {
/* 242 */     for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
/* 243 */       ItemStack stack = (ItemStack)mc.player.inventory.mainInventory.get(i);
/* 244 */       if (stack.getItem() == Items.TOTEM_OF_UNDYING && !stack.isEnchanted()) {
/* 245 */         return i;
/*     */       }
/*     */     } 
/* 248 */     return -1;
/*     */   }
/*     */   
/*     */   private int findTotemInInventoryOn() {
/* 252 */     for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
/* 253 */       ItemStack stack = (ItemStack)mc.player.inventory.mainInventory.get(i);
/* 254 */       if (stack.getItem() == Items.TOTEM_OF_UNDYING && stack.isEnchanted()) {
/* 255 */         return i;
/*     */       }
/*     */     } 
/* 258 */     return -1;
/*     */   }
/*     */   
/*     */   private void moveItem(int j) {
/* 262 */     Item item = mc.player.getItemStackFromSlot(EquipmentSlotType.OFFHAND).getItem();
/* 263 */     if (item != Items.AIR && item != Items.TOTEM_OF_UNDYING)
/* 264 */       this.last = item; 
/* 265 */     this.swapItem = (j < 9) ? (j + 36) : j;
/* 266 */     Inventory.moveItem(45, (j < 9) ? (j + 36) : j, true);
/* 267 */     this.needSwap = true;
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\AutoTotem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */