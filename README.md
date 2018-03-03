Verbos Español
=================

<a href='https://play.google.com/store/apps/details?id=com.xengar.android.verbosespanol'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height=90px/></a>

![Scheme](/readmeImages/Screenshot_20170821-112845x.png)


Android application to learn spanish verb tenses.


Pre-requisites
--------------
- Android SDK 25 or Higher
- [Color Picker Module](http://www.materialdoc.com/color-picker/)


References
----------
- http://www.rae.es/diccionario-panhispanico-de-dudas/apendices/modelos-de-conjugacion-verbal
- http://dle.rae.es/?id=aBpHmn0
- http://www.wordreference.com/conj/EsVerbs.aspx?v=trabajar
- http://conjugador.reverso.net/conjugacion-espanol-verbo-cantar.html

Most used verbs lists:

- http://www.solosequenosenada.com/gramatica/spanish/listado_verbos.php
- https://www.idiomax.com/es/spanish-verb-list.aspx
- https://lenguajeyotrasluces.com/2016/01/11/lista-de-verbos-mas-usados-en-espanol/
- https://www.vocabulix.com/conjugacion2/a_spanish.html
- https://quizlet.com/117397241/los-25-verbos-mas-usados-en-espanol-parte-4-20-verbos-flash-cards/
- https://www.quia.com/jg/514331list.html
- https://quizlet.com/504750/100-verbos-mas-usados-en-espanol-flash-cards/
- http://www.practicaespanol.com/wp-content/uploads/Verbos-espa%C3%B1oles-m%C3%A1s-frecuentes.pdf
- https://quizlet.com/6282091/lista-completa-de-verbos-500-esp-4-flash-cards/
- https://www.quia.com/jg/514331list.html

Verbs to check
--------------
none yet



# Set up

Color Picker Module
-------------------

1.  Download repository from
  ```
  git clone https://android.googlesource.com/platform/frameworks/opt/colorpicker  (preferred) or
  git clone https://xengar@bitbucket.org/xengar/colorpicker.git
  ```

2. Import a new module in android studio with the New/import module menu,
   choosing the path where the project was cloned.
   Remove the empty "colorpicker" directory if needed.

3. Add dependency to app/build.gradle
   ```
   apply plugin: 'com.android.application'

   android {
       ...
   }

   dependencies {
       compile project(':colorpicker')
       ...
   }
   ```

4. Add compileSdkVersion and buildToolsVersion in colorpicker/build.gradle to avoid
   Error buildToolsVersion is not specified. Try to use latest versions.
   ```
    apply plugin: 'com.android.library'

    android {

        compileSdkVersion 26
        buildToolsVersion "26.0.3"

        sourceSets.main {
            manifest.srcFile 'AndroidManifest.xml'
            java.srcDirs = ['src']
            res.srcDirs = ['res']
        }
    }
   ```

5. Commit the modified changes in the colorpicker module.
   (There is no remote repository to push. Keep it local.)
   ```
   cd verbosespanol/colorpicker
   git add -A
   git commit -m "Import colorpicker module into Verbs project"
   ```

## License

Copyright 2018 Angel Garcia

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.


