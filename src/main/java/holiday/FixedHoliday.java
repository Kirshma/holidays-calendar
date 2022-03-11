package holiday;

import java.time.MonthDay;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.*;
import java.time.temporal.*;


/**
 * Die Klasse stellt einen "fixierten" Feiertag dar.
 * Fixiert bedeutet, dass der Feiertag immer an einem bestimmten Tag stattfindet.
 * Ein Beispiel ist der Heilige Abend am 24.12 eines jeden Jahres.
 */
public final class FixedHoliday implements Holiday {

    private final String name;
    private final MonthDay date;

    /**
     * @param name Name des Feiertags
     * @param date die Kombination aus Tag und Monat, an dem der FixedHoliday jedes Jahr stattfindet, z.B am 24.12
     */
    public FixedHoliday(String name, MonthDay date) {
        if (date == null) {
            throw new NullPointerException();
        }
        //Der 29. Februar findet nur in einem Schaltjahr statt und ist daher kein fixierter Feiertag.
        else if (name.isEmpty() || date.equals(MonthDay.of(2, 29))) {
            throw new IllegalArgumentException();
        } else {
            this.name = name;
            this.date = date;
        }
    }


    @Override
    public String getName() {
        return this.name ;
    }

    /**
     * Die Methode wandelt das Parameter Temporal in eines um, das den nächsten Termin des jeweiligen Feiertags (Instanz dieser Klasse) wiedergibt.
     * @param temporal ein beliebiges Temporal Objekt
     * @return ein verändertes Temporal Objekt, mit dem nächsten Termin der FixedHoliday Instanz
     */
    @Override
    public Temporal adjustInto(Temporal temporal) {
        if (!temporal.isSupported(ChronoField.EPOCH_DAY)) {
            throw new IllegalArgumentException("Parameter temporal unterstützt nicht das Feld Epoch Day");
        } else {
            Temporal temp = temporal;
            try {
                if (temp.get(ChronoField.MONTH_OF_YEAR) == (this.date.getMonthValue()) && temp.get(ChronoField.DAY_OF_MONTH) >= this.date.getDayOfMonth()
                        || temp.get(ChronoField.MONTH_OF_YEAR) > this.date.getMonthValue()) {
                    temp = temp.with(ChronoField.MONTH_OF_YEAR, this.date.getMonthValue())
                            .with(ChronoField.DAY_OF_MONTH, this.date.getDayOfMonth())
                            .plus(1, ChronoUnit.YEARS);
                } else {
                    temp = temp.with(ChronoField.MONTH_OF_YEAR, this.date.getMonthValue())
                            .with(ChronoField.DAY_OF_MONTH, this.date.getDayOfMonth());
                }
            } catch (DateTimeException ex) {
                System.out.println("Adjustinto war nicht erfolgreich in " + this.getClass().toString());
            }
            return temp;
        }
    }


    public String toString(){
        return name;
    }


    @Override
    public boolean equals(Object other){
        if (other == this) return true;
        if (!(other instanceof FixedHoliday)) {
            return false;
        }
        FixedHoliday fixedHoliday = (FixedHoliday) other;
        return this.name.equals(fixedHoliday.name);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.name.hashCode();
        //result = 31 * result + this.date.hashCode();
        return result;
    }
}
