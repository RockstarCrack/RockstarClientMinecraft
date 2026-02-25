/*    */ package fun.rockstarity.client.modules.other;
/*    */ 
/*    */ import fun.rockstarity.api.binds.Bind;
/*    */ import fun.rockstarity.api.binds.BindType;
/*    */ import fun.rockstarity.api.binds.Bindable;
/*    */ import fun.rockstarity.api.connection.globals.ClientAPI;
/*    */ import fun.rockstarity.api.connection.globals.GlobalsThread;
/*    */ import fun.rockstarity.api.connection.globals.ServerAPI;
/*    */ import fun.rockstarity.api.connection.globals.SyncServer;
/*    */ import fun.rockstarity.api.events.Event;
/*    */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*    */ import fun.rockstarity.api.helpers.system.ThreadManager;
/*    */ import fun.rockstarity.api.modules.Category;
/*    */ import fun.rockstarity.api.modules.Info;
/*    */ import fun.rockstarity.api.modules.Module;
/*    */ import fun.rockstarity.api.modules.settings.list.Binding;
/*    */ import fun.rockstarity.api.render.globals.marks.MarkServer;
/*    */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Info(name = "Globals", desc = "Показывает пользователей в табе", type = Category.OTHER, module = {"Emotions"})
/*    */ public class Globals
/*    */   extends Module
/*    */ {
/* 33 */   private final TimerUtility timer = new TimerUtility();
/*    */   private boolean init;
/*    */   
/* 36 */   public Binding getWheelBind() { return this.wheelBind; } final Binding wheelBind = (new Binding((Bindable)this, "Колесо эмоций"))
/* 37 */     .addBind(new Bind(74, BindType.HOLD)).desc("Кнопка, при нажатии которой будет открываться колесо эмоций");
/*    */   
/* 39 */   public Binding getMarkBind() { return this.markBind; } final Binding markBind = (new Binding((Bindable)this, "Клавиша метки"))
/* 40 */     .addBind(new Bind(74, BindType.HOLD)).desc("Кнопка, при нажатии которой будет отправляться метка");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NativeInclude
/*    */   public void onEvent(Event event) {
/* 49 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate) {
/* 50 */       if (this.init) {
/* 51 */         if (mc.player.ticksExisted % 15 == 0) {
/* 52 */           ThreadManager.run(() -> ClientAPI.update(ServerAPI.getClients()));
/*    */         }
/*    */         
/* 55 */         if (mc.player.ticksExisted % 15 == 4) {
/* 56 */           MarkServer.update();
/*    */         }
/*    */         
/* 59 */         if (mc.player.ticksExisted % 15 == 9) {
/* 60 */           SyncServer.update();
/*    */         }
/*    */       } 
/*    */       
/* 64 */       if (!this.init) {
/* 65 */         SyncServer.init();
/* 66 */         ServerAPI.init();
/* 67 */         ServerAPI.updateName();
/* 68 */         ClientAPI.update(ServerAPI.getClients());
/* 69 */         this.init = true;
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   @NativeInclude
/*    */   public void onDisable() {
/* 76 */     ServerAPI.finish();
/* 77 */     SyncServer.finish();
/* 78 */     ClientAPI.USERS.clear();
/* 79 */     GlobalsThread.setInit(false);
/* 80 */     this.init = false;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onEnable() {
/* 85 */     SyncServer.init();
/* 86 */     MarkServer.init();
/* 87 */     ServerAPI.init();
/* 88 */     ServerAPI.updateName();
/*    */   }
/*    */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\Globals.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */