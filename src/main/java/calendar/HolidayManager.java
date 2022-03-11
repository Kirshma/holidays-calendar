package calendar;

import holiday.Holiday;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.*;
import java.util.*;

/**
 * Die Klasse HolidayManager verwaltet eine Menge an Feiertagen
 */
public class HolidayManager {


    private List<Holiday> holidays; // oder Collection<? extends Holiday> holidays

    private final Comparator<LocalDateTime> byHourDayMonth = Comparator.comparing(LocalDateTime::getMonth)
            .thenComparing(LocalDateTime::getDayOfMonth) //
            .thenComparing(LocalDateTime::getHour);

    private final Comparator<CalendarEntry> calendarEntry = Comparator.comparing(CalendarEntry::getDate) //
            .thenComparing(CalendarEntry::toString); //


    /**
     * Erstellt einen neuen HolidayManager mit einer Kopie der angegebenen Menge von Feiertagen.
     * @param holidays ist die Menge von Feiertagen
     */
    public HolidayManager(Collection<? extends Holiday> holidays) {
        if (!holidays.isEmpty()) {
            this.holidays = new ArrayList<>(holidays);
        }
    }

    /**
     * @return Liefert eine nicht veränderbare Ansicht auf die verwalteten Feiertage.
     */
    public Collection<Holiday> getHolidays() {
        return Collections.unmodifiableCollection(holidays);
    }


    /**
     * @param date ist das angegebene Datum
     * @return Liefert eine Liste von Kalendereinträgen mit allen Terminen von Feiertagen am angegebenen Datum
     * Die Ausgabe ist aufsteigend sortiert nach dem Namen des Feiertags.
     * */
    public List<CalendarEntry> calendarSheet(LocalDate date) {
        if (date==null) {
            throw new NullPointerException();
        } else {
            List<CalendarEntry> calendarEntries = new ArrayList<>();
            LocalDate aDayBeforeDate = LocalDate.from(date).minus(1, ChronoUnit.DAYS);
            for (Holiday holiday : holidays) {
                LocalDate oneHoliday = aDayBeforeDate.with(holiday);
                while (oneHoliday.isBefore(date.plus(1, ChronoUnit.DAYS))){
                    if (oneHoliday.equals(date)) {
                        calendarEntries.add(new CalendarEntry(oneHoliday, holiday));
                    }
                    oneHoliday= oneHoliday.with(holiday);
                }
            }
            calendarEntries.sort(this.calendarEntry);
            return calendarEntries;
        }
    }


    /**
     * @param month ist der angegebene Monat
     * @return eine Liste von Kalendereinträgen mit allen Terminen von Feiertagen im angegebenen Monat.
     * Die Ausgabe ist aufsteigend sortiert nach Datum, bei gleichem Datum nach dem Namen des Feiertags.
     */
    public List<CalendarEntry> calendarSheet(YearMonth month) {
        if (month==null) {
            throw new NullPointerException();
        } else {
            List<CalendarEntry> calendarEntries = new ArrayList<>();
            LocalDate firstDayOfAMonthInAYear = LocalDate.of(month.getYear(), 12,31).minus(1, ChronoUnit.YEARS);
            for (Holiday holiday : holidays) {
                LocalDate oneHoliday = firstDayOfAMonthInAYear.with(holiday);
                while (oneHoliday.isBefore(LocalDate.of(month.getYear(), month.getMonth(),1).plusMonths(1))){
                    if (oneHoliday.getMonthValue()==month.getMonthValue()) {
                        calendarEntries.add(new CalendarEntry(oneHoliday, holiday));
                    }
                    oneHoliday= oneHoliday.with(holiday);
                }
            }
            calendarEntries.sort(this.calendarEntry);
            return calendarEntries;
        }
    }


    /**
     * @param year ist das angegebene Jahr
     * @return eine Liste von Kalendereinträgen mit allen Terminen von Feiertagen im angegebenen Jahr.
     * Die Ausgabe ist aufsteigend sortiert nach Datum, bei gleichem Datum nach dem Namen des Feiertags.
     */
    public List<CalendarEntry> calendarSheet(int year) {
        if (year==0) {
            throw new IllegalArgumentException();
        } else {
            List<CalendarEntry> calendarEntries = new ArrayList<>();
            LocalDate dayBeforeAYear = LocalDate.of(year, 1, 1).minus(1, ChronoUnit.DAYS);
            LocalDate dayAfterYear = LocalDate.of(year, 1, 1).plus(1, ChronoUnit.YEARS);
            for (Holiday holiday : holidays) {
                LocalDate oneHoliday = dayBeforeAYear.with(holiday);
                while (oneHoliday.getYear()<dayAfterYear.getYear()){
                    if (oneHoliday.getYear()==year) {
                        calendarEntries.add(new CalendarEntry(oneHoliday, holiday));
                    }
                    oneHoliday= oneHoliday.with(holiday);
                }
            }
            calendarEntries.sort(this.calendarEntry);
            return calendarEntries;
        }
    }


    /**
     * @param from from (inklusives) Datum
     * @param to to (exklusives) Datum
     * @return eine Liste aller Termine von Feiertagen im Manager zwischen from (inklusive) und to (exklusive) angegebenen Daten.
     * Die Ausgabe ist aufsteigend sortiert nach Datum, bei gleichem Datum nach dem Namen des Feiertags.
     * Liegt "to" vor "from", wird eine IllegalArgumentException geworfen.
     */
    public List<CalendarEntry> getBetween(LocalDate from, LocalDate to) {
        if (to==null || from ==null){
            throw new NullPointerException();
        }
        else if (to.isBefore(from)) {
            throw new IllegalArgumentException("to war vor from");
        }
        else {
            List<CalendarEntry> calendarEntries = new ArrayList<>();
            LocalDate dayBeforeFrom = LocalDate.from(from).minus(1, ChronoUnit.DAYS);
            for (Holiday holiday: holidays){
                LocalDate oneHoliday = dayBeforeFrom.with(holiday);
                while (oneHoliday.isBefore(to.plus(1, ChronoUnit.DAYS))){
                    if ((oneHoliday.isAfter(from) || oneHoliday.equals(from))){
                        calendarEntries.add(new CalendarEntry(oneHoliday, holiday));
                    }
                    oneHoliday= oneHoliday.with(holiday);
                }

            }
            calendarEntries.sort(this.calendarEntry);
            return calendarEntries;
        }
    }

    /**
     * @param reference ist das angegebene Datum
     * @return Liefert zu jedem Feiertag im Manager den nächsten Termin nach dem angegebenen Datum.
     * Die Ausgabe ist aufsteigend sortiert nach Datum, bei gleichem Datum nach dem Namen des Feiertags.
     */
    public List<CalendarEntry> getNext(LocalDate reference) {
        if (reference==null) {
            throw new NullPointerException();
        } else {
            List<CalendarEntry> calendarEntries = new ArrayList<>();
            for (Holiday holiday : holidays) {
                LocalDate newHoliday = reference.with(holiday);
                CalendarEntry calendarEntry = new CalendarEntry(newHoliday, holiday);
                calendarEntries.add(calendarEntry);
            }
            calendarEntries.sort(this.calendarEntry);
            return calendarEntries;
        }
    }

    /**
     * Liefert eine Liste der nächsten n Termine von Feiertagen im Manager.
     * Es sollen insgesamt n Termine ausgegeben werden und nicht zu jedem Feiertag die nächsten n.
     * Die Ausgabe ist aufsteigend sortiert nach Datum, bei gleichem Datum nach dem Namen des Feiertags.
     * Ist n negativ wird eine IllegalArgumentException geworfen.
     * @param reference ist ab welchem Datum die nächsten n Termine ausgegeben werden sollen
     * @param n ist die Anzahl der nächsten Termine von Feiertagen
     * @return eine Liste der nächsten n Termine von Feiertagen im Manager.
     */
    public List<CalendarEntry> getNext(LocalDate reference, int n) {
        if (n <0){
            throw new IllegalArgumentException("n war negativ");
        }
        if (reference==null) {
            throw new NullPointerException();
        } else {
            List<CalendarEntry> calendarEntries = new ArrayList<>();
            LocalDate referenceToReference = LocalDate.from(reference);
            for (Holiday holiday: holidays){
                for (int i = 0; i<n ;i++){
                    if (!calendarEntries.contains(new CalendarEntry(referenceToReference.with(holiday), holiday))){
                        calendarEntries.add(new CalendarEntry(referenceToReference.with(holiday), holiday));
                    }
                    referenceToReference= referenceToReference.with(holiday);
                }
                referenceToReference = LocalDate.from(reference);
            }
            calendarEntries.sort(this.calendarEntry);
            System.out.println("blub");
            return new ArrayList<>(calendarEntries.subList(0,n));
        }
    }
}
