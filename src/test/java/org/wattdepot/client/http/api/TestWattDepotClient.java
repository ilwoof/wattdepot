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

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.measure.unit.Unit;
import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.wattdepot.client.ClientProperties;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.CollectorProcessDefinitionList;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.DepositoryList;
import org.wattdepot.common.domainmodel.InterpolatedValue;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementList;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.MeasurementTypeList;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorGroupList;
import org.wattdepot.common.domainmodel.SensorList;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.SensorModelList;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementGapException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.util.DateConvert;
import org.wattdepot.common.util.UnitsHelper;
import org.wattdepot.common.util.logger.WattDepotLogger;
import org.wattdepot.common.util.logger.WattDepotLoggerUtil;
import org.wattdepot.server.WattDepotServer;

/**
 * TestWattDepotClient - Test cases for the WattDepotClient class.
 * 
 * @author Cam Moore
 * 
 */
public class TestWattDepotClient {

  private static WattDepotServer server;

  /** The handle on the client. */
  private static WattDepotAdminClient admin;
  private static WattDepotClient test;
  private UserInfo testUser = null;
  private Organization testOrg = null;
  private SensorModel testModel = null;
  private Sensor testSensor = null;
  private MeasurementType testMeasurementType = null;
  private Depository testDepository = null;
  private CollectorProcessDefinition testCPD = null;
  private Measurement testMeasurement1 = null;
  private Measurement testMeasurement2 = null;
  private Measurement testMeasurement3 = null;

  /** The logger. */
  private Logger logger = null;
  /** The serverUrl. */
  private String serverURL = null;

  /**
   * Starts up a WattDepotServer to start the testing.
   * 
   * @throws Exception if there is a problem starting the server.
   */
  @BeforeClass
  public static void setupServer() throws Exception {
    server = WattDepotServer.newTestInstance();
  }

  /**
   * Shuts down the WattDepotServer.
   * 
   * @throws Exception if there is a problem.
   */
  @AfterClass
  public static void stopServer() throws Exception {
    server.stop();
  }

  /**
   *
   */
  @Before
  public void setUp() {
    WattDepotLoggerUtil.removeClientLoggerHandlers();
    // Set up the test instances.
    Set<Property> properties = new HashSet<Property>();
    properties.add(new Property("isAdmin", "no they are not"));
    testUser = new UserInfo("wattdepot-client-id", "First Name", "Last Name", "test@test.com",
        "test-wattdepotclient", properties, "secret1");
    Set<String> users = new HashSet<String>();
    users.add(testUser.getUid());
    testOrg = new Organization("Test WattDepotClient", users);
    testUser.setOrganizationId(testOrg.getId());
    testModel = new SensorModel("TestWattDepotClient Sensor Model", "test_model_protocol",
        "test_model_type", "test_model_version");
    testSensor = new Sensor("TestWattDepotClient Sensor", "test_sensor_uri", testModel.getId(),
        testOrg.getId());
    testMeasurementType = new MeasurementType("Test MeasurementType Name",
        UnitsHelper.quantities.get("Flow Rate (gal/s)"));
    testDepository = new Depository("Test Depository", testMeasurementType, testOrg.getId());
    testCPD = new CollectorProcessDefinition("TestWattDepotClient Collector Process Defintion",
        testSensor.getId(), 10L, testDepository.getId(), testOrg.getId());
    try {
      Date measTime1 = DateConvert.parseCalStringToDate("2013-11-20T14:35:27.925-1000");
      Date measTime2 = DateConvert.parseCalStringToDate("2013-11-20T14:35:37.925-1000");
      Date measTime3 = DateConvert.parseCalStringToDate("2013-11-20T14:45:37.925-1000");
      Double value = 100.0;
      testMeasurement1 = new Measurement(testSensor.getId(), measTime1, value,
          testMeasurementType.unit());
      testMeasurement2 = new Measurement(testSensor.getId(), measTime2, value,
          testMeasurementType.unit());
      testMeasurement3 = new Measurement(testSensor.getId(), measTime3, value,
          testMeasurementType.unit());
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }

    // Set up the logging and clients.
    try {
      ClientProperties props = new ClientProperties();
      props.setTestProperties();
      this.logger = WattDepotLogger.getLogger("org.wattdepot.client",
          props.get(ClientProperties.CLIENT_HOME_DIR));
      WattDepotLogger.setLoggingLevel(logger, props.get(ClientProperties.LOGGING_LEVEL_KEY));
      logger.finest("setUp()");
      this.serverURL = "http://" + props.get(ClientProperties.WATTDEPOT_SERVER_HOST) + ":"
          + props.get(ClientProperties.PORT_KEY) + "/";
      logger.finest(serverURL);
      if (admin == null) {
        try {
          admin = new WattDepotAdminClient(serverURL, UserInfo.ROOT.getUid(),
              Organization.ADMIN_GROUP.getId(), UserInfo.ROOT.getPassword());
        }
        catch (Exception e) {
          System.out.println("Failed with " + UserInfo.ROOT.getUid() + " and "
              + UserInfo.ROOT.getPassword());
        }
      }
      // 1. Define test organization w/o users
      Organization empty = new Organization(testOrg.getId(), testOrg.getName(),
          new HashSet<String>());
      try {
        admin.getOrganization(testOrg.getId());
      }
      catch (IdNotFoundException e) {
        admin.putOrganization(empty);
      }
      // 2. Define the user.
      try {
        admin.getUser(testUser.getUid(), testOrg.getId());
      }
      catch (IdNotFoundException e) {
        admin.putUser(testUser);
      }
      // 3. Update the organization
      admin.updateOrganization(testOrg);

      try {
        admin.putMeasurementType(testMeasurementType);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      if (test == null) {
        test = new WattDepotClient(serverURL, testUser.getUid(), testUser.getOrganizationId(),
            testUser.getPassword());
      }
      test.isHealthy();
      test.getWattDepotUri();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * @throws java.lang.Exception if there is a problem.
   */
  @After
  public void tearDown() throws Exception {
    logger.finest("tearDown()");
    if (admin != null) {
      admin.deleteOrganization(testOrg.getId());
    }
    logger.finest("Done tearDown()");
  }

  /**
   * Test method for
   * {@link org.wattdepot.client.http.api.WattDepotClient#WattDepotClient(java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testWattDepotClient() {
    if (test == null) {
      setUp();
    }
    assertNotNull(test);
    // test some bad cases
    try {
      WattDepotClient bad = new WattDepotClient(null, testUser.getUid(),
          testUser.getOrganizationId(), testUser.getPassword());
      fail(bad + " should not exist.");
    }
    catch (IllegalArgumentException e) {
      // this is what we expect
    }
    catch (BadCredentialException e) {
      fail("We used good credentials");
    }
    try {
      WattDepotClient bad = new WattDepotClient("http://localhost", testUser.getUid(),
          testUser.getOrganizationId(), testUser.getPassword());
      fail(bad + " should not exist.");
    }
    catch (IllegalArgumentException e) {
      // this is what we expect
    }
    catch (BadCredentialException e) {
      fail("We used good credentials");
    }
    try {
      WattDepotClient bad = new WattDepotClient(serverURL, testUser.getUid(),
          testUser.getOrganizationId(), "bad password string");
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
    Depository depo = testDepository;
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
      ret.setId("new_slug");
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
    Depository bogus = new Depository("bogus", depo.getMeasurementType(), depo.getOrganizationId());
    try {
      test.deleteDepository(bogus);
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
    MeasurementType type = testMeasurementType;
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
      assertTrue("Expecting " + numTypes + " got " + list.getMeasurementTypes().size(), list
          .getMeasurementTypes().size() == numTypes);
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
    MeasurementType bogus = new MeasurementType("bogus_meas_type", Unit.valueOf("dyn"));
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
    Sensor sensor = testSensor;
    // Get list
    SensorList list = test.getSensors();
    assertNotNull(list);
    assertTrue(list.getSensors().size() == 0);
    try {
      // Put new instance (CREATE)
      test.putSensor(sensor);
      fail("Can't put a sensor w/o location or model being defined.");
    }
    catch (ResourceException re) {
      if (re.getStatus().equals(Status.CLIENT_ERROR_BAD_REQUEST)) {
        addSensorModel();
        test.putSensor(sensor);
      }
    }
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
    Sensor bogus = new Sensor("bogus", sensor.getUri(), sensor.getModelId(),
        sensor.getOrganizationId());
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
    Set<String> sensors = new HashSet<String>();
    sensors.add("testwattdepotclient-sensor");
    SensorGroup group = new SensorGroup("TestWattDepotClient Sensor Group", sensors,
        testOrg.getId());
    // Get list
    SensorGroupList list = test.getSensorGroups();
    assertNotNull(list);
    assertTrue(list.getGroups().size() == 0);
    try {
      // Put new instance (CREATE)
      test.putSensorGroup(group);
      fail("Can't put SensorGroup w/o defining the sensor.");
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_BAD_REQUEST)) {
        addSensorModel();
        addSensor();
        test.putSensorGroup(group);
      }
    }
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
    SensorGroup bogus = new SensorGroup("bogus", group.getSensors(), group.getOrganizationId());
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
    SensorModel model = testModel;
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
    SensorModel bogus = new SensorModel("bogus", model.getProtocol(), model.getType(),
        model.getVersion());
    try {
      test.deleteSensorModel(bogus);
      fail("Shouldn't be able to delete " + bogus);
    }
    catch (IdNotFoundException e) {
      // this is what we want.
    }
  }

  /**
   * Test method for CollectorProcessDefinitions.
   */
  @Test
  public void testCollectorProcessDefinition() {
    CollectorProcessDefinition data = testCPD;
    // Get list
    CollectorProcessDefinitionList list = test.getCollectorProcessDefinitions();
    assertNotNull(list);
    assertTrue(list.getDefinitions().size() == 0);
    try {
      // Put new instance (CREATE)
      test.putCollectorProcessDefinition(data);
      fail("Can't put a CollectorProcessDefinition for a sensor that isn't defined.");
    }
    catch (ResourceException e) {
      if (e.getStatus().equals(Status.CLIENT_ERROR_BAD_REQUEST)) {
        addSensorModel();
        addSensor();
        addDepository();
        test.putCollectorProcessDefinition(data);
      }
    }
    list = test.getCollectorProcessDefinitions();
    assertTrue(list.getDefinitions().contains(data));
    try {
      // get instance (READ)
      CollectorProcessDefinition ret = test.getCollectorProcessDefinition(data.getId());
      assertEquals(data, ret);
      ret.setDepositoryId("new depotistory_id");
      try {
        test.updateCollectorProcessDefinition(ret);
        fail("Should not be able to update to bogus depository id.");
      }
      catch (Exception e) { // NOPMD
        // we expect this
      }
      list = test.getCollectorProcessDefinitions();
      assertNotNull(list);
      assertTrue(list.getDefinitions().size() == 1);
      // delete instance (DELETE)
      test.deleteCollectorProcessDefinition(data);
      try {
        ret = test.getCollectorProcessDefinition(data.getId());
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
    CollectorProcessDefinition bogus = new CollectorProcessDefinition("bogus", data.getSensorId(),
        data.getPollingInterval(), data.getDepositoryId(), data.getOrganizationId());
    try {
      test.deleteCollectorProcessDefinition(bogus);
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
    Depository depo = testDepository;
    test.putDepository(depo);
    Measurement m1 = testMeasurement1;
    Measurement m2 = testMeasurement2;
    Measurement m3 = testMeasurement3;
    try {
      try {
        test.putMeasurement(depo, m1);
        fail("Can't put a measurement with a sensor that isn't defined.");
      }
      catch (ResourceException re) {
        if (re.getStatus().equals(Status.CLIENT_ERROR_BAD_REQUEST)) {
          addSensorModel();
          addSensor();
          test.putMeasurement(depo, m1);
        }
      }
      test.putMeasurement(depo, m3);
      Sensor s1 = test.getSensor(m1.getSensorId());
      Double val = test.getValue(depo, s1, m1.getDate());
      assertTrue("Got " + val + " was expecting " + m1.getValue(), m1.getValue().equals(val));
      Sensor s2 = test.getSensor(m2.getSensorId());
      val = test.getValue(depo, s2, m2.getDate(), 799L);
      assertTrue("Got " + val + " was expecting " + m1.getValue(), m1.getValue().equals(val));
      val = test.getValue(depo, s1, m1.getDate(), m2.getDate());
      assertTrue("Got " + val + " was expecting " + (m2.getValue() - m1.getValue()),
          (m2.getValue() - m1.getValue()) == val);
      val = test.getValue(depo, s1, m1.getDate(), m2.getDate(), 799L);
      assertTrue("Got " + val + " was expecting " + (m2.getValue() - m1.getValue()),
          (m2.getValue() - m1.getValue()) == val);
      // Get list
      MeasurementList list = test.getMeasurements(depo, s1, m1.getDate(), m3.getDate());
      assertNotNull(list);

      assertTrue("expecting " + 2 + " got " + list.getMeasurements().size(), list.getMeasurements()
          .size() == 2);
      assertTrue(list.getMeasurements().contains(m1));
      assertTrue(list.getMeasurements().contains(m3));
      test.putMeasurement(depo, m2);
      try {
        test.deleteMeasurement(depo, m1);
        list = test.getMeasurements(depo, s1, m1.getDate(), m3.getDate());
        assertNotNull(list);

        assertTrue("expecting " + 2 + " got " + list.getMeasurements().size(), list
            .getMeasurements().size() == 2);
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
    catch (IdNotFoundException e1) {
      fail(e1.getMessage());
    }
    catch (MeasurementGapException e1) {
      fail(e1.getMessage());
    }

    // error conditions
    Measurement bogus = testMeasurement2;
    bogus.setMeasurementType("dyn");
    ;
    try {
      test.putMeasurement(depo, bogus);
      fail("shouldn't be able to put " + bogus + " in " + depo);
    }
    catch (MeasurementTypeException e) {
      // this is what we expect.
    }
    bogus = new Measurement(m1.getSensorId(), new Date(), new Double(1.0), Unit.valueOf("dyn"));
    try {
      test.deleteMeasurement(depo, bogus);
      fail(bogus + " isn't in the depot");
    }
    catch (IdNotFoundException e) {
      // we expect this.
    }
  }

  /**
   * Tests getting measurements and values for SensorGroups.
   */
  @Test
  public void testSensorGroupMeasurements() {
    addSensorModel();
    // Initialize and store the sensors
    Sensor sensor1 = new Sensor("TestWattDepotClient Sensor1", "test_sensor_uri1",
        testModel.getId(), testOrg.getId());
    Sensor sensor2 = new Sensor("TestWattDepotClient Sensor2", "test_sensor_uri2",
        testModel.getId(), testOrg.getId());
    test.putSensor(sensor1);
    test.putSensor(sensor2);
    // Initialize and store the SensorGroup
    Set<String> sensorNames = new HashSet<String>();
    sensorNames.add(sensor1.getId());
    sensorNames.add(sensor2.getId());
    SensorGroup group = new SensorGroup("TestWattDepotClient SensorGroup1", sensorNames,
        testOrg.getId());
    test.putSensorGroup(group);
    // Make sure the depository is saved.
    Depository depo = testDepository;
    test.putDepository(depo);
    // Create some measurements
    try {
      Date measTime1 = DateConvert.parseCalStringToDate("2013-11-20T14:35:27.925-1000");
      Date measTime2 = DateConvert.parseCalStringToDate("2013-11-20T14:35:37.925-1000");
      Date measTime3 = DateConvert.parseCalStringToDate("2013-11-20T14:35:47.925-1000");
      Date measTime4 = DateConvert.parseCalStringToDate("2013-11-20T14:35:57.925-1000");
      Date measTime5 = DateConvert.parseCalStringToDate("2013-11-20T14:36:07.925-1000");
      Date measTime6 = DateConvert.parseCalStringToDate("2013-11-20T14:36:17.925-1000");
      Double value = 50.1;
      Measurement s1m1 = new Measurement(sensor1.getId(), measTime1, value,
          testMeasurementType.unit());
      Measurement s1m2 = new Measurement(sensor1.getId(), measTime3, value,
          testMeasurementType.unit());
      Measurement s1m3 = new Measurement(sensor1.getId(), measTime5, value,
          testMeasurementType.unit());
      Measurement s2m1 = new Measurement(sensor2.getId(), measTime2, value,
          testMeasurementType.unit());
      Measurement s2m2 = new Measurement(sensor2.getId(), measTime4, value,
          testMeasurementType.unit());
      Measurement s2m3 = new Measurement(sensor2.getId(), measTime6, value,
          testMeasurementType.unit());
      test.putMeasurement(depo, s1m1);
      test.putMeasurement(depo, s1m2);
      test.putMeasurement(depo, s1m3);
      test.putMeasurement(depo, s2m1);
      test.putMeasurement(depo, s2m2);
      test.putMeasurement(depo, s2m3);
      
      // now get the measurements for the group
      MeasurementList measurements = test.getMeasurements(depo, group, measTime1, measTime6);
      assertNotNull(measurements);
      assertTrue(measurements.getMeasurements().size() == 6);
      assertTrue(measurements.getMeasurements().contains(s2m3));
      assertTrue(measurements.getMeasurements().contains(s2m2));
      assertTrue(measurements.getMeasurements().contains(s2m1));
      assertTrue(measurements.getMeasurements().contains(s1m3));
      assertTrue(measurements.getMeasurements().contains(s1m2));
      assertTrue(measurements.getMeasurements().contains(s1m1));
      
      InterpolatedValue iv = test.getEarliestValue(depo, sensor1);
      assertNotNull(iv);
      assertTrue(iv.getDate().equals(s1m1.getDate()));
      iv = test.getLatestValue(depo, sensor2);
      assertTrue(iv.getDate().equals(s2m3.getDate()));
      
      // Get value for group
      try {
        Double val = test.getValue(depo, group, measTime3);
        assertNotNull(val);
        assertTrue(Math.abs(val - 2 * value) < 0.0001);
        iv = test.getEarliestValue(depo, group);
        assertNotNull(iv);
        assertTrue(Math.abs(iv.getValue() - 2 * value) < 0.0001);
        assertTrue(measTime2.equals(iv.getDate()));
        iv = test.getLatestValue(depo, group);
        assertNotNull(iv);
        assertTrue(Math.abs(iv.getValue() - 2 * value) < 0.0001);
        assertTrue(measTime5.equals(iv.getDate()));
      }
      catch (NoMeasurementException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
    }
    catch (ParseException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen.");
    }
    catch (DatatypeConfigurationException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen.");
    }
    catch (MeasurementTypeException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen.");
    }

  }

  /**
   * Adds the test SensorModel to the WattDepotServer if it isn't defined.
   */
  private void addSensorModel() {
    try {
      test.getSensorModel(testModel.getId());
    }
    catch (IdNotFoundException e) {
      // we can add the model.
      test.putSensorModel(testModel);
    }
  }

  // /**
  // * Deletes the test SensorModel from the WattDepotServer if it isn't
  // defined.
  // */
  // private void deleteSensorModel() {
  // SensorModel model = InstanceFactory.getSensorModel();
  // try {
  // test.deleteSensorModel(model);
  // }
  // catch (IdNotFoundException e) {
  // // didn't exist so there isn't any problem.
  // }
  // }

  /**
   * Adds the Sensor to the WattDepotServer if it isn't defined.
   */
  private void addSensor() {
    try {
      test.getSensor(testSensor.getId());
    }
    catch (IdNotFoundException e) {
      // doesn't exist so we can add it.
      test.putSensor(testSensor);
    }
  }

  /**
   * Adds the test Depository to WattDepotServer if it isn't defined.
   */
  private void addDepository() {
    try {
      test.getDepository(testDepository.getId());
    }
    catch (IdNotFoundException e) {
      // doesn't exist so we can add it.
      test.putDepository(testDepository);
    }
  }
}