/**
 * MeasurementList.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Cam Moore
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wattdepot.common.domainmodel;

import java.util.ArrayList;

/**
 * MeasurementList - Attempt at getting a list across the wire.
 * 
 * @author Cam Moore
 * 
 */
public class MeasurementList {
  private ArrayList<Measurement> measurements;

  /**
   * Default Constructor.
   */
  public MeasurementList() {
    measurements = new ArrayList<Measurement>();
  }

  /**
   * @return the measurements
   */
  public ArrayList<Measurement> getMeasurements() {
    return measurements;
  }

  /**
   * @param measurements the measurements to set
   */
  public void setMeasurements(ArrayList<Measurement> measurements) {
    this.measurements = measurements;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "MeasurementList [measurements=" + measurements + "]";
  }

}
