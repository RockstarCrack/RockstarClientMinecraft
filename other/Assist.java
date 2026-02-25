/*      */ package fun.rockstarity.client.modules.other;
/*      */ 
/*      */ import com.mojang.blaze3d.matrix.MatrixStack;
/*      */ import com.mojang.blaze3d.platform.GlStateManager;
/*      */ import fun.rockstarity.api.binds.Bindable;
/*      */ import fun.rockstarity.api.events.Event;
/*      */ import fun.rockstarity.api.events.list.game.EventChatScreen;
/*      */ import fun.rockstarity.api.events.list.game.EventPotionHit;
/*      */ import fun.rockstarity.api.events.list.game.EventSetCooldown;
/*      */ import fun.rockstarity.api.events.list.game.EventTotemBreak;
/*      */ import fun.rockstarity.api.events.list.game.inputs.EventInput;
/*      */ import fun.rockstarity.api.events.list.game.inputs.EventKey;
/*      */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*      */ import fun.rockstarity.api.events.list.game.packet.EventSendPacket;
/*      */ import fun.rockstarity.api.events.list.player.EventAttack;
/*      */ import fun.rockstarity.api.events.list.player.EventFinishEat;
/*      */ import fun.rockstarity.api.events.list.player.EventMotionMove;
/*      */ import fun.rockstarity.api.events.list.player.EventUpdate;
/*      */ import fun.rockstarity.api.events.list.render.EventRender3D;
/*      */ import fun.rockstarity.api.helpers.game.Chat;
/*      */ import fun.rockstarity.api.helpers.game.ItemUtility;
/*      */ import fun.rockstarity.api.helpers.game.Server;
/*      */ import fun.rockstarity.api.helpers.math.RussianNumberParser;
/*      */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*      */ import fun.rockstarity.api.helpers.math.net.objecthunter.exp4j.Expression;
/*      */ import fun.rockstarity.api.helpers.math.net.objecthunter.exp4j.ExpressionBuilder;
/*      */ import fun.rockstarity.api.helpers.player.InvUtility;
/*      */ import fun.rockstarity.api.helpers.player.Inventory;
/*      */ import fun.rockstarity.api.helpers.player.Move;
/*      */ import fun.rockstarity.api.helpers.player.Player;
/*      */ import fun.rockstarity.api.helpers.render.Render;
/*      */ import fun.rockstarity.api.modules.Category;
/*      */ import fun.rockstarity.api.modules.Info;
/*      */ import fun.rockstarity.api.modules.Module;
/*      */ import fun.rockstarity.api.modules.settings.list.Binding;
/*      */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*      */ import fun.rockstarity.api.modules.settings.list.Input;
/*      */ import fun.rockstarity.api.modules.settings.list.Mode;
/*      */ import fun.rockstarity.api.modules.settings.list.Select;
/*      */ import fun.rockstarity.api.modules.settings.list.Slider;
/*      */ import fun.rockstarity.api.render.animation.Animation;
/*      */ import fun.rockstarity.api.render.animation.Easing;
/*      */ import fun.rockstarity.api.render.color.themes.Style;
/*      */ import fun.rockstarity.client.modules.combat.Aura;
/*      */ import java.util.List;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
/*      */ import net.minecraft.client.entity.player.ClientPlayerEntity;
/*      */ import net.minecraft.client.resources.I18n;
/*      */ import net.minecraft.client.util.ITooltipFlag;
/*      */ import net.minecraft.entity.Entity;
/*      */ import net.minecraft.entity.LivingEntity;
/*      */ import net.minecraft.entity.player.PlayerEntity;
/*      */ import net.minecraft.entity.player.PlayerInventory;
/*      */ import net.minecraft.inventory.container.ClickType;
/*      */ import net.minecraft.item.Item;
/*      */ import net.minecraft.item.ItemStack;
/*      */ import net.minecraft.item.Items;
/*      */ import net.minecraft.network.IPacket;
/*      */ import net.minecraft.network.play.client.CChatMessagePacket;
/*      */ import net.minecraft.network.play.client.CClickWindowPacket;
/*      */ import net.minecraft.network.play.client.CCloseWindowPacket;
/*      */ import net.minecraft.network.play.client.CHeldItemChangePacket;
/*      */ import net.minecraft.network.play.client.CPlayerDiggingPacket;
/*      */ import net.minecraft.network.play.client.CPlayerPacket;
/*      */ import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
/*      */ import net.minecraft.network.play.client.CResourcePackStatusPacket;
/*      */ import net.minecraft.network.play.server.SChatPacket;
/*      */ import net.minecraft.network.play.server.SEntityTeleportPacket;
/*      */ import net.minecraft.network.play.server.SOpenWindowPacket;
/*      */ import net.minecraft.network.play.server.SPlaySoundEffectPacket;
/*      */ import net.minecraft.network.play.server.SSetSlotPacket;
/*      */ import net.minecraft.potion.Effect;
/*      */ import net.minecraft.potion.EffectInstance;
/*      */ import net.minecraft.potion.EffectType;
/*      */ import net.minecraft.potion.EffectUtils;
/*      */ import net.minecraft.util.Direction;
/*      */ import net.minecraft.util.Hand;
/*      */ import net.minecraft.util.math.AxisAlignedBB;
/*      */ import net.minecraft.util.math.BlockPos;
/*      */ import net.minecraft.util.text.ITextComponent;
/*      */ import net.minecraft.util.text.TextFormatting;
/*      */ import net.minecraft.util.text.TranslationTextComponent;
/*      */ import org.lwjgl.glfw.GLFW;
/*      */ import org.lwjgl.opengl.GL11;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @Info(name = "Assist", desc = "Помощник на серверах", type = Category.OTHER, module = {"SRPSpoof", "ResourcePack", "FTHelper", "FunTime", "RWHelper", "HWHelper", "HolyWorld", "Bind", "AutoChorus", "AutoRCT"})
/*      */ public class Assist
/*      */   extends Module
/*      */ {
/*  113 */   private final Select util = (new Select((Bindable)this, "ReallyWorld")).desc("Вспомогательные функции для ReallyWorld"); public Select getUtil() { return this.util; }
/*      */   
/*  115 */   private final Select.Element dragonFly = new Select.Element(this.util, "Ускорять /fly"); public Select.Element getDragonFly() { return this.dragonFly; }
/*  116 */    private final Select.Element closeMenu = new Select.Element(this.util, "Закрывать меню"); public Select.Element getCloseMenu() { return this.closeMenu; }
/*  117 */    private final Select.Element autootkup = new Select.Element(this.util, "Авто откуп"); public Select.Element getAutootkup() { return this.autootkup; }
/*  118 */    private final Input msgotkup = (new Input((Bindable)this.autootkup, "Сообщение")).set("Выкинь шар").desc("Введите сообщение, которое будет использоваться для отправки в чат"); public Input getMsgotkup() { return this.msgotkup; }
/*  119 */    private Mode otkupMode = new Mode((Bindable)this.autootkup, "Режим"); public Mode getOtkupMode() { return this.otkupMode; }
/*  120 */    private Mode.Element def = new Mode.Element(this.otkupMode, "По здоровью"); public Mode.Element getDef() { return this.def; }
/*  121 */    private Mode.Element totem = new Mode.Element(this.otkupMode, "После сноса тотема"); public Mode.Element getTotem() { return this.totem; }
/*  122 */    private final Slider hpotkup = (new Slider((Bindable)this.autootkup, "Здоровье")).min(1.0F).max(19.0F).inc(0.5F).set(5.0F).hide(() -> Boolean.valueOf(!this.otkupMode.is(this.def))); public Slider getHpotkup() { return this.hpotkup; }
/*      */   
/*  124 */   private final Select.Element autoFix = new Select.Element(this.util, "Авто фикс"); public Select.Element getAutoFix() { return this.autoFix; }
/*  125 */    private final Select.Element rpSpoof = new Select.Element(this.util, "Спуф рп"); public Select.Element getRpSpoof() { return this.rpSpoof; }
/*  126 */    private final Slider speedXZ = (new Slider((Bindable)this.dragonFly, "Скорость по XZ")).min(1.0F).max(10.0F).inc(0.5F).set(2.0F).hide(() -> Boolean.valueOf(!this.dragonFly.get())); public Slider getSpeedXZ() { return this.speedXZ; }
/*  127 */    private final Slider speedY = (new Slider((Bindable)this.dragonFly, "Скорость по Y")).min(1.0F).max(10.0F).inc(1.0F).set(2.0F).hide(() -> Boolean.valueOf(!this.dragonFly.get())); public Slider getSpeedY() { return this.speedY; }
/*      */   
/*  129 */   private final Select utils = (new Select((Bindable)this, "FunTime")).desc("Вспомогательные функции для FunTime. Калькулятор - Позволяет выставлять предметы командой по типу \"/ah sell 64*100\""); public Select getUtils() { return this.utils; }
/*  130 */    private final Select.Element bind = new Select.Element(this.utils, "Исп. по бинду"); public Select.Element getBind() { return this.bind; }
/*  131 */    private final Select.Element autopiona = new Select.Element(this.utils, "Авто /piona"); public Select.Element getAutopiona() { return this.autopiona; }
/*  132 */    private final Select.Element visual = new Select.Element(this.utils, "Отображение радиусов"); public Select.Element getVisual() { return this.visual; }
/*  133 */    private final CheckBox visualRadius = new CheckBox((Bindable)this.visual, "Подсвечивать если в радиусе"); public CheckBox getVisualRadius() { return this.visualRadius; }
/*  134 */    private final Select.Element players = (new Select.Element(this.utils, "Радиусы у других")).hide(() -> Boolean.valueOf(!this.visual.get())); public Select.Element getPlayers() { return this.players; }
/*  135 */    private final Select.Element time = (new Select.Element(this.utils, "Конвертировать время")).set(true); public Select.Element getTime() { return this.time; }
/*  136 */    private final Select.Element autorct = new Select.Element(this.utils, "Перезаход при чарке"); public Select.Element getAutorct() { return this.autorct; }
/*  137 */    private final Select.Element calucalator = (new Select.Element(this.utils, "Калькулятор (/ah sell)")).set(true); public Select.Element getCalucalator() { return this.calucalator; }
/*  138 */    private final Select.Element autochorus = new Select.Element(this.utils, "Авто Хорус"); public Select.Element getAutochorus() { return this.autochorus; }
/*  139 */    private final Select.Element noplacesphere = new Select.Element(this.utils, "Не ставить сферу"); public Select.Element getNoplacesphere() { return this.noplacesphere; }
/*      */   
/*  141 */   private final Select.Element printEffects = new Select.Element(this.utils, "Вывод полученных эффектов"); public Select.Element getPrintEffects() { return this.printEffects; }
/*  142 */    private final Select.Element recudeTime = new Select.Element(this.utils, "Уменьшать задержки"); public Select.Element getRecudeTime() { return this.recudeTime; }
/*      */   
/*  144 */   private final Select hwUtils = (new Select((Bindable)this, "HolyWorld")).desc("Вспомогательные функции для HolyWorld"); public Select getHwUtils() { return this.hwUtils; }
/*  145 */    private final Select.Element autoStop = new Select.Element(this.hwUtils, "Авто стоп"); public Select.Element getAutoStop() { return this.autoStop; }
/*  146 */    private final Select.Element bindHw = new Select.Element(this.hwUtils, "Исп. по бинду"); public Select.Element getBindHw() { return this.bindHw; }
/*  147 */    private final Select.Element fastbreak = new Select.Element(this.hwUtils, "Быстрое разрушение шалкеров"); public Select.Element getFastbreak() { return this.fastbreak; }
/*      */   
/*  149 */   private final Select selectHw = (new Select((Bindable)this.bindHw, "Бинд на...")).hide(() -> Boolean.valueOf(!this.bindHw.get())); public Select getSelectHw() { return this.selectHw; }
/*  150 */    private final Select.Element stan = new Select.Element(this.selectHw, "Стан"); public Select.Element getStan() { return this.stan; }
/*  151 */    private final Select.Element trapBoom = new Select.Element(this.selectHw, "Взрывная трапка"); public Select.Element getTrapBoom() { return this.trapBoom; }
/*      */   
/*  153 */   private final Binding stanBind = (new Binding((Bindable)this.bindHw, "Клавиша стана")).hide(() -> Boolean.valueOf((!this.stan.get() || !this.bindHw.get()))); public Binding getStanBind() { return this.stanBind; }
/*  154 */    private final Binding trapBoomBind = (new Binding((Bindable)this.trapBoom, "Клавиша взрывной трапки")).hide(() -> Boolean.valueOf((!this.trapBoom.get() || !this.bindHw.get()))); public Binding getTrapBoomBind() { return this.trapBoomBind; }
/*      */   
/*  156 */   private final CheckBox floats = (new CheckBox((Bindable)this.calucalator, "Округлять числа")).hide(() -> Boolean.valueOf(!this.calucalator.get())); public CheckBox getFloats() { return this.floats; }
/*      */   
/*  158 */   private final Select select = (new Select((Bindable)this.bind, "Бинд на..")).hide(() -> Boolean.valueOf(!this.bind.get())).desc("Выберите предметы, которые будут использоваться по бинду"); public Select getSelect() { return this.select; }
/*  159 */    private final Select.Element trap = new Select.Element(this.select, "Трапка"); public Select.Element getTrap() { return this.trap; }
/*  160 */    private final Select.Element autoplast = new Select.Element(this.select, "Пласт"); public Select.Element getAutoplast() { return this.autoplast; }
/*  161 */    private final Select.Element shalk = new Select.Element(this.select, "Шалкер"); public Select.Element getShalk() { return this.shalk; }
/*  162 */    private final Select.Element smerch = new Select.Element(this.select, "Огненый смерч"); public Select.Element getSmerch() { return this.smerch; }
/*  163 */    private final Select.Element aura = new Select.Element(this.select, "Божья аура"); public Select.Element getAura() { return this.aura; }
/*  164 */    private final Select.Element crossbow = new Select.Element(this.select, "Арбалет"); public Select.Element getCrossbow() { return this.crossbow; }
/*  165 */    private final Select.Element pilb = new Select.Element(this.select, "Явная пыль"); public Select.Element getPilb() { return this.pilb; }
/*  166 */    private final Select.Element flesh = new Select.Element(this.select, "Моча Флеша"); public Select.Element getFlesh() { return this.flesh; }
/*  167 */    private final Select.Element med = new Select.Element(this.select, "Зелье медика"); public Select.Element getMed() { return this.med; }
/*  168 */    private final Select.Element agent = new Select.Element(this.select, "Зелье Агента"); public Select.Element getAgent() { return this.agent; }
/*  169 */    private final Select.Element win = new Select.Element(this.select, "Зелье Победителя"); public Select.Element getWin() { return this.win; }
/*  170 */    private final Select.Element killer = new Select.Element(this.select, "Зелье Киллера"); public Select.Element getKiller() { return this.killer; }
/*  171 */    private final Select.Element otr = new Select.Element(this.select, "Зелье Открыжки"); public Select.Element getOtr() { return this.otr; }
/*  172 */    private final Select.Element serka = new Select.Element(this.select, "Серная Кислота"); public Select.Element getSerka() { return this.serka; }
/*  173 */    private final Select.Element vsp = new Select.Element(this.select, "Вспышка"); public Select.Element getVsp() { return this.vsp; }
/*  174 */    private final Select.Element snow = new Select.Element(this.select, "Снежок заморозки"); public Select.Element getSnow() { return this.snow; }
/*  175 */    private final Select.Element dezorent = new Select.Element(this.select, "Дезориентация"); public Select.Element getDezorent() { return this.dezorent; }
/*  176 */    private final Select.Element trident = new Select.Element(this.select, "Трезубец"); public Select.Element getTrident() { return this.trident; }
/*  177 */    private final Select.Element horus = new Select.Element(this.select, "Хорус"); public Select.Element getHorus() { return this.horus; }
/*  178 */    private final Select.Element charGapple = new Select.Element(this.select, "Чарка"); public Select.Element getCharGapple() { return this.charGapple; }
/*      */   
/*  180 */   private final Binding trapBind = (new Binding((Bindable)this.bind, "Клавиша трапки")).hide(() -> Boolean.valueOf((!this.trap.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет активироваться трапка"); public Binding getTrapBind() { return this.trapBind; }
/*  181 */    private final Binding crossbowBind = (new Binding((Bindable)this.bind, "Клавиша арбалета")).hide(() -> Boolean.valueOf((!this.crossbow.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет выстреливать арбалет"); public Binding getCrossbowBind() { return this.crossbowBind; }
/*  182 */    private final Binding autoplastBind = (new Binding((Bindable)this.bind, "Клавиша пласта")).hide(() -> Boolean.valueOf((!this.autoplast.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет активироваться пласт"); public Binding getAutoplastBind() { return this.autoplastBind; }
/*  183 */    private final Binding smerchBind = (new Binding((Bindable)this.bind, "Клавиша смерча")).hide(() -> Boolean.valueOf((!this.smerch.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет активироваться смерч"); public Binding getSmerchBind() { return this.smerchBind; }
/*  184 */    private final Binding auraBind = (new Binding((Bindable)this.bind, "Клавиша ауры")).hide(() -> Boolean.valueOf((!this.aura.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет активироваться аура"); public Binding getAuraBind() { return this.auraBind; }
/*  185 */    private final Binding pilbBind = (new Binding((Bindable)this.bind, "Клавиша пыли")).hide(() -> Boolean.valueOf((!this.pilb.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет активироваться пыль"); public Binding getPilbBind() { return this.pilbBind; }
/*  186 */    private final Binding fleshBind = (new Binding((Bindable)this.bind, "Клавиша флеша")).hide(() -> Boolean.valueOf((!this.flesh.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет бросаться флеш"); public Binding getFleshBind() { return this.fleshBind; }
/*  187 */    private final Binding medBind = (new Binding((Bindable)this.bind, "Клавиша медика")).hide(() -> Boolean.valueOf((!this.med.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет бросаться медик"); public Binding getMedBind() { return this.medBind; }
/*  188 */    private final Binding agentBind = (new Binding((Bindable)this.bind, "Клавиша агента")).hide(() -> Boolean.valueOf((!this.agent.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет бросаться агентка"); public Binding getAgentBind() { return this.agentBind; }
/*  189 */    private final Binding winBind = (new Binding((Bindable)this.bind, "Клавиша победилки")).hide(() -> Boolean.valueOf((!this.win.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет бросаться победилка"); public Binding getWinBind() { return this.winBind; }
/*  190 */    private final Binding killerBind = (new Binding((Bindable)this.bind, "Клавиша киллера")).hide(() -> Boolean.valueOf((!this.killer.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет бросаться киллерка"); public Binding getKillerBind() { return this.killerBind; }
/*  191 */    private final Binding otrBind = (new Binding((Bindable)this.bind, "Клавиша отрыжки")).hide(() -> Boolean.valueOf((!this.otr.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет бросаться отрыжка"); public Binding getOtrBind() { return this.otrBind; }
/*  192 */    private final Binding serkaBind = (new Binding((Bindable)this.bind, "Клавиша серки")).hide(() -> Boolean.valueOf((!this.serka.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет бросаться серка"); public Binding getSerkaBind() { return this.serkaBind; }
/*  193 */    private final Binding vspBind = (new Binding((Bindable)this.bind, "Клавиша вспышки")).hide(() -> Boolean.valueOf((!this.vsp.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет бросаться вспышка"); public Binding getVspBind() { return this.vspBind; }
/*  194 */    private final Binding snowBind = (new Binding((Bindable)this.bind, "Клавиша снежка")).hide(() -> Boolean.valueOf((!this.snow.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет активироваться снежок"); public Binding getSnowBind() { return this.snowBind; }
/*  195 */    private final Binding dezorentBind = (new Binding((Bindable)this.bind, "Клавиша дезорента")).hide(() -> Boolean.valueOf((!this.dezorent.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет активироваться дезориентация"); public Binding getDezorentBind() { return this.dezorentBind; }
/*  196 */    private final Binding tridentBind = (new Binding((Bindable)this.bind, "Клавиша трезубца")).hide(() -> Boolean.valueOf((!this.trident.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет бросаться трезубец"); public Binding getTridentBind() { return this.tridentBind; }
/*  197 */    private final Binding horusBind = (new Binding((Bindable)this.bind, "Клавиша хоруса")).hide(() -> Boolean.valueOf((!this.horus.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет использоваться хорус"); public Binding getHorusBind() { return this.horusBind; }
/*  198 */    private final Binding charGappleBind = (new Binding((Bindable)this.bind, "Клавиша чарки")).hide(() -> Boolean.valueOf((!this.charGapple.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет использоваться чаркка"); public Binding getCharGappleBind() { return this.charGappleBind; }
/*  199 */    private final Binding shalkBind = (new Binding((Bindable)this.bind, "Клавиша шалкера")).hide(() -> Boolean.valueOf((!this.shalk.get() || !this.bind.get()))).desc("Кнопка, при нажатии которой, будет открываться шалкер"); public Binding getShalkBind() { return this.shalkBind; }
/*      */   
/*  201 */   private final CheckBox brainPlast = (new CheckBox((Bindable)this.bind, "Умный пласт")).hide(() -> Boolean.valueOf((!this.bind.get() || !this.autoplast.get()))).desc("Если снизу трапка и нажат бинд на пласт то ставит пласт вниз"); public CheckBox getBrainPlast() { return this.brainPlast; }
/*  202 */    private final CheckBox onlyHotbar = (new CheckBox((Bindable)this.bind, "Только из хотбара")).hide(() -> Boolean.valueOf((!this.bind.get() || this.select.getToggled().isEmpty()))); public CheckBox getOnlyHotbar() { return this.onlyHotbar; }
/*      */   
/*  204 */   private final Slider count = (new Slider((Bindable)this.fastbreak, "Кол-во разрушений")).min(2.0F).max(500.0F).inc(1.0F).set(10.0F).hide(() -> Boolean.valueOf(!this.fastbreak.get())).desc("Устанавливает кол-во разрушений шалкера за один раз"); public Slider getCount() { return this.count; }
/*  205 */    private final CheckBox nearPlayer = (new CheckBox((Bindable)this.trap, "Только в радиусе")).desc("Позволяет использовать только когда игрок попадает в радиус трапки").hide(() -> Boolean.valueOf(!this.trap.get())); private int prevTridentSlot; private boolean usingTrident; public CheckBox getNearPlayer() { return this.nearPlayer; }
/*      */ 
/*      */   
/*  208 */   public int getPrevTridentSlot() { return this.prevTridentSlot; } public boolean isUsingTrident() {
/*  209 */     return this.usingTrident;
/*      */   }
/*      */   
/*  212 */   private final TimerUtility fixTimer = new TimerUtility(); public TimerUtility getFixTimer() { return this.fixTimer; }
/*      */ 
/*      */   
/*  215 */   private final Pattern TIME_PATTERN = Pattern.compile("До следующего ивента:?\\s+(\\w+)"); public Pattern getTIME_PATTERN() { return this.TIME_PATTERN; }
/*      */    private boolean messageOtkup = false; public boolean isMessageOtkup() {
/*  217 */     return this.messageOtkup;
/*      */   } private int prevChorusSlot; private boolean usingChorus;
/*      */   public int getPrevChorusSlot() {
/*  220 */     return this.prevChorusSlot; } public boolean isUsingChorus() {
/*  221 */     return this.usingChorus;
/*  222 */   } private TimerUtility chorusTimer = new TimerUtility(); public TimerUtility getChorusTimer() { return this.chorusTimer; }
/*  223 */    private TimerUtility chorusWaitTimer = new TimerUtility(); public TimerUtility getChorusWaitTimer() { return this.chorusWaitTimer; }
/*      */ 
/*      */   
/*  226 */   private String results = ""; public String getResults() { return this.results; }
/*  227 */    private final Animation calcAnim = (new Animation()).setEasing(Easing.BOTH_SINE).setSpeed(300); private boolean stop; public Animation getCalcAnim() { return this.calcAnim; }
/*      */    public boolean isStop() {
/*  229 */     return this.stop;
/*  230 */   } private final TimerUtility delay = new TimerUtility(); public TimerUtility getDelay() { return this.delay; }
/*  231 */    private int tridentEntityId = -1; public int getTridentEntityId() { return this.tridentEntityId; }
/*  232 */    private int tridentThrowerId = -1; private String potion; private boolean use; private boolean key; public int getTridentThrowerId() { return this.tridentThrowerId; } public String getPotion() {
/*  233 */     return this.potion;
/*      */   }
/*  235 */   public boolean isUse() { return this.use; } public boolean isKey() {
/*  236 */     return this.key;
/*  237 */   } private int tick = 0; public int getTick() { return this.tick; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void onEvent(Event event) {
/*  244 */     if (this.fastbreak.get() && event instanceof EventSendPacket) {
/*  245 */       EventSendPacket e = (EventSendPacket)event;
/*  246 */       IPacket iPacket = e.getPacket(); if (iPacket instanceof CPlayerDiggingPacket) { CPlayerDiggingPacket packet = (CPlayerDiggingPacket)iPacket; if (packet
/*  247 */           .getAction() == CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK && mc.world
/*  248 */           .getBlock(packet.getPosition()) instanceof net.minecraft.block.ShulkerBoxBlock)
/*      */         {
/*  250 */           for (int i = 0; i < this.count.get() - 1.0F; i++)
/*      */           {
/*  252 */             mc.player.connection.sendPacketSilent((IPacket)new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, packet.getPosition(), packet.getFacing()));
/*      */           }
/*      */         } }
/*      */     
/*      */     } 
/*  257 */     if (event instanceof EventKey) { EventKey e = (EventKey)event; if (this.bindHw.get() && !(mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen)) {
/*  258 */         handleKeyHw(e);
/*      */       } }
/*      */ 
/*      */     
/*  262 */     if (this.autoStop.get()) {
/*  263 */       if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/*  264 */         IPacket iPacket = e.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket;
/*  265 */           String message = TextFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getString());
/*  266 */           if (message.contains("Телепортация начнется")) {
/*  267 */             this.stop = true;
/*  268 */             this.delay.reset();
/*      */           }  }
/*      */ 
/*      */         
/*  272 */         if (e.getPacket() instanceof net.minecraft.network.play.server.SPlayerPositionLookPacket) {
/*  273 */           this.stop = false;
/*      */         } }
/*      */ 
/*      */       
/*  277 */       if (event instanceof EventInput) { EventInput e = (EventInput)event; if (this.stop && !this.delay.passed(5000L)) {
/*  278 */           e.setForward(0.0F);
/*  279 */           e.setStrafe(0.0F);
/*  280 */           e.setJump(false);
/*      */         }  }
/*      */       
/*  283 */       if (event instanceof fun.rockstarity.api.events.list.game.EventWorldChange) {
/*  284 */         this.stop = false;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  290 */     if (this.rpSpoof.get() && event instanceof EventUpdate && mc.player.ticksExisted < 10) {
/*  291 */       mc.player.connection.sendPacket((IPacket)new CResourcePackStatusPacket(CResourcePackStatusPacket.Action.ACCEPTED));
/*  292 */       mc.player.connection.sendPacket((IPacket)new CResourcePackStatusPacket(CResourcePackStatusPacket.Action.SUCCESSFULLY_LOADED));
/*  293 */       if (mc.currentScreen instanceof net.minecraft.client.gui.screen.ConfirmScreen) mc.displayGuiScreen(null);
/*      */     
/*      */     } 
/*      */     
/*  297 */     if (event instanceof EventMotionMove) { EventMotionMove e = (EventMotionMove)event;
/*  298 */       if (this.dragonFly.get() && 
/*  299 */         mc.player.abilities.isFlying) {
/*  300 */         if (!mc.player.isSneaking() && (mc.getGameSettings()).keyBindJump.isKeyDown()) {
/*  301 */           (mc.player.getMotion()).y = this.speedY.get();
/*  302 */         } else if ((mc.getGameSettings()).keyBindSneak.isKeyDown()) {
/*  303 */           (mc.player.getMotion()).y = -this.speedY.get();
/*      */         } 
/*  305 */         Move.setMoveMotion(e, this.speedXZ.get());
/*      */       }  }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  312 */     if (event instanceof fun.rockstarity.api.events.list.player.EventMotion)
/*      */     {
/*  314 */       if (this.autoFix.get() && !Server.hasCT()) {
/*      */         
/*  316 */         PlayerInventory inv = mc.player.inventory;
/*      */         
/*  318 */         for (int i = 0; i < inv.getSizeInventory(); i++) {
/*  319 */           ItemStack stack = inv.getStackInSlot(i);
/*  320 */           if (!stack.isEmpty() && stack.getItem().isDamageable()) {
/*  321 */             int max = stack.getMaxDamage(), cur = max - stack.getDamage();
/*  322 */             double perc = cur / max;
/*      */             
/*  324 */             if (perc < 0.5D && mc.player.ticksExisted % 25 == 0) {
/*  325 */               mc.player.sendChatMessage("/fix all");
/*  326 */               this.fixTimer.reset();
/*      */ 
/*      */               
/*      */               break;
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  337 */     if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/*      */       
/*  339 */       if (this.closeMenu.get()) {
/*  340 */         IPacket iPacket1 = e.getPacket(); if (iPacket1 instanceof SOpenWindowPacket) { SOpenWindowPacket packet = (SOpenWindowPacket)iPacket1;
/*  341 */           String text = packet.getTitle().getUnformattedComponentText().trim();
/*  342 */           if (text.contains("Меню") || packet.getTitle().getString().contains("ꈁꀀꈂꌁꈂꀁ")) {
/*  343 */             mc.player.connection.sendPacket((IPacket)new CCloseWindowPacket(packet.getWindowId()));
/*  344 */             e.cancel();
/*      */           }  }
/*      */       
/*      */       } 
/*      */       
/*  349 */       IPacket iPacket = e.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket;
/*  350 */         String message = TextFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getString());
/*      */         
/*  352 */         if (message.contains("У Вас нет доступа к данной команде.") && !this.fixTimer.passed(500L)) {
/*  353 */           this.autoFix.set(false);
/*      */         } }
/*      */        }
/*      */ 
/*      */     
/*  358 */     if (this.autootkup.get() && event instanceof EventUpdate) {
/*  359 */       Aura auraModule = (Aura)rock.getModules().get(Aura.class);
/*  360 */       LivingEntity target = auraModule.getPrevTarget();
/*      */       
/*  362 */       if (target != null && 
/*  363 */         this.otkupMode.is(this.def)) {
/*  364 */         float health = target.getHealth();
/*  365 */         if (health < this.hpotkup.get() && !this.messageOtkup) {
/*  366 */           mc.player.sendChatMessage(target.getName().getString() + " " + target.getName().getString());
/*  367 */           this.messageOtkup = true;
/*  368 */         } else if (health >= this.hpotkup.get() && this.messageOtkup) {
/*  369 */           this.messageOtkup = false;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  375 */     if (this.autootkup.get() && event instanceof EventTotemBreak) { EventTotemBreak e = (EventTotemBreak)event;
/*  376 */       if (this.otkupMode.is(this.totem)) {
/*  377 */         LivingEntity livingEntity = e.getEntity();
/*  378 */         if (livingEntity instanceof LivingEntity) { LivingEntity living = livingEntity;
/*  379 */           if (!this.messageOtkup) {
/*  380 */             mc.player.sendChatMessage(living.getName().getString() + " " + living.getName().getString());
/*  381 */             this.messageOtkup = true;
/*      */           }  }
/*      */       
/*      */       }  }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  390 */     if (this.recudeTime.get()) {
/*  391 */       if (event instanceof EventSetCooldown) { EventSetCooldown e = (EventSetCooldown)event;
/*  392 */         if (e.getItem() == Items.ENCHANTED_GOLDEN_APPLE || e
/*  393 */           .getItem() == Items.GOLDEN_APPLE || e
/*  394 */           .getItem() == Items.POTION || e
/*  395 */           .getItem() == Items.CHORUS_FRUIT) {
/*  396 */           e.setCooldown(e.getCooldown() - 32);
/*      */         } }
/*      */ 
/*      */       
/*  400 */       if (event instanceof EventFinishEat) { EventFinishEat e = (EventFinishEat)event; if (ItemUtility.contains(mc.player.getHeldItemMainhand(), "Исцеление")) {
/*  401 */           mc.player.getCooldownTracker().setCooldown(Items.POTION, 270);
/*      */         } }
/*      */       
/*  404 */       if (event instanceof fun.rockstarity.api.events.list.render.EventRender2D && (mc.player.getHeldItemMainhand().getItem() != Items.POTION || ItemUtility.contains(mc.player.getHeldItemMainhand(), "Исцеление"))) {
/*  405 */         if (mc.player.getCooldownTracker().hasCooldown(mc.player.getHeldItemMainhand().getItem())) {
/*  406 */           (mc.getGameSettings()).keyBindUseItem.setPressed(false);
/*      */         } else {
/*  408 */           (mc.getGameSettings()).keyBindUseItem.setPressed((GLFW.glfwGetMouseButton(mc.getMainWindow().getHandle(), (mc.getGameSettings()).keyBindUseItem.getDefault().getKeyCode()) == 1));
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  414 */     if (this.printEffects.get() && 
/*  415 */       event instanceof EventPotionHit) { EventPotionHit e = (EventPotionHit)event;
/*  416 */       if (e.getTarget().getName().getString().equals(mc.player.getName().getString()))
/*  417 */         return;  if (!(e.getTarget() instanceof PlayerEntity))
/*      */         return; 
/*  419 */       Chat.msg(String.format("%s%s%s получил %s%s", new Object[] { TextFormatting.WHITE, e.getTarget().getName().getString(), TextFormatting.GRAY, TextFormatting.WHITE, e.getPotion().getName().getString() }));
/*  420 */       for (EffectInstance effectinstance : e.getEffects()) {
/*  421 */         Effect effect = effectinstance.getPotion();
/*  422 */         int i = (int)(e.getDuration() * effectinstance.getDuration() + 0.5D);
/*      */         
/*  424 */         if (i > 20) {
/*      */           
/*  426 */           EffectInstance eff = new EffectInstance(effect, i, effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.doesShowParticles());
/*  427 */           Chat.msg(
/*  428 */               String.format("%s%s %s %s(%s)", new Object[] {
/*  429 */                   (effect.getEffectType() == EffectType.HARMFUL) ? TextFormatting.RED : TextFormatting.BLUE, I18n.format(eff.getEffectName(), new Object[0]), 
/*  430 */                   I18n.format("enchantment.level." + eff.getAmplifier() + 1, new Object[0]).replace("enchantment.level.", ""), TextFormatting.GRAY, 
/*  431 */                   EffectUtils.getPotionDurationString(eff, 1.0F)
/*      */                 }));
/*      */         } 
/*      */       }  }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  475 */     if (this.nearPlayer.get() && 
/*  476 */       event instanceof EventUpdate) {
/*  477 */       double partialTicks = (mc.currentScreen == null) ? mc.getRenderPartialTicks() : 0.0D;
/*  478 */       double x = mc.player.lastTickPosX + (mc.player.getPosX() - mc.player.lastTickPosX) * partialTicks;
/*  479 */       double y = mc.player.lastTickPosY + (mc.player.getPosY() - mc.player.lastTickPosY) * partialTicks;
/*  480 */       double z = mc.player.lastTickPosZ + (mc.player.getPosZ() - mc.player.lastTickPosZ) * partialTicks;
/*      */       
/*  482 */       AxisAlignedBB box1 = new AxisAlignedBB(x - 1.0D, y, z - 1.0D, x + 2.0D, y + 2.0D, z + 2.0D);
/*  483 */       AxisAlignedBB box2 = new AxisAlignedBB(x - 2.0D, y, z - 2.0D, x + 3.0D, y + 3.0D, z + 3.0D);
/*      */       
/*  485 */       this.use = mc.world.getPlayers().stream().filter(ent -> (ent != mc.player)).map(Entity::getBoundingBox).anyMatch(entityBox -> (box1.intersects(entityBox) || box2.intersects(entityBox)));
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  490 */     if (this.autochorus.get() && ((Aura)rock.getModules().get(Aura.class)).getPrevTarget() != null) {
/*  491 */       if (event instanceof EventUpdate) { EventUpdate e = (EventUpdate)event;
/*  492 */         LivingEntity target = ((Aura)rock.getModules().get(Aura.class)).getPrevTarget();
/*  493 */         if (mc.player.getDistance((Entity)target) < 5.0F) {
/*  494 */           if (!(target.getActiveItemStack().getItem() instanceof net.minecraft.item.ChorusFruitItem)) {
/*  495 */             this.chorusTimer.reset();
/*      */           }
/*      */           
/*  498 */           if (this.chorusTimer.passed(200L) && target.getActiveItemStack().getItem() instanceof net.minecraft.item.ChorusFruitItem) {
/*  499 */             int slot = Inventory.findItemNoChanges(44, Items.CHORUS_FRUIT);
/*      */             
/*  501 */             boolean inHotbar = (slot <= 8);
/*  502 */             if (slot != -1 && !this.usingChorus) {
/*  503 */               this.prevChorusSlot = mc.player.inventory.currentItem;
/*  504 */               (mc.getGameSettings()).keyBindUseItem.setPressed(true);
/*      */               
/*  506 */               if (inHotbar) {
/*  507 */                 mc.player.inventory.currentItem = slot;
/*      */               } else {
/*  509 */                 mc.playerController.pickItem(slot);
/*      */               } 
/*      */               
/*  512 */               this.usingChorus = true;
/*      */             } 
/*      */             
/*  515 */             this.chorusWaitTimer.reset();
/*      */           } 
/*      */         } 
/*      */         
/*  519 */         if (this.usingChorus && this.chorusWaitTimer.passed(1000L)) {
/*  520 */           boolean inHotbar = (this.prevChorusSlot <= 8);
/*      */           
/*  522 */           (mc.getGameSettings()).keyBindUseItem.setPressed(false);
/*      */           
/*  524 */           if (inHotbar) {
/*  525 */             mc.player.inventory.currentItem = this.prevChorusSlot;
/*      */           } else {
/*  527 */             mc.playerController.pickItem(this.prevChorusSlot);
/*      */           } 
/*  529 */           this.usingChorus = false;
/*      */         }  }
/*      */ 
/*      */       
/*  533 */       if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event; if (((Aura)rock.getModules().get(Aura.class)).getPrevTarget() != null) {
/*  534 */           LivingEntity target = ((Aura)rock.getModules().get(Aura.class)).getPrevTarget();
/*      */           
/*  536 */           IPacket iPacket = e.getPacket(); if (iPacket instanceof SEntityTeleportPacket) { SEntityTeleportPacket packet = (SEntityTeleportPacket)iPacket; if (packet.getEntityId() == target.getEntityId())
/*  537 */               this.chorusWaitTimer.reset();  }
/*      */         
/*      */         }  }
/*      */       
/*  541 */       if (event instanceof EventAttack) { EventAttack e = (EventAttack)event; if (this.usingChorus) {
/*  542 */           e.cancel();
/*      */         } }
/*      */       
/*  545 */       if (event instanceof EventFinishEat) { EventFinishEat e = (EventFinishEat)event; if (e.getEntity() == mc.player && this.usingChorus && 
/*  546 */           e.getItem() instanceof net.minecraft.item.ChorusFruitItem) {
/*  547 */           boolean inHotbar = (this.prevChorusSlot <= 8);
/*      */           
/*  549 */           if (inHotbar) {
/*  550 */             mc.player.inventory.currentItem = this.prevChorusSlot;
/*      */           } else {
/*  552 */             mc.playerController.pickItem(this.prevChorusSlot);
/*      */           } 
/*  554 */           (mc.getGameSettings()).keyBindUseItem.setPressed(false);
/*  555 */           this.usingChorus = false;
/*      */         }  }
/*      */     
/*      */     } 
/*      */ 
/*      */     
/*  561 */     if (this.calucalator.get()) {
/*  562 */       if (event instanceof EventSendPacket) { EventSendPacket e = (EventSendPacket)event;
/*  563 */         IPacket iPacket = e.getPacket(); if (iPacket instanceof CChatMessagePacket) { CChatMessagePacket packet = (CChatMessagePacket)iPacket;
/*  564 */           String msg = packet.getMessage();
/*      */           
/*  566 */           if (msg.startsWith("/ah sell ")) {
/*  567 */             String result = calc(msg.replace("/ah sell ", ""));
/*  568 */             if (result.matches("\\d+(\\.\\d+)?")) {
/*  569 */               packet.setMessage("/ah sell " + result);
/*      */             }
/*  571 */             this.results = "";
/*  572 */             this.calcAnim.setForward(false);
/*      */           }  }
/*      */          }
/*      */       
/*  576 */       if (event instanceof EventChatScreen) { EventChatScreen e = (EventChatScreen)event;
/*  577 */         String input = e.getField().getText().replace("/ah sell ", "");
/*  578 */         this.calcAnim.setForward(!this.results.isEmpty());
/*  579 */         if (!input.isEmpty() && input.matches(".*[+\\-*/%\\cos\\sin].*") && e.getField().getText().startsWith("/ah sell ")) {
/*  580 */           this.results = calc(input);
/*      */         } else {
/*  582 */           this.results = "";
/*      */         } 
/*  584 */         if (!this.calcAnim.finished(false)) bold.get(16).draw(e.getMatrixStack(), "Итоговое число: " + this.results, 3.0F * this.calcAnim.get(), (sr.getScaledHeight() - 27), rock.getThemes().getTextFirstColor().alpha(this.calcAnim.get()));
/*      */          }
/*      */     
/*      */     } 
/*  588 */     if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/*  589 */       IPacket iPacket = e.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket;
/*  590 */         String m = packet.getChatComponent().getString();
/*  591 */         if (m.contains("Invalid move player packet received")) {
/*  592 */           e.cancel();
/*      */         } }
/*      */        }
/*      */     
/*  596 */     if (this.autopiona.get() && event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/*  597 */       IPacket iPacket = e.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket;
/*  598 */         String m = packet.getChatComponent().getString();
/*      */ 
/*      */         
/*  601 */         if (m.contains("10,000 было начислено вам") || m.contains("Повторите текст еще раз")) {
/*  602 */           mc.player.sendChatMessage("/piona");
/*      */         } }
/*      */ 
/*      */       
/*  606 */       iPacket = e.getPacket(); if (iPacket instanceof SOpenWindowPacket) { SOpenWindowPacket packet = (SOpenWindowPacket)iPacket;
/*      */         
/*  608 */         if (packet.getTitle().getString().contains("Вам подарок")) {
/*  609 */           e.cancel();
/*  610 */           (mc.getGameSettings()).keyBindSneak.setPressed(true);
/*      */         }  }
/*      */ 
/*      */       
/*  614 */       iPacket = e.getPacket(); if (iPacket instanceof SPlaySoundEffectPacket) { SPlaySoundEffectPacket packet = (SPlaySoundEffectPacket)iPacket;
/*      */         
/*  616 */         if (packet.getSound().getName().toString().contains("glass.place") || packet.getSound().getName().toString().contains("note_block.xylophone")) {
/*  617 */           e.cancel();
/*      */         } }
/*      */ 
/*      */       
/*  621 */       iPacket = e.getPacket(); if (iPacket instanceof SSetSlotPacket) { SSetSlotPacket packet = (SSetSlotPacket)iPacket;
/*  622 */         ItemStack stack = packet.getStack();
/*      */ 
/*      */         
/*  625 */         if (stack.getDisplayName().getString().contains("Ключ")) {
/*  626 */           (mc.getGameSettings()).keyBindSneak.setPressed(false);
/*      */           
/*  628 */           boolean click = true;
/*      */           
/*  630 */           List<ITextComponent> itemTooltip = stack.getTooltip(null, (ITooltipFlag)ITooltipFlag.TooltipFlags.NORMAL);
/*  631 */           for (ITextComponent str : itemTooltip) {
/*  632 */             if (str.getString().contains("Стоп")) {
/*  633 */               mc.player.connection.sendPacket((IPacket)new CCloseWindowPacket(packet.getWindowId()));
/*  634 */               click = false;
/*      */               
/*      */               break;
/*      */             } 
/*      */           } 
/*  639 */           if (click) {
/*  640 */             mc.player.connection.sendPacket((IPacket)new CClickWindowPacket(packet.getWindowId(), 13, 0, ClickType.PICKUP, mc.player.openContainer
/*      */                   
/*  642 */                   .getSlot(13).getStack(), mc.player.openContainer
/*  643 */                   .getNextTransactionID(mc.player.inventory)));
/*      */           }
/*      */         }  }
/*      */        }
/*      */     
/*  648 */     if (this.brainPlast.get()) {
/*  649 */       if (this.tick > 0) Player.look(event, mc.player.rotationYaw, 90.0F, true); 
/*  650 */       if (event instanceof EventUpdate) {
/*  651 */         if (this.tick > 0) this.tick--; 
/*  652 */         if (this.tick == 0 && this.key) {
/*  653 */           if (this.onlyHotbar.get()) {
/*  654 */             hotbar(Items.DRIED_KELP);
/*      */           } else {
/*  656 */             InvUtility.use(Items.DRIED_KELP);
/*      */           } 
/*  658 */           this.key = false;
/*  659 */           this.tick = -1;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  665 */     if (this.time.get() && event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event; IPacket iPacket = e.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket;
/*  666 */         String message = TextFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getString());
/*  667 */         Matcher matcher = this.TIME_PATTERN.matcher(message);
/*  668 */         if (matcher.find() && !matcher.group(1).isEmpty()) {
/*  669 */           boolean numeric = matcher.group(1).matches("[-+]?[0-9]*\\.?[0-9]+");
/*  670 */           if (!numeric) {
/*      */             return;
/*      */           }
/*  673 */           int parse = Integer.parseInt(matcher.group(1));
/*  674 */           String minutes = "" + parse / 60 + " мин";
/*  675 */           String seconds = "" + parse % 60 + " сeк";
/*      */           
/*  677 */           String correctedMessage = message.replace("[1]§r До следующего ивента:§r ", "").replace(" сек§r", "");
/*      */           
/*  679 */           String formattedTime = "§c" + minutes + " " + seconds;
/*      */           
/*  681 */           packet.setChatComponent((ITextComponent)new TranslationTextComponent(message.replace(matcher.group(1), formattedTime).replace("[1]", "§c[1]").replace("До следующего", "§6До следующего").replace("ивента:§r ", "ивента:§r§c ").replace("сек", "")));
/*      */         } 
/*      */         
/*  684 */         if (matcher.find() && message.contains("Статус: »") && !matcher.group(1).isEmpty()) {
/*  685 */           boolean numeric = matcher.group(1).matches("[-+]?[0-9]*\\.?[0-9]+");
/*      */           
/*  687 */           if (!numeric) {
/*      */             return;
/*      */           }
/*  690 */           int parsed = Integer.parseInt(message.split(" ")[(message.split(" ")).length - 2]);
/*      */           
/*  692 */           String minutes = "" + parsed / 60 + " мин";
/*  693 */           String seconds = "" + parsed % 60 + " сeк";
/*      */           
/*  695 */           String correctedMessage = message.replace("[1]§r До следующего ивента:§r ", "").replace(" сек§r", "");
/*      */           
/*  697 */           String formattedTime = ": §c" + minutes + " " + seconds;
/*      */           
/*  699 */           packet.setChatComponent((ITextComponent)new TranslationTextComponent(message.replace(" " + parsed, formattedTime).replace("||", "§c||").replace("Статус", "§fСтатус").replace("»", "§e»").replace("сек", "")));
/*      */         }  }
/*      */        }
/*      */ 
/*      */ 
/*      */     
/*  705 */     if (event instanceof EventFinishEat) { EventFinishEat e = (EventFinishEat)event; if (e.getEntity() == mc.player && 
/*  706 */         this.autorct.get() && e.getItem() == Items.ENCHANTED_GOLDEN_APPLE && !Server.hasCT()) {
/*  707 */         if (!((KTLeave)rock.getModules().get(KTLeave.class)).getBinds().isEmpty()) {
/*  708 */           ((KTLeave)rock.getModules().get(KTLeave.class)).toggle();
/*      */         } else {
/*  710 */           rock.getCommands().execute("rct");
/*      */         } 
/*      */       } }
/*      */ 
/*      */     
/*  715 */     if (this.visualRadius.get() && 
/*  716 */       event instanceof EventUpdate) {
/*  717 */       for (PlayerEntity entity : mc.world.getPlayers()) {
/*  718 */         if (entity == mc.player)
/*  719 */           continue;  ItemStack heldItem = mc.player.getHeldItemMainhand();
/*  720 */         String itemName = heldItem.getDisplayName().getString().toLowerCase();
/*  721 */         boolean glowNearby = false;
/*      */         
/*  723 */         if (itemName.contains("дезориентация") && mc.player.getDistance((Entity)entity) <= 10.0F) {
/*  724 */           glowNearby = true;
/*      */         }
/*      */         
/*  727 */         if (itemName.contains("трапка") && mc.player.getDistance((Entity)entity) <= 4.0F) {
/*  728 */           glowNearby = true;
/*      */         }
/*      */         
/*  731 */         entity.setGlowing(glowNearby);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  737 */     if (event instanceof EventRender3D) { EventRender3D e = (EventRender3D)event; if (this.visual.get()) {
/*  738 */         MatrixStack ms = e.getMatrixStack();
/*      */         
/*  740 */         if (this.players.get()) {
/*  741 */           for (PlayerEntity entity : mc.world.getPlayers()) {
/*  742 */             renderEffects(ms, (LivingEntity)entity, e);
/*      */           }
/*      */         }
/*      */         
/*  746 */         renderEffects(ms, (LivingEntity)mc.player, e);
/*      */       }  }
/*      */ 
/*      */     
/*  750 */     if (!(mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen) && this.bind.get() && 
/*  751 */       event instanceof EventKey) { EventKey e = (EventKey)event;
/*  752 */       handleKey(e); }
/*      */ 
/*      */ 
/*      */     
/*  756 */     if (event instanceof EventUpdate && this.usingTrident) {
/*  757 */       (mc.getGameSettings()).keyBindUseItem.setPressed(true);
/*      */     }
/*      */   }
/*      */   
/*      */   public void handleKeyHw(EventKey e) {
/*  762 */     if (!e.isReleased()) {
/*  763 */       handleItemMovement(e, this.stan.get(), this.stanBind, Items.NETHER_STAR);
/*  764 */       handleItemMovement(e, this.trapBoom.get(), this.trapBoomBind, Items.PRISMARINE_SHARD);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void handleHoldItems(Item item, EventKey e) {
/*  769 */     if (item.isCooldowned())
/*      */       return; 
/*  771 */     if (!e.isReleased()) {
/*  772 */       int from = Inventory.findItemNoChanges(44, item);
/*      */       
/*  774 */       if (from == -1)
/*      */         return; 
/*  776 */       boolean inHotbar = (from <= 8);
/*  777 */       if (inHotbar) {
/*  778 */         this.prevTridentSlot = mc.player.inventory.currentItem;
/*  779 */         mc.player.inventory.currentItem = from;
/*  780 */         (mc.getGameSettings()).keyBindUseItem.setPressed(true);
/*      */       } else {
/*  782 */         this.prevTridentSlot = from;
/*  783 */         mc.playerController.pickItem(from);
/*      */       } 
/*      */       
/*  786 */       this.usingTrident = true;
/*      */     }
/*  788 */     else if (this.usingTrident) {
/*  789 */       (mc.getGameSettings()).keyBindUseItem.setPressed(false);
/*  790 */       mc.player.connection.sendPacket((IPacket)new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
/*      */       
/*  792 */       boolean inHotbar = (this.prevTridentSlot <= 8);
/*      */       
/*  794 */       if (inHotbar) {
/*  795 */         mc.player.inventory.currentItem = this.prevTridentSlot;
/*      */       } else {
/*  797 */         mc.playerController.pickItem(this.prevTridentSlot);
/*      */       } 
/*  799 */       this.usingTrident = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void renderEffects(MatrixStack ms, LivingEntity entity, EventRender3D e) {
/*  805 */     ItemStack heldItem = entity.getHeldItemMainhand();
/*  806 */     String itemName = heldItem.getDisplayName().getString().toLowerCase();
/*      */     
/*  808 */     double partialTicks = (mc.currentScreen == null) ? mc.getRenderPartialTicks() : 0.0D;
/*  809 */     double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * partialTicks - (mc.getRenderManager()).info.getProjectedView().getX();
/*  810 */     double y = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * partialTicks - (mc.getRenderManager()).info.getProjectedView().getY();
/*  811 */     double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * partialTicks - (mc.getRenderManager()).info.getProjectedView().getZ();
/*      */     
/*  813 */     if (itemName.contains("дезориентация")) {
/*  814 */       drawGlowEffect(ms, x, y, z, e);
/*      */     }
/*      */     
/*  817 */     if (itemName.contains("трапка")) {
/*  818 */       drawBoxEffect(e);
/*      */     }
/*      */   }
/*      */   
/*      */   private void drawGlowEffect(MatrixStack ms, double x, double y, double z, EventRender3D e) {
/*  823 */     int len = 361;
/*  824 */     double size = 1.5D;
/*  825 */     int start = (int)(mc.getRenderManager()).info.getPitch();
/*      */     
/*  827 */     for (int i = start; i <= start + len - 1; i++) {
/*  828 */       double angle = Math.toRadians(i);
/*  829 */       double s = 0.5D;
/*  830 */       double posX = x + Math.cos(angle) * 20.0D * s;
/*  831 */       double posY = y;
/*  832 */       double posZ = z + Math.sin(angle) * 20.0D * s;
/*      */       
/*  834 */       ms.push();
/*  835 */       GlStateManager.depthMask(false);
/*  836 */       ms.translate(posX, posY, posZ);
/*  837 */       ms.rotate((mc.getRenderManager()).info.getRotation());
/*      */       
/*  839 */       Render.drawImage(ms, "masks/glow.png", -size / 2.0D, -size / 2.0D, -size / 2.0D, size, size, Style.getPoint(i * 10));
/*  840 */       GlStateManager.depthMask(true);
/*  841 */       ms.pop();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void drawBoxEffect(EventRender3D e) {
/*  846 */     ClientPlayerEntity clientPlayerEntity = mc.player;
/*      */     
/*  848 */     double playerX = ((LivingEntity)clientPlayerEntity).lastTickPosX + (clientPlayerEntity.getPosX() - ((LivingEntity)clientPlayerEntity).lastTickPosX) * mc.getRenderPartialTicks() - (mc.getRenderManager()).info.getProjectedView().getX();
/*  849 */     double playerY = ((LivingEntity)clientPlayerEntity).lastTickPosY + (clientPlayerEntity.getPosY() - ((LivingEntity)clientPlayerEntity).lastTickPosY) * mc.getRenderPartialTicks() - (mc.getRenderManager()).info.getProjectedView().getY();
/*  850 */     double playerZ = ((LivingEntity)clientPlayerEntity).lastTickPosZ + (clientPlayerEntity.getPosZ() - ((LivingEntity)clientPlayerEntity).lastTickPosZ) * mc.getRenderPartialTicks() - (mc.getRenderManager()).info.getProjectedView().getZ();
/*      */     
/*  852 */     GL11.glPushMatrix();
/*  853 */     GL11.glPushAttrib(1048575);
/*      */     
/*  855 */     GL11.glRotated(e.getRenderInfo().getPitch(), 1.0D, 0.0D, 0.0D);
/*  856 */     GL11.glRotated((e.getRenderInfo().getYaw() + 180.0F), 0.0D, 1.0D, 0.0D);
/*      */     
/*  858 */     GL11.glBlendFunc(770, 771);
/*  859 */     GL11.glEnable(3042);
/*  860 */     GL11.glLineWidth(2.0F);
/*  861 */     GL11.glDisable(3553);
/*  862 */     GL11.glDisable(2929);
/*      */     
/*  864 */     double offsetX = -0.5D;
/*  865 */     double offsetY = 0.0D;
/*  866 */     double offsetZ = -0.5D;
/*      */     
/*  868 */     Render.setColor(Style.getMain().getRGB());
/*  869 */     Render.drawBoxing(new AxisAlignedBB(playerX + offsetX - 1.0D, playerY + offsetY, playerZ + offsetZ - 1.0D, playerX + offsetX + 2.0D, playerY + offsetY, playerZ + offsetZ + 2.0D));
/*      */     
/*  871 */     Render.setColor(Style.getSecond().getRGB());
/*  872 */     Render.drawBoxing(new AxisAlignedBB(playerX + offsetX - 2.0D, playerY + offsetY, playerZ + offsetZ - 2.0D, playerX + offsetX + 3.0D, playerY + offsetY, playerZ + offsetZ + 3.0D));
/*      */     
/*  874 */     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/*      */     
/*  876 */     GL11.glPopAttrib();
/*  877 */     GL11.glPopMatrix();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void handleKey(EventKey e) {
/*  883 */     if (mc.currentScreen != null)
/*      */       return; 
/*  885 */     if (!e.isReleased()) {
/*  886 */       if (this.use || !this.nearPlayer.get()) {
/*  887 */         handleItemMovement(e, this.trap.get(), this.trapBind, Items.NETHERITE_SCRAP);
/*      */       }
/*  889 */       handleItemMovement(e, this.autoplast.get(), this.autoplastBind, Items.DRIED_KELP);
/*  890 */       handleItemMovement(e, this.smerch.get(), this.smerchBind, Items.FIRE_CHARGE);
/*  891 */       handleItemMovement(e, this.aura.get(), this.auraBind, Items.PHANTOM_MEMBRANE);
/*  892 */       handleItemMovement(e, this.pilb.get(), this.pilbBind, Items.SUGAR);
/*  893 */       handleItemMovement(e, this.dezorent.get(), this.dezorentBind, Items.ENDER_EYE);
/*  894 */       handleItemMovement(e, this.snow.get(), this.snowBind, Items.SNOWBALL);
/*  895 */       handleItemMovement(e, this.shalk.get(), this.shalkBind, Items.SHULKER_BOX);
/*  896 */       handleItemMovement(e, this.crossbow.get(), this.crossbowBind, Items.CROSSBOW);
/*  897 */       handleNamedPotionThrow(e, this.agent.get(), this.agentBind, "Зелье Агента");
/*  898 */       handleNamedPotionThrow(e, this.med.get(), this.medBind, "Зелье медика");
/*  899 */       handleNamedPotionThrow(e, this.win.get(), this.winBind, "Зелье Победителя");
/*  900 */       handleNamedPotionThrow(e, this.killer.get(), this.killerBind, "Зелье Киллера");
/*  901 */       handleNamedPotionThrow(e, this.otr.get(), this.otrBind, "Зелье Открыжки");
/*  902 */       handleNamedPotionThrow(e, this.serka.get(), this.serkaBind, "Серная Кислота");
/*  903 */       handleNamedPotionThrow(e, this.vsp.get(), this.vspBind, "Вспышка");
/*  904 */       handleNamedPotionThrow(e, this.flesh.get(), this.fleshBind, "Моча Флеша");
/*      */     } 
/*      */     
/*  907 */     if (this.trident.get() && this.tridentBind.getBindByKey(e).isPresent()) {
/*  908 */       handleHoldItems(Items.TRIDENT, e);
/*      */     }
/*      */     
/*  911 */     if (this.horus.get() && this.horusBind.getBindByKey(e).isPresent()) {
/*  912 */       handleHoldItems(Items.CHORUS_FRUIT, e);
/*      */     }
/*      */     
/*  915 */     if (this.charGapple.get() && this.charGappleBind.getBindByKey(e).isPresent()) {
/*  916 */       handleHoldItems(Items.ENCHANTED_GOLDEN_APPLE, e);
/*      */     }
/*      */   }
/*      */   
/*      */   private void handleItemMovement(EventKey e, boolean isFeatureEnabled, Binding bind, Item item) {
/*  921 */     if (isFeatureEnabled && bind.getBindByKey(e).isPresent() && Player.findItemInInv(45, item) != -1) {
/*  922 */       if (this.autoplastBind.getParent() == bind.getParent()) {
/*  923 */         if (this.brainPlast.get() && isTrapNear()) {
/*  924 */           this.key = true;
/*  925 */           this.tick = 2;
/*      */         }
/*  927 */         else if (this.onlyHotbar.get()) {
/*  928 */           hotbar(item);
/*      */         } else {
/*  930 */           InvUtility.use(item);
/*      */         }
/*      */       
/*      */       }
/*  934 */       else if (this.onlyHotbar.get()) {
/*  935 */         hotbar(item);
/*      */       } else {
/*  937 */         InvUtility.use(item);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void handleNamedPotionThrow(EventKey e, boolean enabled, Binding bind, String expectedName) {
/*  944 */     if (!enabled || !bind.getBindByKey(e).isPresent())
/*      */       return; 
/*  946 */     for (int i = 0; i < 36; i++) {
/*  947 */       ItemStack stack = mc.player.inventory.getStackInSlot(i);
/*  948 */       if (isMatchingPotion(stack, expectedName)) {
/*  949 */         if (this.onlyHotbar.get()) {
/*  950 */           hotbar(stack.getItem());
/*      */         } else {
/*  952 */           InvUtility.use(stack.getItem());
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private void hotbar(Item item) {
/*  959 */     for (int i = 0; i < 9; i++) {
/*  960 */       ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
/*  961 */       if (itemStack.getItem() == item) {
/*  962 */         mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(i));
/*  963 */         if (((Aura)rock.getModules().get(Aura.class)).getTarget() != null) {
/*  964 */           mc.player.connection.sendPacket((IPacket)new CPlayerPacket.RotationPacket(mc.player.rotationYaw, mc.player.rotationPitch, true));
/*      */         }
/*      */         
/*  967 */         mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
/*  968 */         mc.player.swingArm(Hand.MAIN_HAND);
/*  969 */         mc.player.connection.sendPacket((IPacket)new CHeldItemChangePacket(mc.player.inventory.currentItem));
/*      */         break;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public String calc(String expression) {
/*  976 */     if (expression.matches(".*[а-яА-Я]+.*")) {
/*  977 */       expression = RussianNumberParser.convert(expression);
/*      */     }
/*      */     
/*  980 */     expression = expression.replaceAll("\\s+", "");
/*      */     
/*  982 */     if (expression.isEmpty()) return "";
/*      */     
/*      */     try {
/*  985 */       Expression expr = (new ExpressionBuilder(expression)).build();
/*  986 */       double result = expr.evaluate();
/*  987 */       if (this.floats.get()) {
/*  988 */         result = Math.round(result);
/*      */       }
/*      */       
/*  991 */       if (result == (long)result) {
/*  992 */         return String.valueOf((long)result);
/*      */       }
/*  994 */       return String.valueOf(result);
/*      */     }
/*  996 */     catch (IllegalArgumentException e) {
/*  997 */       return expression;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void dodge(EventInput e, Entity entity) {
/* 1007 */     if (mc.player.getDistance(entity.getPositionVec()) < 7.0F || mc.player
/* 1008 */       .getDistance(entity.getPositionVec().add(entity.getMotion().mul(2.0D, 2.0D, 2.0D))) < 7.0F || mc.player
/* 1009 */       .getDistance(entity.getPositionVec().add(entity.getMotion().mul(3.0D, 3.0D, 3.0D))) < 7.0F || mc.player
/* 1010 */       .getDistance(entity.getPositionVec().add(entity.getMotion().mul(4.0D, 4.0D, 4.0D))) < 7.0F || mc.player
/* 1011 */       .getDistance(entity.getPositionVec().add(entity.getMotion().mul(5.0D, 5.0D, 5.0D))) < 7.0F) {
/* 1012 */       if (Player.getAngle(entity) < 45.0F || Player.getAngle(entity) > 135.0F) {
/* 1013 */         e.setStrafe((entity.getEntityId() % 2 == 0) ? 1.0F : -1.0F);
/*      */       } else {
/* 1015 */         e.setForward(1.0F);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isMatchingPotion(ItemStack stack, String expectedName) {
/* 1023 */     Item item = stack.getItem();
/* 1024 */     if ((item instanceof net.minecraft.item.PotionItem || item instanceof net.minecraft.item.SplashPotionItem || item instanceof net.minecraft.item.LingeringPotionItem) && 
/* 1025 */       stack.hasDisplayName()) {
/* 1026 */       return stack.getDisplayName().getString().contains(expectedName);
/*      */     }
/*      */     
/* 1029 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isTrapNear() {
/* 1033 */     BlockPos playerPos = mc.player.getPosition();
/*      */     
/* 1035 */     for (int x = -3; x <= 1; x++) {
/* 1036 */       for (int z = -3; z <= 1; z++) {
/* 1037 */         BlockPos checkPos = playerPos.add(x, -1, z);
/* 1038 */         if (mc.world.isAirBlock(checkPos) && mc.world.isAirBlock(checkPos.down()) && mc.world.isAirBlock(checkPos.down(2))) {
/* 1039 */           int solidSides = 0;
/* 1040 */           if (mc.world.getBlockState(checkPos.north()).isSolid()) solidSides++; 
/* 1041 */           if (mc.world.getBlockState(checkPos.south()).isSolid()) solidSides++; 
/* 1042 */           if (mc.world.getBlockState(checkPos.east()).isSolid()) solidSides++; 
/* 1043 */           if (mc.world.getBlockState(checkPos.west()).isSolid()) solidSides++;
/*      */           
/* 1045 */           if (solidSides >= 3) {
/* 1046 */             return true;
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1052 */     return false;
/*      */   }
/*      */   
/*      */   public void onDisable() {}
/*      */   
/*      */   public void onEnable() {}
/*      */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\Assist.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */