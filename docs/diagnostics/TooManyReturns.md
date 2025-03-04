# Метод не должен содержать много возвратов (TooManyReturns)

|      Тип      |    Поддерживаются<br>языки    |     Важность     |    Включена<br>по умолчанию    |    Время на<br>исправление (мин)    |      Теги       |
|:-------------:|:-----------------------------:|:----------------:|:------------------------------:|:-----------------------------------:|:---------------:|
| `Дефект кода` |         `BSL`<br>`OS`         | `Незначительный` |             `Нет`              |                `20`                 | `brainoverload` |

## Параметры


|        Имя        |   Тип   |                          Описание                           |    Значение<br>по умолчанию    |
|:-----------------:|:-------:|:-----------------------------------------------------------:|:------------------------------:|
| `maxReturnsCount` | `Целое` | `Максимально допустимое количество возвратов внутри метода` |              `3`               |
<!-- Блоки выше заполняются автоматически, не трогать -->
## Описание диагностики
<!-- Описание диагностики заполняется вручную. Необходимо понятным языком описать смысл и схему работу -->

Большое количество возвратов в методе (процедуре или функции) увеличивает его сложность и снижает производительность и восприятие.

## Примеры
<!-- В данном разделе приводятся примеры, на которые диагностика срабатывает, а также можно привести пример, как можно исправить ситуацию -->

Пример плохого метода

```bsl
Функция Пример(Условие)
    Если Условие = 1 Тогда
        Возврат "Проверка пройдена";
    ИначеЕсли Условие = 2 Тогда
        ВыполнитьДействие();
        Возврат "Проверка не пройдена";
    ИначеЕсли Условие > 7 Тогда
        Если ВыполнитьПроверку(Условие) Тогда
            Возврат "Проверка пройдена";
        Иначе
            Возврат "Проверка не пройдена";
        КонецЕсли;
    КонецЕсли;
    Возврат "";
КонецФункции
```

## Источники

* [Why Many Return Statements Are a Bad Idea in OOP](https://www.yegor256.com/2015/08/18/multiple-return-statements-in-oop.html)
* [JAVA: Methods should not have too many return statements](https://rules.sonarsource.com/java/RSPEC-1142)
* [Почему ранний возврат из функций так важен?](https://habr.com/ru/post/348074/)

## Сниппеты

<!-- Блоки ниже заполняются автоматически, не трогать -->
### Экранирование кода

```bsl
// BSLLS:TooManyReturns-off
// BSLLS:TooManyReturns-on
```

### Параметр конфигурационного файла

```json
"TooManyReturns": {
    "maxReturnsCount": 3
}
```
