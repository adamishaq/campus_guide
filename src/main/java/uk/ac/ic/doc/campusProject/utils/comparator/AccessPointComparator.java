package uk.ac.ic.doc.campusProject.utils.comparator;

import java.util.Comparator;

import uk.ac.ic.doc.campusProject.model.AccessPoint;

public class AccessPointComparator implements Comparator<AccessPoint> {


	public int compare(AccessPoint lhs, AccessPoint rhs) {
		double ss1 = lhs.getSignalStrength();
		double ss2 = rhs.getSignalStrength();
		
		if (ss1 > ss2) {
			return -1;
		}
		else if (ss1 < ss2) {
			return 1;
		}
		else {
			return 0;
		}
	}

}
