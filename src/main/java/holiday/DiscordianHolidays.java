package holiday;

import java.time.MonthDay;
import java.time.temporal.Temporal;


/**
 * Enum Daten-Klasse zum Ausgeben von diskordianischen Feiertagen
 */
public enum DiscordianHolidays implements Holiday {

    //Feldbezeichner    	Name          	diskordianisch        	gregorianisch
    //MUNGDAY            	Mungday        	5. Chaos               	05.01.
    //CHAOFLUX           	Chaoflux       	50. Chaos              	19.02.
    //ST_TIBS_DAY        	St. Tib's Day  	St. Tib's Day          	29.02.
    //MOJODAY            	Mojoday        	5. Discord             	19.03.
    //DISCOFLUX          	Discoflux      	50. Discord            	03.05.
    //SYADAY             	Syaday         	5. Confusion           	31.05.
    //CONFUFLUX          	Confuflux      	50. Confusion          	15.07.
    //ZARADAY            	Zaraday        	5. Bureaucracy         	12.08.
    //BUREFLUX           	Bureflux       	50. Bureaucracy        	26.09.
    //MALADAY            	Maladay        	5. The Aftermath       	24.10.
    //AFFLUX             	Afflux         	50. The Aftermath      	08.12.

    MUNGDAY("Mungday", 5, 1, new FixedHoliday("Mungday", MonthDay.of(1,5))),
    CHAOFLUX("Chaoflux", 50, 1, new FixedHoliday("Chaoflux",MonthDay.of(2, 19))),
    ST_TIBS_DAY("St. Tib's Day", 0, 0, new FloatingHoliday("St. Tib's Day", Generators.february29())),
    MOJODAY("Mojoday", 5, 2, new FixedHoliday("Mungday",MonthDay.of(3, 19))),
    DISCOFLUX("Discoflux", 50, 2, new FixedHoliday("Mungday",MonthDay.of(5, 3))),
    SYADAY("Syaday", 5, 3, new FixedHoliday("Mungday",MonthDay.of(5, 31))),
    CONFUFLUX("Confuflux", 50, 3, new FixedHoliday("Mungday",MonthDay.of(7, 15))),
    ZARADAY("Zaraday", 5, 4, new FixedHoliday("Mungday",MonthDay.of(8, 12))),
    BUREFLUX("Bureflux", 50, 4, new FixedHoliday("Mungday",MonthDay.of(9, 26))),
    MALADAY("Maladay", 5, 5, new FixedHoliday("Mungday",MonthDay.of(10, 24))),
    AFFLUX("Afflux", 50, 5, new FixedHoliday("Mungday",MonthDay.of(12, 8)));


    private final String name;
    private final Holiday holiday;

    DiscordianHolidays(String string, int dayDiscordian, int monthDiscordian, Holiday holiday) {
        this.name = string;
        this.holiday = holiday;
    }

    @Override
    public String getName() {
        return this.name;
    }


    @Override
    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(this.holiday);
    }
}
