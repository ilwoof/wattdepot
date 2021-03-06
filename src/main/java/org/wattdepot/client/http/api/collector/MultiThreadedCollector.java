/**
 * MultiThreadedCollector.java This file is part of WattDepot.
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
package org.wattdepot.client.http.api.collector;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.validator.routines.UrlValidator;
import org.wattdepot.client.http.api.WattDepotClient;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.exception.BadCredentialException;
import org.wattdepot.common.exception.BadSensorUriException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.util.SensorModelHelper;
import org.wattdepot.common.util.Slug;
import org.wattdepot.common.util.tstamp.Tstamp;

/**
 * MultiThreadedCollector - Abstract base class for all Multi-Threaded
 * Collectors.
 * 
 * @author Cam Moore
 * 
 */
public abstract class MultiThreadedCollector extends TimerTask {

  /** Flag for debugging messages. */
  protected boolean debug;
  /** The definition about the collector. */
  protected CollectorProcessDefinition definition;

  /** The client used to communicate with the WattDepot server. */
  protected WattDepotClient client;
  /** The Depository for storing measurements. */
  protected Depository depository;

  /**
   * Initializes the MultiThreadedCollector.
   * 
   * @param serverUri The URI for the WattDepot server.
   * @param username The name of a user defined in the WattDepot server.
   * @param orgId the id of the organization the user is in.
   * @param password The password for the user.
   * @param collectorId The CollectorProcessDefinitionId used to initialize this
   *        collector.
   * @param debug flag for debugging messages.
   * @throws BadCredentialException if the user or password don't match the
   *         credentials in WattDepot.
   * @throws IdNotFoundException if the processId is not defined.
   * @throws BadSensorUriException if the Sensor's URI isn't valid.
   */
  public MultiThreadedCollector(String serverUri, String username, String orgId, String password,
      String collectorId, boolean debug) throws BadCredentialException, IdNotFoundException,
      BadSensorUriException {
    this.client = new WattDepotClient(serverUri, username, orgId, password);
    this.debug = debug;
    this.definition = client.getCollectorProcessDefinition(collectorId);
    this.depository = client.getDepository(definition.getDepositoryId());
    validate();
  }

  /**
   * @param serverUri The URI for the WattDepot server.
   * @param username The name of a user defined in the WattDepot server.
   * @param orgId the id of the user's organization.
   * @param password The password for the user.
   * @param sensorId The id of the Sensor to poll.
   * @param pollingInterval The polling interval in seconds.
   * @param depository The Depository to store the measurements.
   * @param debug flag for debugging messages.
   * @throws BadCredentialException if the user or password don't match the
   *         credentials in WattDepot.
   * @throws BadSensorUriException if the Sensor's URI isn't valid.
   * @throws IdNotFoundException if there is a problem with the sensorId.
   */
  public MultiThreadedCollector(String serverUri, String username, String orgId, String password,
      String sensorId, Long pollingInterval, Depository depository, boolean debug)
      throws BadCredentialException, BadSensorUriException, IdNotFoundException {
    this.client = new WattDepotClient(serverUri, username, orgId, password);
    this.debug = debug;
    this.definition = new CollectorProcessDefinition(Slug.slugify(sensorId + " " + pollingInterval
        + " " + depository.getName()), sensorId, pollingInterval, depository.getName(), null);
    client.putCollectorProcessDefinition(definition);
    this.depository = depository;
    client.putDepository(depository);
    validate();
  }

  /**
   * @return true if everything is good to go.
   */
  public boolean isValid() {
    if (this.client != null && this.definition != null) {
      return true;
    }
    return false;
  }

  /**
   * @param serverUri The URI for the WattDepot server.
   * @param username The name of a user defined in the WattDepot server.
   * @param orgId the user's organization id.
   * @param password The password for the user.
   * @param collectorId The CollectorProcessDefinitionId used to initialize this
   *        collector.
   * @param debug flag for debugging messages.
   * @return true if sensor starts successfully.
   * @throws InterruptedException If sleep is interrupted for some reason.
   * @throws BadCredentialException if the username and password are invalid.
   */
  public static boolean start(String serverUri, String username, String orgId, String password,
      String collectorId, boolean debug) throws InterruptedException, BadCredentialException {

    // Before starting any sensors, confirm that we can connect to the WattDepot
    // server. We do
    // this here because if the server and sensors are running on the same
    // system, at boot time the
    // sensor might start before the server, causing client calls to fail. If
    // this happens, we
    // want to catch it at the top level, where it will result in the sensor
    // process terminating.
    // If we wait to catch it at the per-sensor level, it might cause a sensor
    // to abort for what
    // might be a short-lived problem. The sensor process should be managed by
    // some other process
    // (such as launchd), so it is OK to terminate because it should get
    // restarted if the server
    // isn't up quite yet.
    WattDepotClient staticClient = new WattDepotClient(serverUri, username, orgId, password);
    if (!staticClient.isHealthy()) {
      System.err.format("Could not connect to server %s. Aborting.%n", serverUri);
      // Pause briefly to rate limit restarts if server doesn't come up for a
      // long time
      Thread.sleep(2000);
      return false;
    }
    // Get the collector process definition
    CollectorProcessDefinition definition = null;
    Sensor sensor = null;
    SensorModel model = null;

    try {
      definition = staticClient.getCollectorProcessDefinition(collectorId);
      staticClient.getDepository(definition.getDepositoryId());
      sensor = staticClient.getSensor(definition.getSensorId());
      model = staticClient.getSensorModel(sensor.getModelId());
      // Get SensorModel to determine what type of collector to start.
      if (model.getName().equals(SensorModelHelper.EGAUGE) && model.getVersion().equals("1.0")) {
        Timer t = new Timer();
        try {
          EGaugeCollector collector = new EGaugeCollector(serverUri, username, sensor.getOrganizationId(),
              password, collectorId, debug);
          if (collector.isValid()) {
            System.out.format("Started polling %s sensor at %s%n", sensor.getName(),
                Tstamp.makeTimestamp());
            t.schedule(collector, 0, definition.getPollingInterval() * 1000);
          }
          else {
            System.err.format("Cannot poll %s sensor%n", sensor.getName());
            return false;
          }
        }
        catch (BadSensorUriException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch (IdNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      else if (model.getName().equals(SensorModelHelper.SHARK) && model.getVersion().equals("1.03")) {
        Timer t = new Timer();
        try {
          SharkCollector collector = new SharkCollector(serverUri, username, orgId, password,
              collectorId, debug);
          if (collector.isValid()) {
            System.out.format("Started polling %s sensor at %s%n", sensor.getName(),
                Tstamp.makeTimestamp());
            t.schedule(collector, 0, definition.getPollingInterval() * 1000);
          }
          else {
            System.err.format("Cannot poll %s sensor%n", sensor.getName());
            return false;
          }
        }
        catch (IdNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch (BadSensorUriException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      else if (model.getName().equals(SensorModelHelper.STRESS)) {
        Timer t = new Timer();
        try {
          StressCollector collector = new StressCollector(serverUri, username, orgId, password, collectorId, debug);
          if (collector.isValid()) {
            System.out.format("Started polling %s sensor at %s%n", sensor.getName(),
                Tstamp.makeTimestamp());
            t.schedule(collector, 0, definition.getPollingInterval() * 1000);
          }
          else {
            System.err.format("Cannot poll %s sensor%n", sensor.getName());
            return false;
          }
        }
        catch (IdNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch (BadSensorUriException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      else if (model.getName().equals(SensorModelHelper.WEATHER)) {
        Timer t = new Timer();
        try {
          NOAAWeatherCollector collector = new NOAAWeatherCollector(serverUri, username, orgId, password, collectorId, debug);
          if (collector.isValid()) {
            System.out.format("Started polling %s sensor at %s%n", sensor.getName(),
                Tstamp.makeTimestamp());
            t.schedule(collector, 0, definition.getPollingInterval() * 1000);
          }
          else {
            System.err.format("Cannot poll %s sensor%n", sensor.getName());
            return false;
          }
        }       
        catch (IdNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch (BadSensorUriException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    catch (IdNotFoundException e) {
      System.err.println(e.getMessage());
      return false;
    }
    return true;
  }

  /**
   * @throws BadSensorUriException if the Sensor's URI isn't valid.
   * @throws IdNotFoundException if there is a problem with the Collector
   *         Process Definition.
   */
  private void validate() throws BadSensorUriException, IdNotFoundException {
    Sensor s = client.getSensor(definition.getSensorId());
    String[] schemes = { "http", "https" };
    UrlValidator urlValidator = new UrlValidator(schemes);
    if (!urlValidator.isValid(s.getUri())) {
      throw new BadSensorUriException(s.getUri() + " is not a valid URI.");
    }
  }
}
