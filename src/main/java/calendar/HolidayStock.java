package calendar;

import holiday.*;

import java.time.MonthDay;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static holiday.Generators.*;
import static java.time.DayOfWeek.*;
import static java.time.Month.*;
import static java.util.Arrays.asList;

/**
 * Die Klasse HolidayStock stellt drei DatenSamples zum Füllen des Kalenders bereit.
 */
public final class HolidayStock {

    /**
     * Die Methode sampleA() stellt sämtliche bekannte Feiertage zur Verfügung.
     * @return ist eine Liste an bekannten Feiertagen.
     */
    public static List<Holiday> sampleA() {
        return asList(
                new FixedHoliday("New Year's Day", MonthDay.of(JANUARY, 1)),
                new FixedHoliday("Epiphany", MonthDay.of(JANUARY, 6)),
                new FixedHoliday("Darwin Day", MonthDay.of(FEBRUARY, 12)),
                new FixedHoliday("Valentine's Day", MonthDay.of(FEBRUARY, 14)),
                new FixedHoliday("Woman's Day", MonthDay.of(MARCH, 8)),
                new FixedHoliday("Pi Day", MonthDay.of(MARCH, 14)),
                new FixedHoliday("St. Patrick's Day", MonthDay.of(MARCH, 17)),
                new FixedHoliday("Walpurgis Night", MonthDay.of(APRIL, 30)),
                new FixedHoliday("Labor Day", MonthDay.of(MAY, 1)),
                new FixedHoliday("Star Wars Day", MonthDay.of(MAY, 4)),
                new FixedHoliday("Victory Day", MonthDay.of(MAY, 8)),
                new FixedHoliday("Towel Day", MonthDay.of(MAY, 25)),
                new FloatingHoliday("Mother's Day", byDayOfWeekInMonth(MAY, SUNDAY, 2)),
                new DependentHoliday("Rosenmontag", easter(), Period.ofDays(-48)),
                new DependentHoliday("Ash Wednesday", easter(), Period.ofDays(-46)),
                new DependentHoliday("Palm Sunday", easter(), Period.ofDays(-7)),
                new DependentHoliday("Good Friday", easter(), Period.ofDays(-2)),
                easter(),
                new DependentHoliday("Easter Monday", easter(), Period.ofDays(1)),
                new DependentHoliday("Ascension Day", easter(), Period.ofDays(39)),
                new DependentHoliday("Pentecost", easter(), Period.ofDays(49)),
                new DependentHoliday("Whit Monday", easter(), Period.ofDays(50)),
                new DependentHoliday("Corpus Christi", easter(), Period.ofDays(60)),
                new FixedHoliday("D-Day", MonthDay.of(JUNE, 6)),
                new FixedHoliday("Christopher Street Day", MonthDay.of(JUNE, 28)),
                new FloatingHoliday("Beer Day", byDayOfWeekInMonth(AUGUST, FRIDAY, 1)),
                new FixedHoliday("Assumption of Mary", MonthDay.of(AUGUST, 15)),
                new FloatingHoliday("Programmers' Day", Generators.byDayOfYear(256)),
                new FixedHoliday("Talk like a Pirate Day", MonthDay.of(SEPTEMBER, 19)),
                new FixedHoliday("German Unity Day", MonthDay.of(OCTOBER, 3)),
                new FloatingHoliday("Columbus Day", byDayOfWeekInMonth(OCTOBER, MONDAY, 2)),
                new FixedHoliday("Reformation Day", MonthDay.of(OCTOBER, 31)),
                new FixedHoliday("Halloween", MonthDay.of(OCTOBER, 31)),
                new FixedHoliday("All Saints' Day", MonthDay.of(NOVEMBER, 1)),
                new FixedHoliday("Guy Fawkes Night", MonthDay.of(NOVEMBER, 5)),
                thanksgiving(),
                new DependentHoliday("Black Friday", thanksgiving(), Period.ofDays(1)),
                new FloatingHoliday("Buß- und Bettag", byDayOfWeekBeforeDate(WEDNESDAY, MonthDay.of(NOVEMBER, 23))),
                new FixedHoliday("St. Martin's Day", MonthDay.of(NOVEMBER, 11)),
                new FixedHoliday("St. Nicholas' Day", MonthDay.of(DECEMBER, 6)),
                new FixedHoliday("St. Lucy's Day", MonthDay.of(DECEMBER, 13)),
                new FixedHoliday("Christmas Eve", MonthDay.of(DECEMBER, 24)),
                new FixedHoliday("Christmas Day", MonthDay.of(DECEMBER, 25)),
                new FixedHoliday("St. Stephen's Day", MonthDay.of(DECEMBER, 26)),
                new FixedHoliday("Sylvester", MonthDay.of(DECEMBER, 31)),
                advent(),
                new DependentHoliday("2. Advent", advent(), Period.ofDays(7)),
                new DependentHoliday("3. Advent", advent(), Period.ofDays(14)),
                new DependentHoliday("4. Advent", advent(), Period.ofDays(21))
        );
    }

    /**
     * Die Methode sampleB() stellt alle sampleA Feiertage als auch Diskordianische und ebenfalls Mondphasen zur Verfügung.
     * @return Liefert eine Liste von üblich bekannten und diskordianischen Feiertagen sowie Mondphasen zum Füllen eines Kalenders.
     */
    public static List<Holiday> sampleB() {
        List<Holiday> res = new ArrayList<>(sampleA());
        res.addAll(asList(DiscordianHolidays.values()));
        res.addAll(asList(MoonPhases.values()));
        return res;
    }

    /**
     * Die Methode sampleC() stellt alle sampleA, sampleB als auch Brückentage (= ein Tag der durch Urlaub zu einem Vier-Tage-Wochenende führt) zur Verfügung.
     * @return Liefert eine Liste an üblich bekannten und diskordianischen Feiertagen, Mondphasen sowie Brückentagen zum Füllen eines Kalenders.
     */
    public static List<Holiday> sampleC() {
        List<Holiday> res = new ArrayList<>(sampleB());
        res.add(new FourDayWeekend(sampleA()));
        return res;
    }

    // +---------------------------+
    // | private factory methods   |
    // +---------------------------+

    private static Holiday easter() {
        return new FloatingHoliday("Easter Sunday", easterSunday());
    }

    private static Holiday thanksgiving() {
        return new FloatingHoliday("Thanksgiving", byDayOfWeekInMonth(NOVEMBER, THURSDAY, 4));
    }

    private static Holiday advent() {
        return new FloatingHoliday("1. Advent", Generators.advent());
    }
}
