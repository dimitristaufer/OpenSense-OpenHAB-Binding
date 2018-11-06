# OpenSense ❤️ openHAB

[![Waffle.io - Columns and their card count](https://badge.waffle.io/dimitristaufer/opensense-openhab-master.svg?columns=all)](https://waffle.io/dimitristaufer/opensense-openhab-master)

![alt text](https://dimitristaufer.com/files/OpenSense_Banner-min.jpg)

An attempt to extent [ISE's](http://www.ise.tu-berlin.de/menue/information_systems_engineering/ "TU-Berlin ISE Homepage") [OpenSense.network](https://www.opensense.network "OpenSense Network's Homepage") by implementing bidirectional bindings with [openHAB](https://www.openhab.org "openHAB's Homepage"), one of the most popular Smart Home platforms.

# OpenSense.network 👉 openHAB 

User Story:
Ich wohne in einer Großstadt. Es ist daher für meine Gesundheit wichtig, mehrmals täglich meine Wohnung zu lüften. Die Stickoxidwerte in der Luft sind über den Tag verteilt sehr unterschiedlich. Daher gibt es bestimmte Uhrzeiten, an denen es sich überhaupt nicht lohnt zu lüften und anderen, an denen es sinnvoll wäre zu lüften. Ich suche nach einem “Thing” für mein openHAB System, welches mir in meinem Dashboard grafisch darstellt, wie sich die Stickoxidwerte über den Tag verteilt verändern. Auf diese Weise kann ich gezielter lüften und muss mir keine Gedanken über die Luftverschmutzung mehr machen.

User Story:
Besonders im Frühjahr leide ich stark unter meiner Pollenallergie. In meiner Wohnung nutze ich deswegen einen Luftreiniger, wie z.B. den Dyson Pure Link oder Xiaomi Mi Air Purifier. Für beide Geräte gibt es ein openHAB Binding mit dem ich den Luftstrom regulieren kann. Die Luftreiniger nutzen einen HEPA Filter aus Borosilikatglasfasern, der nach etwa 4000 Stunden für 60 Euro ausgetauscht werden muss. Zudem ist mein Stromverbrauch seit dem Einsatz des Reinigers stark angestiegen, da der Filter praktisch rund um die Uhr läuft.
Ich bin auf der Suche nach einer automatisierten Lösung, die den Luftstrom reguliert, je nach dem ob die Belastung der Pollenart, auf die ich allergisch reagiere, hoch oder niedrig ist.
—> Über das OpenSense Binding im OpenSense Dashboard kann der Nutzer/die Nutzerin angeben, auf welche Pollenarten er/sie allergisch reagiert. Das Binding ruft die notwendigen Daten im OpenSense Netzwerk ab und steuert den Luftreiniger vollautomatisch über die Bindings der jeweiligen Hersteller.

Which measurements can OpenSense provide to openHAB?

* Unordered list item
* Unordered list item
* Unordered list item

# openHAB 👉 OpenSense.network 

User Story1:
Aufgrund meiner Arbeit werde ich zu beginn des kommenden Jahres in eine Großstadt ziehen. Bei der Wohnungssuche fällt es mir schwer herauszufinden unter welcher Lärmbelästigung spezifische Stadtgebiete und Wohnungsblocks leiden. Baulärm, Hundegebell, Diskotheken und Verkehrslärm. Ich bin sehr lärmempfindlich und suche nach einer einfachen Möglichkeit, die Lärmbelästigung für eine bestimmte Wohnung einzusehen.
-> viele Wetterstationen, wie beispielsweise die NWS01-EC Netatmo Weather Station messen mit einem Sonometer alle 5 Minuten die Lautstärke in einem Bereich von 35 dB bis 120 dB. Diese Werte können über das Netatmo openHAB Binding abgerufen werden und in das OpenSense Netzwerk eingespeist werden.

Which openHAB things can provide which measurements to OpenSense?

* Unordered list item
* Unordered list item
* Unordered list item

# Things

> Things are the entities that can physically be added to a system and which can potentially provide many functionalities in one. It is important to note that Things do not have to be devices, but they can also represent a web service or any other manageable source of information and functionality. From a user perspective, they are relevant for the setup and configuration process, but not for the operation.

# Roadmap

* Unordered list item
* Unordered list item
* Unordered list item
