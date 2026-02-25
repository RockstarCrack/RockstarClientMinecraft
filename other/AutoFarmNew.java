/*     */ package fun.rockstarity.client.modules.other;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
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
/*     */ @Info(name = "AutoFarm", desc = "Автоматически выращивает и собирает культуру", type = Category.OTHER)
/*     */ public class AutoFarmNew
/*     */   extends Module
/*     */ {
/*  44 */   private final Mode culture = (new Mode((Bindable)this, "Культура")).desc("Культура которая будет собираться"); public Mode getCulture() { return this.culture; }
/*  45 */    private final Mode.Element carrot = new Mode.Element(this.culture, "Морковь"); public Mode.Element getCarrot() { return this.carrot; }
/*  46 */    private final Mode.Element potato = new Mode.Element(this.culture, "Картошка"); public Mode.Element getPotato() { return this.potato; }
/*  47 */    private final Mode.Element wheat = new Mode.Element(this.culture, "Пшеница"); public Mode.Element getWheat() { return this.wheat; }
/*  48 */    private final Mode.Element beetroot = new Mode.Element(this.culture, "Свекла"); public Mode.Element getBeetroot() { return this.beetroot; }
/*  49 */    private final Slider delay = (new Slider((Bindable)this, "Задержка сбора")).min(1.0F).max(1000.0F).inc(1.0F).set(200.0F); public Slider getDelay() { return this.delay; }
/*     */   
/*  51 */   private final CheckBox autoExp = (new CheckBox((Bindable)this, "Авто-починка")).set(true); public CheckBox getAutoExp() { return this.autoExp; }
/*  52 */    private final CheckBox autoSell = (new CheckBox((Bindable)this, "Авто-продажа")).set(true); public CheckBox getAutoSell() { return this.autoSell; }
/*  53 */    private final Slider autoExpPerc = (new Slider((Bindable)this, "Начинать чинить при")).min(1.0F).max(100.0F).inc(1.0F).set(15.0F).hide(() -> Boolean.valueOf(!this.autoExp.get())); public Slider getAutoExpPerc() { return this.autoExpPerc; }
/*     */   
/*  55 */   private boolean autoRepair; private boolean autoCos; private final TimerUtility timer = new TimerUtility(); private boolean close; public TimerUtility getTimer() { return this.timer; }
/*  56 */   public boolean isAutoRepair() { return this.autoRepair; } public boolean isAutoCos() { return this.autoCos; } public boolean isClose() { return this.close; }
/*  57 */    private int attempts = 0; private boolean cursorCheck; public int getAttempts() { return this.attempts; } public boolean isCursorCheck() {
/*  58 */     return this.cursorCheck;
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
/*     */   public void onEvent(Event event) {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: instanceof fun/rockstarity/api/events/list/player/EventUpdate
/*     */     //   4: ifeq -> 1173
/*     */     //   7: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   10: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   13: getfield inventory : Lnet/minecraft/entity/player/PlayerInventory;
/*     */     //   16: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   19: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   22: getfield inventory : Lnet/minecraft/entity/player/PlayerInventory;
/*     */     //   25: getfield currentItem : I
/*     */     //   28: invokevirtual getStackInSlot : (I)Lnet/minecraft/item/ItemStack;
/*     */     //   31: astore_2
/*     */     //   32: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   35: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   38: invokevirtual getHeldItemOffhand : ()Lnet/minecraft/item/ItemStack;
/*     */     //   41: astore_3
/*     */     //   42: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   45: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   48: getfield inventory : Lnet/minecraft/entity/player/PlayerInventory;
/*     */     //   51: invokevirtual getItemStack : ()Lnet/minecraft/item/ItemStack;
/*     */     //   54: astore #4
/*     */     //   56: getstatic net/minecraft/item/Items.NETHERITE_HOE : Lnet/minecraft/item/Item;
/*     */     //   59: getstatic net/minecraft/item/Items.DIAMOND_HOE : Lnet/minecraft/item/Item;
/*     */     //   62: invokestatic of : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;
/*     */     //   65: astore #5
/*     */     //   67: getstatic net/minecraft/item/Items.CARROT : Lnet/minecraft/item/Item;
/*     */     //   70: getstatic net/minecraft/item/Items.POTATO : Lnet/minecraft/item/Item;
/*     */     //   73: getstatic net/minecraft/item/Items.WHEAT_SEEDS : Lnet/minecraft/item/Item;
/*     */     //   76: getstatic net/minecraft/item/Items.BEETROOT_SEEDS : Lnet/minecraft/item/Item;
/*     */     //   79: invokestatic of : (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;
/*     */     //   82: astore #6
/*     */     //   84: aload_2
/*     */     //   85: invokevirtual isEmpty : ()Z
/*     */     //   88: istore #7
/*     */     //   90: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   93: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   96: ldc2_w 3.0
/*     */     //   99: fconst_1
/*     */     //   100: iconst_0
/*     */     //   101: invokevirtual pick : (DFZ)Lnet/minecraft/util/math/RayTraceResult;
/*     */     //   104: astore #8
/*     */     //   106: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   109: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   112: invokevirtual getHeldItemMainhand : ()Lnet/minecraft/item/ItemStack;
/*     */     //   115: astore #9
/*     */     //   117: aload_0
/*     */     //   118: getfield autoExp : Lfun/rockstarity/api/modules/settings/list/CheckBox;
/*     */     //   121: invokevirtual get : ()Z
/*     */     //   124: ifeq -> 508
/*     */     //   127: aload #9
/*     */     //   129: invokevirtual getMaxDamage : ()I
/*     */     //   132: istore #10
/*     */     //   134: iload #10
/*     */     //   136: aload #9
/*     */     //   138: invokevirtual getDamage : ()I
/*     */     //   141: isub
/*     */     //   142: istore #11
/*     */     //   144: iload #11
/*     */     //   146: i2d
/*     */     //   147: iload #10
/*     */     //   149: i2d
/*     */     //   150: ddiv
/*     */     //   151: dstore #12
/*     */     //   153: dload #12
/*     */     //   155: aload_0
/*     */     //   156: invokevirtual findItem : ()I
/*     */     //   159: aload_0
/*     */     //   160: invokevirtual findExp : ()I
/*     */     //   163: <illegal opcode> makeConcatWithConstants : (DII)Ljava/lang/String;
/*     */     //   168: invokestatic debug : (Ljava/lang/Object;)V
/*     */     //   171: aload #4
/*     */     //   173: invokevirtual isEmpty : ()Z
/*     */     //   176: ifne -> 248
/*     */     //   179: aload_0
/*     */     //   180: iconst_1
/*     */     //   181: putfield cursorCheck : Z
/*     */     //   184: aload_0
/*     */     //   185: getfield timer : Lfun/rockstarity/api/helpers/math/TimerUtility;
/*     */     //   188: ldc2_w 200
/*     */     //   191: invokevirtual passed : (J)Z
/*     */     //   194: ifeq -> 247
/*     */     //   197: aload_0
/*     */     //   198: invokevirtual findEmptySlot : ()I
/*     */     //   201: istore #14
/*     */     //   203: iload #14
/*     */     //   205: iconst_m1
/*     */     //   206: if_icmpeq -> 247
/*     */     //   209: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   212: getfield playerController : Lnet/minecraft/client/multiplayer/PlayerController;
/*     */     //   215: iconst_0
/*     */     //   216: iload #14
/*     */     //   218: bipush #9
/*     */     //   220: if_icmpge -> 231
/*     */     //   223: iload #14
/*     */     //   225: bipush #36
/*     */     //   227: iadd
/*     */     //   228: goto -> 233
/*     */     //   231: iload #14
/*     */     //   233: iconst_0
/*     */     //   234: getstatic net/minecraft/inventory/container/ClickType.PICKUP : Lnet/minecraft/inventory/container/ClickType;
/*     */     //   237: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   240: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   243: invokevirtual windowClick : (IIILnet/minecraft/inventory/container/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;
/*     */     //   246: pop
/*     */     //   247: return
/*     */     //   248: aload_0
/*     */     //   249: getfield cursorCheck : Z
/*     */     //   252: ifeq -> 267
/*     */     //   255: aload_0
/*     */     //   256: iconst_0
/*     */     //   257: putfield cursorCheck : Z
/*     */     //   260: aload_0
/*     */     //   261: getfield timer : Lfun/rockstarity/api/helpers/math/TimerUtility;
/*     */     //   264: invokevirtual reset : ()V
/*     */     //   267: aload_0
/*     */     //   268: getfield autoRepair : Z
/*     */     //   271: ifeq -> 445
/*     */     //   274: aload_3
/*     */     //   275: invokevirtual getItem : ()Lnet/minecraft/item/Item;
/*     */     //   278: getstatic net/minecraft/item/Items.EXPERIENCE_BOTTLE : Lnet/minecraft/item/Item;
/*     */     //   281: if_acmpeq -> 354
/*     */     //   284: aload_0
/*     */     //   285: invokevirtual findExp : ()I
/*     */     //   288: istore #14
/*     */     //   290: iload #14
/*     */     //   292: iconst_m1
/*     */     //   293: if_icmpeq -> 314
/*     */     //   296: iload #14
/*     */     //   298: bipush #45
/*     */     //   300: if_icmpeq -> 314
/*     */     //   303: iload #14
/*     */     //   305: bipush #45
/*     */     //   307: iconst_1
/*     */     //   308: invokestatic moveItem : (IIZ)V
/*     */     //   311: goto -> 351
/*     */     //   314: iload #14
/*     */     //   316: iconst_m1
/*     */     //   317: if_icmpne -> 351
/*     */     //   320: aload_0
/*     */     //   321: iconst_0
/*     */     //   322: putfield autoRepair : Z
/*     */     //   325: aload_0
/*     */     //   326: invokevirtual findItem : ()I
/*     */     //   329: iconst_m1
/*     */     //   330: if_icmpeq -> 351
/*     */     //   333: aload #4
/*     */     //   335: invokevirtual isEmpty : ()Z
/*     */     //   338: ifeq -> 351
/*     */     //   341: aload_0
/*     */     //   342: invokevirtual findItem : ()I
/*     */     //   345: bipush #45
/*     */     //   347: iconst_1
/*     */     //   348: invokestatic moveItem : (IIZ)V
/*     */     //   351: goto -> 444
/*     */     //   354: aload_0
/*     */     //   355: getfield timer : Lfun/rockstarity/api/helpers/math/TimerUtility;
/*     */     //   358: ldc2_w 300
/*     */     //   361: invokevirtual passed : (J)Z
/*     */     //   364: ifeq -> 444
/*     */     //   367: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   370: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   373: getfield connection : Lnet/minecraft/client/network/play/ClientPlayNetHandler;
/*     */     //   376: new net/minecraft/network/play/client/CPlayerTryUseItemPacket
/*     */     //   379: dup
/*     */     //   380: getstatic net/minecraft/util/Hand.OFF_HAND : Lnet/minecraft/util/Hand;
/*     */     //   383: invokespecial <init> : (Lnet/minecraft/util/Hand;)V
/*     */     //   386: invokevirtual sendPacket : (Lnet/minecraft/network/IPacket;)V
/*     */     //   389: aload_0
/*     */     //   390: getfield timer : Lfun/rockstarity/api/helpers/math/TimerUtility;
/*     */     //   393: invokevirtual reset : ()V
/*     */     //   396: dload #12
/*     */     //   398: ldc2_w 0.95
/*     */     //   401: dcmpl
/*     */     //   402: ifge -> 413
/*     */     //   405: aload_0
/*     */     //   406: invokevirtual findExp : ()I
/*     */     //   409: iconst_m1
/*     */     //   410: if_icmpne -> 444
/*     */     //   413: aload_0
/*     */     //   414: iconst_0
/*     */     //   415: putfield autoRepair : Z
/*     */     //   418: aload #4
/*     */     //   420: invokevirtual isEmpty : ()Z
/*     */     //   423: ifeq -> 444
/*     */     //   426: aload_0
/*     */     //   427: invokevirtual findItem : ()I
/*     */     //   430: iconst_m1
/*     */     //   431: if_icmpeq -> 444
/*     */     //   434: aload_0
/*     */     //   435: invokevirtual findItem : ()I
/*     */     //   438: bipush #45
/*     */     //   440: iconst_1
/*     */     //   441: invokestatic moveItem : (IIZ)V
/*     */     //   444: return
/*     */     //   445: aload #5
/*     */     //   447: aload #9
/*     */     //   449: invokevirtual getItem : ()Lnet/minecraft/item/Item;
/*     */     //   452: invokeinterface contains : (Ljava/lang/Object;)Z
/*     */     //   457: ifeq -> 508
/*     */     //   460: dload #12
/*     */     //   462: aload_0
/*     */     //   463: getfield autoExpPerc : Lfun/rockstarity/api/modules/settings/list/Slider;
/*     */     //   466: invokevirtual get : ()F
/*     */     //   469: ldc 100.0
/*     */     //   471: fdiv
/*     */     //   472: f2d
/*     */     //   473: dcmpg
/*     */     //   474: ifge -> 508
/*     */     //   477: aload_0
/*     */     //   478: getfield autoRepair : Z
/*     */     //   481: ifne -> 508
/*     */     //   484: aload_0
/*     */     //   485: invokevirtual findExp : ()I
/*     */     //   488: iconst_m1
/*     */     //   489: if_icmpne -> 497
/*     */     //   492: aload_0
/*     */     //   493: invokevirtual toggle : ()V
/*     */     //   496: return
/*     */     //   497: aload_0
/*     */     //   498: iconst_1
/*     */     //   499: putfield autoRepair : Z
/*     */     //   502: aload_0
/*     */     //   503: iconst_0
/*     */     //   504: putfield attempts : I
/*     */     //   507: return
/*     */     //   508: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   511: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   514: getfield inventory : Lnet/minecraft/entity/player/PlayerInventory;
/*     */     //   517: invokevirtual getFirstEmptyStack : ()I
/*     */     //   520: iconst_m1
/*     */     //   521: if_icmpne -> 712
/*     */     //   524: aload_0
/*     */     //   525: getfield autoSell : Lfun/rockstarity/api/modules/settings/list/CheckBox;
/*     */     //   528: invokevirtual get : ()Z
/*     */     //   531: ifeq -> 712
/*     */     //   534: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   537: getfield currentScreen : Lnet/minecraft/client/gui/screen/Screen;
/*     */     //   540: astore #12
/*     */     //   542: aload #12
/*     */     //   544: instanceof net/minecraft/client/gui/screen/inventory/ContainerScreen
/*     */     //   547: ifeq -> 677
/*     */     //   550: aload #12
/*     */     //   552: checkcast net/minecraft/client/gui/screen/inventory/ContainerScreen
/*     */     //   555: astore #11
/*     */     //   557: invokestatic isFT : ()Z
/*     */     //   560: ifeq -> 585
/*     */     //   563: aload #11
/*     */     //   565: invokevirtual getTitle : ()Lnet/minecraft/util/text/ITextComponent;
/*     */     //   568: invokeinterface getString : ()Ljava/lang/String;
/*     */     //   573: ldc_w '● Выберите секцию'
/*     */     //   576: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   579: ifeq -> 614
/*     */     //   582: goto -> 604
/*     */     //   585: aload #11
/*     */     //   587: invokevirtual getTitle : ()Lnet/minecraft/util/text/ITextComponent;
/*     */     //   590: invokeinterface getString : ()Ljava/lang/String;
/*     */     //   595: ldc_w '● Выбери секцию'
/*     */     //   598: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   601: ifeq -> 614
/*     */     //   604: bipush #21
/*     */     //   606: iconst_0
/*     */     //   607: getstatic net/minecraft/inventory/container/ClickType.PICKUP : Lnet/minecraft/inventory/container/ClickType;
/*     */     //   610: iconst_1
/*     */     //   611: invokestatic clickSlotId : (IILnet/minecraft/inventory/container/ClickType;Z)V
/*     */     //   614: aload #11
/*     */     //   616: invokevirtual getTitle : ()Lnet/minecraft/util/text/ITextComponent;
/*     */     //   619: invokeinterface getString : ()Ljava/lang/String;
/*     */     //   624: ldc_w 'Скупщик еды'
/*     */     //   627: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   630: ifeq -> 1123
/*     */     //   633: aload_0
/*     */     //   634: invokevirtual ssItem : ()I
/*     */     //   637: iconst_0
/*     */     //   638: getstatic net/minecraft/inventory/container/ClickType.PICKUP : Lnet/minecraft/inventory/container/ClickType;
/*     */     //   641: iconst_1
/*     */     //   642: invokestatic clickSlotId : (IILnet/minecraft/inventory/container/ClickType;Z)V
/*     */     //   645: aload_0
/*     */     //   646: getfield timer : Lfun/rockstarity/api/helpers/math/TimerUtility;
/*     */     //   649: ldc2_w 500
/*     */     //   652: invokevirtual passed : (J)Z
/*     */     //   655: ifeq -> 1123
/*     */     //   658: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   661: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   664: invokevirtual closeScreen : ()V
/*     */     //   667: aload_0
/*     */     //   668: getfield timer : Lfun/rockstarity/api/helpers/math/TimerUtility;
/*     */     //   671: invokevirtual reset : ()V
/*     */     //   674: goto -> 1123
/*     */     //   677: aload_0
/*     */     //   678: getfield timer : Lfun/rockstarity/api/helpers/math/TimerUtility;
/*     */     //   681: ldc2_w 300
/*     */     //   684: invokevirtual passed : (J)Z
/*     */     //   687: ifeq -> 1123
/*     */     //   690: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   693: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   696: ldc_w '/buyer'
/*     */     //   699: invokevirtual sendChatMessage : (Ljava/lang/String;)V
/*     */     //   702: aload_0
/*     */     //   703: getfield timer : Lfun/rockstarity/api/helpers/math/TimerUtility;
/*     */     //   706: invokevirtual reset : ()V
/*     */     //   709: goto -> 1123
/*     */     //   712: aload #8
/*     */     //   714: invokevirtual getType : ()Lnet/minecraft/util/math/RayTraceResult$Type;
/*     */     //   717: getstatic net/minecraft/util/math/RayTraceResult$Type.BLOCK : Lnet/minecraft/util/math/RayTraceResult$Type;
/*     */     //   720: if_acmpne -> 1123
/*     */     //   723: aload #8
/*     */     //   725: instanceof net/minecraft/util/math/BlockRayTraceResult
/*     */     //   728: ifeq -> 1123
/*     */     //   731: aload #8
/*     */     //   733: checkcast net/minecraft/util/math/BlockRayTraceResult
/*     */     //   736: astore #10
/*     */     //   738: aload #10
/*     */     //   740: invokevirtual getPos : ()Lnet/minecraft/util/math/BlockPos;
/*     */     //   743: astore #11
/*     */     //   745: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   748: getfield world : Lnet/minecraft/client/world/ClientWorld;
/*     */     //   751: aload #11
/*     */     //   753: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;
/*     */     //   756: astore #12
/*     */     //   758: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   761: getfield world : Lnet/minecraft/client/world/ClientWorld;
/*     */     //   764: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   767: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   770: invokevirtual getPosition : ()Lnet/minecraft/util/math/BlockPos;
/*     */     //   773: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;
/*     */     //   776: astore #13
/*     */     //   778: aload #12
/*     */     //   780: invokevirtual getBlock : ()Lnet/minecraft/block/Block;
/*     */     //   783: astore #15
/*     */     //   785: aload #15
/*     */     //   787: instanceof net/minecraft/block/CropsBlock
/*     */     //   790: ifeq -> 961
/*     */     //   793: aload #15
/*     */     //   795: checkcast net/minecraft/block/CropsBlock
/*     */     //   798: astore #14
/*     */     //   800: aload #13
/*     */     //   802: invokevirtual getBlock : ()Lnet/minecraft/block/Block;
/*     */     //   805: getstatic net/minecraft/block/Blocks.FARMLAND : Lnet/minecraft/block/Block;
/*     */     //   808: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   811: ifeq -> 961
/*     */     //   814: aload_0
/*     */     //   815: iconst_1
/*     */     //   816: putfield autoCos : Z
/*     */     //   819: aload #5
/*     */     //   821: aload #9
/*     */     //   823: invokevirtual getItem : ()Lnet/minecraft/item/Item;
/*     */     //   826: invokeinterface contains : (Ljava/lang/Object;)Z
/*     */     //   831: ifeq -> 898
/*     */     //   834: aload_0
/*     */     //   835: getfield timer : Lfun/rockstarity/api/helpers/math/TimerUtility;
/*     */     //   838: aload_0
/*     */     //   839: getfield delay : Lfun/rockstarity/api/modules/settings/list/Slider;
/*     */     //   842: invokevirtual get : ()F
/*     */     //   845: invokevirtual passed : (F)Z
/*     */     //   848: ifeq -> 898
/*     */     //   851: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   854: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   857: getstatic net/minecraft/util/Hand.MAIN_HAND : Lnet/minecraft/util/Hand;
/*     */     //   860: iconst_1
/*     */     //   861: invokevirtual swing : (Lnet/minecraft/util/Hand;Z)V
/*     */     //   864: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   867: getfield playerController : Lnet/minecraft/client/multiplayer/PlayerController;
/*     */     //   870: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   873: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   876: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   879: getfield world : Lnet/minecraft/client/world/ClientWorld;
/*     */     //   882: getstatic net/minecraft/util/Hand.MAIN_HAND : Lnet/minecraft/util/Hand;
/*     */     //   885: aload #10
/*     */     //   887: invokevirtual func_217292_a : (Lnet/minecraft/client/entity/player/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/BlockRayTraceResult;)Lnet/minecraft/util/ActionResultType;
/*     */     //   890: pop
/*     */     //   891: aload_0
/*     */     //   892: getfield timer : Lfun/rockstarity/api/helpers/math/TimerUtility;
/*     */     //   895: invokevirtual reset : ()V
/*     */     //   898: aload #12
/*     */     //   900: getstatic net/minecraft/block/CropsBlock.AGE : Lnet/minecraft/state/IntegerProperty;
/*     */     //   903: invokevirtual get : (Lnet/minecraft/state/Property;)Ljava/lang/Comparable;
/*     */     //   906: checkcast java/lang/Integer
/*     */     //   909: invokevirtual intValue : ()I
/*     */     //   912: aload #14
/*     */     //   914: invokevirtual getMaxAge : ()I
/*     */     //   917: if_icmpne -> 1123
/*     */     //   920: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   923: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   926: getstatic net/minecraft/util/Hand.MAIN_HAND : Lnet/minecraft/util/Hand;
/*     */     //   929: iconst_0
/*     */     //   930: invokevirtual swing : (Lnet/minecraft/util/Hand;Z)V
/*     */     //   933: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   936: getfield playerController : Lnet/minecraft/client/multiplayer/PlayerController;
/*     */     //   939: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   942: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   945: invokevirtual getPosition : ()Lnet/minecraft/util/math/BlockPos;
/*     */     //   948: invokevirtual up : ()Lnet/minecraft/util/math/BlockPos;
/*     */     //   951: getstatic net/minecraft/util/Direction.UP : Lnet/minecraft/util/Direction;
/*     */     //   954: invokevirtual onPlayerDamageBlock : (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)Z
/*     */     //   957: pop
/*     */     //   958: goto -> 1123
/*     */     //   961: aload #12
/*     */     //   963: invokevirtual getBlock : ()Lnet/minecraft/block/Block;
/*     */     //   966: getstatic net/minecraft/block/Blocks.FARMLAND : Lnet/minecraft/block/Block;
/*     */     //   969: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   972: ifeq -> 1123
/*     */     //   975: aload #11
/*     */     //   977: invokevirtual up : ()Lnet/minecraft/util/math/BlockPos;
/*     */     //   980: astore #15
/*     */     //   982: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   985: getfield world : Lnet/minecraft/client/world/ClientWorld;
/*     */     //   988: aload #15
/*     */     //   990: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;
/*     */     //   993: astore #16
/*     */     //   995: aload #16
/*     */     //   997: invokevirtual isAir : ()Z
/*     */     //   1000: ifeq -> 1123
/*     */     //   1003: aload_3
/*     */     //   1004: invokevirtual isEmpty : ()Z
/*     */     //   1007: ifne -> 1024
/*     */     //   1010: aload #6
/*     */     //   1012: aload_3
/*     */     //   1013: invokevirtual getItem : ()Lnet/minecraft/item/Item;
/*     */     //   1016: invokeinterface contains : (Ljava/lang/Object;)Z
/*     */     //   1021: ifne -> 1045
/*     */     //   1024: aload_0
/*     */     //   1025: invokevirtual findItem : ()I
/*     */     //   1028: iconst_m1
/*     */     //   1029: if_icmpeq -> 1045
/*     */     //   1032: aload_0
/*     */     //   1033: invokevirtual findItem : ()I
/*     */     //   1036: bipush #45
/*     */     //   1038: iconst_1
/*     */     //   1039: invokestatic moveItem : (IIZ)V
/*     */     //   1042: goto -> 1123
/*     */     //   1045: aload #6
/*     */     //   1047: aload_3
/*     */     //   1048: invokevirtual getItem : ()Lnet/minecraft/item/Item;
/*     */     //   1051: invokeinterface contains : (Ljava/lang/Object;)Z
/*     */     //   1056: ifeq -> 1123
/*     */     //   1059: aload_0
/*     */     //   1060: getfield timer : Lfun/rockstarity/api/helpers/math/TimerUtility;
/*     */     //   1063: aload_0
/*     */     //   1064: getfield delay : Lfun/rockstarity/api/modules/settings/list/Slider;
/*     */     //   1067: invokevirtual get : ()F
/*     */     //   1070: invokevirtual passed : (F)Z
/*     */     //   1073: ifeq -> 1123
/*     */     //   1076: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   1079: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   1082: getstatic net/minecraft/util/Hand.OFF_HAND : Lnet/minecraft/util/Hand;
/*     */     //   1085: iconst_1
/*     */     //   1086: invokevirtual swing : (Lnet/minecraft/util/Hand;Z)V
/*     */     //   1089: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   1092: getfield playerController : Lnet/minecraft/client/multiplayer/PlayerController;
/*     */     //   1095: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   1098: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   1101: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   1104: getfield world : Lnet/minecraft/client/world/ClientWorld;
/*     */     //   1107: getstatic net/minecraft/util/Hand.OFF_HAND : Lnet/minecraft/util/Hand;
/*     */     //   1110: aload #10
/*     */     //   1112: invokevirtual func_217292_a : (Lnet/minecraft/client/entity/player/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/BlockRayTraceResult;)Lnet/minecraft/util/ActionResultType;
/*     */     //   1115: pop
/*     */     //   1116: aload_0
/*     */     //   1117: getfield timer : Lfun/rockstarity/api/helpers/math/TimerUtility;
/*     */     //   1120: invokevirtual reset : ()V
/*     */     //   1123: iload #7
/*     */     //   1125: ifne -> 1142
/*     */     //   1128: aload #5
/*     */     //   1130: aload_2
/*     */     //   1131: invokevirtual getItem : ()Lnet/minecraft/item/Item;
/*     */     //   1134: invokeinterface contains : (Ljava/lang/Object;)Z
/*     */     //   1139: ifne -> 1173
/*     */     //   1142: aload_0
/*     */     //   1143: invokevirtual findHoe : ()I
/*     */     //   1146: iconst_m1
/*     */     //   1147: if_icmpeq -> 1173
/*     */     //   1150: aload_0
/*     */     //   1151: invokevirtual findHoe : ()I
/*     */     //   1154: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   1157: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   1160: getfield inventory : Lnet/minecraft/entity/player/PlayerInventory;
/*     */     //   1163: getfield currentItem : I
/*     */     //   1166: bipush #36
/*     */     //   1168: iadd
/*     */     //   1169: iconst_1
/*     */     //   1170: invokestatic moveItem : (IIZ)V
/*     */     //   1173: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   1176: getfield world : Lnet/minecraft/client/world/ClientWorld;
/*     */     //   1179: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   1182: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   1185: invokevirtual getPosition : ()Lnet/minecraft/util/math/BlockPos;
/*     */     //   1188: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;
/*     */     //   1191: astore_2
/*     */     //   1192: aload_1
/*     */     //   1193: instanceof fun/rockstarity/api/events/list/player/EventMotion
/*     */     //   1196: ifeq -> 1236
/*     */     //   1199: aload_1
/*     */     //   1200: checkcast fun/rockstarity/api/events/list/player/EventMotion
/*     */     //   1203: astore_3
/*     */     //   1204: aload_2
/*     */     //   1205: invokevirtual getBlock : ()Lnet/minecraft/block/Block;
/*     */     //   1208: getstatic net/minecraft/block/Blocks.FARMLAND : Lnet/minecraft/block/Block;
/*     */     //   1211: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1214: ifeq -> 1236
/*     */     //   1217: aload_3
/*     */     //   1218: ldc_w 90.0
/*     */     //   1221: invokevirtual setPitch : (F)V
/*     */     //   1224: getstatic fun/rockstarity/client/modules/other/AutoFarmNew.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   1227: getfield player : Lnet/minecraft/client/entity/player/ClientPlayerEntity;
/*     */     //   1230: ldc_w 90.0
/*     */     //   1233: putfield rotationPitchHead : F
/*     */     //   1236: aload_1
/*     */     //   1237: instanceof fun/rockstarity/api/events/list/player/EventTrace
/*     */     //   1240: ifeq -> 1272
/*     */     //   1243: aload_1
/*     */     //   1244: checkcast fun/rockstarity/api/events/list/player/EventTrace
/*     */     //   1247: astore_3
/*     */     //   1248: aload_2
/*     */     //   1249: invokevirtual getBlock : ()Lnet/minecraft/block/Block;
/*     */     //   1252: getstatic net/minecraft/block/Blocks.FARMLAND : Lnet/minecraft/block/Block;
/*     */     //   1255: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1258: ifeq -> 1272
/*     */     //   1261: aload_3
/*     */     //   1262: ldc_w 90.0
/*     */     //   1265: invokevirtual setPitch : (F)V
/*     */     //   1268: aload_3
/*     */     //   1269: invokevirtual cancel : ()V
/*     */     //   1272: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #62	-> 0
/*     */     //   #63	-> 7
/*     */     //   #64	-> 32
/*     */     //   #65	-> 42
/*     */     //   #67	-> 56
/*     */     //   #68	-> 67
/*     */     //   #69	-> 84
/*     */     //   #70	-> 90
/*     */     //   #71	-> 106
/*     */     //   #73	-> 117
/*     */     //   #74	-> 127
/*     */     //   #75	-> 144
/*     */     //   #77	-> 153
/*     */     //   #79	-> 171
/*     */     //   #80	-> 179
/*     */     //   #81	-> 184
/*     */     //   #82	-> 197
/*     */     //   #83	-> 203
/*     */     //   #84	-> 209
/*     */     //   #87	-> 247
/*     */     //   #88	-> 248
/*     */     //   #89	-> 255
/*     */     //   #90	-> 260
/*     */     //   #93	-> 267
/*     */     //   #94	-> 274
/*     */     //   #95	-> 284
/*     */     //   #96	-> 290
/*     */     //   #97	-> 303
/*     */     //   #98	-> 314
/*     */     //   #99	-> 320
/*     */     //   #100	-> 325
/*     */     //   #101	-> 333
/*     */     //   #102	-> 341
/*     */     //   #106	-> 351
/*     */     //   #107	-> 367
/*     */     //   #108	-> 389
/*     */     //   #110	-> 396
/*     */     //   #111	-> 413
/*     */     //   #112	-> 418
/*     */     //   #113	-> 434
/*     */     //   #117	-> 444
/*     */     //   #120	-> 445
/*     */     //   #121	-> 484
/*     */     //   #122	-> 492
/*     */     //   #123	-> 496
/*     */     //   #125	-> 497
/*     */     //   #126	-> 502
/*     */     //   #127	-> 507
/*     */     //   #131	-> 508
/*     */     //   #132	-> 534
/*     */     //   #133	-> 557
/*     */     //   #134	-> 604
/*     */     //   #136	-> 614
/*     */     //   #137	-> 633
/*     */     //   #138	-> 645
/*     */     //   #139	-> 658
/*     */     //   #140	-> 667
/*     */     //   #143	-> 677
/*     */     //   #144	-> 690
/*     */     //   #145	-> 702
/*     */     //   #147	-> 712
/*     */     //   #148	-> 738
/*     */     //   #149	-> 745
/*     */     //   #150	-> 758
/*     */     //   #152	-> 778
/*     */     //   #153	-> 814
/*     */     //   #154	-> 819
/*     */     //   #155	-> 851
/*     */     //   #156	-> 864
/*     */     //   #157	-> 891
/*     */     //   #160	-> 898
/*     */     //   #161	-> 920
/*     */     //   #162	-> 933
/*     */     //   #164	-> 961
/*     */     //   #165	-> 975
/*     */     //   #166	-> 982
/*     */     //   #168	-> 995
/*     */     //   #169	-> 1003
/*     */     //   #170	-> 1032
/*     */     //   #171	-> 1045
/*     */     //   #172	-> 1076
/*     */     //   #173	-> 1089
/*     */     //   #174	-> 1116
/*     */     //   #180	-> 1123
/*     */     //   #181	-> 1142
/*     */     //   #185	-> 1173
/*     */     //   #187	-> 1192
/*     */     //   #188	-> 1217
/*     */     //   #189	-> 1224
/*     */     //   #191	-> 1236
/*     */     //   #192	-> 1261
/*     */     //   #193	-> 1268
/*     */     //   #195	-> 1272
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   203	44	14	emptySlot	I
/*     */     //   290	61	14	expSlot	I
/*     */     //   134	374	10	max	I
/*     */     //   144	364	11	cur	I
/*     */     //   153	355	12	perc	D
/*     */     //   557	120	11	screen	Lnet/minecraft/client/gui/screen/inventory/ContainerScreen;
/*     */     //   800	161	14	crop	Lnet/minecraft/block/CropsBlock;
/*     */     //   982	141	15	cropPos	Lnet/minecraft/util/math/BlockPos;
/*     */     //   995	128	16	cropState	Lnet/minecraft/block/BlockState;
/*     */     //   745	378	11	pos	Lnet/minecraft/util/math/BlockPos;
/*     */     //   758	365	12	state	Lnet/minecraft/block/BlockState;
/*     */     //   778	345	13	stateX	Lnet/minecraft/block/BlockState;
/*     */     //   738	385	10	blockResult	Lnet/minecraft/util/math/BlockRayTraceResult;
/*     */     //   32	1141	2	mainStack	Lnet/minecraft/item/ItemStack;
/*     */     //   42	1131	3	offhandStack	Lnet/minecraft/item/ItemStack;
/*     */     //   56	1117	4	cursorStack	Lnet/minecraft/item/ItemStack;
/*     */     //   67	1106	5	hoes	Ljava/util/List;
/*     */     //   84	1089	6	items	Ljava/util/List;
/*     */     //   90	1083	7	empty	Z
/*     */     //   106	1067	8	result	Lnet/minecraft/util/math/RayTraceResult;
/*     */     //   117	1056	9	stack	Lnet/minecraft/item/ItemStack;
/*     */     //   1204	32	3	e	Lfun/rockstarity/api/events/list/player/EventMotion;
/*     */     //   1248	24	3	eventTrace	Lfun/rockstarity/api/events/list/player/EventTrace;
/*     */     //   0	1273	0	this	Lfun/rockstarity/client/modules/other/AutoFarmNew;
/*     */     //   0	1273	1	event	Lfun/rockstarity/api/events/Event;
/*     */     //   1192	81	2	stateXX	Lnet/minecraft/block/BlockState;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   557	120	11	screen	Lnet/minecraft/client/gui/screen/inventory/ContainerScreen<*>;
/*     */     //   67	1106	5	hoes	Ljava/util/List<Lnet/minecraft/item/Item;>;
/*     */     //   84	1089	6	items	Ljava/util/List<Lnet/minecraft/item/Item;>;
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
/*     */   private int findHoe() {
/* 198 */     for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
/* 199 */       ItemStack stack = (ItemStack)mc.player.inventory.mainInventory.get(i);
/* 200 */       if (stack.getItem() == Items.DIAMOND_HOE) {
/* 201 */         return (i < 9) ? (36 + i) : i;
/*     */       }
/*     */     } 
/* 204 */     return -1;
/*     */   }
/*     */   
/*     */   private int findExp() {
/* 208 */     if (mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) {
/* 209 */       return 45;
/*     */     }
/*     */     
/* 212 */     for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
/* 213 */       ItemStack stack = (ItemStack)mc.player.inventory.mainInventory.get(i);
/* 214 */       if (stack.getItem() == Items.EXPERIENCE_BOTTLE) {
/* 215 */         return (i < 9) ? (36 + i) : i;
/*     */       }
/*     */     } 
/* 218 */     return -1;
/*     */   }
/*     */   
/*     */   public int findItem() {
/* 222 */     for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
/* 223 */       ItemStack stack = (ItemStack)mc.player.inventory.mainInventory.get(i);
/* 224 */       if ((this.carrot.get() && stack.getItem() == Items.CARROT) || (this.potato
/* 225 */         .get() && stack.getItem() == Items.POTATO) || (this.wheat
/* 226 */         .get() && stack.getItem() == Items.WHEAT_SEEDS) || (this.beetroot
/* 227 */         .get() && stack.getItem() == Items.BEETROOT_SEEDS)) {
/* 228 */         return (i < 9) ? (36 + i) : i;
/*     */       }
/*     */     } 
/* 231 */     return -1;
/*     */   }
/*     */   
/*     */   public int ssItem() {
/* 235 */     if (this.carrot.get() && mc.player.getHeldItemOffhand().getItem() == Items.CARROT) return 10; 
/* 236 */     if (this.potato.get() && mc.player.getHeldItemOffhand().getItem() == Items.POTATO) return 11; 
/* 237 */     if (this.wheat.get() && mc.player.getHeldItemOffhand().getItem() == Items.WHEAT_SEEDS) return 14; 
/* 238 */     if (this.beetroot.get() && mc.player.getHeldItemOffhand().getItem() == Items.BEETROOT_SEEDS) return 12; 
/* 239 */     return -1;
/*     */   }
/*     */   
/*     */   private int findEmptySlot() {
/* 243 */     for (int i = 0; i < 36; i++) {
/* 244 */       if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
/* 245 */         return i;
/*     */       }
/*     */     } 
/* 248 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/* 253 */     this.autoRepair = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 258 */     this.autoRepair = false;
/*     */   }
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\AutoFarmNew.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */