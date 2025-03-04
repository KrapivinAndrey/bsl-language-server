# Пустой блок кода (EmptyCodeBlock)

|      Тип      |    Поддерживаются<br>языки    | Важность |    Включена<br>по умолчанию    |    Время на<br>исправление (мин)    |                Теги                 |
|:-------------:|:-----------------------------:|:--------:|:------------------------------:|:-----------------------------------:|:-----------------------------------:|
| `Дефект кода` |         `BSL`<br>`OS`         | `Важный` |              `Да`              |                 `5`                 |    `badpractice`<br>`suspicious`    |

## Параметры


|       Имя       |   Тип    |              Описание               |    Значение<br>по умолчанию    |
|:---------------:|:--------:|:-----------------------------------:|:------------------------------:|
| `commentAsCode` | `Булево` | `Считать комментарий в блоке кодом` |            `false`             |
<!-- Блоки выше заполняются автоматически, не трогать -->
## Описание диагностики

Пустые блоки являются признаком возможной ошибки:

- Забыли реализовать
- Удалили содержимое

Пустые блоки кода должны быть наполнены либо удалены.

## Сниппеты

<!-- Блоки ниже заполняются автоматически, не трогать -->
### Экранирование кода

```bsl
// BSLLS:EmptyCodeBlock-off
// BSLLS:EmptyCodeBlock-on
```

### Параметр конфигурационного файла

```json
"EmptyCodeBlock": {
    "commentAsCode": false
}
```
