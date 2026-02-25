/*     */ package fun.rockstarity.client.modules.player;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.Binding;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import net.minecraft.client.gui.screen.Screen;
/*     */ import net.minecraft.inventory.container.Container;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CCloseWindowPacket;
/*     */ import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
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
/*     */ @Info(name = "EnderChest", desc = "Утилиты для эндер-сундука", type = Category.PLAYER)
/*     */ public class EnderChest
/*     */   extends Module
/*     */ {
/*  39 */   protected final CheckBox save = (new CheckBox((Bindable)this, "Сохранять")).set(true).desc("Сохраняет открытие эндер-сундука(можно открыть в любое время). Во время того как он открыт вы не можете открывать свой инвентарь");
/*  40 */   protected final Binding close = (new Binding((Bindable)this, "Закрыть сундук")).hide(() -> Boolean.valueOf(!this.save.get()));
/*  41 */   protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); public Container getContainer() {
/*  42 */     return this.container;
/*     */   }
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
/*     */   protected Container container;
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
/*     */   protected Screen screen;
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
/*     */   public void onEvent(Event event) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield save : Lfun/rockstarity/api/modules/settings/list/CheckBox;
/*     */     //   4: invokevirtual get : ()Z
/*     */     //   7: ifeq -> 521
/*     */     //   10: aload_1
/*     */     //   11: instanceof fun/rockstarity/api/events/list/game/inputs/EventKey
/*     */     //   14: ifeq -> 70
/*     */     //   17: aload_1
/*     */     //   18: checkcast fun/rockstarity/api/events/list/game/inputs/EventKey
/*     */     //   21: astore_2
/*     */     //   22: aload_0
/*     */     //   23: getfield close : Lfun/rockstarity/api/modules/settings/list/Binding;
/*     */     //   26: aload_2
/*     */     //   27: invokevirtual check : (Lfun/rockstarity/api/events/list/game/inputs/EventKey;)Z
/*     */     //   30: ifeq -> 70
/*     */     //   33: aload_0
/*     */     //   34: invokevirtual check : ()Z
/*     */     //   37: ifeq -> 70
/*     */     //   40: getstatic fun/rockstarity/client/modules/player/EnderChest.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   43: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   46: getfield connection : Lnet/minecraft/client/network/play/ClientPlayNetHandler;
/*     */     //   49: new net/minecraft/network/play/client/CCloseWindowPacket
/*     */     //   52: dup
/*     */     //   53: aload_0
/*     */     //   54: getfield container : Lnet/minecraft/inventory/container/Container;
/*     */     //   57: getfield windowId : I
/*     */     //   60: invokespecial <init> : (I)V
/*     */     //   63: invokevirtual sendPacketSilent : (Lnet/minecraft/network/IPacket;)V
/*     */     //   66: aload_0
/*     */     //   67: invokevirtual reset : ()V
/*     */     //   70: aload_1
/*     */     //   71: instanceof fun/rockstarity/api/events/list/game/packet/EventSendPacket
/*     */     //   74: ifeq -> 401
/*     */     //   77: aload_1
/*     */     //   78: checkcast fun/rockstarity/api/events/list/game/packet/EventSendPacket
/*     */     //   81: astore_2
/*     */     //   82: aload_2
/*     */     //   83: invokevirtual getPacket : ()Lnet/minecraft/network/IPacket;
/*     */     //   86: astore_3
/*     */     //   87: aload_3
/*     */     //   88: instanceof net/minecraft/network/play/server/SCloseWindowPacket
/*     */     //   91: ifeq -> 105
/*     */     //   94: aload_0
/*     */     //   95: invokevirtual check : ()Z
/*     */     //   98: ifeq -> 105
/*     */     //   101: aload_0
/*     */     //   102: invokevirtual reset : ()V
/*     */     //   105: aload_3
/*     */     //   106: instanceof net/minecraft/network/play/client/CPlayerDiggingPacket
/*     */     //   109: ifeq -> 254
/*     */     //   112: aload_3
/*     */     //   113: checkcast net/minecraft/network/play/client/CPlayerDiggingPacket
/*     */     //   116: astore #4
/*     */     //   118: aload #4
/*     */     //   120: invokevirtual getAction : ()Lnet/minecraft/network/play/client/CPlayerDiggingPacket$Action;
/*     */     //   123: getstatic net/minecraft/network/play/client/CPlayerDiggingPacket$Action.SWAP_ITEM_WITH_OFFHAND : Lnet/minecraft/network/play/client/CPlayerDiggingPacket$Action;
/*     */     //   126: if_acmpne -> 254
/*     */     //   129: aload_0
/*     */     //   130: invokevirtual check : ()Z
/*     */     //   133: ifeq -> 254
/*     */     //   136: getstatic fun/rockstarity/client/modules/player/EnderChest.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   139: getfield currentScreen : Lnet/minecraft/client/gui/screen/Screen;
/*     */     //   142: ifnonnull -> 254
/*     */     //   145: aload_0
/*     */     //   146: getfield container : Lnet/minecraft/inventory/container/Container;
/*     */     //   149: invokevirtual getInventory : ()Lnet/minecraft/util/NonNullList;
/*     */     //   152: invokevirtual size : ()I
/*     */     //   155: bipush #9
/*     */     //   157: isub
/*     */     //   158: getstatic fun/rockstarity/client/modules/player/EnderChest.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   161: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   164: getfield inventory : Lnet/minecraft/entity/player/PlayerInventory;
/*     */     //   167: getfield currentItem : I
/*     */     //   170: iadd
/*     */     //   171: istore #5
/*     */     //   173: aload_0
/*     */     //   174: getfield container : Lnet/minecraft/inventory/container/Container;
/*     */     //   177: getstatic fun/rockstarity/client/modules/player/EnderChest.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   180: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   183: getfield inventory : Lnet/minecraft/entity/player/PlayerInventory;
/*     */     //   186: invokevirtual getNextTransactionID : (Lnet/minecraft/entity/player/PlayerInventory;)S
/*     */     //   189: istore #6
/*     */     //   191: aload_0
/*     */     //   192: getfield container : Lnet/minecraft/inventory/container/Container;
/*     */     //   195: iload #5
/*     */     //   197: bipush #40
/*     */     //   199: getstatic net/minecraft/inventory/container/ClickType.SWAP : Lnet/minecraft/inventory/container/ClickType;
/*     */     //   202: getstatic fun/rockstarity/client/modules/player/EnderChest.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   205: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   208: invokevirtual slotClick : (IILnet/minecraft/inventory/container/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;
/*     */     //   211: astore #7
/*     */     //   213: getstatic fun/rockstarity/client/modules/player/EnderChest.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   216: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   219: getfield connection : Lnet/minecraft/client/network/play/ClientPlayNetHandler;
/*     */     //   222: new net/minecraft/network/play/client/CClickWindowPacket
/*     */     //   225: dup
/*     */     //   226: aload_0
/*     */     //   227: getfield container : Lnet/minecraft/inventory/container/Container;
/*     */     //   230: getfield windowId : I
/*     */     //   233: iload #5
/*     */     //   235: bipush #40
/*     */     //   237: getstatic net/minecraft/inventory/container/ClickType.SWAP : Lnet/minecraft/inventory/container/ClickType;
/*     */     //   240: aload #7
/*     */     //   242: iload #6
/*     */     //   244: invokespecial <init> : (IIILnet/minecraft/inventory/container/ClickType;Lnet/minecraft/item/ItemStack;S)V
/*     */     //   247: invokevirtual sendPacket : (Lnet/minecraft/network/IPacket;)V
/*     */     //   250: aload_2
/*     */     //   251: invokevirtual cancel : ()V
/*     */     //   254: aload_3
/*     */     //   255: instanceof net/minecraft/network/play/client/CPlayerTryUseItemOnBlockPacket
/*     */     //   258: ifeq -> 401
/*     */     //   261: aload_3
/*     */     //   262: checkcast net/minecraft/network/play/client/CPlayerTryUseItemOnBlockPacket
/*     */     //   265: astore #4
/*     */     //   267: aload_0
/*     */     //   268: invokevirtual check : ()Z
/*     */     //   271: ifeq -> 401
/*     */     //   274: getstatic fun/rockstarity/client/modules/player/EnderChest.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   277: getfield world : Lnet/minecraft/client/world/ClientWorld;
/*     */     //   280: getfield loadedTileEntityList : Ljava/util/List;
/*     */     //   283: invokeinterface iterator : ()Ljava/util/Iterator;
/*     */     //   288: astore #5
/*     */     //   290: aload #5
/*     */     //   292: invokeinterface hasNext : ()Z
/*     */     //   297: ifeq -> 401
/*     */     //   300: aload #5
/*     */     //   302: invokeinterface next : ()Ljava/lang/Object;
/*     */     //   307: checkcast net/minecraft/tileentity/TileEntity
/*     */     //   310: astore #6
/*     */     //   312: aload #6
/*     */     //   314: invokevirtual getPos : ()Lnet/minecraft/util/math/BlockPos;
/*     */     //   317: new net/minecraft/util/math/BlockPos
/*     */     //   320: dup
/*     */     //   321: aload #4
/*     */     //   323: invokevirtual func_218794_c : ()Lnet/minecraft/util/math/BlockRayTraceResult;
/*     */     //   326: invokevirtual getPos : ()Lnet/minecraft/util/math/BlockPos;
/*     */     //   329: invokevirtual getVec : ()Lnet/minecraft/util/math/vector/Vector3d;
/*     */     //   332: invokespecial <init> : (Lnet/minecraft/util/math/vector/Vector3d;)V
/*     */     //   335: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   338: ifeq -> 398
/*     */     //   341: getstatic fun/rockstarity/client/modules/player/EnderChest.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   344: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   347: getfield connection : Lnet/minecraft/client/network/play/ClientPlayNetHandler;
/*     */     //   350: new net/minecraft/network/play/client/CCloseWindowPacket
/*     */     //   353: dup
/*     */     //   354: aload_0
/*     */     //   355: getfield container : Lnet/minecraft/inventory/container/Container;
/*     */     //   358: getfield windowId : I
/*     */     //   361: invokespecial <init> : (I)V
/*     */     //   364: invokevirtual sendPacketSilent : (Lnet/minecraft/network/IPacket;)V
/*     */     //   367: aload_0
/*     */     //   368: getfield scheduler : Ljava/util/concurrent/ScheduledExecutorService;
/*     */     //   371: aload #4
/*     */     //   373: <illegal opcode> run : (Lnet/minecraft/network/play/client/CPlayerTryUseItemOnBlockPacket;)Ljava/lang/Runnable;
/*     */     //   378: ldc2_w 50
/*     */     //   381: getstatic java/util/concurrent/TimeUnit.MILLISECONDS : Ljava/util/concurrent/TimeUnit;
/*     */     //   384: invokeinterface schedule : (Ljava/lang/Runnable;JLjava/util/concurrent/TimeUnit;)Ljava/util/concurrent/ScheduledFuture;
/*     */     //   389: pop
/*     */     //   390: aload_0
/*     */     //   391: invokevirtual reset : ()V
/*     */     //   394: aload_2
/*     */     //   395: invokevirtual cancel : ()V
/*     */     //   398: goto -> 290
/*     */     //   401: aload_1
/*     */     //   402: instanceof fun/rockstarity/api/events/list/game/EventCloseScreen
/*     */     //   405: ifeq -> 510
/*     */     //   408: aload_1
/*     */     //   409: checkcast fun/rockstarity/api/events/list/game/EventCloseScreen
/*     */     //   412: astore_2
/*     */     //   413: aload_2
/*     */     //   414: invokevirtual getScreen : ()Lnet/minecraft/client/gui/screen/Screen;
/*     */     //   417: astore #4
/*     */     //   419: aload #4
/*     */     //   421: instanceof net/minecraft/client/gui/screen/inventory/ChestScreen
/*     */     //   424: ifeq -> 499
/*     */     //   427: aload #4
/*     */     //   429: checkcast net/minecraft/client/gui/screen/inventory/ChestScreen
/*     */     //   432: astore_3
/*     */     //   433: aload_3
/*     */     //   434: invokevirtual getTitle : ()Lnet/minecraft/util/text/ITextComponent;
/*     */     //   437: invokeinterface getString : ()Ljava/lang/String;
/*     */     //   442: ldc_w 'Эндер-сундук'
/*     */     //   445: invokevirtual contains : (Ljava/lang/CharSequence;)Z
/*     */     //   448: ifne -> 469
/*     */     //   451: aload_3
/*     */     //   452: invokevirtual getTitle : ()Lnet/minecraft/util/text/ITextComponent;
/*     */     //   455: invokeinterface getString : ()Ljava/lang/String;
/*     */     //   460: ldc_w 'Ender'
/*     */     //   463: invokevirtual contains : (Ljava/lang/CharSequence;)Z
/*     */     //   466: ifeq -> 499
/*     */     //   469: aload_0
/*     */     //   470: getstatic fun/rockstarity/client/modules/player/EnderChest.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   473: getfield currentScreen : Lnet/minecraft/client/gui/screen/Screen;
/*     */     //   476: putfield screen : Lnet/minecraft/client/gui/screen/Screen;
/*     */     //   479: aload_0
/*     */     //   480: getstatic fun/rockstarity/client/modules/player/EnderChest.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   483: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   486: getfield openContainer : Lnet/minecraft/inventory/container/Container;
/*     */     //   489: putfield container : Lnet/minecraft/inventory/container/Container;
/*     */     //   492: aload_2
/*     */     //   493: invokevirtual cancel : ()V
/*     */     //   496: goto -> 510
/*     */     //   499: aload_0
/*     */     //   500: invokevirtual check : ()Z
/*     */     //   503: ifeq -> 510
/*     */     //   506: aload_2
/*     */     //   507: invokevirtual cancel : ()V
/*     */     //   510: aload_1
/*     */     //   511: instanceof fun/rockstarity/api/events/list/game/EventWorldChange
/*     */     //   514: ifeq -> 521
/*     */     //   517: aload_0
/*     */     //   518: invokevirtual reset : ()V
/*     */     //   521: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #47	-> 0
/*     */     //   #48	-> 10
/*     */     //   #49	-> 22
/*     */     //   #50	-> 40
/*     */     //   #51	-> 66
/*     */     //   #55	-> 70
/*     */     //   #56	-> 82
/*     */     //   #57	-> 87
/*     */     //   #58	-> 101
/*     */     //   #61	-> 105
/*     */     //   #62	-> 145
/*     */     //   #63	-> 173
/*     */     //   #64	-> 191
/*     */     //   #65	-> 213
/*     */     //   #66	-> 250
/*     */     //   #69	-> 254
/*     */     //   #70	-> 274
/*     */     //   #71	-> 312
/*     */     //   #72	-> 341
/*     */     //   #73	-> 367
/*     */     //   #74	-> 390
/*     */     //   #75	-> 394
/*     */     //   #77	-> 398
/*     */     //   #81	-> 401
/*     */     //   #82	-> 413
/*     */     //   #83	-> 469
/*     */     //   #84	-> 479
/*     */     //   #85	-> 492
/*     */     //   #86	-> 499
/*     */     //   #87	-> 506
/*     */     //   #91	-> 510
/*     */     //   #92	-> 517
/*     */     //   #95	-> 521
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   22	48	2	e	Lfun/rockstarity/api/events/list/game/inputs/EventKey;
/*     */     //   173	81	5	slot	I
/*     */     //   191	63	6	nextTransactionID	S
/*     */     //   213	41	7	itemstack	Lnet/minecraft/item/ItemStack;
/*     */     //   118	136	4	digging	Lnet/minecraft/network/play/client/CPlayerDiggingPacket;
/*     */     //   312	86	6	entity	Lnet/minecraft/tileentity/TileEntity;
/*     */     //   267	134	4	useItemOnBlock	Lnet/minecraft/network/play/client/CPlayerTryUseItemOnBlockPacket;
/*     */     //   87	314	3	packet	Lnet/minecraft/network/IPacket;
/*     */     //   82	319	2	e	Lfun/rockstarity/api/events/list/game/packet/EventSendPacket;
/*     */     //   433	66	3	scr	Lnet/minecraft/client/gui/screen/inventory/ChestScreen;
/*     */     //   413	97	2	e	Lfun/rockstarity/api/events/list/game/EventCloseScreen;
/*     */     //   0	522	0	this	Lfun/rockstarity/client/modules/player/EnderChest;
/*     */     //   0	522	1	event	Lfun/rockstarity/api/events/Event;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   87	314	3	packet	Lnet/minecraft/network/IPacket<*>;
/*     */   }
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
/*     */   public void enderChestLoad() {
/*  98 */     mc.displayGuiScreen(this.screen);
/*  99 */     mc.player.openContainer = this.container;
/*     */   }
/*     */   
/*     */   public void reset() {
/* 103 */     this.screen = null;
/* 104 */     this.container = null;
/*     */   }
/*     */   
/*     */   public boolean check() {
/* 108 */     return (get() && this.save.get() && this.screen != null && this.container != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEnable() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 118 */     mc.player.connection.sendPacketSilent((IPacket)new CCloseWindowPacket(this.container.windowId));
/* 119 */     reset();
/*     */   }
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\player\EnderChest.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */