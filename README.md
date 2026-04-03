# SimbirSoft

Android-приложение для планирования дел **по дням и часам**: календарь на выбранную дату, почасовая
сетка с задачами, создание новых дел и просмотр деталей.

## Возможности

- **Список дня** — выбор даты в календаре, отображение 24 часовых слотов; в каждом слоте
  показываются все дела, пересекающиеся с этим часом.
- **Создание дела** — название, описание, время начала и окончания (экран на **Jetpack Compose**).
- **Детали дела** — заголовок, период, описание.
- **Локальное хранение** — задачи сохраняются в **Room**; при первом запуске возможна загрузка
  начальных данных из **JSON** в assets.

## Технологии

| Область            | Стек                                                                    |
|--------------------|-------------------------------------------------------------------------|
| Язык               | Kotlin                                                                  |
| UI                 | Material 3, ViewBinding, Fragment, **Jetpack Compose** (экран создания) |
| Навигация          | Navigation Component, общий `MaterialToolbar` с `NavController`         |
| Архитектура        | Слои **data** / **domain** / **presentation**                           |
| DI                 | Koin                                                                    |
| База данных        | Room (KSP)                                                              |
| Сериализация       | kotlinx.serialization (JSON)                                            |
| Асинхронность      | Kotlin Coroutines                                                       |
| Статический анализ | Detekt                                                                  |

## Требования

- Android Studio с поддержкой **AGP 9** и **JDK 17+** (для сборки под текущий Gradle-проект).
- **minSdk 26**, **targetSdk / compileSdk 36**.

## Сборка

Из корня репозитория:

```bash
./gradlew assembleDebug
```

Установка debug-сборки на подключённое устройство или эмулятор выполняется из Android Studio (**Run
**) или через `adb install`.

## Тесты

Unit-тесты для доменной логики `TaskService`:

```bash
./gradlew testDebugUnitTest
```

## Структура модулей (кратко)

- `app/src/main/java/.../data` — Room, репозитории, источники данных (в т.ч. JSON).
- `app/src/main/java/.../domain` — модели, репозитории (интерфейсы), сервисы, use case.
- `app/src/main/java/.../presentation` — ViewModel, фрагменты, Compose-экраны, тема.
- `app/src/main/java/.../di` — модули Koin.

## Демонстрация проекта

📹 **Скринкаст приложения**: [Смотреть на Google Disk](https://drive.google.com/file/d/1u9lSlRP6I58Re4zJ2NZfJAhjiAfxqXL4/view?usp=drive_link)



