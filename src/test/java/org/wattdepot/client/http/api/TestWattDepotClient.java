/**
 * TestWattDepotClient.java This file is part of WattDepot.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.logging.Logger;

import javax.measure.unit.Unit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.client.ClientProperties;
import org.wattdepot.common.domainmodel.CollectorMetaData;
import org.wattdepot.common.domainmodel.CollectorMetaDataList;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.DepositoryList;
import org.wattdepot.common.domainmodel.InstanceFactory;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.MeasurementTypeList;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorGroupList;
import org.wattdepot.common.domainmodel.SensorList;
import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.domainmodel.SensorLocationList;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.SensorModelList;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.util.logger.WattDepotLogger;
import org.wattdepot.server.WattDepotServer;

/**
 * TestWattDepotClient - Test cases for the WattDepotClient class.
 * 
 * @author Cam Moore
 * 
 */
public class TestWattDepotClient {

  protected static WattDepotServer server;

  /** The handle on the client. */
  private static WattDepotAdminClient admin;
  private static WattDepotClient test;
  private static UserInfo testUser = InstanceFactory.getUserInfo();
  private static UserPassword testPassword = InstanceFactory.getUserPassword();
  private static Organization testGroup = InstanceFactory.getUserGroup();

  /** The logger. */
  private Logger logger = null;
  /** The serverUrl. */
  private String serverURL = null;

  /**
   * Starts up a WattDepotServer to start the testing.
   * 
   * @throws Exception
   *           if there is a problem starting the server.
   */
  @BeforeClass
  public static void setupServer() throws Exception {
    server = WattDepotServer.newTestInstance();
  }

  /**
   * Shuts down the WattDepotServer.
   * 
   * @throws Exception
   *           if there is a problem.
   */
  @AfterClass
  public static void stopServer() throws Exception {
    server.stop();
  }

  /**
   * @throws java.lang.Exception
   *           if there is a problem.
   */
  @Before
  public void setUp() {
    try {
      ClientProperties properties = new ClientProperties();
      this.logger = WattDepotLogger.getLogger("org.wattdepot.client",
          properties.get(ClientProperties.CLIENT_HOME_DIR));
      WattDepotLogger.setLoggingLevel(logger,
          properties.get(ClientProperties.LOGGING_LEVEL_KEY));
      logger.finest("setUp()");
      ClientProperties props = new ClientProperties();
      props.setTestProperties();
      this.serverURL = "http://"
          + props.get(ClientProperties.WATTDEPOT_SERVER_HOST) + ":"
          + props.get(ClientProperties.PORT_KEY) + "/";
      logger.finest(serverURL);
      if (admin == null) {
        try {
          admin = new WattDepotAdminClient(serverURL,
              props.get(ClientProperties.USER_NAME),
              props.get(ClientProperties.USER_PASSWORD));
        }
        catch (Exception e) {
          System.out.println("Failed with "
              + props.get(ClientProperties.USER_NAME) + " and "
              + props.get(ClientProperties.USER_PASSWORD));
        }
      }
      admin.putUserPassword(testPassword);
      admin.putUser(testUser);
      admin.putUserGroup(testGroup);
      try {
        admin.putMeasurementType(InstanceFactory.getMeasurementType());
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      if (test == null) {
        test = new WattDepotClient(serverURL, testPassword.getId(),
            testPassword.getPlainText());
      }
      test.isHealthy();
      test.getWattDepotUri();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @throws java.lang.Exception
   *           if there is a problem.
   */
  @After
  public void tearDown() throws Exception {
    logger.finest("tearDown()");
    admin.deleteUser(testPassword.getId());
    admin.deleteUserGroup(testGroup.getId());
    admin.deleteUserPassword(testUser.getId());
    logger.finest("Done tearDown()");
  }

  /**
   * Test method for
   * {@link org.wattdepot.client.http.api.WattDepotClient#WattDepotClient(java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testWattDepotClient() {
    assertNotNull(test);
    // test some bad cases
    try {
      WattDepotClient bad = new WattDepotClient(null, testPassword.getId(),
          testPassword.getPlainText());
      fail(bad + " should not exist.");
    }
    catch (IllegalArgumentException e) {
      // this is what we expect
    }
    catch (BadCredentialException e) {
      fail("We used good credentials");
    }
    try {
      WattDepotClient bad = new WattDepotClient("http://localhost",
          testPassword.getId(), testPassword.getPlainText());
      fail(bad + " should not exist.");
    }
    catch (IllegalArgumentException e) {
      // this is what we expect
    }
    catch (BadCredentialException e) {
      fail("We used good credentials");
    }
    try {
      WattDepotClient bad = new WattDepotClient(serverURL,
          testPassword.getId(), testPassword.getEncryptedPassword());
      fail(bad + " should not exist.");
    }
    catch (BadCredentialException e) {
      // this is wat we expect
    }

  }

  /**
   * Test method for Depositories.
   */
  @Test
  public void testDepository() {
    Depository depo = InstanceFactory.getDepository();
    // Get list
    DepositoryList list = test.getDepositories();
    assertNotNull(list);
    assertTrue(list.getDepositories().size() == 0);
    // Put new instance (CREATE)
    test.putDepository(depo);
    list = test.getDepositories();
    assertNotNull(list);
    assertTrue(list.getDepositories().size() == 1);
    try {
      // get instance (READ)
      Depository ret = test.getDepository(depo.getId());
      assertEquals(depo, ret);
      ret.setSlug("new_slug");
      // update instance (UPDATE)
      try {
        test.updateDepository(ret);
        fail("Can't update depository.");
      }
      catch (Exception e) {
        // expected.
      }
      list = test.getDepositories();
      assertTrue(list.getDepositories().size() == 1);
      // delete instance (DELETE)
      test.deleteDepository(depo);
      try {
        ret = test.getDepository(depo.getName());
        assertNull(ret);
      }
      catch (IdNotFoundException e) {
        // this is what we want.
      }
    }
    catch (IdNotFoundException e) {
      fail("Should have " + depo);
    }

    // error conditions
    Depository bogus = new Depository("bogus", depo.getMeasurementType(),
        depo.getOwner());
    try {
      test.deleteDepository(bogus);
      fail("Shouldn't be able to delete " + bogus);
    }
    catch (IdNotFoundException e) {
      // this is what we want.
    }
  }

  /**
   * Test method for Locations.
   */
  @Test
  public void testLocation() {
    SensorLocation loc = InstanceFactory.getLocation();
    // Get list
    SensorLocationList list = test.getLocations();
    assertNotNull(list);
    assertTrue(list.getLocations().size() == 0);
    // Put new instance (CREATE)
    test.putLocation(loc);
    list = test.getLocations();
    assertNotNull(list);
    assertTrue(list.getLocations().size() == 1);
    try {
      // get instance (READ)
      SensorLocation ret = test.getLocation(loc.getId());
      assertEquals(loc, ret);
      ret.setDescription("new description");
      // update instance (UPDATE)
      test.updateLocation(ret);
      list = test.getLocations();
      assertNotNull(list);
      assertTrue(list.getLocations().size() == 1);
      // delete instance (DELETE)
      test.deleteLocation(loc);
      list = test.getLocations();
      assertNotNull(list);
      assertTrue(list.getLocations().size() == 0);
      try {
        ret = test.getLocation(loc.getId());
        assertNull(ret);
      }
      catch (IdNotFoundException e) {
        // this is what we want.
      }
    }
    catch (IdNotFoundException e) {
      fail("Should have " + loc);
    }
    // error conditions
    SensorLocation bogus = new SensorLocation("bogus", loc.getLatitude(),
        loc.getLongitude(), loc.getAltitude(), loc.getDescription(),
        loc.getOwner());
    try {
      test.deleteLocation(bogus);
      fail("Shouldn't be able to delete " + bogus);
    }
    catch (IdNotFoundException e) {
      // this is what we want.
    }
  }

  /**
   * Test method for MeasurementTypes.
   */
  @Test
  public void testMeasurementType() {
    MeasurementType type = InstanceFactory.getMeasurementType();
    // Put new instance (CREATE)
    try {
      test.putMeasurementType(type);
      fail("Test User should not be able to put a Public Measurement Type.");
    }
    catch (Exception e) {
      // expected since test isn't admin.
      admin.putMeasurementType(type);
    }
    // Get list
    MeasurementTypeList list = test.getMeasurementTypes();
    int numTypes = list.getMeasurementTypes().size();
    assertTrue(list.getMeasurementTypes().contains(type));
    try {
      // get instance (READ)
      MeasurementType ret = test.getMeasurementType(type.getId());
      assertEquals(type, ret);
      ret.setUnits("W");
      // update instance (UPDATE)
      try {
        test.updateMeasurementType(ret);
        fail("Test User should not be able to update a Public Measurement Type.");
      }
      catch (Exception e) {
        // expected
        admin.updateMeasurementType(ret);
      }
      list = test.getMeasurementTypes();
      assertNotNull(list);
      assertTrue("Expecting " + numTypes + " got "
          + list.getMeasurementTypes().size(), list.getMeasurementTypes()
          .size() == numTypes);
      // delete instance (DELETE)
      try {
        test.deleteMeasurementType(ret);
        fail("Test User should not be able to delete a Public Measurement Type.");
      }
      catch (Exception e) {
        // expected.
        admin.deleteMeasurementType(ret);
      }
      try {
        ret = test.getMeasurementType(type.getId());
        assertNull(ret);
        fail("Shouldn't get anything.");
      }
      catch (IdNotFoundException e) {
        // this is what we want.
      }
    }
    catch (IdNotFoundException e) {
      fail("Should have " + type);
    }

    // error conditions
    MeasurementType bogus = new MeasurementType("bogus_meas_type",
        Unit.valueOf("dyn"));
    try {
      test.deleteMeasurementType(bogus);
      fail("Shouldn't be able to delete " + bogus);
    }
    catch (IdNotFoundException e) {
      // this is what we want.
    }

  }

  /**
   * Test method for Sensors.
   */
  @Test
  public void testSensor() {
    Sensor sensor = InstanceFactory.getSensor();
    // Get list
    SensorList list = test.getSensors();
    assertNotNull(list);
    assertTrue(list.getSensors().size() == 0);
    // Put new instance (CREATE)
    test.putSensor(sensor);
    list = test.getSensors();
    assertTrue(list.getSensors().contains(sensor));
    try {
      // get instance (READ)
      Sensor ret = test.getSensor(sensor.getId());
      assertEquals(sensor, ret);
      ret.setUri("bogus_uri");
      // update instance (UPDATE)
      test.updateSensor(ret);
      list = test.getSensors();
      assertNotNull(list);
      assertTrue(list.getSensors().size() == 1);
      // delete instance (DELETE)
      test.deleteSensor(ret);
      try {
        ret = test.getSensor(sensor.getId());
        assertNull(ret);
      }
      catch (IdNotFoundException e) {
        // this is what we want.
      }
    }
    catch (IdNotFoundException e) {
      fail("Should have " + sensor);
    }
    // error conditions
    Sensor bogus = new Sensor("bogus", sensor.getUri(),
        sensor.getSensorLocation(), sensor.getModel(), sensor.getOwner());
    try {
      test.deleteSensor(bogus);
      fail("Shouldn't be able to delete " + bogus);
    }
    catch (IdNotFoundException e) {
      // this is what we want.
    }
  }

  /**
   * Test method for SensorGroups.
   */
  @Test
  public void testSensorGroup() {
    SensorGroup group = InstanceFactory.getSensorGroup();
    // Get list
    SensorGroupList list = test.getSensorGroups();
    assertNotNull(list);
    assertTrue(list.getGroups().size() == 0);
    // Put new instance (CREATE)
    test.putSensorGroup(group);
    list = test.getSensorGroups();
    assertTrue(list.getGroups().contains(group));
    try {
      // get instance (READ)
      SensorGroup ret = test.getSensorGroup(group.getId());
      assertEquals(group, ret);
      ret.setName("changed_name");
      test.updateSensorGroup(group);
      ;
      list = test.getSensorGroups();
      assertNotNull(list);
      assertTrue(list.getGroups().size() == 1);
      // delete instance (DELETE)
      test.deleteSensorGroup(ret);
      try {
        ret = test.getSensorGroup(group.getId());
        assertNull(ret);
      }
      catch (IdNotFoundException e) {
        // this is what we want.
      }
    }
    catch (IdNotFoundException e) {
      fail("Should have " + group);
    }
    // error conditions
    SensorGroup bogus = new SensorGroup("bogus", group.getSensors(),
        group.getOwner());
    try {
      test.deleteSensorGroup(bogus);
      fail("Shouldn't be able to delete " + bogus);
    }
    catch (IdNotFoundException e) {
      // this is what we want.
    }

  }

  /**
   * Test method for SensorModels.
   */
  @Test
  public void testSensorModel() {
    SensorModel model = InstanceFactory.getSensorModel();
    // Get list
    SensorModelList list = test.getSensorModels();
    assertNotNull(list);
    int numModels = list.getModels().size();
    assertTrue(list.getModels().size() >= 0);
    // Put new instance (CREATE)
    test.putSensorModel(model);
    list = test.getSensorModels();
    assertTrue(list.getModels().contains(model));
    try {
      // get instance (READ)
      SensorModel ret = test.getSensorModel(model.getId());
      assertEquals(model, ret);
      ret.setProtocol("new protocol");
      test.updateSensorModel(ret);
      list = test.getSensorModels();
      assertNotNull(list);
      assertTrue(list.getModels().size() == numModels + 1);
      // delete instance (DELETE)
      test.deleteSensorModel(model);
      try {
        ret = test.getSensorModel(model.getId());
        assertNull(ret);
      }
      catch (IdNotFoundException e) {
        // this is what we want.
      }
    }
    catch (IdNotFoundException e) {
      fail("Should have " + model);
    }
    // error conditions
    SensorModel bogus = new SensorModel("bogus", model.getProtocol(),
        model.getType(), model.getVersion());
    try {
      test.deleteSensorModel(bogus);
      fail("Shouldn't be able to delete " + bogus);
    }
    catch (IdNotFoundException e) {
      // this is what we want.
    }
  }

  /**
   * Test method for CollectorMetaDataes.
   */
  @Test
  public void testCollectorMetaData() {
    CollectorMetaData data = InstanceFactory.getCollectorMetaData();
    // Get list
    CollectorMetaDataList list = test.getCollectorMetaDatas();
    assertNotNull(list);
    assertTrue(list.getDatas().size() == 0);
    // Put new instance (CREATE)
    test.putCollectorMetaData(data);
    list = test.getCollectorMetaDatas();
    assertTrue(list.getDatas().contains(data));
    try {
      // get instance (READ)
      CollectorMetaData ret = test.getCollectorMetaData(data.getId());
      assertEquals(data, ret);
      ret.setDepositoryId("new depotistory_id");
      test.updateCollectorMetaData(ret);
      list = test.getCollectorMetaDatas();
      assertNotNull(list);
      assertTrue(list.getDatas().size() == 1);
      // delete instance (DELETE)
      test.deleteCollectorMetaData(data);
      try {
        ret = test.getCollectorMetaData(data.getId());
        assertNull(ret);
      }
      catch (IdNotFoundException e) {
        // this is what we want.
      }
    }
    catch (IdNotFoundException e) {
      fail("Should have " + data);
    }
    // error conditions
    CollectorMetaData bogus = new CollectorMetaData("bogus", data.getSensor(),
        data.getPollingInterval(), data.getDepositoryId(), data.getOwner());
    try {
      test.deleteCollectorMetaData(bogus);
      fail("Shouldn't be able to delete " + bogus);
    }
    catch (IdNotFoundException e) {
      // this is what we want.
    }
  }

  /**
   * Test method for Measurements.
   */
  @Test
  public void testMeasurements() {
    Depository depo = InstanceFactory.getDepository();
    test.putDepository(depo);
    Measurement m1 = InstanceFactory.getMeasurementOne();
    Measurement m2 = InstanceFactory.getMeasurementTwo();
    Measurement m3 = InstanceFactory.getMeasurementThree();
    try {
      test.putMeasurement(depo, m1);
      test.putMeasurement(depo, m3);
      Double val = test.getValue(depo, m1.getSensor(), m1.getDate());
      assertTrue("Got " + val + " was expecting " + m1.getValue(), m1
          .getValue().equals(val));
      val = test.getValue(depo, m2.getSensor(), m2.getDate());
      assertTrue("Got " + val + " was expecting " + m1.getValue(), m1
          .getValue().equals(val));
      // Get list
      MeasurementList list = test.getMeasurements(depo, m1.getSensor(),
          m1.getDate(), m3.getDate());
      assertNotNull(list);

      assertTrue("expecting " + 2 + " got " + list.getMeasurements().size(),
          list.getMeasurements().size() == 2);
      assertTrue(list.getMeasurements().contains(m1));
      assertTrue(list.getMeasurements().contains(m3));
      test.putMeasurement(depo, m2);
      try {
        test.deleteMeasurement(depo, m1);
        list = test.getMeasurements(depo, m1.getSensor(), m1.getDate(),
            m3.getDate());
        assertNotNull(list);

        assertTrue("expecting " + 2 + " got " + list.getMeasurements().size(),
            list.getMeasurements().size() == 2);
      }
      catch (IdNotFoundException e) {
        fail(m1 + " does exist in the depo");
      }

    }
    catch (MeasurementTypeException e) {
      fail(e.getMessage());
    }
    catch (NoMeasurementException e) {
      fail(e.getMessage());
    }

    // error conditions
    Measurement bogus = InstanceFactory.getMeasurementTwo();
    bogus.setMeasurementType("dyn");
    ;
    try {
      test.putMeasurement(depo, bogus);
      fail("shouldn't be able to put " + bogus + " in " + depo);
    }
    catch (MeasurementTypeException e) {
      // this is what we expect.
    }
    bogus = new Measurement(m1.getSensor(), new Date(), new Double(1.0),
        Unit.valueOf("dyn"));
    try {
      test.deleteMeasurement(depo, bogus);
      fail(bogus + " isn't in the depot");
    }
    catch (IdNotFoundException e) {
      // we expect this.
    }
  }

}
