package holiday;

import MoonGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;


/**
 * Enum Klasse zum Ausgeben von Mondphasen.
 * Greift auf die zur Verfügung gestellte Library zur Berechnung der Mondphasen zurück.
 */
public enum MoonPhases implements Holiday {
    NEW_MOON (MoonGenerator.PhaseOfTheMoon.NEW_MOON),
    THIRD_QUARTER (MoonGenerator.PhaseOfTheMoon.THIRD_QUARTER),
    FULL_MOON (MoonGenerator.PhaseOfTheMoon.FULL_MOON),
    FIRST_QUARTER (MoonGenerator.PhaseOfTheMoon.FIRST_QUARTER);


    private final MoonGenerator.PhaseOfTheMoon phaseOfTheMoon;


    MoonPhases(MoonGenerator.PhaseOfTheMoon phaseOfTheMoon){
        this.phaseOfTheMoon = phaseOfTheMoon;
    }


    @Override
    public String getName() {

        if (this==NEW_MOON){
            return "\u25CF";
        }
        else if (this==THIRD_QUARTER){
            return "\u25d1";
        }
        else if (this==FULL_MOON){
            return "\u25cb";
        }
        else if (this==FIRST_QUARTER){
            return "\u25d0";
        }
        return null;
    }


    @Override
    public Temporal adjustInto(Temporal temporal) {

        if (!temporal.isSupported(ChronoField.EPOCH_DAY)){
            throw new IllegalArgumentException();
        }else{
            if (temporal.isSupported(ChronoField.HOUR_OF_DAY)) {
                LocalDateTime localDateTime = MoonGenerator.calculateMoon(LocalDate.from(temporal), phaseOfTheMoon).truncatedTo(ChronoUnit.MINUTES);
                localDateTime.truncatedTo(ChronoUnit.MINUTES);
                if (LocalDate.from(localDateTime).isAfter(LocalDate.from(LocalDateTime.from(temporal))) && !LocalDate.from(localDateTime).equals(LocalDate.from(LocalDateTime.from(temporal)))){
                    return temporal.with(localDateTime);
                }
                localDateTime = MoonGenerator.calculateMoon(LocalDate.from(temporal).plus(20,ChronoUnit.DAYS), phaseOfTheMoon).truncatedTo(ChronoUnit.MINUTES);
                return temporal.with(localDateTime);
            }
            else{
                LocalDate localDate = LocalDate.from(MoonGenerator.calculateMoon(LocalDate.from(temporal), phaseOfTheMoon).truncatedTo(ChronoUnit.MINUTES));
                if (localDate.isAfter(LocalDate.from(temporal)) && !localDate.equals(LocalDate.from(temporal))){
                    return temporal.with(ChronoField.YEAR, localDate.getYear())
                            .with(ChronoField.MONTH_OF_YEAR, localDate.getMonthValue())
                            .with(ChronoField.DAY_OF_MONTH, localDate.getDayOfMonth());
                }
                localDate = LocalDate.from(MoonGenerator.calculateMoon(LocalDate.from(temporal).plus(20,ChronoUnit.DAYS), phaseOfTheMoon).truncatedTo(ChronoUnit.MINUTES));
                return temporal.with(ChronoField.YEAR, localDate.getYear())
                        .with(ChronoField.MONTH_OF_YEAR, localDate.getMonthValue())
                        .with(ChronoField.DAY_OF_MONTH, localDate.getDayOfMonth());
            }
        }
    }
}
