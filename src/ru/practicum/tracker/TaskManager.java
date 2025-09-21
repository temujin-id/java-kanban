package ru.practicum.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;

    private int generateId() {
        int current = nextId;
        nextId = nextId + 1;
        return current;
    }

    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public boolean updateTask(Task task) {
        if (task == null || task.getId() == null) {
            System.out.println("updateTask: нет объекта или не задан id - обновление не выполнено");
            return false;
        }

        int id = task.getId();

        if (!tasks.containsKey(id)) {
            System.out.println("updateTask: задача с id=" + id + " не найдена");
            return false;
        }

        tasks.put(id, task);
        return true;
    }

    public boolean deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("deleteTaskById: задача с id=" + id + " не найдена");
            return false;
        }
        tasks.remove(id);
        return true;
    }

    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public boolean updateEpic(Epic newEpic) {
        if (newEpic == null || newEpic.getId() == null) {
            System.out.println("updateEpic: нет объекта или не задан id - обновление не выполнено");
            return false;
        }

        int id = newEpic.getId();
        Epic current = epics.get(id);
        if (current == null) {
            System.out.println("updateEpic: эпик с id=" + id + " не найден");
            return false;
        }

        current.setTitle(newEpic.getTitle());
        current.setDescription(newEpic.getDescription());

        updateEpicStatus(id);

        return true;
    }

    public boolean deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            System.out.println("deleteEpicById: эпик с id=" + id + " не найден");
            return false;
        }

        for (Integer subId : epic.getSubtaskIds()) {
            subtasks.remove(subId);
        }

        epics.remove(id);
        return true;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Integer subId : epic.getSubtaskIds()) {
            Subtask s = subtasks.get(subId);
            if (s == null) {
                continue;
            }

            Status st = s.getStatus();

            if (st != Status.NEW) {
                allNew = false;
            }
            if (st != Status.DONE) {
                allDone = false;
            }

            if (!allNew && !allDone) {
                break;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public Subtask createSubtask(Subtask subtask) {
        Integer epicId = subtask.getEpicId();
        Epic parent = epics.get(epicId);

        if (parent == null) {
            System.out.println("Эпик с id=" + epicId + " не найден. Подзадача не создана.");
            return null;
        }

        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);

        parent.getSubtaskIds().add(id);
        updateEpicStatus(epicId);

        return subtask;
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public  ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();

    }

    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }

        ArrayList<Subtask> result = new ArrayList<>();
        for (Integer subId : epic.getSubtaskIds()) {
            Subtask s = subtasks.get(subId);
            if (s != null) {
                result.add(s);
            }
        }
        return result;
    }

    public boolean updateSubtask(Subtask newSubtask) {
        if (newSubtask == null || newSubtask.getId() == null) {
            System.out.println("updateSubtask: нет объекта или не задан id - обновление не выполнено");
            return false;
        }

        int id = newSubtask.getId();
        Subtask current = subtasks.get(id);
        if (current == null) {
            System.out.println("updateSubtask: подзадача с id=" + id + " не найдена");
            return false;
        }

        Integer newEpicId = newSubtask.getEpicId();
        Epic newParent = epics.get(newEpicId);
        if (newParent == null) {
            System.out.println("updateSubtask: эпик с id=" + newEpicId + " не найден - обновление не выполнено");
            return false;
        }

        Integer oldEpicId = current.getEpicId();
        if (!oldEpicId.equals(newEpicId)) {
            Epic oldParent = epics.get(oldEpicId);
            if (oldParent != null) {
                oldParent.getSubtaskIds().remove(Integer.valueOf(id));
            }
            newParent.getSubtaskIds().add(id);
            current.setEpicId(newEpicId);
        }

        current.setTitle(newSubtask.getTitle());
        current.setDescription(newSubtask.getDescription());
        current.setStatus(newSubtask.getStatus());

        updateEpicStatus(oldEpicId);
        updateEpicStatus(newEpicId);

        return true;
    }

    public boolean deleteSubtaskById(int id) {
        Subtask sub = subtasks.get(id);
        if (sub == null) {
            System.out.println("deleteSubtaskById: подзадача с id=" + id + " не найдена");
            return false;
        }

        Integer epicId = sub.getEpicId();
        Epic parent = epics.get(epicId);
        if (parent != null) {
            parent.getSubtaskIds().remove(Integer.valueOf(id));
        }

        subtasks.remove(id);

        updateEpicStatus(epicId);

        return true;
    }
}
