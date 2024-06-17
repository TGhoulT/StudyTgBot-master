import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    private final List<Question> questions = new ArrayList<>();
    private final Map<Long, Integer> userScores = new HashMap<>();
    private final Map<Long, Integer> userProgress = new HashMap<>();
    private final Map<Long, Integer> userMessages = new HashMap<>();
    private final Map<Long, Boolean> waitingForCandidateInfo = new HashMap<>();
    private final DBManager dbManager = new DBManager();
    private static final String ADMIN_CHAT_ID = "your_chat_id";

    public Bot() {
        initializeQuestions();
    }

    private void initializeQuestions() {
        questions.add(new Question(
                "Какая разница между классами String и StringBuilder?",
                List.of("Нет разницы", "Разница в создании копий", "Разница в типе используемых данных", "Один класс, а другой примитив"),
                1
        ));
        questions.add(new Question(
                "Выберите только примитивы",
                List.of("int, double, Character", "Boolean, Integer, Double", "short, byte, char", "boolean, float, long, Object"),
                2
        ));
        questions.add(new Question(
                "Чем современные классы LocalDate и LocalDateTime лучше классического класса Date?",
                List.of("Удобны в работе", "Работают более точно", "Преобразовывают в любой формат"),
                0
        ));
        questions.add(new Question(
                "Каким ключевым словом объявляется, что класс наследует другой класс?",
                List.of("Expand", "Extend", "Extended", "Extends"),
                3
        ));
    }

    @Override
    public String getBotUsername() {
        return "@your_bot";
    }

    @Override
    public String getBotToken() {
        return "YOUR_BOT_TOKEN";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleCommand(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }
    }

    private void handleCommand(Message message) {
        String text = message.getText();
        Long chatId = message.getChatId();
        if (text.equals("/test") || text.equals("/start_test")) {
            startTest(chatId);
        } else if (text.equals("/menu")) { //функционал пока не реализован
            sendMenu(chatId);
        } else if (waitingForCandidateInfo.getOrDefault(chatId, false)) {
            saveCandidateInfo(message);
        }
    }

    private void startTest(Long chatId) {
        userScores.put(chatId, 0);
        userProgress.put(chatId, 0);
        sendQuestion(chatId, 0);
    }

    private void sendQuestion(Long chatId, int questionIndex) {
        Question question = questions.get(questionIndex);
        InlineKeyboardMarkup keyboard = buildQuestionKeyboard(question);

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(question.getQuestion())
                .replyMarkup(keyboard)
                .build();

        try {
            Message sentMessage = execute(message);
            userMessages.put(chatId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup buildQuestionKeyboard(Question question) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (int i = 0; i < question.getOptions().size(); i++) {
            buttons.add(List.of(InlineKeyboardButton.builder()
                    .text(question.getOptions().get(i))
                    .callbackData(String.valueOf(i))
                    .build()));
        }
        return InlineKeyboardMarkup.builder().keyboard(buttons).build();
    }

    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        int questionIndex = userProgress.getOrDefault(chatId, 0);

        if (callbackData.matches("\\d+")) {
            int answerIndex = Integer.parseInt(callbackData);
            if (answerIndex == questions.get(questionIndex).getCorrectAnswerIndex()) {
                userScores.put(chatId, userScores.get(chatId) + 1);
            }

            questionIndex++;
            userProgress.put(chatId, questionIndex);

            if (questionIndex < questions.size()) {
                editQuestion(chatId, questionIndex);
            } else {
                finishTest(chatId);
            }
        }

        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .build();

        try {
            execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void editQuestion(Long chatId, int questionIndex) {
        Question question = questions.get(questionIndex);
        InlineKeyboardMarkup keyboard = buildQuestionKeyboard(question);

        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(userMessages.get(chatId))
                .text(question.getQuestion())
                .replyMarkup(keyboard)
                .build();

        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void finishTest(Long chatId) {
        int score = userScores.get(chatId);
        int totalQuestions = questions.size();
        int percentage = (score * 100) / totalQuestions;

        String resultMessage = "Вы прошли тест и правильно выполнили " + percentage + "% заданий!";

        if (percentage > 70) {
            resultMessage = "Вы успешно сдали тест на " + percentage + "%.\n" +
                    "Теперь вы можете связаться с заказчиком по его нику в телеграмме - @ogarJavaDev\n" +
                    "Пожалуйста, отправьте свои ФИО и email.";
            waitingForCandidateInfo.put(chatId, true);
        }

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(resultMessage)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void askCandidateInfo(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Поздравляем, вы прошли тестирование! Пожалуйста, отправьте свои ФИО и email.")
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void saveCandidateInfo(Message message) {
        String[] info = message.getText().split(",");
        Long chatId = message.getChatId();
        if (info.length == 2) {
            String name = info[0].trim();
            String email = info[1].trim();

            int totalQuestions = questions.size(); // Общее количество вопросов в тесте (здесь может быть ваше значение)
            int correctAnswers = userScores.get(chatId); // Количество правильных ответов
            int percent = (int) Math.round((double) correctAnswers / totalQuestions * 100); // Вычисляем процент правильных ответов

            dbManager.saveCandidate(name, email, percent); // Сохраняем процент в базу данных
            sendAdminNotification(name, email); // Уведомляем администратора

            waitingForCandidateInfo.put(chatId, false);
            sendCompletionMessage(chatId);
        } else {
            SendMessage error = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Пожалуйста, отправьте данные в формате: ФИО, email")
                    .build();

            try {
                execute(error);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendAdminNotification(String name, String email) {
        String text = "Новый успешный кандидат:\n\n" +
                "ФИО: " + name + "\n" +
                "Email: " + email;

        SendMessage message = SendMessage.builder()
                .chatId(ADMIN_CHAT_ID)
                .text(text)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendCompletionMessage(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Спасибо! Ваши данные сохранены.")
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMenu(Long chatId) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Пройти тест")
                        .callbackData("/test")
                        .build(),
                InlineKeyboardButton.builder()
                        .text("Материалы")
                        .callbackData("/materials")
                        .build()
        ));

        InlineKeyboardMarkup keyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(buttons)
                .build();

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Выберите действие:")
                .replyMarkup(keyboardMarkup)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
