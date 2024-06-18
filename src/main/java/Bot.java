import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
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

    public InlineKeyboardButton back = InlineKeyboardButton.builder()
            .text("Назад")
            .callbackData("назад")
            .build();
    public InlineKeyboardButton downloadIntellijIdea = InlineKeyboardButton.builder()
            .text("Скачать среду разработки")
            .callbackData("скачать среду разработки")
            .url("https://www.jetbrains.com/ru-ru/idea/download/?section=windows")
            .build();

    public InlineKeyboardButton jarFile = InlineKeyboardButton.builder()
            .text("Упаковка jar-файла")
            .callbackData("упаковка jar-файла")
            .url("https://docs.google.com/document/d/1YM1mylggI9d1ZkxenWfHRGxn_NK38FJ-1JrHj2eeR1E/edit#heading=h.9yiytdaelm8b")
            .build();

    public InlineKeyboardButton encapsulation = InlineKeyboardButton.builder()
            .text("Инкапсуляция")
            .callbackData("инкапсуляция")
            .url("https://blog.skillfactory.ru/glossary/inkapsulyacziya/")
            .build();

    public InlineKeyboardButton pojo = InlineKeyboardButton.builder()
            .text("POJO-классы, геттеры и сеттеры")
            .callbackData("POJO-классы, геттеры и сеттеры")
            .url("https://javarush.com/groups/posts/1928-getterih-i-setterih")
            .build();
    public InlineKeyboardButton operationWithNumbers = InlineKeyboardButton.builder()
            .text("Операции с числами")
            .callbackData("операции с числами")
            .url("docs.oracle.com/cd/A58617_01/server.804/a58225/ch3all.htm")
            .build();

    public InlineKeyboardButton incrementAndDecrement = InlineKeyboardButton.builder()
            .text("Инкремент и декремент")
            .callbackData("инкремент и декремент")
            .url("https://docs.oracle.com/cd/E19253-01/817-6223/chp-typeopexpr-9/index.html")
            .build();

    public InlineKeyboardButton linesConcatenationAndCompare = InlineKeyboardButton.builder()
            .text("Строки, конкатенация и сравнение")
            .callbackData("строки, конкатенация и сравнение")
            .url("https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html")
            .build();

    public InlineKeyboardButton updateNumbersToLinesAndReverse = InlineKeyboardButton.builder()
            .text("Преобразование чисел в строки и обратно")
            .callbackData("преобразование чисел в строки и обратно")
            .url("https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#contains-java.lang.CharSequence-")
            .build();

    public InlineKeyboardButton createArrays = InlineKeyboardButton.builder()
            .text("Создание массивов")
            .callbackData("создание массивов")
            .url("https://docs.oracle.com/javase/tutorial/java/nutsandbolts/arrays.html")
            .build();

    public InlineKeyboardButton workWithArraysInCycles = InlineKeyboardButton.builder()
            .text("Работа с массивами в циклах")
            .callbackData("работа с массивами в циклах")
            .url("https://docs.oracle.com/javase%2F8%2Fdocs%2Fapi%2F%2F/java/util/Arrays.html")
            .build();

    public InlineKeyboardButton hashSet = InlineKeyboardButton.builder()
            .text("Коллекция HashSet")
            .callbackData("коллекция HashSet")
            .url("https://docs.oracle.com/javase/8/docs/api/java/util/HashSet.html")
            .build();

    public InlineKeyboardButton treeSet = InlineKeyboardButton.builder()
            .text("Коллекция TreeSet")
            .callbackData("коллекция TreeSet")
            .url("https://docs.oracle.com/javase/8/docs/api/java/util/TreeSet.html")
            .build();

    public InlineKeyboardButton fromArraysToCollectionAndReverse = InlineKeyboardButton.builder()
            .text("Преобразование массивов и коллекций")
            .callbackData("преобразование массивов и коллекций")
            .url("https://docs.oracle.com/javase/8/docs/api/?java/util/Collections.html")
            .build();

    public InlineKeyboardButton iterator = InlineKeyboardButton.builder()
            .text("Итератор")
            .callbackData("итератор")
            .url("https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html")
            .build();

    private InlineKeyboardMarkup sendModuleOne = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(downloadIntellijIdea))
            .keyboardRow(List.of(jarFile))
            .keyboardRow(List.of(back))
            .build();
    private InlineKeyboardMarkup sendModuleFive = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(encapsulation))
            .keyboardRow(List.of(pojo))
            .keyboardRow(List.of(back))
            .build();

    private InlineKeyboardMarkup sendModuleSeven = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(operationWithNumbers))
            .keyboardRow(List.of(incrementAndDecrement))
            .keyboardRow(List.of(back))
            .build();

    private InlineKeyboardMarkup sendModuleEight = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(linesConcatenationAndCompare))
            .keyboardRow(List.of(updateNumbersToLinesAndReverse))
            .keyboardRow(List.of(back))
            .build();

    private InlineKeyboardMarkup sendModuleNine = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(createArrays))
            .keyboardRow(List.of(workWithArraysInCycles))
            .keyboardRow(List.of(back))
            .build();

    private InlineKeyboardMarkup sendModuleTen = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(hashSet))
            .keyboardRow(List.of(treeSet))
            .keyboardRow(List.of(back))
            .build();

    private InlineKeyboardMarkup sendModuleEleven = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(fromArraysToCollectionAndReverse))
            .keyboardRow(List.of(iterator))
            .keyboardRow(List.of(back))
            .build();

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
        } else if (text.equals("/menu")) {
            sendMenu(chatId);
        } else if (waitingForCandidateInfo.getOrDefault(chatId, false)) {
            saveCandidateInfo(message);
        }
    }

    private void startTest(Long chatId) {
        userProgress.put(chatId, 0);
        userScores.put(chatId, 0);

        //получаем первый вопрос из списка вопросов
        Question firstQuestion = questions.get(0);

        //создать новое сообщение с первым вопросом
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(firstQuestion.getQuestion())
                .replyMarkup(createQuestionMarkup(firstQuestion))
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void startTest(Long chatId, int messageId) {
        //обнуление прогресса и очков пользователя
        userProgress.put(chatId, 0);
        userScores.put(chatId, 0);

        //отправка первого вопроса
        editQuestion(chatId, 0, messageId);
    }

    private InlineKeyboardMarkup createQuestionMarkup(Question question) {
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

        switch (callbackData) {
            case "/test":
                startTest(chatId, messageId);
                break;
            case "/materials":
                sendMaterialsMenu(chatId, messageId);
                break;
            case "вводный модуль":
                sendMaterialDetails(chatId, messageId, "скачать среду разработки", sendModuleOne);
                break;
            case "инкапсуляция":
                sendMaterialDetails(chatId, messageId, "Инкапсуляция", sendModuleFive);
                break;
            case "числа и даты":
                sendMaterialDetails(chatId, messageId, "Числа и даты", sendModuleSeven);
                break;
            case "строки":
                sendMaterialDetails(chatId, messageId, "Строки", sendModuleEight);
                break;
            case "массивы и списки":
                sendMaterialDetails(chatId, messageId, "Массивы и списки", sendModuleNine);
                break;
            case "коллекции Set, Map":
                sendMaterialDetails(chatId, messageId, "Коллекции Set, Map", sendModuleTen);
                break;
            case "comparator Iterator Collections":
                sendMaterialDetails(chatId, messageId, "Comparator Iterator Collections", sendModuleEleven);
                break;
            case "назад":
//                sendMaterialDetails(chatId, messageId, "Выберите материал:", keyboardM1);
                sendMaterialsMenu(chatId, messageId);
                break;
            default:
                //обработка ответов на тест
                if (callbackData.matches("\\d+")) {
                    int answerIndex = Integer.parseInt(callbackData);
                    if (answerIndex == questions.get(questionIndex).getCorrectAnswerIndex()) {
                        userScores.put(chatId, userScores.get(chatId) + 1);
                    }
                    questionIndex++;
                    userProgress.put(chatId, questionIndex);
                    if (questionIndex < questions.size()) {
                        editQuestion(chatId, questionIndex, messageId);
                    } else {
                        finishTest(chatId);
                    }
                }
                break;
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

    private void editQuestion(Long chatId, int questionIndex, int messageId) {
        Question question = questions.get(questionIndex);

        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(question.getQuestion())
                .replyMarkup(createQuestionMarkup(question))
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

    public void saveCandidateInfo(Message message) {
        String[] info = message.getText().split(",");
        Long chatId = message.getChatId();
        if (info.length == 2) {
            String name = info[0].trim();
            String email = info[1].trim();

            int totalQuestions = questions.size(); //общее количество вопросов в тесте
            int correctAnswers = userScores.get(chatId); //количество правильных ответов
            int percent = (int) Math.round((double) correctAnswers / totalQuestions * 100); //вычисляем процент правильных ответов

            dbManager.saveCandidate(name, email, percent); //сохраняем процент в базу данных
            sendAdminNotification(name, email); //уведомляем администратора

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

    private void sendMaterialsMenu(Long chatId, int messageId) {
        List<List<InlineKeyboardButton>> materialsButtons = new ArrayList<>();
        materialsButtons.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Вводный модуль")
                        .callbackData("вводный модуль")
                        .build()
        ));
        materialsButtons.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Инкапсуляция")
                        .callbackData("инкапсуляция")
                        .build()
        ));
        materialsButtons.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Числа и даты")
                        .callbackData("числа и даты")
                        .build()
        ));
        materialsButtons.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Строки")
                        .callbackData("строки")
                        .build()
        ));
        materialsButtons.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Массивы и списки")
                        .callbackData("массивы и списки")
                        .build()
        ));
        materialsButtons.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Коллекции Set, Map")
                        .callbackData("коллекции Set, Map")
                        .build()
        ));
        materialsButtons.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Comparator Iterator Collections")
                        .callbackData("comparator Iterator Collections")
                        .build()
        ));
        materialsButtons.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Назад")
                        .callbackData("назад")
                        .build()
        ));

        InlineKeyboardMarkup materialsMarkup = InlineKeyboardMarkup.builder()
                .keyboard(materialsButtons)
                .build();

        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text("Выберите материал:")
                .build();

        EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .replyMarkup(materialsMarkup)
                .build();

        try {
            execute(editMessageText);
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMaterialDetails(Long chatId, int messageId, String text, InlineKeyboardMarkup markup) {
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(text)
                .build();

        EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .replyMarkup(markup)
                .build();

        try {
            execute(editMessageText);
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
