package db;

import java.time.LocalDate;
import java.util.Calendar;

public class Constraint implements Comparable<Constraint>{
    public LocalDate date;
    public boolean forbidden;

    public Constraint(LocalDate date, boolean forbidden) {
        this.date = date;
        this.forbidden = forbidden;
    }

    public Constraint(LocalDate date) {
        this.date = date;
        this.forbidden = false;
    }

    @Override
    public int compareTo(Constraint other) {
        if (date.isBefore(other.date)) {
            return -1;
        }
        if (date.isAfter(other.date)) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o){
        return this.date.equals(((Constraint)o).date);
    }
}
