package tho.nill.datenlieferung.simpleAttributes;

public class MonatJahr implements Comparable<MonatJahr> {
    private int mj;

    public MonatJahr(int mj) {
        super();
        this.mj = mj;
    }

    public MonatJahr(int monat, int jahr) {
        super();
        this.mj = 12 * jahr + (monat - 1);
    }

    public int getMonat() {
        return (mj % 12) + 1;
    }

    public int getJahr() {
        return mj / 12;
    }

    public int getMJ() {
        return mj;
    }

    @Override
    public int compareTo(MonatJahr o) {
        return mj - o.mj;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + mj;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MonatJahr other = (MonatJahr) obj;
        if (mj != other.mj)
            return false;
        return true;
    }

}
