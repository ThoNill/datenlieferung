package tho.nill.datenlieferung.simpleAttributes;

public class IK implements Comparable<IK> {
    private int ik;

    public IK(int ik) {
        super();
        this.ik = ik;
    }

    public int getIK() {
    	return ik;
    }
    
    
    @Override
    public int compareTo(IK o) {
    	return ik - o.ik;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ik;
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
		IK other = (IK) obj;
		if (ik != other.ik)
			return false;
		return true;
	}


    @Override
    public String toString() {
    	return "" + ik;
    }
}
