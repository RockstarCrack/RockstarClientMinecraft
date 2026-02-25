/*     */ package fun.rockstarity.client.modules.player;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.inputs.EventKey;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Player;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.Binding;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Select;
/*     */ import fun.rockstarity.api.render.ui.alerts.AlertType;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.Items;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "AutoSwap", desc = "Автоматически свапает выбранный предмет", type = Category.PLAYER)
/*     */ public class AutoSwap
/*     */   extends Module
/*     */ {
/*  31 */   private final Select mode = new Select((Bindable)this, "Режим");
/*  32 */   private final Select.Element gappleShield = new Select.Element(this.mode, "Геплы/Щит");
/*  33 */   private final Select.Element ballGapple = new Select.Element(this.mode, "Шар/Геплы");
/*  34 */   private final Select.Element sferaTal = new Select.Element(this.mode, "Сфера/Талик");
/*  35 */   private final Select.Element sferasfera = new Select.Element(this.mode, "Сфера/Сфера");
/*  36 */   private final Select.Element taltal = new Select.Element(this.mode, "Талик/Талик");
/*     */   
/*  38 */   private final CheckBox autoSwap = (new CheckBox((Bindable)this, "Автоматический")).desc("Сам свапает шары по ситуации");
/*     */   
/*  40 */   private final Binding gappleShieldBind = (new Binding((Bindable)this, "Кнопка геплы/щит")).hide(() -> Boolean.valueOf((!this.gappleShield.get() || this.autoSwap.get())));
/*  41 */   private final Binding ballGappleBind = (new Binding((Bindable)this, "Кнопка шар/геплы")).hide(() -> Boolean.valueOf((!this.ballGapple.get() || this.autoSwap.get())));
/*  42 */   private final Binding sferaTalBind = (new Binding((Bindable)this, "Кнопка сфера/талик")).hide(() -> Boolean.valueOf((!this.sferaTal.get() || this.autoSwap.get())));
/*  43 */   private final Binding sferasferaBind = (new Binding((Bindable)this, "Кнопка сфера/сфера")).hide(() -> Boolean.valueOf((!this.sferasfera.get() || this.autoSwap.get())));
/*  44 */   private final Binding taltalBind = (new Binding((Bindable)this, "Кнопка талик/талик")).hide(() -> Boolean.valueOf((!this.taltal.get() || this.autoSwap.get())));
/*     */   
/*     */   private boolean swapHeal;
/*  47 */   private final TimerUtility safelyTimer = new TimerUtility();
/*     */ 
/*     */   
/*     */   public void onEvent(Event event) {
/*  51 */     if (event instanceof EventKey) { EventKey key = (EventKey)event; if (mc.currentScreen == null && !this.autoSwap.get()) {
/*  52 */         handleKey(key);
/*     */       } }
/*     */     
/*  55 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && this.autoSwap.get()) {
/*  56 */       if (mc.player.hurtTime > 0) {
/*  57 */         this.safelyTimer.reset();
/*     */       }
/*     */       
/*  60 */       boolean heal = !this.safelyTimer.passed(1000L);
/*     */       
/*  62 */       if (heal != this.swapHeal && (mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING || mc.player.getHeldItemOffhand().isEnchanted())) {
/*  63 */         if (this.gappleShield.get() && findItem(Items.GOLDEN_APPLE) && findItem(Items.SHIELD)) {
/*  64 */           move(Player.findItem(46, Items.GOLDEN_APPLE), Player.findItem(46, Items.SHIELD));
/*     */         }
/*     */         
/*  67 */         if (this.ballGapple.get() && findItem(Items.PLAYER_HEAD) && findItem(Items.GOLDEN_APPLE)) {
/*  68 */           move(Player.findItem(46, Items.PLAYER_HEAD), Player.findItem(46, Items.GOLDEN_APPLE));
/*     */         }
/*     */         
/*  71 */         if (this.sferaTal.get()) {
/*  72 */           swapSferaTal();
/*     */         }
/*     */         
/*  75 */         if (this.sferasfera.get()) {
/*  76 */           swapSferaSfera();
/*     */         }
/*     */         
/*  79 */         if (this.taltal.get()) {
/*  80 */           swapTalTal();
/*     */         }
/*     */       } 
/*     */       
/*  84 */       this.swapHeal = heal;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleKey(EventKey e) {
/*  89 */     if (!e.isReleased()) {
/*  90 */       if (this.gappleShield.get() && this.gappleShieldBind.getBindByKey(e).isPresent() && findItem(Items.GOLDEN_APPLE) && findItem(Items.SHIELD)) {
/*  91 */         move(Player.findItem(46, Items.GOLDEN_APPLE), Player.findItem(46, Items.SHIELD));
/*     */       }
/*     */       
/*  94 */       if (this.ballGapple.get() && this.ballGappleBind.getBindByKey(e).isPresent() && findItem(Items.PLAYER_HEAD) && findItem(Items.GOLDEN_APPLE)) {
/*  95 */         move(Player.findItem(46, Items.PLAYER_HEAD), Player.findItem(46, Items.GOLDEN_APPLE));
/*     */       }
/*     */       
/*  98 */       if (this.sferaTal.get() && this.sferaTalBind.getBindByKey(e).isPresent() && !this.sferaTalBind.isHide()) {
/*  99 */         swapSferaTal();
/*     */       }
/*     */       
/* 102 */       if (this.sferasfera.get() && this.sferasferaBind.getBindByKey(e).isPresent() && !this.sferasferaBind.isHide()) {
/* 103 */         swapSferaSfera();
/*     */       }
/*     */       
/* 106 */       if (this.taltal.get() && this.taltalBind.getBindByKey(e).isPresent() && !this.taltalBind.isHide()) {
/* 107 */         swapTalTal();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean swapSferaTal() {
/* 113 */     int findSfer = Player.findItemDefault(45, Items.PLAYER_HEAD);
/* 114 */     int findTal = findEnchantedTotem();
/*     */     
/* 116 */     findSfer = (findSfer == 40) ? 45 : ((findSfer < 9) ? (36 + findSfer) : findSfer);
/* 117 */     findTal = (findTal == 40) ? 45 : ((findTal < 9) ? (36 + findTal) : findTal);
/*     */     
/* 119 */     if (findSfer != 45 && findTal != 45) {
/* 120 */       findSfer = 45;
/*     */     }
/*     */     
/* 123 */     if (findSfer != -1 && findTal != -1) {
/* 124 */       move(findSfer, findTal);
/* 125 */       if (mc.player.getHeldItemOffhand().getItem() != Items.AIR) {
/* 126 */         rock.getAlertHandler().alert("Свапнул " + mc.player.getHeldItemOffhand().getDisplayName().getString(), AlertType.INFO);
/*     */       }
/* 128 */       return true;
/*     */     } 
/*     */     
/* 131 */     return false;
/*     */   }
/*     */   
/*     */   private boolean swapSferaSfera() {
/* 135 */     int findSfer1 = findHead(0);
/* 136 */     int findSfer2 = findHead(findSfer1 + 1);
/*     */     
/* 138 */     findSfer1 = (findSfer1 == 40) ? 45 : ((findSfer1 < 9) ? (36 + findSfer1) : findSfer1);
/* 139 */     findSfer2 = (findSfer2 == 40) ? 45 : ((findSfer2 < 9) ? (36 + findSfer2) : findSfer2);
/*     */     
/* 141 */     if (findSfer1 != 45 && findSfer2 != 45) {
/* 142 */       findSfer1 = 45;
/*     */     }
/*     */     
/* 145 */     if (findSfer1 != -1 && findSfer2 != -1) {
/* 146 */       move(findSfer1, findSfer2);
/* 147 */       if (mc.player.getHeldItemOffhand().getItem() != Items.AIR)
/* 148 */         Player.overlay(mc.player.getHeldItemOffhand().getDisplayName()); 
/* 149 */       return true;
/*     */     } 
/*     */     
/* 152 */     return false;
/*     */   }
/*     */   
/*     */   private boolean swapTalTal() {
/* 156 */     int findTal1 = findEnchantedTotem(0);
/* 157 */     int findTal2 = findEnchantedTotem(findTal1 + 1);
/*     */     
/* 159 */     findTal1 = (findTal1 == 40) ? 45 : ((findTal1 < 9) ? (36 + findTal1) : findTal1);
/* 160 */     findTal2 = (findTal2 == 40) ? 45 : ((findTal2 < 9) ? (36 + findTal2) : findTal2);
/*     */     
/* 162 */     if (findTal1 != 45 && findTal2 != 45) {
/* 163 */       findTal1 = 45;
/*     */     }
/*     */     
/* 166 */     if (findTal1 != -1 && findTal2 != -1) {
/* 167 */       move(findTal1, findTal2);
/* 168 */       if (mc.player.getHeldItemOffhand().getItem() != Items.AIR)
/* 169 */         Player.overlay(mc.player.getHeldItemOffhand().getDisplayName()); 
/* 170 */       return true;
/*     */     } 
/*     */     
/* 173 */     return false;
/*     */   }
/*     */   
/*     */   protected void move(int one, int two) {
/* 177 */     Player.moveItem(one, two, true);
/*     */   }
/*     */   
/*     */   protected boolean findItem(Item item) {
/* 181 */     return (Player.findItem(46, item) != -1);
/*     */   }
/*     */   
/*     */   protected int findEnchantedTotem() {
/* 185 */     int startSlot = 0;
/*     */     
/* 187 */     ItemStack stack1 = mc.player.inventory.getStackInSlot(40);
/* 188 */     if (stack1.getItem() == Items.TOTEM_OF_UNDYING && stack1.isEnchanted() && startSlot == 0) {
/* 189 */       return 45;
/*     */     }
/*     */     
/* 192 */     for (int i = startSlot; i < 46; i++) {
/* 193 */       ItemStack stack = mc.player.inventory.getStackInSlot(i);
/* 194 */       if (stack.getItem() == Items.TOTEM_OF_UNDYING && stack.isEnchanted()) {
/* 195 */         return i;
/*     */       }
/*     */     } 
/* 198 */     return -1;
/*     */   }
/*     */   
/*     */   protected int findEnchantedTotem(int startSlot) {
/* 202 */     for (int i = startSlot; i < 46; i++) {
/* 203 */       ItemStack stack = mc.player.inventory.getStackInSlot(i);
/* 204 */       if (stack.getItem() == Items.TOTEM_OF_UNDYING && stack.isEnchanted()) {
/* 205 */         return i;
/*     */       }
/*     */     } 
/* 208 */     return -1;
/*     */   }
/*     */   
/*     */   protected int findHead(int startSlot) {
/* 212 */     for (int i = startSlot; i < 46; i++) {
/* 213 */       ItemStack stack = mc.player.inventory.getStackInSlot(i);
/* 214 */       if (stack.getItem() == Items.PLAYER_HEAD) {
/* 215 */         return i;
/*     */       }
/*     */     } 
/* 218 */     return -1;
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */   
/*     */   public void onEnable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\AutoSwap.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */