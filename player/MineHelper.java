/*     */ package fun.rockstarity.client.modules.player;
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.EventBreakingBad;
/*     */ import fun.rockstarity.api.events.list.game.inputs.EventKey;
/*     */ import fun.rockstarity.api.events.list.player.EventMotion;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.helpers.player.Inventory;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.Binding;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.render.ui.alerts.AlertType;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.inventory.container.ClickType;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.item.Items;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
/*     */ import net.minecraft.util.Hand;
/*     */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*     */ 
/*     */ @Info(name = "MineHelper", desc = "Помощник на шахте", type = Category.PLAYER)
/*     */ public class MineHelper extends Module {
/*  27 */   private final Binding fixBind = new Binding((Bindable)this, "Кнопка починки");
/*  28 */   private final CheckBox save = new CheckBox((Bindable)this, "Сохранять кирку");
/*     */   
/*     */   private boolean release = false;
/*  31 */   private final TimerUtility timer = new TimerUtility();
/*     */   
/*     */   private boolean cursorCheck;
/*     */   
/*     */   public void onEvent(Event event) {
/*  36 */     if (event instanceof EventKey) { EventKey e = (EventKey)event; handleKey(e); }
/*     */     
/*  38 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/*  39 */       Item mainHand = mc.player.getHeldItemMainhand().getItem();
/*  40 */       ItemStack stack = mc.player.getHeldItemMainhand();
/*  41 */       ItemStack cursorStack = mc.player.inventory.getItemStack();
/*     */       
/*  43 */       int max = stack.getMaxDamage(), cur = max - stack.getDamage();
/*     */       
/*  45 */       double perc = cur / max;
/*     */       
/*  47 */       if (this.release) {
/*  48 */         if (!cursorStack.isEmpty()) {
/*  49 */           this.cursorCheck = true;
/*  50 */           if (this.timer.passed(200L)) {
/*  51 */             int emptySlot = findEmptySlot();
/*  52 */             if (emptySlot != -1)
/*  53 */               mc.playerController.windowClick(0, (emptySlot < 9) ? (emptySlot + 36) : emptySlot, 0, ClickType.PICKUP, (PlayerEntity)mc.player); 
/*     */           } 
/*     */           return;
/*     */         } 
/*  57 */         if (this.cursorCheck) {
/*  58 */           this.cursorCheck = false;
/*  59 */           this.timer.reset();
/*     */         } 
/*  61 */         if (mainHand == Items.NETHERITE_PICKAXE || mainHand == Items.DIAMOND_PICKAXE) if ((((findPickaxe() != -1) ? 1 : 0) & ((perc < 0.95D) ? 1 : 0)) != 0) {
/*     */             
/*  63 */             int exp = findExp();
/*     */             
/*  65 */             if (mc.player.getHeldItemOffhand().getItem() != Items.EXPERIENCE_BOTTLE) {
/*  66 */               if (exp != -1) {
/*  67 */                 Inventory.moveItem(exp, 45, true);
/*     */               }
/*  69 */             } else if (this.timer.passed(100L)) {
/*  70 */               mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.OFF_HAND));
/*  71 */               this.timer.reset();
/*     */             } 
/*     */           }  
/*     */       } 
/*     */     } 
/*  76 */     if (event instanceof EventMotion) { EventMotion e = (EventMotion)event; if (this.release) {
/*  77 */         e.setPitch(80.0F);
/*  78 */         mc.player.rotationPitchHead = 80.0F;
/*     */       }  }
/*     */     
/*  81 */     if (event instanceof EventBreakingBad) { EventBreakingBad e = (EventBreakingBad)event; if (this.save.get()) {
/*  82 */         ItemStack stack = mc.player.getHeldItemMainhand();
/*     */         
/*  84 */         int max = stack.getMaxDamage(), cur = max - stack.getDamage();
/*     */         
/*  86 */         double perc = cur / max;
/*     */         
/*  88 */         if (perc < 0.1D && (stack.getItem() == Items.DIAMOND_PICKAXE || stack.getItem() == Items.NETHERITE_PICKAXE)) {
/*  89 */           e.cancel();
/*  90 */           if (this.timer.passed(800L)) {
/*  91 */             rock.getAlertHandler().alert("Кирка на грани поломки!", AlertType.ERROR);
/*  92 */             this.timer.reset();
/*     */           } 
/*     */         } 
/*     */       }  }
/*     */   
/*     */   }
/*     */   private void handleKey(EventKey e) {
/*  99 */     if (mc.currentScreen == null && this.fixBind.getBindByKey(e).isPresent()) {
/* 100 */       this.release = !e.isReleased();
/* 101 */       if (e.isReleased()) mc.playerController.windowClick(0, 45, 0, ClickType.QUICK_MOVE, (PlayerEntity)mc.player); 
/*     */     } 
/*     */   }
/*     */   
/*     */   @NativeInclude
/*     */   private int findPickaxe() {
/* 107 */     for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
/* 108 */       ItemStack stack = (ItemStack)mc.player.inventory.mainInventory.get(i);
/* 109 */       if (stack.getItem() == Items.NETHERITE_PICKAXE || stack.getItem() == Items.DIAMOND_PICKAXE) {
/* 110 */         return (i < 9) ? (36 + i) : i;
/*     */       }
/*     */     } 
/* 113 */     return -1;
/*     */   }
/*     */   
/*     */   private int findEmptySlot() {
/* 117 */     for (int i = 0; i < 36; i++) {
/* 118 */       if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
/* 119 */         return i;
/*     */       }
/*     */     } 
/* 122 */     return -1;
/*     */   }
/*     */   
/*     */   private int findExp() {
/* 126 */     if (mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) {
/* 127 */       return 45;
/*     */     }
/*     */     
/* 130 */     for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
/* 131 */       ItemStack stack = (ItemStack)mc.player.inventory.mainInventory.get(i);
/* 132 */       if (stack.getItem() == Items.EXPERIENCE_BOTTLE) {
/* 133 */         return (i < 9) ? (36 + i) : i;
/*     */       }
/*     */     } 
/* 136 */     return -1;
/*     */   }
/*     */   
/*     */   public void onEnable() {}
/*     */   
/*     */   public void onDisable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\MineHelper.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */