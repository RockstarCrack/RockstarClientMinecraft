/*    */ package fun.rockstarity.client.modules.other;
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*    */ import fun.rockstarity.api.modules.settings.list.Slider;
/*    */ 
/*    */ @Info(name = "AutoBuy", desc = "Авто-покупка предметов", type = Category.OTHER)
/*    */ public class AutoBuy extends Module {
/*    */   private AutoBuyScreen screen;
/*    */   private final Clickable openMenu;
/*    */   private final Slider speed;
/*    */   private final CheckBox swapAn;
/*    */   private final Slider acSpeed;
/*    */   private final Input an1;
/*    */   private final Input an2;
/*    */   private final CheckBox parser;
/*    */   private final Slider parsePercent;
/*    */   
/*    */   public AutoBuyScreen getScreen() {
/*    */     return this.screen;
/*    */   }
/*    */   
/*    */   public Clickable getOpenMenu() {
/*    */     return this.openMenu;
/*    */   }
/*    */   
/*    */   public Slider getSpeed() {
/*    */     return this.speed;
/*    */   }
/*    */   
/*    */   public AutoBuy() {
/* 31 */     this
/*    */ 
/*    */       
/* 34 */       .openMenu = (new Clickable(this, "Меню AutoBuy")).set(() -> handleOpenMenu()).set("Открыть меню").desc("Открытие меню можно назначить на клавишу нажав [ПКМ] по кнопке");
/*    */     
/* 36 */     this
/*    */       
/* 38 */       .speed = (new Slider((Bindable)this, "Скорость обновления")).min(0.0F).max(500.0F).inc(50.0F).set(500.0F).desc("Скорость обновления аукциона. Если установить скорость 500, то античит не будет замедлять работу AutoBuy");
/*    */     
/* 40 */     this
/* 41 */       .swapAn = (new CheckBox((Bindable)this, "Переходить между /an")).desc("Переходит между двумя анархиями, если античит замедляет скорость обновления аукциона больше указанной. Есть риск бана за 4.5");
/*    */     
/* 43 */     this
/*    */ 
/*    */       
/* 46 */       .acSpeed = (new Slider((Bindable)this, "КД для перехода")).min(500.0F).max(1500.0F).inc(50.0F).set(1000.0F).desc("При какой задержке обновления аукциона AutoBuy будет переходить на другую анархию").hide(() -> Boolean.valueOf(!this.swapAn.get()));
/*    */     
/* 48 */     this
/*    */ 
/*    */       
/* 51 */       .an1 = (new Input((Bindable)this, "Первая анархия")).set("/an226").desc("Укажите первую анархию для обхода задержки").hide(() -> Boolean.valueOf(!this.swapAn.get()));
/*    */     
/* 53 */     this
/*    */ 
/*    */       
/* 56 */       .an2 = (new Input((Bindable)this, "Вторая анархия")).set("/an313").desc("Укажите вторую анархию для обхода задержки").hide(() -> Boolean.valueOf(!this.swapAn.get()));
/*    */     
/* 58 */     this
/*    */       
/* 60 */       .parser = (new CheckBox((Bindable)this, "Анализатор цен")).desc("'Парсит' цены с аукциона, чтобы покупать предметы по цене рыночной и продавать по рыночной цене").onEnable(() -> {
/*    */           for (AutoBuyItem item : rock.getAutoBuy().getItems()) {
/*    */             item.setParsed(false);
/*    */           }
/*    */         });
/*    */     
/* 66 */     this
/*    */       
/* 68 */       .parsePercent = (new Slider((Bindable)this, "Процент дешевизны")).min(1.0F).max(100.0F).inc(1.0F).set(44.0F).desc("Укажите, на сколько процентов дешевле рыночной цены AutoBuy будет покупать товары").hide(() -> Boolean.valueOf(!this.parser.get()));
/*    */   } public CheckBox getSwapAn() {
/*    */     return this.swapAn;
/*    */   } private void handleOpenMenu() {
/* 72 */     mc.displayGuiScreen((Screen)this.screen);
/*    */     
/* 74 */     this.screen.getRenderer().getOpening().setForward(true); } public Slider getAcSpeed() {
/*    */     return this.acSpeed;
/*    */   } public Input getAn1() {
/*    */     return this.an1;
/*    */   }
/* 79 */   public void onAllEvent(Event event) { if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && this.screen == null)
/* 80 */       this.screen = new AutoBuyScreen();  }
/*    */    public Input getAn2() {
/*    */     return this.an2;
/*    */   }
/*    */   public void onEvent(Event event) {
/* 85 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate);
/*    */   }
/*    */   
/*    */   public CheckBox getParser() {
/*    */     return this.parser;
/*    */   }
/*    */   
/*    */   public Slider getParsePercent() {
/*    */     return this.parsePercent;
/*    */   }
/*    */   
/*    */   public void onEnable() {}
/*    */   
/*    */   public void onDisable() {}
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\AutoBuy.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */