/*     */ package fun.rockstarity.client.modules.other;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*     */ import fun.rockstarity.api.helpers.game.Server;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.CheckBox;
/*     */ import fun.rockstarity.api.modules.settings.list.Input;
/*     */ import fun.rockstarity.api.modules.settings.list.Mode;
/*     */ import fun.rockstarity.api.modules.settings.list.Slider;
/*     */ import fun.rockstarity.api.render.ui.alerts.AlertType;
/*     */ import java.util.List;
/*     */ import net.minecraft.client.util.ITooltipFlag;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.inventory.container.ClickType;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CClickWindowPacket;
/*     */ import net.minecraft.network.play.client.CCloseWindowPacket;
/*     */ import net.minecraft.network.play.server.SOpenWindowPacket;
/*     */ import net.minecraft.network.play.server.SPlaySoundEffectPacket;
/*     */ import net.minecraft.network.play.server.SSetSlotPacket;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Info(name = "AutoLeave", desc = "Выполняет действие когда рядом сущность", type = Category.OTHER)
/*     */ public class AutoLeave
/*     */   extends Module
/*     */ {
/*  41 */   private final Slider distance = (new Slider((Bindable)this, "Дистанция")).min(10.0F).max(100.0F).set(20.0F).inc(5.0F);
/*     */   
/*  43 */   private final Mode mode = new Mode((Bindable)this, "Что делать");
/*     */   
/*  45 */   private final Mode.Element svo = new Mode.Element(this.mode, "Своя команда");
/*  46 */   private final Input inputSVO = (new Input((Bindable)this, "Команда")).hide(() -> Boolean.valueOf(!this.mode.is(this.svo)));
/*     */   
/*  48 */   private final Mode.Element hub = new Mode.Element(this.mode, "Ливать в хаб");
/*  49 */   private final Mode.Element leave = new Mode.Element(this.mode, "Ливать с сервера");
/*  50 */   private final Mode.Element spawn = new Mode.Element(this.mode, "Ливать на спавн");
/*  51 */   private final Mode.Element darena = new Mode.Element(this.mode, "Ливать на darena");
/*     */   
/*  53 */   private final CheckBox predict = (new CheckBox((Bindable)this, "Заранее")).desc("Делает действие заранее");
/*     */   
/*  55 */   private final Slider slider = (new Slider((Bindable)this, "Время до действия")).max(7.0F).inc(1.0F).min(1.0F).set(7.0F).hide(() -> Boolean.valueOf(!this.predict.get()));
/*     */   
/*     */   private boolean shouldLeave;
/*     */   boolean darenaopenet = false;
/*     */   
/*     */   public void onEvent(Event event) {
/*  61 */     if (event instanceof fun.rockstarity.api.events.list.player.EventUpdate && this.shouldLeave) {
/*  62 */       if (this.predict.get()) {
/*  63 */         if (Server.getTimeCT() == this.slider.get()) {
/*  64 */           leave();
/*  65 */           this.shouldLeave = false;
/*  66 */           toggle();
/*     */         } 
/*     */       } else {
/*  69 */         for (PlayerEntity e : mc.world.getPlayers()) {
/*  70 */           if (e == null || rock.getFriendsHandler().isFriend(e.getName().getString()) || e == mc.player || e.getDistance((Entity)e) > this.distance.get() || Server.hasCT())
/*     */             continue; 
/*  72 */           leave();
/*  73 */           this.shouldLeave = false;
/*  74 */           rock.getAlertHandler().alert("Рядом обнаружен " + e.getName().getString() + ", ливаю", AlertType.SUCCESS);
/*  75 */           toggle();
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*  80 */     if (this.darenaopenet && event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event;
/*     */       
/*  82 */       IPacket iPacket = e.getPacket(); if (iPacket instanceof SOpenWindowPacket) { SOpenWindowPacket packet = (SOpenWindowPacket)iPacket;
/*  83 */         if (packet.getTitle().getString().contains("Арена смерти")) {
/*  84 */           e.cancel();
/*  85 */           (mc.getGameSettings()).keyBindSneak.setPressed(true);
/*     */         }  }
/*     */ 
/*     */       
/*  89 */       iPacket = e.getPacket(); if (iPacket instanceof SPlaySoundEffectPacket) { SPlaySoundEffectPacket packet = (SPlaySoundEffectPacket)iPacket;
/*  90 */         if (packet.getSound().getName().toString().contains("glass.place") || packet.getSound().getName().toString().contains("note_block.xylophone")) {
/*  91 */           e.cancel();
/*     */         } }
/*     */ 
/*     */       
/*  95 */       iPacket = e.getPacket(); if (iPacket instanceof SSetSlotPacket) { SSetSlotPacket packet = (SSetSlotPacket)iPacket;
/*  96 */         ItemStack stack = packet.getStack();
/*     */         
/*  98 */         if (stack.getDisplayName().getString().contains("На Смотровую")) {
/*  99 */           (mc.getGameSettings()).keyBindSneak.setPressed(false);
/*     */           
/* 101 */           boolean click = true;
/*     */           
/* 103 */           List<ITextComponent> itemTooltip = stack.getTooltip(null, (ITooltipFlag)ITooltipFlag.TooltipFlags.NORMAL);
/* 104 */           for (ITextComponent str : itemTooltip) {
/* 105 */             if (str.getString().contains("Стоп")) {
/* 106 */               mc.player.connection.sendPacket((IPacket)new CCloseWindowPacket(packet.getWindowId()));
/* 107 */               click = false;
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/* 112 */           if (click)
/* 113 */             mc.player.connection.sendPacket((IPacket)new CClickWindowPacket(packet.getWindowId(), 24, 0, ClickType.PICKUP, mc.player.openContainer
/*     */                   
/* 115 */                   .getSlot(24).getStack(), mc.player.openContainer
/* 116 */                   .getNextTransactionID(mc.player.inventory))); 
/*     */         }  }
/*     */        }
/*     */   
/*     */   }
/*     */   @NativeInclude
/*     */   protected void leave() {
/* 123 */     if (this.mode.is(this.svo)) {
/* 124 */       if (!this.inputSVO.get().isEmpty()) {
/* 125 */         mc.player.sendChatMessage(this.inputSVO.get());
/* 126 */         rock.getAlertHandler().alert("Используем свою команду: " + this.inputSVO.get(), AlertType.SUCCESS);
/*     */       } else {
/* 128 */         rock.getAlertHandler().alert("Ошибка: Команда не указана!", AlertType.ERROR);
/*     */       } 
/* 130 */     } else if (this.mode.is(this.hub)) {
/* 131 */       mc.player.sendChatMessage("/hub");
/* 132 */       rock.getAlertHandler().alert("Телепортируемся в хаб!", AlertType.SUCCESS);
/* 133 */     } else if (this.mode.is(this.leave)) {
/* 134 */       mc.world.sendQuittingDisconnectingPacket();
/* 135 */     } else if (this.mode.is(this.spawn)) {
/* 136 */       mc.player.sendChatMessage("/spawn");
/* 137 */       rock.getAlertHandler().alert("Телепортируемся на спавн!", AlertType.SUCCESS);
/* 138 */     } else if (this.mode.is(this.darena)) {
/* 139 */       this.darenaopenet = true;
/* 140 */       mc.player.sendChatMessage("/darena");
/* 141 */       rock.getAlertHandler().alert("Телепортируемся на darena!", AlertType.SUCCESS);
/*     */     } 
/*     */   }
/*     */   
/*     */   @NativeInclude
/*     */   public void onEnable() {
/* 147 */     this.shouldLeave = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 152 */     this.shouldLeave = false;
/*     */   }
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\AutoLeave.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */