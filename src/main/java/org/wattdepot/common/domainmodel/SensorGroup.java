/**
 * SensorGroup.java This file is part of WattDepot.
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

import java.util.Set;

import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.util.Slug;

/**
 * SensorGroup represents a group of Sensors. Used for aggregating sensor
 * measurements.
 * 
 * @author Cam Moore
 * 
 */
public class SensorGroup implements IDomainModel {
  /** The unique id for this group usable in URLs. */
  private String id;
  /** The name of the group. */
  private String name;
  /** The List of sensors the compose this group. */
  protected Set<String> sensors;
  /** The owner of this sensor model. */
  private String ownerId;

  /**
   * Hide the default constructor.
   */
  protected SensorGroup() {

  }

  /**
   * Create a new SensorGroup with the given unique id.
   * 
   * @param name The name.
   * @param sensors The set of sensors in the group.
   * @param ownerId the id of the owner of the SensorGroup.
   */
  public SensorGroup(String name, Set<String> sensors, String ownerId) {
    this(Slug.slugify(name), name, sensors, ownerId);
  }

  /**
   * Create a new SensorGroup with the given unique id.
   * 
   * @param id The unique id.
   * @param name The name.
   * @param sensors The set of sensors in the group.
   * @param ownerId the id of the owner of the SensorGroup.
   */
  public SensorGroup(String id, String name, Set<String> sensors, String ownerId) {
    this.id = id;
    this.name = name;
    this.sensors = sensors;
    this.ownerId = ownerId;
  }

  /**
   * @param sensorId The id of the sensor to add.
   * @return true if successful.
   * @see java.util.List#add(java.lang.Object)
   */
  public boolean add(String sensorId) {
    return sensors.add(sensorId);
  }

  /**
   * @param o The Sensor to test.
   * @return true if the Sensor is in this group.
   * @see java.util.List#contains(java.lang.Object)
   */
  public boolean contains(Object o) {
    return sensors.contains(o);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!getClass().isAssignableFrom(obj.getClass())
        && !obj.getClass().isAssignableFrom(getClass()) && getClass() != obj.getClass()) {
      return false;
    }
    SensorGroup other = (SensorGroup) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
      return false;
    }
    if (ownerId == null) {
      if (other.ownerId != null) {
        return false;
      }
    }
    else if (!ownerId.equals(other.ownerId)) {
      return false;
    }
    if (sensors == null) {
      if (other.sensors != null) {
        return false;
      }
    }
    else if (!sensors.equals(other.sensors)) {
      return false;
    }
    return true;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the owner
   */
  public String getOrganizationId() {
    return ownerId;
  }

  /**
   * @return the sensors
   */
  public Set<String> getSensors() {
    return sensors;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
    result = prime * result + ((sensors == null) ? 0 : sensors.hashCode());
    return result;
  }

  /**
   * @param o The Sensor to remove.
   * @return true if successful.
   * @see java.util.List#remove(java.lang.Object)
   */
  public boolean remove(Object o) {
    return sensors.remove(o);
  }

  /**
   * @param id the id to set
   * @exception BadSlugException if the id isn't valid.
   */
  public void setId(String id) throws BadSlugException {
    if (Slug.validateSlug(id)) {
      this.id = id;
    }
    else {
      throw new BadSlugException(id + " is not a valid slug.");
    }
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
    if (this.id == null) {
      this.id = Slug.slugify(name);
    }
  }

  /**
   * @param owner the id of the owner to set
   */
  public void setOrganizationId(String owner) {
    this.ownerId = owner;
  }

  /**
   * @param sensors the sensors to set
   */
  public void setSensors(Set<String> sensors) {
    this.sensors = sensors;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SensorGroup [id=" + id + ", name=" + name + ", sensors=" + sensors + ", ownerId="
        + ownerId + "]";
  }

}
