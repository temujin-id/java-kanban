package ru.practicum.tracker;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task("Оплатить интернет", "До 25 числа");
        Task task2 = new Task("Купить пылесос", "Выбрать модель");
        Epic epic1 = new Epic("Переезд", "В новую квартиру");
        Epic epic2 = new Epic("Праздник", "Организация дня рождения");
        Subtask subtask11 = new Subtask("Собрать коробки", "Кухня и спальня", epic1.getId());
        Subtask subtusk12 = new Subtask("Нанять грузчиков", "3 предложения", epic1.getId());
        Subtask subtask21 = new Subtask("Торт", "Заказать шоколадный", epic2.getId());

        Task t1 = manager.createTask(task1);
        Task t2 = manager.createTask(task2);

        Epic e1 = manager.createEpic(epic1);
        Subtask s11 = manager.createSubtask(subtask11);
        Subtask s12 = manager.createSubtask(subtusk12);

        Epic e2 = manager.createEpic(epic2);
        Subtask s21 = manager.createSubtask(subtask21);
    }
}
