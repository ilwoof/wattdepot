/**
 * UserServerApplication.java This file is part of WattDepot.
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
package org.wattdepot.server.http.api;

import java.util.HashMap;
import java.util.Map;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.wattdepot.common.http.api.API;
import org.wattdepot.server.WattDepotPersistence;

/**
 * UserServerApplication Server app.
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotApplication extends Application {

  private WattDepotPersistence depot;
  private WattDepotComponent component;
  private Map<String, WebSession> sessions;

  /**
   * Default constructor.
   */
  public WattDepotApplication() {
    setName("WattDepot Application");
    setDescription("WattDepot HTTP API implementation");
    setAuthor("Cam Moore");
    sessions = new HashMap<String, WebSession>();
  }

  /**
   * @param id
   *          The WebSession id.
   * @return The WebSession with the given id or null.
   */
  public WebSession getWebSession(String id) {
    return sessions.get(id);
  }

  /**
   * Creates a new WebSession for the given user with their password. If their
   * password doesn't match returns null.
   * 
   * @param username
   *          The unique id for the user.
   * @param password
   *          Their password.
   * @return A new WebSession or null if the password doesn't match the password
   *         in the persistent store.
   */
  public WebSession createWebSession(String username, String password) {
    WebSession ret = null;
    // UserInfo info = depot.getUser(username);
    // if (info != null) {
    // UserGroup group = depot.getUsersGroup(info);
    // if (group != null) {
    // if (password.equals(info.getPassword())) {
    // String id = "" + info.hashCode() + group.hashCode() + new
    // Date().getTime();
    // ret = new WebSession(id, info.getId(), group.getId());
    // sessions.put(id, ret);
    // }
    // }
    // }
    return ret;
  }

  /**
   * Removes the WebSession from the application.
   * 
   * @param id
   *          The id of the session.
   * @return The old WebSession if it existed or null.
   */
  public WebSession removeWebSession(String id) {
    return sessions.remove(id);
  }

  /**
   * @return the depot
   */
  public WattDepotPersistence getDepot() {
    return depot;
  }

  /**
   * @param depot
   *          the depot to set
   */
  public void setDepot(WattDepotPersistence depot) {
    this.depot = depot;
  }

  /**
   * @return the component
   */
  public WattDepotComponent getComponent() {
    return component;
  }

  /**
   * @param component
   *          the component to set
   */
  public void setComponent(WattDepotComponent component) {
    this.component = component;
  }

  /**
   * Creates a root Router to dispatch call to server resources.
   * 
   * @return the inbound root.
   */
  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());
    String webRoot = "file:///" + System.getProperty("user.dir") + "/target/classes";
    Directory directory = new Directory(getContext(), webRoot);
    directory.setListingAllowed(true);
    router.attach("/webroot/", directory);
    // router.attach("/wattdepot/", LoginPageServerResource.class);
    // router.attach("/wattdepot/login/", LoginServerResource.class);
    router.attach(API.MEASUREMENT_TYPE_PUT_URI, MeasurementTypePutServerResource.class);
    router.attach(API.MEASUREMENT_TYPE_URI, MeasurementTypeServerResource.class);
    router.attach(API.MEASUREMENT_TYPES_URI, MeasurementTypesServerResource.class);
    router.attach(API.SENSOR_MODEL_PUT_URI, SensorModelPutServerResource.class);
    router.attach(API.SENSOR_MODEL_URI, SensorModelServerResource.class);
    router.attach(API.SENSOR_MODELS_URI, SensorModelsServerResource.class);
    router.attach(API.ADMIN_URI, AdminServerResource.class);
    router.attach(API.DEPOSITORY_PUT_URI, DepositoryPutServerResource.class);
    router.attach(API.DEPOSITORY_URI, DepositoryServerResource.class);
    router.attach("/wattdepot/{group-id}/depository/{depository_id}/measurement/",
        DepositoryMeasurementServerResource.class);
    router.attach("/wattdepot/{group-id}/depository/{depository_id}/measurement/{meas_id}",
        DepositoryMeasurementServerResource.class);
    router.attach("/wattdepot/{group-id}/depository/{depository_id}/measurements/",
        DepositoryMeasurementsServerResource.class);
    router.attach("/wattdepot/{group-id}/depository/{depository_id}/sensors/",
        DepositorySensorsServerResource.class);
    router.attach("/wattdepot/{group-id}/depository/{depository_id}/value/",
        DepositoryValueServerResource.class);
    router.attach(API.DEPOSITORIES_URI, DepositoriesServerResource.class);
    router.attach("/wattdepot/{group-id}/location/", SensorLocationServerResource.class);
    router.attach("/wattdepot/{group-id}/location/{location_id}",
        SensorLocationServerResource.class);
    router.attach("/wattdepot/{group-id}/locations/", SensorLocationsServerResource.class);
    router.attach("/wattdepot/{group-id}/sensor-group/", SensorGroupServerResource.class);
    router.attach("/wattdepot/{group-id}/sensor-group/{sensorgroup-id}",
        SensorGroupServerResource.class);
    router.attach("/wattdepot/{group-id}/sensor-groups/", SensorGroupsServerResource.class);
    router.attach("/wattdepot/{group-id}/collector-metadata/",
        CollectorMetaDataServerResource.class);
    router.attach("/wattdepot/{group-id}/collector-metadata/{collectormetadata_id}",
        CollectorMetaDataServerResource.class);
    router.attach("/wattdepot/{group-id}/collector-metadatas/",
        CollectorMetaDatasServerResource.class);
    router.attach("/wattdepot/{group-id}/sensor/", SensorServerResource.class);
    router.attach("/wattdepot/{group-id}/sensor/{sensor_id}", SensorServerResource.class);
    router.attach("/wattdepot/{group-id}/sensors/", SensorsServerResource.class);
    router.attach("/wattdepot/{group-id}/user/{user_id}", UserInfoServerResource.class);
    router
        .attach("/wattdepot/{group-id}/user-password/{user_id}", UserPasswordServerResource.class);
    router.attach("/wattdepot/{group-id}/user-group/", UserGroupServerResource.class);
    router.attach("/wattdepot/{group-id}/user-group/{user-group-id}", UserGroupServerResource.class);
    router.attach("/wattdepot/{group-id}/user-groups/", UserGroupsServerResource.class);

    ChallengeAuthenticator authenticator = new ChallengeAuthenticator(getContext(),
        ChallengeScheme.HTTP_BASIC, "WattDepot Realm");
    authenticator.setNext(router);
    return authenticator;

    // return router;
  }
}