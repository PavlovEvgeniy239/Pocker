
# Калькулятор шансов в Техасском Холдеме
Это приложение на Java, которое вычисляет вероятности выигрыша для заданной руки в покере Техасский Холдем. Оно предоставляет графический пользовательский интерфейс (GUI) для ввода пользователем своих карт, карт на столе и расчёта вероятности выигрыша. Кроме того, оно поддерживает вычисление вероятностей выигрыша для нескольких игроков.

## Фичи
- Расчёт вероятности выигрыша для вашей руки.
- Pacчёт матожидание выигрыша
- Расчёт вероятностей выигрыша для стола из нескольких игроков(известны все карты игроков и вероятность считается для каждого).
- Подсчёт вероятности улучшения руки до выбранных комбинаций на следующей улице.
- Подсчёт максимальной обоснованной стаки для предыдущего пункта.
- Простой и интуитивно понятный графический интерфейс (GUI).


## Подсчёт вероятностей
Я реализовал подсчёты вероятностей с помощью алгоритма Монте-Карло
  
Алгоритм Монте-Карло широко используется для оценки вероятностей в играх с высокой степенью случайности, таких как покер. Это методика, основанная на случайном моделировании, которая позволяет получить приближенные решения сложных вероятностных и статистических задач.

### Почему Монте-Карло используется для подсчета вероятностей в покере
- Сложность покера: В покере существует огромное количество возможных комбинаций карт, особенно когда учтены руки всех игроков и оставшиеся карты в колоде. Точный подсчет вероятностей для всех возможных ситуаций зачастую невозможен в реальном времени.
- Скорость и точность: Алгоритм Монте-Карло позволяет быстро и достаточно точно оценить вероятность выигрыша или улучшения руки, путем многократного моделирования возможных исходов.
- Гибкость: Этот метод можно применять для различных игровых ситуаций, включая разные стадии игры (флоп, терн, ривер) и различные числа игроков.

### Алгоритм Монте-Карло для оценки вероятности выигрыша в покере включает следующие шаги:
- Ввод данных: Определяем известные карты.
- Моделирование: Многократно смоделируем возможные исходы игры, случайным образом распределяя оставшиеся карты среди всех игроков и борда.
- Оценка результатов: Определяем результат каждой симуляции и накапливаем статистику статистику.
- Подсчет вероятности: На основе статистики из смоделированных игр, оцениваем вероятности.

### Матожидание выигрыша
Если мы знаем вероятности своего выигрыша и ничьи, при учёте что все игроки доигрывают до конца, то можем посчитать матожидание суммы выигрыша.

Мы знаем:
- bank - текущая сумма в банке
- win - вероятность выигрыша
- tie - вероятность ничьи (В этом случае сумма делится между играками, у которых ничья. Вероятность того, что ничья будет больше чем между двумя игроками крайне мала. Поэтому будем считать, что при ничье мы забираем половину банка)
- players - количество игроков
- bet - сумма ставки

Тогда матожидание выигрыша можно посчитать по такой формуле: 

ME = (win + tie/2) * (bank + bet * (players - 1)) - (1 - tie - win) * bet

## Стратегии
Сначала разберём несколько покерных понятий

### Шансы банка
Шансы банка (pot odds) — это отношение текущего размера банка к стоимости предполагаемого колла (ставки, которую вам нужно сделать, чтобы остаться в игре). Это ключевая концепция в покере, которая помогает игрокам определить, оправдан ли риск продолжения игры на основании потенциального выигрыша.

#### Расчет шансов банка
- Определите текущий размер банка:
Сначала подсчитайте все деньги, находящиеся в банке, включая текущие ставки всех игроков.

- Определите стоимость колла:
Узнайте, сколько вам нужно поставить, чтобы остаться в игре (колл).

- Рассчитайте шансы банка:
Шансы банка = Стоимость колла / (Размер банка до текущего кона + ставки в этом кону без учёта вашей)
​
#### Интерпретация шансов банка
- Высокие шансы банка (например, 1:5): Если шансы банка высоки, это означает, что потенциальный выигрыш существенно превышает ваш текущий вклад. В таких случаях, даже если ваши шансы на улучшение руки невелики, возможно, стоит продолжить игру.
- Низкие шансы банка (например, 1:3): Если шансы банка низкие, это означает, что ваш потенциальный выигрыш лишь незначительно превышает ваш текущий вклад. В таких случаях вам нужны более высокие шансы на улучшение руки, чтобы оправдать продолжение игры.

### Шансы увеличения руки
Шансы увеличения руки (drawing odds) — это вероятность того, что ваша рука улучшится до выигрышной комбинации после открытия следующих карт (терн и/или ривер). Чтобы правильно оценить эти шансы, необходимо учитывать количество аутов (outs) и количество оставшихся карт в колоде (карты, которые не видны вам и другим игрокам).

Ауты — это карты, которые могут улучшить вашу руку до выигрышной.

#### Расчет увеличения руки
- Сначала нужно выбрать комбинации, который вы считаете выигрышными
- Затем нужно определить количество аутов для этих комбинаций
- Затем посчитайте количество неизвестных вам карт
- Вероятность увеличения руки = Количество аутов / Количество неизвестных карт

### Как этим пользоваться

Вам нужно сделать выбор на текущий момент игры: сделать колл или сбросить карты.

Что нужно делать, чтобы принять обоснованное решение
- Считаем шансы банка
- Считаем увеличение руки
- Сравниваем шансы: если шансы на улучшение руки выше шансов банка, это значит, что вам выгодно сделать колл, если нет, то лучше сбросить карты

## Как запустить и пользоваться приложением

### Запуск
- Перейти в директорию src (cd Pocker/src)
- Для запуска прописать команду (java Main)

не успел в jar-ник упаковать :(












