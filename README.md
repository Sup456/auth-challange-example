# Сервис авторизации с защитой от перебора

- [Схема работы](#схема-работы)
- [Попытка авторизации](#попытка-авторизации)
- [Отправка challenge](#отправка-challenge)
- [Вычисление challenge](#вычисление-challenge)
- [Проверка challenge](#проверка-challenge)

## Схема работы
[![](https://mermaid.ink/img/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG5wYXJ0aWNpcGFudCBjIGFzIENsaWVudFxucGFydGljaXBhbnQgcyBhcyBTZXJ2ZXIgXG5cbmMtPj4rczogUE9TVCAvYXV0aCB7dXNlcm5hbWUsIHBhc3N3b3JkfVxucy0-PnM6IGNoZWNrIGlmIGNoYWxsZW5nZSBpcyByZXF1aXJlZFxuYWx0IENoYWxsZW5nZSBpcyBub3QgcmVxdWlyZWRcbnMtPj5zOiBjaGVjayB1c2VybmFtZSBhbmQgcGFzc3dvcmRcbnMtPj5jOiB7YXV0aFN0YXR1cz1BVVRIT1JJWkVEL05PVF9BVVRIT1JJWkVEfVxuZWxzZSBDaGFsbGVuZ2UgaXMgcmVxdWlyZWRcbnMtPj4tYzoge2F1dGhTdGF0dXM9Y2hhbGxlbmdlLCA8YnI-IGNoYWxsZW5nZSA9IHtwcmVmaXgsIGNvbXBsZXhpdHksIGhhc2hGdW5jdGlvbn19XG5lbmRcblxuYWN0aXZhdGUgY1xuYy0-PmM6IHNvbHZlIGNoYWxsZW5nZVxuYy0-PitzOiBQT1NUIC9jaGFsbGVuZ2Uge3ByZWZpeCwgcmVzdWx0fVxucy0-PnM6IGNoZWNrczogPGJyPiAxLiBJZiBwcmVmaXggbWF0Y2hlcyByZXN1bHQgPGJyPiAyLiBJZiBwcmVmaXggaGFzIGEgdmFsaWQgY2hhbGxlbmdlIDxicj4gMy4gSWYgcmVzdWx0IHBhc3NlcyB0aGUgY2hhbGxlbmdlIDxicj4gNC4gSWYgdXNlcm5hbWUtcGFzc3dvcmQgcGFpciBpcyB2YWxpZFxuYWx0IEFsbCBjaGVja3MgcGFzc2VkXG5zLT4-Yzoge2F1dGhTdGF0dXM9QVVUSE9SSVpFRH1cbmVsc2UgXG5zLT4-LWM6IHthdXRoU3RhdHVzPU5PVF9BVVRIT1JJWkVEfVxuZW5kXG5kZWFjdGl2YXRlIGMiLCJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCJ9LCJ1cGRhdGVFZGl0b3IiOmZhbHNlLCJhdXRvU3luYyI6dHJ1ZSwidXBkYXRlRGlhZ3JhbSI6ZmFsc2V9)](https://mermaid-js.github.io/mermaid-live-editor/edit/#eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG5wYXJ0aWNpcGFudCBjIGFzIENsaWVudFxucGFydGljaXBhbnQgcyBhcyBTZXJ2ZXIgXG5cbmMtPj4rczogUE9TVCAvYXV0aCB7dXNlcm5hbWUsIHBhc3N3b3JkfVxucy0-PnM6IGNoZWNrIGlmIGNoYWxsZW5nZSBpcyByZXF1aXJlZFxuYWx0IENoYWxsZW5nZSBpcyBub3QgcmVxdWlyZWRcbnMtPj5zOiBjaGVjayB1c2VybmFtZSBhbmQgcGFzc3dvcmRcbnMtPj5jOiB7YXV0aFN0YXR1cz1BVVRIT1JJWkVEL05PVF9BVVRIT1JJWkVEfVxuZWxzZSBDaGFsbGVuZ2UgaXMgcmVxdWlyZWRcbnMtPj4tYzoge2F1dGhTdGF0dXM9Y2hhbGxlbmdlLCA8YnI-IGNoYWxsZW5nZSA9IHtwcmVmaXgsIGNvbXBsZXhpdHksIGhhc2hGdW5jdGlvbn19XG5lbmRcblxuYWN0aXZhdGUgY1xuYy0-PmM6IHNvbHZlIGNoYWxsZW5nZVxuYy0-PitzOiBQT1NUIC9jaGFsbGVuZ2Uge3ByZWZpeCwgcmVzdWx0fVxucy0-PnM6IGNoZWNrczogPGJyPiAxLiBJZiBwcmVmaXggbWF0Y2hlcyByZXN1bHQgPGJyPiAyLiBJZiBwcmVmaXggaGFzIGEgdmFsaWQgY2hhbGxlbmdlIDxicj4gMy4gSWYgcmVzdWx0IHBhc3NlcyB0aGUgY2hhbGxlbmdlIDxicj4gNC4gSWYgdXNlcm5hbWUtcGFzc3dvcmQgcGFpciBpcyB2YWxpZFxuYWx0IEFsbCBjaGVja3MgcGFzc2VkXG5zLT4-Yzoge2F1dGhTdGF0dXM9QVVUSE9SSVpFRH1cbmVsc2UgXG5zLT4-LWM6IHthdXRoU3RhdHVzPU5PVF9BVVRIT1JJWkVEfVxuZW5kXG5kZWFjdGl2YXRlIGMiLCJtZXJtYWlkIjoie1xuICBcInRoZW1lXCI6IFwiZGVmYXVsdFwiXG59IiwidXBkYXRlRWRpdG9yIjpmYWxzZSwiYXV0b1N5bmMiOnRydWUsInVwZGF0ZURpYWdyYW0iOmZhbHNlfQ)

## Попытка авторизации
Пользователь пытается авторизоваться через POST запрос `/auth` с параметрами:
```
POST /auth
{
    "username": "username",
    "password": "password"
}
```
Параметр|Тип|Комментарий
-------------|-------------|-----
`username`|`String`|
`password`|`String`|В открытом виде только для демонстрации принципа работы

Если сервер не фиксирует попытку перебора паролей, то сразу произойдет попытка авторизации. Пользователь получит следующие результаты:
```
{
    "status": "AUTHORIZED"/"NOT_AUTHORIZED"
}
```

## Отправка challenge
Если же сервер фиксирует попытки перебора пароля, то пользователь будет направлен на challenge. Пример ответа:
```
{
    "status": "CHALLENGE",
    "challenge": {
        "prefix": "def7ff8c-10d8-4fae-a6f2-085c6fa1",
        "complexity": 2,
        "hashFunction": "PBKDF2"
    }
}
```
Параметр|Тип|Комментарий
-------------|-------------|-----
`prefix`|`String`|Уникальный идентификатор challenge
`complexity`|`int`|Сложность challenge(кол-во нулей в хеше)
`hashFunction`|`String`|Возможные значения: SHA256, PBKDF2

## Вычисление challenge

Как только пользователь получил challenge, его клиент(например, браузер) должен начать перебор хешей. Делается это следующим образом:
1. `preHashedString = prefix + <some symbols>`.
2. Вычислить `hash = hashFunction(preHashedString)`.
3. 
  - Если `hash` начинается с меньшего количества нулей, чем указано в `complexity`,  то повторить алгоритм с раннее не использованным значением переменной `<some symbols>`.
  - Иначе `preHashedString` - результат challenge(`result`).

## Проверка challenge
После нахождения `result` клиент отправляет POST запрос `/challenge` на сервер:
```
POST /challenge
{
    "prefix": "def7ff8c-10d8-4fae-a6f2-085c6fa1",
    "result": "def7ff8c-10d8-4fae-a6f2-085c6fa1wQ"
}
```
Параметр|Тип|Комментарий
-------------|-------------|-----
`prefix`|`String`|Уникальный идентификатор challenge
`result`|`String`|Строка, хеш которой удовлетворяет требованиям challenge

Получив этот запрос, сервер проверит следующие утверждения:
1. `result` начинается с `prefix`.
2. `prefix` соответствует действующему `challenge`.
3. `hashFunction(result)` начинается с количества нулей, указанного в `complexity` при отправке.
4. `username` соответствует `password`, которые пользователь указал в `/auth`

Если одна из этих проверок дала отрицательный результат, пользователь получит `"NOT_AUTHORIZED"` без объяснения причины.
Если же все проверки пройдены успешно, то сервер вернет `"AUTHORIZED"`.