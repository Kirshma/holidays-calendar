package calendar;

import holiday.Holiday;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Die Klasse CalendarEntry stellt einen Eintrag (eine Kachel/einen Tag) in einem Kalender dar.
 */
public final class CalendarEntry {

    private final LocalDate date;
    private final Holiday holiday;

    public CalendarEntry(LocalDate date, Holiday holiday){
        if(date!=null && holiday!=null){
            this.date = date;
            this.holiday=holiday;
        }else throw new NullPointerException();
    }

    /**
     * @return Liefert den Feiertag dieses Eintrags zurück.
     */
    public Holiday getHoliday(){
        return this.holiday;
    }

    /**
     * @return Liefert das Datum dieses Eintrags zurück.
     */
    public LocalDate getDate(){
        return this.date;
    }

    /**
     * @return Liefert folgende Darstellung: "<date>: <holiday name>", wobei <date> als ISO_LOCAL_DATE formatiert sein soll.
     */
    public String toString(){
        return String.format("%s: %s", date.format(DateTimeFormatter.ISO_LOCAL_DATE), this.holiday.toString());
    }

    /**
     * Überschreiben von equals und hashCode, sodass zwei Einträge gleich sind, wenn ihr Datum und ihr Feiertag übereinstimmen.
     * @param other Object das auf Gleichheit überprüft werden soll
     * @return wahr, falls Objekte in Datum und Feiertag übereinstimmen.
     */
    @Override
    public boolean equals(Object other){
        if (other == this) return true;
        if (!(other instanceof CalendarEntry)) {
            return false;
        }
        CalendarEntry calendarEntry = (CalendarEntry) other;
        return this.date.equals(calendarEntry.date) && this.holiday.equals(calendarEntry.holiday);
    }


    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.date.hashCode();
        result = 31 * result + this.holiday.hashCode();
        return result;
    }
}
