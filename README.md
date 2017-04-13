# java_hw16-17_spring

## VCS++
Крутая система контроля версий.

### Использование
Main парсит команды, переданные как аргументы, вызывает их аналоги в VCSManager.

* **init** - создается *.myvcs* в текущей директории, ветка *master* и делается первый коммит.
* **add** - добавляет файл в систему контроля версий.
* **remove_repository** - удаляется *.myvcs* вместе со всем ее содержимым.
* **commit** - создается новый коммит *VCSCommit* с текущим временем, именем пользователя и переданным описанием. В него входят все файлы из index в указанных версиях, которые в текущем коммите отсутствовали или отличались.
* **branch** - создается новая ветка *VCSBranch* с переданным названием, которое должно быть уникальным.
* **remove_branch** - ветка с переданным названием удаляется, удаляется соответствующий ей файл в *./myvcs/branches*
* **checkout** - принимает название коммита или ветки. Если это ветка, то переключается на нее. Если коммит, то создается новая ветка с названием этого коммита, затем переключается на нее.
* **merge** - принимает название ветки и сливает ее с текущей.
* **log** - выводит название текущей ветки, проходит рекурсивно по предкам текущего головного коммита, выводит информацию о них в консоль в хронологическом порядке.
* **help** - выводит список команд.
* **status** - выводит измененные/удаленные/недобавленные/staged файлы
* **reset** - сбрасывает состояние файла
* **rm** - файл удаляется как из репозитория, так и физически
* **clean** - удаляются все файлы, не добавленные в репозиторий

### Сборка
В папке hw2 вызовите `./gradlew createJar`. Собранный jar будет в hw3/build/libs.

### Внутреннее устройство

Проект состоит из библиотеки (она описывает саму систему) и консольного приложения для работы с ней (main). В библиотеке есть классы, описывающие разные объекты системы контроля версий, и вспомогательные классы для них. Основной класс - VCSManager - в нем заключены все основные команды рабоыт с репозиторием.