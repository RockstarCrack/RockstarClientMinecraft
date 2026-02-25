/*      */ package fun.rockstarity.client.modules.combat;
/*      */ 
/*      */ import fun.rockstarity.api.ClientInfo;
/*      */ import fun.rockstarity.api.binds.Bindable;
/*      */ import fun.rockstarity.api.events.Event;
/*      */ import fun.rockstarity.api.events.EventType;
/*      */ import fun.rockstarity.api.events.list.game.EventDamage;
/*      */ import fun.rockstarity.api.events.list.game.EventTick;
/*      */ import fun.rockstarity.api.events.list.game.inputs.EventInput;
/*      */ import fun.rockstarity.api.events.list.game.packet.EventSendPacket;
/*      */ import fun.rockstarity.api.events.list.player.EventJump;
/*      */ import fun.rockstarity.api.events.list.player.EventMotion;
/*      */ import fun.rockstarity.api.events.list.player.EventMotionMove;
/*      */ import fun.rockstarity.api.events.list.player.EventMove;
/*      */ import fun.rockstarity.api.events.list.player.EventPostMotion;
/*      */ import fun.rockstarity.api.events.list.player.EventUpdate;
/*      */ import fun.rockstarity.api.helpers.game.Chat;
/*      */ import fun.rockstarity.api.helpers.game.Server;
/*      */ import fun.rockstarity.api.helpers.math.DamageUtility;
/*      */ import fun.rockstarity.api.helpers.math.MathUtility;
/*      */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*      */ import fun.rockstarity.api.helpers.math.VectorUtility;
/*      */ import fun.rockstarity.api.helpers.math.aura.AuraUtility;
/*      */ import fun.rockstarity.api.helpers.math.aura.IdealHitUtility;
/*      */ import fun.rockstarity.api.helpers.math.aura.Rotation;
/*      */ import fun.rockstarity.api.helpers.player.Bypass;
/*      */ import fun.rockstarity.api.helpers.player.FallingPlayer;
/*      */ import fun.rockstarity.api.helpers.player.Move;
/*      */ import fun.rockstarity.api.helpers.player.Player;
/*      */ import fun.rockstarity.api.modules.Category;
/*      */ import fun.rockstarity.api.modules.Info;
/*      */ import fun.rockstarity.api.modules.Module;
/*      */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*      */ import fun.rockstarity.api.modules.settings.list.Mode;
/*      */ import fun.rockstarity.api.modules.settings.list.Select;
/*      */ import fun.rockstarity.api.modules.settings.list.Slider;
/*      */ import fun.rockstarity.api.render.animation.Animation;
/*      */ import fun.rockstarity.api.render.animation.Easing;
/*      */ import fun.rockstarity.api.render.animation.infinity.RotationAnimation;
/*      */ import fun.rockstarity.api.render.ui.alerts.AlertType;
/*      */ import fun.rockstarity.api.render.ui.alerts.Tooltip;
/*      */ import fun.rockstarity.client.modules.move.AutoSprint;
/*      */ import fun.rockstarity.client.modules.other.Globals;
/*      */ import fun.rockstarity.client.modules.player.AutoPearl;
/*      */ import java.util.Comparator;
/*      */ import net.minecraft.block.Blocks;
/*      */ import net.minecraft.entity.Entity;
/*      */ import net.minecraft.entity.LivingEntity;
/*      */ import net.minecraft.entity.player.PlayerEntity;
/*      */ import net.minecraft.item.UseAction;
/*      */ import net.minecraft.network.IPacket;
/*      */ import net.minecraft.network.play.client.CEntityActionPacket;
/*      */ import net.minecraft.network.play.client.CPlayerDiggingPacket;
/*      */ import net.minecraft.network.play.client.CPlayerPacket;
/*      */ import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
/*      */ import net.minecraft.potion.Effects;
/*      */ import net.minecraft.util.Direction;
/*      */ import net.minecraft.util.Hand;
/*      */ import net.minecraft.util.math.BlockPos;
/*      */ import net.minecraft.util.math.MathHelper;
/*      */ import net.minecraft.util.math.vector.Vector2f;
/*      */ import net.minecraft.util.math.vector.Vector3d;
/*      */ import net.minecraft.util.math.vector.Vector3f;
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
/*      */ @Info(name = "Aura", desc = "Бьёт женщин и детей", type = Category.COMBAT, module = {"KillAura", "AttackAura", "HitAura"})
/*      */ public class AuraOld
/*      */   extends Module
/*      */ {
/*  130 */   protected final Mode mode = new Mode((Bindable)this, "Режим");
/*  131 */   protected final Mode.Element classic = new Mode.Element(this.mode, "Классический");
/*      */   
/*  133 */   protected final Mode.Element funtime = new Mode.Element(this.mode, "FunTime");
/*  134 */   protected final Mode.Element snap = new Mode.Element(this.mode, "Снап");
/*  135 */   protected final Mode.Element spooky = new Mode.Element(this.mode, "Spooky"); public Mode.Element getSpooky() { return this.spooky; }
/*      */   
/*  137 */   protected final CheckBox adaptDistance = new CheckBox((Bindable)this.spooky, "Фикс дистанции"); public CheckBox getAdaptDistance() { return this.adaptDistance; }
/*  138 */    protected final Slider spookySpeed = (new Slider((Bindable)this.spooky, "Добавочная скорость")).min(-0.5F).max(0.5F).inc(0.05F).set(-0.15F).desc("Добавочная скорость наводки на цель");
/*  139 */   protected final Slider animSpeed = (new Slider((Bindable)this.spooky, "Скорость анимации")).min(3.0F).max(15.0F).inc(1.0F).set(8.0F).desc("Скорость анимации для наводки. Чем больше - тем медленнее");
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  144 */   protected final CheckBox airCrits = (new CheckBox((Bindable)this, "Воздушные криты")).desc("Зависает в воздухе, позволяя бить на дистанции 6 блоков");
/*      */   
/*  146 */   protected final CheckBox onlyCrits = (new CheckBox((Bindable)this, "Только криты")).set(true).desc("Aura будет бить только тогда, когда может нанести критический удар").hide(() -> Boolean.valueOf(this.airCrits.get())); public CheckBox getOnlyCrits() { return this.onlyCrits; }
/*  147 */    protected final Slider range = (new Slider((Bindable)this, "Дистанция")).min(2.0F).max(6.0F).inc(0.1F).set(3.0F).desc("Дистанция, на которой Aura может ударить").hide(() -> Boolean.valueOf(this.airCrits.get()));
/*  148 */   protected final Slider rangaTip = (new Slider((Bindable)this, "Дистанция наводки")).min(0.0F).max(2.0F).inc(0.1F).set(0.5F).desc("Дистанция, на которой Aura будет искать цель. Плюсуется к обычной дистанции");
/*  149 */   protected final Select targets = (new Select((Bindable)this, "Цели")).min(1).desc("Сущности, которых будет бить Aura");
/*      */   
/*  151 */   protected final Select.Element players = (new Select.Element(this.targets, "Игроки")).set(true);
/*  152 */   protected final Select.Element invisibles = (new Select.Element(this.targets, "Невидимые")).set(true).hide(() -> Boolean.valueOf(!this.players.get()));
/*  153 */   protected final Select.Element naked = (new Select.Element(this.targets, "Голые")).set(true).hide(() -> Boolean.valueOf(!this.players.get()));
/*  154 */   protected final Select.Element friends = (new Select.Element(this.targets, "Друзья")).hide(() -> Boolean.valueOf(!this.players.get()));
/*  155 */   protected final Select.Element bots = (new Select.Element(this.targets, "Боты")).hide(() -> Boolean.valueOf(!this.players.get()));
/*  156 */   protected final Select.Element mobs = new Select.Element(this.targets, "Мобы");
/*  157 */   protected final Select.Element rockUser = (new Select.Element(this.targets, "Пользователи " + String.valueOf(ClientInfo.NAME))).hide(() -> Boolean.valueOf(!((Globals)rock.getModules().get(Globals.class)).get()));
/*      */   
/*  159 */   protected final Mode correction = (new Mode((Bindable)this, "Коррекция движения")).desc("Корректировка движений для обхода некоторых античитов");
/*  160 */   protected final Mode.Element no = new Mode.Element(this.correction, "Нет");
/*  161 */   protected final Mode.Element focused = new Mode.Element(this.correction, "Сфокусированная");
/*  162 */   protected final Mode.Element silent = new Mode.Element(this.correction, "Незаметная");
/*      */   
/*  164 */   protected final CheckBox notifBrack = (new CheckBox((Bindable)this, "Увед. о ломании щита")).set(true).desc("Выводить уведомление при ломании щита сопернику"); public CheckBox getNotifBrack() { return this.notifBrack; }
/*      */   
/*  166 */   protected final CheckBox raycast = (new CheckBox((Bindable)this, "Проверка наводки")).set(true).desc("Aura будет бить только тогда, когда навелась на соперника").hide(() -> Boolean.valueOf(this.airCrits.get()));
/*      */   
/*  168 */   protected final Select additions = (new Select((Bindable)this, "Дополнения")).desc("Различные дополнения для Aura");
/*      */   
/*  170 */   protected final Select.Element onlyWeapon = new Select.Element(this.additions, "Только с оружием");
/*  171 */   protected final Select.Element walls = (new Select.Element(this.additions, "Через стены")).set(true); public Select.Element getWalls() { return this.walls; }
/*  172 */    protected final Select.Element notEat = new Select.Element(this.additions, "Не бить если ешь");
/*  173 */   protected final Select.Element tickSelect = new Select.Element(this.additions, "Умный выбор тика");
/*  174 */   protected final Select.Element attackOnGround = (new Select.Element(this.additions, "Бить на земле")).set(true);
/*      */   
/*  176 */   protected final Mode sort = new Mode((Bindable)this, "Сортировка");
/*  177 */   protected final Mode.Element fov = new Mode.Element(this.sort, "По полю зрения");
/*  178 */   protected final Mode.Element distance = new Mode.Element(this.sort, "По дистанции");
/*  179 */   protected final Mode.Element health = new Mode.Element(this.sort, "По здоровью");
/*      */   
/*  181 */   protected final Mode sprint = new Mode((Bindable)this, "Сброс спринта");
/*  182 */   protected final Mode.Element legit = new Mode.Element(this.sprint, "Легитный");
/*  183 */   protected final Mode.Element classicSprint = new Mode.Element(this.sprint, "Обычный");
/*  184 */   protected final Mode.Element none = new Mode.Element(this.sprint, "Нет");
/*  185 */   protected final Mode.Element always = new Mode.Element(this.sprint, "Постоянно");
/*  186 */   protected final Mode.Element superlegit = new Mode.Element(this.sprint, "Универсальный");
/*      */   
/*  188 */   protected final Mode boost = new Mode((Bindable)this, "Ускорение");
/*  189 */   protected final Mode.Element notBoost = new Mode.Element(this.boost, "Нет");
/*  190 */   protected final Mode.Element direct = new Mode.Element(this.boost, "Прямо");
/*  191 */   protected final Mode.Element toTarget = (new Mode.Element(this.boost, "На цель")).ifEnabled(true);
/*      */ 
/*      */   
/*  194 */   protected final Mode boostMode = new Mode((Bindable)this.toTarget, "Режим");
/*  195 */   protected final Mode.Element funtimeBoost = new Mode.Element(this.boostMode, "FunTime");
/*  196 */   protected final Mode.Element spookyBoost = new Mode.Element(this.boostMode, "Spooky");
/*  197 */   protected final Mode.Element custom = new Mode.Element(this.boostMode, "Свой");
/*      */   
/*  199 */   protected final Slider fallingSpeed = (new Slider((Bindable)this.toTarget, "Скорость падения")).min(0.0F).max(0.4F).inc(0.05F).set(0.3F).desc("Скорость, c которой игрок будет ускоряться падая").hide(() -> Boolean.valueOf((this.funtimeBoost.get() || this.spookyBoost.get())));
/*  200 */   protected final Slider jumpSpeed = (new Slider((Bindable)this.toTarget, "Скорость прыжка")).min(0.0F).max(0.4F).inc(0.05F).set(0.3F).desc("Скорость, c которой игрок будет ускоряться прыгая").hide(() -> Boolean.valueOf((this.funtimeBoost.get() || this.spookyBoost.get())));
/*  201 */   protected final Slider groundSpeed = (new Slider((Bindable)this.toTarget, "Скорость на земле")).min(0.0F).max(0.4F).inc(0.05F).set(0.0F).desc("Скорость, c которой игрок будет ускоряться на земле").hide(() -> Boolean.valueOf((this.funtimeBoost.get() || this.spookyBoost.get())));
/*  202 */   protected final Slider centrifugalForce = (new Slider((Bindable)this.toTarget, "Центробежная сила")).min(0.0F).max(0.3F).inc(0.05F).set(0.0F).desc("Отклонение от центра для вращения").hide(() -> Boolean.valueOf((this.funtimeBoost.get() || this.spookyBoost.get())));
/*  203 */   protected final Slider multiplier = (new Slider((Bindable)this.toTarget, "Множитель")).min(-0.2F).max(3.0F).inc(0.05F).set(-0.1F).desc("Множитель ускорения. Для FunTime желательно ставить значения до 0, для SpookyTime можно ставить значения вполть до 3. Если не знаете, что ставить под конкретно ваш сервер, оствьте 0 или -0.1").hide(() -> Boolean.valueOf(false));
/*      */   
/*  205 */   protected final CheckBox pres = (new CheckBox((Bindable)this, "Преследование")).desc("Не меняет цель даже если появилась другая, более приоритетная цель");
/*      */   
/*  207 */   protected final Slider speed = (new Slider((Bindable)this, "Скорость")).min(170.0F).max(470.0F).inc(10.0F).set(370.0F).desc("Скорость, на которой Aura будет наводится").hide(() -> Boolean.valueOf(!this.mode.is(this.funtime))); protected CheckBox hitlogger;
/*      */   public void setHitlogger(CheckBox hitlogger) {
/*  209 */     this.hitlogger = hitlogger;
/*      */   }
/*  211 */   protected Vector3f rotation = Vector3f.ZERO; public Vector3f getRotation() { return this.rotation; } public void setRotation(Vector3f rotation) { this.rotation = rotation; }
/*  212 */    protected final RotationAnimation rotAnim = new RotationAnimation(); public RotationAnimation getRotAnim() { return this.rotAnim; }
/*  213 */    protected final RotationAnimation spookyAnim = new RotationAnimation(); public RotationAnimation getSpookyAnim() { return this.spookyAnim; }
/*  214 */    protected final RotationAnimation rotAnimAdv = new RotationAnimation(); public RotationAnimation getRotAnimAdv() { return this.rotAnimAdv; }
/*  215 */    protected final RotationAnimation fakeAnim = new RotationAnimation(); protected LivingEntity target; public RotationAnimation getFakeAnim() { return this.fakeAnim; }
/*      */    protected LivingEntity prevTarget; protected boolean requireCritical; public LivingEntity getTarget() {
/*  217 */     return this.target; } public LivingEntity getPrevTarget() { return this.prevTarget; } public void setTarget(LivingEntity target) { this.target = target; } public void setPrevTarget(LivingEntity prevTarget) { this.prevTarget = prevTarget; }
/*      */ 
/*      */ 
/*      */   
/*  221 */   protected final TimerUtility attackTimer = new TimerUtility(); public TimerUtility getAttackTimer() { return this.attackTimer; }
/*  222 */    protected final TimerUtility disableTimer = new TimerUtility();
/*  223 */   protected final TimerUtility swapDirectionYawTimer = new TimerUtility(); protected boolean directionYaw; protected int attacks; protected boolean logged;
/*      */   public int getAttacks() {
/*  225 */     return this.attacks;
/*      */   }
/*      */ 
/*      */   
/*      */   protected float prevTargetHealth;
/*      */   
/*      */   protected int hitCounter;
/*      */   
/*      */   protected boolean attacked;
/*  234 */   protected int rotationTicks = -1;
/*      */   
/*      */   protected int aimTicks;
/*      */   
/*  238 */   protected double[] air = null;
/*      */   
/*      */   protected boolean disabled;
/*      */   protected boolean firstHit;
/*      */   protected int hitsUnderBlocks;
/*  243 */   protected Animation anim = (new Animation()).setSpeed(170).setSize(1.0F).setEasing(Easing.EASE_IN_OUT_QUART);
/*  244 */   protected Animation anim1 = (new Animation()).setSpeed(170).setSize(1.0F).setEasing(Easing.EASE_IN_OUT_QUART);
/*  245 */   protected Animation anim3 = (new Animation()).setSpeed(170).setSize(1.0F).setEasing(Easing.EASE_IN_OUT_QUART);
/*  246 */   protected Animation anim4 = (new Animation()).setSpeed(170).setSize(1.0F).setEasing(Easing.EASE_IN_OUT_QUART);
/*      */   
/*      */   public AuraOld() {
/*  249 */     super(10);
/*      */   }
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
/*      */   public void onAllEvent(Event event) {
/*  280 */     if (event instanceof EventUpdate && mc.player.ticksExisted > 50) {
/*      */       
/*  282 */       if (Server.isHW()) {
/*  283 */         String server = "HolyWorld";
/*  284 */         if (this.walls.get()) rock.getAlertHandler().alert(Tooltip.create("\"Через стены\" не работает на " + server), AlertType.INFO); 
/*  285 */         if (!this.raycast.get()) rock.getAlertHandler().alert(Tooltip.create("\"Проверка наводки\" желательно включать на " + server), AlertType.INFO);
/*      */       
/*      */       } 
/*      */       
/*  289 */       if (Server.isFT()) {
/*  290 */         String server = "FunTime";
/*  291 */         if (!this.raycast.get()) rock.getAlertHandler().alert(Tooltip.create("\"Проверка наводки\" желательно включать на " + server), AlertType.INFO);
/*      */       
/*      */       } 
/*      */       
/*  295 */       if (Server.isRW()) {
/*  296 */         String server = "ReallyWorld";
/*  297 */         if (!this.raycast.get()) rock.getAlertHandler().alert(Tooltip.create("\"Проверка наводки\" желательно включать на " + server), AlertType.INFO);
/*      */       
/*      */       } 
/*      */     } 
/*  301 */     if (((this.mode.is(this.funtime) && !this.disableTimer.passed(500L)) || get()) && this.prevTarget != null) {
/*  302 */       if (event instanceof EventTick) {
/*  303 */         if (this.mode.is(this.spooky)) {
/*  304 */           updateAdvanced();
/*      */         } else {
/*  306 */           calculateRotation();
/*      */         } 
/*      */       }
/*      */       
/*  310 */       if (event instanceof EventMotion) { EventMotion e = (EventMotion)event;
/*  311 */         if (((AutoPearl)rock.getModules().get(AutoPearl.class)).getTick() > 0)
/*  312 */           return;  rotate(e);
/*  313 */         if (mc.player.isOnGround()) {
/*  314 */           IdealHitUtility.setJumped(false);
/*      */         } }
/*      */ 
/*      */       
/*  318 */       correctMovement(event);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   @EventType({EventTick.class, EventMotion.class, EventPostMotion.class, EventUpdate.class})
/*      */   public void onEvent(Event event) {
/*  325 */     if (this.onlyWeapon.get() && !(mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.SwordItem) && !(mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.AxeItem))
/*      */       return; 
/*  327 */     if (event instanceof EventUpdate) {
/*  328 */       findTarget(range() + getAdditionalRange());
/*      */     }
/*      */     
/*  331 */     if (event instanceof EventUpdate && (
/*  332 */       this.target == null || mc.player.isOnGround())) {
/*  333 */       ((AutoSprint)rock.getModules().get(AutoSprint.class)).setCanSprint(true);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  338 */     if (this.target == null)
/*      */       return; 
/*  340 */     handlePackets(event);
/*      */     
/*  342 */     if (event instanceof EventMotion) { EventMotion e = (EventMotion)event;
/*  343 */       if (((AutoPearl)rock.getModules().get(AutoPearl.class)).getTick() > 0)
/*  344 */         return;  rotate(e);
/*  345 */       if (mc.player.isOnGround()) {
/*  346 */         IdealHitUtility.setJumped(false);
/*      */       } }
/*      */ 
/*      */     
/*  350 */     Criticals criticals = (Criticals)rock.getModules().get(Criticals.class);
/*  351 */     if (event instanceof EventMotionMove) { EventMotionMove e = (EventMotionMove)event; if (criticals.get() && criticals.getMode().is(criticals.getFuntime()) && mc.player.isOnGround()) {
/*  352 */         float range = Server.isFT() ? 2.8F : range();
/*  353 */         boolean result = (!this.raycast.get() || MathUtility.rayTraceWithBlock(7.0D, this.rotation.x, this.rotation.y, (Entity)mc.player, (Entity)this.target, false));
/*      */ 
/*      */         
/*  356 */         boolean canAttack = (AuraUtility.distanceTo(AuraUtility.getPoint(this.target)) <= range && mc.player.getCooledAttackStrength() >= IdealHitUtility.getAICooldown());
/*      */         
/*  358 */         if (canAttack || !this.attackTimer.passed(200L)) {
/*  359 */           e.setMotion(Vector3d.ZERO.add(0.0D, (e.getMotion()).y, 0.0D));
/*  360 */           Move.setSpeed(0.0D);
/*      */         } 
/*      */       }  }
/*      */     
/*  364 */     if (event instanceof EventDamage) { EventDamage e = (EventDamage)event; if (e.getTarget() == this.prevTarget) {
/*  365 */         this.prevTarget.lastHit.reset();
/*      */       } }
/*      */     
/*  368 */     if (FallingPlayer.fromPlayer(mc.player).findFall(IdealHitUtility.getAIFallDistance()) && this.always.get()) {
/*  369 */       (mc.getGameSettings()).keyBindSprint.setPressed(false);
/*  370 */       mc.player.setSprinting(false);
/*  371 */       ((AutoSprint)rock.getModules().get(AutoSprint.class)).setCanSprint(false);
/*      */     } 
/*      */     
/*  374 */     if (event instanceof EventUpdate) {
/*  375 */       if (canHit() && !mc.player.isOnGround() && ((IdealHitUtility.canAIFall() && FallingPlayer.fromPlayer(mc.player).findFall(IdealHitUtility.getAIFallDistance())) || criticals.get()) && this.legit.get()) {
/*      */         
/*  377 */         (mc.getGameSettings()).keyBindSprint.setPressed(false);
/*  378 */         mc.player.setSprinting(false);
/*  379 */         mc.player.connection.sendPacket((IPacket)new CEntityActionPacket((Entity)mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
/*  380 */         mc.player.serverSprintState = false;
/*  381 */         ((AutoSprint)rock.getModules().get(AutoSprint.class)).setCanSprint(false);
/*      */       } 
/*      */ 
/*      */       
/*  385 */       if (canHit() && !mc.player.isOnGround() && ((IdealHitUtility.canAIFall() && (FallingPlayer.fromPlayer(mc.player).findFall(IdealHitUtility.getAIFallDistance(), 2) || canCritical())) || criticals.get()) && this.superlegit.get()) {
/*      */         
/*  387 */         (mc.getGameSettings()).keyBindForward.setPressed(false);
/*  388 */         (mc.getGameSettings()).keyBindSprint.setPressed(false);
/*  389 */         mc.player.setSprinting(false);
/*  390 */         mc.player.connection.sendPacket((IPacket)new CEntityActionPacket((Entity)mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
/*  391 */         mc.player.serverSprintState = false;
/*  392 */         ((AutoSprint)rock.getModules().get(AutoSprint.class)).setCanSprint(false);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  397 */     if (event instanceof EventUpdate) {
/*  398 */       if (this.hitlogger != null && this.hitlogger.get() && this.attackTimer.passed(200L) && !this.logged) {
/*  399 */         float healthDiff = this.prevTargetHealth - this.target.getHealth() + this.target.getAbsorptionAmount();
/*  400 */         float requiredDamage = DamageUtility.predictDamage((Entity)this.target);
/*      */         
/*  402 */         String result = "Hit passed (" + String.format("%.1f", new Object[] { Float.valueOf(healthDiff) }) + "hp)";
/*  403 */         if (this.prevTargetHealth <= this.target.getHealth() + this.target.getAbsorptionAmount()) {
/*  404 */           result = "Hit missed (" + this.hitCounter + ")";
/*      */         }
/*      */         
/*  407 */         Chat.debug(result);
/*  408 */         this.logged = true;
/*      */       } 
/*      */       
/*  411 */       if (mc.player.isOnGround()) this.attacked = false;
/*      */       
/*  413 */       if (MathUtility.rayTraceWithBlock(6.0D, this.rotation.x, this.rotation.y, (Entity)mc.player, (Entity)this.target, false)) {
/*  414 */         this.aimTicks++;
/*      */       } else {
/*  416 */         this.aimTicks = 0;
/*      */       } 
/*      */       
/*  419 */       if (this.direct.get()) {
/*  420 */         double speed = Math.hypot(Math.abs(this.target.prevPosX - this.target.getPosX()), Math.abs(this.target.prevPosZ - this.target.getPosZ()));
/*      */ 
/*      */ 
/*      */         
/*  424 */         if (Player.collideWith(this.target)) {
/*  425 */           float p = mc.world.getBlockState(mc.player.getPosition().add((mc.player.getMotion()).x, (mc.player.getMotion()).y, (mc.player.getMotion()).z)).getBlock().getSlipperiness();
/*  426 */           float f = mc.player.isOnGround() ? (p * 1.0F) : (Server.is("infinity") ? 0.91F : 0.81F);
/*  427 */           float f2 = mc.player.isOnGround() ? p : 0.99F;
/*      */ 
/*      */ 
/*      */           
/*  431 */           mc.player.setVelocity(mc.player.getMotion().getX() / f * f2, mc.player.getMotion().getY(), mc.player.getMotion().getZ() / f * f2);
/*      */         }
/*  433 */         else if (mc.player.fallDistance <= 0.5F || Move.getSpeed() == 0.0D) {
/*      */ 
/*      */         
/*      */         }
/*      */       
/*      */       }
/*  439 */       else if (this.toTarget.get() && 
/*  440 */         Player.collideWith(this.target, this.multiplier.get())) {
/*      */         
/*  442 */         Vector3d playerPos = mc.player.getPositionVec();
/*  443 */         Vector3d targetPos = this.target.getPositionVec();
/*      */ 
/*      */         
/*  446 */         Vector3d direction = targetPos.subtract(playerPos).normalize();
/*      */ 
/*      */ 
/*      */         
/*  450 */         float p = mc.world.getBlockState(mc.player.getPosition().add((mc.player.getMotion()).x, (mc.player.getMotion()).y, (mc.player.getMotion()).z)).getBlock().getSlipperiness();
/*  451 */         float f = mc.player.isOnGround() ? (p * 1.0F) : (Server.is("infinity") ? 0.91F : 0.81F);
/*  452 */         float f2 = mc.player.isOnGround() ? p : 0.99F;
/*      */ 
/*      */         
/*  455 */         double motionY = (mc.player.getMotion()).y;
/*      */         
/*  457 */         float ground = this.spookyBoost.get() ? 0.05F : (this.funtimeBoost.get() ? 0.0F : this.groundSpeed.get());
/*  458 */         float falling = this.spookyBoost.get() ? 0.05F : (this.funtimeBoost.get() ? 0.3F : this.fallingSpeed.get());
/*  459 */         float jump = this.spookyBoost.get() ? 0.05F : (this.funtimeBoost.get() ? 0.2F : this.jumpSpeed.get());
/*      */ 
/*      */         
/*  462 */         float gradus = (float)(System.currentTimeMillis() / 100L);
/*  463 */         float centrifugal = (this.funtimeBoost.get() || this.spookyBoost.get()) ? 0.0F : this.centrifugalForce.get();
/*  464 */         float deviationX = (float)Math.cos(Math.toDegrees(gradus)) * centrifugal;
/*  465 */         float deviationZ = (float)Math.sin(Math.toDegrees(gradus)) * centrifugal;
/*  466 */         direction = direction.add(deviationX, 0.0D, deviationZ);
/*      */ 
/*      */         
/*  469 */         double speed = mc.player.isOnGround() ? ground : ((mc.player.fallDistance > 0.0F) ? falling : jump);
/*  470 */         double newX = direction.x * speed * f2 / f;
/*  471 */         double newZ = direction.z * speed * f2 / f;
/*      */ 
/*      */         
/*  474 */         mc.player.setVelocity(
/*  475 */             (mc.player.getMotion()).x + newX, motionY, 
/*      */ 
/*      */             
/*  478 */             (mc.player.getMotion()).z + newZ);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  484 */     if (event instanceof EventUpdate && (
/*  485 */       !this.mode.is(this.snap) || canAttack() || this.rotationTicks >= -10)) {
/*  486 */       tryAttack();
/*      */     }
/*      */     
/*  489 */     if (this.target != null) this.prevTarget = this.target; 
/*      */   }
/*      */   
/*      */   private void rotate(EventMotion e) {
/*  493 */     if (this.mode.is(this.snap) && !Bypass.via()) {
/*  494 */       if (canAttack()) {
/*  495 */         this.rotationTicks++;
/*  496 */         if (this.attacked) {
/*      */           return;
/*      */         }
/*      */       } else {
/*      */         return;
/*      */       } 
/*      */     }
/*  503 */     if (!mc.player.isElytraFlying() && !mc.player.isSwimming() && this.target != null && (!this.snap.get() || !Bypass.via())) {
/*  504 */       boolean rot360 = false;
/*      */       
/*  506 */       boolean realRotation = (!this.mode.is(this.spooky) || !this.mode.is(this.funtime));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  515 */       Vector2f rot = realRotation ? new Vector2f(this.rotation.x, this.rotation.y) : new Vector2f(this.fakeAnim.getYaw(), this.fakeAnim.getPitch());
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  520 */       mc.player.renderYawOffset = rot.x + (rot360 ? (-20.0F + 380.0F * mc.player.getCooledAttackStrength(0.0F)) : 0.0F);
/*  521 */       mc.player.rotationYawHead = rot.x + (rot360 ? (-20.0F + 380.0F * mc.player.getCooledAttackStrength(0.0F)) : 0.0F);
/*  522 */       mc.player.rotationPitchHead = rot.y;
/*      */     } 
/*      */ 
/*      */     
/*  526 */     if (this.snap.get() && Bypass.via() && this.attackTimer.passed(5L)) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  531 */     e.setYaw(this.rotation.x);
/*  532 */     e.setPitch(this.rotation.y);
/*      */ 
/*      */ 
/*      */     
/*  536 */     mc.player.renderYawOffset = this.rotation.z;
/*      */   }
/*      */   
/*      */   private void tryAttack() {
/*  540 */     if (this.mode.is(this.snap)) {
/*  541 */       if (Player.getBlock(0.0D, 2.0D, 0.0D) == Blocks.AIR) {
/*  542 */         this.rotationTicks--;
/*      */       } else {
/*  544 */         this.rotationTicks = 0;
/*      */       } 
/*      */     }
/*      */     
/*  548 */     if (!canAttack())
/*      */       return; 
/*  550 */     ((AutoSprint)rock.getModules().get(AutoSprint.class)).setCanSprint(true);
/*      */     
/*  552 */     if (!Server.isRW() || mc.player.getActiveHand() == Hand.OFF_HAND);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  557 */     if ((mc.player.isInWater() || Player.getBlock(0.0D, -0.20000000298023224D, 0.0D) == Blocks.WATER) && Player.getBlock(0.0D, 1.0D, 0.0D) == Blocks.AIR && (mc.getGameSettings()).keyBindJump.isKeyDown() && 
/*  558 */       mc.player.fallDistance == 0.0F) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  563 */     if (Bypass.via() && this.snap.get() && !((Criticals)rock.getModules().get(Criticals.class)).canCritical())
/*  564 */     { Bypass.send(this.rotation.x, this.rotation.y); }
/*      */     
/*  566 */     else if (this.air != null) { mc.player.connection.sendPacket((IPacket)new CPlayerPacket.RotationPacket(this.rotation.x, this.rotation.y, mc.player.isOnGround())); }
/*      */ 
/*      */     
/*  569 */     boolean blocking = (mc.player.isHandActive() && mc.player.getActiveItemStack().getItem().getUseAction(mc.player.getActiveItemStack()) == UseAction.BLOCK);
/*  570 */     if (blocking) mc.player.connection.sendPacket((IPacket)new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
/*      */     
/*  572 */     if ((mc.player.isSprinting() || mc.player.serverSprintState) && !mc.player.isInWater() && Player.getBlock(0.0D, -0.20000000298023224D, 0.0D) != Blocks.WATER && this.classicSprint.get()) {
/*  573 */       mc.player.connection.sendPacket((IPacket)new CEntityActionPacket((Entity)mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
/*  574 */       mc.player.serverSprintState = false;
/*  575 */       mc.player.setSprinting(false);
/*      */     } 
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
/*  596 */     this.attackTimer.reset();
/*      */     
/*  598 */     mc.playerController.attackEntity((PlayerEntity)mc.player, (Entity)this.target);
/*  599 */     mc.player.swingArm(Hand.MAIN_HAND);
/*  600 */     AuraUtility.tryBreakShield();
/*      */     
/*  602 */     if (blocking) mc.player.connection.sendPacket((IPacket)new CPlayerTryUseItemPacket(mc.player.getActiveHand()));
/*      */     
/*  604 */     this.attacked = true;
/*  605 */     this.firstHit = false;
/*      */     
/*  607 */     this.attacks++;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  612 */     if (Player.getBlock(0.0D, 2.0D, 0.0D) != Blocks.AIR) {
/*  613 */       this.hitsUnderBlocks++;
/*      */     } else {
/*  615 */       this.hitsUnderBlocks = 0;
/*      */     } 
/*      */ 
/*      */     
/*  619 */     this.logged = false;
/*  620 */     this.prevTargetHealth = this.target.getHealth() + this.target.getAbsorptionAmount();
/*  621 */     if (this.rotationTicks < 0 && this.mode.is(this.snap)) this.rotationTicks = -5; 
/*      */   }
/*      */   
/*      */   private void findTarget(float range) {
/*  625 */     LivingEntity target = AuraUtility.calculateTarget(mc.player.getPositionVec(), range, this.players.get(), this.mobs.get(), this.invisibles.get(), this.naked.get(), this.bots.get(), this.friends.get(), this.rockUser.get(), this.sort.is(this.fov), this.sort.is(this.distance), this.sort.is(this.health), false, false);
/*      */     
/*  627 */     if (target == null || this.target == null || !mc.world.getAllEntities().contains(this.target) || this.target.isDead() || !this.pres.get()) {
/*  628 */       this.target = target;
/*      */     }
/*      */   }
/*      */   
/*      */   public void focus(LivingEntity target) {
/*  633 */     if (AuraUtility.isValidTarget((Entity)target, this.players.get(), this.mobs.get(), this.invisibles.get(), this.naked.get(), this.bots.get(), this.friends.get(), this.rockUser.get())) {
/*  634 */       this.target = target;
/*      */     }
/*      */   }
/*      */   
/*      */   public void focus(float range) {
/*  639 */     LivingEntity target = AuraUtility.calculateTarget(mc.player.getPositionVec(), range, this.players.get(), this.mobs.get(), this.invisibles.get(), this.naked.get(), this.bots.get(), this.friends.get(), this.rockUser.get(), true, false, false, false, false);
/*  640 */     this.target = target;
/*      */   }
/*      */   
/*      */   private void correctMovement(Event event) {
/*  644 */     if (!(event instanceof EventJump) && !(event instanceof EventMove) && !(event instanceof EventInput))
/*      */       return; 
/*  646 */     if (this.mode.is(this.snap)) {
/*  647 */       if (!canAttack()) {
/*      */         return;
/*      */       }
/*      */       
/*  651 */       if (this.attacked || Bypass.via()) {
/*      */         return;
/*      */       }
/*      */     } 
/*      */     
/*  656 */     if (!this.correction.is(this.no)) {
/*  657 */       if (event instanceof EventMove) { EventMove e = (EventMove)event;
/*  658 */         e.setYaw(this.rotation.x);
/*  659 */         e.setPitch(this.rotation.y); }
/*      */ 
/*      */       
/*  662 */       if (event instanceof EventInput) { EventInput e = (EventInput)event; if (!Server.isFS() || !Server.isFT() || !mc.player.isInWater()) {
/*  663 */           e.setYaw(this.rotation.x, (this.correction.is(this.silent) || this.target == null) ? mc.player.rotationYaw : (Rotation.get(this.prevTarget.getPositionVec())).x);
/*      */         } }
/*      */       
/*  666 */       if (event instanceof EventJump) { EventJump e = (EventJump)event;
/*  667 */         e.setYaw(this.rotation.x); }
/*      */     
/*      */     } 
/*      */     
/*  671 */     if (event instanceof EventJump) { EventJump e = (EventJump)event;
/*      */       
/*  673 */       IdealHitUtility.setJumped(true); }
/*      */   
/*      */   }
/*      */   
/*      */   public void calculateRotation() {
/*  678 */     if (Float.isNaN(this.rotation.x)) this.rotation.x = 0.0F; 
/*  679 */     if (Float.isNaN(this.rotation.y)) this.rotation.y = 0.0F;
/*      */     
/*  681 */     if (this.target == null) {
/*      */       
/*  683 */       float f1 = mc.player.rotationYaw;
/*  684 */       float f2 = mc.player.rotationPitch;
/*      */       
/*  686 */       float f3 = Math.abs(this.rotAnim.getYaw() - f1);
/*      */       
/*  688 */       int i = (int)getAIRotationSpeed(f3);
/*  689 */       int j = MathUtility.randomInt(100, 110);
/*      */       
/*  691 */       this.rotAnim.animate(new Vector2f(f1, f2), i, j);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  697 */       Vector2f vector2f = Rotation.correctRotation(this.rotAnim
/*  698 */           .getYaw(), this.rotAnim
/*  699 */           .getPitch());
/*      */ 
/*      */ 
/*      */       
/*  703 */       this.rotation = new Vector3f(vector2f.x, vector2f.y, vector2f.x);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  720 */     boolean canCritical = (!mc.player.isOnGround() || mc.player.fallDistance > 0.0F || mc.player.isPotionActive(Effects.BLINDNESS) || mc.player.isPotionActive(Effects.LEVITATION) || mc.player.isPotionActive(Effects.SLOW_FALLING) || mc.player.isInLava() || mc.player.isInWater() || mc.player.isOnLadder() || mc.player.isPassenger() || Player.isInWeb() || mc.player.abilities.isFlying || ((Criticals)rock.getModules().get(Criticals.class)).canCritical() || !this.onlyCrits.get());
/*      */     
/*  722 */     boolean inWater = (!mc.player.isInWater() && Player.getBlock(0.0D, -0.5D, 0.0D) == Blocks.WATER && !mc.player.isSwimming() && Server.isFT());
/*      */     
/*  724 */     float range = range();
/*  725 */     boolean canAttack = (this.target != null && AuraUtility.distanceTo(AuraUtility.getPoint(this.target)) <= range && this.attackTimer.passed((long)this.speed.get()) && canCritical);
/*  726 */     boolean preCanAttack = (this.target != null && AuraUtility.distanceTo(AuraUtility.getPoint(this.target)) <= range && canCritical);
/*      */     
/*  728 */     ElytraTarget elytraTarget = (ElytraTarget)rock.getModules().get(ElytraTarget.class);
/*      */     
/*  730 */     boolean fastRot = (Server.is("infinity") || Server.isRW() || Server.isHW() || Server.is("hvh") || (elytraTarget.get() && this.prevTarget != null && this.prevTarget.isElytraFlying()));
/*  731 */     if (!this.prevTarget.getBacktrack().isEmpty())
/*  732 */       this.prevTarget.getBacktrack().sort(Comparator.comparingDouble(pos -> MathUtility.сквирт(mc.player.getDistanceSq(pos.getPos())))); 
/*  733 */     Vector3d pos = AuraUtility.getPoint(this.prevTarget).subtract(mc.player.getEyePosition(mc.getRenderPartialTicks())).normalize();
/*      */     
/*  735 */     float shortestYawPath = (float)(((Math.toDegrees(Math.atan2(pos.z, pos.x)) - 90.0D - this.rotation.x) % 360.0D + 540.0D) % 360.0D - 180.0D);
/*      */     
/*  737 */     if (this.swapDirectionYawTimer.passed(450L) && ((shortestYawPath > 0.0F)) != this.directionYaw) {
/*  738 */       this.directionYaw = (shortestYawPath > 0.0F);
/*  739 */       this.swapDirectionYawTimer.reset();
/*      */     } 
/*      */     
/*  742 */     float findPitch = (float)Math.min(90.0D, -Math.toDegrees(Math.atan2(pos.y, Math.hypot(pos.x, pos.z))));
/*      */     
/*  744 */     float targetYaw = (canAttack || !this.mode.is(this.funtime) || inWater) ? (this.rotation.x + shortestYawPath) : this.rotation.x;
/*  745 */     float targetPitch = (canAttack || !this.mode.is(this.funtime) || inWater) ? (!Server.isFT() ? findPitch : Math.min(findPitch + (inWater ? MathUtility.random(10.0D, 30.0D) : MathUtility.random(-2.0D, 2.0D)), 90.0F)) : this.rotation.y;
/*      */     
/*  747 */     if (!preCanAttack && this.mode.is(this.funtime) && MathUtility.rayTraceWithBlock(7.0D, this.rotation.x, this.rotation.y, (Entity)mc.player, (Entity)this.target, false) && this.target != null) {
/*  748 */       for (int i = 0; i < 18; i++) {
/*  749 */         targetYaw = this.rotation.x + shortestYawPath + (i * 10);
/*  750 */         if (!MathUtility.rayTraceWithBlock(7.0D, targetYaw, targetPitch, (Entity)mc.player, (Entity)this.target, false)) {
/*  751 */           targetYaw += 10.0F;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     }
/*  757 */     float yawDiff = Math.abs(this.rotAnim.getYaw() - targetYaw);
/*      */     
/*  759 */     int yawSpeed = (int)(fastRot ? 1.0F : getAIRotationSpeed(yawDiff));
/*  760 */     int pitchSpeed = fastRot ? 1 : MathUtility.randomInt(100, 150);
/*      */     
/*  762 */     targetPitch = MathHelper.clamp(targetPitch, -90.0F, 90.0F);
/*  763 */     this.rotAnim.animate(new Vector2f(targetYaw, targetPitch), yawSpeed, pitchSpeed);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  769 */     Vector2f correctedRotation = Rotation.correctRotation(this.rotAnim
/*  770 */         .getYaw(), this.rotAnim
/*  771 */         .getPitch());
/*      */ 
/*      */     
/*  774 */     this.fakeAnim.animate(new Vector2f(this.rotation.x + shortestYawPath + MathUtility.random(-5.0D, 5.0D), findPitch + MathUtility.random(-5.0D, 5.0D)), 
/*  775 */         this.mode.is(this.spooky) ? (yawSpeed / 3) : (yawSpeed * 4), pitchSpeed * 13);
/*      */ 
/*      */     
/*  778 */     if (!Float.isNaN(correctedRotation.x) && !Float.isNaN(correctedRotation.y))
/*      */     {
/*  780 */       this.rotation = new Vector3f(correctedRotation.x, correctedRotation.y, AuraUtility.calculateCorrectYawOffset(this.rotAnim.getYaw(), this.rotation.z));
/*      */     }
/*      */   }
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
/*      */   private float getAIRotationSpeed(float diff) {
/*  795 */     return MathHelper.clamp(diff, 
/*      */         
/*  797 */         MathUtility.randomInt(1, 50), 
/*  798 */         MathUtility.randomInt(150, 200));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean canAttack() {
/*  809 */     return ((canHit() && canCritical()) || canGroundAttack());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean canHit() {
/*  815 */     float range = range();
/*      */     
/*  817 */     if (Server.isFT()) {
/*  818 */       range = Math.min(range, 2.8F);
/*      */     }
/*      */     
/*  821 */     boolean result = (!this.raycast.get() || MathUtility.rayTraceWithBlock(range, this.rotation.x, this.rotation.y, (Entity)mc.player, (Entity)this.target, false));
/*      */ 
/*      */     
/*  824 */     if (this.notEat.get() && (mc.player.getHeldItemOffhand().getUseAction() == UseAction.EAT || mc.player.getHeldItemMainhand().getUseAction() == UseAction.EAT) && mc.player.getActiveHand() == Hand.OFF_HAND && mc.player.isHandActive()) {
/*  825 */       return false;
/*      */     }
/*  827 */     if (Player.getBlock(0.0D, 2.0D, 0.0D) != Blocks.AIR && !(mc.getGameSettings()).keyBindSneak.isPressed() && mc.player.fallDistance > 0.0F) {
/*  828 */       this.requireCritical = true;
/*      */     }
/*  830 */     if (Player.getBlock(0.0D, 2.0D, 0.0D) == Blocks.AIR || !(mc.getGameSettings()).keyBindJump.isKeyDown()) this.requireCritical = false;
/*      */ 
/*      */     
/*  833 */     boolean criticals = ((Criticals)rock.getModules().get(Criticals.class)).canCritical();
/*      */     
/*  835 */     boolean rangeCheck = (AuraUtility.distanceTo(AuraUtility.getPoint(this.prevTarget)) <= range && mc.player.getDistance((Entity)this.prevTarget) <= 6.0F);
/*      */     
/*  837 */     ElytraTarget elytra = (ElytraTarget)rock.getModules().get(ElytraTarget.class);
/*      */     
/*  839 */     if (elytra.get()) {
/*  840 */       rangeCheck = (AuraUtility.distanceTo(AuraUtility.getPoint(this.prevTarget)) <= elytra.overrideRange(this.prevTarget));
/*      */     }
/*      */     
/*  843 */     return (this.target != null && rangeCheck && mc.player
/*  844 */       .getCooledAttackStrength() >= IdealHitUtility.getAICooldown() && (Server.isHW() ? this.attackTimer.passed(500L) : this.attackTimer.passed(370L)) && result && (
/*  845 */       !this.tickSelect.get() || this.target.lastHit.passed(500L)) && ((ElytraTarget)rock.getModules().get(ElytraTarget.class)).canAttack(this.prevTarget));
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean canGroundAttack() {
/*  850 */     float range = range();
/*      */     
/*  852 */     if (Server.isFT()) {
/*  853 */       range = Math.min(range, 2.8F);
/*      */     }
/*      */     
/*  856 */     boolean result = (!this.raycast.get() || MathUtility.rayTraceWithBlock(range, this.rotation.x, this.rotation.y, (Entity)mc.player, (Entity)this.target, false));
/*  857 */     if (this.notEat.get() && (mc.player.getHeldItemOffhand().getUseAction() == UseAction.EAT || mc.player.getHeldItemMainhand().getUseAction() == UseAction.EAT) && mc.player.getActiveHand() == Hand.OFF_HAND && mc.player.isHandActive()) return false;
/*      */     
/*  859 */     boolean rangeCheck = (AuraUtility.distanceTo(AuraUtility.getPoint(this.prevTarget)) <= range && mc.player.getDistance((Entity)this.prevTarget) <= 6.0F);
/*      */     
/*  861 */     ElytraTarget elytra = (ElytraTarget)rock.getModules().get(ElytraTarget.class);
/*      */     
/*  863 */     if (elytra.get()) {
/*  864 */       rangeCheck = (AuraUtility.distanceTo(AuraUtility.getPoint(this.prevTarget)) <= elytra.overrideRange(this.prevTarget));
/*      */     }
/*      */     
/*  867 */     return (this.attackOnGround.get() && !mc.gameSettings.keyBindJump.isKeyDown() && mc.player.isOnGround() && this.target.lastHit
/*  868 */       .passed(500L) && this.attackTimer.passed(500L) && mc.player.getCooledAttackStrength() >= IdealHitUtility.getAICooldown() && this.target != null && rangeCheck && result);
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean canCritical() {
/*  873 */     if (this.airCrits.get()) {
/*  874 */       return (mc.player.fallDistance > 0.0F);
/*      */     }
/*  876 */     if (this.requireCritical && mc.player.isOnGround()) {
/*  877 */       this.requireCritical = false;
/*  878 */       if (this.hitsUnderBlocks > 2) {
/*  879 */         return true;
/*      */       }
/*      */     } 
/*  882 */     double yDiff = (int)mc.player.getPosY() - mc.player.getPosY();
/*  883 */     boolean bl4 = (yDiff == -0.01250004768371582D);
/*  884 */     boolean bl5 = (yDiff == -0.1875D);
/*      */     
/*  886 */     return ((!mc.player.isOnGround() && mc.player.fallDistance > IdealHitUtility.getAIFallDistance() && IdealHitUtility.canAIFall()) || ((bl5 || bl4) && 
/*      */ 
/*      */       
/*  889 */       !mc.player.isSneaking()) || mc.player
/*  890 */       .isPotionActive(Effects.BLINDNESS) || mc.player
/*  891 */       .isPotionActive(Effects.LEVITATION) || mc.player
/*  892 */       .isPotionActive(Effects.SLOW_FALLING) || mc.player
/*  893 */       .isInLava() || mc.player
/*  894 */       .isInWater() || mc.player
/*  895 */       .isOnLadder() || mc.player
/*  896 */       .isPassenger() || 
/*  897 */       Player.isInWeb() || mc.player.abilities.isFlying || ((Criticals)rock
/*      */       
/*  899 */       .getModules().get(Criticals.class)).canCritical() || 
/*  900 */       !this.onlyCrits.get());
/*      */   }
/*      */   
/*      */   private float getAdditionalRange() {
/*  904 */     ElytraTarget elytraTarget = (ElytraTarget)rock.getModules().get(ElytraTarget.class);
/*      */     
/*  906 */     if (elytraTarget.get()) {
/*  907 */       return elytraTarget.getRange();
/*      */     }
/*  909 */     return this.rangaTip.get();
/*      */   }
/*      */   
/*      */   private void resetRotation() {
/*  913 */     this.rotation = new Vector3f(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.renderYawOffset);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  919 */     this.rotAnim.animate(new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch), 1, 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private float range() {
/*  931 */     return (this.airCrits.get() && !this.firstHit) ? 6.0F : this.range.get();
/*      */   }
/*      */   
/*      */   private void handlePackets(Event event) {
/*  935 */     if (!this.airCrits.get())
/*      */       return; 
/*  937 */     if (event instanceof EventUpdate) {
/*  938 */       if (mc.player.isOnGround()) this.disabled = false; 
/*  939 */       if (this.air == null && mc.player.isOnGround()) {
/*  940 */         (mc.player.getMotion()).y = 0.41999998688697815D;
/*      */       }
/*      */     } 
/*      */     
/*  944 */     if (this.disabled)
/*      */       return; 
/*  946 */     if (mc.player.fallDistance > 0.08F && this.air == null && !this.disabled) {
/*  947 */       this.air = new double[] { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D };
/*  948 */       Vector3d pos = mc.player.getPositionVec();
/*  949 */       Vector3d motion = mc.player.getMotion();
/*      */       
/*  951 */       this.air[0] = pos.x;
/*  952 */       this.air[1] = pos.y;
/*  953 */       this.air[2] = pos.z;
/*  954 */       this.air[3] = mc.player.rotationYaw;
/*  955 */       this.air[4] = mc.player.rotationPitch;
/*  956 */       this.air[5] = mc.player.abilities.getFlySpeed();
/*  957 */       this.air[6] = mc.player.abilities.isFlying ? 1.0D : 0.0D;
/*  958 */       this.air[7] = motion.x;
/*  959 */       this.air[8] = motion.y;
/*  960 */       this.air[9] = motion.z;
/*  961 */       this.air[10] = mc.player.isOnGround() ? 1.0D : 0.0D;
/*      */     } 
/*      */     
/*  964 */     if (this.air != null) {
/*  965 */       mc.player.setPosition(this.air[0], this.air[1], this.air[2]);
/*  966 */       mc.player.setMotion(0.0D, 0.0D, 0.0D);
/*      */       
/*  968 */       if (this.target == null || mc.player.getDistance((Entity)this.target) > 6.0F) {
/*  969 */         mc.player.abilities.isFlying = (this.air[6] == 1.0D);
/*  970 */         mc.player.abilities.setFlySpeed((float)this.air[5]);
/*  971 */         mc.player.setPosition(this.air[0], this.air[1], this.air[2]);
/*  972 */         mc.player.setMotion(0.0D, 0.0D, 0.0D);
/*  973 */         mc.player.setOnGround((this.air[10] == 1.0D));
/*  974 */         this.firstHit = true;
/*  975 */         this.disabled = true;
/*  976 */         this.air = null;
/*      */       } 
/*      */       
/*  979 */       if (event instanceof EventUpdate) {
/*  980 */         this.air[11] = this.air[11] + 1.0D;
/*  981 */         if (this.air[11] > 40.0D && Server.isFT()) {
/*  982 */           this.disabled = true;
/*  983 */           this.air = null;
/*  984 */           this.firstHit = true;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  989 */     if (event instanceof EventSendPacket && this.airCrits.get() && this.air != null) {
/*  990 */       IPacket packet = ((EventSendPacket)event).getPacket();
/*      */       
/*  992 */       if (packet instanceof CPlayerPacket || packet instanceof CPlayerPacket.PositionRotationPacket || packet instanceof CPlayerPacket.PositionPacket || packet instanceof CPlayerPacket.RotationPacket)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  998 */         event.cancel();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void onEnable() {
/* 1005 */     resetRotation();
/* 1006 */     this.firstHit = true;
/*      */   }
/*      */ 
/*      */   
/*      */   public void onDisable() {
/* 1011 */     if (this.air != null) {
/* 1012 */       mc.player.abilities.isFlying = (this.air[6] == 1.0D);
/* 1013 */       mc.player.abilities.setFlySpeed((float)this.air[5]);
/* 1014 */       mc.player.setPositionAndRotation(this.air[0], this.air[1], this.air[2], (float)this.air[3], (float)this.air[4]);
/* 1015 */       mc.player.setMotion(Vector3d.ZERO);
/*      */       
/* 1017 */       mc.player.setOnGround((this.air[10] == 1.0D));
/*      */     } 
/* 1019 */     this.air = null;
/* 1020 */     this.target = null;
/* 1021 */     ((AutoSprint)rock.getModules().get(AutoSprint.class)).setCanSprint(true);
/* 1022 */     this.disableTimer.reset();
/*      */   }
/*      */   
/*      */   public boolean predictAttack() {
/* 1026 */     return (this.target != null && canHit() && !mc.player.isOnGround() && IdealHitUtility.canAIFall() && (FallingPlayer.fromPlayer(mc.player).findFall(IdealHitUtility.getAIFallDistance(), 1) || canCritical()));
/*      */   }
/*      */   
/*      */   private void updateAdvanced() {
/* 1030 */     if (this.prevTarget != null && this.target != null) {
/* 1031 */       this.rotation.x = mc.player.rotationYawHead;
/* 1032 */       this.rotation.y = mc.player.rotationPitchHead;
/* 1033 */       this.rotation.z = mc.player.rotationYawHead;
/*      */       
/* 1035 */       this.anim.setForward((Math.abs(mc.player.getPosYEye() - this.target.getPosY()) < 1.9800000190734863D));
/* 1036 */       this.anim1.setForward(MathUtility.rayTraceWithBlock(this.range.get(), mc.player.rotationYawHead, mc.player.rotationPitchHead, (Entity)mc.player, (Entity)this.target, false));
/*      */       
/* 1038 */       this.anim1.setSpeed(250);
/* 1039 */       this.anim3.setForward((AuraUtility.getAngle((Entity)this.target) > 40.0F && 
/* 1040 */           AuraUtility.getAngle((Entity)this.target) < 60.0F));
/* 1041 */       this.anim4.setForward((AuraUtility.getAngle((Entity)this.target) > 90.0F));
/*      */ 
/*      */ 
/*      */       
/* 1045 */       Vector3d vec = VectorUtility.getBestVector(this.target, MathUtility.randomNew(-60.0D, 60.0D) / 180.0F / 15.0F);
/*      */       
/* 1047 */       float shortestYawPath = (float)(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0D - this.rotation.x + 540.0D - 180.0D);
/* 1048 */       float yawToTarget = this.rotation.x + shortestYawPath;
/* 1049 */       float pitchToTarget = (float)-Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.z, vec.x)));
/*      */       
/* 1051 */       float yawDelta = MathHelper.wrapDegrees(yawToTarget - this.rotation.x);
/* 1052 */       float pitchDelta = MathHelper.wrapDegrees(pitchToTarget - this.rotation.y);
/*      */       
/* 1054 */       yawDelta = AuraUtility.GENIUSCLAMPERR$$$(yawDelta, true) * MathUtility.randomNew(0.8D, 0.9D);
/* 1055 */       pitchDelta = AuraUtility.GENIUSCLAMPERR$$$(pitchDelta, false);
/* 1056 */       yawDelta = (AuraUtility.fixDeltaNonVanillaMouse(yawDelta, pitchDelta)).x;
/* 1057 */       pitchDelta = (AuraUtility.fixDeltaNonVanillaMouse(yawDelta, pitchDelta)).y;
/*      */       
/* 1059 */       pitchDelta /= 2.5F;
/* 1060 */       pitchDelta *= (float)(0.12999999523162842D + 0.17D * this.anim1.get() + (0.6F * this.anim.get())) / 1000.0F * MathUtility.randomNew(840.0D, 960.0D);
/*      */       
/* 1062 */       float recDelta = Math.min(Math.abs(yawDelta), 90.0F);
/* 1063 */       yawDelta = (yawDelta > 0.0F) ? recDelta : -recDelta;
/*      */       
/* 1065 */       yawDelta *= 1.0F - this.anim3.get() / 3.0F;
/* 1066 */       yawDelta *= (float)(1.0D - 0.15D * this.anim4.get());
/* 1067 */       pitchDelta *= (float)(1.0D + 0.2D * this.anim4.get());
/* 1068 */       yawDelta *= 1.2F + this.spookySpeed.get();
/*      */       
/* 1070 */       float yaw = this.rotation.x + yawDelta;
/* 1071 */       float pitch = this.rotation.y + pitchDelta;
/* 1072 */       pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
/*      */       
/* 1074 */       this.spookyAnim.easing(Easing.BOTH_CIRC).animate(new Vector2f(yaw, pitch), 
/* 1075 */           (int)this.animSpeed.get(), 
/* 1076 */           (int)((int)this.animSpeed.get() * 1.5D));
/*      */       
/* 1078 */       this.rotAnimAdv.easing(Easing.LINEAR).animate(new Vector2f(this.spookyAnim.getYaw(), this.spookyAnim.getPitch()), 30 - 
/* 1079 */           (int)this.animSpeed.get() * 3, 30 - 
/* 1080 */           (int)((int)this.animSpeed.get() * 1.5D) * 3);
/*      */       
/* 1082 */       Vector2f correctedRotation = Rotation.correctRotation(this.rotAnimAdv.getYaw(), this.rotAnimAdv.getPitch());
/* 1083 */       yaw = correctedRotation.x;
/* 1084 */       pitch = correctedRotation.y;
/*      */       
/* 1086 */       this.rotation = new Vector3f(yaw, pitch, AuraUtility.calculateCorrectYawOffset(yaw, this.rotation.z));
/*      */     } else {
/* 1088 */       resetRotation();
/*      */     } 
/*      */   }
/*      */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\combat\AuraOld.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */