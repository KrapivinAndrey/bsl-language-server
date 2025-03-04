# Пробел в начале комментария (SpaceAtStartComment)

|      Тип      |    Поддерживаются<br>языки    |     Важность     |    Включена<br>по умолчанию    |    Время на<br>исправление (мин)    |    Теги    |
|:-------------:|:-----------------------------:|:----------------:|:------------------------------:|:-----------------------------------:|:----------:|
| `Дефект кода` |         `BSL`<br>`OS`         | `Информационный` |              `Да`              |                 `1`                 | `standard` |

## Параметры


|         Имя          |   Тип    |                                                     Описание                                                     |    Значение<br>по умолчанию    |
|:--------------------:|:--------:|:----------------------------------------------------------------------------------------------------------------:|:------------------------------:|
| `commentsAnnotation` | `Строка` | `Пропускать комментарии-аннотации, начинающиеся с указанных подстрок. Список через запятую. Например: //@,//(c)` |        `//@,//(c),//©`         |
<!-- Блоки выше заполняются автоматически, не трогать -->
## Описание диагностики

Между символами комментария "//" и текстом комментария должен быть пробел.  

Исключением из правила являются _**комментарии-аннотации**_, т.е. комментарии начинающиеся с определенной последовательности символов.

## Источники

* [Стандарт: Тексты модулей, пункт 7.3](https://its.1c.ru/db/v8std#content:456:hdoc)

## Сниппеты

<!-- Блоки ниже заполняются автоматически, не трогать -->
### Экранирование кода

```bsl
// BSLLS:SpaceAtStartComment-off
// BSLLS:SpaceAtStartComment-on
```

### Параметр конфигурационного файла

```json
"SpaceAtStartComment": {
    "commentsAnnotation": "//@,//(c),//©"
}
```
