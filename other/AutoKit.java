/*     */ package fun.rockstarity.client.modules.other;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*     */ import fun.rockstarity.api.helpers.game.Chat;
/*     */ import fun.rockstarity.api.helpers.game.ItemUtility;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Select;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import net.minecraft.client.gui.screen.inventory.ChestScreen;
/*     */ import net.minecraft.enchantment.EnchantmentHelper;
/*     */ import net.minecraft.enchantment.Enchantments;
/*     */ import net.minecraft.inventory.container.ClickType;
/*     */ import net.minecraft.inventory.container.Slot;
/*     */ import net.minecraft.item.ElytraItem;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CClickWindowPacket;
/*     */ import net.minecraft.network.play.server.SChatPacket;
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
/*     */ @Info(name = "AutoKit", desc = "Автоматически покупает сет на FunTime/SpookyTime", type = Category.OTHER)
/*     */ public class AutoKit
/*     */   extends Module
/*     */ {
/*  45 */   private final Mode armor = new Mode((Bindable)this, "Броня");
/*  46 */   private final Mode.Element armorNo = new Mode.Element(this.armor, "Нет");
/*  47 */   private final Mode.Element armorKrush = new Mode.Element(this.armor, "Крушитель");
/*  48 */   private final Mode.Element armorNezer = new Mode.Element(this.armor, "Незеритовая");
/*  49 */   private final CheckBox armorProtect5 = new CheckBox((Bindable)this.armorNezer, "Только 5 прочн.");
/*     */   
/*  51 */   private final Mode additionalWeapon = new Mode((Bindable)this, "Доп. Оружие");
/*  52 */   private final Mode.Element weaponNo = new Mode.Element(this.additionalWeapon, "Нет");
/*  53 */   private final Mode.Element weaponTrident = new Mode.Element(this.additionalWeapon, "Трезубец");
/*  54 */   private final Mode.Element weaponCrossbow = new Mode.Element(this.additionalWeapon, "Арбалет");
/*  55 */   private final Mode arrow = new Mode((Bindable)this.weaponCrossbow, "Стрелы");
/*  56 */   private final Mode.Element proklyataya = new Mode.Element(this.arrow, "Проклятая стрела");
/*  57 */   private final Mode.Element paranoya = new Mode.Element(this.arrow, "Стрела паранойи");
/*  58 */   private final Mode.Element ledyanaya = new Mode.Element(this.arrow, "Ледяная стреда");
/*  59 */   private final Mode.Element dyavol = new Mode.Element(this.arrow, "Дьявольская стрела");
/*  60 */   private final Slider arrowCount = (new Slider((Bindable)this.weaponCrossbow, "Количество")).min(1.0F).max(64.0F).inc(1.0F).set(32.0F);
/*     */   
/*  62 */   private final Mode effectsClean = new Mode((Bindable)this, "Снятие эффектов");
/*  63 */   private final Mode.Element cleanNo = new Mode.Element(this.effectsClean, "Нет");
/*  64 */   private final Mode.Element cleanBozhka = new Mode.Element(this.effectsClean, "Божка");
/*  65 */   private final Mode.Element cleanMilk = new Mode.Element(this.effectsClean, "Молоко");
/*  66 */   private final Slider milkCount = (new Slider((Bindable)this.cleanMilk, "Количество")).min(1.0F).max(6.0F).inc(1.0F).set(2.0F);
/*  67 */   private final Slider bozhkaCount = (new Slider((Bindable)this.cleanBozhka, "Количество")).min(1.0F).max(16.0F).inc(1.0F).set(2.0F);
/*     */   
/*  69 */   private final Mode leftHand = new Mode((Bindable)this, "Левая рука");
/*  70 */   private final Mode.Element leftNo = new Mode.Element(this.leftHand, "Нет");
/*  71 */   private final Mode.Element leftTal = new Mode.Element(this.leftHand, "Талисман");
/*  72 */   private final Mode.Element leftSphere = new Mode.Element(this.leftHand, "Сфера");
/*  73 */   private final Mode selectTal = new Mode((Bindable)this.leftTal, "Выбор");
/*  74 */   private final Mode selectSphere = new Mode((Bindable)this.leftSphere, "Выбор");
/*     */   
/*  76 */   private final Mode potion = new Mode((Bindable)this, "Зелья");
/*  77 */   private final Mode.Element potionNo = new Mode.Element(this.potion, "Нет");
/*  78 */   private final Mode.Element potionBoth = new Mode.Element(this.potion, "Смешанные");
/*  79 */   private final Mode.Element potionSplit = new Mode.Element(this.potion, "Раздельные");
/*  80 */   private final Select potions = new Select((Bindable)this.potionSplit, "Выбор");
/*  81 */   private final Select.Element silka = (new Select.Element(this.potions, "Силка")).set(true);
/*  82 */   private final Select.Element skorka = (new Select.Element(this.potions, "Скорка")).set(true);
/*     */   
/*  84 */   private final Select others = new Select((Bindable)this, "Другое");
/*  85 */   private final Select.Element sword = (new Select.Element(this.others, "Меч круша")).set(true);
/*  86 */   private final Select.Element pearl = (new Select.Element(this.others, "Перки")).set(true);
/*  87 */   private final Select.Element chorus = (new Select.Element(this.others, "Хорусы")).set(true);
/*  88 */   private final Select.Element charka = (new Select.Element(this.others, "Чарки")).set(true);
/*  89 */   private final Select.Element food = (new Select.Element(this.others, "Золотая морковь")).set(true);
/*  90 */   private final Select.Element gapple = (new Select.Element(this.others, "Геплы")).set(true);
/*  91 */   private final Select.Element trap = (new Select.Element(this.others, "Трапки")).set(true);
/*  92 */   private final Select.Element plast = (new Select.Element(this.others, "Пласты")).set(true);
/*  93 */   private final Select.Element pilb = (new Select.Element(this.others, "Явки")).set(true);
/*  94 */   private final Select.Element heal = (new Select.Element(this.others, "Исцел")).set(true);
/*  95 */   private final Select.Element totem = (new Select.Element(this.others, "Тотемы")).set(true);
/*  96 */   private final Select.Element shulker = (new Select.Element(this.others, "Шалкеры")).set(true);
/*  97 */   private final Select.Element exp = (new Select.Element(this.others, "Опыт")).set(true);
/*  98 */   private final Select.Element elytra = (new Select.Element(this.others, "Элитры")).set(true);
/*  99 */   private final Select.Element firework = (new Select.Element(this.others, "Фейерверки")).set(true);
/*     */ 
/*     */   
/* 102 */   private final Slider pearlCount = (new Slider((Bindable)this.pearl, "Количество")).min(1.0F).max(32.0F).inc(1.0F).set(16.0F);
/*     */   
/* 104 */   private final Slider charkaCount = (new Slider((Bindable)this.charka, "Количество")).min(1.0F).max(64.0F).inc(1.0F).set(7.0F);
/*     */   
/* 106 */   private final Slider chorusCount = (new Slider((Bindable)this.chorus, "Количество")).min(1.0F).max(64.0F).inc(1.0F).set(16.0F);
/*     */   
/* 108 */   private final Slider gappleCount = (new Slider((Bindable)this.gapple, "Количество")).min(1.0F).max(64.0F).inc(1.0F).set(16.0F);
/*     */   
/* 110 */   private final Slider trapCount = (new Slider((Bindable)this.trap, "Количество")).min(1.0F).max(64.0F).inc(1.0F).set(6.0F);
/*     */   
/* 112 */   private final Slider plastCount = (new Slider((Bindable)this.plast, "Количество")).min(1.0F).max(64.0F).inc(1.0F).set(6.0F);
/*     */   
/* 114 */   private final Slider pilbCount = (new Slider((Bindable)this.pilb, "Количество")).min(1.0F).max(64.0F).inc(1.0F).set(3.0F);
/*     */   
/* 116 */   private final Slider healCount = (new Slider((Bindable)this.heal, "Количество")).min(1.0F).max(64.0F).inc(1.0F).set(16.0F);
/*     */   
/* 118 */   private final Slider totemCount = (new Slider((Bindable)this.totem, "Количество")).min(1.0F).max(3.0F).inc(1.0F).set(2.0F);
/*     */   
/* 120 */   private final Slider shulkerCount = (new Slider((Bindable)this.shulker, "Количество")).min(1.0F).max(3.0F).inc(1.0F).set(2.0F);
/*     */   
/* 122 */   private final Slider expCount = (new Slider((Bindable)this.exp, "Стаки")).min(1.0F).max(6.0F).inc(1.0F).set(3.0F);
/*     */   
/* 124 */   private final Mode elytraMode = new Mode((Bindable)this.elytra, "Выбор");
/* 125 */   private final Mode.Element elytraDef = new Mode.Element(this.elytraMode, "Обычные");
/* 126 */   private final Mode.Element elytraKrush = new Mode.Element(this.elytraMode, "Крушителя");
/*     */   
/* 128 */   private final Slider fireworkCount = (new Slider((Bindable)this.firework, "Стаки")).min(1.0F).max(4.0F).inc(1.0F).set(2.0F);
/*     */   
/*     */   private int stage;
/* 131 */   private String stageItem = "zxc";
/*     */   
/* 133 */   private final TimerUtility aucTimer = new TimerUtility();
/* 134 */   private final TimerUtility buyTimer = new TimerUtility();
/*     */   
/*     */   public AutoKit() {
/* 137 */     new Mode.Element(this.selectTal, "Талисман Грани");
/* 138 */     new Mode.Element(this.selectTal, "Талисман Дедала");
/* 139 */     new Mode.Element(this.selectTal, "Талисман Тритона");
/* 140 */     new Mode.Element(this.selectTal, "Талисман Гармонии");
/* 141 */     new Mode.Element(this.selectTal, "Талисман Феникса");
/* 142 */     new Mode.Element(this.selectTal, "Талисман Ехидны");
/* 143 */     new Mode.Element(this.selectTal, "Талисман Крушителя");
/* 144 */     new Mode.Element(this.selectTal, "Талисман Карателя");
/*     */     
/* 146 */     new Mode.Element(this.selectSphere, "Сфера Андромеды");
/* 147 */     new Mode.Element(this.selectSphere, "Сфера Пандора");
/* 148 */     new Mode.Element(this.selectSphere, "Сфера Титана");
/* 149 */     new Mode.Element(this.selectSphere, "Сфера Аполлона");
/* 150 */     new Mode.Element(this.selectSphere, "Сфера Астрея");
/* 151 */     new Mode.Element(this.selectSphere, "Сфера Осириса");
/* 152 */     new Mode.Element(this.selectSphere, "Сфера Химеры");
/*     */   }
/*     */ 
/*     */   
/*     */   @NativeInclude
/*     */   public void onEvent(Event event) {
/* 158 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/*     */       
/* 160 */       if (!(mc.player.openContainer instanceof net.minecraft.inventory.container.ChestContainer) && this.aucTimer.passed(1000L)) {
/* 161 */         this.stageItem = "";
/* 162 */         switch (this.stage) {
/*     */           case 0:
/* 164 */             if (!this.armorNo.get()) this.stageItem = this.armorKrush.get() ? "шлем крушителя" : "незеритовый шлем";
/*     */             
/*     */             break;
/*     */           case 1:
/* 168 */             if (!this.armorNo.get()) this.stageItem = this.armorKrush.get() ? "нагрудник крушителя" : "незеритовый нагрудник";
/*     */             
/*     */             break;
/*     */           case 2:
/* 172 */             if (!this.armorNo.get()) this.stageItem = this.armorKrush.get() ? "поножи крушителя" : "незеритовые поножи";
/*     */             
/*     */             break;
/*     */           case 3:
/* 176 */             if (!this.armorNo.get()) this.stageItem = this.armorKrush.get() ? "ботинки крушителя" : "незеритовые ботинки";
/*     */             
/*     */             break;
/*     */           case 4:
/* 180 */             if (this.sword.get()) this.stageItem = "меч крушителя";
/*     */             
/*     */             break;
/*     */           case 5:
/* 184 */             if (this.pearl.get()) this.stageItem = "перка";
/*     */             
/*     */             break;
/*     */           case 6:
/* 188 */             if (this.chorus.get()) this.stageItem = "хорус";
/*     */             
/*     */             break;
/*     */           case 7:
/* 192 */             if (this.charka.get()) this.stageItem = "чарка";
/*     */             
/*     */             break;
/*     */           case 8:
/* 196 */             if (this.gapple.get()) this.stageItem = "золотое яблоко";
/*     */             
/*     */             break;
/*     */           case 9:
/* 200 */             if (this.trap.get()) this.stageItem = "трапка";
/*     */             
/*     */             break;
/*     */           case 10:
/* 204 */             if (this.plast.get()) this.stageItem = "пласт";
/*     */             
/*     */             break;
/*     */           case 11:
/* 208 */             if (this.pilb.get()) this.stageItem = "явная пыль";
/*     */             
/*     */             break;
/*     */           case 12:
/* 212 */             if (this.heal.get()) this.stageItem = "исцел";
/*     */             
/*     */             break;
/*     */           case 13:
/* 216 */             if (this.totem.get()) this.stageItem = "тотем";
/*     */             
/*     */             break;
/*     */           case 14:
/* 220 */             if (!this.weaponNo.get()) this.stageItem = (this.weaponTrident.get() ? "трезубец" : "арбалет") + " крушителя";
/*     */             
/*     */             break;
/*     */           case 15:
/* 224 */             if (this.weaponCrossbow.get()) this.stageItem = this.arrow.getMode().getName();
/*     */             
/*     */             break;
/*     */           case 16:
/* 228 */             if (this.shulker.get()) this.stageItem = "шалкер";
/*     */             
/*     */             break;
/*     */           case 17:
/* 232 */             if (this.exp.get()) this.stageItem = "бутылочка опыта";
/*     */             
/*     */             break;
/*     */           case 18:
/* 236 */             if (!this.cleanNo.get()) this.stageItem = this.cleanMilk.get() ? "молоко" : "божья аура";
/*     */             
/*     */             break;
/*     */           case 19:
/* 240 */             if (!this.leftNo.get()) this.stageItem = this.leftTal.get() ? this.selectTal.getMode().getName() : this.selectSphere.getMode().getName();
/*     */             
/*     */             break;
/*     */           case 20:
/* 244 */             if (!this.potionNo.get()) this.stageItem = "силка";
/*     */             
/*     */             break;
/*     */           case 21:
/* 248 */             if (this.potionSplit.get()) this.stageItem = "скорка";
/*     */             
/*     */             break;
/*     */           case 22:
/* 252 */             if (this.elytra.get()) this.stageItem = this.elytraDef.get() ? "элитры" : "элитры крушителя";
/*     */             
/*     */             break;
/*     */           case 23:
/* 256 */             if (this.firework.get()) this.stageItem = "фейерверк";
/*     */             
/*     */             break;
/*     */           case 24:
/* 260 */             if (this.food.get()) this.stageItem = "Золотая морковь";
/*     */             
/*     */             break;
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 270 */         if (this.stageItem.isBlank()) {
/* 271 */           this.stage++;
/*     */           
/*     */           return;
/*     */         } 
/* 275 */         search(this.stageItem);
/* 276 */         this.aucTimer.reset();
/* 277 */         this.buyTimer.reset();
/*     */       } 
/*     */       
/* 280 */       if (mc.player.openContainer instanceof net.minecraft.inventory.container.ChestContainer) {
/* 281 */         ChestScreen chest = (ChestScreen)mc.currentScreen;
/* 282 */         String chestName = chest.getTitle().getString();
/*     */         
/* 284 */         if (chestName.contains("Поиск") && chestName.contains(this.stageItem.replace("Пандоры", "Пандора"))) {
/* 285 */           ItemStack cheapestStack = null;
/* 286 */           int minPrice = Integer.MAX_VALUE;
/* 287 */           Slot slot = null;
/*     */           
/* 289 */           for (Slot s : mc.player.openContainer.inventorySlots) {
/* 290 */             ItemStack stack = s.getStack();
/*     */             
/* 292 */             if (stack.isEmpty())
/*     */               continue; 
/* 294 */             int price = ItemUtility.getPrice(stack) / stack.getCount();
/*     */             
/* 296 */             if (price > 0 && price < minPrice && canBuy(stack)) {
/* 297 */               minPrice = price;
/* 298 */               cheapestStack = stack;
/* 299 */               slot = s;
/*     */             } 
/*     */           } 
/*     */           
/* 303 */           if (cheapestStack != null) {
/* 304 */             String displayName = cheapestStack.getDisplayName().getString();
/* 305 */             mc.player.connection.sendPacket((IPacket)new CClickWindowPacket(mc.player.openContainer.windowId, slot.slotNumber, 0, ClickType.QUICK_MOVE, mc.player.openContainer.getSlot(slot.slotNumber).getStack(), mc.player.openContainer.getNextTransactionID(mc.player.inventory)));
/* 306 */             Chat.msg("Покупаю " + displayName + " за " + minPrice + "..");
/* 307 */             this.buyTimer.reset();
/* 308 */             mc.player.closeScreen();
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 314 */     if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event; IPacket iPacket = e.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket;
/* 315 */         String message = packet.getChatComponent().getString();
/*     */         
/* 317 */         if (message.contains("Вы успешно купили")) {
/* 318 */           if ((this.stage == 6 && this.chorus.get() && Player.count(Items.CHORUS_FRUIT) < this.chorusCount.get() - 1.0F) || (this.stage == 5 && this.pearl
/* 319 */             .get() && Player.count(Items.ENDER_PEARL) < this.pearlCount.get() - 1.0F) || (this.stage == 7 && this.charka
/* 320 */             .get() && Player.count(Items.ENCHANTED_GOLDEN_APPLE) < this.charkaCount.get() - 1.0F) || (this.stage == 8 && this.gapple
/* 321 */             .get() && Player.count(Items.GOLDEN_APPLE) < this.gappleCount.get() - 1.0F) || (this.stage == 9 && this.trap
/* 322 */             .get() && Player.count(Items.NETHERITE_SCRAP) < this.trapCount.get() - 1.0F) || (this.stage == 10 && this.plast
/* 323 */             .get() && Player.count(Items.DRIED_KELP) < this.plastCount.get() - 1.0F) || (this.stage == 11 && this.pilb
/* 324 */             .get() && Player.count(Items.SUGAR) < this.pilbCount.get() - 1.0F) || (this.stage == 12 && this.heal
/* 325 */             .get() && Player.count(Items.POTION) < this.healCount.get() - 1.0F) || (this.stage == 13 && this.totem
/* 326 */             .get() && Player.count(Items.TOTEM_OF_UNDYING) < this.totemCount.get() - 1.0F) || (this.stage == 15 && this.weaponCrossbow
/* 327 */             .get() && Player.count(Items.ARROW) < this.arrowCount.get() - 1.0F) || (this.stage == 16 && this.shulker
/* 328 */             .get() && Player.shulkerCount() < this.shulkerCount.get() - 1.0F) || (this.stage == 17 && this.exp
/* 329 */             .get() && Player.stackSize(Items.EXPERIENCE_BOTTLE) < this.expCount.get() - 1.0F) || (this.stage == 18 && ((this.cleanBozhka
/* 330 */             .get() && Player.stackSize(Items.PHANTOM_MEMBRANE) < this.bozhkaCount.get() - 1.0F) || (this.cleanMilk.get() && Player.stackSize(Items.MILK_BUCKET) < this.milkCount.get() - 1.0F))) || (this.stage == 23 && this.firework
/* 331 */             .get() && Player.stackSize(Items.FIREWORK_ROCKET) < this.fireworkCount.get() - 1.0F)) {
/*     */             return;
/*     */           }
/*     */           
/* 335 */           this.stage++;
/* 336 */           this.aucTimer.setStartTime(System.currentTimeMillis() - 1000L);
/*     */         }  }
/*     */        }
/*     */   
/*     */   }
/*     */   private boolean canBuy(ItemStack stack) {
/* 342 */     if (!this.aucTimer.passed(2000L) || !this.buyTimer.passed(1000L)) return false;
/*     */ 
/*     */     
/* 345 */     if (stack.getItem() instanceof net.minecraft.item.ArmorItem) {
/* 346 */       if (EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack) > 0) return false; 
/* 347 */       if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) <= 0) return false; 
/* 348 */       if (EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack) < 5) return false;
/*     */ 
/*     */       
/* 351 */       if (EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) < 5 && this.armorProtect5.get()) return false; 
/* 352 */       if (EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) < 4) return false;
/*     */     
/*     */     } 
/*     */     
/* 356 */     if (stack.getItem() instanceof net.minecraft.item.PotionItem)
/* 357 */       if (this.stage != 20 || (this.potionBoth.get() ? (ItemUtility.contains(stack, "Сила III") && ItemUtility.contains(stack, "Скорость III")) : ItemUtility.contains(stack, "Сила III"))) {
/*     */ 
/*     */ 
/*     */         
/* 361 */         if (this.stage == 21 && this.potionSplit.get() && !ItemUtility.contains(stack, "Скорость III"))
/* 362 */           return false; 
/*     */       } else {
/*     */         return false;
/*     */       }  
/* 366 */     Item item = stack.getItem(); if (item instanceof ElytraItem) { ElytraItem elytraItem = (ElytraItem)item;
/* 367 */       int max = stack.getMaxDamage(), cur = max - stack.getDamage();
/* 368 */       double perc = cur / max;
/* 369 */       if (perc < 0.699999988079071D) return false;
/*     */        }
/*     */     
/* 372 */     if (this.stage == 6 && ItemUtility.getPrice(stack) < 100000) {
/* 373 */       return false;
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
/* 393 */     return true;
/*     */   }
/*     */   
/*     */   private void search(String arg) {
/* 397 */     mc.player.sendChatMessage("/ah search " + arg);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/* 402 */     this.stage = 0;
/* 403 */     this.stageItem = "";
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\AutoKit.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */