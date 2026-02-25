/*    */ package fun.rockstarity.client.modules.player;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.helpers.game.Server;
/*    */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*    */ import fun.rockstarity.api.helpers.player.InvUtility;
/*    */ import fun.rockstarity.api.helpers.player.Player;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.Select;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ import net.minecraft.item.Items;
/*    */ import net.minecraft.potion.Effects;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "AutoUse", desc = "Автоматическое использование предметов", type = Category.PLAYER)
/*    */ public class AutoUse
/*    */   extends Module
/*    */ {
/* 29 */   protected final Select items = (new Select((Bindable)this, "Предметы")).min(1).desc("Предметы, которые будут использоваться автоматически");
/* 30 */   protected final Select.Element godaura = (new Select.Element(this.items, "Божья аура")).set(true);
/* 31 */   protected final Select.Element dezz = (new Select.Element(this.items, "Дезориентация")).set(true);
/* 32 */   protected final Select.Element yav = (new Select.Element(this.items, "Явная пыль")).set(true);
/*    */   
/* 34 */   protected final Select godauraConditions = (new Select((Bindable)this, "Условия для божки")).desc("Условия, при которых будет использоваться божья аура").hide(() -> Boolean.valueOf(!this.godaura.get()));
/* 35 */   protected final Select.Element godauraCD = (new Select.Element(this.godauraConditions, "Чарка без кд")).set(true);
/*    */   
/* 37 */   protected final Select dezzConditions = (new Select((Bindable)this, "Условия для дезорки")).desc("Условия, при которых будет использоваться дезориентация").hide(() -> Boolean.valueOf(!this.dezz.get()));
/* 38 */   protected final Select.Element revenge = (new Select.Element(this.dezzConditions, "В ответ")).set(true);
/* 39 */   protected final Select.Element minhealth = (new Select.Element(this.dezzConditions, "Если мало хп")).set(true);
/* 40 */   protected final Slider health = (new Slider((Bindable)this.minhealth, "Здоровье")).min(1.0F).max(19.0F).inc(0.5F).set(6.0F).hide(() -> Boolean.valueOf(!this.minhealth.get()));
/*    */   
/* 42 */   protected final TimerUtility dezzTimer = new TimerUtility();
/* 43 */   protected final TimerUtility godTimer = new TimerUtility();
/* 44 */   protected final TimerUtility sugarTimer = new TimerUtility();
/*    */ 
/*    */   
/*    */   public void onEvent(Event event) {
/* 48 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 49 */       if (this.dezzTimer.passed(1000L) && this.dezz.get() && 
/* 50 */         !mc.player.isHandActive() && 
/* 51 */         !mc.player.getCooldownTracker().hasCooldown(Items.ENDER_EYE) && 
/* 52 */         Player.findItem(44, Items.ENDER_EYE) != -1 && ((
/* 53 */         this.revenge.get() && mc.player.isPotionActive(Effects.MINING_FATIGUE) && mc.player.get(Effects.MINING_FATIGUE).getAmplifier() > 2) || (this.minhealth
/* 54 */         .get() && mc.player.getHealth() + mc.player.getAbsorptionAmount() < this.health.get()))) {
/* 55 */         InvUtility.use(Items.ENDER_EYE);
/* 56 */         this.dezzTimer.reset();
/*    */       } 
/*    */ 
/*    */       
/* 60 */       if (this.godTimer.passed(1000L) && this.godaura.get() && 
/* 61 */         !mc.player.isHandActive() && (
/* 62 */         !this.godauraCD.get() || !mc.player.getCooldownTracker().hasCooldown(Items.ENCHANTED_GOLDEN_APPLE)) && 
/* 63 */         !mc.player.getCooldownTracker().hasCooldown(Items.PHANTOM_MEMBRANE) && 
/* 64 */         Player.findItem(44, Items.PHANTOM_MEMBRANE) != -1 && (
/* 65 */         mc.player.isPotionActive(Effects.JUMP_BOOST) || (mc.player
/* 66 */         .isPotionActive(Effects.WEAKNESS) && mc.player.get(Effects.WEAKNESS).getAmplifier() > 1))) {
/* 67 */         InvUtility.use(Items.PHANTOM_MEMBRANE);
/* 68 */         this.godTimer.reset();
/*    */       } 
/*    */ 
/*    */       
/* 72 */       if (this.sugarTimer.passed(1000L) && this.yav.get() && 
/* 73 */         !mc.player.isHandActive() && 
/* 74 */         Player.findItem(44, Items.SUGAR) != -1 && mc.player.hurtTime > 0)
/*    */       {
/* 76 */         if (Server.hasCT() && 
/* 77 */           !mc.player.getCooldownTracker().hasCooldown(Items.SUGAR)) {
/* 78 */           InvUtility.use(Items.SUGAR);
/* 79 */           this.sugarTimer.reset();
/*    */         } 
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   public void onEnable() {}
/*    */   
/*    */   public void onDisable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\AutoUse.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */