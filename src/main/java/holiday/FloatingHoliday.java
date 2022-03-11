package holiday;

import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;


/**
 * Die Klasse FloatingHoliday stellt einen "beweglichen" Feiertag wie z.B Ostern dar.
 * Beweglich heißt, dass der Feiertag jedes Jahr potentiell auf einen anderen Tag bzw. auf ein anderes Datum fällt.
 */
public final class FloatingHoliday implements Holiday {

    private final String name;
    private final TemporalAdjuster adjuster;

    /**
     * @param name ist der Name des Feiertags
     * @param adjuster gibt für ein gegebenes Temporal den nächsten Termin des beweglichen Feiertags aus (siehe adjustInto())
     */
    public FloatingHoliday(String name, TemporalAdjuster adjuster){
        if (name.isEmpty()){
            throw new IllegalArgumentException("Der Name des Feiertags war leer");
        }
        if (adjuster==null){
            throw new NullPointerException("Adjuster war nicht vorhanden");
        }
        else {
            this.name = name;
            this.adjuster=adjuster;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Die Methode wandelt das Parameter Temporal in eines um, das den nächsten Termin des jeweiligen Feiertags (Instanz dieser Klasse) wiedergibt.
     * @param temporal ein beliebiges Temporal Objekt
     * @return ein verändertes Temporal Objekt, mit dem nächsten Termin der FloatingHoliday Instanz
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

        if (!temporal.isSupported(ChronoField.EPOCH_DAY)){
            throw new IllegalArgumentException("Temporal unterstützt nicht Epoch Day");
        }else{
            return temporal.with(adjuster);
        }
    }

    public String toString(){
        return this.name;
    }

    @Override
    public boolean equals(Object other){
        if (other == this) return true;
        if (!(other instanceof FloatingHoliday)) {
            return false;
        }
        FloatingHoliday floatingHoliday = (FloatingHoliday) other;
        return this.name.equals(floatingHoliday.name);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.name.hashCode();
        //result = 31 * result + this.adjuster.hashCode();
        return result;
    }

}
