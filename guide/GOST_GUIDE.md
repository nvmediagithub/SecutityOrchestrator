Командам хакатона

1.	Зайти в Реестр: https://api-registry-frontend.bankingapi.ru/
2.	Изучить спецификации API;
2.1.	Нажать на Карточку API, которую нужно просмотреть;
2.2.	Нажать на кнопку «Открыть редактор»;
2.3.	Перейти на вкладку «Обзор» или «Код-Редактор».
3.	Получение access_token (аутентификация)
curl -v --data ‘grant_type=client_credentials&client_id=<client_id>&client_secret=<client_secret>' https://auth.bankingapi.ru/auth/realms/kubernetes/protocol/openid-connect/token
4.	Вызов API БЕЗ GOST.
Хост вызова API: https://api.bankingapi.ru/ 
5.	Вызов API с GOST-шлюзом:
Хост вызова API: https://api.gost.bankingapi.ru:8443/ 
То есть условно, обращение к хосту API будет происходить так: https://api.gost.bankingapi.ru:8443/api/rb/rewardsPay/hackathon/v1/cards/accounts/external/{externalAccountID}/rewards/balance

ОБРАЩАЕМ ВАШЕ ВНИМАНИЕ, ЧТО ДЛЯ РАБОТЫ С GOST-ШЛЮЗОМ ВАМ ПОТРЕБУЕТСЯ ВЫПОЛНИТЬ НЕСКОЛЬКО УСЛОВИЙ
1)	НАЛИЧИЕ openssl, совместимого с GOST-протоколами шифрования.
2)	НАЛИЧИЕ curl, совместимого с GOST-протоколами шифрования.
3)	НАЛИЧИЕ доверенного сертификата КриптоПРО (доступен для получения на тестовый период в 1 месяц на официальном сайте) для формирования TLS over HTTPS связи.
