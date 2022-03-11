package holiday;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Period;
import java.time.temporal.*;

/**
 * Die Klasse stellt einen "abhängigen" Feiertag dar.
 * Abhängig in dem Sinne, dass der Feiertag immer zeitversetzt zu einem anderen Feiertag steht und stattfindet.
 */
public final class DependentHoliday implements Holiday {

    private final String name;
    private final Holiday anchor;
    private final Period delta;


    /**
     * @param name des Feiertags
     * @param anchor Feiertag, der als Ankerpunkt genommen werden soll
     * @param delta ist die Zeitspanne zwischen Anker und abhängigem Feiertag
     */
    public DependentHoliday(String name, Holiday anchor, Period delta){
        if (name.isEmpty()){
            throw new IllegalArgumentException("name war leer");
        }
        else if (anchor==null || delta==null){
            throw new NullPointerException("anchor oder delta war null");
        }
        else {
            this.name = name;
            this.anchor = anchor;
            this.delta = delta;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Die Methode wandelt das Parameter Temporal in eines um, das den nächsten Termin des jeweiligen Feiertags (Instanz dieser Klasse) wiedergibt.
     * @param temporal ein beliebiges Temporal Objekt
     * @return ein verändertes Temporal Objekt, mit dem nächsten Termin der DependentHoliday Instanz
     */
    @Override
    public Temporal adjustInto(Temporal temporal) {
        /*
        Hier wird vorausgesetzt, dass der im Konstruktor übergebene "anchor"
        den nächsten Termin des Referenzfeiertags nach dem übergebenen liefert.
        Außerdem wird geprüft, ob "temporal" das Feld ChronoField.EPOCH_DAY unterstützt, da dies für die Umwandlung
        nach LocalDate erforderlich ist.
        Ist dies nicht der Fall, wird eine IllegalArgumentException mit aussagekräftiger Fehlermeldung geworfen.
        Das zurückgegebene Temporal unterstützt die gleichen (oder mehr) Felder wie das Übergebene.
         */

        if (!temporal.isSupported(ChronoField.EPOCH_DAY)) {
            throw new IllegalArgumentException();
        } else {
            LocalDate nonHolidayDate = LocalDate.from(temporal);
            LocalDate dependentHolidayOriginal;
            LocalDate minusDelta;
            dependentHolidayOriginal = LocalDate.from(temporal.with(anchor)).plus(delta);
            minusDelta = LocalDate.from(temporal.minus(delta).with(anchor).plus(delta));
            
            if (dependentHolidayOriginal.compareTo(minusDelta) == 0) {
                if (!dependentHolidayOriginal.isLeapYear() && MonthDay.from(dependentHolidayOriginal).equals(MonthDay.of(2,29))){
                                    return null;
                                }
                return temporal.with(ChronoField.YEAR, dependentHolidayOriginal.getYear())
                        .with(ChronoField.MONTH_OF_YEAR, dependentHolidayOriginal.getMonthValue())
                        .with(ChronoField.DAY_OF_MONTH, dependentHolidayOriginal.getDayOfMonth());
            }
            if (nonHolidayDate.compareTo(dependentHolidayOriginal) == 0) {
                if (!dependentHolidayOriginal.isLeapYear() && MonthDay.from(dependentHolidayOriginal).equals(MonthDay.of(2,29))){
                    return null;
                }
                return temporal.with(ChronoField.YEAR, minusDelta.getYear())
                        .with(ChronoField.MONTH_OF_YEAR, minusDelta.getMonthValue())
                        .with(ChronoField.DAY_OF_MONTH, minusDelta.getDayOfMonth());
            }
            if (nonHolidayDate.isAfter(dependentHolidayOriginal) && nonHolidayDate.isBefore(minusDelta)) {
                return temporal.with(ChronoField.YEAR, minusDelta.getYear())
                        .with(ChronoField.MONTH_OF_YEAR, minusDelta.getMonthValue())
                        .with(ChronoField.DAY_OF_MONTH, minusDelta.getDayOfMonth());
            }
            if (nonHolidayDate.isAfter(minusDelta) && nonHolidayDate.isBefore(dependentHolidayOriginal)) {
                return temporal.with(ChronoField.YEAR, dependentHolidayOriginal.getYear())
                        .with(ChronoField.MONTH_OF_YEAR, dependentHolidayOriginal.getMonthValue())
                        .with(ChronoField.DAY_OF_MONTH, dependentHolidayOriginal.getDayOfMonth());
            }
            if (nonHolidayDate.isBefore(minusDelta) && nonHolidayDate.isBefore(dependentHolidayOriginal)){
                return temporal.with(ChronoField.YEAR, minusDelta.getYear())
                        .with(ChronoField.MONTH_OF_YEAR, minusDelta.getMonthValue())
                        .with(ChronoField.DAY_OF_MONTH, minusDelta.getDayOfMonth());
            }
        }throw new IllegalArgumentException();
    }

    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object other){
        if (other == this) return true;
        if (!(other instanceof DependentHoliday)) {
            return false;
        }
        DependentHoliday dependentHoliday = (DependentHoliday) other;
        return this.name.equals(dependentHoliday.name);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.name.hashCode();
        //result = 31 * result + this.adjuster.hashCode();
        return result;
    }
}
