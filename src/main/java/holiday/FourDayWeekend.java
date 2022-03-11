package holiday;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.*;
import java.util.*;

/**
 * Die Klasse FourDayWeekend stellt einen Brückentag dar, mithilfe dessen ein Vier-Tage-Wochenende gefunden werden kann.
 * Ein Brückentag ist ein Montag, dem ein registrierter Feiertag folgt, bzw. ein Freitag,
 * dem ein solcher voranging, der aber selbst kein Feiertag ist.
 * Nimmt man sich an diesem Brückentag einen Tag Urlaub, hat man vier freie Tage erzielt.
 */
public final class FourDayWeekend implements Holiday {

    private final Collection<Holiday> holidays;
    private LocalDate randomDate = LocalDate.of(1800, 1, 1);
    private Temporal temporal;
    private final Comparator<LocalDate> byDayMonthYear = Comparator.comparing(LocalDate::getYear)
            .thenComparing(LocalDate::getMonth)
            .thenComparing(LocalDate::getDayOfMonth);
    private final List<LocalDate> listOfCandidates = new ArrayList<>();
    private final List<LocalDate> feiertagsListe = new ArrayList<>();

    /**
     * Konstruktor erstellt ein neues FourDayWeekend mit einer Kopie der übergebenen Collection von Feiertagen.
     * @param holidays ist eine Collection von Feiertagen
     */
    public FourDayWeekend(Collection<Holiday> holidays) {
        if (holidays == null || holidays.isEmpty()) {
            throw new NullPointerException();
        }
        this.holidays = new ArrayList<>(holidays);
    }

    /**
     * Konstruktor erstellt ein neues holiday.FourDayWeekend, das alle übergebenen Einträge beinhaltet.
     * @param holidays besitzt einen oder mehrere Holidays
     */
    public FourDayWeekend(Holiday... holidays) {
        if (holidays == null) {
            throw new NullPointerException();
        }
        this.holidays = new ArrayList<>();
        (this.holidays).addAll(Arrays.asList(holidays));
    }

    /**
     * @return Liefert eine unveränderliche Ansicht auf die enthaltenen Feiertage.
     */
    public Collection<Holiday> getHolidays() {
        return Collections.unmodifiableCollection(this.holidays);
    }


    /**
     * @return gibt immer "4 Day Weekend" aus.
     */
    @Override
    public String getName() {
        return "4 Day Weekend";
    }


    /**
     * Wandelt das übergebene Temporal-Objekt in eines mit dem Datum des nächsten Brückentags um.
     * Mit "nächstem" ist hier ein Termin, der nach dem übergebenen liegt, gemeint!
     * Ist die Liste der registrierten Feiertage leer, oder wird im Zeitraum bis 100 Jahre nach temporal kein Brückentag gefunden,
     * soll null zurückgegeben werden.
     * @param temporal ist ein übergebenes Temporal-Objekt, das umgewandelt werden soll
     * @return liefert ein umgewandeltes Temporal mit dem Datum des nächsten Brückentags
     */
    @Override
    public Temporal adjustInto(Temporal temporal) {
        /*
        Hinweise:
        Es kann passieren, dass temporal bereits ein Feiertag und Donnerstag ist.
        In diesem Fall ist Freitag, so er denn kein Feiertag ist, der nächste Brückentag.
        "temporal" selbst muss also in die Suche nach Feiertags Kandidaten einbezogen werden.
        Man muss darauf achten, dass es Feiertage gibt, die nicht im Jahresrhythmus auftreten, z.B. 29. Februar,
        Vollmond, Dienstag. Feiertage müssen entsprechend auch innerhalb eines Jahres mehrfach ausgewertet werden.
        Ist der Tag nach temporal ein Feiertag und Dienstag, so ist temporal zwar eventuell ein Brückentag,
        darf aber nie zurückgegeben werden, da immer ein Ergebnis zurückgegeben werden muss, das nach temporal liegt.
        Für das Zwischenspeichern der Kandidaten bietet sich eine sortierbare Datenstruktur an.
        Verwenden Sie nicht Period.getDays(), wenn Sie die Anzahl an Tagen, die zwischen zwei Daten liegen, herausfinden wollen.
        Diese Methode liefert bei einem Zeitabstand von zwei Monaten und einem Tag "1" zurück.
        Das Ergebnis dieser Methode darf kein Feiertag sein.
        Wurde in den 100 Jahren nach temporal kein Brückentag gefunden, soll die Suche abgebrochen werden.
        Achten Sie auf eine effiziente Implementierung,
        da die automatischen Tests nach einer festen Zeitspanne abgebrochen werden und dann als nicht bestanden gelten.
        Das zurückgegebene Temporal soll die gleichen (oder mehr) Felder unterstützen wie das übergebene.
         */
        if (!temporal.isSupported(ChronoField.EPOCH_DAY)) {
            throw new IllegalArgumentException("Epoch Day wird nicht unterstützt");
        }
        if (this.holidays.isEmpty()) {
            return null;
        } else {
            int rekursionsCount = 0;
            listOfCandidates.clear();
            feiertagsListe.clear();
            randomDate = LocalDate.from(temporal);
            this.temporal = temporal;
            LocalDate result = calc();
            if (result == null){
                return null;
            }else return temporal.with(result);
        }
    }

    /**
     * Überschriebene equals und hashCode Methoden von FourDayWeekend,
     * sodass zwei Instanzen genau dann gleich sind, wenn sie die gleichen Feiertage berücksichtigen.
     * @param other ist das zu vergleichende Objekt mit dieser Klassen-Instanz
     * @return liefert wahr, wenn beide Objekte die gleichen Feiertage berücksichtigen
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof FourDayWeekend)) {
            return false;
        }
        FourDayWeekend fourDayWeekend = (FourDayWeekend) other;
        return this.holidays.containsAll(fourDayWeekend.holidays) && this.holidays.size() == fourDayWeekend.holidays.size();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result;
        for (Holiday holiday : holidays) {
            result = result + holiday.hashCode();
        }
        return result;
    }


    /*
    ------ Ab hier sehr unordentlicher Hilfsfunktions-Code, der überarbeitet werden müsste -------
    Beispielsweise große Funktionen wie calc() zerschlagen in mehrere Kleinere, mit einer einzigen spezifischen Aufgabe.
    Die Namensgebung, Verschachtelung von if-else verbessern, vielleicht durch geeignete Design Patterns in Objekte umwandeln)
    und vor allem Refactoring Maßnahmen durchführen.
     */

    private LocalDate calc(){
        LocalDate maximum = LocalDate.from(temporal).plusYears(100);
        randomDate = LocalDate.from(temporal);
        LocalDate lastElement;
        int iterationMonths = 1;
        do {
            LocalDate iterationDate = randomDate.plusMonths(iterationMonths);
            for (Holiday holiday : holidays) {
                //Spezialfälle
                if (randomDate.with(holiday).with(holiday).equals(randomDate)) {
                    return null;
                }
                if (randomDate.with(holiday).getDayOfWeek().equals(DayOfWeek.THURSDAY))
                    if (randomDate.with(holiday).equals(randomDate.plusDays(1))
                            && randomDate.with(holiday).with(holiday).equals(randomDate.plusDays(2))) {
                        return null;
                    }
                //prüfe ob derzeitiges Datum ein Feiertag und Donnerstag
                if (randomDate.minusDays(1).with(holiday).equals(randomDate) && randomDate.getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
                    boolean isFridayAHoliday = false;
                    for (Holiday toBeSure: holidays){
                        if (randomDate.with(toBeSure).equals(randomDate.plusDays(1))){
                            isFridayAHoliday = true;
                        }
                    }
                    if (!isFridayAHoliday){
                        return randomDate.plusDays(1);
                    }
                }
                //wenn oberes nicht, dann adde bis Datum +4 Monate an Feiertagen, füge Sie in Liste
                LocalDate randomDateBefore = LocalDate.from(randomDate);
                while (randomDate.with(holiday).isBefore(iterationDate.plusDays(1)) || randomDate.with(holiday).equals(iterationDate.plusDays(1))) {
                    if (!feiertagsListe.contains(randomDate.with(holiday))) {
                        feiertagsListe.add(randomDate.with(holiday));
                    }
                    randomDate = randomDate.with(holiday);
                }
                randomDate = LocalDate.from(randomDateBefore);
            }
                //prüfe bisherige Liste
                feiertagsListe.sort(this.byDayMonthYear);
                for (LocalDate potentialHoliday : feiertagsListe) {
                    if (potentialHoliday.getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
                        LocalDate friday =potentialHoliday.plusDays(1);
                            if (!feiertagsListe.contains(friday)) {
                                boolean isFridayAHoliday = false;
                                for (Holiday toBeSure: holidays){
                                    if (potentialHoliday.with(toBeSure).equals(potentialHoliday.plusDays(1))){
                                        isFridayAHoliday = true;
                                    }
                                }
                                if (!isFridayAHoliday){
                                    return friday;
                                }
                            }
                    }
                    if (potentialHoliday.getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
                        LocalDate monday = potentialHoliday.minusDays(1);
                            if (!feiertagsListe.contains(monday) && monday.isAfter(randomDate)) {
                                    boolean isMondayAHoliday = false;
                                    for (Holiday toBeSure: holidays){
                                        LocalDate sunday = potentialHoliday.minus(2, ChronoUnit.DAYS);
                                        if (sunday.with(toBeSure).equals(monday)){
                                            isMondayAHoliday = true;
                                        }
                                    }
                                    if (!isMondayAHoliday){
                                        return monday;
                                    }
                            }
                    }
                }
                if (!feiertagsListe.isEmpty()){
                    lastElement = LocalDate.from(feiertagsListe.get(feiertagsListe.size()-1));
                }else {
                    lastElement = iterationDate;
                }
                randomDate = LocalDate.from(iterationDate);
                feiertagsListe.clear();
            }while (lastElement.isBefore(maximum));
        return null;
        }


    private void fillListWithHolidays(LocalDate maxHoliday){
        for (Holiday holiday : holidays) {
            if (!feiertagsListe.contains(randomDate.minus(1, ChronoUnit.DAYS).with(holiday))) {
                feiertagsListe.add(randomDate.minus(1, ChronoUnit.DAYS).with(holiday));
            }
            if (!feiertagsListe.contains(randomDate.with(holiday))) {
                feiertagsListe.add(randomDate.with(holiday));
            }
            if (!feiertagsListe.contains(randomDate.with(holiday).with(holiday))) {
                feiertagsListe.add(randomDate.with(holiday).with(holiday));
            }
            if (maxHoliday != null) {
                while (randomDate.with(holiday).isBefore(maxHoliday) || randomDate.with(holiday).equals(maxHoliday)) { //&& randomDate.with(holiday).until(maxHoliday, ChronoUnit.DAYS)<200
                    if (!feiertagsListe.contains(randomDate.with(holiday))) {
                        feiertagsListe.add(randomDate.with(holiday));
                    }
                    randomDate = randomDate.with(holiday);
                }
                feiertagsListe.add(randomDate.with(holiday));
                randomDate = LocalDate.from(temporal);
            }
        }
    }


    private LocalDate isTemporalAHoliday() {
        //Es kann passieren, dass temporal bereits ein Feiertag und Donnerstag ist. In diesem Fall ist Freitag, so er denn kein Feiertag ist, der nächste Brückentag.
        // temporal selbst muss also in die Suche nach Feiertags Kandidaten einbezogen werden.
        //Ist der Tag nach temporal ein Feiertag und Dienstag, so ist temporal zwar eventuell ein Brückentag, darf aber nie zurückgegeben werden, da immer ein Ergebnis zurückgegeben werden muss, das nach temporal liegt.
        if (DayOfWeek.from(temporal).equals(DayOfWeek.THURSDAY)) {
            LocalDate donnerstag = LocalDate.from(temporal);
            if (feiertagsListe.contains(donnerstag) && !feiertagsListe.contains(donnerstag.plus(1, ChronoUnit.DAYS))) {
                listOfCandidates.add(donnerstag.plus(1, ChronoUnit.DAYS));
                return donnerstag.plus(1, ChronoUnit.DAYS);
            }
        }
        return null;
    }

    private void searchListForHolidayThursOrTuesday(Collection<LocalDate> concreteHolidayList) {
        randomDate = LocalDate.from(temporal);
        for (LocalDate holiday : concreteHolidayList) {
            if (holiday.isAfter(randomDate)) {
                if (DayOfWeek.from(holiday).equals(DayOfWeek.THURSDAY)) {
                    if (!concreteHolidayList.contains(holiday.plus(1, ChronoUnit.DAYS))) {
                        listOfCandidates.add(holiday.plus(1, ChronoUnit.DAYS));
                    }
                }
                if (DayOfWeek.from(holiday).equals(DayOfWeek.TUESDAY)) {
                    if (!feiertagsListe.contains(holiday.minus(1, ChronoUnit.DAYS)) && !holiday.minus(1, ChronoUnit.DAYS).equals(randomDate)) {
                        listOfCandidates.add(holiday.minus(1, ChronoUnit.DAYS));
                    }
                }
            }
        }
        if (listOfCandidates.isEmpty()) {
            randomDate = LocalDate.from(this.temporal);
            LocalDate randomDate100 = LocalDate.from(randomDate.plus(3, ChronoUnit.YEARS));
            LocalDate lastInList = feiertagsListe.get(feiertagsListe.size()-1);
            while (listOfCandidates.isEmpty() && lastInList.isBefore(randomDate100)) {
                ArrayList<LocalDate> newHolidayList = new ArrayList<>(feiertagsListe.subList(holidays.size(),feiertagsListe.size()-1));
                for (Holiday holiday : holidays) {
                    for (int i = 0; i < 100; i++) {
                        if (!newHolidayList.contains(randomDate.minus(1, ChronoUnit.DAYS).with(holiday).with(holiday))) {
                            newHolidayList.add((randomDate.minus(1, ChronoUnit.DAYS).with(holiday).with(holiday)));
                        }
                        randomDate = LocalDate.from(randomDate.minus(1, ChronoUnit.DAYS).with(holiday).with(holiday));
                    }
                    newHolidayList.add(randomDate.minus(1, ChronoUnit.DAYS).with(holiday).with(holiday));
                    randomDate = LocalDate.from(lastInList);
                }
                for (LocalDate holiday : newHolidayList.subList(0, newHolidayList.size()-2)) {
                    if (holiday.isAfter(LocalDate.from(this.temporal))) {
                        if (DayOfWeek.from(holiday).equals(DayOfWeek.THURSDAY)) {
                            for (Holiday holiday1: holidays){
                                if (holiday.with(holiday1).equals(holiday.plus(1, ChronoUnit.DAYS))){
                                    listOfCandidates.add(holiday.plus(1, ChronoUnit.DAYS));
                                }
                            }
                        }
                        if (DayOfWeek.from(holiday).equals(DayOfWeek.TUESDAY)) {
                            for (Holiday holiday1: holidays){
                                if (holiday.minus(2, ChronoUnit.DAYS).with(holiday1).equals(holiday.minus(1, ChronoUnit.DAYS))){
                                    listOfCandidates.add(holiday.minus(1, ChronoUnit.DAYS));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void wiederholeMitMonaten(Temporal temporal) {

        LocalDate monatlich = LocalDate.from(temporal).plus(1, ChronoUnit.MONTHS);
        ArrayList<LocalDate> newConcreteHolidayList = new ArrayList<>();
        for (Holiday holiday : this.holidays) {
            if (!feiertagsListe.contains(monatlich.minus(1, ChronoUnit.DAYS).with(holiday))) {
                feiertagsListe.add(monatlich.minus(1, ChronoUnit.DAYS).with(holiday));
                newConcreteHolidayList.add(monatlich.minus(1, ChronoUnit.DAYS).with(holiday));
            }
            if (!feiertagsListe.contains(monatlich.with(holiday))) {
                feiertagsListe.add(monatlich.with(holiday));
                newConcreteHolidayList.add(monatlich.with(holiday));
            }
            if (!feiertagsListe.contains(monatlich.with(holiday).with(holiday))) {
                feiertagsListe.add(monatlich.with(holiday).with(holiday));
                newConcreteHolidayList.add(monatlich.with(holiday).with(holiday));
            }
            if (!feiertagsListe.contains(monatlich.with(holiday).with(holiday).with(holiday))) {
                feiertagsListe.add(monatlich.with(holiday).with(holiday).with(holiday));
            }

        }
        searchListForHolidayThursOrTuesday(newConcreteHolidayList);
    }

    private void wiederholeMitJahren(Temporal temporal) {

        LocalDate jahre = LocalDate.from(temporal).plus(1, ChronoUnit.YEARS);
        ArrayList<LocalDate> newConcreteHolidayList = new ArrayList<>();
        for (Holiday holiday : this.holidays) {
            if (!feiertagsListe.contains(jahre.minus(1, ChronoUnit.DAYS).with(holiday))) {
                feiertagsListe.add(jahre.minus(1, ChronoUnit.DAYS).with(holiday));
                newConcreteHolidayList.add(jahre.minus(1, ChronoUnit.DAYS).with(holiday));
            }
            if (!feiertagsListe.contains(jahre.with(holiday))) {
                feiertagsListe.add(jahre.with(holiday));
                newConcreteHolidayList.add(jahre.with(holiday));
            }
            if (!feiertagsListe.contains(jahre.with(holiday).with(holiday))) {
                feiertagsListe.add(jahre.with(holiday).with(holiday));
                newConcreteHolidayList.add(jahre.with(holiday).with(holiday));
            }
        }
        searchListForHolidayThursOrTuesday(newConcreteHolidayList);
    }
}

