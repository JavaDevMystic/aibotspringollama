package jav.dev.pro.aiollamadepspeek.bot;


import jav.dev.pro.aiollamadepspeek.config.PropertiesConfig;
import jav.dev.pro.aiollamadepspeek.entity.QuestionHistory;
import jav.dev.pro.aiollamadepspeek.entity.User;
import jav.dev.pro.aiollamadepspeek.service.KeyBoardService;
import jav.dev.pro.aiollamadepspeek.service.OllamaAiService;
import jav.dev.pro.aiollamadepspeek.service.QuestionService;
import jav.dev.pro.aiollamadepspeek.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

@Component
public class TelegramBotAI extends TelegramLongPollingBot {


    private final PropertiesConfig config;
    private final UserService userService;
    private final QuestionService questionService;
    private final OllamaAiService ollamaAiService;
    private final KeyBoardService keyBoardService;

    private final String url = "https://framerusercontent.com/images/g0YTRh7uRHpbWQgSZz62bO050.png";
    private final String undovUrl = "https://t4.ftcdn.net/jpg/00/17/52/33/360_F_17523397_npLaLSM8Q8zTmZpZz2xMcIVoLE6gT8f7.jpg";

    public TelegramBotAI(PropertiesConfig config, UserService userService, QuestionService questionService, OllamaAiService ollamaAiService, KeyBoardService keyBoardService) {
        this.config = config;
        this.userService = userService;
        this.questionService = questionService;
        this.ollamaAiService = ollamaAiService;
        this.keyBoardService = keyBoardService;
    }


    @Override
    public String getBotUsername() {
        return config.getUsername();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            Message message = update.getMessage();

            if (message.hasText()) {
                handleTextMessage(message);
            } else if (message.hasContact()) {
                handleContactMessage(message);
            }
        }
    }

    private void handleTextMessage(Message message) {
        String text = message.getText();
        Long chatId = message.getChatId();

        if ("/start".equals(text)) {
            if (userService.checkUser(chatId)) {
                sendStartTextForRegisteredUser(chatId);
            } else {
                sendStartPhotoAndKeyboard(chatId);
            }
        } else {
            if (userService.isStart(chatId)) {
                switch (text) {
                    case "📜 Show Question History":
                        // bu yerga tarixni ko'rsatish metodi
//                        sendQuestionHistory(chatId);
                        break;

                    case "❌ Delete Question":
                        // bu yerga savolni o'chirish metodi
//                        deleteUserQuestion(chatId);
                        break;

                    case "⏱ Start Pamidor Timer":
                        // bu yerga Pomidor Timer WebApp tugmasini yuborish
                        sendPamidorTimerWebApp(chatId);
                        break;

                    case "🧑‍💻 Delete User":
//                        deleteUser(chatId);
                        break;

                    default:
                        handleUserQuery(chatId, text);
                        break;
                }
            } else {
                sendNoStartPhotoAndKeyboard(chatId);
            }
        }
    }


    private void handleUserQuery(Long chatId, String query) {

        if (query == null || query.isBlank()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Iltimos, savolingizni to'g'ri formatda yuboring.");
            executeMessage(sendMessage);
            return;
        }
        QuestionHistory history = new QuestionHistory();
        history.setUserChatId(chatId);
        history.setQuestion(query);
        questionService.save(history);


        String answer = ollamaAiService.askOllama(query);
        ReplyKeyboardMarkup replyKeyboardMarkup = keyBoardService.keyboardButtonUserHelper();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(answer);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        executeMessage(sendMessage);

    }

    /// ///////////////////////////  Cantact send save user /////////////////////////////
    private void handleContactMessage(Message message) {
        Long chatId = message.getChatId();

        User user = new User();
        user.setChatId(chatId);
        user.setFirstName(message.getFrom().getFirstName());
        user.setLastName(message.getFrom().getLastName());
        user.setUsername(message.getFrom().getUserName());
        user.setPhoneNumber(message.getContact().getPhoneNumber());
        user.setIsStart(true);
        userService.createUser(user);

        String sentText = """
                <b>Botimizga xush kelibsiz!</b>\n\n
                Siz ushbu bot orqali quyidagi amallarni bajarishingiz mumkin:\n
                — Ixtiyoriy savol bering, bot sizga javob beradi.\n\n
                ⚠️ <i>Diqqat:</i>\n
                — Savollar <b>o‘zbek tilida</b> bo‘lishi kerak.\n
                — Hozircha bot faqat <b>text formatdagi</b> so‘rovlarni qabul qiladi.\n\n
                Agar qandaydir tushunmovchilik bo‘lsa,\n
                <a href="https://t.me/zm_coder">Zaripov Muxiddin</a> ga murojaat qilishingiz mumkin.
                """;

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentText);
        sendMessage.setParseMode("HTML");
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        executeMessage(sendMessage);
    }

    /// /////  start button true passing ///////////////////
    private void sendStartTextForRegisteredUser(Long chatId) {
        String sendText = """
                Hurmatli foydalanuvchi, botdan foydalanishingiz mumkin.\n
                Biror yordam kerak bo‘lsa,\n
                <a href="https://t.me/zm_coder">Zaripov Muxiddin</a> ga murojaat qilishingiz mumkin.
                """;

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(sendText)
                .parseMode("HTML")
                .build();

        executeMessage(sendMessage);
    }

    private void sendStartPhotoAndKeyboard(Long chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(url));

        String caption = """
                Assalomu alaykum, hurmatli foydalanuvchi! 🤖\n\n
                <b>Botimizga xush kelibsiz!</b>\n\n
                📞 Telefon raqamingizni jo'nating\n
                Agar qandaydir tushunmovchilik bo‘lsa,\n
                <a href="https://t.me/zm_coder">Zaripov Muxiddin</a> ga murojaat qilishingiz mumkin.
                """;

        sendPhoto.setCaption(caption);
        sendPhoto.setParseMode("HTML");
        sendPhoto.setReplyMarkup(keyBoardService.createPhoneRequestKeyboard());
        executePhotoMessage(sendPhoto);
    }


    /// ///////////////////  No starter ////////////////////////////////////

    private void sendNoStartPhotoAndKeyboard(Long chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(undovUrl));

        String caption = """
                Assalomu alaykum, hurmatli foydalanuvchi! 🤖\n\n
                <b>Botimizga xush kelibsiz!</b>\n\n
                Botdan foydalanish uchun oldin start tugmasini bosing\n
                Agar qandaydir tushunmovchilik bo‘lsa,\n
                <a href="https://t.me/zm_coder">Zaripov Muxiddin</a> ga murojaat qilishingiz mumkin.
                """;

        sendPhoto.setCaption(caption);
        sendPhoto.setParseMode("HTML");
        executePhotoMessage(sendPhoto);
    }


    public void sendPamidorTimerWebApp(Long chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        final String url = "https://cdn.vectorstock.com/i/1000v/30/04/timer-pomodoro-time-management-technique-vector-33863004.jpg";

        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(url));
        sendPhoto.setCaption("🍅 Pomidor Taymerni tanlang va diqqatni jamlang!");
        InlineKeyboardMarkup markup = keyBoardService.createInlineKeyboardMarkup();
        sendPhoto.setReplyMarkup(markup);
        executePhotoMessage(sendPhoto);
    }

    /// //////////////////   execute message and execute photo //////////////////////////////
    public void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executePhotoMessage(SendPhoto message) {
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
