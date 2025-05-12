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
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

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
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
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
                        sendQuestionHistory(chatId);
                        break;
                    case "⏱ Start Pamidor Timer":
                        sendPamidorTimerWebApp(chatId);
                        break;

                    case "🧑‍💻 Delete User":
                        deleteUser(chatId);
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

        // Savolni tarixga yozamiz (bu qismini darhol bajaramiz)
        QuestionHistory history = new QuestionHistory();
        history.setUserChatId(chatId);
        history.setQuestion(query);
        questionService.save(history);

        // Endi javobni AI dan olish ishini alohida Thread ichida bajaramiz
        new Thread(() -> {
            try {
                String answer = ollamaAiService.askOllama(query);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(answer);
                executeMessage(sendMessage);

            } catch (Exception e) {
                // AI dan javob olishda xatolik bo'lsa
                SendMessage errorMessage = new SendMessage();
                errorMessage.setChatId(chatId);
                errorMessage.setText("Kechirasiz, savolingizga javob olishda xatolik yuz berdi.");
                executeMessage(errorMessage);
                e.printStackTrace(); // log uchun
            }
        }).start(); // Thread ishga tushirildi
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
        ReplyKeyboardMarkup replyKeyboardMarkup = keyBoardService.keyboardButtonUserHelper();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sentText);
        sendMessage.setParseMode("HTML");
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        executeMessage(sendMessage);
    }


    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        // 1. Callbackga javob beramiz (loading effektini yopish uchun)
        try {
            execute(new AnswerCallbackQuery(callbackQuery.getId()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        // 2. Tarixni ko‘rish
        if (data.startsWith("history_")) {
            handleHistoryPagination(chatId, data);
            return;
        }

        // 3. Foydalanuvchini o‘chirish
        else if ("ok_delete_user".equals(data)) {
            boolean deleted = userService.deleteUser(chatId);

            if (deleted) {
                // 3.1. Savollar tarixini o‘chirish
                questionService.deleteUserQuestion(chatId);

                // 3.2. ReplyKeyboardMarkup (oddiy tugmalar) ni olib tashlash
                ReplyKeyboardRemove removeKeyboard = new ReplyKeyboardRemove();
                removeKeyboard.setRemoveKeyboard(true); // Bu keyboardni olib tashlaydi

                // 3.3. Foydalanuvchiga tasdiq xabarini yuboramiz (va tugmalarni yo‘q qilamiz)
                SendMessage confirmation = new SendMessage();
                confirmation.setChatId(chatId.toString());
                confirmation.setText("✅ Foydalanuvchi muvaffaqiyatli o‘chirildi.");
                confirmation.setReplyMarkup(removeKeyboard); // keyboardni olib tashlaymiz

                try {
                    execute(confirmation);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else {
                // Allaqachon o‘chirilgan bo‘lsa, ogohlantirish yuboriladi
                SendMessage alreadyDeleted = new SendMessage(chatId.toString(), "⚠️ Kechirasiz, siz allaqachon o‘chirilgansiz.");
                try {
                    execute(alreadyDeleted);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    /// //////  paganetion //////////////////

    private void handleHistoryPagination(Long chatId, String callbackData) {
        String[] parts = callbackData.split("_");
        String direction = parts[1];
        int page = Integer.parseInt(parts[2]);

        List<QuestionHistory> histories = questionService.questionHistories(chatId);
        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) histories.size() / pageSize);

        showHistoryPage(chatId, histories, page, pageSize, totalPages);
    }

    /// /////   send question History ///////////////////////////////////////
    public void sendQuestionHistory(Long chatId) {
        List<QuestionHistory> histories = questionService.questionHistories(chatId);

        if (histories.isEmpty()) {
            sendMessage(chatId, "Savollar tarixi bo'sh. Hali hech qanday so'rov yubormagansiz.");
            return;
        }

        int currentPage = 0;
        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) histories.size() / pageSize);

        showHistoryPage(chatId, histories, currentPage, pageSize, totalPages);
    }

    /// ///////////////////////   show history page ///////////////////////
    private void showHistoryPage(Long chatId, List<QuestionHistory> histories, int currentPage, int pageSize, int totalPages) {
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, histories.size());
        List<QuestionHistory> pageItems = histories.subList(start, end);

        StringBuilder messageText = new StringBuilder();
        messageText.append("📜 Savollar tarixi (").append(currentPage + 1).append("/").append(totalPages).append(")\n\n");

        for (int i = 0; i < pageItems.size(); i++) {
            QuestionHistory item = pageItems.get(i);
            messageText.append("📌 ")
                    .append(i + 1)
                    .append(". ")
                    .append(item.getQuestion().trim())
                    .append("\n\n");
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        if (totalPages > 1) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            if (currentPage > 0) {
                InlineKeyboardButton prevButton = new InlineKeyboardButton("⬅️ Oldingi");
                prevButton.setCallbackData("history_prev_" + (currentPage - 1));
                row.add(prevButton);
            }

            if (currentPage < totalPages - 1) {
                InlineKeyboardButton nextButton = new InlineKeyboardButton("Keyingi ➡️");
                nextButton.setCallbackData("history_next_" + (currentPage + 1));
                row.add(nextButton);
            }

            keyboard.add(row);
        }

        markup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageText.toString());
        message.setReplyMarkup(markup);
        executeMessage(message);
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

    /// /////////////////////  Delete user  ///////////////////////////////

    private void deleteUser(Long chatId) {
        if (userService.checkUser(chatId)) {
            String text = "\u274C Foydalanuvchi profili o‘chiriladi!\n\nAgar siz bu amalni tasdiqlasangiz, barcha maʼlumotlaringiz yo‘q qilinadi.";

            InlineKeyboardButton okButton = new InlineKeyboardButton();
            okButton.setText("✅ OK");
            okButton.setCallbackData("ok_delete_user");

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(okButton);

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            keyboard.add(row);

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(keyboard);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString()); // chatId should be String
            sendMessage.setText(text);
            sendMessage.setReplyMarkup(markup);

            executeMessage(sendMessage);
        } else {
            // Agar user topilmasa
            SendMessage errorMsg = new SendMessage(chatId.toString(), "\u26A0️ Foydalanuvchi topilmadi.");
           executeMessage(errorMsg);
        }
    }

    /// //////////////////   execute message and execute photo //////////////////////////////
    public void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        executeMessage(message);
    }

    public void executePhotoMessage(SendPhoto message) {
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}