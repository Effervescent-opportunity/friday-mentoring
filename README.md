# friday-mentoring

Выбор всего должен быть обоснован. Нужно писать в комменты, какие альтернативы рассматривались и почему сделан конкретный выбор.

Нужно закоммитить в пятницу до 12:00 МСК.

Выбран Spring Web, потому что Spring Reactive Web я вообще не знаю.
Java 17 liberica because it's on real work
gradle 8.2 because it's newest
alpine linux in docker because it's mininal
no exception handling because I don't have enough time

questions
мы считаем что на сервере точное время? не надо ни с кем синхронизироваться и проверять?


//gradle bootJar makes ....jar - 18M
it has a lot of jars (in BOOT-INF\lib), springboot classes (in org\sprin..) 
my classes are in BOOT-INF/classes/{com...}
application.properties are in BOOT-INF/classes
it has BOOT-INF/classpath.idx and BOOT-INF/layers.idx
//gradle jar makes ...-plain.jar -4kb
my classes are in root {com...}
application.properties is in root
is has static and templates folders
both have META-INF\MANIFEST.MF

//gradle bootBuildImage tries to build image and fails with permission denied^
on Connection to the Docker daemon at 'localhost' failed with error "[13] Permission denied"; ensure the Docker daemon is running and accessible
