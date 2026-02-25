/*     */ package fun.rockstarity.client.modules.other;
/*     */ 
/*     */ import fun.rockstarity.api.binds.Bindable;
/*     */ import fun.rockstarity.api.events.Event;
/*     */ import fun.rockstarity.api.events.EventType;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventReceivePacket;
/*     */ import fun.rockstarity.api.events.list.game.packet.EventSendPacket;
/*     */ import fun.rockstarity.api.events.list.player.EventUpdate;
/*     */ import fun.rockstarity.api.helpers.math.TimerUtility;
/*     */ import fun.rockstarity.api.modules.Category;
/*     */ import fun.rockstarity.api.modules.Info;
/*     */ import fun.rockstarity.api.modules.Module;
/*     */ import fun.rockstarity.api.modules.settings.list.Select;
/*     */ import java.util.Random;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import net.minecraft.client.gui.NewChatGui;
/*     */ import net.minecraft.network.IPacket;
/*     */ import net.minecraft.network.play.client.CChatMessagePacket;
/*     */ import net.minecraft.network.play.server.SChatPacket;
/*     */ import net.minecraft.util.text.ChatType;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import net.minecraft.util.text.StringTextComponent;
/*     */ import net.minecraft.util.text.TextFormatting;
/*     */ import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
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
/*     */ @Info(name = "ChatUtils", desc = "Утилиты для чата", type = Category.OTHER, module = {"Hider"})
/*     */ public class ChatUtils
/*     */   extends Module
/*     */ {
/*  43 */   private final Select delete = (new Select((Bindable)this, "Убрать...")).desc("Убирает какой-то элемент из чата");
/*  44 */   private final Select.Element delAd = new Select.Element(this.delete, "Реклама");
/*  45 */   private final Select.Element spam = new Select.Element(this.delete, "Спам");
/*  46 */   private final Select.Element fon = new Select.Element(this.delete, "Фон"); public Select.Element getFon() { return this.fon; }
/*     */ 
/*     */   
/*  49 */   private final Select utils = (new Select((Bindable)this, "Утилиты")).desc("Утилиты для использования чата");
/*  50 */   private final Select.Element autoMe = new Select.Element(this.utils, "Авто \"Мне\"");
/*  51 */   private final Select.Element casino = new Select.Element(this.utils, "Казино");
/*  52 */   private final Select.Element ahme = new Select.Element(this.utils, "/ah me");
/*  53 */   private final Select.Element kk = new Select.Element(this.utils, "Сокращать тысячи");
/*  54 */   private final Select.Element russia = new Select.Element(this.utils, "Русификация");
/*  55 */   private final Select.Element full = new Select.Element(this.utils, "/pay full");
/*  56 */   private final Select.Element sosal = new Select.Element(this.utils, "Байтить на \"сосал\"");
/*  57 */   private final Select.Element chatHistory = new Select.Element(this.utils, "Сохранять историю"); public Select.Element getChatHistory() { return this.chatHistory; }
/*     */ 
/*     */   
/*  60 */   private final Select highlight = (new Select((Bindable)this, "Подсвечивать...")).desc("Элементы которые будут подсвечиваться в чате");
/*  61 */   private final Select.Element friendsMessages = new Select.Element(this.highlight, "Сообщения друзей"); public Select.Element getFriendsMessages() { return this.friendsMessages; }
/*     */   
/*  63 */   private final Select.Element spec = new Select.Element(this.highlight, "!Спек"); public Select.Element getSpec() { return this.spec; }
/*     */   
/*  65 */   Select settings = (new Select((Bindable)this.spec, "Настройки")).desc("Выберите дополнительные настройки");
/*  66 */   Select.Element nearPlayer = new Select.Element(this.settings, "Игрок рядом"); public Select.Element getNearPlayer() { return this.nearPlayer; }
/*  67 */    private final Select.Element selfMention = new Select.Element(this.highlight, "Упоминание себя"); public Select.Element getSelfMention() { return this.selfMention; }
/*     */   
/*  69 */   final Select.Element portalGod = new Select.Element(this.utils, "Игнорировать портал"); public Select.Element getPortalGod() { return this.portalGod; }
/*     */ 
/*     */   
/*  72 */   private final Pattern LINK_PATTERN = Pattern.compile(".*(\\.su|\\.ru|\\.com|переходите|\\.space|\\.net|\\.club|\\.org|\\.xyz|link|\\.fun|\\.play).*", 2);
/*  73 */   private final Pattern KEYWORD_PATTERN = Pattern.compile(".*(бан|мут|кик|в жопу|в попу|пизды|в рот|в мут|сосать дать|докс|сват|ратку|стилер|вирус|ротик|Рот|Ротик).*", 2);
/*  74 */   private final String[] word = new String[] { "!Скинь мне монеты, и я удвою их", "!Скинь мне монеты, чтобы участвовать в лотерее! Один счастливчик получит главный приз - супер редкий предмет или крупную сумму монет!" };
/*  75 */   private final String[] sosalBait = new String[] { "незерку", "донат" };
/*  76 */   private final TimerUtility timerBait = new TimerUtility();
/*  77 */   private final TimerUtility timer = new TimerUtility();
/*  78 */   private final TimerUtility timerSosal = new TimerUtility();
/*  79 */   private final Pattern COINS_PATTERN = Pattern.compile("(\\w+) получено от игрока (\\w+)");
/*  80 */   private final Random random = new Random(); private String lastMessage; private int amount; private int line;
/*  81 */   final Pattern TIME_PATTERN = Pattern.compile("До следующего ивента: (\\w+)"); private boolean cancel;
/*     */   private boolean active;
/*     */   
/*     */   public boolean isCancel() {
/*  85 */     return this.cancel; } public void setCancel(boolean cancel) { this.cancel = cancel; }
/*     */ 
/*     */   
/*  88 */   private String targetPlayer = null;
/*     */   
/*     */   private boolean startsWith(CChatMessagePacket packet, String startsOne, String startsTwo) {
/*  91 */     String message = packet.getMessage();
/*  92 */     return (message.startsWith(startsOne) || message.startsWith(startsTwo));
/*     */   }
/*     */ 
/*     */   
/*     */   @EventType({EventUpdate.class, EventSendPacket.class, EventReceivePacket.class})
/*     */   public void onEvent(Event event) {
/*  98 */     int balance = 0;
/*     */ 
/*     */     
/* 101 */     if (event instanceof EventUpdate) {
/* 102 */       if (this.casino.get() && this.timer.passed(10000L)) {
/* 103 */         mc.player.sendChatMessage(this.word[this.random.nextInt(this.word.length)]);
/* 104 */         this.timer.reset();
/*     */       } 
/*     */       
/* 107 */       if (this.sosal.get() && 
/* 108 */         this.timerBait.passed(15000L)) {
/* 109 */         mc.player.sendChatMessage("Кто хочет " + this.sosalBait[this.random.nextInt(this.word.length)] + " ?");
/* 110 */         if (this.timerSosal.passed(1050L)) {
/* 111 */           mc.player.sendChatMessage("Кто сосал ?");
/* 112 */           this.timerSosal.reset();
/*     */         } 
/* 114 */         this.timerBait.reset();
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 120 */     if (event instanceof EventSendPacket) { EventSendPacket e = (EventSendPacket)event; IPacket iPacket = e.getPacket(); if (iPacket instanceof CChatMessagePacket) { CChatMessagePacket packet = (CChatMessagePacket)iPacket;
/* 121 */         String message = packet.getMessage();
/*     */         
/* 123 */         if (this.ahme.get() && startsWith(packet, "/ah me", "/ah im")) {
/* 124 */           mc.player.sendChatMessage("/ah " + mc.player.getNameClear());
/* 125 */           e.cancel();
/*     */         } 
/*     */         
/* 128 */         if (this.russia.get()) {
/* 129 */           if (message.startsWith("/рги")) {
/* 130 */             mc.player.sendChatMessage("/hub");
/* 131 */           } else if (message.startsWith("/кез")) {
/* 132 */             mc.player.sendChatMessage("/rtp");
/*     */           } 
/*     */         }
/*     */         
/* 136 */         if (this.full.get() && message.startsWith("/pay")) {
/* 137 */           String[] parts = message.split(" ");
/*     */           
/* 139 */           if (parts.length >= 3 && parts[2].equalsIgnoreCase("full")) {
/* 140 */             String targetPlayer = parts[1];
/* 141 */             mc.player.sendChatMessage("/money");
/* 142 */             e.cancel();
/* 143 */             this.targetPlayer = targetPlayer;
/*     */           } 
/*     */         } 
/*     */         
/* 147 */         if (this.kk.get() && message.startsWith("/ah sell")) {
/* 148 */           String[] parts = message.split(" ");
/*     */           
/* 150 */           if (parts.length > 2) {
/* 151 */             String amountStr = parts[2];
/* 152 */             int amount = parse(amountStr);
/*     */             
/* 154 */             if (amount > 0) {
/* 155 */               packet.setMessage("/ah sell " + amount);
/*     */             }
/*     */           } 
/*     */         }  }
/*     */        }
/*     */ 
/*     */     
/* 162 */     if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event; IPacket iPacket = e.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket;
/* 163 */         String message = packet.getChatComponent().getString();
/* 164 */         if (message.contains("❤ Игрок") || message.contains("❤ Игрок")); }
/*     */        }
/*     */ 
/*     */ 
/*     */     
/* 169 */     if (event instanceof EventReceivePacket) { EventReceivePacket e = (EventReceivePacket)event; IPacket iPacket = e.getPacket(); if (iPacket instanceof SChatPacket) { SChatPacket packet = (SChatPacket)iPacket;
/* 170 */         String message = packet.getChatComponent().getString().toLowerCase();
/* 171 */         String cleanMessage = message.replaceAll("§[0-9a-fk-or]", "");
/*     */         
/* 173 */         if (message.contains("[$] ваш баланс: ")) {
/* 174 */           String balanceStr = cleanMessage.replaceAll("[^0-9.,]", "");
/*     */           
/* 176 */           if (balanceStr.contains(".")) {
/* 177 */             balanceStr = balanceStr.split("\\.")[0];
/*     */           }
/* 179 */           balanceStr = balanceStr.replace(",", "");
/*     */           
/* 181 */           int bal = Integer.parseInt(balanceStr);
/*     */           
/* 183 */           if (bal > 0 && this.targetPlayer != null) {
/* 184 */             mc.player.sendChatMessage("/pay " + this.targetPlayer + " " + bal);
/* 185 */             this.targetPlayer = null;
/*     */           } 
/*     */         } 
/*     */         
/* 189 */         if (this.spam.get() && packet.getType() == ChatType.CHAT) {
/* 190 */           ITextComponent messages = packet.getChatComponent();
/* 191 */           String rawMessage = messages.getString();
/* 192 */           NewChatGui chatGui = mc.ingameGUI.getChatGUI();
/* 193 */           Matcher matcher = this.TIME_PATTERN.matcher(message);
/*     */ 
/*     */ 
/*     */           
/* 197 */           if (this.lastMessage != null && this.lastMessage.equals(rawMessage)) {
/* 198 */             this.amount++;
/* 199 */             chatGui.deleteChatLine(this.line);
/* 200 */             messages.getSiblings().add(new StringTextComponent(String.valueOf(TextFormatting.GRAY) + " [x" + String.valueOf(TextFormatting.GRAY) + "]"));
/*     */           } else {
/* 202 */             this.amount = 1;
/*     */           } 
/* 204 */           this.line++;
/* 205 */           this.lastMessage = rawMessage;
/* 206 */           chatGui.printChatMessageWithOptionalDeletion(messages, this.line);
/* 207 */           if (this.line > 256) this.line = 0; 
/* 208 */           event.cancel();
/*     */         } 
/*     */         
/* 211 */         if (this.delAd.get() && this.LINK_PATTERN.matcher(message).matches()) {
/* 212 */           e.cancel();
/*     */         }
/*     */         
/* 215 */         if (this.autoMe.get() && !this.KEYWORD_PATTERN.matcher(message).matches() && message.contains("кому")) {
/* 216 */           mc.player.sendChatMessage("!мне");
/*     */         }
/*     */         
/* 219 */         if (this.casino.get()) {
/* 220 */           Matcher matcher = this.COINS_PATTERN.matcher(message.replace(",", ""));
/* 221 */           if (matcher.find()) {
/* 222 */             int coins = Integer.parseInt(matcher.group(1));
/* 223 */             String playerName = matcher.group(2);
/* 224 */             if (coins < 1000) {
/* 225 */               mc.player.sendChatMessage("/msg " + playerName + " Слишком маленькая сумма, минимум ставки 1000$");
/*     */             } else {
/* 227 */               mc.player.sendChatMessage("/msg " + playerName + " Спасибо за участие! В случае выигрыша мы вам сообщим");
/*     */             } 
/*     */           } 
/*     */         }  }
/*     */        }
/*     */   
/*     */   }
/*     */   
/*     */   private int parse(String message) {
/* 236 */     double multiplier = 1.0D;
/* 237 */     message = message.toLowerCase().replaceAll("[^0-9кК.,]", "");
/*     */     
/* 239 */     if (message.endsWith("ккк")) {
/* 240 */       multiplier = 1.0E9D;
/* 241 */       message = message.substring(0, message.length() - 3);
/* 242 */     } else if (message.endsWith("кк")) {
/* 243 */       multiplier = 1000000.0D;
/* 244 */       message = message.substring(0, message.length() - 2);
/* 245 */     } else if (message.endsWith("к")) {
/* 246 */       multiplier = 1000.0D;
/* 247 */       message = message.substring(0, message.length() - 1);
/*     */     } 
/*     */     
/* 250 */     message = message.replace(',', '.');
/*     */     
/*     */     try {
/* 253 */       double value = Double.parseDouble(message);
/* 254 */       return (int)(value * multiplier);
/* 255 */     } catch (NumberFormatException e) {
/* 256 */       return 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   @NativeInclude
/*     */   public void onEnable() {
/* 262 */     this.timer.reset();
/*     */   }
/*     */   
/*     */   public void onDisable() {}
/*     */ }


/* Location:              C:\Users\pavel\OneDrive\Рабочий стол\Rockstar\1.16.5\1.16.5.jar!\fun\rockstarity\client\modules\other\ChatUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */