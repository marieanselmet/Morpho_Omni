/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Emma_BacteriaOmni_Tools;

/**
 *
 * @author manselme
 */
import ij.gui.Roi;


/**
 * @author orion-cirb
 */
public class RoiBg {
    private Roi roi;
    private double bgInt;
    
	public RoiBg(Roi roi, double bgInt) {
            this.roi = roi;
            this.bgInt = bgInt;
	}
        
        public void setRoi(Roi roi) {
		this.roi = roi;
	}
        
        public void setBgInt(double bgInt) {
		this.bgInt = bgInt;
	}
        
        public Roi getRoi() {
            return roi;
        }
        
        public double getBgInt() {
            return bgInt;
        }
}