package holiday;

import java.time.*;
import java.time.temporal.*;


/**
 * Diese Utility-Klasse stellt Factory-Methoden zum Erstellen einiger Feiertage mit variablem Datum bereit, wie sie durch FloatingHoliday modelliert werden können.
 */
public final class Generators {

    /*
    Hinweise
    Achten Sie entsprechend darauf, auf Unterstützung von EPOCH_DAY zu prüfen und verringern Sie nicht die Genauigkeit des übergebenen Temporal.
    Halten Sie sich an die Empfehlung der API Dokumentation, die Methode adjustInto nicht direkt aufzurufen, sondern verwenden Sie die with-Methode von Temporal.
    Achten Sie darauf, dass ihre TemporalAdjuster immer ein Datum zurückgeben, das nach dem übergebenen liegt.
    Verwenden Sie keine Klassenvariablen! Konstanten sind erlaubt.
     */

    private final static Generators ourInstance = new Generators();

    private static Generators getInstance() {
        return ourInstance;
    }

    private Generators() {
    }

    private final static TemporalQuery<LocalDate> query = TemporalQueries.localDate();

    /**
     * Erstellt einen TemporalAdjuster für Feiertage nach dem Schema "n - ter Wochentag im Monat".
     * Beispielsweise liegt Thanksgiving immer am 4. Donnerstag im November eines Jahres.
     * @return liefert einen TemporalAdjuster nach dem Schema n - ter Wochentag im Monat
     */
    public static TemporalAdjuster byDayOfWeekInMonth(Month month, DayOfWeek dayOfWeek, int ordinal) {
        return new TemporalAdjuster() {
            @Override
            public Temporal adjustInto(Temporal temporal) {
                LocalDate nonHoliday = LocalDate.of(temporal.get(ChronoField.YEAR), temporal.get(ChronoField.MONTH_OF_YEAR), temporal.get(ChronoField.DAY_OF_MONTH));
                temporal = temporal.with(ChronoField.MONTH_OF_YEAR, month.getValue())
                        .with(TemporalAdjusters.firstInMonth(dayOfWeek)).plus(7 * (ordinal - 1), ChronoUnit.DAYS);
                if (!Generators.getInstance().isThisYear(nonHoliday, temporal)) {
                    temporal = temporal.with(ChronoField.YEAR, temporal.get(ChronoField.YEAR) + 1)
                            .with(ChronoField.MONTH_OF_YEAR, month.getValue())
                            .with(TemporalAdjusters.firstInMonth(dayOfWeek)).plus(7 * (ordinal - 1), ChronoUnit.DAYS);
                    return temporal;
                } else return temporal;
            }
        };
    }

    /**
     * Erstellt einen TemporalAdjuster für Feiertage nach dem Schema: "Wochentag vor festem Datum".
     * Beispielsweise liegt der Buß- und Bettag immer am Mittwoch vor dem 23. November eines Jahres, wobei der 23. selbst nicht mitgezählt wird.
     * @return liefert einen TemporalAdjuster für Feiertage nach dem Schema: "Wochentag vor festem Datum"
     */
    public static TemporalAdjuster byDayOfWeekBeforeDate(DayOfWeek dayOfWeek, MonthDay date) {
        return new TemporalAdjuster() {
            @Override
            public Temporal adjustInto(Temporal temporal) {
                LocalDate nonHoliday = LocalDate.of(temporal.get(ChronoField.YEAR), temporal.get(ChronoField.MONTH_OF_YEAR), temporal.get(ChronoField.DAY_OF_MONTH));
                temporal = temporal.with(ChronoField.MONTH_OF_YEAR, date.getMonthValue())
                        .with(ChronoField.DAY_OF_MONTH, date.getDayOfMonth() - 1)
                        .with(TemporalAdjusters.previousOrSame(dayOfWeek));
                if (!Generators.getInstance().isThisYear(nonHoliday, temporal)) {
                    temporal = temporal.with(ChronoField.YEAR, temporal.get(ChronoField.YEAR) + 1)
                            .with(ChronoField.MONTH_OF_YEAR, date.getMonthValue())
                            .with(ChronoField.DAY_OF_MONTH, date.getDayOfMonth() - 1)
                            .with(TemporalAdjusters.previousOrSame(dayOfWeek));
                } else return temporal;
                return temporal;
            }
        };
    }

    /**
     * Erstellt einen TemporalAdjuster für Feiertage nach dem Schema "n-ter Tag im Jahr".
     * Beispielsweise wird der Programmer's Day immer am 256. Tag des Jahres gefeiert.
     * @return liefert einen TemporalAdjuster für Feiertage nach dem Schema "n-ter Tag im Jahr"
     */
    public static TemporalAdjuster byDayOfYear(int dayOfYear) {
        return new TemporalAdjuster() {
            @Override
            public Temporal adjustInto(Temporal temporal) {

                if (temporal.get(ChronoField.DAY_OF_YEAR) >= dayOfYear) {
                    return temporal = temporal.plus(1, ChronoUnit.YEARS).with(TemporalAdjusters.firstDayOfYear()).plus(dayOfYear - 1, ChronoUnit.DAYS);
                }
                temporal = temporal.with(TemporalAdjusters.firstDayOfYear()).plus(dayOfYear - 1, ChronoUnit.DAYS);

                return temporal;
            }
        };
    }

    /**
     * Formel zur Berechnung vom Ostersonntag
     * @param year das Jahr in dem der Ostersonntag sein soll
     * @return liefert den Ostersonntag als Tag im März, muss also unter Umständen noch entsprechend umgerechnet werden (OS = 32 ⇒ 32. März = 1. April)
     */
    private int calcEasterSunday(int year) {
        int x = year; //(X ist das Kalenderjahr)
        int k = (int) Math.floor(x / 100);
        int m = 15 + (int) Math.floor((3 * k + 3) / 4) - (int) Math.floor((8 * k + 13) / 25);
        int s = 2 - (int) Math.floor((3 * k + 3) / 4);
        int a = Math.floorMod(x, 19);
        int d = Math.floorMod(19 * a + m, 30);
        int r = (int) Math.floor(d / 29) + ((int) Math.floor(d / 28) - (int) Math.floor(d / 29)) * (int) Math.floor(a / 11);
        int og = 21 + d - r;
        int sz = 7 - Math.floorMod(x + Math.floorDiv(x, 4) + s, 7);
        int oe = 7 - Math.floorMod((og - sz), 7);
        //OS stellt dabei den Ostersonntag als Tag im März dar, muss also unter Umständen noch entsprechend umgerechnet werden (OS = 32 ⇒ 32. März = 1. April)
        int os = og + oe;
        return os;

    }

    /**
     * Erstellt einen TemporalAdjuster für den Ostersonntag.
     * Zur Berechnung dieses Datums können Sie folgende modifizierte Variante der Gaußschen Osterformel verwenden,
     * hierbei steht "div" für eine Ganzzahldivision (mit Abschneiden der Nachkommastellen).
     * Für die Herleitung der Formel sowie die genaue Bedeutung der Variablen sei auf den zugehörigen Wikipedia Artikel verwiesen.
     * @return
     */
    public static TemporalAdjuster easterSunday() {
        return new TemporalAdjuster() {
            @Override
            public Temporal adjustInto(Temporal temporal) {

                if (!temporal.isSupported(ChronoField.EPOCH_DAY)) {
                    throw new DateTimeException("Epoch Day not supported");
                }
                int os = Generators.getInstance().calcEasterSunday(temporal.get(ChronoField.YEAR));
                int daysInApril = os - 31;
                LocalDate nonHoliday = LocalDate.of(temporal.get(ChronoField.YEAR), temporal.get(ChronoField.MONTH_OF_YEAR), temporal.get(ChronoField.DAY_OF_MONTH));
                if (os <= 31) {
                    temporal = temporal.with(ChronoField.MONTH_OF_YEAR, 3).with(ChronoField.DAY_OF_MONTH, os);
                } else {
                    temporal = temporal.with(ChronoField.MONTH_OF_YEAR, 4).with(ChronoField.DAY_OF_MONTH, daysInApril);
                }
                if (Generators.getInstance().isThisYear(nonHoliday, temporal)) {
                    return temporal;
                } else {
                    os = Generators.getInstance().calcEasterSunday(temporal.get(ChronoField.YEAR) + 1);
                    daysInApril = os - 31;
                    temporal = temporal.plus(1, ChronoUnit.YEARS);
                    if (os <= 31) {
                        return temporal = temporal.with(ChronoField.MONTH_OF_YEAR, 3).with(ChronoField.DAY_OF_MONTH, os);
                    } else {
                        return temporal = temporal.with(ChronoField.MONTH_OF_YEAR, 4).with(ChronoField.DAY_OF_MONTH, daysInApril);
                    }
                }
            }
        };
    }

    /**
     * Erstellt einen TemporalAdjuster für den ersten Advent.
     * Der erste Advent ist der vierte Sonntag vor dem 25. Dezember, wobei der 25. selbst nicht mitgezählt wird.
     * @return liefert einen TemporalAdjuster für den ersten Advent.
     */
    public static TemporalAdjuster advent() {
        return new TemporalAdjuster() {
            @Override
            public Temporal adjustInto(Temporal temporal) {

                LocalDate nonHoliday = LocalDate.of(temporal.get(ChronoField.YEAR), temporal.get(ChronoField.MONTH_OF_YEAR), temporal.get(ChronoField.DAY_OF_MONTH));
                temporal = temporal.with(ChronoField.MONTH_OF_YEAR, 12)
                        .with(ChronoField.DAY_OF_MONTH, 24)
                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                        .with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
                        .with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
                        .with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
                if (!Generators.getInstance().isThisYear(nonHoliday, temporal)) {
                    temporal = temporal.with(ChronoField.YEAR, temporal.get(ChronoField.YEAR) + 1)
                            .with(ChronoField.MONTH_OF_YEAR, 12)
                            .with(ChronoField.DAY_OF_MONTH, 24)
                            .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                            .with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
                            .with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
                            .with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
                }
                return temporal;
            }
        };
    }


    /**
     * Erstellt einen TemporalAdjuster für den 29. Februar.
     * Folgen Sie den üblichen Regeln für Schaltjahre im Gregorianischen Kalender.
     * @return liefert einen TemporalAdjuster für den 29. Februar.
     */
    public static TemporalAdjuster february29() {
        return temporal -> {
            if (LocalDate.from(temporal).getMonthValue()==2 && LocalDate.from(temporal).getDayOfMonth()==29 && !LocalDate.from(temporal).isLeapYear()){
                temporal= temporal.with(ChronoField.MONTH_OF_YEAR, 3).with(ChronoField.DAY_OF_MONTH,1);
            }
            Temporal temporalWithFebruary29 = Generators.getInstance().findNearestFebruary29(LocalDate.from(temporal));
            return temporal.with(ChronoField.YEAR, temporalWithFebruary29.get(ChronoField.YEAR))
                    .with(ChronoField.MONTH_OF_YEAR, 2)
                    .with(ChronoField.DAY_OF_MONTH, 29);
        };
    }

    private Temporal findNearestFebruary29(Temporal temporal) {
        LocalDate nonHoliday = LocalDate.of(temporal.get(ChronoField.YEAR), temporal.get(ChronoField.MONTH_OF_YEAR), temporal.get(ChronoField.DAY_OF_MONTH));
        if (temporal.with(TemporalAdjusters.lastDayOfYear()).get(ChronoField.DAY_OF_YEAR) == 366) {
            temporal = temporal.with(ChronoField.MONTH_OF_YEAR, 2).with(ChronoField.DAY_OF_MONTH, 29);
            if (Generators.getInstance().isThisYear(nonHoliday, temporal)) {
                return temporal;
            }
        }
        for (int i=0; i<10; i++){
            nonHoliday=nonHoliday.plus(1, ChronoUnit.YEARS);
            if (nonHoliday.with(TemporalAdjusters.lastDayOfYear()).get(ChronoField.DAY_OF_YEAR) == 366){
                return nonHoliday;
            }
        }
        return null;
    }


    private boolean isThisYear(Temporal nonHolidayTemporal, Temporal holidayTemporal) {
        return nonHolidayTemporal.get(ChronoField.YEAR) <= holidayTemporal.get(ChronoField.YEAR)
                && nonHolidayTemporal.get(ChronoField.MONTH_OF_YEAR) <= holidayTemporal.get(ChronoField.MONTH_OF_YEAR)
                && nonHolidayTemporal.get(ChronoField.DAY_OF_MONTH) < holidayTemporal.get(ChronoField.DAY_OF_MONTH)
                || nonHolidayTemporal.get(ChronoField.MONTH_OF_YEAR) < holidayTemporal.get(ChronoField.MONTH_OF_YEAR)
                || nonHolidayTemporal.get(ChronoField.YEAR) < holidayTemporal.get(ChronoField.YEAR);
    }
}
