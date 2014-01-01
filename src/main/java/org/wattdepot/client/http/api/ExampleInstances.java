/**
 * ExampleInstances.java This file is part of WattDepot.
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
package org.wattdepot.client.http.api;

import java.util.HashSet;
import java.util.Set;

import org.wattdepot.client.ClientProperties;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.IdNotFoundException;

/**
 * ExampleInstances inserts some example instances into WattDepot.
 * 
 * @author Cam Moore
 * 
 */
public class ExampleInstances {

  private WattDepotAdminClient admin;
  private WattDepotClient cmoore;
  private WattDepotClient johnson;

  /**
   * Creates several clients for inserting instances into WattDepot.
   */
  public ExampleInstances() {
    ClientProperties props = new ClientProperties();
    String serverURL = "http://" + props.get(ClientProperties.WATTDEPOT_SERVER_HOST) + ":"
        + props.get(ClientProperties.PORT_KEY) + "/";
    try {
      admin = new WattDepotAdminClient(serverURL, props.get(ClientProperties.USER_NAME),
          Organization.ADMIN_GROUP_NAME, props.get(ClientProperties.USER_PASSWORD));
    }
    catch (Exception e) {
      System.out.println("Failed with " + props.get(ClientProperties.USER_NAME) + " and "
          + props.get(ClientProperties.USER_PASSWORD));
    }
    if (admin != null) {
      setUpOrganization();
      try {
        cmoore = new WattDepotClient(serverURL, "cmoore", "uh", "secret1");
      }
      catch (BadCredentialException e) {
        e.printStackTrace();
      }
      try {
        johnson = new WattDepotClient(serverURL, "johnson", "uh", "secret2");
      }
      catch (BadCredentialException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Creates a University of Hawaii, Manoa organization and two users in that
   * organziation.
   */
  private void setUpOrganization() {
    Organization org = new Organization("University of Hawaii, Manoa");
    org.setSlug("uh");
    UserInfo user1 = new UserInfo("cmoore", "Cam", "Moore", "cmoore@hawaii.edu", org.getSlug(),
        new HashSet<Property>());
    UserInfo user2 = new UserInfo("johnson", "Philip", "Johnson", "philipmjohnson@gmail.com",
        org.getSlug(), new HashSet<Property>());
    org.getUsers().add(user1.getUid());
    org.getUsers().add(user2.getUid());
    // check to see if the instances are defined in WattDepot
    try {
      Organization defined = admin.getOrganization(org.getSlug());
      if (defined == null) {
        admin
            .putUserPassword(new UserPassword(user1.getUid(), user1.getOrganizationId(), "secret1"));
        admin
            .putUserPassword(new UserPassword(user2.getUid(), user2.getOrganizationId(), "secret2"));
        admin.putUser(user1);
        admin.putUser(user2);
        admin.putOrganization(org);
      }
    }
    catch (IdNotFoundException e) {
      // not defined so put it.
      admin.putUserPassword(new UserPassword(user1.getUid(), user1.getOrganizationId(), "secret"));
      admin.putUserPassword(new UserPassword(user2.getUid(), user2.getOrganizationId(), "secret"));
      admin.putUser(user1);
      admin.putUser(user2);
      admin.putOrganization(org);
    }
  }

  /**
   * Creates two Depositories, a SensorLocation, two Sensors, a SensorGroup, and
   * two CollectorProcessDefinitions.
   */
  private void setUpItems() {
    SensorLocation loc = new SensorLocation("Ilima 6th floor", new Double(21.294642), new Double(
        -157.812727), new Double(30), "Hale Aloha Ilima residence hall 6th floor", "uh");
    try {
      cmoore.getLocation(loc.getSlug());
    }
    catch (IdNotFoundException e) {
      cmoore.putLocation(loc);
    }
    Sensor telco = new Sensor("Ilima 6th telco", "http://telco", loc.getSlug(), "shark",
        loc.getOwnerId());
    try {
      cmoore.getSensor(telco.getSlug());
    }
    catch (IdNotFoundException e) {
      johnson.putSensor(telco);
    }
    Sensor elect = new Sensor("Ilima 6th electrical", "http://elect", loc.getSlug(), "shark",
        loc.getOwnerId());
    try {
      johnson.getSensor(elect.getSlug());
    }
    catch (IdNotFoundException e) {
      cmoore.putSensor(elect);
    }
    Set<String> sensors = new HashSet<String>();
    sensors.add(telco.getSlug());
    sensors.add(elect.getSlug());
    SensorGroup ilima6 = new SensorGroup("Ilima 6th", sensors, loc.getOwnerId());
    try {
      cmoore.getSensorGroup(ilima6.getSlug());
    }
    catch (IdNotFoundException e) {
      johnson.putSensorGroup(ilima6);
    }
    MeasurementType e = null;
    MeasurementType p = null;
    try {
      e = cmoore.getMeasurementType("energy-wh");
      p = johnson.getMeasurementType("power-w");
    }
    catch (IdNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    Depository energy = null;
    if (e != null) {
      energy = new Depository("Ilima Energy", e, loc.getOwnerId());
      try {
        cmoore.getDepository(energy.getSlug());
      }
      catch (IdNotFoundException e1) {
        johnson.putDepository(energy);
      }
      CollectorProcessDefinition energyCPD1 = new CollectorProcessDefinition(
          "Ilima 6th telco energy", telco.getSlug(), 10L, energy.getSlug(), energy.getOwnerId());
      CollectorProcessDefinition energyCPD2 = new CollectorProcessDefinition(
          "Ilima 6th elect energy", elect.getSlug(), 10L, energy.getSlug(), energy.getOwnerId());
      try {
        cmoore.getCollectorProcessDefinition(energyCPD2.getSlug());
      }
      catch (IdNotFoundException e1) {
        cmoore.putCollectorProcessDefinition(energyCPD2);
      }
      try {
        cmoore.getCollectorProcessDefinition(energyCPD1.getSlug());
      }
      catch (IdNotFoundException e1) {
        cmoore.putCollectorProcessDefinition(energyCPD1);
      }

    }
    if (p != null) {
      Depository power = new Depository("Ilima Power", p, loc.getOwnerId());
      try {
        johnson.getDepository(power.getSlug());
      }
      catch (IdNotFoundException e1) {
        johnson.putDepository(power);
      }
    }
  }

  /**
   * @param args
   *          command line arguments.
   */
  public static void main(String[] args) {
    ExampleInstances e = new ExampleInstances();
    e.setUpItems();
  }

}
